package wig.symboltable.symbols;

import wig.node.ASchema;
import wig.symboltable.SymbolTable;

public class SSchema extends Symbol
{
    private ASchema fSchema;
    private SymbolTable fSymTable;
    
    public SymbolTable getSymTable()
    {
        return fSymTable;
    }

    public void setSymTable(SymbolTable fSymTable)
    {
        this.fSymTable = fSymTable;
    }

    public ASchema getSchema()
    {
        return fSchema;
    }

    public void setSchema(ASchema fSchema)
    {
        this.fSchema = fSchema;
    }
}
