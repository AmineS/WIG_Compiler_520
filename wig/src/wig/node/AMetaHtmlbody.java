/* This file was generated by SableCC (http://www.sablecc.org/). */

package wig.node;

import wig.analysis.*;

@SuppressWarnings("nls")
public final class AMetaHtmlbody extends PHtmlbody
{
    private TMeta _meta_;

    public AMetaHtmlbody()
    {
        // Constructor
    }

    public AMetaHtmlbody(
        @SuppressWarnings("hiding") TMeta _meta_)
    {
        // Constructor
        setMeta(_meta_);

    }

    @Override
    public Object clone()
    {
        return new AMetaHtmlbody(
            cloneNode(this._meta_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAMetaHtmlbody(this);
    }

    public TMeta getMeta()
    {
        return this._meta_;
    }

    public void setMeta(TMeta node)
    {
        if(this._meta_ != null)
        {
            this._meta_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._meta_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._meta_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._meta_ == child)
        {
            this._meta_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._meta_ == oldChild)
        {
            setMeta((TMeta) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}