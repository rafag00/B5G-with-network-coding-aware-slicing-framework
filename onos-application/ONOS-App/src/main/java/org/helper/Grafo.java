package org.helper;

import org.onosproject.net.flow.FlowRule;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A auxiliating graph class containing nodes and flowrule information.
 */ 
public class Grafo {

    private HashMap<String,Node> nodes;
    private ArrayList<ArrayList<FlowRule>> flowrulesInstalled;

    public Grafo(){

        nodes = new HashMap<String,Node>();
        flowrulesInstalled = new ArrayList<ArrayList<FlowRule>>();

    }

    
    /** 
     * Gets the nodes on a graph.
     * @return HashMap with String keys representing Node names and the nodes of a graph.
     */
    public HashMap<String, Node> getNodes() {
        return nodes;
    }

    
    /** 
     * Gets the list of the flow rules installed.
     * @return An ArrayList of ArrayLists containing FlowRule objects. The flow rules installed.
     */
    public ArrayList<ArrayList<FlowRule>> getFlowrulesInstalled() {
        return flowrulesInstalled;
    }

    
    /** 
     * Set the installed flow rulles.
     * @param flowrulesInstalled The installed flow rules.
     */
    public void setFlowrulesInstalled(ArrayList<ArrayList<FlowRule>> flowrulesInstalled) {
        this.flowrulesInstalled = flowrulesInstalled;
    }
}
