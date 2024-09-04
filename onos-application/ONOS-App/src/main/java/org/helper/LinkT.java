package org.helper;

/**
 * A auxiliating link class.
 */
public class LinkT {

    //capacity
    private double bwTotalMbts;
    private double bwAvbleMbts;
    private double lossProb;

    private Node nodeSrc;
    private Node nodeDst;

    //wireless
    //wired
    private String linkType;


    private String edgeToString;


    public LinkT(double bwTotalMbts, double bwAvbleMbts, double lossProb, Node nodeSrc, Node nodeDst, String linkType ){

        this.bwTotalMbts = bwTotalMbts;
        this.bwAvbleMbts = bwAvbleMbts;
        this.lossProb = lossProb;
        this.nodeSrc = nodeSrc;
        this.nodeDst = nodeDst;
        this.linkType = linkType;
        this.edgeToString = "("+nodeSrc.getNodeName()+" : "+nodeDst.getNodeName()+")";

    }


    
    /** 
     * Get the source node of the link.
     * @return Node The source node of the link.
     */
    public Node getNodeSrc() {
        return nodeSrc;
    }

    
    /** 
     * Set the source node of the link.
     * @param nodeSrc The source node of the link.
     */
    public void setNodeSrc(Node nodeSrc) {
        this.nodeSrc = nodeSrc;
    }

    
    /** 
     * Get the total bandwidth of the link (Mbts).
     * @return double
     */
    public double getBwTotalMbts() {
        return bwTotalMbts;
    }

    
    /** 
     * Set the total bandwidth of the link (Mbts).
     * @param bwTotalMbts The total bandwidth of the link.
     */
    public void setBwTotalMbts(double bwTotalMbts) {
        this.bwTotalMbts = bwTotalMbts;
    }

    /**
     * Get the available bandwidth of the link (Mbts).
     * @return double The available bandwidth of the link.
     */
    public double getBwAvbleMbts() {
        return bwAvbleMbts;
    }

    /**
     * Set the available bandwidth of the link (Mbts).
     * @param bwAvbleMbts The available bandwidth of the link.
     */
    public void setBwAvbleMbts(double bwAvbleMbts) {
        this.bwAvbleMbts = bwAvbleMbts;
    }

    /**
     * Get the loss probability of the link.
     * @return double The loss probability of the link.
     */
    public double getLossProb() {
        return lossProb;
    }

    /**
     * Set the loss probability of the link.
     * @param lossProb The loss probability of the link.
     */
    public void setLossProb(double lossProb) {
        this.lossProb = lossProb;
    }

    /**
     * Get the destination node of the link.
     * @return Node The destination node of the link.
     */
    public Node getNodeDst() {
        return nodeDst;
    }

    /**
     * Set the destination node of the link.
     * @param nodeDst The destination node of the link.
     */
    public void setNodeDst(Node nodeDst) {
        this.nodeDst = nodeDst;
    }

    /**
     * Get the type of the link.
     * @return String The type of the link.
     */
    public String getLinkType() {
        return linkType;
    }

    /**
     * Set the type of the link.
     * @param linkType The type of the link.
     */
    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    /**
     * Get the string representation of the edge.
     * @return String The string representation of the edge.
     */
    public String getEdgeToString() {
        return edgeToString;
    }

    /**
     * Set the string representation of the edge.
     * @param edgeToString The string representation of the edge.
     */
    public void setEdgeToString(String edgeToString) {
        this.edgeToString = edgeToString;
    }
}
