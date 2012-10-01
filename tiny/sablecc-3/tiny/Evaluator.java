package tiny;

import tiny.parser.*;
import tiny.lexer.*;
import tiny.node.*;
import tiny.analysis.*;
import java.util.*;

public class Evaluator extends DepthFirstAdapter
{
  /* (static) eval function */
  public static int eval(Node ast)
  {
    Evaluator e = new Evaluator();
    ast.apply(e);
    return e.getValue(ast);
  }
    
  /* Hashtable, holding intermediate values for AST nodes */
  private Hashtable values = new Hashtable();

  /* Utility methods to set/get values for AST nodes */
  private void setValue(Node node, int value)
  { values.put(node, new Integer(value));
  }

  private int getValue(Node node)
  { /* gets and removes the associated value.
       This reduces memory pressure, but you should 
       replace "remove" with "get" if you intend to
       lookup the same value more than once (e.g.: in
       an interpreter). */

    Integer value = (Integer) values.get(node);
    return value.intValue();
  }

  /* We deal with each grammar alternative, one by one */

  /* AST root (hidden [start = exp;] production) */
  public void outStart(Start node)
  { setValue(node, getValue(node.getPExp())); }

  /* plus */ 
  public void outAPlusExp(APlusExp node)
  { 
     if (getValue(node.getL())==0)
     {
        setValue(node, getValue(node.getR())); 
     }
     else if (getValue(node.getR())==0)
     {
        setValue(node, getValue(node.getL())); 
     }
     else
     {
        setValue(node, getValue(node.getL()) + getValue(node.getR())); 
     }
  }
  
  /* minus */
  public void outAMinusExp(AMinusExp node)
  {
     if (getValue(node.getL())==0)
     {
        setValue(node, -getValue(node.getR())); 
     }
     else if (getValue(node.getR())==0)
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
     if (getValue(node.getL()) == 0 || getValue(node.getR()) == 0)
     {
        setValue(node, 0); 
     }
     else if (getValue(node.getL())==1)
     {
        setValue(node, getValue(node.getR())); 
     }
     else if (getValue(node.getL())==1)
     {
        setValue(node, getValue(node.getR())); 
     }
     else
     {
        setValue(node, getValue(node.getL()) * getValue(node.getR())); 
     }
  }

  /* div */
  public void outADivdExp(ADivdExp node)
  { 	  	  
	  if (getValue(node.getR())==0)
	  {
		  System.out.println("Attempting division by zero!");
		  System.exit(0);
	  }	 	 
	  else if (getValue(node.getL())==0)
	  {
        setValue(node, 0); 
	  }
	  else if (getValue(node.getR())==1)
	  {
        setValue(node, getValue(node.getL())); 
	  }
	  else
	  {
	     setValue(node, getValue(node.getL()) / getValue(node.getR())); 
	  }
  }
  
  /* mod */
  public void outAModExp(AModExp node)
  { 
     if (getValue(node.getR()) == 0)
     {
        System.out.println("Attempting division by zero!");
        System.exit(0);
     }
     else if (getValue(node.getL()) == 0)
     {
        setValue(node, 0);
     }
     else
     {
        setValue(node, getValue(node.getL()) % getValue(node.getR())); 
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
  { setValue(node, Math.abs(getValue(node.getR()))); }

  /* neg */
  public void outANegExp(ANegExp node)
  { setValue(node, -1*(getValue(node.getR()))); }
  
  /* identifier (!!!) */
  public void outAIdExp(AIdExp node)
  { throw new RuntimeException("I can't evaluate the value of an identifier!"); }
  
  /* number */
  public void outANumberExp(ANumberExp node)
  { setValue(node, Integer.parseInt(node.getNumber().getText())); }
}
