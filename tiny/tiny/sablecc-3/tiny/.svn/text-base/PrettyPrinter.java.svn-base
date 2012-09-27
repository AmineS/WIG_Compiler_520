package tiny;

import tiny.parser.*;
import tiny.lexer.*;
import tiny.node.*;
import tiny.analysis.*;
import java.util.*;

public class PrettyPrinter extends DepthFirstAdapter {
  public static void print(Node node) {
      node.apply(new PrettyPrinter());
  }

  private void puts(String s) {
      System.out.print(s);
      System.out.flush();
  }

  /* plus */ 
  public void caseAPlusExp(APlusExp node) {
      puts("(");
      node.getL().apply(this);
      puts("+");
      node.getR().apply(this);
      puts(")");
  }

  /* minus */
  public void caseAMinusExp(AMinusExp node) { 
      puts("(");
      node.getL().apply(this);
      puts("-");
      node.getR().apply(this);
      puts("}");
  }

  /* mult */
  public void caseAMultExp(AMultExp node) { 
      puts("(");
      node.getL().apply(this);
      puts("*");
      node.getR().apply(this);
      puts(")");
  }

  /* div */
  public void caseADivdExp(ADivdExp node) {
      puts("(");
      node.getL().apply(this);
      puts("/");
      node.getR().apply(this);
      puts(")");
  }

  /* identifier */
  public void caseAIdExp(AIdExp node) {
      puts(node.getId().getText());
  }
  
  /* number */
  public void outANumberExp(ANumberExp node) {
      puts(node.getNumber().getText());
  }
}
