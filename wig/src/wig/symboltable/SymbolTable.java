package wig.symboltable;

import java.util.HashMap;

public class SymbolTable
{
    private HashMap<String, Symbol> fTable;
    private SymbolTable fNext;
    
    public SymbolTable()
    {
        fTable = new HashMap<String, Symbol>();
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
    
    public static Symbol putSymbol(SymbolTable symTable, String name, SymbolKind symKind)
    {
        if(symTable.fTable.containsKey(name))
        {
            return symTable.fTable.get(name);
        }
        Symbol sym = new Symbol();
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
        
        symbol = current.getSymbol(current, name);                
        if(symbol == null && current.getNext() != null)
        {
            symbol = lookupHierarchy(current.getNext(), name);
        }
        
        return symbol;
    }    
}
