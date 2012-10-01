package tiny;

import tiny.parser.*;
import tiny.lexer.*;
import tiny.node.*;
import tiny.analysis.*;
import java.util.*;

public class Evaluator extends DepthFirstAdapter
{
    /* (static) eval function, returns expression node 
     * containing full or partial evaluation 
     */
    public static Node eval(Node ast)
    {
        Evaluator e = new Evaluator();    
        ast.apply(e);

        // Return a number expression in case of full evaluation otherwise
        // return the ast that has been modified for partial evaluation 
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
        // in case of full evaluation, associate the value 
        // with the root node otherwise replace the node in 
        // the tree with a new number expression.
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
        // if there is no value associated with the 
        // then the method will return, indicating 
        // partial evaluation.
        if(getValue(node.getPExp()) == null) return;
        setValue(node, getValue(node.getPExp())); 
    }

    /* plus */ 
    public void outAPlusExp(APlusExp node)
    { 
        Integer rightNodeValue = getValue(node.getR());
        Integer leftNodeValue = getValue(node.getL());

        /*
         *  If there is a zero in either of the children
         *  then the current node is replaced by the 
         *  other child. If either of the children did
         *  not have a value, this means that evaluation
         *  was partially done and the addition does not
         *  need to proceed. If evaluation is successful
         *  the addition step proceeds.
         */ 
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
        Integer rightNodeValue = getValue(node.getR());
        Integer leftNodeValue = getValue(node.getL());

        /*
         * Similar to addition. Except that when the left 
         * node is zero then a negative expression must be 
         * returned.
         */
        if (leftNodeValue != null && leftNodeValue==0)
        {
            if (rightNodeValue!=null)
            {
                setValue(node.getR(),-getValue(node.getR()));
                node.replaceBy(node.getR());
            }
            else
            {
                ANegExp newNegExp = new ANegExp();
                newNegExp.setR(node.getR());
                node.replaceBy(newNegExp);
            }
        }
        else if (rightNodeValue != null && rightNodeValue==0)
        {
            node.replaceBy(node.getL()); 
        }
        else if (rightNodeValue == null || leftNodeValue == null)  
        {
            return;
        }
        else
        {
            setValue(node, leftNodeValue - rightNodeValue);
        }
    }

    /* mult */
    public void outAMultExp(AMultExp node)
    { 
        Integer rightNodeValue = getValue(node.getR());
        Integer leftNodeValue = getValue(node.getL());

        /*
         * Similar to addition except that when a node is zero
         * it replaces the current node since the value of the 
         * other node doesn't matter. Similarly, if the value 
         * is none, the other node replaces the current node. 
         */
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
        
        /*
         * Similar to multiplication. Divide by zero will force 
         * the system to exit. 
         */
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

        /*
         * Same as division.
         */
        if(rightNodeValue!= null && rightNodeValue == 0)
        {
            System.out.println("Attempting modulus by zero!");
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
        Integer rightNodeValue = getValue(node.getR());
        Integer leftNodeValue = getValue(node.getL());

        /*
         * Same as addition except that anything to the 
         * exponent of zero returns 1, anything to the 
         * exponent of 1 returns itself and and 1 to the 
         * exponent of anything returns 1. 
         */
        
        if (leftNodeValue != null && leftNodeValue == 0 &&
            rightNodeValue !=null && rightNodeValue == 0)
        {
            System.out.println("0**0 is undefined!");
            System.exit(0);            
        }
        else if (leftNodeValue != null && leftNodeValue==0)
        {
            setValue(node.getR(),0);
            node.replaceBy(node.getR());
        }
        else if (rightNodeValue != null && rightNodeValue==0)
        {
            setValue(node.getR(),1);
            node.replaceBy(node.getR()); 
        }
        else if (leftNodeValue != null && leftNodeValue == 1)
        {
            setValue(node.getR(),1);
            node.replaceBy(node.getR());
        }
        else if (rightNodeValue != null && rightNodeValue == 1)
        {
            node.replaceBy(node.getL());
        }
        else if (rightNodeValue == null || leftNodeValue == null)  
        {
            return;
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

        /*
         * If there is no value to the child, that is
         * partial evaluation is being done then simply 
         * return 
         */
        if(rightNodeValue == null) return;

        setValue(node, Math.abs(rightNodeValue)); 
    }

    /* neg */
    public void outANegExp(ANegExp node)
    {
        Integer rightNodeValue = getValue(node.getR());
        
        /*
         * If there is no value to the child, that is
         * partial evaluation is being done then simply 
         * return 
         */
        if(rightNodeValue == null) return;
        
        setValue(node, -1*(rightNodeValue)); 
    }

    /* identifier (!!!) */
    public void outAIdExp(AIdExp node)
    {
        /*
         * Incase of an identifier, don't store any value in the 
         * hash table. This will prevent any further evaluation in 
         * that part of the tree and force an expression to be printed. 
         */
        return; 
    }

    /* number */
    public void outANumberExp(ANumberExp node)
    { setValue(node, Integer.parseInt(node.getNumber().getText())); }

    private Node replaceNodesWithValues(Node node, Integer nodeValue)
    {
        /*
         * replaces a node with a number expression equivalent of the 
         * given integer value.
         */
        //null check 
        if(node == null) return null; 

        Node newNode = new ANumberExp(new TNumber(nodeValue.toString()));
        //if there is an integer value then return the value 
        node.replaceBy(newNode);

        return newNode;
    }
}
