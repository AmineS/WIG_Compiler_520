package wig.type;

public class TypeRules
{
    
    public static boolean intAddition(Types e1, Types e2)     
    {
        return e1 == Types.INT && e2 == Types.INT;
    }    
    
    public static boolean stringAddition(Types e1, Types e2)     
    {
        return 
                (e1 == Types.STRING &&  e2 == Types.STRING) ||
                (e1 == Types.INT &&  e2 == Types.STRING) ||
                (e1 == Types.STRING &&  e2 == Types.INT);
    }
    
    public static boolean intSubtraction(Types e1, Types e2)     
    {
        return e1 == Types.INT && e2 == Types.INT;
    }
    
    public static boolean intMultiplication(Types e1, Types e2)     
    {
        return e1 == Types.INT && e2 == Types.INT;
    }
    
    public static boolean intDivision(Types e1, Types e2)     
    {
        return e1 == Types.INT && e2 == Types.INT;
    }
    
    public static boolean intModulo(Types e1, Types e2)     
    {
        return e1 == Types.INT && e2 == Types.INT;
    }
    
    public static boolean intNegation(Types e)     
    {
        return e == Types.INT;
    }
    
    public static boolean intComparison(Types e1, Types e2)     
    {
        return e1 == Types.INT && e2 == Types.INT;
    }
    
    
    public static boolean notStatement(Types e)     
    {
        return e == Types.INT;
    }
    
    public static boolean logicalComparison(Types e1, Types e2)     
    {
        return e1 == Types.BOOL && e2 == Types.BOOL;
    }
    
    public static boolean assignment(Types e1, Types e2)     
    {
        return e1 == e2; 
    }
    
    public static boolean controlFlow(Types e)     
    {
        return e == Types.BOOL;
    }
    
    
    public static boolean functionCall(Types[] arguments, Types[] parameters)
    {
        boolean argumentsAreValid = true;
        
        if(arguments.length != parameters.length)
        {
            System.out.println("Invalid number of arguments in functional call!");
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
