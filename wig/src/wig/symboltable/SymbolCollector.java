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
import wig.node.AInputHtmlbody;
import wig.node.ANameInputattr;
import wig.node.ASchema;
import wig.node.ASelectHtmlbody;
import wig.node.AService;
import wig.node.ASession;
import wig.node.AVariable;
import wig.node.Node;
import wig.node.PArgument;
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

public class SymbolCollector extends DepthFirstAdapter
{
    
    private LinkedList<SymbolTable> fSymbolTables = new LinkedList<SymbolTable>();
    private SymbolTable fServiceSymTable = new SymbolTable();
    private SymbolTable fCurrentSymTable = fServiceSymTable;
    private SymbolAnalysisTraversal fTraversal = SymbolAnalysisTraversal.COLLECT_IDENTIFIERS;
    
    
    public void analyze(Node node)
    {
        fSymbolTables.add(fServiceSymTable);
        node.apply(this);
    }
    
    public void caseAService(AService node)
    {
        
        for(PHtml html : node.getHtml())
        {
            html.apply(this);
        }
        
        for(PSchema schema : node.getSchema())
        {
            schema.apply(this);
        }
        
        for(PVariable variable : node.getVariable())
        {
            variable.apply(this);
        }
        
        for(PFunction function : node.getFunction())
        {
            function.apply(this);
        }
        
        for(PSession session : node.getSession())
        {
            session.apply(this);
        }
    }
    
    
    public void inAHtml(AHtml node)
    {
        SymbolTable scopedSymbolTable = SymbolTable.scopeSymbolTable(fCurrentSymTable);
        fSymbolTables.add(scopedSymbolTable);
        fCurrentSymTable = scopedSymbolTable;
    }
    
    public void caseAHtml(AHtml node)
    {
        inAHtml(node);
        
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
        
        outAHtml(node);
    }
    
    public void outAHtml(AHtml node)
    {
        fCurrentSymTable = fCurrentSymTable.getNext();
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
                if(SymbolTable.lookupHierarchy(fCurrentSymTable, name) != null)
                {
                    puts("Error: Variable name " + name + " already defined.");
                }
                else
                {
                    List<TIdentifier> tmpList = new ArrayList<TIdentifier>();
                    tmpList.add(new TIdentifier(name));
                    AVariable g_l_variable = new AVariable(type, tmpList);
                    Symbol sym = SymbolTable.putSymbol(fCurrentSymTable, name, SymbolKind.VARIABLE);
                    sym.setVariable(g_l_variable);
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
            if(SymbolTable.getSymbol(fCurrentSymTable.getNext(), name) != null)
            {
                puts("Error: Hole name " + name + " already defined.");
            }
            else
            {
                Symbol sym = SymbolTable.putSymbol(fCurrentSymTable.getNext(), name, SymbolKind.HOLE);
                sym.setHole(node);
            }
        }
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
                name = nameAttr.getName().toString().trim();
            }
        }
        if(name != null)
        {
            if(fTraversal == SymbolAnalysisTraversal.COLLECT_IDENTIFIERS)
            {
                if(SymbolTable.getSymbol(fCurrentSymTable.getNext(), name) != null)
                {
                    puts("Error: Input name " + name + " already defined.");
                }
                else
                {
                    Symbol sym = SymbolTable.putSymbol(fCurrentSymTable, name, SymbolKind.INPUT_TAG);
                    sym.setInput(node);
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
                if(SymbolTable.getSymbol(fCurrentSymTable.getNext(), name) != null)
                {
                    puts("Error: Input name " + name + " already defined.");
                }
                else
                {
                    Symbol sym = SymbolTable.putSymbol(fCurrentSymTable, name, SymbolKind.SELECT_TAG);
                    sym.setSelect(node);
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
        SymbolTable scopedSymbolTable = SymbolTable.scopeSymbolTable(fCurrentSymTable);
        fSymbolTables.add(scopedSymbolTable);
        fCurrentSymTable = scopedSymbolTable;
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
    
    public void outACompoundStm(ACompoundstm node)
    {
        fCurrentSymTable = fCurrentSymTable.getNext();
    }
    
    
    private void puts(String s)
    {
        System.out.print(s + "\n");
        System.out.flush();
    }
    
    public SymbolTable getServiceTable()
    {
        return this.fServiceSymTable;
    }
}
