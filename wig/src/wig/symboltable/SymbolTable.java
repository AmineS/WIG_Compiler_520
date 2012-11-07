package wig.symboltable;

import java.util.HashMap;

import wig.node.AArgument;
import wig.node.ACompoundstm;
import wig.node.AField;
import wig.node.AFunction;
import wig.node.AHoleHtmlbody;
import wig.node.AHtml;
import wig.node.AInputHtmlbody;
import wig.node.ASchema;
import wig.node.ASelectHtmlbody;
import wig.node.ASession;
import wig.node.ATupleType;
import wig.node.AVariable;
import wig.node.Node;
import wig.symboltable.symbols.SArgument;
import wig.symboltable.symbols.SField;
import wig.symboltable.symbols.SFunction;
import wig.symboltable.symbols.SHole;
import wig.symboltable.symbols.SHtml;
import wig.symboltable.symbols.SInput;
import wig.symboltable.symbols.SSchema;
import wig.symboltable.symbols.SSelect;
import wig.symboltable.symbols.SSession;
import wig.symboltable.symbols.SVariable;
import wig.symboltable.symbols.Symbol;

public class SymbolTable
{
    private HashMap<String, Symbol> fTable;
    private HashMap<ACompoundstm, SymbolTable> fCompoundStms;
    private SymbolTable fNext;
    
    public SymbolTable()
    {
        fTable = new HashMap<String, Symbol>();
        fCompoundStms = new HashMap<ACompoundstm, SymbolTable>();
        fNext = null;
    }
    
    public HashMap<String, Symbol> getTable()
    {
       return fTable; 
    }
    
    public SymbolTable getNext()
    {
        return fNext;
    }
    
    public void setNext(SymbolTable next)
    {
        fNext = next;
    }
    
    public static SymbolTable scopeSymbolTable(SymbolTable symTable)
    {
        SymbolTable scopedSymTable = new SymbolTable();
        scopedSymTable.setNext(symTable);
        return scopedSymTable;
    }
    
    public static Symbol putSymbol(SymbolTable symTable, String name, SymbolKind symKind, Node node, SymbolTable newSymTable)
    {
        if(symTable.fTable.containsKey(name))
        {
            return symTable.fTable.get(name);
        }
        
        Symbol sym = null;        
        switch (symKind)
        {
            case HTML_CONST:
                sym = new SHtml();
                ((SHtml)sym).setHtml((AHtml)node);
                ((SHtml)sym).setSymTable(newSymTable);
                break;
            case INPUT_TAG: 
                sym = new SInput();
                ((SInput)sym).setInput((AInputHtmlbody)node);
                ((SInput)sym).setSymTable(newSymTable);
                break;
            case SELECT_TAG: 
                sym = new SSelect();
                ((SSelect)sym).setSelect((ASelectHtmlbody)node);
                ((SSelect)sym).setSymTable(newSymTable);
                break;
            case HOLE: 
                sym = new SHole();
                ((SHole)sym).setHole((AHoleHtmlbody)node);
                break;
            case SCHEMA: 
                sym = new SSchema();
                ((SSchema)sym).setSchema((ASchema)node);
                ((SSchema)sym).setSymTable(newSymTable);
                break;
            case VARIABLE: 
                sym = new SVariable();
                ((SVariable)sym).setVariable((AVariable)node);
                SymbolTable variableSymTable = (SymbolTable) getSymbolTableOfSchema(symTable, node);
                if(variableSymTable != null)
                {
                    TupleSymbolTable tupleSymbolTable = new TupleSymbolTable(variableSymTable.getTable());
                    ((SVariable)sym).setTupleSymbolTable(tupleSymbolTable);
                }
                break;
            case FUNCTION: 
                sym = new SFunction();
                ((SFunction)sym).setFunction((AFunction)node);
                ((SFunction)sym).setSymTable(newSymTable);
                break;
            case ARGUMENT: 
                sym = new SArgument();
                ((SArgument)sym).setArgument((AArgument)node);
                break;
            case SESSION: 
                sym = new SSession();
                ((SSession)sym).setSession((ASession)node);
                ((SSession)sym).setSymTable(newSymTable);
                break;
            case FIELD:
                sym = new SField();
                ((SField)sym).setField((AField)node);
                break;                
        }        
        sym.setName(name);
        sym.setKind(symKind);
        symTable.fTable.put(name, sym);
        return sym;
    }
    
    public static Symbol getSymbol(SymbolTable symTable, String name)
    {
        return symTable.fTable.get(name);
    }
    
    public static boolean defSymbol(SymbolTable symTable, String name)
    {
        return symTable.fTable.containsKey(name);
    }
    
    public static Symbol lookupHierarchy(SymbolTable current, String name) 
    {               
        Symbol symbol;               
        
        if(current == null) return null;        
        
        symbol = getSymbol(current, name);                
        if(symbol == null && current.getNext() != null)
        {
            symbol = lookupHierarchy(current.getNext(), name);
        }
        
        return symbol;
    }
    
    public static SymbolTable getScopedSymbolTable(Symbol symbol)
    {
        SymbolTable symbolTable = null;
        
        switch(symbol.getKind())
        {
            case HTML_CONST:
                symbolTable = ((SHtml)symbol).getSymTable();
                break;
            case INPUT_TAG: 
                symbolTable = ((SInput)symbol).getSymTable();
                break;
            case SELECT_TAG: 
                symbolTable = ((SSelect)symbol).getSymTable();
                break;
            case HOLE:
                break;
            case SCHEMA: 
                symbolTable = ((SSchema)symbol).getSymTable();
                break;
            case VARIABLE: 
                break;
            case FUNCTION: 
                symbolTable = ((SFunction)symbol).getSymTable();
                break;
            case ARGUMENT: 
                break;
            case SESSION: 
                symbolTable = ((SSession)symbol).getSymTable();
                break;
            case FIELD:
                break;   
            default:
                break;
        }
        
        return symbolTable;
    }
    
    public void setCompoundStatementSymbolTable(ACompoundstm node, SymbolTable symbolTable)
    {
        fCompoundStms.put(node, symbolTable);
    }
    
    public SymbolTable getCompoundStatementSymbolTable(ACompoundstm node)
    {
        return fCompoundStms.get(node);
    }
    
    public static SymbolTable getSymbolTableOfSchema(SymbolTable current, Node node)
    {
        if(node instanceof AVariable)
        {
            AVariable variable = (AVariable) node;
            if(variable.getType() instanceof ATupleType)
            {
                ATupleType tupleType = (ATupleType) variable.getType();
                String nameOfSchema = tupleType.getIdentifier().getText().trim();
                Symbol symbol = lookupHierarchy(current, nameOfSchema);
                if(symbol == null)
                {
                    return null;
                }
                else if(symbol.getKind() == SymbolKind.SCHEMA)
                {
                    SSchema sSchema = (SSchema) symbol;
                    return sSchema.getSymTable();
                }
            }
        }
        
        return null;
    }
}
