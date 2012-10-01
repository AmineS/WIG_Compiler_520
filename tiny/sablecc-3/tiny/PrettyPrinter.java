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

  private void puts(String s) 
  {
      System.out.print(s);
      System.out.flush();
  }

  /* plus */ 
  public void caseAPlusExp(APlusExp node) 
  {
      if (node.parent().getClass() == tiny.node.Start.class)
      {
          node.getL().apply(this);
          puts("+");
          node.getR().apply(this);
          return;
      }
      
	  if (isLowerPrecedence((PExp)(node),(PExp)(node.parent())))
	  {
		  puts("(");
	  }
	  
      node.getL().apply(this);
      puts("+");
      node.getR().apply(this);
      
      if (isLowerPrecedence((PExp)node,(PExp)node.parent()))
      {
    	  puts(")");
      }      
  }

  /* minus */
  public void caseAMinusExp(AMinusExp node) 
  { 
      if (node.parent().getClass() == tiny.node.Start.class)
      {
          node.getL().apply(this);
          puts("-");
          node.getR().apply(this);
          return;
      }
      
	  if (isLowerPrecedence((PExp)(node),(PExp)(node.parent())))
	  {
		  puts("(");
	  }
	  
      node.getL().apply(this);
      puts("-");
      node.getR().apply(this);
      
      if (isLowerPrecedence((PExp)node,(PExp)node.parent()))
      {
    	  puts(")");
      }   
  }

  /* abs */
  public void caseAAbsExp(AAbsExp node) 
  {
	  puts("abs(");
	  node.getR().apply(this);
	  puts(")");
  }
  
  /* mult */
  public void caseAMultExp(AMultExp node) 
  { 
	  if (node.parent().getClass()==tiny.node.Start.class)
	  {
	      node.getL().apply(this);
	      puts("*");
	      node.getR().apply(this);
	      return;
	  }
	  
	  if (isLowerPrecedence((PExp)(node),(PExp)(node.parent())))
	  {
		  puts("(");
	  }
      node.getL().apply(this);
      puts("*");
      node.getR().apply(this);
      if (isLowerPrecedence((PExp)node,(PExp)node.parent()))
      {
    	  puts(")");
      }
  }

  /* div */
  public void caseADivdExp(ADivdExp node) 
  {
      if (node.parent().getClass() == tiny.node.Start.class)
      {
          node.getL().apply(this);
          puts("/");
          node.getR().apply(this);
          return;
      }
      
	  if (isLowerPrecedence((PExp)(node),(PExp)(node.parent())))
	  {
		  puts("(");
	  }
	  
      node.getL().apply(this);
      puts("/");
      node.getR().apply(this);
      
      if (isLowerPrecedence((PExp)node,(PExp)node.parent()))
      {
    	  puts(")");
      }
  }

  /* mod */
  public void caseAModExp(AModExp node) 
  {
      if (node.parent().getClass() == tiny.node.Start.class)
      {
          node.getL().apply(this);
          puts("%");
          node.getR().apply(this);
          return;
      }
      
	  if (isLowerPrecedence((PExp)(node),(PExp)(node.parent())))
	  {
		  puts("(");
	  }
	  
      node.getL().apply(this);
      puts("%");
      node.getR().apply(this);
      
      if (isLowerPrecedence((PExp)node,(PExp)node.parent()))
      {
    	  puts(")");
      }   
  }
  
  /* exponentiation */
  public void caseAExponExp(AExponExp node) 
  {
      if (node.parent().getClass() == tiny.node.Start.class)
      {
          node.getL().apply(this);
          puts("**");
          node.getR().apply(this);
          return;
      }
      
	  if (isLowerPrecedence((PExp)(node),(PExp)(node.parent())))
	  {
		  puts("(");
	  }
	  
      node.getL().apply(this);
      puts("**");
      node.getR().apply(this);
      
      if (isLowerPrecedence((PExp)node,(PExp)node.parent()))
      {
    	  puts(")");
      }   
  }
  
  /*negative*/
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
  
  public boolean isLowerPrecedence(PExp e1, PExp e2)
  {
	  if (e2==null)
	  {
		  return false;
	  }
	  return calculatePrecedence(e1) < calculatePrecedence(e2);
  }
  
  public int calculatePrecedence(PExp e)
  {
	  if (e.getClass()==AMinusExp.class)
	  {
		  return 1;
	  }
	  else if (e.getClass()==ADivdExp.class && e.getClass()==AMultExp.class && e.getClass()==AModExp.class)
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
