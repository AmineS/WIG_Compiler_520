package wig.symboltable;

import java.util.HashMap;
import java.util.LinkedList;

import wig.symboltable.symbols.Symbol;

public class SymbolTablePrinter
{
    private LinkedList<SymbolTable> fSymTabList = new LinkedList<SymbolTable>();
    
    public SymbolTablePrinter(SymbolCollector sta)
    {
        this.fSymTabList = sta.getSymbolTables();
    }
    
    public void printAll()
    {
        System.out.println("\nSymbol Tables:");
        HashMap<String,Symbol> currentHashMap;
        System.out.println("---------------------------------------");
        System.out.println("Name\t\t|\tKind");
        for (SymbolTable st: this.fSymTabList)
        {
            currentHashMap = st.getTable();
            
            if (currentHashMap.keySet().isEmpty())
            {
                continue;
            }
            
            System.out.println("---------------------------------------");
            for (String s: currentHashMap.keySet())
            {
                if (s.length()>=8 && s.length()<16)
                {
                    System.out.println(s + "\t|\t" + currentHashMap.get(s).getKind().name().toLowerCase().replace("_", " "));
                }
                else
                {
                    System.out.println(s + "\t\t|\t" + currentHashMap.get(s).getKind().name().toLowerCase().replace("_", " "));
                }
            }
        }
        System.out.println("---------------------------------------");
    }
}
