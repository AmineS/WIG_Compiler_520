package wig.weeder;

import java.util.*;

import com.sun.xml.internal.ws.wsdl.writer.document.Service;

import wig.parser.*;
import wig.lexer.*;
import wig.node.*;
import wig.analysis.*;


public class Weeder extends DepthFirstAdapter
{
    private Set<String> fGlobalVariablesDeclared = new HashSet<String>(); // HTML, Variables(tuple, string, int ....)
    private Set<String> fHtmlsSchemasGlobalVariablesNames = new HashSet<String>(); // HTML const names + Schema names + Variable names 
    private Set<String> fSessionNames = new HashSet<String>(); //Session names
    private Set<String> fFunctionNames = new HashSet<String>(); // Function names
    private Set<String> fCurrentLocalVariableNames = new HashSet<String>();

    public static void weed(Node node)
    {
        node.apply(new Weeder());
    }
    
    public void caseAHtml(AHtml node)
    {
        String name = node.getIdentifier().getText();
        if(fHtmlsSchemasGlobalVariablesNames.contains(name))
        {
            System.out.println("Error: Duplicate variable: " + node.getIdentifier().getText() + " at line " + node.getIdentifier().getLine());
        }
        else
        {
            fHtmlsSchemasGlobalVariablesNames.add(name);
        }
    }
    
    public void caseASchema(ASchema node)
    {
        String name = node.getIdentifier().getText();
        if(fHtmlsSchemasGlobalVariablesNames.contains(name))
        {
            System.out.println("Error: Duplicate variable: " + node.getIdentifier().getText() + " at line " + node.getIdentifier().getLine());
        }
        else
        {
            fHtmlsSchemasGlobalVariablesNames.add(name);
        } 
    }
    
    public void caseAVariable(AVariable node)
    {
        if(node.parent().getClass().equals(AService.class) )
        {
            for(TIdentifier identifier : node.getIdentifier())
            {
                if(fHtmlsSchemasGlobalVariablesNames.contains(identifier.getText()))
                {
                    System.out.println("Error: Duplicate variable: " + identifier.getText() + " at line " + identifier.getLine());
                }
                else
                {
                    fHtmlsSchemasGlobalVariablesNames.add(identifier.getText());
                }
            }
        }
        outAVariable(node);
    }
    
    public void outAVariable(AVariable node)
    {
        System.out.println("Hello");
    }
        
}
