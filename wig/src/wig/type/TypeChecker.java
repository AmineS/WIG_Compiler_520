package wig.type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import wig.analysis.DepthFirstAdapter;
import wig.node.*;
import wig.symboltable.SymbolTable;
import wig.symboltable.TupleSymbolTable;
import wig.symboltable.symbols.SArgument;
import wig.symboltable.symbols.SField;
import wig.symboltable.symbols.SFunction;
import wig.symboltable.symbols.SVariable;
import wig.symboltable.symbols.Symbol;

/**
 * Performs Type Checking
 * @author group-h
 */
public class TypeChecker extends DepthFirstAdapter
{
    private TypeTable fTypeTable;
    private SymbolTable fServiceSymbolTable;
    private SymbolTable fCurrentSymbolTable;
    
    public void typeCheck(Node node)
    {
        node.apply(this);
    }
    
    public TypeChecker(SymbolTable symbolTable)
    {
        fServiceSymbolTable = symbolTable;
        fCurrentSymbolTable= fServiceSymbolTable;
        fTypeTable = new TypeTable();
    }
    
    public TypeTable getTypeTable()
    {
        return fTypeTable;
    }
    
    public void inACompoundStm(ACompoundstm node)
    {
        if(! (node.parent() instanceof AFunction || node.parent() instanceof ASession) )
        {
            fCurrentSymbolTable = fCurrentSymbolTable.getCompoundStatementSymbolTable(node);
        }
    }
    
    public void caseACompoundstm(ACompoundstm node)
    {        
        inACompoundStm(node); 
        
        LinkedList<PStm> stm_list = node.getStm();
        LinkedList<PVariable> var_list = node.getVariable();
        Iterator<PStm> stm_iter = stm_list.iterator();
        Iterator<PVariable> var_iter = var_list.iterator();          
        
        while(var_iter.hasNext())
        {
            var_iter.next().apply(this);
        }
        
        while(stm_iter.hasNext())
        {
            stm_iter.next().apply(this);
        }        
        
        outACompoundStm(node);
    }
    
    public void outACompoundStm(ACompoundstm node)
    {
        if(! (node.parent() instanceof AFunction || node.parent() instanceof ASession) )
        {
            fCurrentSymbolTable = fCurrentSymbolTable.getNext();
        }
    }

    
    public void inStart(Start node)
    {
        defaultIn(node);
    }

    public void outStart(Start node)
    {
        defaultOut(node);
    }

    public void defaultIn(@SuppressWarnings("unused") Node node)
    {
        // Do nothing
    }

    public void defaultOut(@SuppressWarnings("unused") Node node)
    {
        // Do nothing
    }

    @Override
    public void caseStart(Start node)
    {
        inStart(node);
        node.getPService().apply(this);
        node.getEOF().apply(this);
        outStart(node);
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
        Symbol symbol = SymbolTable.lookupHierarchy(fCurrentSymbolTable, node.getIdentifier().getText());
        fCurrentSymbolTable = SymbolTable.getScopedSymbolTable(symbol);
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
    
    public void outAHtml(AHtml node)
    {
        fCurrentSymbolTable = fCurrentSymbolTable.getNext();
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
        Symbol symbol = SymbolTable.lookupHierarchy(fCurrentSymbolTable, node.getIdentifier().getText());
        fCurrentSymbolTable = SymbolTable.getScopedSymbolTable(symbol);
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
    
    public void outAFunction(AFunction node)
    {
        fCurrentSymbolTable = fCurrentSymbolTable.getNext();
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
        Symbol symbol = SymbolTable.lookupHierarchy(fCurrentSymbolTable, node.getIdentifier().getText());
        fCurrentSymbolTable = SymbolTable.getScopedSymbolTable(symbol);
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
    
    public void outASession(ASession node)
    {
        fCurrentSymbolTable = fCurrentSymbolTable.getNext();
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
            
            LinkedList<PInput> inputList = ((AReceive)node.getReceive()).getInput();
            
            for (PInput pi:inputList)
            {
                AInput ai = (AInput)pi;
                ai.apply(this);

                if (fTypeTable.getNodeType(ai.getLvalue()) != Type.STRING
                        && fTypeTable.getNodeType(ai.getLvalue()) != Type.INT)
                {
                    puts("Error: Left Value in Receive statement not of type string/int. Line no:" + ai.getIdentifier().getLine());
                    System.exit(-1);
                }
            }
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
        if(node != null)
        {
            node.apply(this);
        }
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
        
        Type nodeType = fTypeTable.getNodeType(node.getExp());
        
        if(nodeType != null)
        {
            Node parentNode = node.parent();
            while(!(parentNode instanceof AFunction))
            {
                parentNode = parentNode.parent();
            }
            AFunction functionNode = (AFunction) parentNode;
            Type functionType = nodeToType(functionNode.getType());
            if(!TypeRules.returnExpression(functionType, nodeType))
            {
                puts("Error: Return type mismatch in function " + functionNode.getIdentifier().getText().trim() + ". Line no.:" + functionNode.getIdentifier().getLine());
                System.exit(-1);
            }
        }
        else
        {
            puts("Fatal Error (loc=1): Type table does not contain entries for required nodes!");
            System.exit(-1);
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
        
        Type conditionNodeType = fTypeTable.getNodeType(node.getExp());
        
        if(conditionNodeType != null)
        {
            if(TypeRules.controlFlow(conditionNodeType))
            {
                fTypeTable.setNodeType(node, Type.BOOL);
            }
            else
            {
                puts("Error: Type mismatch for condition in if statement.");
                System.exit(-1);
            }
        }
        else
        {
            puts("Fatal Error (loc=2): Type table does not contain entries for required nodes!");
            System.exit(-1);
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
        
        Type conditionNodeType = fTypeTable.getNodeType(node.getExp());
        
        if(conditionNodeType != null)
        {
            if(TypeRules.controlFlow(conditionNodeType))
            {
                fTypeTable.setNodeType(node, Type.BOOL);
            }
            else
            {
                puts("Error: Type mismatch for condition in if else statement.");
                System.exit(-1);
            }
        }
        else
        {
            puts("Fatal Error (loc=3): Type table does not contain entries for required nodes!");
            System.exit(-1);
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
        
        Type conditionNodeType = fTypeTable.getNodeType(node.getExp());
        
        if(conditionNodeType != null)
        {
            if(TypeRules.controlFlow(conditionNodeType))
            {
                fTypeTable.setNodeType(node, Type.BOOL);
            }
            else
            {
                puts("Error: Type mismatch for condition in while statement.");
                System.exit(-1);
            }
        }
        else
        {
            puts("Fatal Error (loc=4): Type table does not contain entries for required nodes!");
            System.exit(-1);
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

    public void inACompoundstm(ACompoundstm node)
    {
        defaultIn(node);
    }

    public void outACompoundstm(ACompoundstm node)
    {
        defaultOut(node);
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
        
        Type leftNodeType = fTypeTable.getNodeType(node.getLvalue());
        Type rightNodeType = fTypeTable.getNodeType(node.getRight());
        
        if(leftNodeType != null && rightNodeType != null)
        {
            if(! TypeRules.assignment(leftNodeType, rightNodeType))
            {
                puts("Error: Type mismatch for assignment.");
                System.exit(-1);
            }
            fTypeTable.setNodeType(node, leftNodeType);
        }
        else
        {
            puts("Fatal Error (loc=5): Type table does not contain entries for required nodes!");
            System.exit(-1);
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
        
        Type leftNodeType = fTypeTable.getNodeType(node.getLeft());
        Type rightNodeType = fTypeTable.getNodeType(node.getRight());
        
        if(leftNodeType != null && rightNodeType != null)
        {
            if(TypeRules.logicalComparison(leftNodeType, rightNodeType))
            {
                fTypeTable.setNodeType(node, Type.BOOL);
            }
            else
            {
                puts("Error: Type mismatch for || logical comparison.");
                System.exit(-1);
            }
        }
        else
        {
            puts("Fatal Error (loc=6): Type table does not contain entries for required nodes!");
            System.exit(-1);
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
        
        Type leftNodeType = fTypeTable.getNodeType(node.getLeft());
        Type rightNodeType = fTypeTable.getNodeType(node.getRight());
        
        if(leftNodeType != null && rightNodeType != null)
        {
            if(TypeRules.logicalComparison(leftNodeType, rightNodeType))
            {
                fTypeTable.setNodeType(node, Type.BOOL);
            }
            else
            {
                puts("Error: Type mismatch for && logical comparison.");
                System.exit(-1);
            }
        }
        else
        {
            puts("Fatal Error (loc=7): Type table does not contain entries for required nodes!");
            System.exit(-1);
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
        
        Type leftNodeType = fTypeTable.getNodeType(node.getLeft());
        Type rightNodeType = fTypeTable.getNodeType(node.getRight());
        
        if(leftNodeType != null && rightNodeType != null)
        {
            if(TypeRules.intComparison(leftNodeType, rightNodeType) ||
                    TypeRules.strComparison(leftNodeType, rightNodeType) ||
                        TypeRules.tuppyComparison(leftNodeType, rightNodeType))
            {
                fTypeTable.setNodeType(node, Type.BOOL);
            }
            else
            {
                puts("Error: Type mismatch for equal comparison.");
                System.exit(-1);
            }
        }
        else
        {
            puts("Error (loc=8): Type table does not contain entries for required nodes!");
            System.exit(-1);
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
        
        Type leftNodeType = fTypeTable.getNodeType(node.getLeft());
        Type rightNodeType = fTypeTable.getNodeType(node.getRight());
        
        if(leftNodeType != null && rightNodeType != null)
        {
            if(TypeRules.intComparison(leftNodeType, rightNodeType)||
                    TypeRules.strComparison(leftNodeType, rightNodeType))
            {
                fTypeTable.setNodeType(node, Type.BOOL);
            }
            else
            {
                puts("Error: Type mismatch for not equal comparison.");
                System.exit(-1);
            }
        }
        else
        {
            puts("Error(9): Type table does not contain entries for required nodes!");
            System.exit(-1);
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
        
        Type leftNodeType = fTypeTable.getNodeType(node.getLeft());
        Type rightNodeType = fTypeTable.getNodeType(node.getRight());
        
        if(leftNodeType != null && rightNodeType != null)
        {
            if(TypeRules.intComparison(leftNodeType, rightNodeType))
            {
                fTypeTable.setNodeType(node, Type.BOOL);
            }
            else
            {
                puts("Error: Type mismatch for less than comparison.");
                System.exit(-1);
            }
        }
        else
        {
            puts("Error(10): Type table does not contain entries for required nodes!");
            System.exit(-1);
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
        
        Type leftNodeType = fTypeTable.getNodeType(node.getLeft());
        Type rightNodeType = fTypeTable.getNodeType(node.getRight());
        
        if(leftNodeType != null && rightNodeType != null)
        {
            if(TypeRules.intComparison(leftNodeType, rightNodeType))
            {
                fTypeTable.setNodeType(node, Type.BOOL);
            }
            else
            {
                puts("Error: Type mismatch for greater than comparison.");
                System.exit(-1);
            }
        }
        else
        {
            puts("Error(11): Type table does not contain entries for required nodes!");
            System.exit(-1);
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
        
        Type leftNodeType = fTypeTable.getNodeType(node.getLeft());
        Type rightNodeType = fTypeTable.getNodeType(node.getRight());
        
        if(leftNodeType != null && rightNodeType != null)
        {
            if(TypeRules.intComparison(leftNodeType, rightNodeType))
            {
                fTypeTable.setNodeType(node, Type.BOOL);
            }
            else
            {
                puts("Error: Type mismatch for less or equal comparison.");
                System.exit(-1);
            }
        }
        else
        {
            puts("Error(12): Type table does not contain entries for required nodes!");
            System.exit(-1);
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
        
        Type leftNodeType = fTypeTable.getNodeType(node.getLeft());
        Type rightNodeType = fTypeTable.getNodeType(node.getRight());
        
        if(leftNodeType != null && rightNodeType != null)
        {
            if(TypeRules.intComparison(leftNodeType, rightNodeType))
            {
                fTypeTable.setNodeType(node, Type.BOOL);
            }
            else
            {
                puts("Error: Type mismatch for greater or equal comparison.");
                System.exit(-1);
            }
        }
        else
        {
            puts("Error(13): Type table does not contain entries for required nodes!");
            System.exit(-1);
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
        
        Type leftNodeType = fTypeTable.getNodeType(node.getLeft());
        Type rightNodeType = fTypeTable.getNodeType(node.getRight());
        
        if(leftNodeType != null && rightNodeType != null)
        {
            if(TypeRules.intAddition(leftNodeType, rightNodeType))
            {
                fTypeTable.setNodeType(node, Type.INT);
            }
            else if(TypeRules.stringAddition(leftNodeType, rightNodeType))
            {
                fTypeTable.setNodeType(node, Type.STRING);
            }
            else
            {
                puts("Error: Type mismatch for addition.");
                System.exit(-1);
            }
        }
        else
        {
            puts("Error(14): Type table does not contain entries for required nodes!");
            System.exit(-1);
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
        
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        
        Type leftNodeType = fTypeTable.getNodeType(node.getLeft());
        Type rightNodeType = fTypeTable.getNodeType(node.getRight());
        
        if(leftNodeType != null && rightNodeType != null)
        {
            if(TypeRules.intSubtraction(leftNodeType, rightNodeType))
            {
                fTypeTable.setNodeType(node, Type.INT);
            }
            else
            {
                puts("Error: Type mismatch for subtraction.");
                System.exit(-1);
            }
        }
        else
        {
            puts("Error(15): Type table does not contain entries for required nodes!");
            System.exit(-1);
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
        
        Type leftNodeType = fTypeTable.getNodeType(node.getLeft());
        Type rightNodeType = fTypeTable.getNodeType(node.getRight());
        
        if(leftNodeType != null && rightNodeType != null)
        {
            if(TypeRules.intMultiplication(leftNodeType, rightNodeType))
            {
                fTypeTable.setNodeType(node, Type.INT);
            }
            else
            {
                puts("Error: Type mismatch for multiplication.");
                System.exit(-1);
            }
        }
        else
        {
            puts("Error(16): Type table does not contain entries for required nodes!");
            System.exit(-1);
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
        
        Type leftNodeType = fTypeTable.getNodeType(node.getLeft());
        Type rightNodeType = fTypeTable.getNodeType(node.getRight());
        
        if(leftNodeType != null && rightNodeType != null)
        {
            if(TypeRules.intDivision(leftNodeType, rightNodeType))
            {
                fTypeTable.setNodeType(node, Type.INT);
            }
            else
            {
                puts("Error: Type mismatch for division.");
                System.exit(-1);
            }
        }
        else
        {
            puts("Error(17): Type table does not contain entries for required nodes!");
            System.exit(-1);
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
        
        Type leftNodeType = fTypeTable.getNodeType(node.getLeft());
        Type rightNodeType = fTypeTable.getNodeType(node.getRight());
        
        if(leftNodeType != null && rightNodeType != null)
        {
            if(TypeRules.intModulo(leftNodeType, rightNodeType))
            {
                fTypeTable.setNodeType(node, Type.INT);
            }
            else
            {
                puts("Error: Type mismatch for modulo.");
                System.exit(-1);
            }
        }
        else
        {
            puts("Error(18): Type table does not contain entries for required nodes!");
            System.exit(-1);
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
        if (node.getLeft()==null && node.getRight()==null)
        {
            puts("Error(24): Left nodes and right nodes of a join expression not found.");
        }
               
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        
        if(fTypeTable.containsNode(node.getLeft()))
        {
            if(fTypeTable.getNodeType(node.getLeft()) == Type.TUPLE)
            {
                fTypeTable.setNodeType(node, Type.TUPLE);
            }
            else
            {
                puts("Error: Type mismatch: Right part of join expression is not a tuple.");
                System.exit(-1);
            }
        }
        else if(node.getLeft() instanceof ALvalueExp)
        {
            ALvalueExp lvalueExp = (ALvalueExp) node.getLeft();
            if(lvalueExp.getLvalue() instanceof ASimpleLvalue)
            {
                ASimpleLvalue simpleLvalue = (ASimpleLvalue) lvalueExp.getLvalue();
                String tupleName = simpleLvalue.getIdentifier().getText().trim();
                SVariable variable = (SVariable) SymbolTable.lookupHierarchy(fCurrentSymbolTable, tupleName);
                if(variable.getTupleSymbolTable() != null)
                {
                    fTypeTable.setNodeType(node, Type.TUPLE);
                }
                else
                {
                    puts("Error: Type mismatch: " + tupleName + " is not a tuple, at line: "+ simpleLvalue.getIdentifier().getLine());
                    System.exit(-1);
                }
            }
        }
        else
        {
            puts("Error(21): Type table does not contain entries for required nodes!");
            System.exit(-1);
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

        if(fTypeTable.containsNode(node.getLeft()))
        {
            if(fTypeTable.getNodeType(node.getLeft()) == Type.TUPLE)
            {
                fTypeTable.setNodeType(node, Type.TUPLE);
            }
            else
            {
                puts("Error: Type mismatch: Left side of remove expression is not a tuple.");
                System.exit(-1);
            }
        }
        else if(node.getLeft() instanceof ALvalueExp)
        {
            ALvalueExp lvalueExp = (ALvalueExp) node.getLeft();
            if(lvalueExp.getLvalue() instanceof ASimpleLvalue)
            {
                ASimpleLvalue simpleLvalue = (ASimpleLvalue) lvalueExp.getLvalue();
                String tupleName = simpleLvalue.getIdentifier().getText().trim();
                SVariable variable = (SVariable) SymbolTable.lookupHierarchy(fCurrentSymbolTable, tupleName);
                if(variable.getTupleSymbolTable() != null)
                {
                    fTypeTable.setNodeType(node, Type.TUPLE);
                    
                    // check if identifier part of tuple's schema

                }
                else
                {
                    puts("Error: Type mismatch: " + tupleName + " is not a tuple, at line: "+ simpleLvalue.getIdentifier().getLine());
                    System.exit(-1);
                }
            }
        }
        else
        {
            puts("Error: Wrong format for keep expression.");
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
       
        if(fTypeTable.containsNode(node.getLeft()))
        {
            if(fTypeTable.getNodeType(node.getLeft()) == Type.TUPLE)
            {
                fTypeTable.setNodeType(node, Type.TUPLE);
            }
            else
            {
                puts("Error: Type mismatch: Left side of remove expression is not a tuple.");
                System.exit(-1);
            }
        }
        else if(node.getLeft() instanceof ALvalueExp)
        {
            ALvalueExp lvalueExp = (ALvalueExp) node.getLeft();
            if(lvalueExp.getLvalue() instanceof ASimpleLvalue)
            {
                ASimpleLvalue simpleLvalue = (ASimpleLvalue) lvalueExp.getLvalue();
                String tupleName = simpleLvalue.getIdentifier().getText().trim();
                SVariable variable = (SVariable) SymbolTable.lookupHierarchy(fCurrentSymbolTable, tupleName);
                if(variable.getTupleSymbolTable() != null)
                {
                    fTypeTable.setNodeType(node, Type.TUPLE);
                }
                else
                {
                    puts("Error: Type mismatch: " + tupleName + " is not a tuple, at line: "+ simpleLvalue.getIdentifier().getLine());
                    System.exit(-1);
                }
            }
        }
        else
        {
            puts("Error(23): Type table does not contain entries for required nodes!");
            System.exit(-1);
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

        if(node.getLeft() instanceof ALvalueExp)
        {
            ALvalueExp lvalueExp = (ALvalueExp) node.getLeft();
            if(lvalueExp.getLvalue() instanceof ASimpleLvalue)
            {
                ASimpleLvalue simpleLvalue = (ASimpleLvalue) lvalueExp.getLvalue();
                String tupleName = simpleLvalue.getIdentifier().getText().trim();
                SVariable variable = (SVariable) SymbolTable.lookupHierarchy(fCurrentSymbolTable, tupleName);
                if(variable.getTupleSymbolTable() != null)
                {
                    fTypeTable.setNodeType(node, Type.TUPLE);
                    
                    // check if identifier part of tuple's schema

                }
                else
                {
                    puts("Error: Type mismatch: " + tupleName + " is not a tuple, at line: "+ simpleLvalue.getIdentifier().getLine());
                    System.exit(-1);
                }
            }
        }
        else
        {
            puts("Error: Wrong format for keep expression.");
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
        
        if(fTypeTable.containsNode(node.getLeft()))
        {
            if(fTypeTable.getNodeType(node.getLeft()) == Type.TUPLE)
            {
                fTypeTable.setNodeType(node, Type.TUPLE);
            }
            else
            {
                puts("Error: Type mismatch: Left side of removemany expression is not a tuple.");
                System.exit(-1);
            }
        }
        else if(node.getLeft() instanceof ALvalueExp)
        {
            ALvalueExp lvalueExp = (ALvalueExp) node.getLeft();
            if(lvalueExp.getLvalue() instanceof ASimpleLvalue)
            {
                ASimpleLvalue simpleLvalue = (ASimpleLvalue) lvalueExp.getLvalue();
                String tupleName = simpleLvalue.getIdentifier().getText().trim();
                SVariable variable = (SVariable) SymbolTable.lookupHierarchy(fCurrentSymbolTable, tupleName);
                if(variable.getTupleSymbolTable() != null)
                {
                    fTypeTable.setNodeType(node, Type.TUPLE);
                }
                else
                {
                    puts("Error: Type mismatch: " + tupleName + " is not a tuple, at line: "+ simpleLvalue.getIdentifier().getLine());
                    System.exit(-1);
                }
            }
        }
        else
        {
            puts("Error(23): Type table does not contain entries for required nodes!");
            System.exit(-1);
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
        Type leftNodeType = fTypeTable.getNodeType(node.getLeft());
        
        if(leftNodeType != null)
        {
            if(TypeRules.notExpression(leftNodeType))
            {
                fTypeTable.setNodeType(node, Type.BOOL);
            }
            else
            {
                puts("Error: Type mismatch for not expression.");
                System.exit(-1);
            }
        }
        else
        {
            puts("Error(19): Type table does not contain entries for required nodes!");
            System.exit(-1);
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
        
        Type leftNodeType = fTypeTable.getNodeType(node.getLeft());
        
        if(leftNodeType != null)
        {
            if(TypeRules.intNegation(leftNodeType))
            {
                fTypeTable.setNodeType(node, Type.INT);
            }
            else
            {
                puts("Error: Type mismatch for negation.");
                System.exit(-1);
            }
        }
        else
        {
            puts("Error(20): Type table does not contain entries for required nodes!");
            //System.exit(-1);
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
        Type leftNodeType = fTypeTable.getNodeType(node.getLeft());
        if(leftNodeType != null)
        {
            fTypeTable.setNodeType(node, leftNodeType);
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
        Type lValueNodeType = fTypeTable.getNodeType(node.getLvalue());
        if(lValueNodeType != null)
        {
            fTypeTable.setNodeType(node, lValueNodeType);
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
       
        SFunction symbol = (SFunction) SymbolTable.lookupHierarchy(fCurrentSymbolTable, node.getIdentifier().getText().trim());
        AFunction function = symbol.getFunction();
        
        Type[] argTypes = getArgumentTypes(function.getArgument());
        Type[] paramTypes;
        {
            List<PExp> copy = new ArrayList<PExp>(node.getExp());            
            paramTypes = new Type[copy.size()];
            
            for(PExp e : copy)
            {
                e.apply(this);
            }
            
            for(int i=0; i<paramTypes.length; ++i)
            {
                paramTypes[i] = fTypeTable.getNodeType(copy.get(i));
            }
        }        
        
        if(TypeRules.functionCall(argTypes, paramTypes))
        {
            fTypeTable.setNodeType(node, nodeToType(function.getType()));
        }
        else
        {
            puts("Error: Argument mismatch for function call " + function.getIdentifier().getText()+ ". Line no.:" + node.getIdentifier().getLine());
            System.exit(-1);
        }
        outACallExp(node);
    }
    
    private Type[] getArgumentTypes(LinkedList<PArgument> arguments)
    {
        Type[] argTypes = new Type[arguments.size()];
        
        for(int i = 0; i<argTypes.length; ++i)
        {
            AArgument arg = (AArgument)arguments.get(i);
            argTypes[i] = nodeToType(arg.getType());
        }
        
        return argTypes;
    }
    
    private Type nodeToType(PType node)
    {
        Type type = null;
        if(node instanceof AIntType)
        {
            type = Type.INT;
        }
        else if (node instanceof AStringType)
        {
            type = Type.STRING;
        }
        else if (node instanceof ABoolType)
        {
            type = Type.BOOL;
        }
        else if (node instanceof AVoidType)
        {
            type = Type.VOID;   
        }
        else if (node instanceof ATupleType)
        {
            type = Type.TUPLE;
        }
        else
        {
            puts("Error: Unexpected type!");
            System.exit(-1);
        }
        
        return type;
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
        fTypeTable.setNodeType(node, Type.INT);
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
            fTypeTable.setNodeType(node, Type.BOOL);
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
            fTypeTable.setNodeType(node, Type.BOOL);
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
        fTypeTable.setNodeType(node, Type.STRING);
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
        Type expType = fTypeTable.getNodeType(node.getExp());
        if(expType != null)
        {
            fTypeTable.setNodeType(node, expType);
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
        
        try
        {
            SVariable symbol = (SVariable) SymbolTable.lookupHierarchy(fCurrentSymbolTable, node.getIdentifier().getText());
            AVariable variable = symbol.getVariable();        
            fTypeTable.setNodeType(node, nodeToType(variable.getType()));
        }
        catch(ClassCastException e)
        {
            SArgument symbol = (SArgument) SymbolTable.lookupHierarchy(fCurrentSymbolTable, node.getIdentifier().getText());
            AArgument arg = symbol.getArgument();        
            fTypeTable.setNodeType(node, nodeToType(arg.getType()));
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
        
        SVariable symbol = (SVariable) SymbolTable.lookupHierarchy(fCurrentSymbolTable, node.getLeft().getText());
        TupleSymbolTable tupleSymbolTable = symbol.getTupleSymbolTable();
        
        if (tupleSymbolTable==null)
        {
            puts("Error: Left side of  a qualified value not a tuple. Line no.:" + node.getLeft().getLine());
            System.exit(-1);
        }
        
        SField field = (SField) tupleSymbolTable.getSymbol(node.getRight().getText());
        
        if(field == null)
        {
            puts("Error: a field with the name " + node.getRight().getText() + " does not exist in the schema for tuple " + node.getRight().getText() + ". Line no.:" + node.getLeft().getLine());
            System.exit(-1);
        }
        else
        {            
            fTypeTable.setNodeType(node, nodeToType(field.getField().getType()));
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
    
    private void puts(String s)
    {
        System.out.print(s + "\n");
        System.out.flush();
    }            
}