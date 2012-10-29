package wig.symboltable.symbols;

import wig.node.AInputHtmlbody;
import wig.symboltable.SymbolTable;

public class SInput extends Symbol
{
    private AInputHtmlbody fInput;
    private SymbolTable fSymTable;
    
    public SymbolTable getSymTable()
    {
        return fSymTable;
    }

    public void setSymTable(SymbolTable fSymTable)
    {
        this.fSymTable = fSymTable;
    }

    public AInputHtmlbody getInput()
    {
        return fInput;
    }

    public void setInput(AInputHtmlbody fInput)
    {
        this.fInput = fInput;
    }
}
