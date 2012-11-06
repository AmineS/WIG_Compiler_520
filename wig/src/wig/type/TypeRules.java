package wig.type;

public class TypeRules
{
    
    public static boolean intAddition(Type e1, Type e2)     
    {
        return e1 == Type.INT && e2 == Type.INT;
    }    
    
    public static boolean stringAddition(Type e1, Type e2)     
    {
        return 
                (e1 == Type.STRING &&  e2 == Type.STRING) ||
                (e1 == Type.INT &&  e2 == Type.STRING) ||
                (e1 == Type.STRING &&  e2 == Type.INT);
    }
    
    public static boolean intSubtraction(Type e1, Type e2)     
    {
        return e1 == Type.INT && e2 == Type.INT;
    }
    
    public static boolean intMultiplication(Type e1, Type e2)     
    {
        return e1 == Type.INT && e2 == Type.INT;
    }
    
    public static boolean intDivision(Type e1, Type e2)     
    {
        return e1 == Type.INT && e2 == Type.INT;
    }
    
    public static boolean intModulo(Type e1, Type e2)     
    {
        return e1 == Type.INT && e2 == Type.INT;
    }
    
    public static boolean intNegation(Type e)     
    {
        return e == Type.INT;
    }
    
    public static boolean intComparison(Type e1, Type e2)     
    {
        return e1 == Type.INT && e2 == Type.INT;
    }
    
    public static boolean strComparison(Type e1, Type e2)
    {
        return e1 == Type.STRING && e2 == Type.STRING;
    }
    
    public static boolean notExpression(Type e)     
    {
        return e == Type.BOOL;
    }
    
    public static boolean logicalComparison(Type e1, Type e2)     
    {
        return e1 == Type.BOOL && e2 == Type.BOOL;
    }
    
    public static boolean assignment(Type e1, Type e2)     
    {
        return e1 == e2; 
    }
    
    public static boolean controlFlow(Type e)     
    {
        return e == Type.BOOL;
    }
    
    
    public static boolean functionCall(Type[] arguments, Type[] parameters)
    {
        boolean argumentsAreValid = true;
        
        if(arguments.length != parameters.length)
        {
            System.out.println("Invalid number of arguments in functional call!");
            System.exit(-1);
        }
        else 
        {
            for(int i = 0; i < arguments.length; ++i)
            {
                if(arguments[i] != parameters[i])
                {
                    argumentsAreValid = false;
                    break;
                }
            }            
        }             
        return argumentsAreValid;
    }
    
    public static boolean returnExpression(Type e1, Type e2)
    {
        return e1 == e2;
    }
/* 
 *  

Not complete:

 - keep: “e keep x” is of type T, then e is of type ‘t1’, x is of type ‘a’, T.x is of type ‘a’
 - remove: e remove x is of type 
 - keep_many: e keep (x1,x2,x3...) is 
 - remove_many: e remove (x1,x2,x3...) is 
- function call: e.f(e1,e2,...,en) is of type tau
 */

}
