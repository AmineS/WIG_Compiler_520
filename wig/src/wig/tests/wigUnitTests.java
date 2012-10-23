package wig.tests;

import wig.node.*;
import java.util.*;


import org.junit.*;

import wig.node.Start;

public class wigUnitTests
{

    @Test
    public void test()
    {
        testTree();
        testHasOneService();
        testServiceHasSession();
        testSessionHasId();
        testSessionHasId();
    }
 
    public void testTree()
    {
        Start tree = createTree();
        Assert.assertNotNull(tree);
    }

    public void testHasOneService()
    {
        Start tree = createTree();
        Assert.assertNotNull(tree.getPService());
    }
    
    public void testServiceHasSession()
    {
        Start tree = createTree();
        AService service = (AService)tree.getPService();      
        Assert.assertNotNull(service.getSession());
    }
    
    public void testSessionHasId()
    {
        Start tree = createTree();
        AService service = (AService)tree.getPService();
        ASession session = ((ASession)(service.getSession().getFirst()));
        Assert.assertNotNull(session.getIdentifier());
    }
    
    private Start createTree()
    {
        TIdentifier sessionId = new TIdentifier("testSession");
        ACompoundstm compStm = new ACompoundstm();
        
        AHtml html = new AHtml();
        ASession session = new ASession(sessionId, compStm);
        AVariable variable = new AVariable();
        ASchema schema = new ASchema();
        AFunction function = new AFunction();
        
        LinkedList<AHtml> htmls = new LinkedList<AHtml>();
        htmls.add(html);
        
        LinkedList<ASession> sessions = new LinkedList<ASession>();
        sessions.add(session);
        
        LinkedList<AVariable> variables = new LinkedList<AVariable>();
        variables.add(variable);
        
        LinkedList<ASchema> schemas = new LinkedList<ASchema>();
        schemas.add(schema);
        
        LinkedList<AFunction> functions = new LinkedList<AFunction>();
        functions.add(function);
        
        AService service = new AService(htmls,schemas,variables,functions,sessions);
        Start tree = new Start(service, new EOF());
        
        return tree;
    }
}
