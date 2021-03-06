/* This file was generated by SableCC (http://www.sablecc.org/). */

package wig.node;

import java.util.*;
import wig.analysis.*;

@SuppressWarnings("nls")
public final class ASchema extends PSchema
{
    private TIdentifier _identifier_;
    private final LinkedList<PField> _field_ = new LinkedList<PField>();

    public ASchema()
    {
        // Constructor
    }

    public ASchema(
        @SuppressWarnings("hiding") TIdentifier _identifier_,
        @SuppressWarnings("hiding") List<?> _field_)
    {
        // Constructor
        setIdentifier(_identifier_);

        setField(_field_);

    }

    @Override
    public Object clone()
    {
        return new ASchema(
            cloneNode(this._identifier_),
            cloneList(this._field_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseASchema(this);
    }

    public TIdentifier getIdentifier()
    {
        return this._identifier_;
    }

    public void setIdentifier(TIdentifier node)
    {
        if(this._identifier_ != null)
        {
            this._identifier_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._identifier_ = node;
    }

    public LinkedList<PField> getField()
    {
        return this._field_;
    }

    public void setField(List<?> list)
    {
        for(PField e : this._field_)
        {
            e.parent(null);
        }
        this._field_.clear();

        for(Object obj_e : list)
        {
            PField e = (PField) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._field_.add(e);
        }
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._identifier_)
            + toString(this._field_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._identifier_ == child)
        {
            this._identifier_ = null;
            return;
        }

        if(this._field_.remove(child))
        {
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._identifier_ == oldChild)
        {
            setIdentifier((TIdentifier) newChild);
            return;
        }

        for(ListIterator<PField> i = this._field_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PField) newChild);
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
