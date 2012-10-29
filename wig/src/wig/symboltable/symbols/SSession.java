package wig.symboltable.symbols;

import wig.node.ASession;
import wig.symboltable.SymbolTable;

public class SSession extends Symbol
{
    private ASession fSession;
    private SymbolTable fSymTable;
    
    
    public SymbolTable getSymTable()
    {
        return fSymTable;
    }

    public void setSymTable(SymbolTable fSymTable)
    {
        this.fSymTable = fSymTable;
    }

    public ASession getSession()
    {
        return fSession;
    }

    public void setSession(ASession fSession)
    {
        this.fSession = fSession;
    }
}
