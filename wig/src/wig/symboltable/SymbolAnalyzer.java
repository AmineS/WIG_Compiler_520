package wig.symboltable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import wig.analysis.DepthFirstAdapter;
import wig.node.ACallExp;
import wig.node.ACompStm;
import wig.node.ACompoundstm;
import wig.node.AExitStm;
import wig.node.AFunction;
import wig.node.AHtml;
import wig.node.AIdDocument;
import wig.node.AInput;
import wig.node.APlug;
import wig.node.APlugDocument;
import wig.node.APlugs;
import wig.node.AQualifiedLvalue;
import wig.node.AReceive;
import wig.node.ASession;
import wig.node.AShowStm;
import wig.node.ASimpleLvalue;
import wig.node.Node;
import wig.node.PHtmlbody;
import wig.node.PInput;
import wig.node.PPlug;
import wig.node.PStm;
import wig.node.PVariable;
import wig.symboltable.symbols.SVariable;
import wig.symboltable.symbols.Symbol;

public class SymbolAnalyzer extends DepthFirstAdapter
{
    SymbolTable serviceSymbolTable;
    SymbolTable currentSymbolTable;

    public void analyze(Node node)
    {
        node.apply(this);
    }
    
    public SymbolAnalyzer(SymbolTable symbolTable)
    {
        serviceSymbolTable = symbolTable;
        currentSymbolTable= serviceSymbolTable;
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
    }

    public void caseASimpleLvalue(ASimpleLvalue node)
    {                   
        String name = node.getIdentifier().toString().trim();
        Symbol symbol;
        
        symbol = SymbolTable.getSymbol(currentSymbolTable, name);
        
        if(symbol == null)
        {
            symbol = SymbolTable.lookupHierarchy(currentSymbolTable, name);            
            if(symbol == null)
            {
                puts("Error: Symbol " + name + " not defined. Line no:" + node.getIdentifier().getLine());
                System.exit(1);
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
                SymbolTable symbolTableOfSchema = SymbolTable.getSymbolTableOfSchema(currentSymbolTable, sVariable.getVariable());
                if(!SymbolTable.defSymbol(symbolTableOfSchema, rightName))
                {
                    puts("Error: Symbol" + rightName + " not defined in the tuple's schema. Line no:" + node.getLeft().getLine() );
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
}
