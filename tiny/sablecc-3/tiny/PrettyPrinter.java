package tiny;

import tiny.parser.*;
import tiny.lexer.*;
import tiny.node.*;
import tiny.analysis.*;
import java.util.*;

public class PrettyPrinter extends DepthFirstAdapter 
{
  public static void print(Node node)
  {
      node.apply(new PrettyPrinter());
  }

  protected void puts(String s) 
  {
      System.out.print(s);
      System.out.flush();
  }

  /**
   * Addition
   * @param APlusExp - an expression with addition
   */
  public void caseAPlusExp(APlusExp node) 
  {
      // check if start node
      if (node.parent().getClass() == tiny.node.Start.class)
      {
          node.getL().apply(this);
          puts("+");
          node.getR().apply(this);
          return;
      }
      
      // check for precedence of parent node
	  if (isLowerPrecedence((PExp)(node),(PExp)(node.parent())))
	  {
		  puts("(");
	  }
	  
	  // print values/identifiers and operator
      node.getL().apply(this);
      puts("+");
      node.getR().apply(this);
      
      if (isLowerPrecedence((PExp)node,(PExp)node.parent()))
      {
    	  puts(")");
      }      
  }

  /**
   * Subtraction
   * @param AMinusExp - An expression with subtraction
   */
  public void caseAMinusExp(AMinusExp node) 
  { 
      // check if start node
      if (node.parent().getClass() == tiny.node.Start.class)
      {
          node.getL().apply(this);
          puts("-");
          node.getR().apply(this);
          return;
      }
      
      // check for precedence of parent node
	  if (isLowerPrecedence((PExp)(node),(PExp)(node.parent())))
	  {
		  puts("(");
	  }
	  
	  // print out the values/identifiers and the operator
      node.getL().apply(this);
      puts("-");
      node.getR().apply(this);
      
      if (isLowerPrecedence((PExp)node,(PExp)node.parent()))
      {
    	  puts(")");
      }   
  }

  /**
   * Finding absolute value
   * @param AAbsExp - an expression with abs 
   */
  public void caseAAbsExp(AAbsExp node) 
  {
	  puts("abs(");
	  node.getR().apply(this);
	  puts(")");
  }
  
  /**
   * Multiplication
   * @param AMultExp - an expression with multiplication
   */
  public void caseAMultExp(AMultExp node) 
  { 
      // check if start node
	  if (node.parent().getClass()==tiny.node.Start.class)
	  {
	      node.getL().apply(this);
	      puts("*");
	      node.getR().apply(this);
	      return;
	  }
	  
	  // check for precedence of parent node
	  if (isLowerPrecedence((PExp)(node),(PExp)(node.parent())))
	  {
		  puts("(");
	  }
	  
	  // print the values/identifiers and the operator
      node.getL().apply(this);
      puts("*");
      node.getR().apply(this);
      
      if (isLowerPrecedence((PExp)node,(PExp)node.parent()))
      {
    	  puts(")");
      }
  }

  /**
   * Division
   * @param ADivdExp - an expression with division
   */
  public void caseADivdExp(ADivdExp node) 
  {
      // check if its the start node
      if (node.parent().getClass() == tiny.node.Start.class)
      {
          node.getL().apply(this);
          puts("/");
          node.getR().apply(this);
          return;
      }
      
      // check for precedence of parent node
	  if (isLowerPrecedence((PExp)(node),(PExp)(node.parent())))
	  {
		  puts("(");
	  }
	  
	  // print the values/identifiers and operator
      node.getL().apply(this);
      puts("/");
      node.getR().apply(this);
      
      if (isLowerPrecedence((PExp)node,(PExp)node.parent()))
      {
    	  puts(")");
      }
  }

  /**
   * Modulus
   * @param AModExp - an expression with modulus
   */
  public void caseAModExp(AModExp node) 
  {
      // check if it is the start node
      if (node.parent().getClass() == tiny.node.Start.class)
      {
          node.getL().apply(this);
          puts("%");
          node.getR().apply(this);
          return;
      }
      
      // check for precedence of the parent node
	  if (isLowerPrecedence((PExp)(node),(PExp)(node.parent())))
	  {
		  puts("(");
	  }
	  
	  // print out values/identifiers and operator
      node.getL().apply(this);
      puts("%");
      node.getR().apply(this);
      
      if (isLowerPrecedence((PExp)node,(PExp)node.parent()))
      {
    	  puts(")");
      }   
  }
  
  /**
   * Exponentiation
   * @param AExponExp - an expression with an exponentiation
   */
  public void caseAExponExp(AExponExp node) 
  {
      // check if its a start node
      if (node.parent().getClass() == tiny.node.Start.class)
      {
          node.getL().apply(this);
          puts("**");
          node.getR().apply(this);
          return;
      }
      
      // check for precedence of parent
	  if (isLowerPrecedence((PExp)(node),(PExp)(node.parent())))
	  {
		  puts("(");
	  }
	  
	  // print out the values/identifiers and operator
      node.getL().apply(this);
      puts("**");
      node.getR().apply(this);
      
      if (isLowerPrecedence((PExp)node,(PExp)node.parent()))
      {
    	  puts(")");
      }   
  }
  
  /**
   * Unary Minus Operator
   * @param ANegExp - an expression with a unary minus operation
   */
  public void caseANegExp(ANegExp node)
  {
	  puts("-");
	  node.getR().apply(this);
  }
  
  /* identifier */
  public void caseAIdExp(AIdExp node) 
  {
      puts(node.getId().getText());
  }
  
  /* number */
  public void outANumberExp(ANumberExp node) 
  {
      puts(node.getNumber().getText());
  }
  
  /**
   * Check if expression represented by PExp e1 has lower precedence than PExp e2 
   * @param e1
   * @param e2
   * @return true if e1 has lower precedence, false otherwise
   */
  public boolean isLowerPrecedence(PExp e1, PExp e2)
  {
	  if (e2==null)
	  {
		  return false;
	  }
	  return calculatePrecedence(e1) < calculatePrecedence(e2);
  }
  
  /**
   * Calculate the precedence of an expression
   * @param e
   * @return int - for the precendence level
   */
  public int calculatePrecedence(PExp e)
  {
	  if (e.getClass()==AMinusExp.class || e.getClass()==APlusExp.class)
	  {
		  return 1;
	  }
	  else if (e.getClass()==ADivdExp.class || e.getClass()==AMultExp.class || e.getClass()==AModExp.class)
	  {
		  return 2;
	  }
	  else if (e.getClass()==AExponExp.class){
		  return 3;
	  }
	  else
	  {
		  return 0;
	  }
  }
}
