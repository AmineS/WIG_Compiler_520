/* This file was generated by SableCC (http://www.sablecc.org/). */

package wig.node;

import wig.analysis.*;

@SuppressWarnings("nls")
public final class AAssignAttribute extends PAttribute
{
    private PAttr _leftAttr_;
    private PAttr _rightAttr_;

    public AAssignAttribute()
    {
        // Constructor
    }

    public AAssignAttribute(
        @SuppressWarnings("hiding") PAttr _leftAttr_,
        @SuppressWarnings("hiding") PAttr _rightAttr_)
    {
        // Constructor
        setLeftAttr(_leftAttr_);

        setRightAttr(_rightAttr_);

    }

    @Override
    public Object clone()
    {
        return new AAssignAttribute(
            cloneNode(this._leftAttr_),
            cloneNode(this._rightAttr_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAAssignAttribute(this);
    }

    public PAttr getLeftAttr()
    {
        return this._leftAttr_;
    }

    public void setLeftAttr(PAttr node)
    {
        if(this._leftAttr_ != null)
        {
            this._leftAttr_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._leftAttr_ = node;
    }

    public PAttr getRightAttr()
    {
        return this._rightAttr_;
    }

    public void setRightAttr(PAttr node)
    {
        if(this._rightAttr_ != null)
        {
            this._rightAttr_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._rightAttr_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._leftAttr_)
            + toString(this._rightAttr_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._leftAttr_ == child)
        {
            this._leftAttr_ = null;
            return;
        }

        if(this._rightAttr_ == child)
        {
            this._rightAttr_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._leftAttr_ == oldChild)
        {
            setLeftAttr((PAttr) newChild);
            return;
        }

        if(this._rightAttr_ == oldChild)
        {
            setRightAttr((PAttr) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}