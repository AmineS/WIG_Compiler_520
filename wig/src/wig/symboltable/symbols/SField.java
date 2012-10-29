package wig.symboltable.symbols;

import wig.node.AField;

public class SField extends Symbol
{
    private AField fField;

    public AField getField()
    {
        return fField;
    }

    public void setField(AField fField)
    {
        this.fField = fField;
    }
}
