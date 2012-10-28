package wig.symboltable;

import java.util.HashMap;
import java.util.LinkedList;

public class SymbolTablePrinter
{

    private LinkedList<SymbolTable> fSymTabList = new LinkedList<SymbolTable>();
    
    public SymbolTablePrinter(SymbolCollector sta)
    {
        this.fSymTabList = sta.getSymbolTables();
    }
    
    public void printAll()
    {
        HashMap<String,Symbol> currentHashMap;
        for (SymbolTable st: this.fSymTabList)
        {
            currentHashMap = st.getTable();
            
            if (currentHashMap.keySet().isEmpty())
            {
                continue;
            }
            
            System.out.println("---------------------------------------------");
            for (String s: currentHashMap.keySet())
            {
                System.out.println(s + '\t' + currentHashMap.get(s).getKind().name().toLowerCase().replace("_", " "));
            }
            
        }
    }
}
