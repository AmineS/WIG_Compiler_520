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
    
    if(e.hasValue(ast))
    {
       return new TNumber(Integer.toString(e.getValue(ast)));
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
  { values.put(node, new Integer(value));}

  private int getValue(Node node)
  { /* gets and removes the associated value.
       This reduces memory pressure, but you should 
       replace "remove" with "get" if you intend to
       lookup the same value more than once (e.g.: in
       an interpreter). */

    Integer value = (Integer) values.get(node);
    return value.intValue();
  }

  /* checks if the node has a value in the hash table */ 
  private boolean hasValue(Node node)
  { return null != values.get(node); }
  /* We deal with each grammar alternative, one by one */

  /* AST root (hidden [start = exp;] production) */
  public void outStart(Start node)
  { setValue(node, getValue(node.getPExp())); }

  /* plus */ 
  public void outAPlusExp(APlusExp node)
  { setValue(node, getValue(node.getL()) + getValue(node.getR())); }
  
  /* minus */
  public void outAMinusExp(AMinusExp node)
  { setValue(node, getValue(node.getL()) - getValue(node.getR())); }

  /* mult */
  public void outAMultExp(AMultExp node)
  { setValue(node, getValue(node.getL()) * getValue(node.getR())); }

  /* div */
  public void outADivdExp(ADivdExp node)
  { 	  	  
	  if (getValue(node.getR())==0)
	  {
		  System.out.println("Attempting division by zero!");
		  System.exit(0);
	  }	 	 
	  setValue(node, getValue(node.getL()) / getValue(node.getR())); 
  }
  
  /* mod */
  public void outAModExp(AModExp node)
  { setValue(node, getValue(node.getL()) % getValue(node.getR())); }
  
  /* exponentiation */
  public void outAExponExp(AExponExp node)
  { setValue(node, (int)(Math.pow(getValue(node.getL()), getValue(node.getR())))); }
  
  /* abs*/
  public void outAAbsExp(AAbsExp node)
  { setValue(node, Math.abs(getValue(node.getR()))); }

  /* neg*/
  public void outANegExp(ANegExp node)
  { setValue(node, -1*(getValue(node.getR()))); }
  
  /* identifier (!!!) */
  public void outAIdExp(AIdExp node)
  { return; }
  
  /* number */
  public void outANumberExp(ANumberExp node)
  { setValue(node, Integer.parseInt(node.getNumber().getText())); }
  
  private void replaceNodesWithValues(Node node)
  {
     //null check 
     if(node == null) return; 
     
     //check if there is a value for the node 
     Integer nodeValue = (Integer) this.values.get(node);     
     if(nodeValue != null)
     {
        //if there is an integer value then return the value 
        node.replaceBy(new TNumber(nodeValue.toString()));
     }
  }
}
