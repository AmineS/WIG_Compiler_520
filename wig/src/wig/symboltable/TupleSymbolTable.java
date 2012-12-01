package wig.symboltable;

import java.util.HashMap;

import wig.symboltable.symbols.Symbol;

public class TupleSymbolTable implements Cloneable
{
    private HashMap<String, Symbol> fTupleSymbolTable;

    @SuppressWarnings("unchecked")
    public TupleSymbolTable(HashMap<String, Symbol> table)
    {
        fTupleSymbolTable = (HashMap<String, Symbol>) table.clone();
    }
    
    
    public TupleSymbolTable(HashMap<String, Symbol> table, int i)
    {
        fTupleSymbolTable = table;
    }
    
    public void removeField(String key)
    {
        this.fTupleSymbolTable.remove(key);
    }
    
    /**
     * This is used by keep for tuples. Keeps only the field 'key'
     * @param key
     */
    public void keep(String key)
    {
        HashMap<String, Symbol> tempSymTable = new HashMap<String, Symbol>();
        tempSymTable.put(key, fTupleSymbolTable.get(key));
        fTupleSymbolTable = tempSymTable;
    }
    
    
    public void keepMany(Object[] keys)
    {
        HashMap<String, Symbol> tempSymTable = new HashMap<String, Symbol>();
        for (Object key: keys)
        {
            tempSymTable.put((String)key, fTupleSymbolTable.get((String)key));
        }
        fTupleSymbolTable = tempSymTable;
    }
    
    /**
     * Remove field from tuple.
     * @param key
     * @return
     */
    public void remove(String key)
    {
        fTupleSymbolTable.remove(key);
    }

    /**
     * Merge tuples
     * @param tst
     */
    public void merge(TupleSymbolTable tst)
    {
        for (String s: tst.getHashMap().keySet())
        {
            if (fTupleSymbolTable.containsKey(s))
                continue;
            else
            {
                fTupleSymbolTable.put(s, tst.getHashMap().get(s));
            }
        }
    }
    
    public boolean defSymbol(String name)
    {
        return fTupleSymbolTable.containsKey(name);
    }
    
    public Symbol getSymbol(String name)
    {
        return fTupleSymbolTable.get(name);
    }
    
    public HashMap<String, Symbol> getHashMap()
    {
        return fTupleSymbolTable;
    }
    
}
