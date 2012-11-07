package wig.type;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import wig.node.Node;

/**
 * A wrapper around a hashtable which maps nodes to types
 * @author group-h
 */
public class TypeTable
{
    /** The HashMap from node to type */
    private Map<Node,Type> fNodeToTypeMap = new HashMap<Node,Type>();
    
    /**
     * Constructor
     */
    public TypeTable()
    {
    }
    
    /**
     * Get the node's type
     * @param node
     * @return node's type
     */
    public Type getNodeType(Node node)
    {
       return fNodeToTypeMap.get(node); 
    }
    
    /**
     * Set the type for a node
     * @param node - node for which type is being set
     * @param type - the type of the node
     */
    public void setNodeType(Node node, Type type)
    {
        fNodeToTypeMap.put(node, type);
    }
    
    /**
     * Check if the TypeTable contains the node 
     * @param node
     */
    public boolean containsNode(Node node)
    {
       return  fNodeToTypeMap.containsKey(node);
    }
}
