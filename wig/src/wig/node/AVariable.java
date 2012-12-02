/* This file was generated by SableCC (http://www.sablecc.org/). */

package wig.node;

import java.util.*;
import wig.analysis.*;

@SuppressWarnings("nls")
public final class AVariable extends PVariable
{
    private PType _type_;
    private final LinkedList<TIdentifier> _identifier_ = new LinkedList<TIdentifier>();

    public AVariable()
    {
        // Constructor
    }

    public AVariable(
        @SuppressWarnings("hiding") PType _type_,
        @SuppressWarnings("hiding") List<?> _identifier_)
    {
        // Constructor
        setType(_type_);

        setIdentifier(_identifier_);

    }

    @Override
    public Object clone()
    {
        return new AVariable(
            cloneNode(this._type_),
            cloneList(this._identifier_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAVariable(this);
    }

    public PType getType()
    {
        return this._type_;
    }

    public void setType(PType node)
    {
        if(this._type_ != null)
        {
            this._type_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._type_ = node;
    }

    public LinkedList<TIdentifier> getIdentifier()
    {
        return this._identifier_;
    }

    public void setIdentifier(List<?> list)
    {
        for(TIdentifier e : this._identifier_)
        {
            e.parent(null);
        }
        this._identifier_.clear();

        for(Object obj_e : list)
        {
            TIdentifier e = (TIdentifier) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._identifier_.add(e);
        }
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._type_)
            + toString(this._identifier_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._type_ == child)
        {
            this._type_ = null;
            return;
        }

        if(this._identifier_.remove(child))
        {
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._type_ == oldChild)
        {
            setType((PType) newChild);
            return;
        }

        for(ListIterator<TIdentifier> i = this._identifier_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((TIdentifier) newChild);
                    newChild.parent(this);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        throw new RuntimeException("Not a child.");
    }
}