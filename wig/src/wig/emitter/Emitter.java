package wig.emitter;


import java.util.*;

import wig.analysis.DepthFirstAdapter;
import wig.node.*;
import wig.symboltable.*;
import wig.symboltable.symbols.Symbol;

public class Emitter extends DepthFirstAdapter
{
    SymbolTable serviceSymbolTable;
    SymbolTable currentSymbolTable;
    
    public void emit(Node node)
    {
        node.apply(this);
    }   
    public Emitter(SymbolTable symbolTable)
    {
        serviceSymbolTable = symbolTable;
        currentSymbolTable= serviceSymbolTable;
    }
    
    private void puts(String s)
    {
        System.out.print(s);
        System.out.flush();
    }

    public void defaultIn(@SuppressWarnings("unused") Node node)
    {
        // Do nothing
    }

    public void defaultOut(@SuppressWarnings("unused") Node node)
    {
        // Do nothing
    }

    public void inAService(AService node)
    {
        defaultIn(node);
    }
    public void outAService(AService node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAService(AService node)
    {
        inAService(node);
        {
            List<PHtml> copy = new ArrayList<PHtml>(node.getHtml());
            for(PHtml e : copy)
            {
                e.apply(this);
            }
        }
        {
            List<PSchema> copy = new ArrayList<PSchema>(node.getSchema());
            for(PSchema e : copy)
            {
                e.apply(this);
            }
        }
        {
            List<PVariable> copy = new ArrayList<PVariable>(node.getVariable());
            for(PVariable e : copy)
            {
                e.apply(this);
            }
        }
        {
            List<PFunction> copy = new ArrayList<PFunction>(node.getFunction());
            for(PFunction e : copy)
            {
                e.apply(this);
            }
        }
        {
            List<PSession> copy = new ArrayList<PSession>(node.getSession());
            for(PSession e : copy)
            {
                e.apply(this);
            }
        }
        outAService(node);
    }

    public void inAHtml(AHtml node)
    {
        Symbol symbol = SymbolTable.getSymbol(currentSymbolTable, node.getIdentifier().getText());
        currentSymbolTable = SymbolTable.getScopedSymbolTable(symbol);
    }    
    public void outAHtml(AHtml node)
    {
        currentSymbolTable = currentSymbolTable.getNext();
    }    
    @Override
    public void caseAHtml(AHtml node)
    {
        inAHtml(node);
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        {
            List<PHtmlbody> copy = new ArrayList<PHtmlbody>(node.getHtmlbody());
            for(PHtmlbody e : copy)
            {
                e.apply(this);
            }
        }
        outAHtml(node);
    }

    public void inATagStartHtmlbody(ATagStartHtmlbody node)
    {
        defaultIn(node);
    }
    public void outATagStartHtmlbody(ATagStartHtmlbody node)
    {
        defaultOut(node);
    }
    @Override
    public void caseATagStartHtmlbody(ATagStartHtmlbody node)
    {
        inATagStartHtmlbody(node);
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        {
            List<PAttribute> copy = new ArrayList<PAttribute>(node.getAttribute());
            for(PAttribute e : copy)
            {
                e.apply(this);
            }
        }
        outATagStartHtmlbody(node);
    }

    public void inATagEndHtmlbody(ATagEndHtmlbody node)
    {
        defaultIn(node);
    }
    public void outATagEndHtmlbody(ATagEndHtmlbody node)
    {
        defaultOut(node);
    }
    @Override
    public void caseATagEndHtmlbody(ATagEndHtmlbody node)
    {
        inATagEndHtmlbody(node);
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        outATagEndHtmlbody(node);
    }

    public void inAHoleHtmlbody(AHoleHtmlbody node)
    {
        defaultIn(node);
    }
    public void outAHoleHtmlbody(AHoleHtmlbody node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAHoleHtmlbody(AHoleHtmlbody node)
    {
        inAHoleHtmlbody(node);
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        outAHoleHtmlbody(node);
    }

    public void inAWhateverHtmlbody(AWhateverHtmlbody node)
    {
        defaultIn(node);
    }
    public void outAWhateverHtmlbody(AWhateverHtmlbody node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAWhateverHtmlbody(AWhateverHtmlbody node)
    {
        inAWhateverHtmlbody(node);
        if(node.getWhatever() != null)
        {
            node.getWhatever().apply(this);
        }
        outAWhateverHtmlbody(node);
    }

    public void inAMetaHtmlbody(AMetaHtmlbody node)
    {
        defaultIn(node);
    }
    public void outAMetaHtmlbody(AMetaHtmlbody node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAMetaHtmlbody(AMetaHtmlbody node)
    {
        inAMetaHtmlbody(node);
        if(node.getMeta() != null)
        {
            node.getMeta().apply(this);
        }
        outAMetaHtmlbody(node);
    }

    public void inAInputHtmlbody(AInputHtmlbody node)
    {
        defaultIn(node);
    }
    public void outAInputHtmlbody(AInputHtmlbody node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAInputHtmlbody(AInputHtmlbody node)
    {
        inAInputHtmlbody(node);
        if(node.getInput() != null)
        {
            node.getInput().apply(this);
        }
        {
            List<PInputattr> copy = new ArrayList<PInputattr>(node.getInputattr());
            for(PInputattr e : copy)
            {
                e.apply(this);
            }
        }
        outAInputHtmlbody(node);
    }
    
    public void inASelectHtmlbody(ASelectHtmlbody node)
    {
        defaultIn(node);
    }
    public void outASelectHtmlbody(ASelectHtmlbody node)
    {
        defaultOut(node);
    }
    @Override
    public void caseASelectHtmlbody(ASelectHtmlbody node)
    {
        inASelectHtmlbody(node);
        if(node.getSelectTag() != null)
        {
            node.getSelectTag().apply(this);
        }
        {
            List<PInputattr> copy = new ArrayList<PInputattr>(node.getInputattr());
            for(PInputattr e : copy)
            {
                e.apply(this);
            }
        }
        if(node.getFirstGt() != null)
        {
            node.getFirstGt().apply(this);
        }
        {
            List<PHtmlbody> copy = new ArrayList<PHtmlbody>(node.getHtmlbody());
            for(PHtmlbody e : copy)
            {
                e.apply(this);
            }
        }
        outASelectHtmlbody(node);
    }

    public void inANameInputattr(ANameInputattr node)
    {
        defaultIn(node);
    }
    public void outANameInputattr(ANameInputattr node)
    {
        defaultOut(node);
    }
    @Override
    public void caseANameInputattr(ANameInputattr node)
    {
        inANameInputattr(node);
        if(node.getName() != null)
        {
            node.getName().apply(this);
        }
        if(node.getAttr() != null)
        {
            node.getAttr().apply(this);
        }
        outANameInputattr(node);
    }

    public void inATypeInputattr(ATypeInputattr node)
    {
        defaultIn(node);
    }
    public void outATypeInputattr(ATypeInputattr node)
    {
        defaultOut(node);
    }
    @Override
    public void caseATypeInputattr(ATypeInputattr node)
    {
        inATypeInputattr(node);
        if(node.getType() != null)
        {
            node.getType().apply(this);
        }
        if(node.getInputtype() != null)
        {
            node.getInputtype().apply(this);
        }
        outATypeInputattr(node);
    }

    public void inAAttributeInputattr(AAttributeInputattr node)
    {
        defaultIn(node);
    }
    public void outAAttributeInputattr(AAttributeInputattr node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAAttributeInputattr(AAttributeInputattr node)
    {
        inAAttributeInputattr(node);
        if(node.getAttribute() != null)
        {
            node.getAttribute().apply(this);
        }
        outAAttributeInputattr(node);
    }

    public void inATexttypeInputtype(ATexttypeInputtype node)
    {
        defaultIn(node);
    }
    public void outATexttypeInputtype(ATexttypeInputtype node)
    {
        defaultOut(node);
    }
    @Override
    public void caseATexttypeInputtype(ATexttypeInputtype node)
    {
        inATexttypeInputtype(node);
        if(node.getText() != null)
        {
            node.getText().apply(this);
        }
        outATexttypeInputtype(node);
    }

    public void inARadiotypeInputtype(ARadiotypeInputtype node)
    {
        defaultIn(node);
    }
    public void outARadiotypeInputtype(ARadiotypeInputtype node)
    {
        defaultOut(node);
    }
    @Override
    public void caseARadiotypeInputtype(ARadiotypeInputtype node)
    {
        inARadiotypeInputtype(node);
        if(node.getRadio() != null)
        {
            node.getRadio().apply(this);
        }
        outARadiotypeInputtype(node);
    }
    
    public void inAStrtypeInputtype(AStrtypeInputtype node)
    {
        defaultIn(node);
    }
    public void outAStrtypeInputtype(AStrtypeInputtype node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAStrtypeInputtype(AStrtypeInputtype node)
    {
        inAStrtypeInputtype(node);
        if(node.getStringconst() != null)
        {
            node.getStringconst().apply(this);
        }
        outAStrtypeInputtype(node);
    }

    public void inAAttrAttribute(AAttrAttribute node)
    {
        defaultIn(node);
    }
    public void outAAttrAttribute(AAttrAttribute node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAAttrAttribute(AAttrAttribute node)
    {
        inAAttrAttribute(node);
        if(node.getAttr() != null)
        {
            node.getAttr().apply(this);
        }
        outAAttrAttribute(node);
    }

    public void inAAssignAttribute(AAssignAttribute node)
    {
        defaultIn(node);
    }
    public void outAAssignAttribute(AAssignAttribute node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAAssignAttribute(AAssignAttribute node)
    {
        inAAssignAttribute(node);
        if(node.getLeftAttr() != null)
        {
            node.getLeftAttr().apply(this);
        }
        if(node.getRightAttr() != null)
        {
            node.getRightAttr().apply(this);
        }
        outAAssignAttribute(node);
    }

    public void inAIdAttr(AIdAttr node)
    {
        defaultIn(node);
    }
    public void outAIdAttr(AIdAttr node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAIdAttr(AIdAttr node)
    {
        inAIdAttr(node);
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        outAIdAttr(node);
    }

    public void inAStrAttr(AStrAttr node)
    {
        defaultIn(node);
    }
    public void outAStrAttr(AStrAttr node)
    {
        defaultOut(node);
    }
    @Override
    public void caseAStrAttr(AStrAttr node)
    {
        inAStrAttr(node);
        if(node.getStringconst() != null)
        {
            node.getStringconst().apply(this);
        }
        outAStrAttr(node);
    }

    public void inAIconstAttr(AIconstAttr node)
    {
        defaultIn(node);
    }

    public void outAIconstAttr(AIconstAttr node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAIconstAttr(AIconstAttr node)
    {
        inAIconstAttr(node);
        if(node.getIntconst() != null)
        {
            node.getIntconst().apply(this);
        }
        outAIconstAttr(node);
    }

    public void inANegintIntconst(ANegintIntconst node)
    {
        defaultIn(node);
    }

    public void outANegintIntconst(ANegintIntconst node)
    {
        defaultOut(node);
    }

    @Override
    public void caseANegintIntconst(ANegintIntconst node)
    {
        inANegintIntconst(node);
        if(node.getNegIntconst() != null)
        {
            node.getNegIntconst().apply(this);
        }
        outANegintIntconst(node);
    }

    public void inAPosintIntconst(APosintIntconst node)
    {
        defaultIn(node);
    }

    public void outAPosintIntconst(APosintIntconst node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAPosintIntconst(APosintIntconst node)
    {
        inAPosintIntconst(node);
        if(node.getPosIntconst() != null)
        {
            node.getPosIntconst().apply(this);
        }
        outAPosintIntconst(node);
    }

    public void inASchema(ASchema node)
    {
        defaultIn(node);
    }

    public void outASchema(ASchema node)
    {
        defaultOut(node);
    }

    @Override
    public void caseASchema(ASchema node)
    {
        inASchema(node);
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        {
            List<PField> copy = new ArrayList<PField>(node.getField());
            for(PField e : copy)
            {
                e.apply(this);
            }
        }
        outASchema(node);
    }

    public void inAField(AField node)
    {
        defaultIn(node);
    }

    public void outAField(AField node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAField(AField node)
    {
        inAField(node);
        if(node.getType() != null)
        {
            node.getType().apply(this);
        }
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        outAField(node);
    }

    public void inAVariable(AVariable node)
    {
        defaultIn(node);
    }

    public void outAVariable(AVariable node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAVariable(AVariable node)
    {
        inAVariable(node);
        if(node.getType() != null)
        {
            node.getType().apply(this);
        }
        {
            List<TIdentifier> copy = new ArrayList<TIdentifier>(node.getIdentifier());
            for(TIdentifier e : copy)
            {
                e.apply(this);
            }
        }
        outAVariable(node);
    }

    public void inAIdentifiers(AIdentifiers node)
    {
        defaultIn(node);
    }

    public void outAIdentifiers(AIdentifiers node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAIdentifiers(AIdentifiers node)
    {
        inAIdentifiers(node);
        {
            List<TIdentifier> copy = new ArrayList<TIdentifier>(node.getIdentifier());
            for(TIdentifier e : copy)
            {
                e.apply(this);
            }
        }
        outAIdentifiers(node);
    }

    public void inAIntType(AIntType node)
    {
        defaultIn(node);
    }

    public void outAIntType(AIntType node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAIntType(AIntType node)
    {
        inAIntType(node);
        if(node.getInt() != null)
        {
            node.getInt().apply(this);
        }
        outAIntType(node);
    }

    public void inABoolType(ABoolType node)
    {
        defaultIn(node);
    }

    public void outABoolType(ABoolType node)
    {
        defaultOut(node);
    }

    @Override
    public void caseABoolType(ABoolType node)
    {
        inABoolType(node);
        if(node.getBool() != null)
        {
            node.getBool().apply(this);
        }
        outABoolType(node);
    }

    public void inAStringType(AStringType node)
    {
        defaultIn(node);
    }

    public void outAStringType(AStringType node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAStringType(AStringType node)
    {
        inAStringType(node);
        if(node.getString() != null)
        {
            node.getString().apply(this);
        }
        outAStringType(node);
    }

    public void inAVoidType(AVoidType node)
    {
        defaultIn(node);
    }

    public void outAVoidType(AVoidType node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAVoidType(AVoidType node)
    {
        inAVoidType(node);
        if(node.getVoid() != null)
        {
            node.getVoid().apply(this);
        }
        outAVoidType(node);
    }

    public void inASimpleType(ASimpleType node)
    {
        defaultIn(node);
    }

    public void outASimpleType(ASimpleType node)
    {
        defaultOut(node);
    }

    @Override
    public void caseASimpleType(ASimpleType node)
    {
        inASimpleType(node);
        if(node.getType() != null)
        {
            node.getType().apply(this);
        }
        outASimpleType(node);
    }

    public void inATupleType(ATupleType node)
    {
        defaultIn(node);
    }

    public void outATupleType(ATupleType node)
    {
        defaultOut(node);
    }

    @Override
    public void caseATupleType(ATupleType node)
    {
        inATupleType(node);
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        outATupleType(node);
    }

    public void inAFunction(AFunction node)
    {
        Symbol symbol = SymbolTable.getSymbol(currentSymbolTable, node.getIdentifier().getText());
        currentSymbolTable = SymbolTable.getScopedSymbolTable(symbol);
    }
    
    public void outAFunction(AFunction node)
    {
        currentSymbolTable = currentSymbolTable.getNext();
    }

    @Override
    public void caseAFunction(AFunction node)
    {
        inAFunction(node);
        if(node.getType() != null)
        {
            node.getType().apply(this);
        }
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        {
            List<PArgument> copy = new ArrayList<PArgument>(node.getArgument());
            for(PArgument e : copy)
            {
                e.apply(this);
            }
        }
        if(node.getCompoundstm() != null)
        {
            node.getCompoundstm().apply(this);
        }
        outAFunction(node);
    }

    public void inAArguments(AArguments node)
    {
        defaultIn(node);
    }

    public void outAArguments(AArguments node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAArguments(AArguments node)
    {
        inAArguments(node);
        {
            List<PArgument> copy = new ArrayList<PArgument>(node.getArgument());
            for(PArgument e : copy)
            {
                e.apply(this);
            }
        }
        outAArguments(node);
    }

    public void inAArgument(AArgument node)
    {
        defaultIn(node);
    }

    public void outAArgument(AArgument node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAArgument(AArgument node)
    {
        inAArgument(node);
        if(node.getType() != null)
        {
            node.getType().apply(this);
        }
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        outAArgument(node);
    }

    public void inASession(ASession node)
    {
        Symbol symbol = SymbolTable.getSymbol(currentSymbolTable, node.getIdentifier().getText());
        currentSymbolTable = SymbolTable.getScopedSymbolTable(symbol);
    }

    public void outASession(ASession node)
    {
        currentSymbolTable = currentSymbolTable.getNext();
    }

    @Override
    public void caseASession(ASession node)
    {
        inASession(node);
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        if(node.getCompoundstm() != null)
        {
            node.getCompoundstm().apply(this);
        }
        outASession(node);
    }

    public void inAEmptyStm(AEmptyStm node)
    {
        defaultIn(node);
    }

    public void outAEmptyStm(AEmptyStm node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAEmptyStm(AEmptyStm node)
    {
        inAEmptyStm(node);
        outAEmptyStm(node);
    }

    public void inAShowStm(AShowStm node)
    {
        defaultIn(node);
    }

    public void outAShowStm(AShowStm node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAShowStm(AShowStm node)
    {
        inAShowStm(node);
        if(node.getDocument() != null)
        {
            node.getDocument().apply(this);
        }
        if(node.getReceive() != null)
        {
            node.getReceive().apply(this);
        }
        outAShowStm(node);
    }

    public void inAExitStm(AExitStm node)
    {
        defaultIn(node);
    }

    public void outAExitStm(AExitStm node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAExitStm(AExitStm node)
    {
        inAExitStm(node);
        if(node.getDocument() != null)
        {
            node.getDocument().apply(this);
        }
        outAExitStm(node);
    }

    public void inAReturnStm(AReturnStm node)
    {
        defaultIn(node);
    }

    public void outAReturnStm(AReturnStm node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAReturnStm(AReturnStm node)
    {
        inAReturnStm(node);
        outAReturnStm(node);
    }

    public void inAReturnexpStm(AReturnexpStm node)
    {
        defaultIn(node);
    }

    public void outAReturnexpStm(AReturnexpStm node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAReturnexpStm(AReturnexpStm node)
    {
        inAReturnexpStm(node);
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
        }
        outAReturnexpStm(node);
    }

    public void inAIfStm(AIfStm node)
    {
        defaultIn(node);
    }

    public void outAIfStm(AIfStm node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAIfStm(AIfStm node)
    {
        inAIfStm(node);
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
        }
        if(node.getStm() != null)
        {
            node.getStm().apply(this);
        }
        outAIfStm(node);
    }

    public void inAIfelseStm(AIfelseStm node)
    {
        defaultIn(node);
    }

    public void outAIfelseStm(AIfelseStm node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAIfelseStm(AIfelseStm node)
    {
        inAIfelseStm(node);
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
        }
        if(node.getThenStm() != null)
        {
            node.getThenStm().apply(this);
        }
        if(node.getElseStm() != null)
        {
            node.getElseStm().apply(this);
        }
        outAIfelseStm(node);
    }

    public void inAWhileStm(AWhileStm node)
    {
        defaultIn(node);
    }

    public void outAWhileStm(AWhileStm node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAWhileStm(AWhileStm node)
    {
        inAWhileStm(node);
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
        }
        if(node.getStm() != null)
        {
            node.getStm().apply(this);
        }
        outAWhileStm(node);
    }

    public void inACompStm(ACompStm node)
    {
        defaultIn(node);
    }

    public void outACompStm(ACompStm node)
    {
        defaultOut(node);
    }

    @Override
    public void caseACompStm(ACompStm node)
    {
        inACompStm(node);
        if(node.getCompoundstm() != null)
        {
            node.getCompoundstm().apply(this);
        }
        outACompStm(node);
    }

    public void inAExpStm(AExpStm node)
    {
        defaultIn(node);
    }

    public void outAExpStm(AExpStm node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAExpStm(AExpStm node)
    {
        inAExpStm(node);
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
        }
        outAExpStm(node);
    }

    public void inAIdDocument(AIdDocument node)
    {
        defaultIn(node);
    }

    public void outAIdDocument(AIdDocument node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAIdDocument(AIdDocument node)
    {
        inAIdDocument(node);
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        outAIdDocument(node);
    }

    public void inAPlugDocument(APlugDocument node)
    {
        defaultIn(node);
    }

    public void outAPlugDocument(APlugDocument node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAPlugDocument(APlugDocument node)
    {
        inAPlugDocument(node);
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        {
            List<PPlug> copy = new ArrayList<PPlug>(node.getPlug());
            for(PPlug e : copy)
            {
                e.apply(this);
            }
        }
        outAPlugDocument(node);
    }

    public void inAReceive(AReceive node)
    {
        defaultIn(node);
    }

    public void outAReceive(AReceive node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAReceive(AReceive node)
    {
        inAReceive(node);
        {
            List<PInput> copy = new ArrayList<PInput>(node.getInput());
            for(PInput e : copy)
            {
                e.apply(this);
            }
        }
        outAReceive(node);
    }

    public void inACompoundStm(ACompoundstm node)
    {
        if(! (node.parent() instanceof AFunction || node.parent() instanceof ASession) )
        {
            currentSymbolTable = currentSymbolTable.getCompoundStatementSymbolTable(node);
        }
    }
    
    public void outACompoundStm(ACompoundstm node)
    {
        if(! (node.parent() instanceof AFunction || node.parent() instanceof ASession) )
        {
            currentSymbolTable = currentSymbolTable.getNext();
        }
    }

    @Override
    public void caseACompoundstm(ACompoundstm node)
    {
        inACompoundstm(node);
        {
            List<PVariable> copy = new ArrayList<PVariable>(node.getVariable());
            for(PVariable e : copy)
            {
                e.apply(this);
            }
        }
        {
            List<PStm> copy = new ArrayList<PStm>(node.getStm());
            for(PStm e : copy)
            {
                e.apply(this);
            }
        }
        outACompoundstm(node);
    }

    public void inAPlugs(APlugs node)
    {
        defaultIn(node);
    }

    public void outAPlugs(APlugs node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAPlugs(APlugs node)
    {
        inAPlugs(node);
        {
            List<PPlug> copy = new ArrayList<PPlug>(node.getPlug());
            for(PPlug e : copy)
            {
                e.apply(this);
            }
        }
        outAPlugs(node);
    }

    public void inAPlug(APlug node)
    {
        defaultIn(node);
    }

    public void outAPlug(APlug node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAPlug(APlug node)
    {
        inAPlug(node);
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
        }
        outAPlug(node);
    }

    public void inAInputs(AInputs node)
    {
        defaultIn(node);
    }

    public void outAInputs(AInputs node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAInputs(AInputs node)
    {
        inAInputs(node);
        {
            List<PInput> copy = new ArrayList<PInput>(node.getInput());
            for(PInput e : copy)
            {
                e.apply(this);
            }
        }
        outAInputs(node);
    }

    public void inAInput(AInput node)
    {
        defaultIn(node);
    }

    public void outAInput(AInput node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAInput(AInput node)
    {
        inAInput(node);
        if(node.getLvalue() != null)
        {
            node.getLvalue().apply(this);
        }
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        outAInput(node);
    }

    public void inAAssignExp(AAssignExp node)
    {
        defaultIn(node);
    }

    public void outAAssignExp(AAssignExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAAssignExp(AAssignExp node)
    {
        inAAssignExp(node);
        if(node.getLvalue() != null)
        {
            node.getLvalue().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAAssignExp(node);
    }

    public void inAOrExp(AOrExp node)
    {
        defaultIn(node);
    }

    public void outAOrExp(AOrExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAOrExp(AOrExp node)
    {
        inAOrExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAOrExp(node);
    }

    public void inAAndExp(AAndExp node)
    {
        defaultIn(node);
    }

    public void outAAndExp(AAndExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAAndExp(AAndExp node)
    {
        inAAndExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAAndExp(node);
    }

    public void inAEqExp(AEqExp node)
    {
        defaultIn(node);
    }

    public void outAEqExp(AEqExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAEqExp(AEqExp node)
    {
        inAEqExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAEqExp(node);
    }

    public void inANeqExp(ANeqExp node)
    {
        defaultIn(node);
    }

    public void outANeqExp(ANeqExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseANeqExp(ANeqExp node)
    {
        inANeqExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outANeqExp(node);
    }

    public void inALtExp(ALtExp node)
    {
        defaultIn(node);
    }

    public void outALtExp(ALtExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseALtExp(ALtExp node)
    {
        inALtExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outALtExp(node);
    }

    public void inAGtExp(AGtExp node)
    {
        defaultIn(node);
    }

    public void outAGtExp(AGtExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAGtExp(AGtExp node)
    {
        inAGtExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAGtExp(node);
    }

    public void inALteqExp(ALteqExp node)
    {
        defaultIn(node);
    }

    public void outALteqExp(ALteqExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseALteqExp(ALteqExp node)
    {
        inALteqExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outALteqExp(node);
    }

    public void inAGteqExp(AGteqExp node)
    {
        defaultIn(node);
    }

    public void outAGteqExp(AGteqExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAGteqExp(AGteqExp node)
    {
        inAGteqExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAGteqExp(node);
    }

    public void inAPlusExp(APlusExp node)
    {
        defaultIn(node);
    }

    public void outAPlusExp(APlusExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAPlusExp(APlusExp node)
    {
        inAPlusExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAPlusExp(node);
    }

    public void inAMinusExp(AMinusExp node)
    {
        defaultIn(node);
    }

    public void outAMinusExp(AMinusExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAMinusExp(AMinusExp node)
    {
        inAMinusExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        puts("-");
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAMinusExp(node);
    }

    public void inAMultExp(AMultExp node)
    {
        defaultIn(node);
    }

    public void outAMultExp(AMultExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAMultExp(AMultExp node)
    {
        inAMultExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAMultExp(node);
    }

    public void inADivExp(ADivExp node)
    {
        defaultIn(node);
    }

    public void outADivExp(ADivExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseADivExp(ADivExp node)
    {
        inADivExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outADivExp(node);
    }

    public void inAModExp(AModExp node)
    {
        defaultIn(node);
    }

    public void outAModExp(AModExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAModExp(AModExp node)
    {
        inAModExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAModExp(node);
    }

    public void inAJoinExp(AJoinExp node)
    {
        defaultIn(node);
    }

    public void outAJoinExp(AJoinExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAJoinExp(AJoinExp node)
    {
        inAJoinExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAJoinExp(node);
    }

    public void inAKeepExp(AKeepExp node)
    {
        defaultIn(node);
    }

    public void outAKeepExp(AKeepExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAKeepExp(AKeepExp node)
    {
        inAKeepExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        outAKeepExp(node);
    }

    public void inARemoveExp(ARemoveExp node)
    {
        defaultIn(node);
    }

    public void outARemoveExp(ARemoveExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseARemoveExp(ARemoveExp node)
    {
        inARemoveExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        outARemoveExp(node);
    }

    public void inAKeepManyExp(AKeepManyExp node)
    {
        defaultIn(node);
    }

    public void outAKeepManyExp(AKeepManyExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAKeepManyExp(AKeepManyExp node)
    {
        inAKeepManyExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        {
            List<TIdentifier> copy = new ArrayList<TIdentifier>(node.getIdentifier());
            for(TIdentifier e : copy)
            {
                e.apply(this);
            }
        }
        outAKeepManyExp(node);
    }

    public void inARemoveManyExp(ARemoveManyExp node)
    {
        defaultIn(node);
    }

    public void outARemoveManyExp(ARemoveManyExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseARemoveManyExp(ARemoveManyExp node)
    {
        inARemoveManyExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        {
            List<TIdentifier> copy = new ArrayList<TIdentifier>(node.getIdentifier());
            for(TIdentifier e : copy)
            {
                e.apply(this);
            }
        }
        outARemoveManyExp(node);
    }

    public void inANotExp(ANotExp node)
    {
        defaultIn(node);
    }

    public void outANotExp(ANotExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseANotExp(ANotExp node)
    {
        inANotExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        outANotExp(node);
    }

    public void inANegExp(ANegExp node)
    {
        defaultIn(node);
    }

    public void outANegExp(ANegExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseANegExp(ANegExp node)
    {
        inANegExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        outANegExp(node);
    }

    public void inADefaultExp(ADefaultExp node)
    {
        defaultIn(node);
    }

    public void outADefaultExp(ADefaultExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseADefaultExp(ADefaultExp node)
    {
        inADefaultExp(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        outADefaultExp(node);
    }

    public void inALvalueExp(ALvalueExp node)
    {
        defaultIn(node);
    }

    public void outALvalueExp(ALvalueExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseALvalueExp(ALvalueExp node)
    {
        inALvalueExp(node);
        if(node.getLvalue() != null)
        {
            node.getLvalue().apply(this);
        }
        outALvalueExp(node);
    }

    public void inACallExp(ACallExp node)
    {
        defaultIn(node);
    }

    public void outACallExp(ACallExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseACallExp(ACallExp node)
    {
        inACallExp(node);
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        {
            List<PExp> copy = new ArrayList<PExp>(node.getExp());
            for(PExp e : copy)
            {
                e.apply(this);
            }
        }
        outACallExp(node);
    }

    public void inAIntExp(AIntExp node)
    {
        defaultIn(node);
    }

    public void outAIntExp(AIntExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAIntExp(AIntExp node)
    {
        inAIntExp(node);
        if(node.getIntconst() != null)
        {
            node.getIntconst().apply(this);
        }
        outAIntExp(node);
    }

    public void inATrueExp(ATrueExp node)
    {
        defaultIn(node);
    }

    public void outATrueExp(ATrueExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseATrueExp(ATrueExp node)
    {
        inATrueExp(node);
        if(node.getTrue() != null)
        {
            node.getTrue().apply(this);
        }
        outATrueExp(node);
    }

    public void inAFalseExp(AFalseExp node)
    {
        defaultIn(node);
    }

    public void outAFalseExp(AFalseExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAFalseExp(AFalseExp node)
    {
        inAFalseExp(node);
        if(node.getFalse() != null)
        {
            node.getFalse().apply(this);
        }
        outAFalseExp(node);
    }

    public void inAStringExp(AStringExp node)
    {
        defaultIn(node);
    }

    public void outAStringExp(AStringExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAStringExp(AStringExp node)
    {
        inAStringExp(node);
        if(node.getStringconst() != null)
        {
            node.getStringconst().apply(this);
        }
        outAStringExp(node);
    }

    public void inATupleExp(ATupleExp node)
    {
        defaultIn(node);
    }

    public void outATupleExp(ATupleExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseATupleExp(ATupleExp node)
    {
        inATupleExp(node);
        {
            List<PFieldvalue> copy = new ArrayList<PFieldvalue>(node.getFieldvalue());
            for(PFieldvalue e : copy)
            {
                e.apply(this);
            }
        }
        outATupleExp(node);
    }

    public void inAParenExp(AParenExp node)
    {
        defaultIn(node);
    }

    public void outAParenExp(AParenExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAParenExp(AParenExp node)
    {
        inAParenExp(node);
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
        }
        outAParenExp(node);
    }

    public void inAExps(AExps node)
    {
        defaultIn(node);
    }

    public void outAExps(AExps node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAExps(AExps node)
    {
        inAExps(node);
        {
            List<PExp> copy = new ArrayList<PExp>(node.getExp());
            for(PExp e : copy)
            {
                e.apply(this);
            }
        }
        outAExps(node);
    }

    public void inASimpleLvalue(ASimpleLvalue node)
    {
        defaultIn(node);
    }

    public void outASimpleLvalue(ASimpleLvalue node)
    {
        defaultOut(node);
    }

    @Override
    public void caseASimpleLvalue(ASimpleLvalue node)
    {
        inASimpleLvalue(node);
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        outASimpleLvalue(node);
    }

    public void inAQualifiedLvalue(AQualifiedLvalue node)
    {
        defaultIn(node);
    }

    public void outAQualifiedLvalue(AQualifiedLvalue node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAQualifiedLvalue(AQualifiedLvalue node)
    {
        inAQualifiedLvalue(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAQualifiedLvalue(node);
    }

    public void inAFieldvalues(AFieldvalues node)
    {
        defaultIn(node);
    }

    public void outAFieldvalues(AFieldvalues node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAFieldvalues(AFieldvalues node)
    {
        inAFieldvalues(node);
        {
            List<PFieldvalue> copy = new ArrayList<PFieldvalue>(node.getFieldvalue());
            for(PFieldvalue e : copy)
            {
                e.apply(this);
            }
        }
        outAFieldvalues(node);
    }

    public void inAFieldvalue(AFieldvalue node)
    {
        defaultIn(node);
    }

    public void outAFieldvalue(AFieldvalue node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAFieldvalue(AFieldvalue node)
    {
        inAFieldvalue(node);
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
        }
        outAFieldvalue(node);
    }


    public void caseTService(TService node)
    {
        
    }
    public void caseTConst(TConst node)
    {
        
    }
    public void caseTHtml(THtml node)
    {
        
    }
    public void caseTHtmlTagStart(THtmlTagStart node)
    {
        
    }
    public void caseTSchema(TSchema node)
    {
        
    }
    public void caseTSession(TSession node)
    {
        
    }
    public void caseTShow(TShow node)
    {
        
    }
    public void caseTExit(TExit node)
    {
        
    }
    public void caseTReturn(TReturn node)
    {
        
    }
    public void caseTIf(TIf node)
    {
        
    }
    public void caseTElse(TElse node)
    {
        
    }
    public void caseTWhile(TWhile node)
    {
        
    }
    public void caseTPlug(TPlug node)
    {
        
    }
    public void caseTReceive(TReceive node)
    {
        
    }
    public void caseTInt(TInt node)
    {
        puts(node.getText());
    }
    public void caseTBool(TBool node)
    {
        puts(node.getText());
    }
    public void caseTString(TString node)
    {
        puts(node.getText());
    }
    public void caseTVoid(TVoid node)
    {
        
    }
    public void caseTTuple(TTuple node)
    {
        
    }
    public void caseTTrue(TTrue node)
    {
        
    }
    public void caseTFalse(TFalse node)
    {
        
    }
    public void caseTMeta(TMeta node)
    {
        
    }
    public void caseTHtmlTagEnd(THtmlTagEnd node)
    {
        
    }
    public void caseTInput(TInput node)
    {
        
    }
    public void caseTPosIntconst(TPosIntconst node)
    {
        
    }
    public void caseTNegIntconst(TNegIntconst node)
    {
        
    }
    public void caseTSelect(TSelect node)
    {
        
    }
    public void caseTType(TType node)
    {
        
    }
    public void caseTName(TName node)
    {
        
    }
    public void caseTText(TText node)
    {
        
    }
    public void caseTRadio(TRadio node)
    {
        
    }
    public void caseTLBrace(TLBrace node)
    {
        
    }
    public void caseTRBrace(TRBrace node)
    {
        
    }
    public void caseTAssign(TAssign node)
    {
        
    }
    public void caseTSemicolon(TSemicolon node)
    {
        
    }
    public void caseTLt(TLt node)
    {
        
    }
    public void caseTGt(TGt node)
    {
        
    }
    public void caseTLtSlash(TLtSlash node)
    {
        
    }
    public void caseTLtBracket(TLtBracket node)
    {
        
    }
    public void caseTGtBracket(TGtBracket node)
    {
        
    }
    public void caseTComment(TComment node)
    {
        
    }
    public void caseTLPar(TLPar node)
    {
        
    }
    public void caseTRPar(TRPar node)
    {
        
    }
    public void caseTLBracket(TLBracket node)
    {
        
    }
    public void caseTRBracket(TRBracket node)
    {
        
    }
    public void caseTComma(TComma node)
    {
        
    }
    public void caseTKeep(TKeep node)
    {
        
    }
    public void caseTRemove(TRemove node)
    {
        
    }
    public void caseTJoin(TJoin node)
    {
        
    }
    public void caseTEq(TEq node)
    {
        puts("==");
    }
    public void caseTNeq(TNeq node)
    {
        puts("!=");   
    }
    public void caseTLteq(TLteq node)
    {
        puts("<=");
    }
    public void caseTGteq(TGteq node)
    {
        puts(">=");
    }
    public void caseTNot(TNot node)
    {
        puts("!");
    }
    
    public void caseTMinus(TMinus node)
    {
        puts("-");
    }
    public void caseTPlus(TPlus node)
    {
        puts("+");
    }
    public void caseTMult(TMult node)
    {
        puts("*");
    }
    public void caseTDiv(TDiv node)
    {
        puts("/");
    }
    public void caseTMod(TMod node)
    {
        puts("%");
    }
    public void caseTAnd(TAnd node)
    {
        puts("&&");
    }
    public void caseTOr(TOr node)
    {
        puts("||");
    }
    public void caseTDot(TDot node){}
    public void caseTEol(TEol node)
    {
        puts(node.getText());
    }
    public void caseTBlank(TBlank node)
    {
        puts(node.getText());
    }
    public void caseTIdentifier(TIdentifier node)
    {
        puts("$"+node.getText());
    }
    public void caseTStringconst(TStringconst node)
    {
        puts(node.getText());
    }
    public void caseTWhatever(TWhatever node)
    {
        puts(node.getText());
    }
    public void caseEOF(EOF node)
    {
        puts(node.getText());
    }
    
}
