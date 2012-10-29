package wig.symboltable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import wig.analysis.DepthFirstAdapter;
import wig.node.AArgument;
import wig.node.ACompStm;
import wig.node.ACompoundstm;
import wig.node.AField;
import wig.node.AFunction;
import wig.node.AHoleHtmlbody;
import wig.node.AHtml;
import wig.node.AInputHtmlbody;
import wig.node.ANameInputattr;
import wig.node.ASchema;
import wig.node.ASelectHtmlbody;
import wig.node.AService;
import wig.node.ASession;
import wig.node.AStrAttr;
import wig.node.AVariable;
import wig.node.Node;
import wig.node.PArgument;
import wig.node.PField;
import wig.node.PFunction;
import wig.node.PHtml;
import wig.node.PHtmlbody;
import wig.node.PInputattr;
import wig.node.PSchema;
import wig.node.PSession;
import wig.node.PStm;
import wig.node.PType;
import wig.node.PVariable;
import wig.node.TIdentifier;
import wig.symboltable.symbols.*;

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
        Symbol symbol;
        
        symbol = SymbolTable.getSymbol(currentSymbolTable, name);
        
        if(symbol == null)
        {
            symbol = SymbolTable.lookupHierarchy(currentSymbolTable, name);            
            if(symbol == null)
            {
                puts("Error: Symbol" + name + "not defined." );
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
        currentSymbolTable = currentSymbolTable.getCompoundStatementSymbolTable(node);
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
        currentSymbolTable = currentSymbolTable.getNext();
    }

    
    
    private void puts(String s)
    {
        System.out.print(s + "\n");
        System.out.flush();
    }            
}
