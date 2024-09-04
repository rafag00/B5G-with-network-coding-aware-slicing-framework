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
package org.uc.dei.mei.framework.onospath;


import org.helper.CustomLinkWeigher;
import org.helper.EnergyLinkWeigher;
import org.helper.EnergyLinkWeigherAlgo;
import org.helper.SliceLinkWeigher;
import org.onosproject.net.*;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.link.LinkService;
import org.onosproject.net.topology.*;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uc.dei.mei.framework.algorithm.AlgoHelper;

import javax.sql.DataSource;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Implements the PathInterface and uses the ONOS API to calculate paths between two elements.
 */
@Component(immediate = true,
        service = {PathInterface.class}
)

public class ONOSAppPathComponent implements PathInterface {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected PathService pathService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected LinkService linkService;

    @Reference
    private DataSource dataSource;

    @Activate
    protected void activate(){
        log.info("Started Path Component - rik");
    }

    @Deactivate
    protected void deactivate() {
        log.info("Stopped Path Component - rik");
    }


    @Override
    public String getDisjoint(ElementId srcID, ElementId dstID, String weigherSTR){

        LinkWeigher weigher = new CustomLinkWeigher();
        if(weigherSTR.equals("geo")) {
            weigher = null;
        }else if(weigherSTR.equals("hop")){
            weigher = HopCountLinkWeigher.DEFAULT_HOP_COUNT_WEIGHER;
        }else if(weigherSTR.equals("energy")){
            weigher = new EnergyLinkWeigher("video", dataSource);
        }

        System.out.println(pathService.getDisjointPaths(srcID, dstID, weigher));


        /*DeviceId src = DeviceId.deviceId("of:1000000000000006");
        DeviceId dst = DeviceId.deviceId("of:1000000000000009");

        HostId srch = HostId.hostId("AA:BB:CC:DD:00:0B/None");
        HostId dsth = HostId.hostId("AA:BB:CC:DD:00:0C/None");

        System.out.println("----------");


        //http://api.onosproject.org/1.13.2/org/onosproject/net/topology/PathService.html
        System.out.println("Dijont 06 to 09"+pathService.getDisjointPaths(src, dst));
        System.out.println("Dijont 0B(11) to 0C(12)"+pathService.getDisjointPaths(srch, dsth));

        System.out.println("----------");

        //System.out.println("K 06 to 09"+pathService.getKShortestPaths(src, dst).toString());

        pathService.getKShortestPaths(src, dst).forEach(s -> System.out.println("K 0B 06 to 09"+s));
        pathService.getKShortestPaths(srch, dsth).forEach(s -> System.out.println("K 0B(11) to 0C(12)"+s));

        System.out.println("----------");*/


        return "getDisjointPath";
    }


    @Override
    public Path getK(ElementId srcID, ElementId dstID, String weigherSTR, String service_str){

        LinkWeigher weigher = new CustomLinkWeigher();
        if(weigherSTR.equals("geo")) {
            weigher = null;
        }else if(weigherSTR.equals("hop")){
            weigher = HopCountLinkWeigher.DEFAULT_HOP_COUNT_WEIGHER;
        }else if(weigherSTR.equals("energy")){
            weigher = new EnergyLinkWeigher(service_str, dataSource);
        }


        //pathService.getKShortestPaths(srcID, dstID, weigher ).forEach(s -> System.out.println(s));
        //System.out.println(pathService.getPaths(srcID, dstID, weigher).toString());

        Path minPath = pathService.getKShortestPaths(srcID, dstID, weigher).limit(50).min(Comparator.comparing(Path::weight))
                .orElseThrow(NoSuchElementException::new);

        System.out.println(minPath.weight());
        System.out.println(minPath.links());

        return minPath;


        /*System.out.println("Weigth:" + weigher.toString());
        System.out.println("Weigth:" + weigher.getInitialWeight());
        System.out.println("Weigth:" + weigher.getNonViableWeight());


        PortNumber pn = PortNumber.fromString("3");
        ConnectPoint ccSrc = new ConnectPoint(srcID, pn);
        ConnectPoint ccDst = new ConnectPoint(dstID, pn);

        Link l = linkService.getLink( ccSrc, ccDst);

        TopologyEdge topoedge = new DefaultTopologyEdge(new DefaultTopologyVertex(l.src().deviceId()), new DefaultTopologyVertex(l.dst().deviceId()) , l);

        System.out.println("Edge222:" + topoedge.toString());

        System.out.println("peso222:"+weigher.weight(topoedge));*/


        //return "getDisjointPath";
    }

    
    @Override
    public Path getKAlgo(ElementId srcID, ElementId dstID, String service_str, AlgoHelper algohelper){

        //LinkWeigher weigher  = new EnergyLinkWeigherAlgo(service_str, dataSource, algohelper);
        LinkWeigher weigher  = new EnergyLinkWeigherAlgo(service_str, algohelper);

        Path minPath = pathService.getKShortestPaths(srcID, dstID, weigher).limit(50).min(Comparator.comparing(Path::weight))
                .orElseThrow(NoSuchElementException::new);


        return minPath;

    }

    @Override
    public Path getSlicePath(ElementId srcId, ElementId dstId, String sliceType, double sLoss, double sJit, double sLat, double sBw) {    
        double impBw, impLoss, impLat, impJit, impEne;

        //Define the importance of each property considering the slice type
        switch (sliceType) {
            case "eMBB":
                impBw = -10;
                impLoss = 3;
                impLat = 7;
                impJit = 5;
                impEne = 1;
                break;
            case "uRLLC":
                impBw = -3;
                impLoss = 10;
                impLat = 10;
                impJit = 7;
                impEne = 1;
                break;
            case "mMTC":
                impBw = -1;
                impLoss = 3;
                impLat = 1;
                impJit = 1;
                impEne = 10;
                break;
            case "uLBC":
                impBw = -7;
                impLoss = 7;
                impLat = 7;
                impJit = 5;
                impEne = 1;
                break;
            case "BE":
                Random rand = new Random();
                impBw = (double)(-(rand.nextInt(10)+1));
                impLoss = (double)((rand.nextInt(10)+1));
                impLat = (double)((rand.nextInt(10)+1));
                impJit = (double)((rand.nextInt(10)+1));
                impEne = (double)((rand.nextInt(10)+1));
                break;
            default:
                log.info("Slice type not found");
                return null;
        }

        SliceLinkWeigher weigher = new SliceLinkWeigher(sLoss, sJit, sLat, sBw, impBw, impLoss, impLat, impJit, impEne);
        //log.info("After creating weigher");
        //.min(Comparator.comparing(Path::weight)): This part of the code sorts the returned paths in ascending order based on their weights and selects the path with the minimum weight. The Comparator.comparing(Path::weight) part creates a comparator that compares paths based on their weights.
        //log.info("Path without weight: "+pathService.getKShortestPaths(srcId, dstId).min(Comparator.comparing(Path::weight)).orElseThrow(NoSuchElementException::new));

        Path calcPath = pathService.getKShortestPaths(srcId, dstId, weigher).limit(5).min(Comparator.comparing(Path::weight)).orElseThrow(NoSuchElementException::new);
        //log.info("Calculating path");
        return calcPath;
    }
}


