package wig.symboltable.symbols;

import wig.node.ASelectHtmlbody;
import wig.symboltable.SymbolTable;

public class SSelect extends Symbol
{
    private ASelectHtmlbody fSelect;
    private SymbolTable fSymTable;
    
    public SymbolTable getSymTable()
    {
        return fSymTable;
    }

    public void setSymTable(SymbolTable fSymTable)
    {
        this.fSymTable = fSymTable;
    }

    public ASelectHtmlbody getSelect()
    {
        return fSelect;
    }

    public void setSelect(ASelectHtmlbody fSelect)
    {
        this.fSelect = fSelect;
    }
}
