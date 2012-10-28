package wig.symboltable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import wig.analysis.DepthFirstAdapter;
import wig.node.AArgument;
import wig.node.ACompStm;
import wig.node.ACompoundstm;
import wig.node.AFunction;
import wig.node.AHoleHtmlbody;
import wig.node.AHtml;
import wig.node.ASchema;
import wig.node.ASession;
import wig.node.AVariable;
import wig.node.Node;
import wig.node.PArgument;
import wig.node.PHtmlbody;
import wig.node.PStm;
import wig.node.PType;
import wig.node.PVariable;
import wig.node.TIdentifier;

public class SymbolAnalyzer extends DepthFirstAdapter
{
    
    private LinkedList<SymbolTable> fSymbolTables = new LinkedList<SymbolTable>();
    private SymbolTable fServiceSymTable = new SymbolTable();
    private SymbolTable fCurrentSymTable = fServiceSymTable;
    private SymbolAnalysisTraversal fTraversal = SymbolAnalysisTraversal.COLLECT_IDENTIFIERS;
    
    
    public void analyze(Node node)
    {
        fSymbolTables.add(fServiceSymTable);
        node.apply(new SymbolAnalyzer());
    }
    
    
    public void caseAHtml(AHtml node)
    {
        String name = node.getIdentifier().toString().trim();
        
        List<PHtmlbody> copy = new ArrayList<PHtmlbody>(node.getHtmlbody());
        for(PHtmlbody e : copy)
        {
            e.apply(this);
        }
        
        if(fTraversal == SymbolAnalysisTraversal.COLLECT_IDENTIFIERS)
        {
            if(SymbolTable.getSymbol(fCurrentSymTable.getNext(), name) != null)
            {
                puts("Error: HTML variable Name " + name + " already defined.");
            }
            else
            {
                Symbol sym = SymbolTable.putSymbol(fCurrentSymTable.getNext(), name, SymbolKind.HTML_CONST);
                sym.setHtml(node);
            }
        }
        
    }
    
    
    public void caseASchema(ASchema node)
    {
        String name = node.getIdentifier().toString().trim();
        
        if(fTraversal == SymbolAnalysisTraversal.COLLECT_IDENTIFIERS)
        {
            if(SymbolTable.getSymbol(fCurrentSymTable, name) != null)
            {
                puts("Error: Schema name " + name + " already defined.");
            }
            else
            {
                Symbol sym = SymbolTable.putSymbol(fCurrentSymTable, name, SymbolKind.SCHEMA);
                sym.setSchema(node);
            }
        }
    }
    
    public void caseAVariable(AVariable node)
    {
        LinkedList<TIdentifier> variables = node.getIdentifier();
        String name = null;
        PType type = node.getType();
        if(fTraversal == SymbolAnalysisTraversal.COLLECT_IDENTIFIERS)
        {
            for(TIdentifier variable : variables)
            {
                name = variable.toString().trim();
                if(SymbolTable.getSymbol(fCurrentSymTable, name) != null)
                {
                    puts("Error: Variable name " + name + " already defined.");
                }
                else
                {
                    List<TIdentifier> tmpList = new ArrayList<TIdentifier>();
                    tmpList.add(new TIdentifier(name));
                    AVariable globalVariable = new AVariable(type, tmpList);
                    Symbol sym = SymbolTable.putSymbol(fCurrentSymTable, name, SymbolKind.VARIABLE);
                    sym.setVariable(globalVariable);
                }
            }
        }
    }
    
    public void inAFunction(AFunction function)
    {
        SymbolTable scopedSymbolTable = SymbolTable.scopeSymbolTable(fCurrentSymTable);
        fSymbolTables.add(scopedSymbolTable);
        fCurrentSymTable = scopedSymbolTable;
    }
    
    public void caseAFunction(AFunction node)
    {
        inAFunction(node);

        List<PArgument> copy = new ArrayList<PArgument>(node.getArgument());
        for(PArgument e : copy)
        {
            e.apply(this);
        }
        
        if(node.getCompoundstm() != null)
        {
            node.getCompoundstm().apply(this);
        }
        
        String name = node.getIdentifier().toString().trim();
        if(fTraversal == SymbolAnalysisTraversal.COLLECT_IDENTIFIERS)
        {
            if(SymbolTable.getSymbol(fCurrentSymTable.getNext(), name) != null)
            {
                puts("Error: Function name " + name + " already defined.");
            }
            else
            {
                Symbol sym = SymbolTable.putSymbol(fCurrentSymTable.getNext(), name, SymbolKind.FUNCTION);
                sym.setFunction(node);
            }
        }
        
        outAFunction(node);
    }
    
    public void outAFunction(AFunction function)
    {
        fCurrentSymTable = fCurrentSymTable.getNext();
    }

    public void inASession(ASession node)
    {
        SymbolTable scopedSymbolTable = SymbolTable.scopeSymbolTable(fCurrentSymTable);
        fSymbolTables.add(scopedSymbolTable);
        fCurrentSymTable = scopedSymbolTable;
    }
    
    public void caseASession(ASession node)
    {
        inASession(node);
        
        if(node.getCompoundstm() != null)
        {
            node.getCompoundstm().apply(this);
        }
        
        String name = node.getIdentifier().toString().trim();
        if(fTraversal == SymbolAnalysisTraversal.COLLECT_IDENTIFIERS)
        {
            if(SymbolTable.getSymbol(fCurrentSymTable.getNext(), name) != null)
            {
                puts("Error: Session name " + name + " already defined.");
            }
            else
            {
                Symbol sym = SymbolTable.putSymbol(fCurrentSymTable.getNext(), name, SymbolKind.SESSION);
                sym.setSession(node);
            }
        }
        
        outASession(node);
    }
    
    public void outASession(ASession node)
    {
        fCurrentSymTable = fCurrentSymTable.getNext();
    }
    
    public void caseAArgument(AArgument node)
    {
        String name = node.getIdentifier().toString().trim();
        if(fTraversal == SymbolAnalysisTraversal.COLLECT_IDENTIFIERS)
        {
            if(SymbolTable.getSymbol(fCurrentSymTable, name) != null)
            {
                puts("Error: Function name " + name + " already defined.");
            }
            else
            {
                Symbol sym = SymbolTable.putSymbol(fCurrentSymTable, name, SymbolKind.ARGUMENT);
                sym.setArgument(node);
            }
        }
    }
    
    // We want hole to be in the global scope
    public void caseHoleHtmlbody(AHoleHtmlbody node)
    {
        String name = node.getIdentifier().toString().trim();
        if(fTraversal == SymbolAnalysisTraversal.COLLECT_IDENTIFIERS)
        {
            if(SymbolTable.getSymbol(fCurrentSymTable, name) != null)
            {
                puts("Error: Hole name " + name + " already defined.");
            }
            else
            {
                Symbol sym = SymbolTable.putSymbol(fCurrentSymTable, name, SymbolKind.HOLE);
                sym.setHole(node);
            }
        }
    }
    
    public void caseACompStm(ACompStm node)
    {
        node.getCompoundstm().apply(this);
    }
    
    public void caseACompoundstm(ACompoundstm node)
    {
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
        
    }
    
    
    
    private void puts(String s)
    {
        System.out.print(s + "\n");
        System.out.flush();
    }
}
