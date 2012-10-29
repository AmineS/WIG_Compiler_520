package wig.symboltable.symbols;

import wig.node.AHtml;
import wig.symboltable.SymbolTable;

public class SHtml extends Symbol
{
    private AHtml fHtml;
    private SymbolTable fSymTable;
    
    public SymbolTable getSymTable()
    {
        return fSymTable;
    }
    public void setSymTable(SymbolTable fSymTable)
    {
        this.fSymTable = fSymTable;
    }
    public AHtml getHtml()
    {
        return fHtml;
    }
    public void setHtml(AHtml fHtml)
    {
        this.fHtml = fHtml;
    }
}
