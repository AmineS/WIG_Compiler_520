package wig.symboltable;

import java.util.HashMap;

import wig.symboltable.symbols.Symbol;

public class TupleSymbolTable
{
    private HashMap<String, Symbol> fTupleSymbolTable;

    @SuppressWarnings("unchecked")
    public TupleSymbolTable(HashMap<String, Symbol> table)
    {
        fTupleSymbolTable = (HashMap<String, Symbol>) table.clone();
    }
    
    public void removeField(String key)
    {
        this.fTupleSymbolTable.remove(key);
    }
    
    public boolean defSymbol(String name)
    {
        return fTupleSymbolTable.containsKey(name);
    }
    
    public Symbol getSymbol(String name)
    {
        return fTupleSymbolTable.get(name);
    }
}
