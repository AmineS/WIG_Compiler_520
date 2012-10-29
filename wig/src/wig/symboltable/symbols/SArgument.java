package wig.symboltable.symbols;

import wig.node.AArgument;

public class SArgument extends Symbol
{
    private AArgument fArgument;

    public AArgument getArgument()
    {
        return fArgument;
    }

    public void setArgument(AArgument fArgument)
    {
        this.fArgument = fArgument;
    }
}
