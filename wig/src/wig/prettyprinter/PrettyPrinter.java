package wig.prettyprinter;

import java.util.*;

import wig.parser.*;
import wig.lexer.*;
import wig.node.*;
import wig.analysis.*;


public class PrettyPrinter extends DepthFirstAdapter
{
    private int tabCount = 0;
    private int oldCount = 0;
    
    public static void print(Node node)
    {
        node.apply(new PrettyPrinter());
    }
    
    private void puts(String s)
    {
        System.out.print(s);
        System.out.flush();
    }
    
    private void putsTab(String s)
    {
        printTab();
        System.out.print(s);
        System.out.flush();
    }
    private void printTab()
    {
        for(int i=0; i<tabCount; ++i)
        {
            System.out.print("    ");
        }
    }
    private void incrementTabCount()
    {
        ++tabCount;
    }
    
    private void decrementTabCount()
    {
        --tabCount;
    }    
    
    private void resetTabs()
    {
        oldCount = tabCount;
        tabCount = 0; 
    }
    
    private void restoreTabs()
    {
        tabCount = oldCount;
    }
    
    public void caseAService(AService node)
    {
        puts("service\n{\n");
        incrementTabCount();
        
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
        
        puts("}");
        decrementTabCount();
    }
    
    public void caseAHtml(AHtml node)
    {
        putsTab("const html ");        
        node.getIdentifier().apply(this);
        puts(" = ");
        puts("<html>");
        incrementTabCount();
        for(PHtmlbody htmlbody : node.getHtmlbody())
        {
            htmlbody.apply(this);
        }
        decrementTabCount();
        puts("\n");
        putsTab("</html>\n\n");
    }
    
    public void caseATagStartHtmlbody(ATagStartHtmlbody node)
    {              
        String id = node.getIdentifier().getText();
        if(id.equals("br"))
        {
            puts("\n");
            incrementTabCount();
            putsTab("");
            puts("<");
            decrementTabCount();
        }
        else if (id.equals("body"))
        {
            puts("\n");
            putsTab("<");
            incrementTabCount();
        }
        else if(id.equals("option"))
        {
            puts("\n");
            incrementTabCount();
            putsTab("<");
            decrementTabCount();
        }
        else if(id.equals("td"))
        {
            putsTab("<");
            resetTabs();
        }
        else
        {
            putsTab("<");
        }
        node.getIdentifier().apply(this);
        if (node.getAttribute().size()!=0)
            puts(" ");
        for(PAttribute attribute : node.getAttribute())
        {
            attribute.apply(this);
        }
        puts(">");
    }
    
      public void caseATagEndHtmlbody(ATagEndHtmlbody node)
      {
          
          String id = node.getIdentifier().getText();
          
          if(id.equals("body"))
          {
              decrementTabCount();
              puts("\n");
              putsTab("</");
          }
          else if(id.equals("td"))
          {
              puts("</");
              restoreTabs();
          }
          else if(id.equals("table") || id.equals("tr") || id.equals("p"))
          {
              putsTab("</");
          }
          else
          {
              puts("</");              
          }
          node.getIdentifier().apply(this);
          puts(">");          
      }
      
      
      public void caseAHoleHtmlbody(AHoleHtmlbody node)
      {
          puts("<[");
          node.getIdentifier().apply(this);
          puts("]>");
      }
      
      public void caseAWhateverHtmlbody(AWhateverHtmlbody node)
      {
          if(node.getWhatever() != null)
          {
              node.getWhatever().apply(this);
          }
      }
      
      public void caseAMetaHtmlbody(AMetaHtmlbody node)
      {
          node.getMeta().apply(this);          
      }
      
      public void caseAInputHtmlbody(AInputHtmlbody node)
      {          
          putsTab("<");
          node.getInput().apply(this);          
          for(PInputattr inputAttr : node.getInputattr())
          {
              inputAttr.apply(this);
          }          
          puts("/>");
      }
      
      public void caseASelectHtmlbody(ASelectHtmlbody node)
      {
          puts("\n");
          putsTab("<");
          node.getSelectTag().apply(this);                        
          for(PInputattr attr : node.getInputattr())
          {
              attr.apply(this);
          }
          puts(">");   
          
          for(PHtmlbody htmlBody : node.getHtmlbody())
          {
              htmlBody.apply(this);
          }
          
          puts("\n");
          putsTab("</");       
          node.getSelectTag().apply(this);
          puts(">");
      }
      
      public void caseANameInputattr(ANameInputattr node)
      {
          puts(" name="); 
          if(node.getAttr() != null)
          {
              node.getAttr().apply(this);              
          }
      }      
      @Override
      public void caseATypeInputattr(ATypeInputattr node)
      {
          puts(" type=");
          if(node.getInputtype() != null)
          {
              node.getInputtype().apply(this);
          }
      }      
            
      public void caseAAttributeInputattr(AAttributeInputattr node)
      {
          if(node.getAttribute() != null)
          {
              node.getAttribute().apply(this);
          }
      }
      
      public void caseATexttypeInputtype(ATexttypeInputtype node)
      {
          if(node.getText() != null)
          {
              node.getText().apply(this);
          }
      }
      
      public void caseARadiotypeInputtype(ARadiotypeInputtype node)
      {
          if(node.getRadio() != null)
          {
              node.getRadio().apply(this);
          }
      }
      
      public void caseAStrtypeInputtype(AStrtypeInputtype node)
      {
          if(node.getStringconst() != null)
          {
              node.getStringconst().apply(this);
          }
      }
      
      public void caseAAttrAttribute(AAttrAttribute node)
      {
          if(node.getAttr() != null)
          {
              node.getAttr().apply(this);
          }
      }           
      
      public void caseAAssignAttribute(AAssignAttribute node)
      {
          puts(" ");
          if(node.getLeftAttr() != null)
          {
              node.getLeftAttr().apply(this);
          }
                    
          if(node.getLeftAttr() != null && node.getRightAttr() != null)
          {
              puts("=");
          }
          
          if(node.getRightAttr() != null)
          {
              node.getRightAttr().apply(this);
          }          
      }
      
      public void caseAIdAttr(AIdAttr node)
      {
          if(node.getIdentifier() != null)
          {
              node.getIdentifier().apply(this);
          }
      }
      
      public void caseAStrAttr(AStrAttr node)
      {          
          if(node.getStringconst() != null)
          {
              node.getStringconst().apply(this);
          }          
      }
      
      public void caseAIconstAttr(AIconstAttr node)
      {
          if(node.getIntconst() != null)
          {
              node.getIntconst().apply(this);
          }
      }
      
      
      public void caseANegintIntconst(ANegintIntconst node)
      {
          if(node.getNegIntconst() != null)
          {
              node.getNegIntconst().apply(this);
          }
      }
      
      public void caseAPosintIntconst(APosintIntconst node)
      {
          if(node.getPosIntconst() != null)
          {
              node.getPosIntconst().apply(this);
          }
      }
            
      public void caseASchema(ASchema node)
      {
          putsTab("schema ");
          if(node.getIdentifier() != null)
          {
              node.getIdentifier().apply(this);
              puts("\n");
          }          
          putsTab("{\n");
          incrementTabCount();
         
          List<PField> fields= node.getField();
          
          if(fields != null)
          {
              for(PField field : fields)
              {
                  putsTab("");
                  field.apply(this);
              }
          }
          decrementTabCount();
          putsTab("}\n");
      }
   
      public void caseAEmptyStm(AEmptyStm node)
      {
          puts(";\n");
      }
      
      public void caseAShowStm(AShowStm node)
      {
          putsTab("show ");
          if(node.getDocument() != null)
          {
              node.getDocument().apply(this);
          }
          if(node.getReceive() != null)
          {
              node.getReceive().apply(this);
          }
          puts(";\n");
      }
      
      public void caseAExitStm(AExitStm node)
      {
          putsTab("exit ");
          node.getDocument().apply(this);
          puts(";\n");
      }
      
      public void caseAReturnStm(AReturnStm node)
      {
          puts("return;\n");
      }
      
      public void caseAReturnexpStm(AReturnexpStm node)
      {
          putsTab("return ");
          node.getExp().apply(this);
          puts("\n;");
      }
      
      public void caseAIfStm(AIfStm node)
      {
          puts("\n");
          putsTab("if(");
          node.getExp().apply(this);
          puts(")\n");
          if(!(node.getStm() instanceof ACompStm))
          {
              putsTab("{");
              puts("\n");
          }
          incrementTabCount();
          node.getStm().apply(this);
          if(!(node.getStm() instanceof ACompStm))
          {
              putsTab("}");
              puts("\n");
          }
          decrementTabCount();
      }
      
      public void caseAIfelseStm(AIfelseStm node)
      {
          puts("\n");
          putsTab("if(");
          node.getExp().apply(this);
          puts(")\n");
          if(!(node.getThenStm() instanceof ACompStm))
          {
              putsTab("{");
              puts("\n");
          }
          node.getThenStm().apply(this);
          if(!(node.getThenStm() instanceof ACompStm))
          {
              putsTab("}");
          }
          putsTab("else\n");
          if(!(node.getElseStm() instanceof ACompStm))
          {
              putsTab("{");
              puts("\n");
          }
          node.getElseStm().apply(this);
          if(!(node.getElseStm() instanceof ACompStm))
          {
              putsTab("}");
              puts("\n");
          }
      }
      
      public void caseAWhileStm(AWhileStm node)
      {
          puts("\n");
          putsTab("while(");
          node.getExp().apply(this);
          puts(")\n");
          if(!(node.getStm() instanceof ACompStm))
          {
              putsTab("{");
              puts("\n");
          }
          node.getStm().apply(this);
          if(!(node.getStm() instanceof ACompStm))
          {
              putsTab("}");
              puts("\n");
          }
      }
      
      public void caseACompStm(ACompStm node)
      {
          node.getCompoundstm().apply(this);
      }
      
      public void caseAExpStm(AExpStm node)
      {
          putsTab("");
          node.getExp().apply(this);
          puts(";\n");
      }
      
      public void caseAIdDocument(AIdDocument node)
      {
          node.getIdentifier().apply(this);
      }
      
      public void caseAPlugDocument(APlugDocument node)
      {
          LinkedList<PPlug> plug_list;
          Iterator<PPlug> iter;
          int plug_list_size, counter;

          puts("plug ");
          node.getIdentifier().apply(this);
          puts("[");
          
          plug_list = node.getPlug();
          iter = plug_list.iterator();
          counter = 0;
          plug_list_size = plug_list.size();
          
          while(iter.hasNext())
          {
             iter.next().apply(this);
             if(iter.hasNext())
                 puts(",");
          }
          puts("] ");     

      }
      
      public void caseAReceive(AReceive node)
      {          
          LinkedList<PInput> input_list;
          Iterator<PInput> iter;
          int input_list_size, counter;
          puts("receive");
          
          input_list = node.getInput();
          iter = input_list.iterator();
          counter = 0;
          input_list_size = input_list.size();

          puts("[");
          while(iter.hasNext())
          {
             iter.next().apply(this);
             if(iter.hasNext())
                 puts(",");
          }
          puts("]");
      }
      
      public void caseACompoundstm(ACompoundstm node)
      {
          LinkedList<PStm> stm_list = node.getStm();
          LinkedList<PVariable> var_list = node.getVariable();
          Iterator<PStm> stm_iter = stm_list.iterator();
          Iterator<PVariable> var_iter = var_list.iterator();          
          
          putsTab("{\n");
          incrementTabCount();
          while(var_iter.hasNext())
          {
              var_iter.next().apply(this);
          }
          
          while(stm_iter.hasNext())
          {
              stm_iter.next().apply(this);
          }
          decrementTabCount();
          putsTab("}\n");
          
      }

      
      public void caseAPlugs(APlugs node)
      {
          LinkedList<PPlug> plug_list;
          Iterator<PPlug> iter;
          int plug_list_size, counter;
          
          plug_list = node.getPlug();
          iter = plug_list.iterator();
          counter = 0;
          plug_list_size = plug_list.size();
          
          while(iter.hasNext())
          {
             iter.next().apply(this);
             if(counter!=plug_list_size-1)
                 puts(",");
          }
      }
      
      public void caseAPlug(APlug node)
      {
          node.getIdentifier().apply(this);
          puts("=");
          node.getExp().apply(this);
      }
      
      public void caseAInputs(AInputs node)
      {
          LinkedList<PInput> input_list;
          Iterator<PInput> iter;
          int input_list_size, counter;
          
          input_list = node.getInput();
          iter = input_list.iterator();
          counter = 0;
          input_list_size = input_list.size();
          
          while(iter.hasNext())
          {
             iter.next().apply(this);
             if(counter!=input_list_size-1)
                 puts(",");
          }
      }
      
      public void caseAInput(AInput node)
      {
          node.getLvalue().apply(this);
          puts("=");
          node.getIdentifier().apply(this);
      }
      
      public void caseAAssignExp(AAssignExp node)
      {
          node.getLvalue().apply(this);
          puts(" = ");
          node.getRight().apply(this);
      }
      
      public void caseAOrExp(AOrExp node)
      {
          node.getLeft().apply(this);
          puts(" || ");
          node.getRight().apply(this);
      }
      
      public void caseAAndExp(AAndExp node)
      {
          node.getLeft().apply(this);
          puts("&&");
          node.getRight().apply(this);
      }
            
      public void caseAEqExp(AEqExp node)
      {
          node.getLeft().apply(this);
          puts("==");
          node.getRight().apply(this);
      }
            
      public void caseANeqExp(ANeqExp node)
      {
          node.getLeft().apply(this);
          puts("!=");
          node.getRight().apply(this);
      }
      
      public void caseALtExp(ALtExp node)
      {
          node.getLeft().apply(this);
          puts("<");
          node.getRight().apply(this);
      }
      
      public void caseAGtExp(AGtExp node)
      {
          node.getLeft().apply(this);
          puts(">");
          node.getRight().apply(this);
      }
      
      public void caseALteqExp(ALteqExp node)
      {
          node.getLeft().apply(this);
          puts("<=");
          node.getRight().apply(this);
      }
      
      public void caseAGteqExp(AGteqExp node)
      {
          node.getLeft().apply(this);
          puts(">=");
          node.getRight().apply(this);
      }
     
      
      public void caseAPlusExp(APlusExp node)
      {
          node.getLeft().apply(this);
          puts("+");
          node.getRight().apply(this);
      }
      
      public void caseAMinusExp(AMinusExp node)
      {
          node.getLeft().apply(this);
          puts("-");
          node.getRight().apply(this);
      }
      
      public void caseAMultExp(AMultExp node)
      {
          node.getLeft().apply(this);
          puts("*");
          node.getRight().apply(this);
      }

      public void caseADivExp(ADivExp node)
      {
          node.getLeft().apply(this);
          puts("/");
          node.getRight().apply(this);
      }
      
      public void caseAModExp(AModExp node)
      {
          node.getLeft().apply(this);
          puts("%");
          node.getRight().apply(this);
      }
      
      public void caseAJoinExp(AJoinExp node)
      {
          node.getLeft().apply(this);
          puts("<<");
          node.getRight().apply(this);
      }
      
      public void caseAKeepExp(AKeepExp node)
      {
          node.getLeft().apply(this);
          puts(" keep ");
          node.getIdentifier().apply(this);
      }

      public void caseARemoveExp(ARemoveExp node)
      {
          node.getLeft().apply(this);
          puts(" remove ");
          node.getIdentifier().apply(this);
      }
      
      public void caseAKeepManyExp(AKeepManyExp node)
      {
          int counter,linked_list_size;
          LinkedList<TIdentifier> identifier_list;
          Iterator<TIdentifier> iter;
          
          node.getLeft().apply(this);
          puts("keep");
          identifier_list = node.getIdentifier();
          linked_list_size = identifier_list.size();
          iter = identifier_list.iterator();
          counter = 0;
          
          puts("(");
          while(iter.hasNext())
          {
              iter.next().apply(this);
              if (counter!=linked_list_size-1)
                  puts(",");
              counter++;
          }
          puts(")");
      }
      
      public void caseARemoveManyExp(ARemoveManyExp node)
      {
          int counter,linked_list_size;
          LinkedList<TIdentifier> identifier_list;
          Iterator<TIdentifier> iter;
          
          node.getLeft().apply(this);
          puts("remove");
          identifier_list = node.getIdentifier();
          linked_list_size = identifier_list.size();
          iter = identifier_list.iterator();
          counter = 0;

          puts("(");
          while(iter.hasNext())
          {
              iter.next().apply(this);
              if (counter!=linked_list_size-1)
                  puts(",");
              counter++;
          }
          puts(")");
      }
      
      public void caseANotExp(ANotExp node)
      {
          puts("!");
          node.getLeft().apply(this);
      }
      
      public void caseANegExp(ANegExp node)
      {
          puts("-");
          node.getLeft().apply(this);
      }
      
      public void caseADefaultExp(ADefaultExp node)
      {
          node.apply(this);
      }
      
      public void caseALvalueExp(ALvalueExp node)
      {
          node.getLvalue().apply(this);
      }

      public void caseACallExp(ACallExp node)
      {
          Iterator<PExp> iter;

          node.getIdentifier().apply(this);
          
          iter = node.getExp().iterator();          

          puts("(");
          while(iter.hasNext())
          {
              iter.next().apply(this);
          }
          puts(")");
      }
      
      public void caseAIntExp(AIntExp node)
      {
          node.getIntconst().apply(this);
      }
      
      public void caseATrueExp(ATrueExp node)
      {
          node.getTrue().apply(this);
      }
      
      public void caseAFalseExp(AFalseExp node)
      {
          node.getFalse().apply(this);
      }
      
      public void caseAStringExp(AStringExp node)
      {
          node.getStringconst().apply(this);
      }
      
      public void caseATupleExp(ATupleExp node)
      {   
          puts("tuple {");
          Iterator<PFieldvalue> iter = node.getFieldvalue().iterator();
          while(iter.hasNext())
          {
              iter.next().apply(this);
          }
          puts("}");
      }
      
      public void caseAParenExp(AParenExp node)
      {
          puts("(");
          node.getExp().apply(this);
          puts(")");
      }
      
      public void caseAExps(AExps node)
      {
          for (PExp expression: node.getExp())
          {
              expression.apply(this);
              puts(",");
          }
      }
      
      public void caseAQualifiedLvalue(AQualifiedLvalue node)
      {
          node.getLeft().apply(this);
          puts(".");
          node.getRight().apply(this);
      }
      
      public void caseASimpleLvalue(ASimpleLvalue node)
      {
          node.getIdentifier().apply(this);
      }
   
      public void caseAFieldvalues(AFieldvalues node)
      {
          for (PFieldvalue fieldvalue : node.getFieldvalue())
          {
              fieldvalue.apply(this);
          }
      }          
      
      public void caseAFieldvalue(AFieldvalue node)
      {
          node.getIdentifier().apply(this);
          puts(" = ");
          node.getExp().apply(this);
      }
      
      public void caseTService(TService node)
      {
          puts(node.getText());
      }
      
      public void caseTConst(TConst node)
      {
          puts(node.getText());
      }

      public void caseTHtml(THtml node)
      {
          puts(node.getText());
      }
      
      public void caseTHtmlTagStart(THtmlTagStart node)
      {
          puts(node.getText());
      }
      
      public void caseTSchema(TSchema node)
      {
          puts(node.getText());
      }
      
      public void caseTSession(TSession node)
      {
          puts(node.getText());
      }
      
      public void caseTShow(TShow node)
      {
          puts(node.getText());
      }
      
      public void caseTExit(TExit node)
      {
          puts(node.getText());
      }
      
      public void caseTReturn(TReturn node)
      {
          puts(node.getText());
      }
      
      public void caseTIf(TIf node)
      {
          puts(node.getText());
      }
      
      public void caseTElse(TElse node)
      {
          puts(node.getText());
      }
      
      public void caseTWhile(TWhile node)
      {
          puts(node.getText());
      }
      
      public void caseTPlug(TPlug node)
      {
          puts(node.getText());
      }
      
      public void caseTReceive(TReceive node)
      {
          puts(node.getText());
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
          puts(node.getText());
      }
      
      public void caseTTuple(TTuple node)
      {
          puts(node.getText());
      }
      
      public void caseTTrue(TTrue node)
      {
          puts(node.getText());
      }
      
      public void caseTFalse(TFalse node)
      {
          puts(node.getText());
      }
      
      public void caseTHtmlTagEnd(THtmlTagEnd node)
      {
          puts(node.getText());
      }
      
      public void caseTInput(TInput node)
      {
          puts(node.getText());
      }
      
      public void caseTPosIntconst(TPosIntconst node)
      {
          puts(node.getText());
      }
      
      public void caseTNegIntconst(TNegIntconst node)
      {
          puts(node.getText());
      }
      
      public void caseTSelect(TSelect node)
      {
          puts(node.getText());
      }
      
      public void caseTType(TType node)
      {
          puts(node.getText());
      }
      
      public void caseTName(TName node)
      {
          puts(node.getText());
      }
      
      public void caseTText(TText node)
      {
          puts(node.getText());
      }
      
      public void caseTRadio(TRadio node)
      {
          puts(node.getText());
      }
      
      public void caseTLBrace(TLBrace node)
      {
          puts(node.getText());
      }
      
      public void caseTRBrace(TRBrace node)
      {
          puts(node.getText());
      }
      
      public void caseTAssign(TAssign node)
      {
          puts(node.getText());
      }
      
      public void caseTSemicolon(TSemicolon node)
      {
          puts(node.getText());
      }
      
      public void caseTLt(TLt node)
      {
          puts(node.getText());
      }
      
      public void caseTGt(TGt node)
      {
          puts(node.getText());
      }
      
      public void caseTLtSlash(TLtSlash node)
      {
          puts(node.getText());
      }
      
      public void caseTLtBracket(TLtBracket node)
      {
          puts(node.getText());
      }
      
      public void caseTGtBracket(TGtBracket node)
      {
          puts(node.getText());
      }
      
      public void caseTComment(TComment node)
      {
          puts(node.getText());
      }
      
      public void caseTLPar(TLPar node)
      {
          puts(node.getText());
      }
      
      public void caseTRPar(TRPar node)
      {
          puts(node.getText());
      }
      
      public void caseTLBracket(TLBracket node)
      {
          puts(node.getText());
      }
      
      public void caseTRBracket(TRBracket node)
      {
          puts(node.getText());
      }
      
      public void caseTComma(TComma node)
      {
          puts(node.getText());
      }
      
      public void caseTKeep(TKeep node)
      {
          puts(node.getText());
      }
      
      public void caseTRemove(TRemove node)
      {
          puts(node.getText());
      }
      
      public void caseTJoin(TJoin node)
      {
          puts(node.getText());
      }
      
      public void caseTEq(TEq node)
      {
          puts(node.getText());
      }
      
      public void caseTNeq(TNeq node)
      {
          puts(node.getText());
      }
      
      public void caseTLteq(TLteq node)
      {
          puts(node.getText());
      }
      
      public void caseTGteq(TGteq node)
      {
          puts(node.getText());
      }
      
      public void caseTNot(TNot node)
      {
          puts(node.getText());
      }
      
      public void caseTMinus(TMinus node)
      {
          puts(node.getText());
      }
      
      public void caseTPlus(TPlus node)
      {
          puts(node.getText());
      }
      
      public void caseTMult(TMult node)
      {
          puts(node.getText());
      }
      
      public void caseTDiv(TDiv node)
      {
          puts(node.getText());
      }
     
      
      
      public void caseTMod(TMod node)
      {
          puts(node.getText());
      }      
      
      public void caseTAnd(TAnd node)
      {
          puts(node.getText());
      }
      
      public void caseTOr(TOr node)
      {
          puts(node.getText());
      }
           
      public void caseTDot(TDot node)
      {
          puts(node.getText());
      }
      
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
          puts(node.getText());
      }
      
      public void caseTStringconst(TStringconst node)
      {
          
          puts(node.getText());
      }
      
      public void caseTMeta(TMeta node)
      {
          puts(node.getText());
      }
      
      public void caseTWhatever(TWhatever node)
      {
          String text = node.getText();          
          if(text.matches("[\\s\\t]*[\n][\\s\\t]*"))
          {
              puts(text);
          }
          else if(text.charAt(0) == '\n')
          {
              puts("\n");
              putsTab(text.substring(1));
          }
          else
          {
              puts(text);
          }
      }
      
      public void caseEOF(EOF node)
      {
          puts(node.getText());
      }



      public void caseAField(AField node)
      {
          if(node.getType() != null)
          {
              node.getType().apply(this);
          }
          
          puts(" ");
          if(node.getIdentifier() != null)
          {
              node.getIdentifier().apply(this);
          }          
          puts(";\n");
      }
            
      public void caseAVariable(AVariable node)
      {
          putsTab("");
          if(node.getType() != null)
          {
              node.getType().apply(this);
          }
          
          puts(" ");
          
          List<TIdentifier> ids = node.getIdentifier();          
          if(ids!=null)
          {              
              for(TIdentifier id : ids)
              {
                  id.apply(this);
                  puts(" ");
              }
          }          
          puts(";\n");
      }
            
      public void caseAIdentifiers(AIdentifiers node)
      {          
          List<TIdentifier> ids = node.getIdentifier();          
          if(ids!=null)
          {              
              for(TIdentifier id : ids)
              {
                  id.apply(this);
                  puts(" ");
              }
          }          

      }

      public void caseAIntType(AIntType node)
      {
          if(node.getInt() != null)
          {
              node.getInt().apply(this);
          }
      }
      
      public void caseABoolType(ABoolType node)
      {
          if(node.getBool() != null)
          {
              node.getBool().apply(this);
          }
      }
      public void caseAStringType(AStringType node)
      {
          if(node.getString() != null)
          {
              node.getString().apply(this);
          }
      }
      public void caseAVoidType(AVoidType node)
      {
          if(node.getVoid() != null)
          {
              node.getVoid().apply(this);
          }
      }
      public void caseASimpleType(ASimpleType node)
      {
          if(node.getType() != null)
          {
              node.getType().apply(this);
          }
      }
      
      public void caseATupleType(ATupleType node)
      {
          puts("tuple ");
          if(node.getIdentifier() != null)
          {
              node.getIdentifier().apply(this);
          }
      }
      
      public void caseAFunction(AFunction node)
      {
          puts("\n");
          putsTab("");
          if(node.getType() != null)
          {
              node.getType().apply(this);
          }
          puts(" ");
          
          if(node.getIdentifier() != null)
          {
              node.getIdentifier().apply(this);
          }
          puts("(");
          
          List<PArgument> arguments = node.getArgument();
          if(arguments != null)
          {          
              for(PArgument argument : arguments)
              {
                  argument.apply(this);
                  puts(" ");
              }
          }          
          puts(")\n");
          if(node.getCompoundstm() != null)
          {
              node.getCompoundstm().apply(this);
          }
      }
            
      public void caseAArguments(AArguments node)
      {
          List<PArgument> arguments = node.getArgument();
          if(arguments != null)
          {          
              for(PArgument argument : arguments)
              {
                  argument.apply(this);
                  puts(" ");
              }
          }    
      }
      
      public void caseAArgument(AArgument node)
      {
          if(node.getType() != null)
          {
              node.getType().apply(this);
          }
          puts(" ");
          if(node.getIdentifier() != null)
          {
              node.getIdentifier().apply(this);
          }
      }
           
      public void caseASession(ASession node)
      {
          puts("\n");
          putsTab("session " );
          if(node.getIdentifier() != null)
          {
              node.getIdentifier().apply(this);
          }
          puts("()\n");
          if(node.getCompoundstm() != null)
          {
              node.getCompoundstm().apply(this);
          }
      }
}
