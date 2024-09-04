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


//import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Interface with the database functions to be used in the CLI.
*/
public interface DataBaseWriteInterface {

    /**
     * Takes a snapshot of the database by being called by the CLI command writeDataBase.
     * @return String Always returns "worksWriteDataBase" even when it doesn't work and does nothing with it.
     */
    String write();

    /**
     * Takes a snapshot of the database at a certain time interval.
     * @param eachSec The time between each snapshot.
     * @return String Always returns "worksWriteDataBaseLoop" even when it doesn't work and does nothing with it.
     */
    String writeLoop(long eachSec);

    /**
     * Stops the loop that takes the snapshots to the database.
     */
    void stopsLoop();

    /** 
     * Resets the database schema.
     * @return String Always returns "worksResetDataBase" even when it doesn't work and does nothing with it.
     */
    String resetSchema();
    
    /**
     * Auxiliary function to create the tables in the database.
     * It is used by the CLI command createDataBase.
     * @return String Always returns "worksCreateDataBase" even when it doesn't work and does nothing with it.
     */
    String createSchema();

    /**
    * Innitialises the intra tables values in the database.
    * Seems identical to innitInterTables but also calls innitTableServiceHostRelationIntra(con);???
    * @return String Always returns "workInnit IntraDatabase" even when it doesn't work and does nothing with it.
    */
    String innitIntraTables();

    /**
    * Innitialises the inter tables values in the database.
    * @return String Always returns "workInnit InterDatabase" even when it doesn't work and does nothing with it.
    */
    String innitInterTables();

    /**
     * Innitialises the demo tables values in the database.
     * @return String Always returns "workInnit IntraDataBase" even when it doesn't work and does nothing with it.
     */
    String innitDemoTables();

    /**
     * Initialises the tables Noe values in the database.
     * Initializes Noe Values for complexer H vs F.
     * @return String Always returns "workInnit IntraDataBase" even when it doesn't work and does nothing with it.
     */
    String innitNoeTables();

    /**
     * Reads the data from the table and prints it to the console.
     * @param table The table to be read.
     */
    void readTable(String table);

    /**
     * Calculates and prints the proportion of the total packets sent by each port in a device at the latest time.
     * @param swStr (uriStr in the actual function) Device thats being collected.
     */
    void MetricsBytes(String swStr);

    /**
     * Calculates and prints the proportion of the total packets sent by each port in a device at a certain time.
     * @param swStr (uriStr in the actual function) Device thats being collected.
     * @param timeepoch (time in the actual function) TimeStamp of the metric.
     */
    void MetricsBytesTime(String swStr, String timeepoch);

    /**
     * Updates the table with the list. 
     * @param table to be updated.
     * @param list values that are going to be updated in the table.
     */
    void updateTabel(String table, ArrayList<String> list);

    /** 
     * Inserts the list into a table.
     * @param table to be inserted to.
     * @param list values that are going to be introduced in the table.
     */
    void insertTabel(String table, ArrayList<String> list);

    /**
     * Gets the slices from the database and prints them to the console.
     */
    void getSlices();

    /**
     * Gets a slice by type from the database.
     * @param sliceType The type of slice to be retrieved.
     * @return Map A map with the slice properties.
     */
    Map<String, String> getSliceByType(String sliceType);

    /**
     * Gets the link from the database by source and destination.
     * @param srcURI The source URI.
     * @param dstURI The destination URI.
     * @param srcPort The source port.
     * @param dstPort The destination port.
     * @return Map A map with the link properties.
     */
    Map<String, String> getLinkBySrcToDst(String srcURI, String dstURI, int srcPort, int dstPort);

    /**
     * Gets a slice by a certain DSCP value.
     * @param dscp The DSCP value.
     * @return Map A map with the slice properties.
     */
    Map<String, String> getSliceByDSCP(int dscp);
}