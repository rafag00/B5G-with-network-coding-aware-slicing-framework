package org.helper;

import org.onosproject.net.topology.LinkWeigher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uc.dei.mei.framework.database.DataBaseWriteInterface;
import org.onlab.graph.ScalarWeight;
import org.onlab.graph.Weight;
import org.onosproject.net.Link;
import org.onosproject.net.topology.TopologyEdge;
import org.onosproject.net.statistic.Load;
import org.onosproject.net.statistic.StatisticService;
import static org.onosproject.cli.AbstractShellCommand.get;

import static java.lang.Math.pow;
import java.util.Map;

public class SliceLinkWeigher implements LinkWeigher {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private double sLoss;
    private double sJit;
    private double sLat;
    private double sBw;
    private double impBw;
    private double impLoss;
    private double impLat;
    private double impJit;
    private double impEne;


    public SliceLinkWeigher(double sLoss, double sJit, double sLat, double sBw, double impBw, double impLoss, double impLat, double impJit, double impEne) {
        super();
        this.sLoss = sLoss;
        this.sJit = sJit;
        this.sLat = sLat;
        this.sBw = sBw;
        this.impBw = impBw;
        this.impLoss = impLoss;
        this.impLat = impLat;
        this.impJit = impJit;
        this.impEne = impEne;
    }

    /** 
     * Gets the initial weight.
     * @return Weight The initial weight.
     */
    @Override
    public Weight getInitialWeight() {
        return ScalarWeight.toWeight(1.0);
    }
   
    /** 
     * Gets the non viable weight (maybe the weight for it not be considered a link?).
     * @return Weight The non viable weight.
     */
    @Override
    public Weight getNonViableWeight() {
        return ScalarWeight.toWeight(1000000.0);
        //return ScalarWeight.NON_VIABLE_WEIGHT;
    }

    /** 
     * Gets the weight of a link trough the sliceLinkCost.
     * @param edge The edge to get the weight from.
     * @return Weight The weight of the link.
     */
    @Override
    public Weight weight(TopologyEdge edge) {

        Link link = edge.link();

        double cost = sliceLinkCost(link);

        return ScalarWeight.toWeight(cost);
    }

    /**
     * Calculates the cost of a link based on the slice and link properties.
     * @param link The link to calculate the cost.
     * @return double The cost of the link.
     */
    private double sliceLinkCost(Link link) {
        double linkBw = 0;
        double linkLoss = 0.0;
        double linkLat = 0;
        double linkJit = 0;
        double linkEne = 0;

        
        //Get the link properties from the DB
        DataBaseWriteInterface serviceDB = get(DataBaseWriteInterface.class);

        //log.info("Before converting to long");
        //Only considering physical ports that's why it's safe to use the (int) conversion
        Map<String, String> linkProperties = serviceDB.getLinkBySrcToDst(link.src().deviceId().toString(), link.dst().deviceId().toString(), (int)link.src().port().toLong(), (int)link.dst().port().toLong());
        //log.info("After converting to long");

        try{
            if (linkProperties.isEmpty()) {
                log.info("Link properties not found: "+link.src().deviceId().toString()+" -> "+link.dst().deviceId().toString());
                return -1;
            }

            linkBw = Double.parseDouble(linkProperties.get("bw"));
            linkLoss = Double.parseDouble(linkProperties.get("loss_prob"));
            linkLat = Double.parseDouble(linkProperties.get("latency"));
            linkJit = Double.parseDouble(linkProperties.get("jitter"));
            linkEne = Double.parseDouble(linkProperties.get("energy_consumption"));
        }catch (Exception e){
            log.info("Error parsing link properties");
            return -1;
        }

        //log.info("After getting link properties");
        StatisticService statsService = get(StatisticService.class);

        Load linkLoad = statsService.load(link); // Obtain the current observed rate (in bytes/s) on a link - need to convert to bits/s

        linkBw = linkBw  - (linkLoad.rate()*8);
        
        //Calculate the weight of each property
        //log.info("bwW no pow: "+(linkBw/sBw));
        //log.info("lossW no pow: "+(linkLoss/sLoss));
        //log.info("latW no pow: "+(linkLat/sLat));
        //log.info("jitW no pow: "+(linkJit/sJit));
        //log.info("eneW no pow: "+linkEne);

        double bwW = pow((linkBw/sBw)+1,impBw);
        double lossW = pow((linkLoss/sLoss)+1,impLoss);
        double latW = pow((linkLat/sLat)+1,impLat);
        double jitW = pow((linkJit/sJit)+1,impJit);
        double eneW = pow(linkEne+1, impEne);

        //log.info("bwW: "+bwW);
        //log.info("lossW: "+lossW);
        //log.info("latW: "+latW);
        //log.info("jitW: "+jitW);
        //log.info("eneW: "+eneW);
        
        //log.info("DONE CALCULATING LINK COST FOR "+link.src().deviceId().toString()+" -> "+link.dst().deviceId().toString() + " COST: "+(bwW + lossW + latW + jitW + eneW));
        return (bwW + lossW + latW + jitW + eneW);
    }

}
