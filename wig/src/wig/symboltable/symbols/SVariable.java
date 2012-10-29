package wig.symboltable.symbols;

import wig.node.AVariable;


public class SVariable extends Symbol
{
    private AVariable fVariable;

    public AVariable getVariable()
    {
        return fVariable;
    }

    public void setVariable(AVariable fVariable)
    {
        this.fVariable = fVariable;
    }
}
