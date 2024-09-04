//https://groups.google.com/a/onosproject.org/g/onos-dev/c/38IL1y3-TmY
package org.helper;

import org.onlab.graph.ScalarWeight;
import org.onlab.graph.Weight;
import org.onosproject.net.Link;
//import org.onosproject.net.link.LinkService;
import org.onosproject.net.topology.LinkWeigher;
import org.onosproject.net.topology.TopologyEdge;
//import org.osgi.service.component.annotations.Reference;
//import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Energy Link Weigher.
 */
public final class EnergyLinkWeigher implements LinkWeigher {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private String serviceStr;
    private DataSource dataSource;
    private double datarate;

    public EnergyLinkWeigher(String serviceStr, DataSource dataSource) {
        super();
        this.serviceStr = serviceStr;
        this.dataSource = dataSource;
        this.datarate = getDatarate();
    }

    
    /** 
     * Gets the initial weight.
     * @return Weight The initial weight.
     */
    @Override
    public Weight getInitialWeight() {
        return ScalarWeight.toWeight(0.0);
    }

    
    /** 
     * Gets the non viable weight (maybe the weight for it not be considered a link?).
     * @return Weight The non viable weight.
     */
    @Override
    public Weight getNonViableWeight() {
        return ScalarWeight.NON_VIABLE_WEIGHT;
    }

    
    /** 
     * Gets the weight of a link trough a fixed cost.
     * @param edge The edge to get the weight from.
     * @return Weight The weight of the edge.
     */
    @Override
    public Weight weight(TopologyEdge edge) {

        Link link = edge.link();

        double cost = 1.0;

        return ScalarWeight.toWeight(cost);
    }



    
    /** 
     * Gets the datarate from the database.
     * @return double The datarate.
     */
    private double getDatarate() {
        String readTableString = "select datarate_mbs from service_conf where name = '"+this.serviceStr+"'";
        try(Connection con = dataSource.getConnection();
            PreparedStatement prepStmt = con.prepareStatement(readTableString)){

            ResultSet rs = prepStmt.executeQuery();


            if(rs.next() == false){
                throw new Exception();
            }

            //0 : idk what it is
            //1 : datarate,
            return Double.parseDouble(rs.getString(1));


        } catch (SQLException throwables) {
            log.info("ERROR reading table configurations from DataBase. "+getClass());
            throwables.printStackTrace();
            return 0;
        } catch (Exception e) {
            log.info("ERROR, configuration table is empty");
            return 0;
        }

    }



}