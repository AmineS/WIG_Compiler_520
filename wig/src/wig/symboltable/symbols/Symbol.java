package wig.symboltable.symbols;

import wig.symboltable.SymbolKind;

public abstract class Symbol
{
    private String fName;
    private SymbolKind fKind;
    private Symbol fNext;
    
    public Symbol(){}
   
    public String getName()
    {
        return fName;
    }
    
    public SymbolKind getKind()
    {
        return fKind;
    }
    
  
    public Symbol getNext()
    {
        return fNext;
    }
    
    public void setName(String name)
    {
       fName = name;
    }
    
    public void setKind(SymbolKind kind)
    {
        fKind = kind;
    }    
    
    public void setNext(Symbol next)
    {
        fNext = next;
    }
}
