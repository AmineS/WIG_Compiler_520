package wig.type;

import java.util.Hashtable;

import wig.node.Node;

/**
 * A wrapper around a hashtable which maps nodes to types
 * @author group-h
 */
public class TypeTable
{
    /** The hashtable from node to type */
    private Hashtable<Node,Type> fHashTable = new Hashtable<Node,Type>();
    
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
       return fHashTable.get(node); 
    }
    
    /**
     * Set the type for a node
     * @param node - node for which type is being set
     * @param type - the type of the node
     */
    public void setNodeType(Node node, Type type)
    {
        fHashTable.put(node, type);
    }
    
    /**
     * Check if the TypeTable contains the node 
     * @param node
     */
    public boolean containsNode(Node node)
    {
       return  fHashTable.contains(node);
    }
}
