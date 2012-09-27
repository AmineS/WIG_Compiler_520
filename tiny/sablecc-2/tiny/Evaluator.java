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

    Integer value = (Integer) values.remove(node);
    return value.intValue();
  }

  /* We deal with each grammar alternative, one by one */

  /* AST root (hidden [start = exp;] production) */
  public void outStart(Start node)
  { setValue(node, getValue(node.getPExp())); }

  /* plus */
  public void outAPlusExp(APlusExp node)
  { setValue(node, getValue(node.getExp()) + getValue(node.getFactor())); }
  
  /* minus */
  public void outAMinusExp(AMinusExp node)
  { setValue(node, getValue(node.getExp()) - getValue(node.getFactor())); }

  /* factor */
  public void outAFactorExp(AFactorExp node)
  { setValue(node, getValue(node.getFactor())); }

  /* mult */
  public void outAMultFactor(AMultFactor node)
  { setValue(node, getValue(node.getFactor()) * getValue(node.getTerm())); }

  /* div */
  public void outADivdFactor(ADivdFactor node)
  { setValue(node, getValue(node.getFactor()) / getValue(node.getTerm())); }

  /* term */
  public void outATermFactor(ATermFactor node)
  { setValue(node, getValue(node.getTerm())); }

  /* parentheses */
  public void outAParenTerm(AParenTerm node)
  { setValue(node, getValue(node.getExp())); }

  /* identifier (!!!) */
  public void outAIdTerm(AIdTerm node)
  { throw new RuntimeException("I can't evaluate the value of an identifier!"); }
  
  /* number */
  public void outANumberTerm(ANumberTerm node)
  { setValue(node, Integer.parseInt(node.getNumber().getText())); }
}
