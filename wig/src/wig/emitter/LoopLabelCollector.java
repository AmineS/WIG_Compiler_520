package wig.emitter;

import java.util.ArrayList;
import java.util.HashMap;

import wig.analysis.DepthFirstAdapter;
import wig.node.AShowStm;
import wig.node.AWhileStm;
import wig.node.Node;
import wig.symboltable.SymbolTable;

public class LoopLabelCollector extends DepthFirstAdapter
{
    HashMap<Node,String> labelMap;
    ArrayList<String> labels = new ArrayList<String>();
    
    private ArrayList<String> getLabels(Node node)
    {
        node.apply(this);
        return labels;
    }
    
    
    public void caseAWhileStm(AWhileStm node)
    {
    }
    
    public void caseAShowStm(AShowStm node)
    {
        labels.add(labelMap.get(node));
    }
    
    public LoopLabelCollector(HashMap<Node,String> map)
    {
        labelMap = map;
    }
}
