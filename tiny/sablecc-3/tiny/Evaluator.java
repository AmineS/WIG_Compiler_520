package tiny;

import tiny.parser.*;
import tiny.lexer.*;
import tiny.node.*;
import tiny.analysis.*;
import java.util.*;

public class Evaluator extends DepthFirstAdapter
{
  /* (static) eval function */
  public static Node eval(Node ast)
  {
    Evaluator e = new Evaluator();
    
    ast.apply(e);
    
    if(e.getValue(ast) != null)
    {
       return new ANumberExp(new TNumber(Integer.toString(e.getValue(ast))));
    }
    else
    {
       return ast;
    }    
  }
    
  /* Hashtable, holding intermediate values for AST nodes */
  private Hashtable values = new Hashtable();

  /* Utility methods to set/get values for AST nodes */
  private void setValue(Node node, int value)
  {
     if(node.getClass() == tiny.node.Start.class)
     {
        values.put(node, new Integer(value));
     }
     else
     {
        Node newNode = this.replaceNodesWithValues(node, value);
        values.put(newNode, new Integer(value));
     }
  }

  private Integer getValue(Node node)
  { /* gets and removes the associated value.
       This reduces memory pressure, but you should 
       replace "remove" with "get" if you intend to
       lookup the same value more than once (e.g.: in
       an interpreter). */

    return (Integer) values.get(node);
  }
  /* We deal with each grammar alternative, one by one */

  /* AST root (hidden [start = exp;] production) */
  public void outStart(Start node)
  { 
     if(getValue(node.getPExp()) == null) return;
     setValue(node, getValue(node.getPExp())); 
  }

  /* plus */ 
  public void outAPlusExp(APlusExp node)
  { 
     Integer rightNodeValue = getValue(node.getR());
     Integer leftNodeValue = getValue(node.getL());
               
     if(leftNodeValue != null && leftNodeValue==0)
     {
        node.replaceBy(node.getR());
     }
     else if(rightNodeValue != null && rightNodeValue==0)
     {
        node.replaceBy(node.getL()); 
     }
     else if(rightNodeValue == null || leftNodeValue == null)  
     {
        return;
     }
     else
     {
        setValue(node, leftNodeValue + rightNodeValue);
     }
  }
  
  /* minus */
  public void outAMinusExp(AMinusExp node)
  {
     if(getValue(node.getL())==0)
     {
        setValue(node, -getValue(node.getR())); 
     }
     else if(getValue(node.getR())==0)
     {
        setValue(node, getValue(node.getL())); 
     }
     else
     {
        setValue(node, getValue(node.getL()) - getValue(node.getR())); 
     }
  }

  /* mult */
  public void outAMultExp(AMultExp node)
  { 
      Integer rightNodeValue = getValue(node.getR());
      Integer leftNodeValue = getValue(node.getL());
                
      if(leftNodeValue != null && leftNodeValue == 0)
      {
         node.replaceBy(node.getL());
      }
      else if(rightNodeValue != null && rightNodeValue == 0)
      {
          node.replaceBy(node.getR()); 
      }
      else if(rightNodeValue != null && rightNodeValue == 1)
      {
          node.replaceBy(node.getL());
      }
      else if(leftNodeValue != null && leftNodeValue == 1)
      {
          node.replaceBy(node.getR());
      }
      else if(rightNodeValue == null || leftNodeValue == null)  
      {
         return;
      }
     else
     {
        setValue(node, getValue(node.getL()) * getValue(node.getR())); 
     }
  }

  /* div */
  public void outADivdExp(ADivdExp node)
  {
     Integer rightNodeValue = getValue(node.getR());
     Integer leftNodeValue = getValue(node.getL());
     
	  if (rightNodeValue != null && rightNodeValue==0)
	  {
		  System.out.println("Attempting division by zero!");
		  System.exit(0);
	  }	 	 
	  else if (leftNodeValue != null && leftNodeValue ==0)
	  {
        node.replaceBy(node.getL()); 
	  }
	  else if (rightNodeValue != null && rightNodeValue==1)
	  {
        node.replaceBy(node.getL()); 
	  }
	  else if (rightNodeValue == null || leftNodeValue == null)
	  {
	     return;
	  }
	  else
	  {
	     setValue(node, leftNodeValue / rightNodeValue); 
	  }
  }
  
  /* mod */
  public void outAModExp(AModExp node)
  {
     
     Integer rightNodeValue = getValue(node.getR());
     Integer leftNodeValue = getValue(node.getL());
     
     if(rightNodeValue!= null && rightNodeValue == 0)
     {
        System.out.println("Attempting division by zero!");
        System.exit(0);
     }
     else if(leftNodeValue != null && leftNodeValue == 0)
     {
        node.replaceBy(node.getL());
     }
     else if(leftNodeValue == null || rightNodeValue == null)
     {
        return;
     }
     else
     {
        setValue(node, leftNodeValue % rightNodeValue); 
     }
  }
  
  /* exponentiation */
  public void outAExponExp(AExponExp node)
  { 
     if (getValue(node.getL())==0)
     {
        setValue(node, 0);
     }
     else if (getValue(node.getR())==0)
     {
        setValue(node, 1);
     }
     else if (getValue(node.getL())==1)
     {
        setValue(node, 1);
     }
     else if (getValue(node.getR())==1)
     {
        setValue(node, getValue(node.getL()));
     }
     else
     {
        setValue(node, (int)(Math.pow(getValue(node.getL()), getValue(node.getR()))));
     }
  }
  
  /* abs*/
  public void outAAbsExp(AAbsExp node)
  {
      Integer rightNodeValue = getValue(node.getR());
      
      if(rightNodeValue == null) return;
      
      setValue(node, Math.abs(rightNodeValue)); 
  }

  /* neg */
  public void outANegExp(ANegExp node)
  { setValue(node, -1*(getValue(node.getR()))); }
  
  /* identifier (!!!) */
  public void outAIdExp(AIdExp node)
  { return; }
  
  /* number */
  public void outANumberExp(ANumberExp node)
  { setValue(node, Integer.parseInt(node.getNumber().getText())); }
  
  private Node replaceNodesWithValues(Node node, Integer nodeValue)
  {
     //null check 
     if(node == null) return null; 
     
     Node newNode = new ANumberExp(new TNumber(nodeValue.toString()));
        //if there is an integer value then return the value 
     node.replaceBy(newNode);
     
     return newNode;
  }
}
