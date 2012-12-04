package wig.emitter;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.sun.org.apache.bcel.internal.generic.Type;

import b.HtmlEscape;

import wig.analysis.DepthFirstAdapter;
import wig.node.*;
import wig.symboltable.SymbolTable;
import wig.symboltable.symbols.SField;
import wig.symboltable.symbols.SSession;
import wig.symboltable.symbols.SVariable;
import wig.symboltable.symbols.Symbol;
import wig.type.TypeTable;

public class Emitter extends DepthFirstAdapter
{
    private SymbolTable serviceSymbolTable;
    private SymbolTable currentSymbolTable;
    private TypeTable typeTable = null;
    private StringBuilder phpCode;
    private HashMap<String, String> globalVariablesMap = new HashMap<String, String>();
    private HashMap<String, HashMap<String, String>> localVariableMaps = new HashMap<String, HashMap<String, String>>();
    private HashMap<Node,String> labelMap = new HashMap<Node,String>();
    private final String globalFname = "global.txt";
    private String htmlStr = "";
    private boolean isInFunc = false;
    private boolean inAWhileCond = false;
    private String currentSessionName = "";
    private boolean needSemicolInCall = false;
    private int showCounter = 0;
    private int loopCounter = 0;
    private int tabCount = 0;
    private String urlPrefix = "";
    private String fileName;
    private String globalJsonFileName = "globals.json";
    private boolean currHtmlHasInputOrSelect = false;
    private boolean isFirstTagInHtml = true;
    
    public void emit(Node node) throws IOException
    {
        puts("<?php"); 
        ++tabCount;
        puts("\nsession_start();\n");
        puts(PHPHelper.getHelperPHPFunctions());
        node.apply(this);
        --tabCount;
        puts("\n?>");
        printPhpCode();

    }      

    public void printLocalsAndGlobals()
    {
        for(String s : globalVariablesMap.keySet())
        {
            System.out.println(s + " " + globalVariablesMap.get(s));
        }
        
        for(String session: localVariableMaps.keySet())
        {
            HashMap<String, String> localVariableMap = localVariableMaps.get(session);
            
            System.out.println("Session " + session + ":");
            for(String s : localVariableMap.keySet())
            {
                System.out.println("\t" + s + " " + localVariableMap.get(s));
            }
        }
    }
    
    public Emitter(SymbolTable symbolTable, TypeTable typeTable, String up, String fname) throws IOException
    {
        serviceSymbolTable = symbolTable;
        this.typeTable = typeTable;  
        currentSymbolTable= serviceSymbolTable;
        phpCode = new StringBuilder();
        urlPrefix = up;
        fileName = fname;
        initializeGlobalVariablesMap();
        initializeLocalSymbolVariablesMaps();
        writeVariablesToFile(globalFname, globalVariablesMap);
        FileWriter fr = new FileWriter(globalJsonFileName);
        fr.write("");
        fr.close();        
    }
    
    public void initializeGlobalVariablesMap()
    {
    	for(String symName : serviceSymbolTable.getTable().keySet())
    	{
    		Symbol currSymbol = SymbolTable.getSymbol(serviceSymbolTable, symName);
    		if(currSymbol instanceof SVariable)
    		{
    			SVariable currVariable = (SVariable) currSymbol;
    			if(currVariable.getTupleSymbolTable() != null)
    			{
    				//handle tuple case
    				String tupStr = tupleToString(currVariable.getTupleSymbolTable().getHashMap());
    				globalVariablesMap.put(symName, tupStr);
    			}
    			else
    			{
    				PType currVariableType = currVariable.getVariable().getType();
    				if(currVariableType instanceof AIntType)
    				{
    					globalVariablesMap.put(symName, "0");
    				}
    				else if(currVariableType instanceof AStringType)
    				{
    					globalVariablesMap.put(symName, "");
    				}
    				else if(currVariableType instanceof ABoolType)
    				{
    					globalVariablesMap.put(symName, "false");
    				}
    			}
    		}
    	}
    }
  
    public void  initializeLocalSymbolVariablesMaps()
    {
        localVariableMaps.clear();
        Set<String> symbolNames = serviceSymbolTable.getTable().keySet();
        for(String symbolName : symbolNames)
        {
            Symbol currSymbol = SymbolTable.getSymbol(serviceSymbolTable, symbolName);
            if(currSymbol instanceof SSession)
            {
                localVariableMaps.put(symbolName, getSessionSymbolVariableMap((SSession)currSymbol));
            }
        }
    }  
    
    private HashMap<String,String>  getSessionSymbolVariableMap(SSession session)
    {
        SymbolTable sessionSymbolTable = session.getSymTable();
        HashMap<String,String> localSymbolVariableMap = new HashMap<String, String>();        
        Set<String> symbolNames = sessionSymbolTable.getTable().keySet();
        
        for(String symbolName : symbolNames)
        {
            Symbol currSymbol = SymbolTable.getSymbol(sessionSymbolTable, symbolName);
            if(currSymbol instanceof SVariable)
            {
                SVariable currVariable = (SVariable) currSymbol;
                if(currVariable.getTupleSymbolTable() != null)
                {
                    //handle tuple case
                    String tupStr = tupleToString(currVariable.getTupleSymbolTable().getHashMap());
                    localSymbolVariableMap.put(symbolName, tupStr);
                }
                else
                {
                    PType currVariableType = currVariable.getVariable().getType();
                    if(currVariableType instanceof AIntType)
                    {
                        localSymbolVariableMap.put(symbolName, "0");
                    }
                    else if(currVariableType instanceof AStringType)
                    {
                        localSymbolVariableMap.put(symbolName, "");
                    }
                    else if(currVariableType instanceof ABoolType)
                    {
                        localSymbolVariableMap.put(symbolName, "false");
                    }
                }
            }
        }
        
        return localSymbolVariableMap;
    }
    private String tupleToString(HashMap<String,Symbol> tupleFields)
    {
    	String tupStr = "";
    	int counter = 0;
    	int size = tupleFields.size();
    	for (String k: tupleFields.keySet())
    	{
    		SField sv = (SField) tupleFields.get(k);
    		PType ptyp = sv.getField().getType();
    		
    		if(ptyp instanceof AIntType)
			{
				tupStr += k + "=0";
			}
			else if(ptyp instanceof AStringType)
			{
				tupStr += k + "= ";
			}
			else if(ptyp instanceof ABoolType)
			{
				tupStr += k + "=false";
			} 
    		counter++;
    		if (counter < size)
    			tupStr += ", ";
    		
    	}
    	return tupStr;
    }
    
    private void writeVariablesToFile(String fname, HashMap<String,String> variablesMap)
    {
    	try 
    	{
			FileWriter fw = new FileWriter(fname);
			for (String s: variablesMap.keySet())
			{
				fw.write(s + " " + variablesMap.get(s) + "\n");
			}
			fw.close();
		} 
    	catch (IOException e) 
    	{
    		System.out.println("Cannot write to file " + fname);
    		System.exit(-1);
    	}
    }
    private void puts(String s)
    {
        phpCode.append(s.replaceAll("\n", tabbedNewLine()));
    }
    private void putOpenBrace()
    {
        puts("{");
        ++tabCount;
        puts("\n");
    }
    private void putCloseBrace()
    {
        --tabCount;
        puts("\n}\n");
    }
    private void printPhpCode()
    {
        System.out.println(phpCode.toString());
    }
    private String tabbedNewLine()
    {
        StringBuilder tabbedLine = new StringBuilder();
        tabbedLine.append('\n');
        for(int i=0; i<tabCount; ++i)
        {
            tabbedLine.append('\t');
        }
        return tabbedLine.toString();
    }
    
    public void defaultIn(Node node)
    {
        // Do nothing
    }
    public void defaultOut(Node node)
    {
        // Do nothing
    }

    public void inAService(AService node)
    {
        defaultIn(node);
    }
    public void outAService(AService node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAService(AService node)
    {
        inAService(node);
        {
            List<PHtml> copy = new ArrayList<PHtml>(node.getHtml());
            for(PHtml e : copy)
            {
                e.apply(this);
            }
        }
        {
            List<PSchema> schemas = new ArrayList<PSchema>(node.getSchema());
            for(PSchema schema : schemas)
            {
                schema.apply(this);
            }
        }
        {
            List<PVariable> copy = new ArrayList<PVariable>(node.getVariable());
            for(PVariable e : copy)
            {
                e.apply(this);
            }
            printServiceGlobals(copy);
        }
        {
            List<PFunction> copy = new ArrayList<PFunction>(node.getFunction());
            for(PFunction e : copy)
            {
                e.apply(this);
            }
        }
        {
            puts("\n$WIG_SESSION = $_GET[\"session\"];\nreadGlobals();\n");
            
            List<PSession> sessions = new ArrayList<PSession>(node.getSession());
            for(int i=0; i<sessions.size(); ++i)
            {                
                ASession session  = (ASession)sessions.get(i);
                puts("if (strcmp($WIG_SESSION, \"" + session.getIdentifier().getText()+"\")==0)\n");                
                session.apply(this);                
                if(i!=sessions.size()-1)
                {
                    puts("else ");
                }        
            }
        }
        outAService(node);
    }
    private void printServiceGlobals(List<PVariable> variables)
    {
        puts("$globals= array();");
        for(PVariable variable : variables)
        {
            AVariable avariable = (AVariable) variable;
            
            for(TIdentifier identifier : avariable.getIdentifier())
            {
                puts(varNameToPhp(identifier.getText())+"=");
                if(avariable.getType() instanceof AIntType)
                {
                    puts("0");
                }
                else if (avariable.getType() instanceof AStringType)
                {
                    puts("\"\"");
                }
                else if (avariable.getType() instanceof ABoolType)
                {
                    puts("FALSE");
                }
                else if (avariable.getType() instanceof ATupleType)
                {
                    puts ("$schema_" + ((ATupleType)(avariable.getType())).getIdentifier().getText());
                }
                puts(";\n");
            }
        }
    }
    
    public void inAHtml(AHtml node)
    {
        Symbol symbol = SymbolTable.getSymbol(currentSymbolTable, node.getIdentifier().getText());
        currentSymbolTable = SymbolTable.getScopedSymbolTable(symbol);
    }    
    public void outAHtml(AHtml node)
    {
        currentSymbolTable = currentSymbolTable.getNext();
        puts(htmlStr);
        htmlStr = "";
        isFirstTagInHtml = true;
    }    
    @Override
    public void caseAHtml(AHtml node)
    {
        inAHtml(node);
        htmlStr += "function "+ node.getIdentifier().getText().trim() +" ($holes, $url, $currSessionName){ \n\t$html = \"<html>";
        {
            List<PHtmlbody> copy = new ArrayList<PHtmlbody>(node.getHtmlbody());
            for(PHtmlbody e : copy)
            {
                e.apply(this);
            }
        }

        htmlStr += "<br/><input type='hidden' name='session' value='\".$currSessionName.\"'><input type='submit' value='Submit'></br>";
        htmlStr += "</form>";

        htmlStr += "</body>";
        htmlStr += "</html>\";\n\techo unescapeHTML($html);\n\texit(0);\n}\n";
        outAHtml(node);
    }

    public void inATagStartHtmlbody(ATagStartHtmlbody node)
    {
        defaultIn(node);
    }
    public void outATagStartHtmlbody(ATagStartHtmlbody node)
    {
        defaultOut(node);
    }
    @Override
    public void caseATagStartHtmlbody(ATagStartHtmlbody node)
    {
        inATagStartHtmlbody(node);
        if(!node.getIdentifier().getText().trim().equals("body") && isFirstTagInHtml)
        {
            htmlStr += "<body><form name='\".$currSessionName.\"' action='\".$url.\"' method=\'get\'><" + node.getIdentifier().getText().trim();
            isFirstTagInHtml = false;
        }
        else if(node.getIdentifier().getText().trim().equals("body") && isFirstTagInHtml)
        {
            htmlStr += "<body><form name='\".$currSessionName.\"' action='\".$url.\"' method=\'get\'";
            isFirstTagInHtml = false;
        }
        else 
        {
            htmlStr += "<"+node.getIdentifier().getText().trim();
            isFirstTagInHtml = false;
        }
        {
            List<PAttribute> copy = new ArrayList<PAttribute>(node.getAttribute());
            for(PAttribute e : copy)
            {
                e.apply(this);
            }
        }
        htmlStr += ">";
        outATagStartHtmlbody(node);
    }

    public void inATagEndHtmlbody(ATagEndHtmlbody node)
    {
        defaultIn(node);
    }
    public void outATagEndHtmlbody(ATagEndHtmlbody node)
    {
        defaultOut(node);
    }
    @Override
    public void caseATagEndHtmlbody(ATagEndHtmlbody node)
    {
        inATagEndHtmlbody(node);
        if(!node.getIdentifier().getText().trim().equals("body"))
        {
            htmlStr += "</"+node.getIdentifier().getText().trim()+">";
        }
        outATagEndHtmlbody(node);
    }

    public void inAHoleHtmlbody(AHoleHtmlbody node)
    {
        defaultIn(node);
    }
    public void outAHoleHtmlbody(AHoleHtmlbody node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAHoleHtmlbody(AHoleHtmlbody node)
    {
        inAHoleHtmlbody(node);
        htmlStr += "\".$holes[\""+ node.getIdentifier().getText().trim() +"\"].\"";
        outAHoleHtmlbody(node);
    }

    public void inAWhateverHtmlbody(AWhateverHtmlbody node)
    {
        defaultIn(node);
    }
    public void outAWhateverHtmlbody(AWhateverHtmlbody node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAWhateverHtmlbody(AWhateverHtmlbody node)
    {
        inAWhateverHtmlbody(node);
        if(node.getWhatever() != null)
        {
            node.getWhatever().apply(this);
        }
        outAWhateverHtmlbody(node);
    }

    public void inAMetaHtmlbody(AMetaHtmlbody node)
    {
        defaultIn(node);
    }
    public void outAMetaHtmlbody(AMetaHtmlbody node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAMetaHtmlbody(AMetaHtmlbody node)
    {
        inAMetaHtmlbody(node);
        if(node.getMeta() != null)
        {
            node.getMeta().apply(this);
        }
        outAMetaHtmlbody(node);
    }

    public void inAInputHtmlbody(AInputHtmlbody node)
    {
        defaultIn(node);
    }
    public void outAInputHtmlbody(AInputHtmlbody node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAInputHtmlbody(AInputHtmlbody node)
    {
        inAInputHtmlbody(node);
        htmlStr += "<"  + node.getInput().getText() + " ";
        {
            List<PInputattr> copy = new ArrayList<PInputattr>(node.getInputattr());
            for(PInputattr e : copy)
            {
                e.apply(this);
            }
        }
        htmlStr += "/>";
        outAInputHtmlbody(node);
    }
    
    public void inASelectHtmlbody(ASelectHtmlbody node)
    {
        defaultIn(node);
    }
    public void outASelectHtmlbody(ASelectHtmlbody node)
    {
        defaultOut(node);
    }
    @Override
    public void caseASelectHtmlbody(ASelectHtmlbody node)
    {
        inASelectHtmlbody(node);
        if(node.getSelectTag() != null)
        {
            node.getSelectTag().apply(this);
        }
        
        htmlStr += "<"  + node.getSelectTag().getText().trim();
        
        {
            List<PInputattr> copy = new ArrayList<PInputattr>(node.getInputattr());
            for(PInputattr e : copy)
            {
                e.apply(this);
            }
        }
        
        htmlStr += ">";
        
        if(node.getFirstGt() != null)
        {
            node.getFirstGt().apply(this);
        }
        {
            List<PHtmlbody> copy = new ArrayList<PHtmlbody>(node.getHtmlbody());
            for(PHtmlbody e : copy)
            {
                e.apply(this);
            }
        }
        
        htmlStr += "</" + node.getSelectTag().getText().trim() +">";
        
        outASelectHtmlbody(node);
    }

    public void inANameInputattr(ANameInputattr node)
    {
        defaultIn(node);
    }
    public void outANameInputattr(ANameInputattr node)
    {
        defaultOut(node);
    }
    @Override
    public void caseANameInputattr(ANameInputattr node)
    {
        inANameInputattr(node);
        htmlStr += " " + node.getName().getText().trim() + "=" + HtmlEscape.escape(node.getAttr().toString().trim()) + " ";
        outANameInputattr(node);
    }

    public void inATypeInputattr(ATypeInputattr node)
    {
        defaultIn(node);
    }
    public void outATypeInputattr(ATypeInputattr node)
    {
        defaultOut(node);
    }
    @Override
    public void caseATypeInputattr(ATypeInputattr node)
    {
        inATypeInputattr(node);
        if(node.getType() != null)
        {
            node.getType().apply(this);
        }
        if(node.getInputtype() != null)
        {
            node.getInputtype().apply(this);
        }
        outATypeInputattr(node);
    }

    public void inAAttributeInputattr(AAttributeInputattr node)
    {
        defaultIn(node);
    }
    public void outAAttributeInputattr(AAttributeInputattr node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAAttributeInputattr(AAttributeInputattr node)
    {
        inAAttributeInputattr(node);
        if(node.getAttribute() != null)
        {
            node.getAttribute().apply(this);
        }
        outAAttributeInputattr(node);
    }

    public void inATexttypeInputtype(ATexttypeInputtype node)
    {
        defaultIn(node);
    }
    public void outATexttypeInputtype(ATexttypeInputtype node)
    {
        defaultOut(node);
    }
    @Override
    public void caseATexttypeInputtype(ATexttypeInputtype node)
    {
        inATexttypeInputtype(node);
        if(node.getText() != null)
        {
            puts("\"" + node.getText() + "\"");//node.getText().apply(this);
        }
        outATexttypeInputtype(node);
    }

    public void inARadiotypeInputtype(ARadiotypeInputtype node)
    {
        defaultIn(node);
    }
    public void outARadiotypeInputtype(ARadiotypeInputtype node)
    {
        defaultOut(node);
    }
    @Override
    public void caseARadiotypeInputtype(ARadiotypeInputtype node)
    {
        inARadiotypeInputtype(node);
        if(node.getRadio() != null)
        {
            node.getRadio().apply(this);
        }
        outARadiotypeInputtype(node);
    }
    
    public void inAStrtypeInputtype(AStrtypeInputtype node)
    {
        defaultIn(node);
    }
    public void outAStrtypeInputtype(AStrtypeInputtype node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAStrtypeInputtype(AStrtypeInputtype node)
    {
        inAStrtypeInputtype(node);
        if(node.getStringconst() != null)
        {
            htmlStr += "type=" +  HtmlEscape.escape(node.getStringconst().getText().trim());
        }
        outAStrtypeInputtype(node);
    }

    public void inAAttrAttribute(AAttrAttribute node)
    {
        defaultIn(node);
    }
    public void outAAttrAttribute(AAttrAttribute node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAAttrAttribute(AAttrAttribute node)
    {
        inAAttrAttribute(node);
        if(node.getAttr() != null)
        {
            node.getAttr().apply(this);
        }
        outAAttrAttribute(node);
    }

    public void inAAssignAttribute(AAssignAttribute node)
    {
        defaultIn(node);
    }
    public void outAAssignAttribute(AAssignAttribute node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAAssignAttribute(AAssignAttribute node)
    {
        inAAssignAttribute(node);
        if(node.getLeftAttr() != null)
        {
            node.getLeftAttr().apply(this);
        }
        if(node.getRightAttr() != null)
        {
            node.getRightAttr().apply(this);
        }
        outAAssignAttribute(node);
    }

    public void inAIdAttr(AIdAttr node)
    {
        defaultIn(node);
    }
    public void outAIdAttr(AIdAttr node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAIdAttr(AIdAttr node)
    {
        inAIdAttr(node);
        if(node.getIdentifier() != null)
        {
            htmlStr += " " + node.getIdentifier().getText().trim() + "=";
        }
        outAIdAttr(node);
    }

    public void inAStrAttr(AStrAttr node)
    {
        defaultIn(node);
    }
    public void outAStrAttr(AStrAttr node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAStrAttr(AStrAttr node)
    {
        inAStrAttr(node);
        Node parentNode = node.parent();
        if(parentNode instanceof AAssignAttribute)
        {
            AAssignAttribute assignAttribute = (AAssignAttribute) parentNode;
            Node leftAttr = assignAttribute.getLeftAttr(); 
            if(leftAttr instanceof AIdAttr)
            {
                AIdAttr tagAttribute = (AIdAttr) assignAttribute.getLeftAttr();
                if((tagAttribute.getIdentifier().getText().trim().equals("href")||tagAttribute.getIdentifier().getText().trim().equals("src")) && node.getStringconst() != null)
                {
                    htmlStr += "quot;&amp;" + node.getStringconst().getText().trim().replace("\"", "") + "&amp;quot;";
                }
                else
                {
                    htmlStr += HtmlEscape.escape(node.getStringconst().getText().trim());
                }
            }

        }
        else
        {
            if(node.getStringconst() != null)
            {
                htmlStr += HtmlEscape.escape(node.getStringconst().getText().trim());
            }
        }


        outAStrAttr(node);
    }

    public void inAIconstAttr(AIconstAttr node)
    {
        defaultIn(node);
    }
    public void outAIconstAttr(AIconstAttr node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAIconstAttr(AIconstAttr node)
    {
        inAIconstAttr(node);
        Node parentNode = node.parent();
        if(parentNode instanceof AAssignAttribute)
        {
            AAssignAttribute assignAttribute = (AAssignAttribute) parentNode;
            Node leftAttr = assignAttribute.getLeftAttr(); 
            if(leftAttr instanceof AIdAttr)
            {
                AIdAttr tagAttribute = (AIdAttr) assignAttribute.getLeftAttr();
                if((tagAttribute.getIdentifier().getText().trim().equals("href")||tagAttribute.getIdentifier().getText().trim().equals("src")) && node.getIntconst() != null)
                {
                    htmlStr += node.getIntconst().toString();
                }
                else
                {
                    htmlStr += HtmlEscape.escape(node.getIntconst().toString());
                }
            }

        }
        else
        {
            if(node.getIntconst() != null)
            {
                htmlStr += HtmlEscape.escape(node.getIntconst().toString());
            }
        }

        outAIconstAttr(node);
    }

    public void inANegintIntconst(ANegintIntconst node)
    {
        defaultIn(node);
    }
    public void outANegintIntconst(ANegintIntconst node)
    {
        defaultOut(node);
    }
    @Override
    public void caseANegintIntconst(ANegintIntconst node)
    {
        inANegintIntconst(node);
        if(node.getNegIntconst() != null)
        {
            puts(node.getNegIntconst().getText());
        }
        outANegintIntconst(node);
    }

    public void inAPosintIntconst(APosintIntconst node)
    {
        defaultIn(node);
    }
    public void outAPosintIntconst(APosintIntconst node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAPosintIntconst(APosintIntconst node)
    {
        inAPosintIntconst(node);
        if(node.getPosIntconst() != null)
        {
            puts(node.getPosIntconst().getText());
        }
        outAPosintIntconst(node);
    }

    public void inASchema(ASchema node)
    {
        defaultIn(node);
    }
    public void outASchema(ASchema node)
    {
        defaultOut(node);
    }
    @Override
    public void caseASchema(ASchema node)
    {
        inASchema(node);
        if(node.getIdentifier() != null)
        {
            puts("$schema_"+node.getIdentifier().getText()+"= array(");
        }
        {
            List<PField> fields = new ArrayList<PField>(node.getField());
            boolean first = true;
            for(PField field : fields)
            {   
                AField afield = (AField) field;
                if(!first) 
                    puts(",");
                else
                    first = !first;
                puts("\""+afield.getIdentifier().getText()+"\"=>");
                if(afield.getType() instanceof AIntType)
                {
                    puts("0");
                }
                else if (afield.getType() instanceof AStringType)
                {
                    puts("\"\"");
                }
                else if (afield.getType() instanceof ABoolType)
                {
                    puts("FALSE");
                }
                else if (afield.getType() instanceof ATupleType)
                {
                    puts ("array()");
                }                
            }
            puts(");\n");
        }
        outASchema(node);
    }

    public void inAField(AField node)
    {
        defaultIn(node);
    }
    public void outAField(AField node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAField(AField node)
    {
        inAField(node);
        if(node.getType() != null)
        {
            node.getType().apply(this);
        }
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        outAField(node);
    }
    
    public void inAVariable(AVariable node)
    {
        defaultIn(node);
    }
    public void outAVariable(AVariable node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAVariable(AVariable node)
    {
        inAVariable(node);
        {
            /*List<TIdentifier> copy = new ArrayList<TIdentifier>(node.getIdentifier());
            for(TIdentifier identifier : copy)
            {
                puts("$"+identifier.getText()+"=");
                if(node.getType() instanceof AIntType)
                {
                    puts("0");
                }
                else if (node.getType() instanceof AStringType)
                {
                    puts("\"\"");
                }
                else if (node.getType() instanceof ABoolType)
                {
                    puts("FALSE");
                }
                else if (node.getType() instanceof ATupleType)
                {
                    puts ("array()");
                }
                puts(";\n");
            }*/
        }
        outAVariable(node);
    }

    public void inAIdentifiers(AIdentifiers node)
    {
        defaultIn(node);
    }
    public void outAIdentifiers(AIdentifiers node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAIdentifiers(AIdentifiers node)
    {
        inAIdentifiers(node);
        {
            List<TIdentifier> copy = new ArrayList<TIdentifier>(node.getIdentifier());
            for(TIdentifier e : copy)
            {
                e.apply(this);
            }
        }
        outAIdentifiers(node);
    }

    public void inAIntType(AIntType node)
    {
        defaultIn(node);
    }
    public void outAIntType(AIntType node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAIntType(AIntType node)
    {
        inAIntType(node);
        if(node.getInt() != null)
        {
            node.getInt().apply(this);
        }
        outAIntType(node);
    }

    public void inABoolType(ABoolType node)
    {
        defaultIn(node);
    }
    public void outABoolType(ABoolType node)
    {
        defaultOut(node);
    }
    @Override
    public void caseABoolType(ABoolType node)
    {
        inABoolType(node);
        if(node.getBool() != null)
        {
            node.getBool().apply(this);
        }
        outABoolType(node);
    }

    public void inAStringType(AStringType node)
    {
        defaultIn(node);
    }
    public void outAStringType(AStringType node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAStringType(AStringType node)
    {
        inAStringType(node);
        if(node.getString() != null)
        {
            node.getString().apply(this);
        }
        outAStringType(node);
    }

    public void inAVoidType(AVoidType node)
    {
        defaultIn(node);
    }
    public void outAVoidType(AVoidType node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAVoidType(AVoidType node)
    {
        inAVoidType(node);
        if(node.getVoid() != null)
        {
            node.getVoid().apply(this);
        }
        outAVoidType(node);
    }

    public void inASimpleType(ASimpleType node)
    {
        defaultIn(node);
    }
    public void outASimpleType(ASimpleType node)
    {
        defaultOut(node);
    }
    @Override
    public void caseASimpleType(ASimpleType node)
    {
        inASimpleType(node);
        if(node.getType() != null)
        {
            node.getType().apply(this);
        }
        outASimpleType(node);
    }

    public void inATupleType(ATupleType node)
    {
        defaultIn(node);
    }
    public void outATupleType(ATupleType node)
    {
        defaultOut(node);
    }
    @Override
    public void caseATupleType(ATupleType node)
    {
        inATupleType(node);
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        outATupleType(node);
    }

    public void inAFunction(AFunction node)
    {
        isInFunc = true;
        Symbol symbol = SymbolTable.getSymbol(currentSymbolTable, node.getIdentifier().getText());
        currentSymbolTable = SymbolTable.getScopedSymbolTable(symbol);
    }
    public void outAFunction(AFunction node)
    {
        currentSymbolTable = currentSymbolTable.getNext();
        //currentFunctionVars.clear();
        isInFunc = false;
    }
    @Override
    public void caseAFunction(AFunction node)
    {
        inAFunction(node);
        puts("\nfunction ");

        if(node.getIdentifier() != null)
        {
            puts(node.getIdentifier().getText());
        }
        
        puts("(");
        
        {
            List<PArgument> copy = new ArrayList<PArgument>(node.getArgument());
            int counter = 0;
            int size = copy.size();
            for(PArgument e : copy)
            {
                e.apply(this);
                counter++;
                if (counter<size)
                    puts(",");
            }
        }
        
        puts(")\n");
        if(node.getCompoundstm() != null)
        {
            node.getCompoundstm().apply(this);
        }
        outAFunction(node);
    }

    public void inAArguments(AArguments node)
    {
        defaultIn(node);
    }
    public void outAArguments(AArguments node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAArguments(AArguments node)
    {
        inAArguments(node);
        {
            List<PArgument> copy = new ArrayList<PArgument>(node.getArgument());
            for(PArgument e : copy)
            {
                e.apply(this);
            }
        }
        outAArguments(node);
    }

    public void inAArgument(AArgument node)
    {
        defaultIn(node);
    }
    public void outAArgument(AArgument node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAArgument(AArgument node)
    {
        inAArgument(node);
        if(node.getIdentifier() != null)
        {
            puts("$" + node.getIdentifier().getText());
            //currentFunctionVars.add(node.getIdentifier().getText());
        }
        outAArgument(node);
    }

    public void inASession(ASession node)
    {
        Symbol symbol = SymbolTable.getSymbol(currentSymbolTable, node.getIdentifier().getText());
        currentSymbolTable = SymbolTable.getScopedSymbolTable(symbol);
        currentSessionName = node.getIdentifier().getText();
    }
    
    public void outASession(ASession node)
    {
        currentSymbolTable = currentSymbolTable.getNext();
        currentSessionName = "";
    }
    @Override
    public void caseASession(ASession node)
    {
        inASession(node);
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        if(node.getCompoundstm() != null)
        {
            node.getCompoundstm().apply(this);
        }
        outASession(node);
    }

    public void inAEmptyStm(AEmptyStm node)
    {
        defaultIn(node);
    }
    public void outAEmptyStm(AEmptyStm node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAEmptyStm(AEmptyStm node)
    {
        inAEmptyStm(node);
        outAEmptyStm(node);
    }

    public void inAShowStm(AShowStm node)
    {
        defaultIn(node);
    }
    public void outAShowStm(AShowStm node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAShowStm(AShowStm node)
    {
        String label = getNextShowLabel(node);
        inAShowStm(node);
        String currShowLabel = this.getNextShowLabel(node);
        puts("if (!isset($_SESSION[\"" + currentSessionName + "\"]['currShow']))");
        putOpenBrace();
        puts("$_SESSION[\"" + currentSessionName + "\"]['currShow'] = \"\";\n" );
        putCloseBrace();
        puts("if (strcmp($_SESSION[\"" + currentSessionName + "\"]['currShow'],\"\") == 0)\n");
        putOpenBrace();
        puts("$_SESSION[\"" + currentSessionName + "\"]['currShow'] = \"" + currShowLabel + "\";\n" );
        puts("saveLocalsState(\""+currShowLabel+"\",\""+currentSessionName+"\");\n");
        puts("writeGlobals();\n");
        if(node.getDocument() != null)
        {
            node.getDocument().apply(this);
        }
        putCloseBrace();
        puts("loadLocalsState(\""+currShowLabel+"\",\""+currentSessionName+"\");\n");
        puts("if (strcmp($_SESSION[\"" + currentSessionName + "\"]['currShow'],'" + currShowLabel + "') == 0)\n");
        putOpenBrace();
        puts("readGlobals();");
        if(node.getReceive() != null)
        {
            node.getReceive().apply(this);
        }
        puts("$_SESSION[\"" + currentSessionName + "\"]['currShow'] = \"\";\n" );
        putCloseBrace();
        outAShowStm(node);
    }

    public void inAExitStm(AExitStm node)
    {
        defaultIn(node);
    }
    public void outAExitStm(AExitStm node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAExitStm(AExitStm node)
    {
        inAExitStm(node);
        if(node.getDocument() != null)
        {
            puts("writeGlobals();\n");
            node.getDocument().apply(this);
        }
        outAExitStm(node);
    }

    public void inAReturnStm(AReturnStm node)
    {
        defaultIn(node);
    }
    public void outAReturnStm(AReturnStm node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAReturnStm(AReturnStm node)
    {
        inAReturnStm(node);
        outAReturnStm(node);
    }

    public void inAReturnexpStm(AReturnexpStm node)
    {
        defaultIn(node);
    }
    public void outAReturnexpStm(AReturnexpStm node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAReturnexpStm(AReturnexpStm node)
    {
        inAReturnexpStm(node);
        if(node.getExp() != null)
        {
            puts("return ");
            node.getExp().apply(this);
            puts(";\n");
        }
        outAReturnexpStm(node);
    }

    public void inAIfStm(AIfStm node)
    {
        defaultIn(node);
    }
    public void outAIfStm(AIfStm node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAIfStm(AIfStm node)
    {
        inAIfStm(node);
        puts("if");
        puts(" (");
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
        }
        puts(")\n");
        if(node.getStm() != null)
        {
            if(!(node.getStm() instanceof ACompStm))
            {
                putOpenBrace();
            }
            node.getStm().apply(this);
            if(!(node.getStm() instanceof ACompStm))
            {
                putCloseBrace();
            }
        }
        outAIfStm(node);
    }

    public void inAIfelseStm(AIfelseStm node)
    {
        defaultIn(node);
    }
    public void outAIfelseStm(AIfelseStm node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAIfelseStm(AIfelseStm node)
    {
        inAIfelseStm(node);
        puts("if");
        puts("(");
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
        }
        puts(")\n");
        if(node.getThenStm() != null)
        {
            if(!(node.getThenStm() instanceof ACompStm))
            {
                putOpenBrace();
            }
            node.getThenStm().apply(this);
            if(!(node.getThenStm() instanceof ACompStm))
            {
                putCloseBrace();
            }
            
        }
        puts("else");
        if(node.getElseStm() != null)
        {
            if(!(node.getElseStm() instanceof ACompStm))
            {
                putOpenBrace();
            }
            node.getElseStm().apply(this);
            if(!(node.getElseStm() instanceof ACompStm))
            {
                putCloseBrace();
            }
        }
        outAIfelseStm(node);
    }

    public void inAWhileStm(AWhileStm node)
    {
        defaultIn(node);
    }
    public void outAWhileStm(AWhileStm node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAWhileStm(AWhileStm node)
    {
        String label = getNextLoopLabel(node);       
        inAWhileStm(node);

        initializeWhileState(label);
        puts("\nif(!(isset(");
        printLocalsState();
        puts("[\""+label+"\"]) && ");
        printLocalsState();
        puts("[\"" + label + "\"][\"skip\"]))\n");
        putOpenBrace();
        puts("while");
        puts("(");
        if(node.getExp() != null)
        {
            inAWhileCond = true;
            node.getExp().apply(this);
            inAWhileCond = false;
        }
        puts(")\n");
        if(node.getStm() != null)
        {
            if(!(node.getStm() instanceof ACompStm))
            {
                putOpenBrace();
            }
            node.getStm().apply(this);
            if(!(node.getStm() instanceof ACompStm))
            {
                putCloseBrace();
            }
        }
        printLocalsState();
        puts("[\"" + label + "\"][\"skip\"]=TRUE;");
        putCloseBrace();
        puts("else\n");
        putOpenBrace();
        puts("loadLocalsState(\""+label+"\",\""+currentSessionName+"\");\n");
        putCloseBrace();
        outAWhileStm(node);
    }

    public void inACompStm(ACompStm node)
    {
        defaultIn(node);
    }
    public void outACompStm(ACompStm node)
    {
        defaultOut(node);
    }
    @Override
    public void caseACompStm(ACompStm node)
    {
        inACompStm(node);
        if(node.getCompoundstm() != null)
        {
            node.getCompoundstm().apply(this);
        }
        outACompStm(node);
    }

    
    public void inAExpStm(AExpStm node)
    {
        defaultIn(node);
    }
    public void outAExpStm(AExpStm node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAExpStm(AExpStm node)
    {
        inAExpStm(node);
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
        }
        outAExpStm(node);
    }

    public void inAIdDocument(AIdDocument node)
    {
        defaultIn(node);
    }
    public void outAIdDocument(AIdDocument node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAIdDocument(AIdDocument node)
    {
        inAIdDocument(node);
        if(node.getIdentifier() != null)
        {
            puts(node.getIdentifier().getText() + "(null, \"" + urlPrefix + "/" + fileName + ".php" + "\", \"" + currentSessionName + "\");\n");
        }
        outAIdDocument(node);
    }

    public void inAPlugDocument(APlugDocument node)
    {
        defaultIn(node);
    }
    public void outAPlugDocument(APlugDocument node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAPlugDocument(APlugDocument node)
    {
        inAPlugDocument(node);
        if(node.getIdentifier() != null)
        {
            puts(node.getIdentifier().getText() + "(");
        }
        {
            List<PPlug> copy = new ArrayList<PPlug>(node.getPlug());
            int size;
            int counter;
            size = copy.size();
            counter = 0;
            puts("array(");
            for(PPlug e : copy)
            {                
                e.apply(this);
                counter++;
                if (counter<size)
                    puts(",");
            }
            puts("), ");
        }
        puts("\"" + urlPrefix + "/" + fileName + ".php" + "\", \"" + currentSessionName + "\");\n");
        outAPlugDocument(node);
    }

    public void inAReceive(AReceive node)
    {
        defaultIn(node);
    }
    public void outAReceive(AReceive node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAReceive(AReceive node)
    {
        inAReceive(node);
        {
            
            List<PInput> copy = new ArrayList<PInput>(node.getInput());
            for(PInput e : copy)
            {
                e.apply(this);
            }
        }
        outAReceive(node);
    }

    public void inACompoundStm(ACompoundstm node)
    {
        if(! (node.parent() instanceof AFunction || node.parent() instanceof ASession) )
        {
            currentSymbolTable = currentSymbolTable.getCompoundStatementSymbolTable(node);
        }
    }
    public void outACompoundStm(ACompoundstm node)
    {
        if(! (node.parent() instanceof AFunction || node.parent() instanceof ASession) )
        {
            currentSymbolTable = currentSymbolTable.getNext();
        }
    }
    @Override
    public void caseACompoundstm(ACompoundstm node)
    {
        inACompoundstm(node);
        putOpenBrace();
        
        if(node.parent().parent() instanceof AWhileStm)
        {
            AWhileStm whileNode = (AWhileStm) node.parent().parent();
            puts("if(isset(");
            printLocalsState();
            puts("[\""+labelMap.get(whileNode)+"\"][\"first\"]) && !");
            printLocalsState();
            puts("[\""+labelMap.get(whileNode)+"\"][\"first\"])\n");
            putOpenBrace();
            puts("loadLocalsState(\""+labelMap.get(whileNode)+"\", \""+ currentSessionName+ "\");\n");
            putCloseBrace();
            printLocalsState();
            puts("[\""+labelMap.get(whileNode)+"\"][\"first\"] = FALSE;\n");
        }
        {
            List<PVariable> variables = new ArrayList<PVariable>(node.getVariable());
            for(PVariable e : variables)
            {
                e.apply(this);
            }
            
            if(node.parent() instanceof ASession)
            {
                printSessionLocals((ASession) node.parent(), variables);
            }
            
        }
        {
            List<PStm> copy = new ArrayList<PStm>(node.getStm());
            for(PStm e : copy)
            {
                e.apply(this);
            }
        }
        if(node.parent().parent() instanceof AWhileStm)
        {
            LoopLabelCollector labelcollector = new LoopLabelCollector(labelMap);
            ArrayList<String> labels = labelcollector.generateLabels(node); 
            AWhileStm whileNode = (AWhileStm) node.parent().parent();
            for(String label:labels)
            {
                puts("unset(");
                printLocalsState();
                puts("[\""+label+"\"]);\n");                
            }
            puts("saveLocalsState(\""+labelMap.get(whileNode)+"\", \""+ currentSessionName+ "\");\n");
        }
        putCloseBrace();
        outACompoundstm(node);
    }

    private void printSessionLocals(ASession session, List<PVariable> variables)
    {
        String localsArray = "$_SESSION[\""+session.getIdentifier().getText() + "\"][\"locals\"]";
        puts(localsArray+ "= array();\n");
        
        for(PVariable variable : variables)
        {
            AVariable avariable = (AVariable) variable;
            
            for(TIdentifier identifier : avariable.getIdentifier())
            {
                puts(localsArray+"[\""+identifier.getText()+"\"]=");
                if(avariable.getType() instanceof AIntType)
                {
                    puts("0");
                }
                else if (avariable.getType() instanceof AStringType)
                {
                    puts("\"\"");
                }
                else if (avariable.getType() instanceof ABoolType)
                {
                    puts("FALSE");
                }
                else if (avariable.getType() instanceof ATupleType)
                {
                    puts ("$schema_" + ((ATupleType)(avariable.getType())).getIdentifier().getText());
                }
                puts(";\n");
            }
        }
    }
    public void inAPlugs(APlugs node)
    {
        defaultIn(node);
    }
    public void outAPlugs(APlugs node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAPlugs(APlugs node)
    {
        inAPlugs(node);
        {
            List<PPlug> copy = new ArrayList<PPlug>(node.getPlug());
            for(PPlug e : copy)
            {
                e.apply(this);
            }
        }
        outAPlugs(node);
    }

    public void inAPlug(APlug node)
    {
        defaultIn(node);
    }
    public void outAPlug(APlug node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAPlug(APlug node)
    {
        inAPlug(node);
        if(node.getIdentifier() != null)
        {
            puts("\"" + node.getIdentifier().getText() + "\" => ");
        }
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
        }
        outAPlug(node);
    }

    public void inAInputs(AInputs node)
    {
        defaultIn(node);
    }
    public void outAInputs(AInputs node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAInputs(AInputs node)
    {
        inAInputs(node);
        {
            List<PInput> copy = new ArrayList<PInput>(node.getInput());
            for(PInput e : copy)
            {
                e.apply(this);
            }
        }
        outAInputs(node);
    }

    public void inAInput(AInput node)
    {
        defaultIn(node);
    }
    public void outAInput(AInput node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAInput(AInput node)
    {
        inAInput(node);
        String variableName = "";
        String tupleName = "";
        String tupleField = "";
        boolean isTuple = false;
        if(node.getLvalue() != null)
        {
            Node leftNode = node.getLvalue();
            puts("\nif(isset($_GET['"+  node.getIdentifier().getText().trim() + "']))\n");
            putOpenBrace();
            if(leftNode instanceof ASimpleLvalue)
            {
                ASimpleLvalue lValue = (ASimpleLvalue) leftNode;
                variableName = lValue.getIdentifier().getText().trim();
                String scope = "";
                if (globalVariablesMap.get(variableName) != null)
                {
                    puts("$_SESSION['globals']['"+ variableName +"'] = ");  
                }
                else
                {
                    puts("$_SESSION[\"" + currentSessionName + "\"]['locals']['"+ variableName +"'] = ");  
                }
              
            }
            else if(leftNode instanceof AQualifiedLvalue)
            {
                AQualifiedLvalue lQValue = (AQualifiedLvalue) leftNode;
                tupleName = lQValue.getLeft().getText().trim();
                String scope = "";
                if (globalVariablesMap.get(tupleName) != null)
                {
                    scope = "globals";
                }
                else
                {
                    scope = "locals";
                }
                tupleField = lQValue.getRight().getText().trim();
                puts("$_SESSION[\"" + currentSessionName + "\"]['"+ scope + "']['"+ tupleName +"']['" + tupleField + "'] = ");
                isTuple = true;                
            }
        }
        if(node.getIdentifier() != null)
        {
            String inputFieldVar = node.getIdentifier().getText().trim();
            String defaultValue = "";
            if(!isTuple)
            {
                if(globalVariablesMap.containsKey(variableName))
                {
                    defaultValue = globalVariablesMap.get(variableName);
                }
                else
                {
                    defaultValue = localVariableMaps.get(currentSessionName).get(variableName);
                }
            }
            else
            {
                if(globalVariablesMap.containsKey(tupleName))
                {
                    defaultValue = getTupleDefaultValue(globalVariablesMap.get(tupleName), tupleField);
                }
                else
                {
                    defaultValue = getTupleDefaultValue(localVariableMaps.get(currentSessionName).get(tupleName), tupleField);
                }
            }
            
            if(defaultValue.equals("0"))
            {
                puts("intval($_GET['"+ inputFieldVar + "']);");

            }
            else if(defaultValue.equals(" ") || defaultValue.equals(""))
            {
                puts("$_GET['"+ inputFieldVar + "'];");
            }
            else
            {
                puts("\nERROR: Cannot Receive Tuple! @ line number " + node.getIdentifier().getLine());
                System.exit(-1);
            }
            
            putCloseBrace();
        }
        outAInput(node);
    }

    public void inAAssignExp(AAssignExp node)
    {
        defaultIn(node);
    }
    public void outAAssignExp(AAssignExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAAssignExp(AAssignExp node)
    {
        inAAssignExp(node);
        if(inAWhileCond)
        {
            puts("(");
        }
            
        if(node.getLvalue() != null)
        {
            node.getLvalue().apply(this);
        }
        puts(" = ");
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        if (!inAWhileCond)
        {
            puts(";\n");
        }
        else
        {
            puts(")");
        }
        outAAssignExp(node);
    }

    public void inAOrExp(AOrExp node)
    {
        defaultIn(node);
    }
    public void outAOrExp(AOrExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAOrExp(AOrExp node)
    {
        inAOrExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        puts("||");
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAOrExp(node);
    }

    public void inAAndExp(AAndExp node)
    {
        defaultIn(node);
    }
    public void outAAndExp(AAndExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAAndExp(AAndExp node)
    {
        inAAndExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        puts("&&");
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAAndExp(node);
    }

    public void inAEqExp(AEqExp node)
    {
        defaultIn(node);
    }
    public void outAEqExp(AEqExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAEqExp(AEqExp node)
    {
        inAEqExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        puts(" == ");
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAEqExp(node);
    }

    public void inANeqExp(ANeqExp node)
    {
        defaultIn(node);
    }
    public void outANeqExp(ANeqExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseANeqExp(ANeqExp node)
    {
        inANeqExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        puts(" != ");
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outANeqExp(node);
    }

    public void inALtExp(ALtExp node)
    {
        defaultIn(node);
    }
    public void outALtExp(ALtExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseALtExp(ALtExp node)
    {
        inALtExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        puts(" < ");
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outALtExp(node);
    }

    public void inAGtExp(AGtExp node)
    {
        defaultIn(node);
    }
    public void outAGtExp(AGtExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAGtExp(AGtExp node)
    {
        inAGtExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        puts(" >= ");
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAGtExp(node);
    }

    public void inALteqExp(ALteqExp node)
    {
        defaultIn(node);
    }
    public void outALteqExp(ALteqExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseALteqExp(ALteqExp node)
    {
        inALteqExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        puts(" <= ");
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outALteqExp(node);
    }

    public void inAGteqExp(AGteqExp node)
    {
        defaultIn(node);
    }
    public void outAGteqExp(AGteqExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAGteqExp(AGteqExp node)
    {
        inAGteqExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        puts(" >= ");
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAGteqExp(node);
    }

    public void inAPlusExp(APlusExp node)
    {
        defaultIn(node);
    }
    public void outAPlusExp(APlusExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAPlusExp(APlusExp node)
    {
        inAPlusExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(typeTable.getNodeType(node.getLeft()).equals(wig.type.Type.STRING) || typeTable.getNodeType(node.getRight()).equals(wig.type.Type.STRING))
        {
            puts(" . ");
        }
        else
        {
            puts(" + ");
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAPlusExp(node);
    }

    public void inAMinusExp(AMinusExp node)
    {
        defaultIn(node);
    }
    public void outAMinusExp(AMinusExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAMinusExp(AMinusExp node)
    {
        inAMinusExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        puts("-");
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAMinusExp(node);
    }

    public void inAMultExp(AMultExp node)
    {
        defaultIn(node);
    }
    public void outAMultExp(AMultExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAMultExp(AMultExp node)
    {
        inAMultExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        puts(" * ");
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAMultExp(node);
    }

    public void inADivExp(ADivExp node)
    {
        defaultIn(node);
    }
    public void outADivExp(ADivExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseADivExp(ADivExp node)
    {
        inADivExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        puts(" / ");
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outADivExp(node);
    }

    public void inAModExp(AModExp node)
    {
        defaultIn(node);
    }
    public void outAModExp(AModExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAModExp(AModExp node)
    {
        inAModExp(node);
        puts("(");
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        puts(" % ");
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        puts(")");
        outAModExp(node);
    }

    public void inAJoinExp(AJoinExp node)
    {
        defaultIn(node);
    }
    public void outAJoinExp(AJoinExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAJoinExp(AJoinExp node)
    {
        inAJoinExp(node);
        puts("array_merge(");
        if(node.getLeft() != null && node.getRight() != null)
        {
            puts(varNameToPhp(node.getLeft().toString().replace(" ", "")));
            puts(", ");
            puts(varNameToPhp(node.getRight().toString().replace(" ", "")));
        }
        puts(")");
        outAJoinExp(node);
    }

    public void inAKeepExp(AKeepExp node)
    {
        defaultIn(node);
    }
    public void outAKeepExp(AKeepExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAKeepExp(AKeepExp node)
    {
        inAKeepExp(node);
        if(node.getLeft() != null && node.getIdentifier() != null)
        {            
            if(node.getLeft() instanceof ALvalueExp)
            {
                puts("array(\"" + node.getIdentifier().toString().replace(" ", "") + "\" => ");
                puts(varNameToPhp(node.getLeft().toString().replace(" ", ""))  + "[\"" + 
                        node.getIdentifier().toString().replace(" ", "") + "\"]");
                puts(")");
            }
            else
            {
                node.getLeft().apply(this);
            }
        }        
        outAKeepExp(node);
    }

    public void inARemoveExp(ARemoveExp node)
    {
        defaultIn(node);
    }
    public void outARemoveExp(ARemoveExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseARemoveExp(ARemoveExp node)
    {
        inARemoveExp(node);        
        if(node.getLeft() != null && node.getIdentifier() != null)
        {
            puts("array_remove_key(");
            if (node.getLeft() instanceof ARemoveExp)
            {
                node.getLeft().apply(this);
            }
            if (node.getLeft() instanceof ALvalueExp)
            {
                puts(varNameToPhp(node.getLeft().toString().replace(" ", "")));
            }
            puts(", array(\""); 
            puts(node.getIdentifier().getText() + "\"))");
        }        
        outARemoveExp(node);
    }

    public void inAKeepManyExp(AKeepManyExp node)
    {
        defaultIn(node);
    }
    public void outAKeepManyExp(AKeepManyExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAKeepManyExp(AKeepManyExp node)
    {
        inAKeepManyExp(node);
        if(node.getLeft() != null)
        {
            puts("array(");
            List<TIdentifier> copy = new ArrayList<TIdentifier>(node.getIdentifier());
            int size = copy.size();
            int counter = 0;
            for(TIdentifier e : copy)
            {
                puts("\"" + e.getText() + "\" => ");
                puts(varNameToPhp(node.getLeft().toString().replace(" ", ""))  + "[\"" + 
                        e.getText() + "\"]");
                counter++;
                if (counter < size)
                {
                    puts(", ");
                }
            }
            puts(")");
        }
        outAKeepManyExp(node);
    }

    public void inARemoveManyExp(ARemoveManyExp node)
    {
        defaultIn(node);
    }
    public void outARemoveManyExp(ARemoveManyExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseARemoveManyExp(ARemoveManyExp node)
    {
        inARemoveManyExp(node);
        if(node.getLeft() != null)
        {
            puts("array_remove_key(" + varNameToPhp(node.getLeft().toString().replace(" ", "")) + ", ");
            puts("array(");
            List<TIdentifier> copy = new ArrayList<TIdentifier>(node.getIdentifier());
            int size = copy.size();
            int counter = 0;
            for(TIdentifier e : copy)
            {
                puts("\"" + e.getText() + "\"");
                counter++;
                if (counter < size)
                {
                    puts(", ");
                }
            }
            puts(")");
        }
        outARemoveManyExp(node);
    }

    public void inANotExp(ANotExp node)
    {
        defaultIn(node);
    }
    public void outANotExp(ANotExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseANotExp(ANotExp node)
    {
        inANotExp(node);
        puts("(");
        puts("!");
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        puts(")");
        outANotExp(node);
    }

    public void inANegExp(ANegExp node)
    {
        defaultIn(node);
    }
    public void outANegExp(ANegExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseANegExp(ANegExp node)
    {
        inANegExp(node);
        puts("(-");
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        puts(")");
        outANegExp(node);
    }

    public void inADefaultExp(ADefaultExp node)
    {
        defaultIn(node);
    }
    public void outADefaultExp(ADefaultExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseADefaultExp(ADefaultExp node)
    {
        inADefaultExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        outADefaultExp(node);
    }

    public void inALvalueExp(ALvalueExp node)
    {
        defaultIn(node);
    }
    public void outALvalueExp(ALvalueExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseALvalueExp(ALvalueExp node)
    {
        inALvalueExp(node);
        if(node.getLvalue() != null)
        {
            node.getLvalue().apply(this);
        }
        outALvalueExp(node);
    }

    public void inACallExp(ACallExp node)
    {
        defaultIn(node);
    }
    public void outACallExp(ACallExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseACallExp(ACallExp node)
    {
        inACallExp(node);
        if(node.getIdentifier() != null)
        {
            puts(node.getIdentifier().getText() + "(");
        }
        List<PExp> copy = new ArrayList<PExp>(node.getExp());
        int size = copy.size();
        int counter = 0;
        for(PExp e : copy)
        {
           e.apply(this);
           counter++;
           if (counter < size)
           {
               puts(", ");
           }
        }
        puts(")");
        if (needSemicolInCall)
        {
            puts(";\n");
        }
        outACallExp(node);
    }

    public void inAIntExp(AIntExp node)
    {
        defaultIn(node);
    }
    public void outAIntExp(AIntExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAIntExp(AIntExp node)
    {
        inAIntExp(node);
        if(node.getIntconst() != null)
        {
            node.getIntconst().apply(this);
        }
        outAIntExp(node);
    }

    public void inATrueExp(ATrueExp node)
    {
        defaultIn(node);
    }
    public void outATrueExp(ATrueExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseATrueExp(ATrueExp node)
    {
        inATrueExp(node);
        if(node.getTrue() != null)
        {
            node.getTrue().apply(this);
        }
        outATrueExp(node);
    }

    public void inAFalseExp(AFalseExp node)
    {
        defaultIn(node);
    }
    public void outAFalseExp(AFalseExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAFalseExp(AFalseExp node)
    {
        inAFalseExp(node);
        if(node.getFalse() != null)
        {
            node.getFalse().apply(this);
        }
        outAFalseExp(node);
    }

    public void inAStringExp(AStringExp node)
    {
        defaultIn(node);
    }
    public void outAStringExp(AStringExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAStringExp(AStringExp node)
    {
        inAStringExp(node);
        if(node.getStringconst() != null)
        {
            node.getStringconst().apply(this);
        }
        outAStringExp(node);
    }

    public void inATupleExp(ATupleExp node)
    {
        defaultIn(node);
    }
    public void outATupleExp(ATupleExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseATupleExp(ATupleExp node)
    {
        inATupleExp(node);
        {
            List<PFieldvalue> copy = new ArrayList<PFieldvalue>(node.getFieldvalue());
            for(PFieldvalue e : copy)
            {
                e.apply(this);
            }
        }
        outATupleExp(node);
    }

    public void inAParenExp(AParenExp node)
    {
        defaultIn(node);
    }
    public void outAParenExp(AParenExp node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAParenExp(AParenExp node)
    {
        inAParenExp(node);
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
        }
        outAParenExp(node);
    }

    public void inAExps(AExps node)
    {
        defaultIn(node);
    }
    public void outAExps(AExps node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAExps(AExps node)
    {
        inAExps(node);
        {
            List<PExp> copy = new ArrayList<PExp>(node.getExp());
            for(PExp e : copy)
            {
                e.apply(this);
            }
        }
        outAExps(node);
    }

    public void inASimpleLvalue(ASimpleLvalue node)
    {
        defaultIn(node);
    }
    public void outASimpleLvalue(ASimpleLvalue node)
    {
        defaultOut(node);
    }
    @Override
    public void caseASimpleLvalue(ASimpleLvalue node)
    {
        inASimpleLvalue(node);
        if(node.getIdentifier() != null)
        {
            puts(varNameToPhp(node.getIdentifier().getText()));
        }
        outASimpleLvalue(node);
    }

    public void inAQualifiedLvalue(AQualifiedLvalue node)
    {
        defaultIn(node);
    }
    public void outAQualifiedLvalue(AQualifiedLvalue node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAQualifiedLvalue(AQualifiedLvalue node)
    {
        inAQualifiedLvalue(node);
        puts(varNameToPhp(node.getLeft().getText()));
        puts("[\""+node.getRight().getText()+"\"]");
        outAQualifiedLvalue(node);
    }

    public void inAFieldvalues(AFieldvalues node)
    {
        defaultIn(node);
    }
    public void outAFieldvalues(AFieldvalues node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAFieldvalues(AFieldvalues node)
    {
        inAFieldvalues(node);
        {
            List<PFieldvalue> copy = new ArrayList<PFieldvalue>(node.getFieldvalue());
            for(PFieldvalue e : copy)
            {
                e.apply(this);
            }
        }
        outAFieldvalues(node);
    }

    public void inAFieldvalue(AFieldvalue node)
    {
        defaultIn(node);
    }
    public void outAFieldvalue(AFieldvalue node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAFieldvalue(AFieldvalue node)
    {
        inAFieldvalue(node);
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
        }
        outAFieldvalue(node);
    }

    public void caseTService(TService node)
    {
        
    }
    public void caseTConst(TConst node)
    {
        
    }
    public void caseTHtml(THtml node)
    {
        
    }
    public void caseTHtmlTagStart(THtmlTagStart node)
    {
        
    }
    public void caseTSchema(TSchema node)
    {
        
    }
    public void caseTSession(TSession node)
    {
        
    }
    public void caseTShow(TShow node)
    {
        
    }
    public void caseTExit(TExit node)
    {
        
    }
    public void caseTReturn(TReturn node)
    {
        
    }
    public void caseTIf(TIf node)
    {
        
    }
    public void caseTElse(TElse node)
    {
        
    }
    public void caseTWhile(TWhile node)
    {
        
    }
    public void caseTPlug(TPlug node)
    {
        
    }
    public void caseTReceive(TReceive node)
    {
        
    }

    public void caseTVoid(TVoid node)
    {
        
    }
    public void caseTTuple(TTuple node)
    {
        
    }
    public void caseTTrue(TTrue node)
    {
        
    }
    public void caseTFalse(TFalse node)
    {
        
    }
    public void caseTMeta(TMeta node)
    {
        htmlStr += node.getText().trim();
    }
    public void caseTHtmlTagEnd(THtmlTagEnd node)
    {
        
    }
    public void caseTInput(TInput node)
    {
        htmlStr += node.getText().trim();
    }
    public void caseTPosIntconst(TPosIntconst node)
    {
        puts(node.getText());
    }
    public void caseTNegIntconst(TNegIntconst node)
    {
        puts(node.getText());
    }
    public void caseTSelect(TSelect node)
    {
        
    }
    public void caseTType(TType node)
    {
        
    }
    public void caseTName(TName node)
    {
        
    }
    public void caseTText(TText node)
    {
        
    }
    public void caseTRadio(TRadio node)
    {
        
    }
    public void caseTLBrace(TLBrace node)
    {
        
    }
    public void caseTRBrace(TRBrace node)
    {
        
    }
    public void caseTAssign(TAssign node)
    {
        
    }
    public void caseTSemicolon(TSemicolon node)
    {
        
    }
    public void caseTLt(TLt node)
    {
        
    }
    public void caseTGt(TGt node)
    {
        
    }
    public void caseTLtSlash(TLtSlash node)
    {
        
    }
    public void caseTLtBracket(TLtBracket node)
    {
        
    }
    public void caseTGtBracket(TGtBracket node)
    {
        
    }
    public void caseTComment(TComment node)
    {
        
    }
    public void caseTLPar(TLPar node)
    {
        
    }
    public void caseTRPar(TRPar node)
    {
        
    }
    public void caseTLBracket(TLBracket node)
    {
        
    }
    public void caseTRBracket(TRBracket node)
    {
        
    }
    public void caseTComma(TComma node)
    {
        
    }
    public void caseTKeep(TKeep node)
    {
        
    }
    public void caseTRemove(TRemove node)
    {
        
    }
    public void caseTJoin(TJoin node)
    {
        
    }
    public void caseTEq(TEq node)
    {
        puts("==");
    }
    public void caseTNeq(TNeq node)
    {
        puts("!=");   
    }
    public void caseTLteq(TLteq node)
    {
        puts("<=");
    }
    public void caseTGteq(TGteq node)
    {
        puts(">=");
    }
    public void caseTNot(TNot node)
    {
        puts("!");
    }
    public void caseTMinus(TMinus node)
    {
        puts("-");
    }
    public void caseTPlus(TPlus node)
    {
        puts("+");
    }
    public void caseTMult(TMult node)
    {
        puts("*");
    }
    public void caseTDiv(TDiv node)
    {
        puts("/");
    }
    public void caseTMod(TMod node)
    {
        puts("%");
    }
    public void caseTAnd(TAnd node)
    {
        puts(" && ");
    }
    public void caseTOr(TOr node)
    {
        puts(" || ");
    }
    public void caseTDot(TDot node){}
    public void caseTEol(TEol node)
    {
        puts(node.getText());
    }
    public void caseTBlank(TBlank node)
    {
        puts(node.getText());
    }
    public void caseTIdentifier(TIdentifier node)
    {
    }
    public void caseTStringconst(TStringconst node)
    {
        puts(node.getText() + " ");
    }
    public void caseTWhatever(TWhatever node)
    {
        htmlStr += node.getText().trim();
    }
   
    public void caseEOF(EOF node)
    {
    }
    
    public String varNameToPhp(String varName)
    {
        if (globalVariablesMap.get(varName) != null)
        {
            return "$_SESSION[\"globals\"][\""+varName+"\"]";
        }
        else if (isInFunc)
        {
            return "$" + varName;
        }
        else
        {
            return "$_SESSION[\"" + currentSessionName + "\"][\"locals\"][\""+varName+"\"]";
        }
    } 
    
    /**
     * 
     * @param str - the fields
     * @param field - the field you want
     * @return - the default value of the field you want
     */
    public static String getTupleDefaultValue(String str, String field)
    {
        String[] split = str.split(",");
        for (String s: split)
        {
            String[] s1 = s.split("=");
            if (s1[0].contains(field))
            {
                return s1[1];
            }
        }
        return "";
    }

    private void saveShowState(String label)
    {
        printLocalsState();
        puts("[\""+label+"\"]");
        puts("[\"locals\"]");
     
        printLocalsState();
        puts("[\""+label+"\"]");
        puts("[\"globals\"]");
    }   
    
    private void initializeWhileState(String label)
    {
        printLocalsState();
        puts("[\""+label+"\"]");
        puts("[\"locals\"]=array();\n");
     
        printLocalsState();
        puts("[\""+label+"\"]");
        puts("[\"globals\"]=array();\n");
        
        printLocalsState();
        puts("[\""+label+"\"]");
        puts("[\"skip\"]=false;\n");
        printLocalsState();
        puts("[\""+label+"\"]");
        puts("[\"first\"]=true;\n");
    }  
    private void printLocalsState()
    {
        puts("$_SESSION[\"" + currentSessionName + "\"][\"locals_states\"]");
    }
    
    private String getNextShowLabel(Node node)
    {
        String label = "show"+ (++showCounter);
        labelMap.put(node, label);
        return label;
    }
    private String getNextLoopLabel(Node node)
    {
        String label = "loop"+ (++loopCounter);
        labelMap.put(node, label);
        return label;
    }
}
