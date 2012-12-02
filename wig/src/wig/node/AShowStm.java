/* This file was generated by SableCC (http://www.sablecc.org/). */

package wig.node;

import wig.analysis.*;

@SuppressWarnings("nls")
public final class AShowStm extends PStm
{
    private PDocument _document_;
    private PReceive _receive_;

    public AShowStm()
    {
        // Constructor
    }

    public AShowStm(
        @SuppressWarnings("hiding") PDocument _document_,
        @SuppressWarnings("hiding") PReceive _receive_)
    {
        // Constructor
        setDocument(_document_);

        setReceive(_receive_);

    }

    @Override
    public Object clone()
    {
        return new AShowStm(
            cloneNode(this._document_),
            cloneNode(this._receive_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAShowStm(this);
    }

    public PDocument getDocument()
    {
        return this._document_;
    }

    public void setDocument(PDocument node)
    {
        if(this._document_ != null)
        {
            this._document_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._document_ = node;
    }

    public PReceive getReceive()
    {
        return this._receive_;
    }

    public void setReceive(PReceive node)
    {
        if(this._receive_ != null)
        {
            this._receive_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._receive_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._document_)
            + toString(this._receive_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._document_ == child)
        {
            this._document_ = null;
            return;
        }

        if(this._receive_ == child)
        {
            this._receive_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._document_ == oldChild)
        {
            setDocument((PDocument) newChild);
            return;
        }

        if(this._receive_ == oldChild)
        {
            setReceive((PReceive) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}