package wig.weeder;

import java.util.*;

import wig.node.*;
import wig.analysis.*;


public class Weeder extends DepthFirstAdapter
{
    private Set<String> fHtmlsGlobalVariablesNames = new HashSet<String>(); // HTML const names + Tuple names + Variable names 
    private Set<String> fSessionNames = new HashSet<String>(); //Session names
    private Set<String> fSchemasNames = new HashSet<String>();
    private Set<String> fCurrentLocalVariableNames = new HashSet<String>();
    private Set<String> fHoleVariables = new HashSet<String>();
    private Set<String> fInputVariables = new HashSet<String>();
    private Set<String> fInputVariablesSameHtml = new HashSet<String>();
    private static boolean fErrorPresent = false;
    private boolean inAHtml = false;
    /**
     * Weeder
     * @param node
     */
    public static void weed(Node node)
    {
        node.apply(new Weeder());
        if (fErrorPresent)
        {
            System.exit(1);
        }
    }

   
    /**
     * Make sure no html's have the same name
     */
    public void caseAHtml(AHtml node)
    {
        String name = node.getIdentifier().getText();
        
        if (node.getIdentifier().getText().toLowerCase().equals("list") || node.getIdentifier().getText().toLowerCase().equals("Return"))
        {
            System.out.println("Reserved word \"" + node.getIdentifier().getText() + "\" is being used as a html const name. Line no:" + node.getIdentifier().getLine());
            System.exit(-1);
        }
        
        if(fHtmlsGlobalVariablesNames.contains(name))
        {
            System.out.println("Error: Duplicate variable: " + node.getIdentifier().getText() + " at line " + node.getIdentifier().getLine());
            fErrorPresent = true;
        }
        else
        {
            fHtmlsGlobalVariablesNames.add(name);
        }
        
        for(PHtmlbody htmlbody : node.getHtmlbody())
        {
            htmlbody.apply(this);
        }
        outAHtml(node);
    }
    
    public void outAHtml(AHtml node)
    {
        fInputVariablesSameHtml.clear();
    }
    
    /**
     * Make sure:
     * no schema have the same name
     * No memebers have the same name
     * No schema definition is empty 
     */
    public void caseASchema(ASchema node)
    {
        String name = node.getIdentifier().getText();
        if(fSchemasNames.contains(name))
        {
            System.out.println("Error: Duplicate schema: " + node.getIdentifier().getText() + " at line " + node.getIdentifier().getLine());
            fErrorPresent = true;
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
                fErrorPresent = true;
            }
        }
        
        if(fieldsNames.isEmpty())
        {
            System.out.println("Error: Definition for schema " + name + " cannot be empty at line :" + node.getIdentifier().getLine()); 
            fErrorPresent = true;
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
    
    /**
     * Make sure:
     * that global as well as local variables do not have conflicting names
     * that variables are not declared with void type 
     */
    public void caseAVariable(AVariable node)
    {
        if(node.parent().getClass().equals(AService.class))
        {
            for(TIdentifier identifier : node.getIdentifier())
            {
                if (identifier.getText().toLowerCase().equals("list") || identifier.getText().equals("Return"))
                {
                    System.out.println("Reserved word \"" + identifier.getText() + "\" or  is being used as a variable name. Line no:" + identifier.getLine());
                    System.exit(-1);
                }
                
                if(fHtmlsGlobalVariablesNames.contains(identifier.getText()))
                {
                    System.out.println("Error: Duplicate global variable: " + identifier.getText() + " at line " + identifier.getLine());
                    fErrorPresent = true;
                }
                else
                {
                    fHtmlsGlobalVariablesNames.add(identifier.getText());
                }
            }
        }
        else
        {
            for(TIdentifier identifier : node.getIdentifier())
            {
                if (identifier.getText().equals("Return"))
                {
                    System.out.println("Reserved word \"Return\" is being used as a variable name. Line no:" + identifier.getLine());
                    System.exit(-1);
                }
                if (identifier.getText().toLowerCase().equals("list"))
                {
                    System.out.println("Reserved word \"" + identifier.getText() + "\" or  is being used as a variable name. Line no:" + identifier.getLine());
                    System.exit(-1);
                }
                if(fCurrentLocalVariableNames.contains(identifier.getText()) && !fHtmlsGlobalVariablesNames.contains(identifier.getText()))
                {
                    System.out.println("Error: Duplicate local variable: " + identifier.getText() + " at line " + identifier.getLine());
                    fErrorPresent = true;
                }
                else if(fHtmlsGlobalVariablesNames.contains(identifier.getText()))
                {
                    System.out.println("Error: Duplicate global variable: " + identifier.getText() + " at line " + identifier.getLine());
                    fErrorPresent = true;
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
            fErrorPresent = true;
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
    
    /**
     * Clear local variable names Set data structure when leaving a function 
     * @param node - a variable node
     */
    public void outAFunction(AVariable node)
    {
        fCurrentLocalVariableNames.clear();
    }
    
    /**
     * Clear local variables when leaving a session
     */
    public void outASession(ASession node)
    {
        fCurrentLocalVariableNames.clear();
    }
    
    /**
     * Check there are no conflicts for input tags attributes and receive constructs
     */
    public void caseAInput(AInput node)
    {
        String leftValueName = node.getLvalue().toString().trim();
        
        // check if variable name already exists
        if(node.getLvalue() instanceof ASimpleLvalue && !fCurrentLocalVariableNames.contains(leftValueName) && !fHtmlsGlobalVariablesNames.contains(leftValueName))
        {
            System.out.println("Error: Variable " + leftValueName + " is not defined in global and local scope" + " at line " + node.getIdentifier().getLine());
            fErrorPresent = true;
        }
        else if(node.getLvalue() instanceof AQualifiedLvalue)
        {
            AQualifiedLvalue lQValue = (AQualifiedLvalue) node.getLvalue();
            String tupleName = lQValue.getLeft().getText().trim();
            if(!fCurrentLocalVariableNames.contains(tupleName) && !fHtmlsGlobalVariablesNames.contains(tupleName))
            {
                System.out.println("Error: Variable " + leftValueName + " is not defined in global and local scope" + " at line " + node.getIdentifier().getLine());
                fErrorPresent = true;
            }
        }
        
        // check whether, in a receive construct, the identifier of an input corresponds to an inputattribute 
        if (!fInputVariables.contains(node.getIdentifier().toString()))
        {
            System.out.println("Error: The input attribute '" + node.getIdentifier().getText() + "' you are trying to receive from at line no. " + node.getIdentifier().getLine() + " does not exist.");
            fErrorPresent = true;
        }
        
        node.getLvalue().apply(this);
        node.getIdentifier().apply(this);
    }
    
    /**
     * Check for duplicate sessions and that session always has an exit statement
     */
    public void caseASession(ASession node)
    {
        String sessionName = node.getIdentifier().toString().trim();
        
        if (sessionName.equals("destroy"))
        {
            System.out.println("A session cannot be named \"destroy\". Line no:" + node.getIdentifier().getLine());
            fErrorPresent = true;
        }
        
        if(fSessionNames.contains(sessionName))
        {
            System.out.println("Error: Duplicate Session " + sessionName + " at line " + node.getIdentifier().getLine());
            fErrorPresent = true;
        }
        else
        {
            fSessionNames.add(sessionName);
        }
        
        ACompoundstm compoundStatement = (ACompoundstm) node.getCompoundstm();
        if((compoundStatement.getStm().isEmpty() || !(compoundStatement.getStm().getLast() instanceof AExitStm)))
        {
            System.out.println("Error Session " + node.getIdentifier().getText().trim() + " does not have a exit statement at line " + node.getIdentifier().getLine());
            fErrorPresent = true;
        }
        
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
    
    /**
     * Check whether a schema being used by a tuple is defined
     */
    public void caseATupleType(ATupleType node)
    {
        String schema = node.toString().trim();
        if(!fSchemasNames.contains(schema))
        {
            System.out.println("Error: Schema " + schema + " is not defined");
            fErrorPresent = true;
        }
    }
    
    public void caseAInputHtmlbody(AInputHtmlbody node)
    {
        for(PInputattr inputAttr : node.getInputattr())
        {
            inputAttr.apply(this);
        } 
    }
    
    public void caseASelectHtmlbody(ASelectHtmlbody node)
    {  
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
    
    /**
     * Check whether non-void functions have a return statement
     */
    public void caseAFunction(AFunction node)
    {
        boolean hasReturnType = false;
        // check if function should have return statement
        if(node.getType() instanceof AIntType || node.getType() instanceof ABoolType || node.getType() instanceof AStringType || node.getType() instanceof ATupleType)
        {
            hasReturnType = true;
        }
        
        ACompoundstm compoundStatement = (ACompoundstm) node.getCompoundstm();

        // check that a function body does not have a show/exit statement
        LinkedList<PStm> stmL = compoundStatement.getStm();
        for (PStm ps: stmL)
        {
            if (ps instanceof AShowStm)
            {
                System.out.println("show statement not allowed in functions!");
                fErrorPresent = true;
            } 
            if (ps instanceof AExitStm)
            {
                System.out.println("exit statement not allowed in functions!");
                fErrorPresent = true;
            }
        }
        
        // check for return statement
        boolean hasReturnStatement;
   
        if(hasReturnType && (compoundStatement.getStm().isEmpty() || !(compoundStatement.getStm().getLast() instanceof AReturnexpStm)))
        {
            LinkedList<PStm> stmList = compoundStatement.getStm();
            hasReturnStatement = false;

            for (PStm stm: stmList)
            {
                /*
                 * Check if there are return statements in if/ ifelse/ while statements. 
                 */
                               
                if (stm instanceof AIfStm)
                {
                    AIfStm ifStm = (AIfStm) stm;
                    if (ifStm.getStm() instanceof ACompStm)
                    {
                        ACompStm compStm = (ACompStm) ifStm.getStm();
                        if (((ACompoundstm)compStm.getCompoundstm()).getStm().getLast() instanceof AReturnexpStm)
                        {
                            hasReturnStatement = true;
                            break;
                        }                        
                    }
                    else if (ifStm.getStm() instanceof AReturnexpStm)
                    {
                        hasReturnStatement = true;
                        break;
                    }
                }
                else if (stm instanceof AIfelseStm)
                {
                    AIfelseStm ifelseStm = (AIfelseStm) stm;
                    
                    // check thenstm
                    if (ifelseStm.getThenStm() instanceof AReturnexpStm)
                    {
                        hasReturnStatement = true;
                        break;
                    }
                    else if (ifelseStm.getThenStm() instanceof ACompStm)
                    {
                        ACompStm thenComp = (ACompStm) ifelseStm.getThenStm();
                        if (((ACompoundstm)thenComp.getCompoundstm()).getStm().getLast() instanceof AReturnexpStm)
                        {
                            hasReturnStatement = true;
                            break;
                        }
                    }
                    
                    // now check elsestm
                    if (ifelseStm.getElseStm() instanceof AReturnexpStm)
                    {
                        hasReturnStatement = true;
                        break;
                    }
                    else if (ifelseStm.getElseStm() instanceof ACompStm)
                    {
                        ACompStm thenComp = (ACompStm) ifelseStm.getElseStm();
                        if (((ACompoundstm)thenComp.getCompoundstm()).getStm().getLast() instanceof AReturnexpStm)
                        {
                            hasReturnStatement = true;
                            break;
                        }
                    }
                }
                else if (stm instanceof AWhileStm)
                {
                    AWhileStm whileStm = (AWhileStm) stm;
                    if (whileStm.getStm() instanceof ACompStm)
                    {
                        ACompStm compStm = (ACompStm) whileStm.getStm();
                        if (((ACompoundstm)compStm.getCompoundstm()).getStm().getLast() instanceof AReturnexpStm)
                        {
                            hasReturnStatement = true;
                            break;
                        }     
                    }
                    else if (whileStm.getStm() instanceof AReturnexpStm)
                    {
                        hasReturnStatement = true;
                        break;
                    }               
                }
            }
        }
        else
        {
            hasReturnStatement = true;
        }
        
        if (!hasReturnStatement)
        {
            System.out.println("Error: Function " + node.getIdentifier().getText().trim() + "does not have a return type. Line no:" + node.getIdentifier().getLine());
        }
        outAFunction(node);
    }
    
    /**
     * Ensure plugs always plug to existing hole variables
     */
    public void caseAPlug(APlug node)
    {
        // 
        if (fHoleVariables.contains(node.getIdentifier().getText()))
        {
            return;
        }
        else
        {
            System.out.println("Error: Trying to plug to non-existing hole variable '" + node.getIdentifier().getText() + "' at line " + node.getIdentifier().getLine());
            fErrorPresent = true;
        }
    }
    
    /**
     * Check for conflicts between hole variable names
     */
    public void caseAHoleHtmlbody(AHoleHtmlbody node)
    {
        if (!fHoleVariables.contains(node.getIdentifier().getText()))
        {        
            fHoleVariables.add(node.getIdentifier().getText());
        }
    }
    
    /**
     * Check for division by zero
     */
    public void caseADivExp(ADivExp node)
    {
        // report error if division by zero
        if (node.getRight().toString().matches("0"))
        {
            System.out.println("Error: Attempting division by zero: " + node.getLeft().toString().trim() + "/" + node.getRight());
            fErrorPresent = true;
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
      
      /**
       * Check for conflicts in attribute nams in input tags 
       */
      public void caseANameInputattr(ANameInputattr node)
      {
          if (fInputVariablesSameHtml.contains(node.getAttr().toString().replace("\"", "")))
          {
              //System.out.println("Error: Attribute '" + node.getAttr().toString().replace("\"", "") + "' in input tag at line " + node.getName().getLine() + " for " + node.getName().getText() + " already exists in the same html const!");
              //fErrorPresent = true;
          }
          else
          {
              fInputVariables.add(node.getAttr().toString().replace("\"", ""));
              fInputVariablesSameHtml.add(node.getAttr().toString().replace("\"", ""));
          }
          
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
          outACompoundstm(node);
      }
      
      public void outACompoundstm(ACompoundstm node)
      {
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
          if (node.getRight() instanceof AJoinExp)
          {
              System.out.println("Chained joins not allowed.");
              fErrorPresent = true;
          }
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

      public void caseTIdentifier(TIdentifier identifier)
      {
          
          if (identifier.getText().equals("Return") || identifier.getText().toLowerCase().equals("list"))
          {
              System.out.println("Reserved word \"Return\" is being used as a variable name. Line no:" + identifier.getLine());
              System.exit(-1);
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
