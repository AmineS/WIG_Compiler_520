package wig.weeder;

import java.util.*;

import com.sun.xml.internal.ws.wsdl.writer.document.Service;

import wig.parser.*;
import wig.lexer.*;
import wig.node.*;
import wig.analysis.*;


public class Weeder extends DepthFirstAdapter
{
    private Set<String> fHtmlsTuplesGlobalVariablesNames = new HashSet<String>(); // HTML const names + Tuple names + Variable names 
    private Set<String> fSessionNames = new HashSet<String>(); //Session names
    private Set<String> fSchemasNames = new HashSet<String>();
    private Set<String> fFunctionNames = new HashSet<String>(); // Function names
    private Set<String> fCurrentLocalVariableNames = new HashSet<String>();
    private Set<String> fInputFieldsNames =  new HashSet<String>();
    private Set<String> fHoleVariables = new HashSet<String>();

    public static void weed(Node node)
    {
        node.apply(new Weeder());
    }
    
    public void caseAHtml(AHtml node)
    {
        String name = node.getIdentifier().getText();
        if(fHtmlsTuplesGlobalVariablesNames.contains(name))
        {
            System.out.println("Error: Duplicate variable: " + node.getIdentifier().getText() + " at line " + node.getIdentifier().getLine());
        }
        else
        {
            fHtmlsTuplesGlobalVariablesNames.add(name);
        }
        
        for(PHtmlbody htmlbody : node.getHtmlbody())
        {
            htmlbody.apply(this);
        }
    }
    
    //Weeder
    public void caseASchema(ASchema node)
    {
        String name = node.getIdentifier().getText();
        if(fSchemasNames.contains(name))
        {
            System.out.println("Error: Duplicate schema: " + node.getIdentifier().getText() + " at line " + node.getIdentifier().getLine());
        }
        else
        {
            fSchemasNames.add(name);
        }

        Set<String> fieldsNames = new HashSet<String>();
        for(PField field : node.getField())
        {
            AField fieldImpl = (AField) field;
            String memberName = fieldImpl.getIdentifier().toString().trim();
            if(!fieldsNames.contains(memberName))
            {
                fieldsNames.add(memberName);
            }
            else
            {
                System.out.println("Error: Duplicate member " + memberName + " in Schema " + name + " declared at line " + node.getIdentifier().getLine()); 
            }
        }
        
        if(fieldsNames.isEmpty())
        {
            System.out.println("Error: Definition for schema " + name + " cannot be empty at line :" + node.getIdentifier().getLine()); 
        }
       
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }          
       
        List<PField> fields= node.getField();
        
        if(fields != null)
        {
            for(PField field : fields)
            {
                field.apply(this);
            }
        }
    }
    
    //Weeder
    public void caseAVariable(AVariable node)
    {
        if(node.parent().getClass().equals(AService.class))
        {
            for(TIdentifier identifier : node.getIdentifier())
            {
                if(fHtmlsTuplesGlobalVariablesNames.contains(identifier.getText()))
                {
                    System.out.println("Error: Duplicate variable: " + identifier.getText() + " at line " + identifier.getLine());
                }
                else
                {
                    fHtmlsTuplesGlobalVariablesNames.add(identifier.getText());
                }
            }
        }
        else
        {
            for(TIdentifier identifier : node.getIdentifier())
            {
                if(fCurrentLocalVariableNames.contains(identifier.getText()) && !fHtmlsTuplesGlobalVariablesNames.contains(identifier.getText()))
                {
                    System.out.println("Error: Duplicate local variable: " + identifier.getText() + " at line " + identifier.getLine());
                }
                else if(fHtmlsTuplesGlobalVariablesNames.contains(identifier.getText()))
                {
                    System.out.println("Error: Duplicate global variable: " + identifier.getText() + " at line " + identifier.getLine());
                }
                else
                {
                    fCurrentLocalVariableNames.add(identifier.getText());
                }
            }
        }
        
        if(node.getType().toString().trim().equals("void"))
        {
            System.out.println("Error: Variable cannot be of type void at line " + node.getIdentifier().getFirst().getLine());
        }
        
        if(node.getType() != null)
        {
            node.getType().apply(this);
        }
        
        List<TIdentifier> ids = node.getIdentifier();          
        if(ids!=null)
        {              
            for(TIdentifier id : ids)
            {
                id.apply(this);
            }
        } 
    }
    
    // Weeder
    public void outAFunction(AVariable node)
    {
        fCurrentLocalVariableNames.clear();
    }
    
    // Weeder
    public void outASession(ASession node)
    {
        fCurrentLocalVariableNames.clear();
    }
    
    // Weeder
    public void caseAInput(AInput node)
    {
        String leftValueName = node.getLvalue().toString().trim();
        String rightValueName = node.getIdentifier().getText();
        if(!fCurrentLocalVariableNames.contains(leftValueName) && !fHtmlsTuplesGlobalVariablesNames.contains(leftValueName))
        {
            System.out.println("Error: Variable " + leftValueName + " is not defined in global and local scope" + " at line " + node.getIdentifier().getLine());
        }
        if(!fInputFieldsNames.contains(rightValueName))
        {
            System.out.println("Error: Variable " + rightValueName + " is not a defined input field" + " at line " + node.getIdentifier().getLine());
        }
        node.getLvalue().apply(this);
        node.getIdentifier().apply(this);
    }
    
    // Weeder
    public void caseASession(ASession node)
    {
        String sessionName = node.getIdentifier().toString().trim();
        if(fSessionNames.contains(sessionName))
        {
            System.out.println("Error: Duplicate Session " + sessionName + " at line " + node.getIdentifier().getLine());
        }
        else
        {
            fSessionNames.add(sessionName);
        }
        
        ACompoundstm compoundStatement = (ACompoundstm) node.getCompoundstm();
        if((compoundStatement.getStm().isEmpty() || !(compoundStatement.getStm().getLast() instanceof AExitStm)))
        {
            System.out.println("Error Session " + node.getIdentifier().getText().trim() + " does not have a exit statement at line " + node.getIdentifier().getLine());
        }
        
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        if(node.getCompoundstm() != null)
        {
            node.getCompoundstm().apply(this);
        }

    }
    
    public void caseATupleType(ATupleType node)
    {
        String schema = node.toString().trim();
        if(!fSchemasNames.contains(schema))
        {
            System.out.println("Error: Schema " + schema + " is not defined");
        }
    }
    
    public void caseAInputHtmlbody(AInputHtmlbody node)
    {
        int nameCounter = 0;
        int typeCounter = 0;
        for(PInputattr inputattr : node.getInputattr())
        {
            if(inputattr instanceof ANameInputattr)
            {
                ANameInputattr nameInputattr = (ANameInputattr) inputattr;
                nameCounter++;
                if(nameCounter > 1)
                {
                    System.out.println("Error: Input field must have one name attribute at line: " + nameInputattr.getName().getLine());
                }
                fInputFieldsNames.add(nameInputattr.getAttr().toString().trim());
            }
            else if(inputattr instanceof ATypeInputattr)
            {
                ATypeInputattr typeInputattr = (ATypeInputattr) inputattr;
                typeCounter++;
                if(typeCounter > 1)
                {
                    System.out.println("Error: Input field must have one type attribute at line: " + typeInputattr.getType().getLine());
                }
            }
        }
        if(nameCounter == 0)
        {
            System.out.println("Error: Input field must have a name attribute at line: " + node.getInput().getLine());
        }
        if(typeCounter == 0)
        {
            System.out.println("Error: Input field must have a type attribute at line: " + node.getInput().getLine());
        }
    }
    
    public void caseASelectHtmlbody(ASelectHtmlbody node)
    {
        int nameCounter = 0;
        for(PInputattr inputattr : node.getInputattr())
        {
            AAttributeInputattr inputattr2 = (AAttributeInputattr) inputattr;
            AAssignAttribute assignAttribute = (AAssignAttribute) inputattr2.getAttribute();
            if(assignAttribute.getLeftAttr().toString().trim().equals("name"))
            {
                nameCounter++;
                if(nameCounter > 1)
                {
                    System.out.println("Error: Select field must have one name attribute at line: " + node.getSelectTag().getLine());
                }
                fInputFieldsNames.add(assignAttribute.getRightAttr().toString().trim().toString().trim());
            }
            else if(assignAttribute.getLeftAttr().toString().trim().equals("type"))
            {
                System.out.println("Error: Select field must have no type attribute at line: " + node.getSelectTag().getLine());
            }
        }
        
        if(nameCounter == 0)
        {
            System.out.println("Error: Select field must have one name attribute at line: " + node.getSelectTag().getLine());
        }
        
        for(PInputattr attr : node.getInputattr())
        {
            attr.apply(this);
        }
        
        for(PHtmlbody htmlBody : node.getHtmlbody())
        {
            htmlBody.apply(this);
        }
        
        node.getSelectTag().apply(this);
    }
    
    public void caseAFunction(AFunction node)
    {
        boolean hasReturnType = false;
        if(node.getType() instanceof AIntType || node.getType() instanceof ABoolType || node.getType() instanceof AStringType || node.getType() instanceof ATupleType)
        {
            hasReturnType = true;
        }
        ACompoundstm compoundStatement = (ACompoundstm) node.getCompoundstm();
        if(hasReturnType && (compoundStatement.getStm().isEmpty() || !(compoundStatement.getStm().getLast() instanceof AReturnexpStm)))
        {
            System.out.println("Error non void function " + node.getIdentifier().getText().trim() + " does not have a return statement at line " + node.getIdentifier().getLine());
        }
    }
    
    public void caseAPlug(APlug node)
    {
        // check if plugging to non-existing hole variable
        if (fHoleVariables.contains(node.getIdentifier().getText()))
        {
            return;
        }
        else
        {
            System.out.println("Error: Trying to plug to non-existing hole variable '" + node.getIdentifier().getText() + "' at line " + node.getIdentifier().getLine());
        }
    }
    
    public void caseAHoleHtmlbody(AHoleHtmlbody node)
    {
        // check if 2 or more hole variables have the same name
        if (fHoleVariables.contains(node.getIdentifier().getText()))
        {
            System.out.println("Error: Duplicate hole variable: " + node.getIdentifier().getText() + " at line " + node.getIdentifier().getLine());
        }
        else
        {        
            fHoleVariables.add(node.getIdentifier().getText());
        }
    }
    
    public void caseADivExp(ADivExp node)
    {
        // report error if division by zero
        if (node.getRight().toString().matches("[^0]*[0][^0]*"))
        {
            
            System.out.println("Error: Attempting division by zero: " + node.getLeft().toString().trim() + "/" + node.getRight());
        }
        node.getLeft().apply(this);
        node.getRight().apply(this);
    }
    
    public void caseATupleExp(ATupleExp node)
    {   
        Iterator<PFieldvalue> iter = node.getFieldvalue().iterator();
        while(iter.hasNext())
        {
            iter.next().apply(this);
        }
    }
   
    public void caseAService(AService node)
    {
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
    }
    
    public void caseATagStartHtmlbody(ATagStartHtmlbody node)
    {              
        node.getIdentifier().apply(this);
        for(PAttribute attribute : node.getAttribute())
        {
            attribute.apply(this);
        }
    }
    
    public void caseATagEndHtmlbody(ATagEndHtmlbody node)
    {
        node.getIdentifier().apply(this);
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
      
      
      public void caseANameInputattr(ANameInputattr node)
      {
          if(node.getAttr() != null)
          {
              node.getAttr().apply(this);              
          }
      }      
      @Override
      public void caseATypeInputattr(ATypeInputattr node)
      {
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
          if(node.getLeftAttr() != null)
          {
              node.getLeftAttr().apply(this);
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
     
      public void caseAShowStm(AShowStm node)
      {
          if(node.getDocument() != null)
          {
              node.getDocument().apply(this);
          }
          if(node.getReceive() != null)
          {
              node.getReceive().apply(this);
          }
      }
      
      public void caseAExitStm(AExitStm node)
      {
          node.getDocument().apply(this);
      }
      
      public void caseAReturnStm(AReturnStm node)
      {
      }
      
      public void caseAReturnexpStm(AReturnexpStm node)
      {
          node.getExp().apply(this);
      }
      
      public void caseAIfStm(AIfStm node)
      {
          node.getExp().apply(this);
          node.getStm().apply(this);
      }
      
      public void caseAIfelseStm(AIfelseStm node)
      {
          node.getExp().apply(this);
          node.getThenStm().apply(this);
          node.getElseStm().apply(this);
      }
      
      public void caseAWhileStm(AWhileStm node)
      {
          node.getExp().apply(this);
          node.getStm().apply(this);
      }
      
      public void caseACompStm(ACompStm node)
      {
          node.getCompoundstm().apply(this);
      }
      
      public void caseAExpStm(AExpStm node)
      {
          node.getExp().apply(this);
      }
      
      public void caseAIdDocument(AIdDocument node)
      {
          node.getIdentifier().apply(this);
      }
      
      public void caseAPlugDocument(APlugDocument node)
      {
          LinkedList<PPlug> plug_list;
          Iterator<PPlug> iter;

          node.getIdentifier().apply(this);
          plug_list = node.getPlug();
          iter = plug_list.iterator();
          
          while(iter.hasNext())
          {
             iter.next().apply(this);
          }
      }
      
      public void caseAReceive(AReceive node)
      {
          LinkedList<PInput> input_list;
          Iterator<PInput> iter;
          
          input_list = node.getInput();
          iter = input_list.iterator();

          while(iter.hasNext())
          {
             iter.next().apply(this);
          }
      }
      
      public void caseACompoundstm(ACompoundstm node)
      {
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
      }

      
      public void caseAPlugs(APlugs node)
      {
          LinkedList<PPlug> plug_list;
          Iterator<PPlug> iter;
          
          plug_list = node.getPlug();
          iter = plug_list.iterator();
          
          while(iter.hasNext())
          {
             iter.next().apply(this);
          }
      }

      public void caseAInputs(AInputs node)
      {
          LinkedList<PInput> input_list;
          Iterator<PInput> iter;
          
          input_list = node.getInput();
          iter = input_list.iterator();
          
          while(iter.hasNext())
          {
             iter.next().apply(this);
          }
      }
      
      
      public void caseAAssignExp(AAssignExp node)
      {
          node.getLvalue().apply(this);
          node.getRight().apply(this);
      }
      
      public void caseAOrExp(AOrExp node)
      {
          node.getLeft().apply(this);
          node.getRight().apply(this);
      }
      
      public void caseAAndExp(AAndExp node)
      {
          node.getLeft().apply(this);
          node.getRight().apply(this);
      }
            
      public void caseAEqExp(AEqExp node)
      {
          node.getLeft().apply(this);
          node.getRight().apply(this);
      }
            
      public void caseANeqExp(ANeqExp node)
      {
          node.getLeft().apply(this);
          node.getRight().apply(this);
      }
      
      public void caseALtExp(ALtExp node)
      {
          node.getLeft().apply(this);
          node.getRight().apply(this);
      }
      
      public void caseAGtExp(AGtExp node)
      {
          node.getLeft().apply(this);
          node.getRight().apply(this);
      }
      
      public void caseALteqExp(ALteqExp node)
      {
          node.getLeft().apply(this);
          node.getRight().apply(this);
      }
      
      public void caseAGteqExp(AGteqExp node)
      {
          node.getLeft().apply(this);
          node.getRight().apply(this);
      }
     
      
      public void caseAPlusExp(APlusExp node)
      {
          node.getLeft().apply(this);
          node.getRight().apply(this);
      }
      
      public void caseAMinusExp(AMinusExp node)
      {
          node.getLeft().apply(this);
          node.getRight().apply(this);
      }
      
      public void caseAMultExp(AMultExp node)
      {
          node.getLeft().apply(this);
          node.getRight().apply(this);
      }
      
      public void caseAModExp(AModExp node)
      {
          node.getLeft().apply(this);
          node.getRight().apply(this);
      }
      
      public void caseAJoinExp(AJoinExp node)
      {
          node.getLeft().apply(this);
          node.getRight().apply(this);
      }
      
      public void caseAKeepExp(AKeepExp node)
      {
          node.getLeft().apply(this);
          node.getIdentifier().apply(this);
      }

      public void caseARemoveExp(ARemoveExp node)
      {
          node.getLeft().apply(this);
          node.getIdentifier().apply(this);
      }
      
      public void caseAKeepManyExp(AKeepManyExp node)
      {
          LinkedList<TIdentifier> identifier_list;
          Iterator<TIdentifier> iter;
          
          node.getLeft().apply(this);
          identifier_list = node.getIdentifier();
          iter = identifier_list.iterator();
          
          while(iter.hasNext())
          {
              iter.next().apply(this);
          }
      }
      
      public void caseARemoveManyExp(ARemoveManyExp node)
      {
          LinkedList<TIdentifier> identifier_list;
          Iterator<TIdentifier> iter;
          
          node.getLeft().apply(this);
          identifier_list = node.getIdentifier();
          iter = identifier_list.iterator();

          while(iter.hasNext())
          {
              iter.next().apply(this);
          }
      }
      
      public void caseANotExp(ANotExp node)
      {
          node.getLeft().apply(this);
      }
      
      public void caseANegExp(ANegExp node)
      {
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

          while(iter.hasNext())
          {
              iter.next().apply(this);
          }
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
      

      public void caseAParenExp(AParenExp node)
      {
          node.getExp().apply(this);
      }
      
      public void caseAExps(AExps node)
      {
          for (PExp expression: node.getExp())
          {
              expression.apply(this);
          }
      }
      
      public void caseAQualifiedLvalue(AQualifiedLvalue node)
      {
          node.getLeft().apply(this);
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
          node.getExp().apply(this);
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
      }
      
      public void caseTBool(TBool node)
      {
      }
      
      public void caseTString(TString node)
      {
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
      }
      
      public void caseTNeq(TNeq node)
      {
      }
      
      public void caseTLteq(TLteq node)
      {
      }
      
      public void caseTGteq(TGteq node)
      {
      }
      
      public void caseTNot(TNot node)
      {
      }
      
      public void caseTMinus(TMinus node)
      {
      }
      
      public void caseTPlus(TPlus node)
      {
      }
      
      public void caseTMult(TMult node)
      {
      }
      
      public void caseTDiv(TDiv node)
      {
      }
     
      public void caseTMod(TMod node)
      {
      }      
      
      public void caseTAnd(TAnd node)
      {
      }
      
      public void caseTOr(TOr node)
      {
      }
           
      public void caseTDot(TDot node)
      {
      }
      
      public void caseTEol(TEol node)
      {
      }
      
      public void caseTBlank(TBlank node)
      {
      }
      
      public void caseTIdentifier(TIdentifier node)
      {
      }
      
      public void caseTStringconst(TStringconst node)
      {
      }
      
      public void caseTMeta(TMeta node)
      {
      }
      
      public void caseTWhatever(TWhatever node)
      {
      }
      
      public void caseEOF(EOF node)
      {
      }

      public void caseAField(AField node)
      {
          if(node.getType() != null)
          {
              node.getType().apply(this);
          }
          
          if(node.getIdentifier() != null)
          {
              node.getIdentifier().apply(this);
          }          
      }
            
            
      public void caseAIdentifiers(AIdentifiers node)
      {          
          List<TIdentifier> ids = node.getIdentifier();          
          if(ids!=null)
          {              
              for(TIdentifier id : ids)
              {
                  id.apply(this);
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
            
      public void caseAArguments(AArguments node)
      {
          List<PArgument> arguments = node.getArgument();
          if(arguments != null)
          {          
              for(PArgument argument : arguments)
              {
                  argument.apply(this);
              }
          }    
      }
      
      public void caseAArgument(AArgument node)
      {
          if(node.getType() != null)
          {
              node.getType().apply(this);
          }

          if(node.getIdentifier() != null)
          {
              node.getIdentifier().apply(this);
          }
      }
}
