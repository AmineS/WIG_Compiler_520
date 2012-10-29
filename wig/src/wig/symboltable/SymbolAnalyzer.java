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
    
    
    public void inAFunction(AFunction function)
    {
        SymbolTable scopedSymbolTable = SymbolTable.scopeSymbolTable(fCurrentSymTable);
        fSymbolTables.add(scopedSymbolTable);
        fCurrentSymTable = scopedSymbolTable;
    }
    
    public void caseAFunction(AFunction node)
    {
        inAFunction(node);

        String name = node.getIdentifier().toString().trim();
        if(fTraversal == SymbolAnalysisTraversal.COLLECT_IDENTIFIERS)
        {
            if(SymbolTable.getSymbol(fCurrentSymTable.getNext(), name) != null)
            {
                puts("Error: Function name " + name + " already defined.");
            }
            else
            {
                SymbolTable.putSymbol(fCurrentSymTable.getNext(), name, SymbolKind.FUNCTION, node, fCurrentSymTable);                
            }
        }
        
        if(node.getCompoundstm() != null)
        {
            node.getCompoundstm().apply(this);
        }
        
        List<PArgument> copy = new ArrayList<PArgument>(node.getArgument());
        for(PArgument e : copy)
        {
            e.apply(this);
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

        String name = node.getIdentifier().toString().trim();
        if(fTraversal == SymbolAnalysisTraversal.COLLECT_IDENTIFIERS)
        {
            if(SymbolTable.getSymbol(fCurrentSymTable.getNext(), name) != null)
            {
                puts("Error: Session name " + name + " already defined.");
            }
            else
            {
                SymbolTable.putSymbol(fCurrentSymTable.getNext(), name, SymbolKind.SESSION, node, fCurrentSymTable);
            }
        }
        
        if(node.getCompoundstm() != null)
        {
            node.getCompoundstm().apply(this);
        }
        
        
        outASession(node);
    }
    
    public void outASession(ASession node)
    {
        fCurrentSymTable = fCurrentSymTable.getNext();
    }
    
    public void caseAInputHtmlbody(AInputHtmlbody node)
    {
        LinkedList<PInputattr> attributes = node.getInputattr();
        String name = null;
        for(PInputattr attribute : attributes)
        {
            if(attribute instanceof  ANameInputattr)
            {
                ANameInputattr nameAttr = (ANameInputattr) attribute;
                AStrAttr strAttr = (AStrAttr) nameAttr.getAttr();
                name = strAttr.getStringconst().getText().trim().replace("\"", "");
            }
        }
        if(name != null)
        {
            if(fTraversal == SymbolAnalysisTraversal.COLLECT_IDENTIFIERS)
            {
                if(SymbolTable.getSymbol(fCurrentSymTable, name) != null)
                {
                    puts("Error: Input name " + name + " already defined.");
                }
                else
                {
                    SymbolTable.putSymbol(fCurrentSymTable, name, SymbolKind.INPUT_TAG, node, fCurrentSymTable);
                }
            }
        }
    }
    
    public void caseASelectHtmlbody(ASelectHtmlbody node)
    {
        LinkedList<PInputattr> attributes = node.getInputattr();
        String name = null;
        for(PInputattr attribute : attributes)
        {
            if(attribute instanceof  ANameInputattr)
            {
                ANameInputattr nameAttr = (ANameInputattr) attribute;
                name = nameAttr.getName().toString().trim();
            }
        }
        if(name != null)
        {
            if(fTraversal == SymbolAnalysisTraversal.COLLECT_IDENTIFIERS)
            {
                if(SymbolTable.getSymbol(fCurrentSymTable, name) != null)
                {
                    puts("Error: Input name " + name + " already defined.");
                }
                else
                {
                    SymbolTable.putSymbol(fCurrentSymTable, name, SymbolKind.SELECT_TAG, node, fCurrentSymTable);
                }
            }
        }
    }
    
    public void caseACompStm(ACompStm node)
    {
        node.getCompoundstm().apply(this);
    }
    
    public void inACompoundStm(ACompoundstm node)
    {
        if(! (node.parent() instanceof AFunction || node.parent() instanceof ASession) )
        {
            SymbolTable scopedSymbolTable = SymbolTable.scopeSymbolTable(fCurrentSymTable);
            fSymbolTables.add(scopedSymbolTable);
            fCurrentSymTable = scopedSymbolTable;
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
        if(! (node.parent() instanceof AFunction))
        {
            fCurrentSymTable = fCurrentSymTable.getNext();
        }
    }
    
    
    public void caseAField(AField node)
    {
        String name = node.getIdentifier().toString().trim();
        if(SymbolTable.getSymbol(fCurrentSymTable, name) != null)
        {
            puts("Error: Field name " + name + " already defined.");
        }
        else
        {
            SymbolTable.putSymbol(fCurrentSymTable, name, SymbolKind.FIELD, node, fCurrentSymTable);
        }
    }
    
    
    private void puts(String s)
    {
        System.out.print(s + "\n");
        System.out.flush();
    }            
}
