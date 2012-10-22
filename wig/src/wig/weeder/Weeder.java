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

    public static void weed(Node node)
    {
        node.apply(new Weeder());
    }
    
    // Weeder
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
        
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        if(node.getCompoundstm() != null)
        {
            node.getCompoundstm().apply(this);
        }

    }
    
    // Weeder
    public void caseATupleType(ATupleType node)
    {
        String schema = node.toString().trim();
        if(!fSchemasNames.contains(schema))
        {
            System.out.println("Error: Schema " + schema + " is not defined");
        }
    }
    
    // Weeder
    public void caseAInputHtmlbody(AInputHtmlbody node)
    {
        int nameCounter = 0;
        int typeCounter = 0;
        for(PInputattr inputattr : node.getInputattr())
        {
            if (inputattr instanceof ANameInputattr)
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
    
    public void caseAFunction(AFunction node)
    {
        boolean hasReturnType = false;
        if(node.getType() instanceof AIntType || node.getType() instanceof ABoolType || node.getType() instanceof AStringType || node.getType() instanceof ATupleType)
        {
            hasReturnType = true;
        }
        ACompoundstm compoundStatement = (ACompoundstm) node.getCompoundstm();
        if(hasReturnType && (compoundStatement.getStm().size() == 0 || !(compoundStatement.getStm().getLast() instanceof AReturnexpStm)))
        {
            System.out.println("Error non void function " + node.getIdentifier().getText().trim() + " does not have a return statement at line " + node.getIdentifier().getLine());
        }
    }
    
    public void caseATupleExp(ATupleExp node)
    {   
        System.out.println("tuple {");
        Iterator<PFieldvalue> iter = node.getFieldvalue().iterator();
        while(iter.hasNext())
        {
            iter.next().apply(this);
        }
        System.out.println("}");
    }
   
    public void caseAService(AService node)
    {
        System.out.println("service\n{\n");
        
        
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
        
        System.out.println("}");
        
    }
    
    public void caseATagStartHtmlbody(ATagStartHtmlbody node)
    {              
        String id = node.getIdentifier().getText();
        if(id.equals("br"))
        {
            System.out.println("\n");
            
            System.out.println("");
            System.out.println("<");
            
        }
        else if (id.equals("body"))
        {
            System.out.println("\n");
            System.out.println("<");
            
        }
        else if(id.equals("td"))
        {
            System.out.println("<");
        }
        else
        {
            System.out.println("<");
        }
        node.getIdentifier().apply(this);
        if (node.getAttribute().size()!=0)
            System.out.println(" ");
        for(PAttribute attribute : node.getAttribute())
        {
            attribute.apply(this);
        }
        System.out.println(">");
    }
    
      public void caseATagEndHtmlbody(ATagEndHtmlbody node)
      {
          
          String id = node.getIdentifier().getText();
          
          if(id.equals("body"))
          {
              
              System.out.println("\n");
              System.out.println("</");
          }
          else if(id.equals("td"))
          {
              System.out.println("</");
          }
          else if(id.equals("table") || id.equals("tr") || id.equals("p"))
          {
              System.out.println("</");
          }
          else
          {
              System.out.println("</");              
          }
          node.getIdentifier().apply(this);
          System.out.println(">");
      }
      
      
      public void caseAHoleHtmlbody(AHoleHtmlbody node)
      {
          System.out.println("<[");
          node.getIdentifier().apply(this);
          System.out.println("]>");
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
      
      public void caseASelectHtmlbody(ASelectHtmlbody node)
      {
          System.out.println("\n");
          System.out.println("<");
          node.getSelectTag().apply(this);                        
          for(PInputattr attr : node.getInputattr())
          {
              attr.apply(this);
          }
          System.out.println(">");   
          
          for(PHtmlbody htmlBody : node.getHtmlbody())
          {
              htmlBody.apply(this);
          }
          
          System.out.println("</");
          node.getSelectTag().apply(this);
          System.out.println(">");
      }
      
      public void caseANameInputattr(ANameInputattr node)
      {
          System.out.println(" name=\""); 
          if(node.getAttr() != null)
          {
              node.getAttr().apply(this);              
          }
          System.out.println("\"");
      }      
      @Override
      public void caseATypeInputattr(ATypeInputattr node)
      {
          System.out.println(" type=\"");
          if(node.getInputtype() != null)
          {
              node.getInputtype().apply(this);
          }
          System.out.println("\"");
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
                    
          if(node.getLeftAttr() != null && node.getRightAttr() != null)
          {
              System.out.println("=");
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
   
      public void caseAEmptyStm(AEmptyStm node)
      {
          System.out.println(";\n");
      }
      
      public void caseAShowStm(AShowStm node)
      {
          System.out.println("show ");
          if(node.getDocument() != null)
          {
              node.getDocument().apply(this);
          }
          if(node.getReceive() != null)
          {
              node.getReceive().apply(this);
          }
          System.out.println(";\n");
      }
      
      public void caseAExitStm(AExitStm node)
      {
          System.out.println("exit ");
          node.getDocument().apply(this);
          System.out.println(";\n");
      }
      
      public void caseAReturnStm(AReturnStm node)
      {
          System.out.println("return;\n");
      }
      
      public void caseAReturnexpStm(AReturnexpStm node)
      {
          System.out.println("return ");
          node.getExp().apply(this);
          System.out.println("\n;");
      }
      
      public void caseAIfStm(AIfStm node)
      {
          System.out.println("\n");
          System.out.println("if(");
          node.getExp().apply(this);
          System.out.println(")\n");
          if(!(node.getStm() instanceof ACompStm))
          {
              System.out.println("{");
              System.out.println("\n");
          }
          
          node.getStm().apply(this);
          if(!(node.getStm() instanceof ACompStm))
          {
              System.out.println("}");
              System.out.println("\n");
          }
          
      }
      
      public void caseAIfelseStm(AIfelseStm node)
      {
          System.out.println("\n");
          System.out.println("if(");
          node.getExp().apply(this);
          System.out.println(")\n");
          if(!(node.getThenStm() instanceof ACompStm))
          {
              System.out.println("{");
              System.out.println("\n");
          }
          node.getThenStm().apply(this);
          if(!(node.getThenStm() instanceof ACompStm))
          {
              System.out.println("}");
          }
          System.out.println("else\n");
          if(!(node.getElseStm() instanceof ACompStm))
          {
              System.out.println("{");
              System.out.println("\n");
          }
          node.getElseStm().apply(this);
          if(!(node.getElseStm() instanceof ACompStm))
          {
              System.out.println("}");
              System.out.println("\n");
          }
      }
      
      public void caseAWhileStm(AWhileStm node)
      {
          System.out.println("\n");
          System.out.println("while(");
          node.getExp().apply(this);
          System.out.println(")\n");
          if(!(node.getStm() instanceof ACompStm))
          {
              System.out.println("{");
              System.out.println("\n");
          }
          node.getStm().apply(this);
          if(!(node.getStm() instanceof ACompStm))
          {
              System.out.println("}");
              System.out.println("\n");
          }
      }
      
      public void caseACompStm(ACompStm node)
      {
          node.getCompoundstm().apply(this);
      }
      
      public void caseAExpStm(AExpStm node)
      {
          System.out.println("");
          node.getExp().apply(this);
          System.out.println(";\n");
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

          System.out.println("plug ");
          node.getIdentifier().apply(this);
          System.out.println("[");
          
          plug_list = node.getPlug();
          iter = plug_list.iterator();
          counter = 0;
          plug_list_size = plug_list.size();
          
          while(iter.hasNext())
          {
             iter.next().apply(this);
             if(counter!=plug_list_size-1)
                 System.out.println(",");
          }
          System.out.println("] ");     

      }
      
      public void caseAReceive(AReceive node)
      {
          LinkedList<PInput> input_list;
          Iterator<PInput> iter;
          int input_list_size, counter;
          System.out.println("receive");
          
          input_list = node.getInput();
          iter = input_list.iterator();
          counter = 0;
          input_list_size = input_list.size();

          System.out.println("[");
          while(iter.hasNext())
          {
             iter.next().apply(this);
             if(counter!=input_list_size-1)
                 System.out.println(",");
          }
          System.out.println("]");

          
      }
      
      public void caseACompoundstm(ACompoundstm node)
      {
          LinkedList<PStm> stm_list = node.getStm();
          LinkedList<PVariable> var_list = node.getVariable();
          Iterator<PStm> stm_iter = stm_list.iterator();
          Iterator<PVariable> var_iter = var_list.iterator();          
          
          System.out.println("{\n");
          
          while(var_iter.hasNext())
          {
              var_iter.next().apply(this);
          }
          
          while(stm_iter.hasNext())
          {
              stm_iter.next().apply(this);
          }
          
          System.out.println("}\n");
          
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
                 System.out.println(",");
          }
      }
      
      public void caseAPlug(APlug node)
      {
          node.getIdentifier().apply(this);
          System.out.println("=");
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
                 System.out.println(",");
          }
      }
      
      
      public void caseAAssignExp(AAssignExp node)
      {
          node.getLvalue().apply(this);
          System.out.println(" = ");
          node.getRight().apply(this);
      }
      
      public void caseAOrExp(AOrExp node)
      {
          node.getLeft().apply(this);
          System.out.println(" || ");
          node.getRight().apply(this);
      }
      
      public void caseAAndExp(AAndExp node)
      {
          node.getLeft().apply(this);
          System.out.println("&&");
          node.getRight().apply(this);
      }
            
      public void caseAEqExp(AEqExp node)
      {
          node.getLeft().apply(this);
          System.out.println("==");
          node.getRight().apply(this);
      }
            
      public void caseANeqExp(ANeqExp node)
      {
          node.getLeft().apply(this);
          System.out.println("!=");
          node.getRight().apply(this);
      }
      
      public void caseALtExp(ALtExp node)
      {
          node.getLeft().apply(this);
          System.out.println("<");
          node.getRight().apply(this);
      }
      
      public void caseAGtExp(AGtExp node)
      {
          node.getLeft().apply(this);
          System.out.println(">");
          node.getRight().apply(this);
      }
      
      public void caseALteqExp(ALteqExp node)
      {
          node.getLeft().apply(this);
          System.out.println("<=");
          node.getRight().apply(this);
      }
      
      public void caseAGteqExp(AGteqExp node)
      {
          node.getLeft().apply(this);
          System.out.println(">=");
          node.getRight().apply(this);
      }
     
      
      public void caseAPlusExp(APlusExp node)
      {
          node.getLeft().apply(this);
          System.out.println("+");
          node.getRight().apply(this);
      }
      
      public void caseAMinusExp(AMinusExp node)
      {
          node.getLeft().apply(this);
          System.out.println("-");
          node.getRight().apply(this);
      }
      
      public void caseAMultExp(AMultExp node)
      {
          node.getLeft().apply(this);
          System.out.println("*");
          node.getRight().apply(this);
      }

      public void caseADivExp(ADivExp node)
      {
          node.getLeft().apply(this);
          System.out.println("/");
          node.getRight().apply(this);
      }
      
      public void caseAModExp(AModExp node)
      {
          node.getLeft().apply(this);
          System.out.println("%");
          node.getRight().apply(this);
      }
      
      public void caseAJoinExp(AJoinExp node)
      {
          node.getLeft().apply(this);
          System.out.println("<<");
          node.getRight().apply(this);
      }
      
      public void caseAKeepExp(AKeepExp node)
      {
          node.getLeft().apply(this);
          System.out.println(" keep ");
          node.getIdentifier().apply(this);
      }

      public void caseARemoveExp(ARemoveExp node)
      {
          node.getLeft().apply(this);
          System.out.println(" remove ");
          node.getIdentifier().apply(this);
      }
      
      public void caseAKeepManyExp(AKeepManyExp node)
      {
          int counter,linked_list_size;
          LinkedList<TIdentifier> identifier_list;
          Iterator<TIdentifier> iter;
          
          node.getLeft().apply(this);
          System.out.println("keep");
          identifier_list = node.getIdentifier();
          linked_list_size = identifier_list.size();
          iter = identifier_list.iterator();
          counter = 0;
          
          System.out.println("(");
          while(iter.hasNext())
          {
              iter.next().apply(this);
              if (counter!=linked_list_size-1)
                  System.out.println(",");
              counter++;
          }
          System.out.println(")");
      }
      
      public void caseARemoveManyExp(ARemoveManyExp node)
      {
          int counter,linked_list_size;
          LinkedList<TIdentifier> identifier_list;
          Iterator<TIdentifier> iter;
          
          node.getLeft().apply(this);
          System.out.println("remove");
          identifier_list = node.getIdentifier();
          linked_list_size = identifier_list.size();
          iter = identifier_list.iterator();
          counter = 0;

          System.out.println("(");
          while(iter.hasNext())
          {
              iter.next().apply(this);
              if (counter!=linked_list_size-1)
                  System.out.println(",");
              counter++;
          }
          System.out.println(")");
      }
      
      public void caseANotExp(ANotExp node)
      {
          System.out.println("!");
          node.getLeft().apply(this);
      }
      
      public void caseANegExp(ANegExp node)
      {
          System.out.println("-");
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

          System.out.println("(");
          while(iter.hasNext())
          {
              iter.next().apply(this);
          }
          System.out.println(")");
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
          System.out.println("(");
          node.getExp().apply(this);
          System.out.println(")");
      }
      
      public void caseAExps(AExps node)
      {
          for (PExp expression: node.getExp())
          {
              expression.apply(this);
              System.out.println(",");
          }
      }
      
      public void caseAQualifiedLvalue(AQualifiedLvalue node)
      {
          node.getLeft().apply(this);
          System.out.println(".");
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
          System.out.println(" = ");
          node.getExp().apply(this);
      }
      
      public void caseTService(TService node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTConst(TConst node)
      {
          System.out.println(node.getText());
      }

      public void caseTHtml(THtml node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTHtmlTagStart(THtmlTagStart node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTSchema(TSchema node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTSession(TSession node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTShow(TShow node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTExit(TExit node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTReturn(TReturn node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTIf(TIf node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTElse(TElse node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTWhile(TWhile node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTPlug(TPlug node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTReceive(TReceive node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTInt(TInt node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTBool(TBool node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTString(TString node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTVoid(TVoid node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTTuple(TTuple node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTTrue(TTrue node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTFalse(TFalse node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTHtmlTagEnd(THtmlTagEnd node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTInput(TInput node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTPosIntconst(TPosIntconst node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTNegIntconst(TNegIntconst node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTSelect(TSelect node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTType(TType node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTName(TName node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTText(TText node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTRadio(TRadio node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTLBrace(TLBrace node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTRBrace(TRBrace node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTAssign(TAssign node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTSemicolon(TSemicolon node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTLt(TLt node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTGt(TGt node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTLtSlash(TLtSlash node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTLtBracket(TLtBracket node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTGtBracket(TGtBracket node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTComment(TComment node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTLPar(TLPar node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTRPar(TRPar node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTLBracket(TLBracket node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTRBracket(TRBracket node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTComma(TComma node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTKeep(TKeep node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTRemove(TRemove node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTJoin(TJoin node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTEq(TEq node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTNeq(TNeq node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTLteq(TLteq node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTGteq(TGteq node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTNot(TNot node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTMinus(TMinus node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTPlus(TPlus node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTMult(TMult node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTDiv(TDiv node)
      {
          System.out.println(node.getText());
      }
     
      
      
      public void caseTMod(TMod node)
      {
          System.out.println(node.getText());
      }      
      
      public void caseTAnd(TAnd node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTOr(TOr node)
      {
          System.out.println(node.getText());
      }
           
      public void caseTDot(TDot node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTEol(TEol node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTBlank(TBlank node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTIdentifier(TIdentifier node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTStringconst(TStringconst node)
      {
          
          System.out.println(node.getText());
      }
      
      public void caseTMeta(TMeta node)
      {
          System.out.println(node.getText());
      }
      
      public void caseTWhatever(TWhatever node)
      {
          String text = node.getText();          
          if(text.matches("[\\s\\t]*[\n][\\s\\t]*"))
          {
              System.out.println(text);
          }
          else if(text.charAt(0) == '\n')
          {
              System.out.println("\n");
              System.out.println(text.substring(1));
          }
          else
          {
              System.out.println(text);
          }
      }
      
      public void caseEOF(EOF node)
      {
          System.out.println(node.getText());
      }



      public void caseAField(AField node)
      {
          if(node.getType() != null)
          {
              node.getType().apply(this);
          }
          
          System.out.println(" ");
          if(node.getIdentifier() != null)
          {
              node.getIdentifier().apply(this);
          }          
          System.out.println(";\n");
      }
            
            
      public void caseAIdentifiers(AIdentifiers node)
      {          
          List<TIdentifier> ids = node.getIdentifier();          
          if(ids!=null)
          {              
              for(TIdentifier id : ids)
              {
                  id.apply(this);
                  System.out.println(" ");
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
                  System.out.println(" ");
              }
          }    
      }
      
      public void caseAArgument(AArgument node)
      {
          if(node.getType() != null)
          {
              node.getType().apply(this);
          }
          System.out.println(" ");
          if(node.getIdentifier() != null)
          {
              node.getIdentifier().apply(this);
          }
      }
      
      
           
}
