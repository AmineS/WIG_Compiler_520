package wig.symboltable.symbols;

import wig.node.ATupleType;
import wig.node.AVariable;
import wig.symboltable.TupleSymbolTable;


public class SVariable extends Symbol
{
    private AVariable fVariable;
    private TupleSymbolTable fTupleSymbolTable;

    public AVariable getVariable()
    {
        return fVariable;
    }
    
    public TupleSymbolTable getTupleSymbolTable()
    {
        return fTupleSymbolTable;
    }

    public void setVariable(AVariable fVariable)
    {
        this.fVariable = fVariable;
    }
    
    public void setTupleSymbolTable(TupleSymbolTable symTable)
    {
        fTupleSymbolTable = symTable;
    }
}
