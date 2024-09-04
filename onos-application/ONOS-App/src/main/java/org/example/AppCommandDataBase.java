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
package org.example;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.onosproject.cli.AbstractShellCommand;
import org.uc.dei.mei.framework.database.DataBaseWriteInterface;

import java.util.ArrayList;
//import java.util.Arrays;


/**
 * Apache Karaf CLI commands for interacting with the database.
 * Comunication wit DataBase.
 * Disponibilizes the DataBase's tables and actions to the user trough the ONOS CLI.
 */
@Service
@Command(scope = "onos", name = "database",
         description = "Handles DataBase comunications")

public class AppCommandDataBase extends AbstractShellCommand {

    final String[] actionOptions = {"read","write","delete"};

    private static volatile boolean running = false;


   // @Option(name = "-a", aliases = { "--action"}, description = "specifies an action to apply to database table (use with flag --table)", required = false, multiValued = false)
   // String action = null/*actionOptions[0]*/;

    @Option(name = "-t", aliases = { "--table" }, description = "specifies a database table to act on (use with flag --action)", required = false, multiValued = false)
    String table = null /*"device"*/;

    @Option(name = "-iA", aliases = { "--initIntra" }, description = "initializes some Intra DB values, e.g link speed, services, energy", required = false, multiValued = false)
    boolean initIntraBoll = false;

    @Option(name = "-iE", aliases = { "--initInter" }, description = "initializes some Inter DB values, e.g link speed, services, energy", required = false, multiValued = false)
    boolean initInterBoll = false;

    @Option(name = "-iD", aliases = { "--initDemo" }, description = "initializes some Inter DB values, e.g link speed, services, energy", required = false, multiValued = false)
    boolean initDemoBoll = false;

    @Option(name = "-iN", aliases = { "--initNoe" }, description = "initializes Noe Values for complexer H vs F", required = false, multiValued = false)
    boolean initNoeBoll = false;

    @Option(name = "-r", aliases = { "--reset" }, description = "resets the DataBase's schema", required = false, multiValued = false)
    boolean resetBoll = false;

    @Option(name = "-c", aliases = { "--create" }, description = "creates the DataBase's Tables and associations of the schema", required = false, multiValued = false)
    boolean createBoll = false;

    @Option(name = "-s", aliases = { "--snap" }, description = "takes a snapshot of the topology", required = false, multiValued = false)
    boolean snapBoll = false;

    @Option(name = "-l", aliases = { "--loop" }, description = "takes a snapshot of the topology each x sec", required = false, multiValued = false)
    long eachSec = 0;

    @Option(name = "-sl", aliases = { "--stoploop" }, description = "stops the looping action of taking snapchots of the topology", required = false, multiValued = false)
    boolean stopLoopBoll = false;


    @Option(name = "-md", aliases = { "--metricsDevice" }, description = "e.x. of:0000000000000001 |return the bytes Paper metrics", required = false, multiValued = false)
    String uriStr = null /*"of:0000000000000001"*/;

    @Option(name = "-mdt", aliases = { "--metricsDeviceTime" }, description = "e.x. of:0000000000000001 |return the bytes Paper metrics", required = false, multiValued = false)
    String uriStrTime = null /*"of:0000000000000001"*/;


    @Option(name = "-up", aliases = { "--update" }, description = "updates entries", required = false, multiValued = false)
    Boolean updateT = false /*"device"*/;

    @Option(name = "-is", aliases = { "--insert" }, description = "updates entries", required = false, multiValued = false)
    Boolean insertT = false /*"device"*/;

    @Argument(index = 0, name = "arg1", description = "", required = false)
    String arg_1 = null;

    @Argument(index = 1, name = "list", description = "updates entries", required = false, multiValued = true)
    ArrayList<String> listArg = null;




    /**
     * Executes the DataBase command.
     * Verifies which action to take and calls the respective method.
     * Methods come from the DataBaseWriteInterface.
     */
    @Override
    protected void doExecute() {

        if(updateT != false){
            DataBaseWriteInterface serviceDB = get(DataBaseWriteInterface.class);
            //database --update as 2 3
            System.out.println( arg_1 );
            System.out.println( listArg.toString() );
            serviceDB.updateTabel(arg_1, listArg);

        }else if(insertT != false){
            DataBaseWriteInterface serviceDB = get(DataBaseWriteInterface.class);
            serviceDB.insertTabel(arg_1, listArg);
        }


        /*if(action != null  && table != null){
            //table action
            TableAction();
        }else if(( action != null  && table == null) || (action == null  && table != null)) {
            //miss use of table action
            log.info("ERROR, '--action' and '--table' need to be used together");
        */
        if(table != null){
            TableRead();
        }else if(uriStr != null){
            DataBaseWriteInterface serviceDB = get(DataBaseWriteInterface.class);
            serviceDB.MetricsBytes(uriStr);
        }else if(uriStrTime != null){
            DataBaseWriteInterface serviceDB = get(DataBaseWriteInterface.class);
            System.out.println(arg_1);
            serviceDB.MetricsBytesTime(uriStrTime,arg_1);

        }else if(resetBoll == true){
            //reset the DBschema
            DataBaseWriteInterface serviceDB = get(DataBaseWriteInterface.class);
            serviceDB.resetSchema();
        }else if(createBoll == true){
            //creates the DBschema
            DataBaseWriteInterface serviceDB = get(DataBaseWriteInterface.class);
            serviceDB.createSchema();
        }else if(initIntraBoll == true){
            DataBaseWriteInterface serviceDB = get(DataBaseWriteInterface.class);
            serviceDB.innitIntraTables();
        }else if(initInterBoll == true){
            DataBaseWriteInterface serviceDB = get(DataBaseWriteInterface.class);
            serviceDB.innitInterTables();
        }else if(initDemoBoll == true){
            DataBaseWriteInterface serviceDB = get(DataBaseWriteInterface.class);
            serviceDB.innitDemoTables();
        }else if(initNoeBoll == true){
            DataBaseWriteInterface serviceDB = get(DataBaseWriteInterface.class);
            serviceDB.innitNoeTables();
        }else if(snapBoll == true){
            DataBaseWriteInterface serviceDB = get(DataBaseWriteInterface.class);
            serviceDB.write();
        }else if(eachSec != 0){
            //begin looping action
            DataBaseWriteInterface serviceDB = get(DataBaseWriteInterface.class);
            running = true;
            log.info("Starting loop action");
            Thread loopThread = new Thread(() -> {
                while (running) {
                    // Your while loop logic here
                    serviceDB.write();
                    // Sleep for a while to avoid busy-waiting
                    try {
                        Thread.sleep(eachSec*1000); // Adjust sleep duration as needed
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                log.info("While loop has been stopped.");
            });
    
            loopThread.start();
            
            //log.info(serviceDB.writeLoop(eachSec));
        }else if(stopLoopBoll == true){
            //stop looping action
            running = false;
            //DataBaseWriteInterface serviceDB = get(DataBaseWriteInterface.class);
            //serviceDB.stopsLoop();
        /*}else if(action == null && table == null){
            //other action
            DataBaseWriteInterface serviceDB = get(DataBaseWriteInterface.class);
            log.info(serviceDB.write());*/
        }else{
            log.info("TO-DO IMPLEMENTATION");
           //Write all info to DB , for now this is the implementation
        }

    }


    private void TableRead(){
        DataBaseWriteInterface serviceDB = get(DataBaseWriteInterface.class);
        log.info("read on "+table);
        serviceDB.readTable(table);
    }

   /* private void TableAction(){
        //Filters out bad action options
        if(!Arrays.asList(actionOptions).contains(action)){
            log.info("ERROR, only supported options for flag '-action' are: "+ Arrays.toString(actionOptions) );
            return;
        }

        DataBaseWriteInterface serviceDB = get(DataBaseWriteInterface.class);
        log.info(action +" on "+table);
        if(action.equals(actionOptions[0]) == true){
            //read
            serviceDB.readTable(table);
        }else if(action.equals(actionOptions[1]) == true){
            log.info("TODO...");
        }else if(action.equals(actionOptions[2]) == true) {
            log.info("TODO...");
        }else{
            log.info("Error, impossible combination");
        }

    }*/


}
