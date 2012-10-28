package wig.symboltable;

import wig.node.AArgument;
import wig.node.AFunction;
import wig.node.AHoleHtmlbody;
import wig.node.AHtml;
import wig.node.AInputHtmlbody;
import wig.node.AMetaHtmlbody;
import wig.node.ASchema;
import wig.node.ASelectHtmlbody;
import wig.node.ASession;
import wig.node.AVariable;
import wig.node.AWhateverHtmlbody;

public class Symbol
{
    private String fName;
    private SymbolKind fKind;
    
    private AHtml fHtml;
    private AInputHtmlbody fInput;
    private ASelectHtmlbody fSelect;
    private AWhateverHtmlbody fWhatever;
    private AMetaHtmlbody fMeta;
    private AHoleHtmlbody fHole;
    private ASchema fSchema;
    private AVariable fVariable;
    private AFunction fFunction;
    private AArgument fArgument;
    private ASession fSession;
    private AVariable fLocal;   
    
    private Symbol fNext;
    
    public Symbol(){};
    
    public String getName()
    {
        return fName;
    }
    
    public SymbolKind getKind()
    {
        return fKind;
    }
    
    public AHtml getHtml()
    {
        return fHtml;
    }
    
    public AInputHtmlbody getInput()
    {
        return fInput;
    }
    
    public ASelectHtmlbody getSelect()
    {
        return fSelect;
    }
    
    public AWhateverHtmlbody getWhatever()
    {
        return fWhatever;
    }
    
    public AMetaHtmlbody getMeta()
    {
        return fMeta;
    }
    
    public AHoleHtmlbody getHole()
    {
        return fHole;
    }
    
    public ASchema getSchema()
    {
        return fSchema;
    }
    
    public AVariable getVariable()
    {
        return fVariable;
    }
    
    public AFunction getFunction()
    {
        return fFunction;
    }
    
    public AArgument getArgument()
    {
        return fArgument;
    }
    
    public ASession getSession()
    {
        return fSession;
    }
    
    public AVariable getLocal()
    {
        return fLocal;
    }
    
    public Symbol getNext()
    {
        return fNext;
    }
    
    public void setName(String name)
    {
       fName = name;
    }
    
    public void setKind(SymbolKind kind)
    {
        fKind = kind;
    }
    
    public void setHtml(AHtml html) 
    {
        fHtml = html;
    }
    
    public void setInput(AInputHtmlbody input)
    {
        fInput = input;
    }
    
    public void setSelect(ASelectHtmlbody select)
    {
       fSelect = select;
    }
    
    public void setWhatever(AWhateverHtmlbody whatever)
    {
        fWhatever = whatever;
    }
    
    public void setMeta(AMetaHtmlbody meta)
    {
        fMeta = meta;
    }
    
    public void setHole(AHoleHtmlbody hole)
    {
        fHole = hole;
    }
    
    public void setSchema(ASchema schema)
    {
        fSchema = schema;
    }
    
    public void setVariable(AVariable variable)
    {
        fVariable = variable;
    }
    
    public void setFunction(AFunction function)
    {
        fFunction = function;
    }
    
    public void setArgument(AArgument argument)
    {
        fArgument = argument;
    }
    
    public void setSession(ASession session)
    {
        fSession = session;
    }
    
    public void setLocal(AVariable local)
    {
         fLocal = local;
    }
    
    public void setNext(Symbol next)
    {
        fNext = next;
    }
}
