package wig.symboltable.symbols;

import wig.node.AFunction;
import wig.symboltable.SymbolTable;

public class SFunction extends Symbol
{
    private AFunction fFunction;
    private SymbolTable fSymTable;
    
    public SymbolTable getSymTable()
    {
        return fSymTable;
    }

    public void setSymTable(SymbolTable fSymTable)
    {
        this.fSymTable = fSymTable;
    }

    public AFunction getFunction()
    {
        return fFunction;
    }

    public void setFunction(AFunction fFunction)
    {
        this.fFunction = fFunction;
    }
}
