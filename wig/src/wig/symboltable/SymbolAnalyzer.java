package wig.symboltable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import wig.analysis.DepthFirstAdapter;
import wig.node.AArgument;
import wig.node.AAssignExp;
import wig.node.ACallExp;
import wig.node.ACompStm;
import wig.node.ACompoundstm;
import wig.node.AExitStm;
import wig.node.AFunction;
import wig.node.AHtml;
import wig.node.AIdDocument;
import wig.node.AInput;
import wig.node.AJoinExp;
import wig.node.AKeepExp;
import wig.node.AKeepManyExp;
import wig.node.ALvalueExp;
import wig.node.APlug;
import wig.node.APlugDocument;
import wig.node.APlugs;
import wig.node.AQualifiedLvalue;
import wig.node.AReceive;
import wig.node.ARemoveExp;
import wig.node.ARemoveManyExp;
import wig.node.ASession;
import wig.node.AShowStm;
import wig.node.ASimpleLvalue;
import wig.node.Node;
import wig.node.PExp;
import wig.node.PHtmlbody;
import wig.node.PInput;
import wig.node.PPlug;
import wig.node.PStm;
import wig.node.PVariable;
import wig.node.TIdentifier;
import wig.symboltable.symbols.SArgument;
import wig.symboltable.symbols.SVariable;
import wig.symboltable.symbols.Symbol;

public class SymbolAnalyzer extends DepthFirstAdapter
{
    SymbolTable serviceSymbolTable;
    SymbolTable currentSymbolTable;
    SVariable currentTuple;
    
    public void analyze(Node node)
    {
        node.apply(this);
    }
    
    public SymbolAnalyzer(SymbolTable symbolTable)
    {
        serviceSymbolTable = symbolTable;
        currentSymbolTable= serviceSymbolTable;
    }
    
    public SymbolTable getServiceSymbolTable()
    {
        return serviceSymbolTable;
    }
    
    public void inAHtml(AHtml node)
    {
        Symbol symbol = SymbolTable.getSymbol(currentSymbolTable, node.getIdentifier().getText());
        currentSymbolTable = SymbolTable.getScopedSymbolTable(symbol);
    }
    
    public void caseAHtml(AHtml node)
    {
        inAHtml(node);
       
        String name = node.getIdentifier().toString().trim();
        
        if(SymbolTable.getSymbol(currentSymbolTable, name) == null)
        {
            if(SymbolTable.lookupHierarchy(currentSymbolTable, name) == null)
            {
                puts("Error: Symbol " + name + " not defined. Line no:" + node.getIdentifier().getLine() );
                System.exit(1);
            }
        }
                
        List<PHtmlbody> copy = new ArrayList<PHtmlbody>(node.getHtmlbody());
        for(PHtmlbody body : copy)
        {
            body.apply(this);
        }        
        
        outAHtml(node);
    }
    
    public void outAHtml(AHtml node)
    {
        currentSymbolTable = currentSymbolTable.getNext();
    }    
    
    
    public void inAFunction(AFunction node)
    {
        Symbol symbol = SymbolTable.getSymbol(currentSymbolTable, node.getIdentifier().getText());
        currentSymbolTable = SymbolTable.getScopedSymbolTable(symbol);
    }
    
    public void caseAFunction(AFunction node)
    {
        inAFunction(node);          
        
        if(node.getCompoundstm() != null)
        {
            node.getCompoundstm().apply(this);
        }
             
        outAFunction(node);
    }
    
    public void outAFunction(AFunction node)
    {
        currentSymbolTable = currentSymbolTable.getNext();
    }

    public void inASession(ASession node)
    {
        Symbol symbol = SymbolTable.getSymbol(currentSymbolTable, node.getIdentifier().getText());
        currentSymbolTable = SymbolTable.getScopedSymbolTable(symbol);
    }
    
    public void caseASession(ASession node)
    {
        inASession(node);
        if(node.getCompoundstm() != null)
        {
            node.getCompoundstm().apply(this);
        }               
        outASession(node);
    }
    
    public void outASession(ASession node)
    {
        currentSymbolTable = currentSymbolTable.getNext();
    }
    
    
    public void caseACompStm(ACompStm node)
    {
        node.getCompoundstm().apply(this);
    }    
    
    public void inACompoundStm(ACompoundstm node)
    {
        if(! (node.parent() instanceof AFunction || node.parent() instanceof ASession) )
        {
            currentSymbolTable = currentSymbolTable.getCompoundStatementSymbolTable(node);
        }
    }
    
    public void caseACompoundstm(ACompoundstm node)
    {        
        inACompoundStm(node); 
        
        LinkedList<PStm> stm_list = node.getStm();
        LinkedList<PVariable> var_list = node.getVariable();
        Iterator<PStm> stm_iter = stm_list.iterator();
        Iterator<PVariable> var_iter = var_list.iterator();          
        
        while(var_iter.hasNext())
        {
            var_iter.next().apply(this);
        }
        
        while(stm_iter.hasNext())
        {
            stm_iter.next().apply(this);
        }        
        
        outACompoundStm(node);
    }
    
    public void outACompoundStm(ACompoundstm node)
    {
        if(! (node.parent() instanceof AFunction || node.parent() instanceof ASession) )
        {
            currentSymbolTable = currentSymbolTable.getNext();
        }
    }
    
    public void caseAShowStm(AShowStm node)
    {
        AReceive receive = null;
        String htmlName = null;
        
        if (node.getReceive() != null)
        {
            receive = (AReceive) node.getReceive();
        }
        
        // check if we have an APlugDocument or an AIdDocument node
        if (node.getDocument() instanceof APlugDocument)
        {
            APlugDocument doc = (APlugDocument) node.getDocument();
            htmlName = doc.getIdentifier().getText();
            doc.apply(this);
        }
        else
        {
            AIdDocument doc = (AIdDocument) node.getDocument();
            htmlName = doc.getIdentifier().getText();
        }
        
        // check if the html name the show statement is referring to is defined
        if (SymbolTable.defSymbol(serviceSymbolTable,htmlName))
        {
            Symbol symbol = SymbolTable.getSymbol(serviceSymbolTable, htmlName);
            SymbolTable htmlNameTable = SymbolTable.getScopedSymbolTable(symbol);
    
            if (receive != null)
            {
                LinkedList<PInput> inputList = receive.getInput();
                
                // check if identifiers in receive statement are valid
                for (PInput pi: inputList)
                {
                    AInput ai = (AInput) pi;
                    
                    if (!(SymbolTable.defSymbol(htmlNameTable, ai.getIdentifier().getText())))
                    {
                        System.out.println("Error: Identifier '" + ai.getIdentifier().getText() + "' in receive statement is not defined. Line no:" + ai.getIdentifier().getLine());
                        System.exit(1);
                    }
                }
            }
        }
        else
        {
            System.out.println("Error: Html const '" + htmlName + "' show statement is referring to does not exist.");
            System.exit(1);
        }
    }
    
    public void caseAPlugs(APlugs node)
    {
        LinkedList<PPlug> plugList = node.getPlug();
        for (PPlug pp: plugList)
        {
            pp.apply(this);
        }
    }
        
    public void caseAPlugDocument(APlugDocument node)
    {
        String htmlName = node.getIdentifier().getText();
        Symbol symbol = SymbolTable.getSymbol(serviceSymbolTable, htmlName);
        SymbolTable htmlNameTable = SymbolTable.getScopedSymbolTable(symbol);
        
        LinkedList<PPlug> plugList = node.getPlug();
        
        for (PPlug pp : plugList)
        {
            APlug ap = (APlug) pp;
            
            // check if the identifier part of plug was defined in the html which has name htmlName
            if (!(SymbolTable.defSymbol(htmlNameTable, ap.getIdentifier().getText().trim())))
            {
                System.out.println("Error: Plug identifier '" + ap.getIdentifier().getText() + "' is not defined. Line no:" + ap.getIdentifier().getLine());
                System.exit(1);
            }
            
            ap.getExp().apply(this);
            pp.apply(this);
        }
        node.getIdentifier().apply(this);
    }
    
    public void caseAIdDocument(AIdDocument node)
    {
        String htmlName = node.getIdentifier().getText();
        // check if the html name exists
        if (!SymbolTable.defSymbol(serviceSymbolTable, htmlName))
        {
            System.out.println("Error: Html const '" + htmlName +"' is not defined. Line no:" + node.getIdentifier().getLine());
            System.exit(1);
        }
    }
    
    public void caseACallExp(ACallExp node)
    {
        String name = node.getIdentifier().getText().trim();
        if(SymbolTable.lookupHierarchy(currentSymbolTable, name) == null)
        {
            puts("Function name " + name + " undefined. Line no: " + node.getIdentifier().getLine() );
            System.exit(1);
        }
        
        node.getIdentifier().apply(this);
        
        LinkedList<PExp> expList = node.getExp();
        for (PExp pe: expList)
        {
            pe.apply(this);
        }
    }

    public void caseAArgument(AArgument node)
    {
        String name = node.getIdentifier().getText().trim();
        if(SymbolTable.lookupHierarchy(currentSymbolTable, name) == null)
        {
            puts("Argument name " + name + " undefined. Line no: " + node.getIdentifier().getLine() );
            System.exit(1);
        }
    }
    
    @SuppressWarnings("unchecked")
    public void caseASimpleLvalue(ASimpleLvalue node)
    {                   
        String name = node.getIdentifier().toString().trim();
        Symbol symbol;
        symbol = SymbolTable.lookupHierarchy(currentSymbolTable, name);            
        if(symbol == null)
        {
            puts("Error: Symbol " + name + " not defined. Line no:" + node.getIdentifier().getLine());
            System.exit(1);
        }  
        else
        {
            try
            {
                SVariable tup = (SVariable) SymbolTable.lookupHierarchy(currentSymbolTable, name);
                if (tup != null && currentTuple != null)
                {
                    // a tuple                
                    tup.setTupleSymbolTable(new TupleSymbolTable(((HashMap<String, Symbol>) currentTuple.getTupleSymbolTable().getHashMap().clone())));
                    currentTuple = null;
                }
            }
            catch(ClassCastException e)
            {
            }
        }
    }

    public void caseAQualifiedLvalue(AQualifiedLvalue node)
    {
        String leftName = node.getLeft().toString().trim();
        String rightName = node.getRight().toString().trim();        
        Symbol symbolLeft = SymbolTable.lookupHierarchy(currentSymbolTable, leftName);
        
        if(symbolLeft != null)
        {
            symbolLeft = SymbolTable.lookupHierarchy(currentSymbolTable, leftName);            
            if(symbolLeft == null)
            {
                puts("Error: Symbol" + leftName + "not defined. Line no:" + node.getLeft().getLine() );
                System.exit(1);
            }
            if(symbolLeft instanceof SVariable)
            {
                SVariable sVariable = (SVariable) symbolLeft;
                TupleSymbolTable tupleSymbolTable = sVariable.getTupleSymbolTable();
                if(tupleSymbolTable == null)
                {
                    puts("Error: Symbol " + leftName + " is not declared as a tuple. Line no: " + node.getLeft().getLine() );
                    System.exit(1);
                }
                if(!tupleSymbolTable.defSymbol(rightName))
                {
                    puts("Error: Symbol " + rightName + " not defined in the tuple's schema. Line no:" + node.getLeft().getLine() );
                    System.exit(1);
                }
            }
        }
        
    }
    
    public void caseAExitStm(AExitStm node)
    {
        node.getDocument().apply(this);
    }
    
    private void puts(String s)
    {
        System.out.print(s + "\n");
        System.out.flush();
    }            
    
    @SuppressWarnings("unchecked")
    public void caseAKeepExp(AKeepExp node)
    {
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }

        if(node.getLeft() instanceof ALvalueExp)
        {
            ALvalueExp lvalueExp = (ALvalueExp) node.getLeft();
            if(lvalueExp.getLvalue() instanceof ASimpleLvalue)
            {
                ASimpleLvalue simpleLvalue = (ASimpleLvalue) lvalueExp.getLvalue();
                String tupleName = simpleLvalue.getIdentifier().getText().trim();
                
                SVariable firstTup;
                firstTup = (SVariable) SymbolTable.lookupHierarchy(currentSymbolTable, tupleName);
                TupleSymbolTable tst = firstTup.getTupleSymbolTable();
                
                currentTuple = new SVariable();
                        
                TupleSymbolTable tstTemp = new TupleSymbolTable((HashMap<String, Symbol>) tst.getHashMap().clone());
                
                currentTuple.setTupleSymbolTable(tstTemp);
                
                tst = currentTuple.getTupleSymbolTable();

                if(tst != null)
                {
                    // check if identifier part of tuple's schema
                    String key = node.getIdentifier().getText().trim();
                    if (tst.defSymbol(key))
                    {
                        tst.keep(key);
                    }
                    else
                    {
                        puts("Error: " + key + " is not part of that Tuple. Line no:" + node.getIdentifier().getLine());
                        System.exit(-1);
                    }
                }
                else
                {
                    puts("Error: Tuple does not have fields");
                    System.exit(-1);
                }
            }
        }
        else if(currentTuple != null)
        {
            TupleSymbolTable tst = currentTuple.getTupleSymbolTable();
            if(tst != null)
            {
                // check if identifier part of tuple's schema
                String key = node.getIdentifier().getText().trim();
                if (tst.defSymbol(key))
                {
                    tst.keep(key);
                }
                else
                {
                    puts("Error: " + key + " is not part of that Tuple. Line no:" + node.getIdentifier().getLine());
                    System.exit(-1);
                }
            }
            else
            {
                puts("Error: Not a tuple.");
                System.exit(-1);
            }
        }
        else
        {
            puts("Error: Wrong format for keep expression.");
        }
    }
    
    @SuppressWarnings("unchecked")
    public void caseARemoveExp(ARemoveExp node)
    {
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }

        if(node.getLeft() instanceof ALvalueExp)
        {
            ALvalueExp lvalueExp = (ALvalueExp) node.getLeft();
            if(lvalueExp.getLvalue() instanceof ASimpleLvalue)
            {
                ASimpleLvalue simpleLvalue = (ASimpleLvalue) lvalueExp.getLvalue();
                String tupleName = simpleLvalue.getIdentifier().getText().trim();
                
                SVariable firstTup;
                firstTup = (SVariable) SymbolTable.lookupHierarchy(currentSymbolTable, tupleName);
                TupleSymbolTable tst = firstTup.getTupleSymbolTable();
                
                currentTuple = new SVariable();
                        
                TupleSymbolTable tstTemp = new TupleSymbolTable((HashMap<String, Symbol>) tst.getHashMap().clone());
                
                currentTuple.setTupleSymbolTable(tstTemp);
                
                tst = currentTuple.getTupleSymbolTable();
                
                if(tst != null)
                {
                    // check if identifier part of tuple's schema
                    String key = node.getIdentifier().getText().trim();
                    if (tst.defSymbol(key))
                    {
                        tst.remove(key);
                    }
                    else
                    {
                        puts("Error: " + key + " is not part of that Tuple. Line no:" + node.getIdentifier().getLine());
                        System.exit(-1);
                    }
                }
                else
                {
                    puts("Error: Tuple does not have fields");
                    System.exit(-1);
                }
            }
        }
        else if(currentTuple != null)
        {
            TupleSymbolTable tst = currentTuple.getTupleSymbolTable();
            if(tst != null)
            {
                // check if identifier part of tuple's schema
                String key = node.getIdentifier().getText().trim();
                if (tst.defSymbol(key))
                {
                    tst.remove(key);
                }
                else
                {
                    puts("Error: " + key + " is not part of that Tuple. Line no:" + node.getIdentifier().getLine());
                    System.exit(-1);
                }
            }
            else
            {
                puts("Error: Not a tuple.");
                System.exit(-1);
            }
        }
        else
        {
            puts("Error: Wrong format for keep expression.");
        }
    }
    
    
    
    public void caseARemoveManyExp(ARemoveManyExp node)
    {
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getIdentifier() != null)
        {
            for (TIdentifier ti: node.getIdentifier())
            {
                ti.apply(this);
            }
        }

        if(node.getLeft() instanceof ALvalueExp)
        {
            ALvalueExp lvalueExp = (ALvalueExp) node.getLeft();
            if(lvalueExp.getLvalue() instanceof ASimpleLvalue)
            {
                ASimpleLvalue simpleLvalue = (ASimpleLvalue) lvalueExp.getLvalue();
                String tupleName = simpleLvalue.getIdentifier().getText().trim();
                
                SVariable firstTup;
                firstTup = (SVariable) SymbolTable.lookupHierarchy(currentSymbolTable, tupleName);
                TupleSymbolTable tst = firstTup.getTupleSymbolTable();
                
                currentTuple = new SVariable();
                        
                @SuppressWarnings("unchecked")
                TupleSymbolTable tstTemp = new TupleSymbolTable((HashMap<String, Symbol>) tst.getHashMap().clone());
                
                currentTuple.setTupleSymbolTable(tstTemp);
                
                tst = currentTuple.getTupleSymbolTable();
                
                if(tst != null)
                {
                    for (TIdentifier ti: node.getIdentifier())
                    {
                        String key = ti.getText().trim();
                        if (tst.defSymbol(key))
                        {
                            tst.remove(key);
                        }
                        else
                        {
                            puts("Error: " + key + " is not part of that Tuple. Line no:" + ti.getLine());
                            System.exit(-1);
                        }
                    }
                }
                else
                {
                    puts("Error: Tuple does not have fields");
                    System.exit(-1);
                }
            }
        }
        else if(currentTuple != null)
        {
            TupleSymbolTable tst = currentTuple.getTupleSymbolTable();
            if(tst != null)
            {
                for (TIdentifier ti: node.getIdentifier())
                {
                    String key = ti.getText().trim();
                    if (tst.defSymbol(key))
                    {
                        tst.remove(key);
                    }
                    else
                    {
                        puts("Error: " + key + " is not part of that Tuple. Line no:" + ti.getLine());
                        System.exit(-1);
                    }
                }
            }
            else
            {
                puts("Error: Not a tuple.");
                System.exit(-1);
            }
        }
        else
        {
            puts("Error: Wrong format for keep expression.");
        }
    }
    
    
    public void caseAKeepManyExp(AKeepManyExp node)
    {
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getIdentifier() != null)
        {
            for (TIdentifier ti: node.getIdentifier())
            {
                ti.apply(this);
            }
        }

        if(node.getLeft() instanceof ALvalueExp)
        {
            ALvalueExp lvalueExp = (ALvalueExp) node.getLeft();
            if(lvalueExp.getLvalue() instanceof ASimpleLvalue)
            {
                ASimpleLvalue simpleLvalue = (ASimpleLvalue) lvalueExp.getLvalue();
                String tupleName = simpleLvalue.getIdentifier().getText().trim();
                
                SVariable firstTup;
                firstTup = (SVariable) SymbolTable.lookupHierarchy(currentSymbolTable, tupleName);
                TupleSymbolTable tst = firstTup.getTupleSymbolTable();
                
                currentTuple = new SVariable();
                        
                @SuppressWarnings("unchecked")
                TupleSymbolTable tstTemp = new TupleSymbolTable((HashMap<String, Symbol>) tst.getHashMap().clone());
                
                currentTuple.setTupleSymbolTable(tstTemp);
                
                tst = currentTuple.getTupleSymbolTable();
                  
                if(tst != null)
                {
                    ArrayList<String> keys = new ArrayList<String>();
                    
                    for (TIdentifier ti: node.getIdentifier())
                    {
                        String key = ti.getText().trim();
                        if (tst.defSymbol(key))
                        {
                            keys.add(key);
                        }
                        else
                        {
                            puts("Error: " + key + " is not part of that Tuple. Line no:" + ti.getLine());
                            System.exit(-1);
                        }
                    }
                    tst.keepMany(keys.toArray());
                }
                else
                {
                    puts("Error: Not a tuple.Line no: " + simpleLvalue.getIdentifier().getLine());
                    System.exit(-1);
                }
            }
        }
        else if(currentTuple != null)
        {
            TupleSymbolTable tst = currentTuple.getTupleSymbolTable();
            if(tst != null)
            {
                ArrayList<String> keys = new ArrayList<String>();
                
                for (TIdentifier ti: node.getIdentifier())
                {
                    String key = ti.getText().trim();
                    if (tst.defSymbol(key))
                    {
                        keys.add(key);
                    }
                    else
                    {
                        puts("Error: " + key + " is not part of that Tuple. Line no:" + ti.getLine());
                        System.exit(-1);
                    }
                }
                tst.keepMany(keys.toArray());
            }
            else
            {
                puts("Error: Not a tuple.");
                System.exit(-1);
            }
        }
        else
        {
            puts("Error: Wrong format for keep expression.");
        }
    }
    
    public void caseAJoinExp(AJoinExp node)
    {
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);        
        }

        if(node.getLeft() instanceof ALvalueExp)
        {
            ALvalueExp lvalueExp = (ALvalueExp) node.getLeft();
            if(lvalueExp.getLvalue() instanceof ASimpleLvalue)
            {
                ASimpleLvalue simpleLvalue = (ASimpleLvalue) lvalueExp.getLvalue();
                String tupleName = simpleLvalue.getIdentifier().getText().trim();
                currentTuple = (SVariable) SymbolTable.lookupHierarchy(currentSymbolTable, tupleName);
                TupleSymbolTable tst = currentTuple.getTupleSymbolTable();
                SVariable rightTuple = null;
                
                if (node.getRight() instanceof ALvalueExp)
                {
                    ALvalueExp lvalueExp2 = (ALvalueExp) node.getRight();
                    ASimpleLvalue simpleLvalue2 = (ASimpleLvalue) lvalueExp2.getLvalue();
                    String tupleName2 = simpleLvalue2.getIdentifier().getText().trim();
                    rightTuple = (SVariable) SymbolTable.lookupHierarchy(currentSymbolTable, tupleName2);
                }
                
                if(tst != null && rightTuple.getTupleSymbolTable() != null)
                {
                    tst.merge(rightTuple.getTupleSymbolTable());
                }
                else
                {
                    puts("Error: Not a tuple.Line no: " + simpleLvalue.getIdentifier().getLine());
                    System.exit(-1);
                }
            }
        }
        else if(currentTuple != null)
        {
            TupleSymbolTable tst = currentTuple.getTupleSymbolTable();
            SVariable rightTuple = null;
            
            if (node.getRight() instanceof ALvalueExp)
            {
                ALvalueExp lvalueExp2 = (ALvalueExp) node.getRight();
                ASimpleLvalue simpleLvalue2 = (ASimpleLvalue) lvalueExp2.getLvalue();
                String tupleName2 = simpleLvalue2.getIdentifier().getText().trim();
                rightTuple = (SVariable) SymbolTable.lookupHierarchy(currentSymbolTable, tupleName2);
            }
            
            if(tst != null && rightTuple.getTupleSymbolTable() != null)
            {
                tst.merge(rightTuple.getTupleSymbolTable());
            }
            else
            {
                puts("Error: Not a tuple.");
                System.exit(-1);
            }
        }    
    }
    
    
    public void caseAAssignExp(AAssignExp node)
    {
        node.getRight().apply(this);
        node.getLvalue().apply(this);

    }    
}