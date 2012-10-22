package wig.weeder;

import java.util.*;

import com.sun.xml.internal.ws.wsdl.writer.document.Service;

import wig.parser.*;
import wig.lexer.*;
import wig.node.*;
import wig.analysis.*;


public class Weeder extends DepthFirstAdapter
{
    private Set<String> fHtmlsTuplesGlobalVariablesNames = new HashSet<String>(); // HTML const names + Tuple names + Variable names 
    private Set<String> fSessionNames = new HashSet<String>(); //Session names
    private Set<String> fSchemasNames = new HashSet<String>();
    private Set<String> fFunctionNames = new HashSet<String>(); // Function names
    private Set<String> fCurrentLocalVariableNames = new HashSet<String>();

    public static void weed(Node node)
    {
        node.apply(new Weeder());
    }
    
    public void caseAHtml(AHtml node)
    {
        String name = node.getIdentifier().getText();
        if(fHtmlsTuplesGlobalVariablesNames.contains(name))
        {
            System.out.println("Error: Duplicate variable: " + node.getIdentifier().getText() + " at line " + node.getIdentifier().getLine());
        }
        else
        {
            fHtmlsTuplesGlobalVariablesNames.add(name);
        }
    }
    
    public void caseASchema(ASchema node)
    {
        String name = node.getIdentifier().getText();
        if(fSchemasNames.contains(name))
        {
            System.out.println("Error: Duplicate schema: " + node.getIdentifier().getText() + " at line " + node.getIdentifier().getLine());
        }
        else
        {
            fSchemasNames.add(name);
        }
        Set<String> fieldsNames = new HashSet<String>();
        for(PField field : node.getField())
        {
            AField fieldImpl = (AField) field;
            String memberName = fieldImpl.getIdentifier().toString().trim();
            if(!fieldsNames.contains(memberName))
            {
                fieldsNames.add(memberName);
            }
            else
            {
                System.out.println("Error: Duplicate member " + memberName + " in Schema " + name + " declared at line " + node.getIdentifier().getLine()); 
            }
        }
        
    }
    
    public void caseAVariable(AVariable node)
    {
        if(node.parent().getClass().equals(AService.class))
        {
            for(TIdentifier identifier : node.getIdentifier())
            {
                if(fHtmlsTuplesGlobalVariablesNames.contains(identifier.getText()))
                {
                    System.out.println("Error: Duplicate variable: " + identifier.getText() + " at line " + identifier.getLine());
                }
                else
                {
                    fHtmlsTuplesGlobalVariablesNames.add(identifier.getText());
                }
            }
        }
        else
        {
            for(TIdentifier identifier : node.getIdentifier())
            {
                if(fCurrentLocalVariableNames.contains(identifier.getText()) && !fHtmlsTuplesGlobalVariablesNames.contains(identifier.getText()))
                {
                    System.out.println("Error: Duplicate local variable: " + identifier.getText() + " at line " + identifier.getLine());
                }
                else if(fHtmlsTuplesGlobalVariablesNames.contains(identifier.getText()))
                {
                    System.out.println("Error: Duplicate global variable: " + identifier.getText() + " at line " + identifier.getLine());
                }
                else
                {
                    fCurrentLocalVariableNames.add(identifier.getText());
                }
            }
        }
    }
    
    public void outAFunction(AVariable node)
    {
        fCurrentLocalVariableNames.clear();
    }
    
    public void outASession(ASession node)
    {
        fCurrentLocalVariableNames.clear();
    }
    
    public void caseAInput(AInput node)
    {
        String leftValueName = node.getLvalue().toString().trim();
        if(!fCurrentLocalVariableNames.contains(leftValueName) && !fHtmlsTuplesGlobalVariablesNames.contains(leftValueName))
        {
            System.out.println("Error: Variable " + leftValueName + " is not defined in global and local scope" + " at line " + node.getIdentifier().getLine() );
        }
        // Need to check that the value received actually exists as an input field
    }
    
    public void caseASession(ASession node)
    {
        String sessionName = node.getIdentifier().toString().trim();
        if(fSessionNames.contains(sessionName))
        {
            System.out.println("Error: Duplicate Session " + sessionName + " at line " + node.getIdentifier().getLine());
        }
        else
        {
            fSessionNames.add(sessionName);
        }
    }
    
    public void caseATupleExp(ATupleExp node)
    {   
        System.out.println("tuple {");
        Iterator<PFieldvalue> iter = node.getFieldvalue().iterator();
        while(iter.hasNext())
        {
            iter.next().apply(this);
        }
        System.out.println("}");
    }
    
    public void caseATupleType(ATupleType node)
    {
        String schema = node.toString().trim();
        if(!fSchemasNames.contains(schema))
        {
            System.out.println("Error: Schema " + schema + " is not defined");
        }
    }
   
}
