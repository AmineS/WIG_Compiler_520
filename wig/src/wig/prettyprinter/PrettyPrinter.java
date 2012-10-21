package wig.prettyprinter;

import java.util.*;

import wig.parser.*;
import wig.lexer.*;
import wig.node.*;
import wig.analysis.*;


public class PrettyPrinter extends DepthFirstAdapter
{
    private int tabCount = 0;
    
    public static void print(Node node)
    {
        node.apply(new PrettyPrinter());
    }
    
    private void puts(String s)
    {
//        printTab();
        System.out.print(s);
        System.out.flush();
    }
    
    private void printTab()
    {
        for(int i=0; i<tabCount; ++i)
        {
            puts("\t");
        }
    }
    
    public void caseAService(AService node)
    {
        puts("Service \n{\n");
        ++tabCount;
        
        for(PHtml html : node.getHtml())
        {
            html.apply(this);
        }
        
        for(PSchema schema : node.getSchema())
        {
            schema.apply(this);
        }
        
        for(PVariable variable : node.getVariable())
        {
            variable.apply(this);
        }
        
        for(PFunction function : node.getFunction())
        {
            function.apply(this);
        }
        
        for(PSession session : node.getSession())
        {
            session.apply(this);
        }
        
        puts("\n}");
        --tabCount;
    }
    
    public void caseAHtml(AHtml node)
    {
        for(PHtmlbody htmlbody : node.getHtmlbody())
        {
            htmlbody.apply(this);
        }
    }
    
    public void caseATagStartHtmlbody(ATagStartHtmlbody node)
    {
        puts("const html");
        
        node.getIdentifier().apply(this);
        
        puts("= <html> ");
        
        for(PAttribute attribute : node.getAttribute())
        {
            attribute.apply(this);
        }
    }
    
      public void caseATagEndHtmlbody(ATagEndHtmlbody node)
      {
          puts(" </html>;\n");
      }
      
      
      public void caseAHoleHtmlbody(AHoleHtmlbody node)
      {
          node.getIdentifier().apply(this);
      }
      
      public void caseAWhateverHtmlbody(AWhateverHtmlbody node)
      {
          node.getWhatever().apply(this);
      }
      
      public void caseAMetaHtmlbody(AMetaHtmlbody node)
      {
          puts("<!--");
          
          node.getMeta().apply(this);
          
          puts("-->");
      }
      
      public void caseAInputHtmlbody(AInputHtmlbody node)
      {
          node.getInput().apply(this);
          
          for(PInputattr inputAttr : node.getInputattr())
          {
              inputAttr.apply(this);
          }
          
          puts("/>");
      }
      
      public void caseASelectHtmlbody(ASelectHtmlbody node)
      {
          node.getSelectTag().apply(this);
      }
      
//    public void caseANameInputattr(ANameInputattr node);
//    public void caseATypeInputattr(ATypeInputattr node);
//    public void caseAAttributeInputattr(AAttributeInputattr node);
//    public void caseATexttypeInputtype(ATexttypeInputtype node);
//    public void caseARadiotypeInputtype(ARadiotypeInputtype node);
//    public void caseAStrtypeInputtype(AStrtypeInputtype node);
//    public void caseAAttrAttribute(AAttrAttribute node);
//    public void caseAAssignAttribute(AAssignAttribute node);
//    public void caseAIdAttr(AIdAttr node);
//    public void caseAStrAttr(AStrAttr node);
//    public void caseAIconstAttr(AIconstAttr node);
//    public void caseANegintIntconst(ANegintIntconst node);
//    public void caseAPosintIntconst(APosintIntconst node);
//    public void caseASchema(ASchema node);
//    public void caseAField(AField node);
//    public void caseAVariable(AVariable node);
//    public void caseAIdentifiers(AIdentifiers node);
//    public void caseAIntType(AIntType node);
//    public void caseABoolType(ABoolType node);
//    public void caseAStringType(AStringType node);
//    public void caseApublic voidType(Apublic voidType node);
//    public void caseASimpleType(ASimpleType node);
//    public void caseATupleType(ATupleType node);
//    public void caseAFunction(AFunction node);
//    public void caseAArguments(AArguments node);
//    public void caseAArgument(AArgument node);
//    public void caseASession(ASession node);
//    public void caseAEmptyStm(AEmptyStm node);
//    public void caseAShowStm(AShowStm node);
//    public void caseAExitStm(AExitStm node);
//    public void caseAReturnStm(AReturnStm node);
//    public void caseAReturnexpStm(AReturnexpStm node);
//    public void caseAIfStm(AIfStm node);
//    public void caseAIfelseStm(AIfelseStm node);
//    public void caseAWhileStm(AWhileStm node);
//    public void caseACompStm(ACompStm node);
//    public void caseAExpStm(AExpStm node);
//    public void caseAIdDocument(AIdDocument node);
//    public void caseAPlugDocument(APlugDocument node);
//    public void caseAReceive(AReceive node);
//    public void caseACompoundstm(ACompoundstm node);
//    public void caseAPlugs(APlugs node);
//    public void caseAPlug(APlug node);
//    public void caseAInputs(AInputs node);
//    public void caseAInput(AInput node);
//    public void caseAAssignExp(AAssignExp node);
//    public void caseAOrExp(AOrExp node);
//    public void caseAAndExp(AAndExp node);
//    public void caseAEqExp(AEqExp node);
//    public void caseANeqExp(ANeqExp node);
//    public void caseALtExp(ALtExp node);
//    public void caseAGtExp(AGtExp node);
//    public void caseALteqExp(ALteqExp node);
//    public void caseAGteqExp(AGteqExp node);
//    public void caseAPlusExp(APlusExp node);
//    public void caseAMinusExp(AMinusExp node);
//    public void caseAMultExp(AMultExp node);
//    public void caseADivExp(ADivExp node);
//    public void caseAModExp(AModExp node);
//    public void caseAJoinExp(AJoinExp node);
//    public void caseAKeepExp(AKeepExp node);
//    public void caseARemoveExp(ARemoveExp node);
//    public void caseAKeepManyExp(AKeepManyExp node);
//    public void caseARemoveManyExp(ARemoveManyExp node);
//    public void caseANotExp(ANotExp node);
//    public void caseANegExp(ANegExp node);
//    public void caseADefaultExp(ADefaultExp node);
//    public void caseALvalueExp(ALvalueExp node);
//    public void caseACallExp(ACallExp node);
//    public void caseAIntExp(AIntExp node);
//    public void caseATrueExp(ATrueExp node);
//    public void caseAFalseExp(AFalseExp node);
//    public void caseAStringExp(AStringExp node);
//    public void caseATupleExp(ATupleExp node);
//    public void caseAParenExp(AParenExp node);
//    public void caseAExps(AExps node);
//    public void caseASimpleLvalue(ASimpleLvalue node);
//    public void caseAQualifiedLvalue(AQualifiedLvalue node);
//    public void caseAFieldvalues(AFieldvalues node);
//    public void caseAFieldvalue(AFieldvalue node);
//
//    public void caseTEol(TEol node);
//    public void caseTBlank(TBlank node);
//    public void caseTService(TService node);
//    public void caseTConst(TConst node);
//    public void caseTHtml(THtml node);
//    public void caseTSchema(TSchema node);
//    public void caseTSession(TSession node);
//    public void caseTShow(TShow node);
//    public void caseTExit(TExit node);
//    public void caseTReturn(TReturn node);
//    public void caseTIf(TIf node);
//    public void caseTElse(TElse node);
//    public void caseTWhile(TWhile node);
//    public void caseTPlug(TPlug node);
//    public void caseTReceive(TReceive node);
//    public void caseTInt(TInt node);
//    public void caseTBool(TBool node);
//    public void caseTString(TString node);
//    public void caseTpublic void(Tpublic void node);
//    public void caseTTuple(TTuple node);
//    public void caseTTrue(TTrue node);
//    public void caseTFalse(TFalse node);
//    public void caseTLBrace(TLBrace node);
//    public void caseTRBrace(TRBrace node);
//    public void caseTAssign(TAssign node);
//    public void caseTSemicolon(TSemicolon node);
//    public void caseTLt(TLt node);
//    public void caseTGt(TGt node);
//    public void caseTLPar(TLPar node);
//    public void caseTRPar(TRPar node);
//    public void caseTLBracket(TLBracket node);
//    public void caseTRBracket(TRBracket node);
//    public void caseTComma(TComma node);
//    public void caseTKeep(TKeep node);
//    public void caseTRemove(TRemove node);
//    public void caseTJoin(TJoin node);
//    public void caseTEq(TEq node);
//    public void caseTNeq(TNeq node);
//    public void caseTLteq(TLteq node);
//    public void caseTGteq(TGteq node);
//    public void caseTNot(TNot node);
//    public void caseTMinus(TMinus node);
//    public void caseTPlus(TPlus node);
//    public void caseTMult(TMult node);
//    public void caseTDiv(TDiv node);
//    public void caseTMod(TMod node);
//    public void caseTAnd(TAnd node);
//    public void caseTOr(TOr node);
//    public void caseTDot(TDot node);
//    public void caseTInput(TInput node);
//    public void caseTSelect(TSelect node);
//    public void caseTType(TType node);
//    public void caseTName(TName node);
//    public void caseTText(TText node);
//    public void caseTRadio(TRadio node);
//    public void caseTLtSlash(TLtSlash node);
//    public void caseTNegIntconst(TNegIntconst node);
//    public void caseTIdentifier(TIdentifier node);
//    public void caseTIntconst(TIntconst node);
//    public void caseTStringconst(TStringconst node);
//    public void caseTHtmlCommentStart(THtmlCommentStart node);
//    public void caseTHtmlCommentEnd(THtmlCommentEnd node);
//    public void caseTHtmlTagsStart(THtmlTagsStart node);
//    public void caseTHtmlTagsEnd(THtmlTagsEnd node);
//    public void caseTLtBracket(TLtBracket node);
//    public void caseTGtBracket(TGtBracket node);
//    public void caseTHtmlTagStart(THtmlTagStart node);
//    public void caseTHtmlTagEnd(THtmlTagEnd node);
//    public void caseTMeta(TMeta node);
//    public void caseTWigCommentStart(TWigCommentStart node);
//    public void caseTWigCommentEnd(TWigCommentEnd node);
//    public void caseTWhatever(TWhatever node);
//    public void caseTPosIntconst(TPosIntconst node);
//    public void caseEOF(EOF node);
}
