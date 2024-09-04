/*
 * Copyright 2021-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uc.dei.mei.framework.database;


//import org.helper.Node;
import org.onlab.packet.IpAddress;
import org.onosproject.cfg.ComponentConfigService;
import org.onosproject.net.*;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.device.PortStatistics;
import org.onosproject.net.driver.DriverService;
import org.onosproject.net.flow.FlowEntry;
import org.onosproject.net.flow.FlowRuleService;
import org.onosproject.net.host.HostService;
import org.onosproject.net.link.LinkService;
import org.onosproject.net.statistic.PortStatisticsService;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.onosproject.core.HybridLogicalClockService;
//import org.uc.dei.mei.framework.database.DataBaseWriteInterface;

/**
 * Implements the DataBaseWriteInterface and is responsible for interacting to the database.
 */
@Component(immediate = true,
        service = {DataBaseWriteInterface.class}
)

public class ONOSAppDataBaseComponent implements DataBaseWriteInterface {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DriverService driverService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected HostService hostService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected LinkService linkService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected PortStatisticsService portStatisticsService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected FlowRuleService flowRuleService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected HybridLogicalClockService hybridLogicalClockService;


    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected ComponentConfigService cfgService;

    protected Long timestampvar;

    //controll var to stop the looping action
    protected boolean stopsLoop;

    //for DataBase conection I think it uses onr of the existing JDBC datasources. Because there is only one, it uses it
    //http://liquid-reality.de/Karaf-Tutorial/06/
    //https://github.com/cschneider/Karaf-Tutorial/blob/master/db/examplejdbc/src/main/java/net/lr/tutorial/karaf/db/example/DbExample.java
    @Reference
    private DataSource dataSource;


    private Map<String, Object> configurations = null;

    @Activate
    protected void activate(){
        log.info("Started DB App - rik");
    }

    @Override
    public void getSlices() {
        try (Connection con = dataSource.getConnection()) {
            String readTableString = "select * from slice";
            try (PreparedStatement prepStmt = con.prepareStatement(readTableString)) {
                ResultSet rs = prepStmt.executeQuery();
                ResultSetMetaData meta = rs.getMetaData();
                while (rs.next()) {
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        System.out.println(meta.getColumnName(i) + ": " + rs.getString(i));
                    }
                }
            } catch (SQLException throwables) {
                log.info("ERROR reading table slice from DataBase. " + getClass());
                throwables.printStackTrace();
            }
        } catch (SQLException throwables) {
            log.info("ERROR retrieving conection from DataBase in class " + getClass());
            throwables.printStackTrace();
        }
    }

    @Override
    public Map<String, String> getSliceByType(String sliceType){
        try(Connection con = dataSource.getConnection()){
            String readString = "select * from slice where sst_type = '"+sliceType+"' and active = true";
            try(PreparedStatement prepStmt = con.prepareStatement(readString)){
                ResultSet rs = prepStmt.executeQuery();
                ResultSetMetaData meta = rs.getMetaData();
                Map<String, String> slice = new HashMap<String, String>();
                while(rs.next()){
                    for(int i = 1; i <= meta.getColumnCount(); i++){
                        if(meta.getColumnType(i) == -5){
                            //BIG INT
                            slice.put(meta.getColumnName(i), String.valueOf(rs.getLong(i)));
                        }
                        else if (meta.getColumnType(i) == 8){
                            //DOUBLE
                            slice.put(meta.getColumnName(i), String.valueOf(rs.getDouble(i)));
                        }
                        else if (meta.getColumnType(i) == 12){
                            //Var Char
                            slice.put(meta.getColumnName(i), rs.getString(i));
                        }
                        else if (meta.getColumnType(i) == 16){
                            //Boolean
                            slice.put(meta.getColumnName(i), String.valueOf(rs.getBoolean(i)));
                        }
                    }
                }
                return slice;
            } catch (SQLException throwables) {
                log.info("ERROR reading table slice from DataBase. "+getClass());
                throwables.printStackTrace();
            }
        } catch (SQLException throwables) {
            log.info("ERROR retrieving conection from DataBase in class " + getClass());
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, String> getLinkBySrcToDst(String srcURI, String dstURI, int srcPort, int dstPort){
        try(Connection con = dataSource.getConnection()){
            String readString = "select * from link where port_device_uri_src = '"+srcURI+"' and port_device_uri_dst = '"+dstURI+"' and port_number_src = "+srcPort+" and port_number_dst = "+dstPort;
            try(PreparedStatement prepStmt = con.prepareStatement(readString)){
                ResultSet rs = prepStmt.executeQuery();
                ResultSetMetaData meta = rs.getMetaData();
                Map<String, String> link = new HashMap<String, String>();
                while(rs.next()){
                    for(int i = 1; i <= meta.getColumnCount(); i++){
                        if(meta.getColumnType(i) == -5){
                            //BIG INT
                            link.put(meta.getColumnName(i), String.valueOf(rs.getLong(i)));
                        }
                        else if (meta.getColumnType(i) == 8){
                            //DOUBLE
                            link.put(meta.getColumnName(i), String.valueOf(rs.getDouble(i)));
                        }
                        else if (meta.getColumnType(i) == 12){
                            //Var Char
                            link.put(meta.getColumnName(i), rs.getString(i));
                        }
                        else if (meta.getColumnType(i) == 16){
                            //Boolean
                            link.put(meta.getColumnName(i), String.valueOf(rs.getBoolean(i)));
                        }
                    }
                }
                return link;
            } catch (SQLException throwables) {
                log.info("ERROR reading table link from DataBase. "+getClass());
                throwables.printStackTrace();
            }
        } catch (SQLException throwables) {
            log.info("ERROR retrieving conection from DataBase in class " + getClass());
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, String> getSliceByDSCP(int dscp){
        try(Connection con = dataSource.getConnection()){
            String readString = "select * from slice where "+dscp+" = ANY(dscp) and active = true";
            try(PreparedStatement prepStmt = con.prepareStatement(readString)){
                ResultSet rs = prepStmt.executeQuery();
                ResultSetMetaData meta = rs.getMetaData();
                Map<String, String> link = new HashMap<String, String>();
                while(rs.next()){
                    for(int i = 1; i <= meta.getColumnCount(); i++){
                        if(meta.getColumnType(i) == -5){
                            //BIG INT
                            link.put(meta.getColumnName(i), String.valueOf(rs.getLong(i)));
                        }
                        else if (meta.getColumnType(i) == 8){
                            //DOUBLE
                            link.put(meta.getColumnName(i), String.valueOf(rs.getDouble(i)));
                        }
                        else if (meta.getColumnType(i) == 12){
                            //Var Char
                            link.put(meta.getColumnName(i), rs.getString(i));
                        }
                        else if (meta.getColumnType(i) == 16){
                            //Boolean
                            link.put(meta.getColumnName(i), String.valueOf(rs.getBoolean(i)));
                        }
                    }
                }
                return link;
            } catch (SQLException throwables) {
                log.info("ERROR reading table link from DataBase. "+getClass());
                throwables.printStackTrace();
            }
        } catch (SQLException throwables) {
            log.info("ERROR retrieving conection from DataBase in class " + getClass());
            throwables.printStackTrace();
        }
        return null;
    }


    @Override
    public void insertTabel(String table, ArrayList<String> list){
        //update device_machine_relation set
        // device_uri='of:0000000000000001', machine_name='Cubi'
        // where device_uri='of:0000000000000001' and machine_name='Cubi'


        //INSERT INTO table_name (column1, column2, column3, ...)
        //VALUES (value1, value2, value3, ...);

        //String writeConfigString = "update "+table+" set into machine (name) values (?)";
        String writeConfigString = "insert into "+table+" (";
        ArrayList<Integer> tableColumsTypes = new ArrayList<Integer>();

        String readTableString = "select * from "+table;
        try(Connection con = dataSource.getConnection();
            PreparedStatement prepStmt = con.prepareStatement(readTableString)){

            ResultSet rs = prepStmt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();


            System.out.println("Colum names:");
            int i = 1;
            for(; i+1 <= meta.getColumnCount(); i++){
                tableColumsTypes.add(meta.getColumnType(i));
                writeConfigString = writeConfigString +" "+meta.getColumnName(i)+",";
            }
            tableColumsTypes.add(meta.getColumnType(i));
            writeConfigString = writeConfigString +" "+meta.getColumnName(i);
            writeConfigString = writeConfigString +")";


        } catch (SQLException throwables) {
            log.info("ERROR reading table "+table+" from DataBase. "+getClass());
            throwables.printStackTrace();
        }

        writeConfigString = writeConfigString +" values (";

        //VARCHAR is type 2
        //BIGINT is type 5
        //Here if the collum tipe is 12, it means it is a string, so it needs to be between 2 ' '.
        int i = 0;
        for (; i+1< list.size();i++){

            if(tableColumsTypes.get(i) == 12){
                writeConfigString = writeConfigString+" '"+list.get(i)+"',";
            }else{
                writeConfigString = writeConfigString+" "+list.get(i)+",";
            }


        }
        //last one, dunno why not done in the for but ok
        if(tableColumsTypes.get(i) == 12){
            writeConfigString = writeConfigString+" '"+list.get(i)+"'";
        }else{
            writeConfigString = writeConfigString+" "+list.get(i)+"";
        }

        writeConfigString = writeConfigString +")";

        try(Connection con = dataSource.getConnection();
            PreparedStatement prepStmt = con.prepareStatement(writeConfigString)){

            prepStmt.executeUpdate();
            System.out.println("EXEC: "+writeConfigString);

        } catch (SQLException throwables) {
            log.info("ERROR reading table "+table+" from DataBase. "+getClass());
            throwables.printStackTrace();
        }

        System.out.println(" ");
        System.out.println("writeConfigString: "+writeConfigString);
    }


    @Override
    public void updateTabel(String table, ArrayList<String> list){
        //update device_machine_relation set
        // device_uri='of:0000000000000001', machine_name='Cubi'
        // where device_uri='of:0000000000000001' and machine_name='Cubi'


        //String writeConfigString = "update "+table+" set into machine (name) values (?)";
        String writeConfigString = "update "+table+" set";
        ArrayList<String> tableColums = new ArrayList<String>();
        ArrayList<Integer> tableColumsTypes = new ArrayList<Integer>();

        String readTableString = "select * from "+table;
        try(Connection con = dataSource.getConnection();
            PreparedStatement prepStmt = con.prepareStatement(readTableString)){

            ResultSet rs = prepStmt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();

            System.out.println("Colum names:");
            for(int i = 1; i <= meta.getColumnCount(); i++){
                tableColums.add(meta.getColumnName(i).toString());
                tableColumsTypes.add(meta.getColumnType(i));
                System.out.println("type colum"+meta.getColumnType(i));
            }

        } catch (SQLException throwables) {
            log.info("ERROR reading table "+table+" from DataBase. "+getClass());
            throwables.printStackTrace();
        }

        if(tableColums.size() != list.size()){
            System.out.println("ERROR SIZE"); //maybe throw exception
            return;
        }

        //VARCHAR TYPE2
        //BIGINT TYPE-5

        for ( int i = 0; i< tableColums.size();i++){
            if(tableColumsTypes.get(i) == 12){
                writeConfigString = writeConfigString+" "+tableColums.get(i)+"='"+list.get(i)+"'";
            }else{
                writeConfigString = writeConfigString+" "+tableColums.get(i)+"="+list.get(i);
            }
            //if it is not the last one add a comma
            if(i+1 < tableColumsTypes.size()){
                writeConfigString = writeConfigString+",";
            }
        }

        //list of if a collum is a PK or not
        ArrayList<Boolean> listPk = new ArrayList<Boolean>();
        //boolean andB = false;

        //wouldn't it be better to have this at the start of the method?
        if(table.equals("general_conf")){
            listPk.add(true); // 1
            listPk.add(false); // bw wired
            listPk.add(false); // bw mesh
            listPk.add(false); //priority
            listPk.add(false); //timeuout
            listPk.add(false); //appid
            listPk.add(false); //'3-obj-fairness'
        }else if(table.equals("machine")){
            return;
        }else if(table.equals("energy")){
            listPk.add(true); // energy_wired_u
            listPk.add(false); // energy value
            listPk.add(false); // datarate
            listPk.add(false); //cof 0
            listPk.add(false); //cof 1
            listPk.add(false); //cof 2

        }else if(table.equals("device_machine_relation")){
            listPk.add(true); // 'of:001'
            listPk.add(false); //'Cubi'
        }else if(table.equals("service_conf")){
            listPk.add(true); // 'voice'
            listPk.add(false); //3
        }else if(table.equals("service_host_relation")){
            listPk.add(true); // 'macH'
            listPk.add(true); // 'macS'
            listPk.add(false); // 'voice'
        }else{
            return;
        }


        for ( int i = 0; i< tableColums.size();i++){
            if(listPk.get(i) == true){

                if(tableColumsTypes.get(i) == 12){
                    writeConfigString = writeConfigString+" where "+tableColums.get(i)+"='"+list.get(i)+"' and";
                }else {
                    writeConfigString = writeConfigString + " where " + tableColums.get(i) + "=" + list.get(i)+" and";
                }

            }
        }
        writeConfigString = writeConfigString + " 1=1";


        try(Connection con = dataSource.getConnection();
            PreparedStatement prepStmt = con.prepareStatement(writeConfigString)){

            prepStmt.executeUpdate();
            System.out.println("EXEC: "+writeConfigString);

        } catch (SQLException throwables) {
            log.info("ERROR reading table "+table+" from DataBase. "+getClass());
            throwables.printStackTrace();
        }

        //why is this repeated if none of the values are changed?
        if(tableColums.size() != list.size()){
            System.out.println("ERROR SIZE");
            return;
        }


        System.out.println(" ");
        System.out.println("writeConfigString: "+writeConfigString);
    }



/*
    private void innitTableMachine(Connection con) throws SQLException {
        con.setAutoCommit(false);

        String writeConfigString = "insert into machine (name) values (?)";

        String name = null;

        try(PreparedStatement prepStmt = con.prepareStatement(writeConfigString)){

            name = "PI";
            prepStmt.setString(1, name );
            prepStmt.addBatch();

            name = "Cubi";
            prepStmt.setString(1, name );
            prepStmt.addBatch();


            int[] updateCounts = prepStmt.executeBatch();
            con.commit();
            log.info("Machine Table Table Innitialised");


        } catch (BatchUpdateException b) {
            log.info("ERROR (BatchUpdate) in class "+getClass());
            b.printStackTrace();
        } catch (SQLException ex) {
            log.info("ERROR (SQLException) in class "+getClass());
            ex.printStackTrace();
        } finally {
            con.setAutoCommit(true);
        }



    }

*/
    @Override
    public String createSchema() {

        try(Connection con = dataSource.getConnection()){
            creatTables(con);
            insertBestEffortSlice(con);
        }catch (SQLException throwables) {
            log.info("ERROR retriving conection from DataBase in class "+getClass());
            throwables.printStackTrace();
        }

        return "worksCreateDataBase";
    }

    
    @Override
    public String resetSchema() {

        try(Connection con = dataSource.getConnection()){
            dropSchema(con);
            creatTables(con);
            insertBestEffortSlice(con);
        }catch (SQLException throwables) {
            log.info("ERROR retriving conection from DataBase in class "+getClass());
            throwables.printStackTrace();
        }

        return "worksResetDataBase";
    }

    /**
    * Inserts the best effort slice into the database.
    */
    private void insertBestEffortSlice(Connection con) throws SQLException {
        String writeString = "insert into slice (slice_id, use_case, sst_type, dw_bw, up_bw, latency, loss_prob, jitter, dscp, _5qi, active) values (666, 'Best effort slice for unidentified packets.', 'BE', 10000000, 10000000, 20, 15, 10, '{0}', '{9}', true)";
        try(PreparedStatement prepStmt = con.prepareStatement(writeString)){
            prepStmt.execute();
        } catch (SQLException throwables) {
            log.info("ERROR writing the BE slice in table slice from DataBase. "+getClass());
            throwables.printStackTrace();
        }
    }

    /**
     * Drops all the tables in the database.
     * Used by the resetSchema method.
     * @param con Connection to the database.
     * @throws SQLException If there is an error in the SQL query.
     */
    private void dropSchema(Connection con) throws SQLException {
        //get all the tables names
        String getTableName = "select table_name from information_schema.tables where table_schema='public' and table_type='BASE TABLE'";


        try(Statement stmt = con.createStatement()){

            ResultSet rs = stmt.executeQuery(getTableName);

            while(rs.next()){
                String tableName = rs.getString(1);
                String dropTable = "drop table "+tableName+" cascade";

                //drop each table
                try(Statement stmt2 = con.createStatement()){
                    stmt2.executeUpdate(dropTable);
                }catch (SQLException throwables) {
                    log.info("ERROR retrieving all table names from DataBase. "+getClass());
                    throwables.printStackTrace();
                }
            }

            log.info("Successfully drop all tables ");

        }catch (SQLException throwables) {
            log.info("ERROR retrieving all table names from DataBase. "+getClass());
            throwables.printStackTrace();
        }


    }

    /**
     * Creates all the tables in the database.
     * @param con Connection to the database.
     * @throws SQLException If there is an error in the SQL query.
     */
    private void creatTables(Connection con) throws SQLException {

        String createTables = "CREATE TABLE flowrule (\n" +
                "\trule_id\t\t VARCHAR(512),\n" +
                "\tstate\t\t VARCHAR(512),\n" +
                "\tbytes\t\t BIGINT,\n" +
                "\tpackets\t\t BIGINT,\n" +
                "\tduration\t\t BIGINT,\n" +
                "\tpriority\t\t BIGINT,\n" +
                "\ttable_name\t\t VARCHAR(512),\n" +
                "\tapp_id\t\t BIGINT,\n" +
                "\tgroup_id\t\t BIGINT,\n" +
                "\ttimeout\t\t BIGINT,\n" +
                "\thard_timeout\t BIGINT,\n" +
                "\tpermanent\t\t BOOL,\n" +
                "\tselector\t\t VARCHAR(1024),\n" +
                "\ttreatment\t\t VARCHAR(1024),\n" +
                "\tdevice_timestamp_sim BIGINT,\n" +
                "\tdevice_uri\t\t VARCHAR(512),\n" +
                "\tPRIMARY KEY(rule_id,device_timestamp_sim,device_uri)\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE machine (\n" +
                "\tname VARCHAR(512),\n" +
                "\tPRIMARY KEY(name)\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE energy(\n" +
                "\tname\t VARCHAR(512),\n" +
                "\tenergy_value DOUBLE PRECISION GENERATED ALWAYS AS ( cof_degree_2*(datarate^2) + cof_degree_1*datarate + cof_degree_0 ) STORED,\n" +
                "\tdatarate\t DOUBLE PRECISION,\n" +
                "\tcof_degree_0\t DOUBLE PRECISION,\n" +
                "\tcof_degree_1\t DOUBLE PRECISION,\n" +
                "\tcof_degree_2\t DOUBLE PRECISION,\n" +
                "\tmachine_name VARCHAR(512),\n" +
                "\tPRIMARY KEY(name,machine_name)\n" +
                ");\n" +
                "ALTER TABLE energy ADD CONSTRAINT energy_fk1 FOREIGN KEY (machine_name) REFERENCES machine(name);\n" +
                "\n" +
                "\n" +
                "\n" +
                "CREATE TABLE service_conf (\n" +
                "\tname\t VARCHAR(512),\n" +
                "\tdatarate_mbs DOUBLE PRECISION,\n" +
                "\tPRIMARY KEY(name)\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE service_host_relation (\n" +
                "\tmac_src\t\t VARCHAR(512),\n" +
                "\tmac_dst\t\t VARCHAR(512),\n" +
                "\tservice_conf_name VARCHAR(512),\n" +
                "\tPRIMARY KEY(mac_src,mac_dst)\n" +
                ");\n" +
                "ALTER TABLE service_host_relation ADD CONSTRAINT service_host_relation_fk1 FOREIGN KEY (service_conf_name) REFERENCES service_conf(name);\n" +
                "\n" +
                "CREATE TABLE device_machine_relation (\n" + //why not just connect the device to the machine?
                "\tdevice_uri\t VARCHAR(512),\n" +
                "\tmachine_name VARCHAR(512),\n" +
                "\tPRIMARY KEY(device_uri)\n" +
                ");\n" +
                "ALTER TABLE device_machine_relation ADD CONSTRAINT device_machine_relation_fk1 FOREIGN KEY (machine_name) REFERENCES machine(name);\n" +
                "\n" +
                "\n" +
                "CREATE TABLE general_conf(\n" +
                "\tpkey\t\t BIGINT DEFAULT 1,\n" +
                "\twired_bw_mbs\t BIGINT NOT NULL DEFAULT 1000,\n" +
                "\twireless_bw_mbs BIGINT NOT NULL DEFAULT 54,\n" +
                "\tflowrule_priority BIGINT NOT NULL DEFAULT 16,\n" +
                "\tflowrule_timeout\t BIGINT NOT NULL DEFAULT 10,\n" +
                "\tflowrule_appid\t BIGINT NOT NULL DEFAULT 24,\n" +
                "\tdefault_fw_algorithm VARCHAR(512) NOT NULL DEFAULT '3-obj-fairness',\n" +
                "\tPRIMARY KEY(pkey)\n" +
                ");\n" +
                "ALTER TABLE general_conf ADD CONSTRAINT constraint_0 CHECK (pkey = 1);\n" +
                "ALTER TABLE general_conf ADD CONSTRAINT constraint_1 CHECK (default_fw_algorithm = 'onos-k-short' OR\n" +
                "default_fw_algorithm = '3-obj-fairness');\n" +
                "\n" +
                "\n" +
                "\n" +
                "CREATE TABLE device (\n" +
                "\ttimestamp_sim BIGINT,\n" +
                "\turi\t\t VARCHAR(512),\n" +
                "\tdevice_type\t VARCHAR(512) NOT NULL,\n" +
                "\tmanufacturer\t VARCHAR(512) NOT NULL,\n" +
                "\thw_version\t VARCHAR(512) NOT NULL,\n" +
                "\tsw_version\t VARCHAR(512) NOT NULL,\n" +
                "\tserial_number VARCHAR(512) NOT NULL,\n" +
                "\tdriver\t VARCHAR(512),\n" +
                "\tchassis_id\t BIGINT NOT NULL,\n" +
                "\tis_available\t BOOL NOT NULL,\n" +
                "\tPRIMARY KEY(timestamp_sim,uri)\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE host (\n" +
                "\ttimestamp_sim BIGINT,\n" +
                "\tmac\t\t VARCHAR(512),\n" +
                "\tvlan_id\t SMALLINT NOT NULL,\n" +
                "\tinner_vlan_id SMALLINT NOT NULL,\n" +
                "\ttp_id\t VARCHAR(512) NOT NULL,\n" +
                "\tPRIMARY KEY(timestamp_sim,mac)\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE location (\n" +
                "\ttable_id\t serial,\n" +
                "\testablished_time\t\t BIGINT NOT NULL,\n" +
                "\tport_number\t\t BIGINT NOT NULL,\n" +
                "\tport_device_timestamp_sim BIGINT NOT NULL,\n" +
                "\tport_device_uri\t\t VARCHAR(512) NOT NULL,\n" +
                "\thost_timestamp_sim\t BIGINT NOT NULL,\n" +
                "\thost_mac\t\t\t VARCHAR(512) NOT NULL,\n" +
                "\tPRIMARY KEY(table_id)\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE interface (\n" +
                "\tip_adress\t\t VARCHAR(512),\n" + //address badly written
                "\thost_timestamp_sim BIGINT NOT NULL,\n" +
                "\thost_mac\t\t VARCHAR(512) NOT NULL,\n" +
                "\tPRIMARY KEY(ip_adress,host_timestamp_sim,host_mac)\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE link (\n" +
                "\tlink_type\t\t\t VARCHAR(512) NOT NULL,\n" +
                "\tlink_state\t\t VARCHAR(512) NOT NULL,\n" +
                "\tbw\t\t\t DOUBLE PRECISION NOT NULL,\n" + //started adding here
                "\tlatency\t\t\t DOUBLE PRECISION NOT NULL,\n" +
                "\tjitter\t\t\t DOUBLE PRECISION NOT NULL,\n" +
                "\tloss_prob\t\t DOUBLE PRECISION NOT NULL,\n" +
                "\tenergy_consumption\t\t DOUBLE PRECISION NOT NULL,\n" + //ended adding here
                "\tport_number_src\t\t BIGINT NOT NULL,\n" +
                "\tport_number_dst\t\t BIGINT NOT NULL,\n" +
                "\tport_device_timestamp_sim_src\t BIGINT NOT NULL,\n" +
                "\tport_device_timestamp_sim_dst BIGINT NOT NULL,\n" +
                "\tport_device_uri_src\t\t VARCHAR(512) NOT NULL,\n" +
                "\tport_device_uri_dst\t\t VARCHAR(512) NOT NULL,\n" +
                "\tPRIMARY KEY(port_number_src,port_device_uri_src, port_device_timestamp_sim_src, port_number_dst, port_device_uri_dst, port_device_timestamp_sim_dst)\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE packet_statistics (\n" +
                "\trate\t\t\t BIGINT NOT NULL,\n" +
                "\tlatest_counter\t\t BIGINT,\n" +
                "\tis_valid\t\t\t BOOL,\n" +
                "\tcapture_time\t\t BIGINT NOT NULL,\n" +
                "\tpackets_received\t\t BIGINT,\n" +
                "\tpackets_sent\t\t BIGINT,\n" +
                "\tport_alive_time_sec\t BIGINT,\n" +
                "\tdrop_count_rx\t\t BIGINT,\n" +
                "\tdrop_count_tx\t\t BIGINT,\n" +
                "\terror_count_rx\t\t BIGINT,\n" +
                "\terror_count_tx\t\t BIGINT,\n" +
                "\tport_number\t\t BIGINT,\n" +
                "\tport_device_timestamp_sim BIGINT,\n" +
                "\tport_device_uri\t\t VARCHAR(512),\n" +
                "\tPRIMARY KEY(port_number,port_device_timestamp_sim,port_device_uri)\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE port (\n" +
                "\tnumber\t\t BIGINT,\n" +
                "\tspeed\t\t BIGINT NOT NULL,\n" +
                "\ttype\t\t VARCHAR(512) NOT NULL,\n" +
                "\tenabled\t\t BOOL NOT NULL,\n" +
                "\tname\t\t VARCHAR(512) NOT NULL,\n" +
                "\tdevice_timestamp_sim BIGINT,\n" +
                "\tdevice_uri\t\t VARCHAR(512),\n" +
                "\tPRIMARY KEY(number,device_timestamp_sim,device_uri)\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE byte_statistics (\n" +
                "\trate\t\t\t BIGINT,\n" +
                "\tlatest_counter\t\t BIGINT,\n" +
                "\tis_valid\t\t\t BOOL,\n" +
                "\tcapture_time\t\t BIGINT,\n" +
                "\tbytes_received\t\t BIGINT,\n" +
                "\tbytes_sent\t\t BIGINT,\n" +
                "\tport_alive_time_sec\t BIGINT,\n" +
                "\tport_number\t\t BIGINT,\n" +
                "\tport_device_timestamp_sim BIGINT,\n" +
                "\tport_device_uri\t\t VARCHAR(512),\n" +
                "\tPRIMARY KEY(port_number,port_device_timestamp_sim,port_device_uri)\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE slice (\n" + //start adding here
                "\tslice_id\t\t BIGSERIAL,\n" +
                "\tuse_case\t\t VARCHAR(512),\n" +
                "\tsst_type\t\t VARCHAR(512) NOT NULL,\n" +
                "\tdw_bw\t\t\t DOUBLE PRECISION NOT NULL,\n" +
                "\tup_bw\t\t\t DOUBLE PRECISION NULL,\n" +
                "\tlatency\t\t\t DOUBLE PRECISION NOT NULL,\n" +
                "\tloss_prob\t\t DOUBLE PRECISION NOT NULL,\n" +
                "\tjitter\t\t\t DOUBLE PRECISION NOT NULL,\n" +
                "\tdscp\t\t\t INTEGER[] NOT NULL,\n" +
                "\t_5qi\t\t\t INTEGER[] NOT NULL,\n" +
                "\tactive\t\t\t BOOL NOT NULL,\n" +
                "\tPRIMARY KEY(slice_id)\n" +
                ");\n" +
                "\n" + //ended adding here
                "ALTER TABLE device ADD CONSTRAINT constraint_0 CHECK (device_type='SWITCH' OR\n" +
                "device_type='ROUTER' OR\n" +
                "device_type='ROADM' OR\n" +
                "device_type='OTN' OR\n" +
                "device_type='ROADM_OTN' OR\n" +
                "device_type='FIREWALL' OR\n" +
                "device_type='BALANCER' OR\n" +
                "device_type='IPS' OR\n" +
                "device_type='IDS' OR\n" +
                "device_type='CONTROLLER' OR\n" +
                "device_type='VIRTUAL' OR\n" +
                "device_type='FIBER_SWITCH' OR\n" +
                "device_type='MICROWAVE' OR\n" +
                "device_type='OLT' OR\n" +
                "device_type='ONU' OR\n" +
                "device_type='OPTICAL_AMPLIFIER' OR\n" +
                "device_type='OLS' OR\n" +
                "device_type='TERMINAL_DEVICE' OR\n" +
                "device_type='OTHER' OR\n" +
                "device_type='SERVER');\n" +
                "ALTER TABLE host ADD CONSTRAINT constraint_tpid CHECK (tp_id ='arp' OR\n" +
                "tp_id ='rarp' OR\n" +
                "tp_id ='ipv4' OR\n" +
                "tp_id ='ipv6' OR\n" +
                "tp_id ='lldp' OR\n" +
                "tp_id ='vlan' OR\n" +
                "tp_id ='qinq' OR\n" +
                "tp_id ='bddp' OR\n" +
                "tp_id ='mpls_unicast' OR\n" +
                "tp_id ='mpls_multicast' OR\n" +
                "tp_id ='eapol' OR\n" +
                "tp_id ='pppoed' OR\n" +
                "tp_id ='slow' OR\n" +
                "tp_id ='unknown');\n" +
                "ALTER TABLE location ADD CONSTRAINT location_fk1 FOREIGN KEY (port_number,port_device_uri,port_device_timestamp_sim) REFERENCES port(number,device_uri,device_timestamp_sim);\n" +
                "ALTER TABLE location ADD CONSTRAINT location_fk4 FOREIGN KEY (host_mac,host_timestamp_sim) REFERENCES host(mac,timestamp_sim);\n" +
                "ALTER TABLE interface ADD CONSTRAINT interface_fk1 FOREIGN KEY (host_mac,host_timestamp_sim) REFERENCES host(mac,timestamp_sim);\n" +
                "\n" +
                "\n" +
                "ALTER TABLE link ADD CONSTRAINT link_fk1 FOREIGN KEY (port_number_src,port_device_uri_src, port_device_timestamp_sim_src) REFERENCES port(number,device_uri,device_timestamp_sim);\n" +
                "ALTER TABLE link ADD CONSTRAINT link_fk3 FOREIGN KEY (port_number_dst,port_device_uri_dst, port_device_timestamp_sim_dst) REFERENCES port(number,device_uri,device_timestamp_sim);\n" +
                "\n" +
                "ALTER TABLE link ADD CONSTRAINT constraint_link_state CHECK (link_state='ACTIVE' OR\n" +
                "link_state='INACTIVE');\n" +
                "ALTER TABLE link ADD CONSTRAINT constraint_link_type CHECK (link_type='DIRECT' OR\n" +
                "link_type='INDIRECT' OR\n" +
                "link_type='EDGE' OR\n" +
                "link_type='TUNNEL' OR\n" +
                "link_type='OPTICAL' OR\n" +
                "link_type='VIRTUAL');\n" +
                "ALTER TABLE packet_statistics ADD CONSTRAINT packet_statistics_fk1 FOREIGN KEY (port_number,port_device_timestamp_sim,port_device_uri) REFERENCES port(number,device_timestamp_sim,device_uri);\n" +
                "ALTER TABLE port ADD CONSTRAINT port_fk1 FOREIGN KEY (device_uri,device_timestamp_sim) REFERENCES device(uri,timestamp_sim);\n" +
                "ALTER TABLE byte_statistics ADD CONSTRAINT byte_statistics_fk1 FOREIGN KEY (port_number,port_device_timestamp_sim,port_device_uri) REFERENCES port(number,device_timestamp_sim,device_uri);\n" +
                "\n" +
                "ALTER TABLE energy ADD CONSTRAINT constraint_0 CHECK (name='energy_wired_u' OR\n" +
                "name='energy_wired_d' OR\n" +
                "name='energy_wireless_d' OR\n" +
                "name='energy_wireless_u' OR\n" +
                "name='energy_wired_idle' OR\n" +
                "name='energy_wireless_idle' OR\n" +
                "name='energy_cpu');\n" +
                "ALTER TABLE flowrule ADD CONSTRAINT flowrule_fk1 FOREIGN KEY (device_timestamp_sim, device_uri) REFERENCES device(timestamp_sim,uri);\n";

        try(Statement stmt = con.createStatement()){
            stmt.executeUpdate(createTables);

            log.info("Successfully created tables ");

        }catch (SQLException throwables) {
            log.info("ERROR creating tables in DataBase"+getClass());
            throwables.printStackTrace();
        }

    }

    @Override
    public String innitInterTables(){
        try(Connection con = dataSource.getConnection()){
            innitTableGeneralConfig(con);
            innitTableMachine(con);
            innitTableEnergy(con);
            innitTableDeviceMachineRelationInter(con);
            innitTableServiceConfig(con);
            //innitTableServiceHostRelationIntra(con);
            //innitTableServiceHostRelationInter(con);

        }catch (SQLException throwables) {
            log.info("ERROR retriving conection from DataBase in class "+getClass());
            throwables.printStackTrace();
        }

        return "workInnit IntraDataBase";
    }

    @Override
    public String innitIntraTables(){
        try(Connection con = dataSource.getConnection()){
            innitTableGeneralConfig(con);
            innitTableMachine(con);
            innitTableEnergy(con);
            innitTableDeviceMachineRelationIntra(con);

            innitTableServiceConfig(con);
            innitTableServiceHostRelationIntra(con);

        }catch (SQLException throwables) {
            log.info("ERROR retriving conection from DataBase in class "+getClass());
            throwables.printStackTrace();
        }

        return "workInnit IntraDataBase";
    }


    @Override
    public String innitDemoTables(){
        try(Connection con = dataSource.getConnection()){
            innitTableGeneralConfig(con);
            innitTableMachine(con);
            innitTableEnergy(con);

            innitTableDeviceMachineRelationDemo(con);

            innitTableServiceConfig(con);
            innitTableServiceHostRelationDemo(con);

        }catch (SQLException throwables) {
            log.info("ERROR retriving conection from DataBase in class "+getClass());
            throwables.printStackTrace();
        }

        return "workInnit IntraDataBase";
    }


    @Override
    public String innitNoeTables(){
        try(Connection con = dataSource.getConnection()){
            innitTableGeneralConfig(con);
            innitTableMachine(con);
            innitTableEnergy(con);
            innitTableDeviceMachineRelationIntra(con);

            innitTableServiceConfig(con);
            innitTableServiceHostRelationIntraNoe(con);

        }catch (SQLException throwables) {
            log.info("ERROR retriving conection from DataBase in class "+getClass());
            throwables.printStackTrace();
        }

        return "workInnit IntraDataBase";
    }

    @Override
    public void readTable(String table){

        String readTableString = "select * from "+table;


        try(Connection con = dataSource.getConnection();
            PreparedStatement prepStmt = con.prepareStatement(readTableString)){

            ResultSet rs = prepStmt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();


            System.out.println("Colum names:");
            for(int i = 1; i <= meta.getColumnCount(); i++){
                System.out.print(meta.getColumnName(i).toString()+";");
            }
            System.out.println();

            while (rs.next()) {
                for(int i = 1; i <= meta.getColumnCount(); i++) {
                    System.out.print(rs.getString(i)+"; ");
                }
                System.out.println();
            }
            System.out.println();

        } catch (SQLException throwables) {
            log.info("ERROR reading table "+table+" from DataBase. "+getClass());
            throwables.printStackTrace();
        }

    }


    @Override
    public void MetricsBytesTime(String uriStr, String time){

        //converts time to from a String to a Double
        double time_sim = Double.parseDouble(time);

        //This first query is kinda usselles, only detects if the port table for a sepecific
        //device is empty.
        //Selects everything from the table port where the number is greater than 0 and the params are related
        int numPorts;
        //SELECT * from port where number>0 and device_timestamp_sim = '1661376166485' and device_uri = 'of:0000000000000001'
        String readTableString2 = "SELECT COUNT(*) from port where number>0 and device_uri = '"+uriStr+"' and \n" +
                "device_timestamp_sim = "+time_sim+" ";
        try(Connection con = dataSource.getConnection();
            PreparedStatement prepStmt = con.prepareStatement(readTableString2)){

            ResultSet rs = prepStmt.executeQuery();

            if(rs.next() == false){
                throw new Exception();
            }

            //0 : idk what it is
            //1 : portNumber
            numPorts = rs.getInt(1); //This is never used.

        } catch (SQLException throwables) {
            log.info("ERROR reading count from DataBase. "+getClass());
            throwables.printStackTrace();
            return;
        } catch (Exception e) {
            log.info("ERROR, port table is empty, no count value");
            return;
        }

        System.out.println(":5:"+time_sim); //why is this here?

        //port_device_uri uriStr
        //port
        ArrayList<Integer> packetsSent = new ArrayList<Integer>();
        float sumSent = 0;
        //Selects the packets_sent from the table packet_statistics where the port_number is greater than 0 and the params are related ordered by the number of the port
        String readTableString3 = "SELECT packets_sent from packet_statistics where port_number>0 and \n" +
                "port_device_timestamp_sim = "+time_sim+" and port_device_uri = '"+uriStr+"'\n" +
                "order by port_number\n";

        try(Connection con = dataSource.getConnection();
            PreparedStatement prepStmt = con.prepareStatement(readTableString3)){

            ResultSet rs = prepStmt.executeQuery();

            while (rs.next()) {
                //0 : idk what it is
                //1 : sim value
                int sent = rs.getInt(1);
                packetsSent.add(sent);
                sumSent+=sent;
            }

        } catch (SQLException throwables) {
            log.info("ERROR reading table service_host_relation from DataBase. "+getClass());
            throwables.printStackTrace();
            return;
        } catch (Exception e) {
            log.info("ERROR, service_host_relation table is empty, using default values for port speed");
            return;
        }

        System.out.println("Array"+packetsSent.toString());
        for ( int i = 0;i<packetsSent.size();i++ ) {
            System.out.println("Port "+(i+1)+": "+packetsSent.get(i)/sumSent);
        }

    }


    @Override
    public void MetricsBytes(String uriStr){

        //As the time_sim is not a param calculates the max value of the time_sim
        String readTableString = "SELECT MAX(port_device_timestamp_sim) FROM packet_statistics";
        double time_sim;
        try(Connection con = dataSource.getConnection();
            PreparedStatement prepStmt = con.prepareStatement(readTableString)){

            ResultSet rs = prepStmt.executeQuery();

            if(rs.next() == false){
                throw new Exception();
            }

            //0 : idk what it is
            //1 : sim value
            time_sim = rs.getDouble(1);

        } catch (SQLException throwables) {
            log.info("ERROR reading amx from DataBase. "+getClass());
            throwables.printStackTrace();
            return;
        } catch (Exception e) {
            log.info("ERROR, port_device_timestamp_sim table is empty,no Max value");
            return;
        }

        //This first query is kinda usselles, only detects if the port table for a sepecific
        //device is empty.
        //Selects everything from the table port where the number is greater than 0 and the params are related
        int numPorts;
        //SELECT * from port where number>0 and device_timestamp_sim = '1661376166485' and device_uri = 'of:0000000000000001'
        String readTableString2 = "SELECT COUNT(*) from port where number>0 and device_timestamp_sim = "+ time_sim +" \n" +
                "and device_uri = '"+uriStr+"'";
        try(Connection con = dataSource.getConnection();
            PreparedStatement prepStmt = con.prepareStatement(readTableString2)){

            ResultSet rs = prepStmt.executeQuery();

            if(rs.next() == false){
                throw new Exception();
            }

            //0 : idk what it is
            //1 : portNumber
            numPorts = rs.getInt(1);
        } catch (SQLException throwables) {
            log.info("ERROR reading count from DataBase. "+getClass());
            throwables.printStackTrace();
            return;
        } catch (Exception e) {
            log.info("ERROR, port table is empty, no count value");
            return;
        }

        //port_device_uri uriStr
        //port
        ArrayList<Integer> packetsSent = new ArrayList<Integer>();
        float sumSent = 0;
        String readTableString3 = "SELECT packets_sent from packet_statistics where port_number>0 and \n" +
                "port_device_timestamp_sim = "+time_sim+" and port_device_uri = '"+uriStr+"'\n" +
                "order by port_number\n";
        try(Connection con = dataSource.getConnection();
            PreparedStatement prepStmt = con.prepareStatement(readTableString3)){

            ResultSet rs = prepStmt.executeQuery();


            while (rs.next()) {
                //0 : idk what it is
                //1 : sim value
                int sent = rs.getInt(1);
                packetsSent.add(sent);
                sumSent+=sent;
            }



        } catch (SQLException throwables) {
            log.info("ERROR reading table service_host_relation from DataBase. "+getClass());
            throwables.printStackTrace();
            return;
        } catch (Exception e) {
            log.info("ERROR, service_host_relation table is empty, using default values for port speed");
            return;
        }

        System.out.println("Array"+packetsSent.toString());
        for ( int i = 0;i<packetsSent.size();i++ ) {
            System.out.println("Port "+(i+1)+": "+packetsSent.get(i)/sumSent);
        }
    }


    @Override
    public void stopsLoop() {
        stopsLoop = true;
    }


    @Override
    public String writeLoop(long eachSec) {

        LoadsDBConfigurations();

        log.info("Starting looping action");
        stopsLoop = false;
        while(stopsLoop == false){


            timestampvar = hybridLogicalClockService.timeNow().logicalTime();
            System.out.println("Takes a snapshot at:" + timestampvar);
            //System.out.println("timeSnap:"+timestampvar);
            try(Connection con = dataSource.getConnection()){

                writeHosts(con);
                writeAdresses(con);
                writeDevices(con);
                writePorts(con);
                writeLocations(con);
                writeLinks(con);
                writeByteStat(con);
                writePacketStat(con);
                //writeServiceHistory(con);
                writeFlowRules(con);

            }catch (SQLException throwables) {
                log.info("ERROR retriving conection from DataBase in class "+getClass());
                throwables.printStackTrace();
            }
            try {
                TimeUnit.SECONDS.sleep(eachSec);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return "worksWriteDataBaseLoop";
    }



    @Override
    public String write() {

        LoadsDBConfigurations();

        timestampvar = hybridLogicalClockService.timeNow().logicalTime();

        log.info("timestampvar::"+timestampvar);
        try(Connection con = dataSource.getConnection()){

            writeHosts(con);
            writeAdresses(con);
            writeDevices(con);
            writePorts(con);
            writeLocations(con);
            writeLinks(con);
            writeByteStat(con);
            writePacketStat(con);
            //writeServiceHistory(con);
            writeFlowRules(con);

        }catch (SQLException throwables) {
            log.info("ERROR retriving conection from DataBase in class "+getClass());
            throwables.printStackTrace();
        }

        return "worksWriteDataBase";
    }


    /**
     * Writes the hosts to the database.
     * @param con Connection to the database.
     * @throws SQLException If there is an error in the SQL query.
     */
    private void writeHosts(Connection con) throws SQLException {
        con.setAutoCommit(false);

        String writeDevicesString = "insert into host (timestamp_sim, mac, vlan_id, inner_vlan_id, tp_id) values (?,?,?,?,?)";

        Long timestamp_sim;
        String mac = null;
        Short vlanId = null;
        Short innerVlanId = null;
        String tpId = null;


        try(PreparedStatement prepStmt = con.prepareStatement(writeDevicesString)){

            Iterable<Host> hosts = hostService.getHosts();

            //Parse info
            for (Host h : hosts) {

                timestamp_sim = timestampvar;
                mac = h.mac().toString();
                vlanId = h.vlan().toShort();
                innerVlanId = h.innerVlan().toShort();
                tpId = h.tpid().toString();


                //Write in DB
                prepStmt.setLong(1, timestamp_sim );
                prepStmt.setString(2, mac );
                prepStmt.setShort(3, vlanId );
                prepStmt.setShort(4, innerVlanId);
                prepStmt.setString(5, tpId);

                prepStmt.addBatch();
            }

            //int[] updateCounts = prepStmt.executeBatch();
            prepStmt.executeBatch();
            con.commit();
            log.info("Hosts Inserted");


        } catch (BatchUpdateException b) {
            log.info("ERROR (BatchUpdate) in class "+getClass());
            b.printStackTrace();
        } catch (SQLException ex) {
            log.info("ERROR (SQLException) in class "+getClass());
            ex.printStackTrace();
        } finally {
            con.setAutoCommit(true);
        }

    }


    //needs Hosts + service_host_relation
    /*private void writeServiceHistory(Connection con) throws SQLException {
        con.setAutoCommit(false);

        String writeDevicesString = "insert into service_history (service_name,host_timestamp_sim_src,host_mac_src, host_timestamp_sim_dst,host_mac_dst) values (?,?,?,?,?)";

        String service_name = null;
        Long host_timestamp_sim_src;
        String host_mac_src = null;
        Long host_timestamp_sim_dst;
        String host_mac_dst = null;



        try(PreparedStatement prepStmt = con.prepareStatement(writeDevicesString)) {


            Iterable<Host> hosts = hostService.getHosts();

            //Parse info
            for (Host h : hosts) {

                host_mac_src =
                host_mac_dst =
                service_name = getService(host_mac_src, host_mac_dst);

                host_timestamp_sim_src = timestampvar;
                host_timestamp_sim_dst = timestampvar;

                //Write in DB
                prepStmt.setString(1, service_name);
                prepStmt.setLong(2, host_timestamp_sim_src);
                prepStmt.setString(3, host_mac_src);
                prepStmt.setLong(4, host_timestamp_sim_dst);
                prepStmt.setString(5, host_mac_dst);

                prepStmt.addBatch();

            }


            //Commented from here
                String readTableString = "select * from service_host_relation";
            PreparedStatement prepStmtRead = con.prepareStatement(readTableString);

            ResultSet rs = prepStmtRead.executeQuery();

            while (rs.next() == true) {

                //0 : idk what it is
                //1 : mac_src
                host_mac_src = rs.getString(1);
                //2 : mac_dst
                host_mac_dst = rs.getString(2);
                //3 service_conf_name
                service_name = rs.getString(3);


            }
            //to here

            int[] updateCounts = prepStmt.executeBatch();
            con.commit();
            log.info("ServiceHistory Inserted");

        } catch (BatchUpdateException b) {
            log.info("ERROR (BatchUpdate) in class "+getClass());
            b.printStackTrace();
        } catch (SQLException ex) {
            log.info("ERROR (SQLException) in class "+getClass());
            ex.printStackTrace();
        } finally {
            con.setAutoCommit(true);
        }
    }*/


    //Needs Host's FK
    /**
     * Writes the adresses to the database.
     * @param con Connection to the database.
     * @throws SQLException If there is an error in the SQL query.
     */
    private void writeAdresses(Connection con) throws SQLException {
        con.setAutoCommit(false);

        String writeDevicesString = "insert into interface (ip_adress,  host_timestamp_sim, host_mac) values (?,?,?)";


        String ipAdress = null;
        Long  timestamp_sim;
        String hostMac = null;


        try(PreparedStatement prepStmt = con.prepareStatement(writeDevicesString)){

            Iterable<Host> hosts = hostService.getHosts();


            //Parse info
            for (Host h : hosts) {

                hostMac = h.mac().toString();
                timestamp_sim = timestampvar;
                Set<IpAddress> adresses = h.ipAddresses();

                for(IpAddress adress : adresses){

                    ipAdress = adress.toString();


                    //Write in DB
                    prepStmt.setString(1, ipAdress );
                    prepStmt.setLong(2, timestamp_sim );
                    prepStmt.setString(3, hostMac );

                    prepStmt.addBatch();

                }

            }

            //int[] updateCounts = prepStmt.executeBatch();
            prepStmt.executeBatch();
            con.commit();
            log.info("IpAdresses Inserted");

        } catch (BatchUpdateException b) {
            log.info("ERROR (BatchUpdate) in class "+getClass());
            b.printStackTrace();
        } catch (SQLException ex) {
            log.info("ERROR (SQLException) in class "+getClass());
            ex.printStackTrace();
        } finally {
            con.setAutoCommit(true);
        }

    }

    /**
     * Writes the devices data to the database.
     * @param con Connection to the database.
     * @throws SQLException If there is an error in the SQL query.
     */
    private void writeDevices(Connection con) throws SQLException {
        con.setAutoCommit(false);

        String writeDevicesString = "insert into device (timestamp_sim,uri, device_type, manufacturer, " +
                "hw_version, sw_version, serial_number, driver, chassis_id, is_available) values (?,?,?,?,?,?,?,?,?,?)";

        Long timestamp_sim;
        String deviceUri = null;
        String deviceType = null;
        String manufacturer = null;
        String hwVersion = null;
        String swVersion = null;
        String serialNumber = null;
        String driver = null;
        Long chassisId = null;
        Boolean isAvailable  = null;
        //String machine_name  = null;

        try(PreparedStatement prepStmt = con.prepareStatement(writeDevicesString)){

            Iterable<Device> devices = deviceService.getAvailableDevices();

            //Parse info
            for (Device d : devices){
                timestamp_sim = timestampvar;
                deviceUri = d.id().uri().toString();
                deviceType = d.type().toString();
                manufacturer = d.manufacturer();
                hwVersion = d.hwVersion();
                swVersion = d.swVersion();
                serialNumber = d.serialNumber();
                driver = driverService.getDriver(d.id()).name();
                chassisId = d.chassisId().id();
                isAvailable = deviceService.isAvailable(d.id());
                //machine_name = getMachine(deviceUri);

                //Write in DB
                prepStmt.setLong(1, timestamp_sim );
                prepStmt.setString(2, deviceUri );
                prepStmt.setString(3, deviceType );
                prepStmt.setString(4, manufacturer);
                prepStmt.setString(5, hwVersion);
                prepStmt.setString(6, swVersion);
                prepStmt.setString(7, serialNumber);
                prepStmt.setString(8, driver);
                prepStmt.setLong(9, chassisId );
                prepStmt.setBoolean(10, isAvailable);
                //prepStmt.setString(11, machine_name);
                prepStmt.addBatch();
            }

            //int[] updateCounts = prepStmt.executeBatch();
            prepStmt.executeBatch();
            con.commit();
            log.info("Devices Inserted");


        } catch (BatchUpdateException b) {
            log.info("ERROR (BatchUpdate) in class "+getClass());
            b.printStackTrace();
        } catch (SQLException ex) {
            log.info("ERROR (SQLException) in class "+getClass());
            ex.printStackTrace();
        } finally {
            con.setAutoCommit(true);
        }

    }

    //Needs Device's FK
    /**
     * Writes the ports to the database (THE PORT SPEED IS SAVED WITH A STATIC VALUE).
     * @param con Connection to the database.
     * @throws SQLException If there is an error in the SQL query.
     */
    private void writePorts(Connection con) throws SQLException {
        con.setAutoCommit(false);

        String writePortsString = "insert into port (number, speed, type, enabled, name, device_timestamp_sim, device_uri) values (?,?,?,?,?,?,?)";


        Long number = null;
        Long speed = null;
        String type = null;
        Boolean enabled = null;
        String name = null;
        Long device_timestamp_sim;
        String deviceUri = null;


        try(PreparedStatement prepStmt = con.prepareStatement(writePortsString)){

            Iterable<Device> devices = deviceService.getAvailableDevices();

            //Parse info
            for (Device d : devices){



                List<Port> ports = deviceService.getPorts(d.id());

                for(Port p : ports){

                    number = p.number().toLong();

                    type = p.type().toString();
                    enabled = p.isEnabled();
                    name = nameAjust(p);
                    device_timestamp_sim = timestampvar;
                    deviceUri = d.id().uri().toString();

                    speed = speedAjust(p,name);

                    //Write in DB
                    prepStmt.setLong(1, number );
                    prepStmt.setLong(2, speed );
                    prepStmt.setString(3, type);
                    prepStmt.setBoolean(4, enabled);
                    prepStmt.setString(5, name);
                    prepStmt.setLong(6, device_timestamp_sim);
                    prepStmt.setString(7, deviceUri);
                    prepStmt.addBatch();

                }
            }

            //int[] updateCounts = prepStmt.executeBatch();
            prepStmt.executeBatch();
            con.commit();
            log.info("Ports Inserted");


        } catch (BatchUpdateException b) {
            log.info("ERROR (BatchUpdate) in class "+getClass());
            b.printStackTrace();
        } catch (SQLException ex) {
            log.info("ERROR (SQLException) in class "+getClass());
            ex.printStackTrace();
        } finally {
            con.setAutoCommit(true);
        }

    }

    //Needs Host's FK
    //Needs Device's FK
    /**
     * Writes the historical connections of each host in the topology (locations) to the database.
     * @param con Connection to the database.
     * @throws SQLException If there is an error in the SQL query.
     */
    private void writeLocations(Connection con) throws SQLException {
        con.setAutoCommit(false);

        String writeDevicesString = "insert into location (established_time, port_number,port_device_timestamp_sim, port_device_uri, host_timestamp_sim,host_mac) values (?,?,?,?,?,?)";

        Long establishedTime = null;
        Long portNumber = null;
        Long port_device_timestamp_sim;
        String deviceUri = null;
        Long host_timestamp_sim;
        String hostMac = null;


        try(PreparedStatement prepStmt = con.prepareStatement(writeDevicesString)){

            Iterable<Host> hosts = hostService.getHosts();

            //Parse info
            for (Host h : hosts) {
                hostMac = h.mac().toString();
                host_timestamp_sim = timestampvar;

                Set<HostLocation> locations = h.locations();

                for(HostLocation location : locations) {


                    establishedTime = location.time();
                    portNumber = location.port().toLong();
                    port_device_timestamp_sim = timestampvar;
                    deviceUri = location.deviceId().uri().toString();



                    //Write in DB
                    prepStmt.setLong(1, establishedTime);
                    prepStmt.setLong(2, portNumber);
                    prepStmt.setLong(3, port_device_timestamp_sim);
                    prepStmt.setString(4, deviceUri);
                    prepStmt.setLong(5, host_timestamp_sim);
                    prepStmt.setString(6, hostMac);

                    prepStmt.addBatch();
                }
            }

            //int[] updateCounts = prepStmt.executeBatch();
            prepStmt.executeBatch();
            con.commit();
            log.info("Locations Inserted");


        } catch (BatchUpdateException b) {
            log.info("ERROR (BatchUpdate) in class "+getClass());
            b.printStackTrace();
        } catch (SQLException ex) {
            log.info("ERROR (SQLException) in class "+getClass());
            ex.printStackTrace();
        } finally {
            con.setAutoCommit(true);
        }

    }

    //Needs Device's FK
    /**
     * Writes the links assets to the database.
     * @param con Connection to the database.
     * @throws SQLException If there is an error in the SQL query.
     */
    private void writeLinks(Connection con) throws SQLException {
        con.setAutoCommit(false);

        String writeLinkString = "insert into link (link_type, link_state, port_number_src, port_number_dst, port_device_timestamp_sim_src, port_device_timestamp_sim_dst,port_device_uri_src, port_device_uri_dst, bw, latency, jitter, loss_prob, energy_consumption) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";

        String queryLinkValues = "select bw, latency, jitter, loss_prob, energy_consumption from link where port_number_src = ? AND port_number_dst = ? AND port_device_uri_src = ? AND port_device_uri_dst = ? AND port_device_timestamp_sim_dst = (SELECT MAX(port_device_timestamp_sim_dst) FROM link)";

        String linkType = null;
        String linkState = null;
        Long srcPort = null;
        Long dstPort = null;
        Long port_device_timestamp_sim_src;
        Long port_device_timestamp_sim_dst;
        String deviceUriSrc = null;
        String deviceUriDst = null;


        try{
            PreparedStatement prepStmt = con.prepareStatement(writeLinkString);
            PreparedStatement prepQueryStm = con.prepareStatement(queryLinkValues);

            Iterable<Link> links = linkService.getLinks();

            //Parse info
            for (Link l : links) {
                linkType = l.type().toString() ;
                linkState = l.state().toString();
                srcPort = l.src().port().toLong();
                dstPort = l.dst().port().toLong();
                port_device_timestamp_sim_src = timestampvar;
                port_device_timestamp_sim_dst = timestampvar;
                deviceUriSrc = l.src().deviceId().uri().toString() ;
                deviceUriDst = l.dst().deviceId().uri().toString();


                //Write in DB
                prepStmt.setString(1, linkType );
                prepStmt.setString(2, linkState );
                prepStmt.setLong(3, srcPort);
                prepStmt.setLong(4, dstPort);
                prepStmt.setLong(5, port_device_timestamp_sim_src);
                prepStmt.setLong(6, port_device_timestamp_sim_dst);
                prepStmt.setString(7, deviceUriSrc);
                prepStmt.setString(8, deviceUriDst);

                //Prepare query
                prepQueryStm.setLong(1, srcPort);
                prepQueryStm.setLong(2, dstPort);
                prepQueryStm.setString(3, deviceUriSrc);
                prepQueryStm.setString(4, deviceUriDst);

                ResultSet rs = prepQueryStm.executeQuery();
                ResultSetMetaData meta = rs.getMetaData();

                if (rs.next() == true) {
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        switch (meta.getColumnName(i)) {
                            case "bw":
                                prepStmt.setDouble(9, rs.getDouble(i));
                                break;
                            case "latency":
                                prepStmt.setDouble(10, rs.getDouble(i));
                                break;
                            case "jitter":
                                prepStmt.setDouble(11, rs.getDouble(i));
                                break;
                            case "loss_prob":
                                prepStmt.setDouble(12, rs.getDouble(i));
                                break;
                            case "energy_consumption":
                                prepStmt.setDouble(13, rs.getDouble(i));
                                break;
                            default:
                                break;
                        }
                    }
                } else {
                    prepStmt.setDouble(9, -1);
                    prepStmt.setDouble(10, -1);
                    prepStmt.setDouble(11, -1);
                    prepStmt.setDouble(12, -1);
                    prepStmt.setDouble(13, -1);
                }

                prepStmt.addBatch();
            }

            //int[] updateCounts = prepStmt.executeBatch();
            prepStmt.executeBatch();
            con.commit();
            log.info("Links Inserted");


        } catch (BatchUpdateException b) {
            log.info("ERROR (BatchUpdate) in class "+getClass());
            b.printStackTrace();
        } catch (SQLException ex) {
            log.info("ERROR (SQLException) in class "+getClass());
            ex.printStackTrace();
        } finally {
            con.setAutoCommit(true);
        }

    }

    //Needs Device's FK
    /**
     * Writes the bytes statistics of each port to the database.
     * @param con Connection to the database.
     * @throws SQLException If there is an error in the SQL query.
     */
    private void writeByteStat(Connection con) throws SQLException {
        con.setAutoCommit(false);

        String writePortsString = "insert into byte_statistics (rate, latest_counter, is_valid, capture_time, bytes_received, bytes_sent, port_alive_time_sec, port_number,port_device_timestamp_sim, port_device_uri) values (?,?,?,?,?,?,?,?,?,?)";


        Long rate = null;
        Long latestCounter = null;
        Boolean isValid = null;
        Long captureTime = null;
        Long bytesReceived = null;
        Long bytesSent = null;
        Long portAliveTimeSec = null;
        Long portNumber = null;
        Long port_device_timestamp_sim;
        String deviceUri = null;



        try(PreparedStatement prepStmt = con.prepareStatement(writePortsString)){

            Iterable<Device> devices = deviceService.getAvailableDevices();

            //Parse info
            for (Device d : devices){

                List<Port> ports = deviceService.getPorts(d.id());

                for(Port p : ports){

                    PortStatistics portstatistics = deviceService.getStatisticsForPort(d.id(), p.number());

                    //filter out port.type=="LOCAL" ports
                    if(portstatistics == null){
                        continue;
                    }


                    //parses the device+port object into a connectionPoint object
                    ConnectPoint cp = ConnectPoint.deviceConnectPoint(d.id()+"/"+p.number());

                    rate = portStatisticsService.load( cp ,PortStatisticsService.MetricType.BYTES).rate();
                    latestCounter = portStatisticsService.load( cp ,PortStatisticsService.MetricType.BYTES).latest();
                    isValid = portStatisticsService.load( cp ,PortStatisticsService.MetricType.BYTES).isValid();
                    captureTime = portStatisticsService.load( cp ,PortStatisticsService.MetricType.BYTES).time();

                    bytesReceived = portstatistics.bytesReceived();
                    bytesSent = portstatistics.bytesSent();
                    portAliveTimeSec = portstatistics.durationSec();

                    portNumber = p.number().toLong();
                    port_device_timestamp_sim = timestampvar;
                    deviceUri = d.id().uri().toString();

                    //Write in DB
                    prepStmt.setLong(1, rate );
                    prepStmt.setLong(2, latestCounter );
                    prepStmt.setBoolean(3, isValid);
                    prepStmt.setLong(4, captureTime);
                    prepStmt.setLong(5, bytesReceived);
                    prepStmt.setLong(6, bytesSent);
                    prepStmt.setLong(7, portAliveTimeSec);
                    prepStmt.setLong(8, portNumber);
                    prepStmt.setLong(9, port_device_timestamp_sim);
                    prepStmt.setString(10, deviceUri);
                    prepStmt.addBatch();

                }
            }

            //int[] updateCounts = prepStmt.executeBatch();
            prepStmt.executeBatch();
            con.commit();
            log.info("Ports Bytes Inserted");


        } catch (BatchUpdateException b) {
            log.info("ERROR (BatchUpdate) in class "+getClass());
            b.printStackTrace();
        } catch (SQLException ex) {
            log.info("ERROR (SQLException) in class "+getClass());
            ex.printStackTrace();
        } finally {
            con.setAutoCommit(true);
        }

    }


    //Needs Device's FK
    /**
     * Writes the packets statistics of each port to the database.
     * @param con Connection to the database.
     * @throws SQLException If there is an error in the SQL query.
     */
    private void writePacketStat(Connection con) throws SQLException {
        con.setAutoCommit(false);

        String writePortsString = "insert into packet_statistics (rate, latest_counter, is_valid, capture_time, packets_received, " +
                "packets_sent, port_alive_time_sec, drop_count_rx, drop_count_tx, error_count_rx, error_count_tx, port_number, port_device_timestamp_sim, port_device_uri) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


        Long rate = null;
        Long latestCounter = null;
        Boolean isValid = null;
        Long captureTime = null;
        Long bytesReceived = null;
        Long bytesSent = null;
        Long portAliveTimeSec = null;
        Long dropCountRx = null;
        Long dropCountTx = null;
        Long errorCountRx = null;
        Long errorCountTx = null;

        Long portNumber = null;
        Long port_device_timestamp_sim;
        String deviceUri = null;

        try(PreparedStatement prepStmt = con.prepareStatement(writePortsString)){

            Iterable<Device> devices = deviceService.getAvailableDevices();

            //Parse info
            for (Device d : devices){

                List<Port> ports = deviceService.getPorts(d.id());

                for(Port p : ports){

                    PortStatistics portstatistics = deviceService.getStatisticsForPort(d.id(), p.number());

                    //filter out port.type=="LOCAL" ports
                    if(portstatistics == null){
                        continue;
                    }


                    //parses the device+port object into a connectionPoint object
                    ConnectPoint cp = ConnectPoint.deviceConnectPoint(d.id()+"/"+p.number());

                    rate = portStatisticsService.load( cp ,PortStatisticsService.MetricType.PACKETS).rate();
                    latestCounter = portStatisticsService.load( cp ,PortStatisticsService.MetricType.PACKETS).latest();
                    isValid = portStatisticsService.load( cp ,PortStatisticsService.MetricType.PACKETS).isValid();
                    captureTime = portStatisticsService.load( cp ,PortStatisticsService.MetricType.PACKETS).time();

                    bytesReceived = portstatistics.packetsReceived();
                    bytesSent = portstatistics.packetsSent();
                    portAliveTimeSec = portstatistics.durationSec();

                    dropCountRx = portstatistics.packetsRxDropped();
                    dropCountTx = portstatistics.packetsTxDropped();
                    errorCountRx = portstatistics.packetsRxErrors();
                    errorCountTx = portstatistics.packetsTxErrors();

                    portNumber = p.number().toLong();
                    port_device_timestamp_sim = timestampvar;
                    deviceUri = d.id().uri().toString();

                    //Write in DB
                    prepStmt.setLong(1, rate );
                    prepStmt.setLong(2, latestCounter );
                    prepStmt.setBoolean(3, isValid);
                    prepStmt.setLong(4, captureTime);
                    prepStmt.setLong(5, bytesReceived);
                    prepStmt.setLong(6, bytesSent);
                    prepStmt.setLong(7, portAliveTimeSec);
                    prepStmt.setLong(8, dropCountRx);
                    prepStmt.setLong(9, dropCountTx);
                    prepStmt.setLong(10, errorCountRx);
                    prepStmt.setLong(11, errorCountTx);
                    prepStmt.setLong(12, portNumber);
                    prepStmt.setLong(13, port_device_timestamp_sim);
                    prepStmt.setString(14, deviceUri);
                    prepStmt.addBatch();

                }
            }

            //int[] updateCounts = prepStmt.executeBatch();
            prepStmt.executeBatch();
            con.commit();
            log.info("Ports Packets Inserted");


        } catch (BatchUpdateException b) {
            log.info("ERROR (BatchUpdate) in class "+getClass());
            b.printStackTrace();
        } catch (SQLException ex) {
            log.info("ERROR (SQLException) in class "+getClass());
            ex.printStackTrace();
        } finally {
            con.setAutoCommit(true);
        }

    }

    //Needs Device's FK
    /**
     * Writes the flow rules of each device to the database.
     * @param con Connection to the database.
     * @throws SQLException If there is an error in the SQL query.
     */
    private void writeFlowRules(Connection con) throws SQLException {
        con.setAutoCommit(false);

        String writePortsString = "insert into flowrule (rule_id, state, bytes, packets, duration, " +
                "priority, table_name, app_id, group_id, timeout, hard_timeout, " +
                "permanent, selector, treatment,device_timestamp_sim,device_uri) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


        String rule_id = null;
        String state = null;
        Long bytes = null;
        Long packets = null;
        Long duration = null;
        Long priority = null;
        String table_name = null;
        Long app_id = null;
        Long group_id = null;
        Long timeout = null;
        Long hard_timeout = null;
        Boolean permanent = null;
        String selector = null;
        String treatment = null;

        Long device_timestamp_sim;
        String deviceUri = null;

        try(PreparedStatement prepStmt = con.prepareStatement(writePortsString)){


            Iterable<Device> devices = deviceService.getAvailableDevices();
            for (Device d : devices) {

                for (FlowEntry fr : flowRuleService.getFlowEntries(d.id()) ) {
                    rule_id = fr.id().toString();
                    state = fr.state().toString();
                    bytes = fr.bytes();
                    packets = fr.packets();
                    duration = fr.life();
                    priority = (long) fr.priority();
                    table_name = fr.table().toString();
                    app_id = (long) fr.appId();
                    group_id = (long) fr.groupId().id();
                    timeout = (long) fr.timeout();
                    hard_timeout = (long) fr.hardTimeout();
                    permanent = fr.isPermanent();
                    selector = fr.selector().toString();
                    treatment = fr.treatment().toString();
                    device_timestamp_sim = timestampvar;
                    deviceUri = d.id().uri().toString();

                    //Write in DB
                    prepStmt.setString(1, rule_id );
                    prepStmt.setString(2, state );
                    prepStmt.setLong(3, bytes);
                    prepStmt.setLong(4, packets);
                    prepStmt.setLong(5, duration);
                    prepStmt.setLong(6, priority);
                    prepStmt.setString(7, table_name);
                    prepStmt.setLong(8, app_id);
                    prepStmt.setLong(9, group_id);
                    prepStmt.setLong(10, timeout);
                    prepStmt.setLong(11, hard_timeout);
                    prepStmt.setBoolean(12, permanent);
                    prepStmt.setString(13, selector);
                    prepStmt.setString(14, treatment);
                    prepStmt.setLong(15, device_timestamp_sim);
                    prepStmt.setString(16, deviceUri);
                    prepStmt.addBatch();

                }


            }
            //int[] updateCounts = prepStmt.executeBatch();
            prepStmt.executeBatch();
            con.commit();
            log.info("FlowRules Inserted");


        } catch (BatchUpdateException b) {
            log.info("ERROR (BatchUpdate) in class "+getClass());
            b.printStackTrace();
        } catch (SQLException ex) {
            log.info("ERROR (SQLException) in class "+getClass());
            ex.printStackTrace();
        } finally {
            con.setAutoCommit(true);
        }

    }







    //config que deveria ser feita pelo rest
    /**
     * Innitialises the general_conf table in the database.
     * @param con Connection to the database.
     * @throws SQLException If there is an error in the SQL query.
     */
    private void innitTableGeneralConfig(Connection con) throws SQLException {
        con.setAutoCommit(false);
        String writeConfigString = "insert into general_conf (wired_bw_mbs, wireless_bw_mbs, flowrule_priority, flowrule_timeout, flowrule_appid, default_fw_algorithm) values (?,?,?,?,?,?)";


        Long wired_bw_mbs = (long) 1000;
        Long wireless_bw_mbs = (long) 54;
        Long flowrule_priority = (long) 16;
        Long flowrule_timrout = (long) 10;//8??
        Long flowrule_appid = (long) 24;
        String default_fw_algorithm = "3-obj-fairness";



        try(PreparedStatement prepStmt = con.prepareStatement(writeConfigString)){

            //Write in DB
            prepStmt.setLong(1, wired_bw_mbs );
            prepStmt.setLong(2, wireless_bw_mbs );
            prepStmt.setLong(3, flowrule_priority);
            prepStmt.setLong(4, flowrule_timrout);
            prepStmt.setLong(5, flowrule_appid);
            prepStmt.setString(6, default_fw_algorithm);
            prepStmt.addBatch();

            //int[] updateCounts = prepStmt.executeBatch();
            prepStmt.executeBatch();
            con.commit();
            log.info("General Config Table Innitialised");


        } catch (BatchUpdateException b) {
            log.info("ERROR (BatchUpdate) in class "+getClass());
            b.printStackTrace();
        } catch (SQLException ex) {
            log.info("ERROR (SQLException) in class "+getClass());
            ex.printStackTrace();
        } finally {
            con.setAutoCommit(true);
        }



    }

    //config que deveria ser feita pelo rest
    /**
     * Innitialises the service_conf table in the database.
     * @param con Connection to the database.
     * @throws SQLException If there is an error in the SQL query.
     */
    private void innitTableServiceConfig(Connection con) throws SQLException {
        con.setAutoCommit(false);

        //String writeConfigString = "insert into service_conf (name, datarate_mbs, delay_sec) values (?,?,?)";
        String writeConfigString = "insert into service_conf (name, datarate_mbs) values (?,?)";


        String name = null;
        double datarate_mbs = 0.0;
        //double delay_sec = 0.0;



        try(PreparedStatement prepStmt = con.prepareStatement(writeConfigString)){


            //Write in DB
            //video
            name = "video";
            datarate_mbs = 1.5;
            //delay_sec = 0.036;
            prepStmt.setString(1, name );
            prepStmt.setDouble(2, datarate_mbs );
            //prepStmt.setDouble(3, delay_sec);
            prepStmt.addBatch();


            //video
            name = "voice";
            datarate_mbs = 0.0244;
            //delay_sec = 0.020;
            prepStmt.setString(1, name );
            prepStmt.setDouble(2, datarate_mbs );
            //prepStmt.setDouble(3, delay_sec);
            prepStmt.addBatch();


            //video
            name = "BUD";
            datarate_mbs = 12.288;
            //ATENCAO aqui, nao sei se este e valor correto a usar
            //delay_sec = 0.2844;
            prepStmt.setString(1, name );
            prepStmt.setDouble(2, datarate_mbs );
            //prepStmt.setDouble(3, delay_sec);
            prepStmt.addBatch();

            //int[] updateCounts = prepStmt.executeBatch();
            prepStmt.executeBatch();
            con.commit();
            log.info("Service Conf Table Innitialised");


        } catch (BatchUpdateException b) {
            log.info("ERROR (BatchUpdate) in class "+getClass());
            b.printStackTrace();
        } catch (SQLException ex) {
            log.info("ERROR (SQLException) in class "+getClass());
            ex.printStackTrace();
        } finally {
            con.setAutoCommit(true);
        }



    }

    //config que deveria ser feita pelo rest
    /**
     * Innitialises the device_machine_relation table in the database for Demo purposes.
     * @param con Connection to the database.
     * @throws SQLException If there is an error in the SQL query.
     */
    private void innitTableDeviceMachineRelationDemo(Connection con) throws SQLException {
        con.setAutoCommit(false);

        String writeConfigString = "insert into device_machine_relation (device_uri, machine_name) values (?,?)";


        String device_uri = null;
        String machine_name = null;


        try(PreparedStatement prepStmt = con.prepareStatement(writeConfigString)){


            int swIndex = 1;
            for (; swIndex <= 4; swIndex++) {


                String hexa = Integer.toHexString(swIndex);
                int num = 16 - hexa.length();
                device_uri = "of:" + String.format("%1$" + num + "s", "").replace(' ', '0') + Integer.toHexString(swIndex);

                machine_name = "PI";

                prepStmt.setString(1, device_uri );
                prepStmt.setString(2, machine_name );
                prepStmt.addBatch();
            }


            //int[] updateCounts = prepStmt.executeBatch();
            prepStmt.executeBatch();
            con.commit();
            log.info("Device Machine Relation Table Innitialised");


        } catch (BatchUpdateException b) {
            log.info("ERROR (BatchUpdate) in class "+getClass());
            b.printStackTrace();
        } catch (SQLException ex) {
            log.info("ERROR (SQLException) in class "+getClass());
            ex.printStackTrace();
        } finally {
            con.setAutoCommit(true);
        }

    }



    //config que deveria ser feita pelo rest
    /**
     * Innitialises the device_machine_relation table in the database for IntraCluster purposes.
     * @param con Connection to the database.
     * @throws SQLException If there is an error in the SQL query.
     */
    private void innitTableDeviceMachineRelationIntra(Connection con) throws SQLException {
        con.setAutoCommit(false);

        String writeConfigString = "insert into device_machine_relation (device_uri, machine_name) values (?,?)";


        String device_uri = null;
        String machine_name = null;


        try(PreparedStatement prepStmt = con.prepareStatement(writeConfigString)){

            int numClusters = 6;
            int numSW = 6;

            //SW
            int swIndex = 1;
            for (; swIndex <= numClusters * numSW; swIndex++) {


                String hexa = Integer.toHexString(swIndex);
                int num = 16 - hexa.length();
                device_uri = "of:" + String.format("%1$" + num + "s", "").replace(' ', '0') + Integer.toHexString(swIndex);

                if (swIndex % 2 == 0) {
                    //is pair
                    machine_name = "Cubi";
                } else {
                    machine_name = "PI";
                }

                prepStmt.setString(1, device_uri );
                prepStmt.setString(2, machine_name );
                prepStmt.addBatch();
            }

            //SWCC
            String hexa = Integer.toHexString(swIndex);
            int num = 16 - hexa.length();
            device_uri = "of:" + String.format("%1$" + num + "s", "").replace(' ', '0') + Integer.toHexString(swIndex);
            machine_name = "PI";

            prepStmt.setString(1, device_uri );
            prepStmt.setString(2, machine_name );
            prepStmt.addBatch();

            swIndex++;

            //SWC
            for (int i = 0; i < numClusters; i++) {

                String nodeNameSWC = "swc" + (swIndex + i);
                String hexaSWC = Integer.toHexString(swIndex + i);
                int numSWC = 16 - hexaSWC.length();
                device_uri = "of:" + String.format("%1$" + numSWC + "s", "").replace(' ', '0') + Integer.toHexString(swIndex + i);


                machine_name = "PI";

                prepStmt.setString(1, device_uri );
                prepStmt.setString(2, machine_name );
                prepStmt.addBatch();
            }

            //int[] updateCounts = prepStmt.executeBatch();
            prepStmt.executeBatch();
            con.commit();
            log.info("Device Machine Relation Table Innitialised");


        } catch (BatchUpdateException b) {
            log.info("ERROR (BatchUpdate) in class "+getClass());
            b.printStackTrace();
        } catch (SQLException ex) {
            log.info("ERROR (SQLException) in class "+getClass());
            ex.printStackTrace();
        } finally {
            con.setAutoCommit(true);
        }

    }


    //config que deveria ser feita pelo rest
    /**
     * Innitialises the device_machine_relation table in the database for InterCluster purposes.
     * @param con Connection to the database.
     * @throws SQLException If there is an error in the SQL query.
     */
    private void innitTableDeviceMachineRelationInter(Connection con) throws SQLException {
        con.setAutoCommit(false);

        String writeConfigString = "insert into device_machine_relation (device_uri, machine_name) values (?,?)";


        String device_uri = null;
        String machine_name = null;


        try(PreparedStatement prepStmt = con.prepareStatement(writeConfigString)){

            int numClusters = 6;
            int numSW = 6;

            //SW
            int swIndex = 1;
            for (; swIndex <= numClusters * numSW; swIndex++) {


                String hexa = Integer.toHexString(swIndex);
                int num = 16 - hexa.length();
                device_uri = "of:" + String.format("%1$" + num + "s", "").replace(' ', '0') + Integer.toHexString(swIndex);

                if (swIndex % 2 == 0) {
                    //is pair
                    machine_name = "Cubi";
                } else {
                    machine_name = "PI";
                }

                prepStmt.setString(1, device_uri );
                prepStmt.setString(2, machine_name );
                prepStmt.addBatch();
            }

            //SWCC1
            String hexa = Integer.toHexString(swIndex);
            int num = 16 - hexa.length();
            device_uri = "of:" + String.format("%1$" + num + "s", "").replace(' ', '0') + Integer.toHexString(swIndex);
            machine_name = "PI";

            prepStmt.setString(1, device_uri );
            prepStmt.setString(2, machine_name );
            prepStmt.addBatch();

            swIndex++;


            //SWCC2
            String hexa2 = Integer.toHexString(swIndex);
            int num2 = 16 - hexa2.length();
            String device_uri2 = "of:" + String.format("%1$" + num2 + "s", "").replace(' ', '0') + Integer.toHexString(swIndex);
            String machine_name2 = "PI";

            prepStmt.setString(1, device_uri2 );
            prepStmt.setString(2, machine_name2 );
            prepStmt.addBatch();

            swIndex++;

            //SWC
            for (int i = 0; i < numClusters; i++) {

                String nodeNameSWC = "swc" + (swIndex + i);
                String hexaSWC = Integer.toHexString(swIndex + i);
                int numSWC = 16 - hexaSWC.length();
                device_uri = "of:" + String.format("%1$" + numSWC + "s", "").replace(' ', '0') + Integer.toHexString(swIndex + i);


                machine_name = "PI";

                prepStmt.setString(1, device_uri );
                prepStmt.setString(2, machine_name );
                prepStmt.addBatch();
            }

            //int[] updateCounts = prepStmt.executeBatch();
            prepStmt.executeBatch();
            con.commit();
            log.info("Device Machine Relation Table Innitialised");


        } catch (BatchUpdateException b) {
            log.info("ERROR (BatchUpdate) in class "+getClass());
            b.printStackTrace();
        } catch (SQLException ex) {
            log.info("ERROR (SQLException) in class "+getClass());
            ex.printStackTrace();
        } finally {
            con.setAutoCommit(true);
        }
    }




    //config que deveria ser feita pelo rest
    /**
     * Innitialises the service_host_relation table in the database for IntraCluster related to the Noe?
     * @param con Connection to the database.
     * @throws SQLException If there is an error in the SQL query.
     */
    private void innitTableServiceHostRelationIntraNoe(Connection con) throws SQLException {
        con.setAutoCommit(false);

        String writeConfigString = "insert into service_host_relation (mac_src, mac_dst, service_conf_name) values (?,?,?)";

        //AA:BB:CC:DD:00:01
        String mac_src = null;
        String mac_dst = null;
        String service_conf_name = null;



        try(PreparedStatement prepStmt = con.prepareStatement(writeConfigString)){

            //video
            for(int i=0; i<7;i++){

                int src = i+1;
                int dst = i+22;


                String hexa = Integer.toHexString(src);
                hexa = hexa.toUpperCase();
                if(hexa.length() == 1){
                    hexa = "0"+hexa;
                }

                String hexa1 = Integer.toHexString(dst);
                hexa1 = hexa1.toUpperCase();
                if(hexa1.length() == 1){
                    hexa1 = "0"+hexa1;
                }

                mac_src = "AA:BB:CC:DD:00:"+hexa;
                mac_dst = "AA:BB:CC:DD:00:"+hexa1;
                service_conf_name = "video";

                prepStmt.setString(1, mac_src );
                prepStmt.setString(2, mac_dst );
                prepStmt.setString(3, service_conf_name);
                prepStmt.addBatch();
            }



            //voice
            for(int i=0; i<7;i++){

                int src = i+8;
                int dst = i+29;


                String hexa = Integer.toHexString(src);
                hexa = hexa.toUpperCase();
                if(hexa.length() == 1){
                    hexa = "0"+hexa;
                }

                String hexa1 = Integer.toHexString(dst);
                hexa1 = hexa1.toUpperCase();
                if(hexa1.length() == 1){
                    hexa1 = "0"+hexa1;
                }

                mac_src = "AA:BB:CC:DD:00:"+hexa;
                mac_dst = "AA:BB:CC:DD:00:"+hexa1;
                service_conf_name = "voice";

                prepStmt.setString(1, mac_src );
                prepStmt.setString(2, mac_dst );
                prepStmt.setString(3, service_conf_name);
                prepStmt.addBatch();
            }


            //BUD
            for(int i=0; i<7;i++){

                int src = i+15;
                int dst = i+36;


                String hexa = Integer.toHexString(src);
                hexa = hexa.toUpperCase();
                if(hexa.length() == 1){
                    hexa = "0"+hexa;
                }

                String hexa1 = Integer.toHexString(dst);
                hexa1 = hexa1.toUpperCase();
                if(hexa1.length() == 1){
                    hexa1 = "0"+hexa1;
                }

                mac_src = "AA:BB:CC:DD:00:"+hexa;
                mac_dst = "AA:BB:CC:DD:00:"+hexa1;
                service_conf_name = "BUD";

                prepStmt.setString(1, mac_src );
                prepStmt.setString(2, mac_dst );
                prepStmt.setString(3, service_conf_name);
                prepStmt.addBatch();

                prepStmt.setString(1, mac_dst);
                prepStmt.setString(2, mac_src);
                prepStmt.setString(3, service_conf_name);
                prepStmt.addBatch();
            }


            //int[] updateCounts = prepStmt.executeBatch();
            prepStmt.executeBatch();
            con.commit();
            log.info("Service Host Relation Table Innitialised");


        } catch (BatchUpdateException b) {
            log.info("ERROR (BatchUpdate) in class "+getClass());
            b.printStackTrace();
        } catch (SQLException ex) {
            log.info("ERROR (SQLException) in class "+getClass());
            ex.printStackTrace();
        } finally {
            con.setAutoCommit(true);
        }
    }




    //config que deveria ser feita pelo rest
    /**
     * Innitialises the service_host_relation table in the database for IntraCluster purposes.
     * @param con Connection to the database.
     * @throws SQLException If there is an error in the SQL query.
     */
    private void innitTableServiceHostRelationIntra(Connection con) throws SQLException {
        con.setAutoCommit(false);

        String writeConfigString = "insert into service_host_relation (mac_src, mac_dst, service_conf_name) values (?,?,?)";

        //AA:BB:CC:DD:00:01
        String mac_src = null;
        String mac_dst = null;
        String service_conf_name = null;



        try(PreparedStatement prepStmt = con.prepareStatement(writeConfigString)){

            //video
            mac_src = "AA:BB:CC:DD:00:01";
            mac_dst = "AA:BB:CC:DD:00:04";
            service_conf_name = "video";

            prepStmt.setString(1, mac_src );
            prepStmt.setString(2, mac_dst );
            prepStmt.setString(3, service_conf_name);
            prepStmt.addBatch();

            //voice
            mac_src = "AA:BB:CC:DD:00:02";
            mac_dst = "AA:BB:CC:DD:00:05";
            service_conf_name = "voice";

            prepStmt.setString(1, mac_src );
            prepStmt.setString(2, mac_dst );
            prepStmt.setString(3, service_conf_name);
            prepStmt.addBatch();

            //BUD
            mac_src = "AA:BB:CC:DD:00:03";
            mac_dst = "AA:BB:CC:DD:00:06";
            service_conf_name = "BUD";

            prepStmt.setString(1, mac_src );
            prepStmt.setString(2, mac_dst );
            prepStmt.setString(3, service_conf_name);
            prepStmt.addBatch();
            mac_src = "AA:BB:CC:DD:00:06";
            mac_dst = "AA:BB:CC:DD:00:03";
            prepStmt.setString(1, mac_src );
            prepStmt.setString(2, mac_dst );
            prepStmt.setString(3, service_conf_name);
            prepStmt.addBatch();


            //int[] updateCounts = prepStmt.executeBatch();
            prepStmt.executeBatch();
            con.commit();
            log.info("Service Host Relation Table Innitialised");


        } catch (BatchUpdateException b) {
            log.info("ERROR (BatchUpdate) in class "+getClass());
            b.printStackTrace();
        } catch (SQLException ex) {
            log.info("ERROR (SQLException) in class "+getClass());
            ex.printStackTrace();
        } finally {
            con.setAutoCommit(true);
        }
    }

    //config que deveria ser feita pelo rest
    private void innitTableServiceHostRelationInter(Connection con) throws SQLException {
        con.setAutoCommit(false);

        String writeConfigString = "insert into service_host_relation (mac_src, mac_dst, service_conf_name) values (?,?,?)";

        //AA:BB:CC:DD:00:01
        String mac_src = null;
        String mac_dst = null;
        String service_conf_name = null;

        try(PreparedStatement prepStmt = con.prepareStatement(writeConfigString)){


            //video
            // sw1->sw37----
            mac_src = "AA:BB:CC:DD:00:01";
            mac_dst = "AA:BB:CC:DD:00:25";
            service_conf_name = "video";
            prepStmt.setString(1, mac_src );
            prepStmt.setString(2, mac_dst );
            prepStmt.setString(3, service_conf_name);
            prepStmt.addBatch();

            mac_src = "AA:BB:CC:DD:00:02";
            mac_dst = "AA:BB:CC:DD:00:26";
            service_conf_name = "video";
            prepStmt.setString(1, mac_src );
            prepStmt.setString(2, mac_dst );
            prepStmt.setString(3, service_conf_name);
            prepStmt.addBatch();

            mac_src = "AA:BB:CC:DD:00:03";
            mac_dst = "AA:BB:CC:DD:00:27";
            service_conf_name = "video";
            prepStmt.setString(1, mac_src );
            prepStmt.setString(2, mac_dst );
            prepStmt.setString(3, service_conf_name);
            prepStmt.addBatch();

            mac_src = "AA:BB:CC:DD:00:04";
            mac_dst = "AA:BB:CC:DD:00:28";
            service_conf_name = "video";
            prepStmt.setString(1, mac_src );
            prepStmt.setString(2, mac_dst );
            prepStmt.setString(3, service_conf_name);
            prepStmt.addBatch();

            mac_src = "AA:BB:CC:DD:00:05";
            mac_dst = "AA:BB:CC:DD:00:26";
            service_conf_name = "video";
            prepStmt.setString(1, mac_src );
            prepStmt.setString(2, mac_dst );
            prepStmt.setString(3, service_conf_name);
            prepStmt.addBatch();

            mac_src = "AA:BB:CC:DD:00:06";
            mac_dst = "AA:BB:CC:DD:00:27";
            service_conf_name = "video";
            prepStmt.setString(1, mac_src );
            prepStmt.setString(2, mac_dst );
            prepStmt.setString(3, service_conf_name);
            prepStmt.addBatch();


            //------------------
            //video
            // sw1->sw19
            mac_src = "AA:BB:CC:DD:00:01";
            mac_dst = "AA:BB:CC:DD:00:13";
            service_conf_name = "video";

            prepStmt.setString(1, mac_src );
            prepStmt.setString(2, mac_dst );
            prepStmt.setString(3, service_conf_name);
            prepStmt.addBatch();

            //voice
            //sw2->sw20
            mac_src = "AA:BB:CC:DD:00:02";
            mac_dst = "AA:BB:CC:DD:00:14";
            service_conf_name = "voice";

            prepStmt.setString(1, mac_src );
            prepStmt.setString(2, mac_dst );
            prepStmt.setString(3, service_conf_name);
            prepStmt.addBatch();

            //BUD
            //sw3->sw21
            mac_src = "AA:BB:CC:DD:00:03";
            mac_dst = "AA:BB:CC:DD:00:15";
            service_conf_name = "BUD";

            prepStmt.setString(1, mac_src );
            prepStmt.setString(2, mac_dst );
            prepStmt.setString(3, service_conf_name);
            prepStmt.addBatch();
            mac_src = "AA:BB:CC:DD:00:15";
            mac_dst = "AA:BB:CC:DD:00:03";
            prepStmt.setString(1, mac_src );
            prepStmt.setString(2, mac_dst );
            prepStmt.setString(3, service_conf_name);
            prepStmt.addBatch();


            //int[] updateCounts = prepStmt.executeBatch();
            prepStmt.executeBatch();
            con.commit();
            log.info("Service Host Relation Table Innitialised");


        } catch (BatchUpdateException b) {
            log.info("ERROR (BatchUpdate) in class "+getClass());
            b.printStackTrace();
        } catch (SQLException ex) {
            log.info("ERROR (SQLException) in class "+getClass());
            ex.printStackTrace();
        } finally {
            con.setAutoCommit(true);
        }

    }

    //config que deveria ser feita pelo rest
    /**
     * Innitialises the service_host_relation table in the database for Demo.
     * @param con Connection to the database.
     * @throws SQLException If there is an error in the SQL query.
     */
    private void innitTableServiceHostRelationDemo(Connection con) throws SQLException {
        con.setAutoCommit(false);

        String writeConfigString = "insert into service_host_relation (mac_src, mac_dst, service_conf_name) values (?,?,?)";

        //AA:BB:CC:DD:00:01
        String mac_src = null;
        String mac_dst = null;
        String service_conf_name = null;



        try(PreparedStatement prepStmt = con.prepareStatement(writeConfigString)){

            //video
            mac_src = "AA:BB:CC:DD:00:01";
            mac_dst = "AA:BB:CC:DD:00:02";
            service_conf_name = "video";

            prepStmt.setString(1, mac_src );
            prepStmt.setString(2, mac_dst );
            prepStmt.setString(3, service_conf_name);
            prepStmt.addBatch();



            //int[] updateCounts = prepStmt.executeBatch();
            prepStmt.executeBatch();
            con.commit();
            log.info("Service Host Relation Table Innitialised");


        } catch (BatchUpdateException b) {
            log.info("ERROR (BatchUpdate) in class "+getClass());
            b.printStackTrace();
        } catch (SQLException ex) {
            log.info("ERROR (SQLException) in class "+getClass());
            ex.printStackTrace();
        } finally {
            con.setAutoCommit(true);
        }
    }




    //config que deveria ser feita pelo rest
    /**
     * Innitialises the machine table in the database.
     * @param con Connection to the database.
     * @throws SQLException If there is an error in the SQL query.
     */
    private void innitTableMachine(Connection con) throws SQLException {
        con.setAutoCommit(false);

        String writeConfigString = "insert into machine (name) values (?)";

        String name = null;

        try(PreparedStatement prepStmt = con.prepareStatement(writeConfigString)){

            name = "PI";
            prepStmt.setString(1, name );
            prepStmt.addBatch();

            name = "Cubi";
            prepStmt.setString(1, name );
            prepStmt.addBatch();


            //int[] updateCounts = prepStmt.executeBatch();
            prepStmt.executeBatch();
            con.commit();
            log.info("Machine Table Table Innitialised");


        } catch (BatchUpdateException b) {
            log.info("ERROR (BatchUpdate) in class "+getClass());
            b.printStackTrace();
        } catch (SQLException ex) {
            log.info("ERROR (SQLException) in class "+getClass());
            ex.printStackTrace();
        } finally {
            con.setAutoCommit(true);
        }
    }

    //config que deveria ser feita pelo rest
    /**
     * Innitialises the energy table in the database.
     * @param con Connection to the database.
     * @throws SQLException If there is an error in the SQL query.
     */
    private void innitTableEnergy(Connection con) throws SQLException {
        con.setAutoCommit(false);

        //INSERT INTO machine (name, energy_name, datarate, cof_degree_2, cof_degree_1, cof_degree_0) VALUES ('PI', 'energy_wireless_u', 1, -0.25*exp(-6), 1.99*exp(-3), -0.072);

        String writeConfigString = "insert into energy (name, machine_name, datarate, cof_degree_2, cof_degree_1, cof_degree_0) values (?,?,?,?,?,?)";

        String name = null;
        String machine_name = null;
        double datarate = 0.0;
        double cof_degree_2 = 0.0;
        double cof_degree_1 = 0.0;
        double cof_degree_0 = 0.0;

        try(PreparedStatement prepStmt = con.prepareStatement(writeConfigString)){

            name = "energy_wireless_u";
            machine_name = "PI";
            datarate = 0.0;
            cof_degree_2 = -0.25*Math.exp(-06);
            cof_degree_1 = 1.99*Math.exp(-03);
            cof_degree_0 = -0.072;
            prepStmt.setString(1, name );
            prepStmt.setString(2, machine_name );
            prepStmt.setDouble(3, datarate );
            prepStmt.setDouble(4, cof_degree_2 );
            prepStmt.setDouble(5, cof_degree_1 );
            prepStmt.setDouble(6, cof_degree_0 );
            prepStmt.addBatch();


            name = "energy_wired_u";
            machine_name = "PI";
            datarate = 0.0;
            cof_degree_2 = 26.2*Math.exp(-06);
            cof_degree_1 = 0.357*Math.exp(-03);
            cof_degree_0 = 0.007;
            prepStmt.setString(1, name );
            prepStmt.setString(2, machine_name );
            prepStmt.setDouble(3, datarate );
            prepStmt.setDouble(4, cof_degree_2 );
            prepStmt.setDouble(5, cof_degree_1 );
            prepStmt.setDouble(6, cof_degree_0 );
            prepStmt.addBatch();


            name = "energy_wireless_d";
            machine_name = "PI";
            datarate = 0.0;
            cof_degree_2 = 1.85*Math.exp(-03);
            cof_degree_1 = 13.5*Math.exp(-03);
            cof_degree_0 = 0.072;
            prepStmt.setString(1, name );
            prepStmt.setString(2, machine_name );
            prepStmt.setDouble(3, datarate );
            prepStmt.setDouble(4, cof_degree_2 );
            prepStmt.setDouble(5, cof_degree_1 );
            prepStmt.setDouble(6, cof_degree_0 );
            prepStmt.addBatch();

            name = "energy_wired_d";
            machine_name = "PI";
            datarate = 0.0;
            cof_degree_2 = -4.33*Math.exp(-03);
            //aqui e sem epsilon
            cof_degree_1 = Math.pow(0.485,-03);
            cof_degree_0 = - 0.007;
            prepStmt.setString(1, name );
            prepStmt.setString(2, machine_name );
            prepStmt.setDouble(3, datarate );
            prepStmt.setDouble(4, cof_degree_2 );
            prepStmt.setDouble(5, cof_degree_1 );
            prepStmt.setDouble(6, cof_degree_0 );
            prepStmt.addBatch();

            name = "energy_wired_idle";
            machine_name = "PI";
            datarate = 0.0;
            cof_degree_2 = 0.0;
            cof_degree_1 = 0.0;
            cof_degree_0 = -0.1176;
            prepStmt.setString(1, name );
            prepStmt.setString(2, machine_name );
            prepStmt.setDouble(3, datarate );
            prepStmt.setDouble(4, cof_degree_2 );
            prepStmt.setDouble(5, cof_degree_1 );
            prepStmt.setDouble(6, cof_degree_0 );
            prepStmt.addBatch();

            name = "energy_wireless_idle";
            machine_name = "PI";
            datarate = 0.0;
            cof_degree_2 = 0.0;
            cof_degree_1 = 0.0;
            cof_degree_0 = 0.7645;
            prepStmt.setString(1, name );
            prepStmt.setString(2, machine_name );
            prepStmt.setDouble(3, datarate );
            prepStmt.setDouble(4, cof_degree_2 );
            prepStmt.setDouble(5, cof_degree_1 );
            prepStmt.setDouble(6, cof_degree_0 );
            prepStmt.addBatch();

            name = "energy_cpu";
            machine_name = "PI";
            datarate = 0.0;
            cof_degree_2 = 0.0;
            cof_degree_1 = 0.0;
            cof_degree_0 = 0.6191;
            prepStmt.setString(1, name );
            prepStmt.setString(2, machine_name );
            prepStmt.setDouble(3, datarate );
            prepStmt.setDouble(4, cof_degree_2 );
            prepStmt.setDouble(5, cof_degree_1 );
            prepStmt.setDouble(6, cof_degree_0 );
            prepStmt.addBatch();



            //CUBI--------------------------------
            name = "energy_wireless_u";
            machine_name = "Cubi";
            datarate = 0.0;
            cof_degree_2 = -0.307*Math.exp(-03);
            cof_degree_1 = 22.8*Math.exp(-03);
            cof_degree_0 = 0.011;
            prepStmt.setString(1, name );
            prepStmt.setString(2, machine_name );
            prepStmt.setDouble(3, datarate );
            prepStmt.setDouble(4, cof_degree_2 );
            prepStmt.setDouble(5, cof_degree_1 );
            prepStmt.setDouble(6, cof_degree_0 );
            prepStmt.addBatch();

            name = "energy_wired_u";
            machine_name = "Cubi";
            datarate = 0.0;
            cof_degree_2 = -17.6*Math.exp(-06);
            cof_degree_1 = 6.13*Math.exp(-03);
            cof_degree_0 = - 0.056;
            prepStmt.setString(1, name );
            prepStmt.setString(2, machine_name );
            prepStmt.setDouble(3, datarate );
            prepStmt.setDouble(4, cof_degree_2 );
            prepStmt.setDouble(5, cof_degree_1 );
            prepStmt.setDouble(6, cof_degree_0 );
            prepStmt.addBatch();

            name = "energy_wireless_d";
            machine_name = "Cubi";
            datarate = 0.0;
            cof_degree_2 = 0.137*Math.exp(-03);
            cof_degree_1 = 6.33*Math.exp(-03);
            cof_degree_0 = - 0.011;
            prepStmt.setString(1, name );
            prepStmt.setString(2, machine_name );
            prepStmt.setDouble(3, datarate );
            prepStmt.setDouble(4, cof_degree_2 );
            prepStmt.setDouble(5, cof_degree_1 );
            prepStmt.setDouble(6, cof_degree_0 );
            prepStmt.addBatch();


            name = "energy_wired_d";
            machine_name = "Cubi";
            datarate = 0.0;
            cof_degree_2 = -20.9*Math.exp(-06);
            cof_degree_1 = 2.5*Math.exp(-03);
            cof_degree_0 = 0.056;
            prepStmt.setString(1, name );
            prepStmt.setString(2, machine_name );
            prepStmt.setDouble(3, datarate );
            prepStmt.setDouble(4, cof_degree_2 );
            prepStmt.setDouble(5, cof_degree_1 );
            prepStmt.setDouble(6, cof_degree_0 );
            prepStmt.addBatch();


            name = "energy_wired_idle";
            machine_name = "Cubi";
            datarate = 0.0;
            cof_degree_2 = 0.0;
            cof_degree_1 = 0.0;
            cof_degree_0 = 0.224;
            prepStmt.setString(1, name );
            prepStmt.setString(2, machine_name );
            prepStmt.setDouble(3, datarate );
            prepStmt.setDouble(4, cof_degree_2 );
            prepStmt.setDouble(5, cof_degree_1 );
            prepStmt.setDouble(6, cof_degree_0 );
            prepStmt.addBatch();

            name = "energy_wireless_idle";
            machine_name = "Cubi";
            datarate = 0.0;
            cof_degree_2 = 0.0;
            cof_degree_1 = 0.0;
            cof_degree_0 = 0.306;
            prepStmt.setString(1, name );
            prepStmt.setString(2, machine_name );
            prepStmt.setDouble(3, datarate );
            prepStmt.setDouble(4, cof_degree_2 );
            prepStmt.setDouble(5, cof_degree_1 );
            prepStmt.setDouble(6, cof_degree_0 );
            prepStmt.addBatch();

            name = "energy_cpu";
            machine_name = "Cubi";
            datarate = 0.0;
            cof_degree_2 = 0.0;
            cof_degree_1 = 0.0;
            cof_degree_0 = 1.037;
            prepStmt.setString(1, name );
            prepStmt.setString(2, machine_name );
            prepStmt.setDouble(3, datarate );
            prepStmt.setDouble(4, cof_degree_2 );
            prepStmt.setDouble(5, cof_degree_1 );
            prepStmt.setDouble(6, cof_degree_0 );
            prepStmt.addBatch();


            //int[] updateCounts = prepStmt.executeBatch();
            prepStmt.executeBatch();
            con.commit();
            log.info("Energy Table Innitialised");


        } catch (BatchUpdateException b) {
            log.info("ERROR (BatchUpdate) in class "+getClass());
            b.printStackTrace();
        } catch (SQLException ex) {
            log.info("ERROR (SQLException) in class "+getClass());
            ex.printStackTrace();
        } finally {
            con.setAutoCommit(true);
        }
    }

    /**
     * Retreives the name of the service that the 2 macs are using.
     * @param mac_src The source mac.
     * @param mac_dst The destination mac.
     * @return The name of the service.
     */
    private String getService(String mac_src, String mac_dst) {


        String readTableString = "select * FROM service_host_relation where mac_src='"+mac_src+"' and mac_dst='"+mac_dst+"'";
        try(Connection con = dataSource.getConnection();
            PreparedStatement prepStmt = con.prepareStatement(readTableString)){

            ResultSet rs = prepStmt.executeQuery();

            if(rs.next() == false){
                throw new Exception();
            }

            //0 : idk what it is
            //1 : mac_src, we will not use
            //2 : mac_dst, we will not use
            //3 : serfice_conf_name
            return  rs.getString(3);

        } catch (SQLException throwables) {
            log.info("ERROR reading table service_host_relation from DataBase. "+getClass());
            throwables.printStackTrace();
            return "";
        } catch (Exception e) {
            log.info("ERROR, service_host_relation table is empty, using default values for port speed");
            return "";
        }

    }


    private String getMachine(String uri) {


        String readTableString = "select * FROM device_machine_relation where device_uri='"+uri+"'";
        try(Connection con = dataSource.getConnection();
            PreparedStatement prepStmt = con.prepareStatement(readTableString)){

            ResultSet rs = prepStmt.executeQuery();

            if(rs.next() == false){
                throw new Exception();
            }

            //0 : idk what it is
            //1 : device_uri, we will not use
            //2 : machine_name
            return  rs.getString(2);

        } catch (SQLException throwables) {
            log.info("ERROR reading table device_machine_relation from DataBase. "+getClass());
            throwables.printStackTrace();
            return "";
        } catch (Exception e) {
            log.info("ERROR, device_machine_relation table is empty, using default values for port speed");
            return "";
        }

    }


    //Loads the configuraions
    /**
     * Loads the configurations from the database.
     * @return 1 if the configurations were loaded, 0 if not.
     */
    private int LoadsDBConfigurations() {

        configurations = new HashMap<>();

        //----Querries the speed values
        String readTableString = "select * from general_conf";
        try(Connection con = dataSource.getConnection();
            PreparedStatement prepStmt = con.prepareStatement(readTableString)){

            ResultSet rs = prepStmt.executeQuery();

            if(rs.next() == false){
                throw new Exception();
            }

            //0 : idk what it is
            //1 : pkey, we will not use
            //2 : wired_bw
            configurations.put("wired_bw_mbs", Long.parseLong(rs.getString(2)));
            //3 : wireless_bw
            configurations.put("wireless_bw_mbs", Long.parseLong(rs.getString(3)));
            //4 : flowrule_priority
            configurations.put("flowrule_priority", Long.parseLong(rs.getString(4)));
            //5 : flowrule_timeout
            configurations.put("flowrule_timeout", Long.parseLong(rs.getString(5)));
            //6 : flowrule_appid
            configurations.put("flowrule_appid", Long.parseLong(rs.getString(6)));
            //7 : default_fw_algorithm
            configurations.put("default_fw_algorithm", rs.getString(7));


            con.close();
        } catch (SQLException throwables) {
            log.info("ERROR reading table configurations from DataBase. "+getClass());
            throwables.printStackTrace();
            return 0;
        } catch (Exception e) {
            log.info("ERROR, configuration table is empty, using default values for port speed");
            return 0;
        }

        return 1;
    }

    /**
     * Adjusts the name of the ports that are going to be stores.
     * @param p The port.
     * @return The adjusted name of the port.
     */
    private String nameAjust(Port p){
        //sw17-eth1, swc42-eth2, swcc37-eth1


        String portname = p.annotations().value("portName");
        String portnameFixed = null;

        long number = p.number().toLong();
        String[] truncated = portname.split("-");
        if(truncated.length == 1){
            //sw12 e.g.
            return portname;
        }
        if(truncated[1].startsWith("eth")) {
            portnameFixed = portname;
        }else if(truncated[1].startsWith("mp")) {
            portnameFixed = truncated[0] + "-mp" + number;
        }else if(truncated[1].startsWith("wlan")) {
            portnameFixed = truncated[0] + "-wlan" + number;
        }

        return portnameFixed;
    }

    //function to ajust Port Speed
    /**
     * Adjusts the speed of the port to the configured values.
     * @param p The port.
     * @param portname The name of the port.
     * @return The speed of the port.
     */
    private Long speedAjust(Port p, String portname) {

        //default values
        Long speedWired = (long) 2002;
        Long speedWireless = (long) 202;



        //if( (configurations != null) || (LoadsDBConfigurations() == 1) ){
        if( configurations != null){
            speedWired = (long) configurations.get("wired_bw_mbs") ;
            speedWireless = (long) configurations.get("wireless_bw_mbs") ;
        }


        //----Updates the values of speed
        String[] truncated = portname.split("-");

        if (truncated.length == 1){
            return (long) 0;
        }else if(truncated[1].startsWith("eth")) {
            return speedWired;
        }else if(truncated[1].startsWith("mp") || truncated[1].startsWith("wlan")){
            return speedWireless;
        }

        return (long) 0;
    }

}
