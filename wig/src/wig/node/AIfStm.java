/* This file was generated by SableCC (http://www.sablecc.org/). */

package wig.node;

import wig.analysis.*;

@SuppressWarnings("nls")
public final class AIfStm extends PStm
{
    private PExp _exp_;
    private PStm _stm_;

    public AIfStm()
    {
        // Constructor
    }

    public AIfStm(
        @SuppressWarnings("hiding") PExp _exp_,
        @SuppressWarnings("hiding") PStm _stm_)
    {
        // Constructor
        setExp(_exp_);

        setStm(_stm_);

    }

    @Override
    public Object clone()
    {
        return new AIfStm(
            cloneNode(this._exp_),
            cloneNode(this._stm_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAIfStm(this);
    }

    public PExp getExp()
    {
        return this._exp_;
    }

    public void setExp(PExp node)
    {
        if(this._exp_ != null)
        {
            this._exp_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._exp_ = node;
    }

    public PStm getStm()
    {
        return this._stm_;
    }

    public void setStm(PStm node)
    {
        if(this._stm_ != null)
        {
            this._stm_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._stm_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._exp_)
            + toString(this._stm_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._exp_ == child)
        {
            this._exp_ = null;
            return;
        }

        if(this._stm_ == child)
        {
            this._stm_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._exp_ == oldChild)
        {
            setExp((PExp) newChild);
            return;
        }

        if(this._stm_ == oldChild)
        {
            setStm((PStm) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
