package wig.symboltable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import wig.analysis.DepthFirstAdapter;
import wig.node.ACallExp;
import wig.node.ACompStm;
import wig.node.ACompoundstm;
import wig.node.AExpStm;
import wig.node.AFunction;
import wig.node.AHtml;
import wig.node.ASession;
import wig.node.Node;
import wig.node.PHtmlbody;
import wig.node.PStm;
import wig.node.PVariable;
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
    
    public void caseACallExp(ACallExp node)
    {
        String name = node.getIdentifier().getText().trim();
        if(!SymbolTable.defSymbol(currentSymbolTable, name))
        {
            puts("Function name " + name + " undefined.");
        }
    }

    
    
    private void puts(String s)
    {
        System.out.print(s + "\n");
        System.out.flush();
    }            
}
