package wig.type;

public class TypeRules
{
    
    public static boolean intAddition(Types e1, Types e2)     
    {
        return true;
    }
    
    
    public static boolean stringAddition(Types e1, Types e2)     
    {
        return true;
    }
    
    public static boolean intSubtraction(Types e1, Types e2)     
    {
        return true;
    }
    
    public static boolean intMultiplication(Types e1, Types e2)     
    {
        return true;
    }
    
    public static boolean intDivision(Types e1, Types e2)     
    {
        return true;
    }
    
    public static boolean intModulo(Types e1, Types e2)     
    {
        return true;
    }
    
    public static boolean intNegation(Types e)     
    {
        return true;
    }
    
    public static boolean intComparison(Types e1, Types e2)     
    {
        return true;
    }
    
    
    public static boolean notStatement(Types e)     
    {
        return true;
    }
    
    public static boolean logicalComparison(Types e1, Types e2)     
    {
        return true;
    }
    
    public static boolean assignment(Types e1, Types e2)     
    {
        return true;
    }
    
    public static boolean controlFlow(Types e)     
    {
        return true;
    }
    
    public static boolean fieldValue(Types e)     
    {
        return true;
    }
    
    
    public static boolean functionCall(Types[] arguments, Types[] parameters)
    {
        return true;
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
