/* This file was generated by SableCC (http://www.sablecc.org/). */

package wig.node;

import wig.analysis.*;

@SuppressWarnings("nls")
public final class THtml extends Token
{
    public THtml()
    {
        super.setText("html");
    }

    public THtml(int line, int pos)
    {
        super.setText("html");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new THtml(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTHtml(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change THtml text.");
    }
}