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
import org.uc.dei.mei.framework.algorithm.AlgorithmInterface;

/**
 * Apache Karaf CLI commands for interacting with the algorithm.
 */
@Service
@Command(scope = "onos", name = "algo",
         description = "responsible for fairness mechanisms")

public class AppCommandAlgorithm extends AbstractShellCommand {

    @Option(name="-getAlgos",description = "get algoritmos info") boolean getAlgosBoll;
    //@Option(name="-setAlgo",description = "set the currrent values from the configuration tables pulled from DB") boolean seePullDBConfBoll;
    @Option(name="-currentAlgo",description = "get current fw algo") boolean currentAlgoBoll;

    @Option(name="-pullDB",description = "pull the values from the configuration tables in the DB") boolean pullDBConfBoll;
    @Option(name="-seeDBVal",description = "set the currrent values from the configuration tables pulled from DB") boolean seePullDBConfBoll;

    @Option(name="-lossO",description = "returns the difference between packets sent and received of a Link") boolean lossOPackBoll;
    @Option(name="-loss",description = "returns the difference between packets sent and received ofa sw") boolean lossPackBoll;
    @Option(name="-lossDebug",description = "returns the difference between packets sent and received of a sw") boolean lossPackDEBUGBoll;


    @Option(name="-setL",description = "updated the loss values in the tracking graph") boolean updateLossValuesBoll;
    @Option(name="-getL",description = "retrieves the loss values in the tracking graph") boolean getLossValuesBoll;


    @Option(name="-setNoe",description = "retrieves the loss values in the tracking graph") boolean setNoeBoll;
    @Option(name="-getNoe",description = "retrieves the loss values in the tracking graph") boolean getNoeBoll;

    @Option(name="-addP",description = "adds custom Packet Process") boolean addProcessBoll;
    @Option(name="-rmP",description = "removes custom Packet Process") boolean removeProcessBoll;

    @Option(name="-rFP",description = "resets the fainess path history") boolean resFPBoll;
    @Option(name="-rOP",description = "resets the onos path history") boolean resOPBoll;

    @Option(name="-gFP",description = "gets the fainess path history") boolean getFPBoll;
    @Option(name="-gOP",description = "gets the onos path history") boolean getOPBoll;

    @Option(name="-getTBW",description = "retrieves the total bw values of the tracking graph") boolean geAllTotalBWPBoll;
    @Option(name="-setLinkLoss",description = "Ex: sw1 sw4 0.03 | sets the loss value of a link in the traking graph") boolean setLinkLossBoll;



    @Option(name="-jgrapht",description = "returns information of hosts in topology ") boolean jgraphtBoll;
    @Option(name="-onos",description = "returns information of links in topology") boolean onosBoll;



    @Argument(index = 0, name = "arg1", description = "", required = false)
    String arg_1 = null;
    @Argument(index = 1, name = "arg2", description = "", required = false)
    String arg_2 = null;
    @Argument(index = 2, name = "arg3", description = "", required = false)
    String arg_3 = null;


    @Override
    protected void doExecute() {

        AlgorithmInterface service = get(AlgorithmInterface.class);




        if(jgraphtBoll == true){
            //NAO FUNCIONAM PKL EU NAO ATUALIZO O SRC/ SUPPLY E O DST/DEMAND
            //log.info(service.getFairPath("video").toString());
        }else if(onosBoll == true){
            //log.info(service.getONOSShortPath("video").toString());
        }else if(lossOPackBoll == true){
            log.info("Packet diff between "+arg_1+" and "+arg_2+":"+service.getPacktDiffLink(arg_1, arg_2) );
        }else if(lossPackBoll == true){
            log.info("Packet SUM diff in "+arg_1+": "+service.getPacktDiffSW(arg_1, arg_2) );
        }else if(lossPackDEBUGBoll == true){
            service.getLinkLossDEBUG(arg_1,arg_2);

        }else if(updateLossValuesBoll == true){
            service.hardUpdateLoss();
        }else if(getLossValuesBoll == true){
            log.info(service.getStoredLoss());

        }else if(getNoeBoll == true){
            log.info(""+service.getNoe());
        }else if(setNoeBoll == true){
            service.setNoe(Boolean.getBoolean(arg_1));

        }else if(getFPBoll == true){
            log.info("FAIRNESS PATH");
            for ( String ss : service.getPHistF()) {
                log.info(ss);
            }

        }else if(getOPBoll == true){
            log.info("ONOS PATH");
            for ( String ss : service.getPHistO()) {
                log.info(ss);
            }
        }else if(geAllTotalBWPBoll == true){
            log.info(service.getAllTotalBW());


        }else if(resFPBoll == true){
            service.resetFPathHistoy();
        }else if(resOPBoll == true){
            service.resetOPathHistory();

        }else if(setLinkLossBoll == true){
            service.setLinkLossG(arg_1,arg_2,arg_3);
        }else if(addProcessBoll == true){
            service.addProcess();
        }else if(removeProcessBoll == true){
            service.removeProcess();

        }else if(pullDBConfBoll == true){
            log.info(""+service.pullDBConf() );
        }else if(seePullDBConfBoll == true) {
            log.info("" + service.seePullDBConf());


        }else if(getAlgosBoll == true){
            log.info("Avaiable Algorithms: ");
            service.getAlgos();


        }else if(currentAlgoBoll == true){
            log.info("Current Algorithm: "+service.seePullDBConf()  );

        }else{
            //TODO uncomment
            //log.info(service.getFairPath("video").toString());
        }




    }

}
