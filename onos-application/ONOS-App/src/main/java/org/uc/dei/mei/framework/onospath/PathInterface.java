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

import org.onosproject.net.ElementId;
import org.onosproject.net.Path;
//import org.onosproject.net.topology.LinkWeigher;
import org.uc.dei.mei.framework.algorithm.AlgoHelper;

//import javax.sql.DataSource;

/**
 * Interface with the path functions to be used in the CLI.
 */
public interface PathInterface {
        
    /** 
     * It's called by the CLI command to get the disjoint paths between two elements.
     * First calculates the weigh of the path and them caculates by calling getDisjointPaths from ONOS API.
     * @param srcID ID of the source element.
     * @param dstID ID of the destination element.
     * @param weigher (weigherSTR in the actual function) Type of weigher to be used (geo, hop, energy).
     * @return String Always returns "getDisjointPath".
     */
    String getDisjoint(ElementId srcID, ElementId dstID, String weigher);

    /** 
     * It's called by the CLI command to get the k shortest paths between two elements.
     * First calculates the weigh of the path and them caculates by calling getKShortestPath from ONOS API.
     * @param srcID ID of the source element.
     * @param dstID ID of the destination element.
     * @param weigher (weigherSTR in the actual function) Type of weigher to be used (geo, hop, energy).
     * @param service_str Type of service to be used.
     * @return Path Returns the path with the lowest weight.
     */
    Path getK(ElementId srcID, ElementId dstID, String weigher, String service_str);

    /** 
     * It's called by the ONOSAppAlgotithm to get the k shortest paths between two elements.
     * Uses the EnergyLinkWeigherAlgo to calculate the weigh of the path and them caculates by calling getKShortestPath from ONOS API.
     * @param srcID ID of the source element.
     * @param dstID ID of the destination element.
     * @param service_str Type of service to be used.
     * @param algohelper Helper class to be used.
     * @return Path Returns the path with the lowest weight.
     */
    Path getKAlgo(ElementId srcID, ElementId dstID, String service_str, AlgoHelper algohelper);

    /**
     * It's called by the ONOSAppSliceProcessor to get the k shortest paths between two elements.
     * Uses the SliceLinkWeigher to calculate the weigh of the path and them caculates by calling getKShortestPath from ONOS API.
     * @param srcId ID of the source element.
     * @param dstId ID of the destination element.
     * @param sliceType Type of slice to be used.
     * @param sLoss Loss of the slice.
     * @param sJit Jitter of the slice.
     * @param sLat Latency of the slice.
     * @param sBw Bandwidth of the slice.
     * @return Path Returns the path with the lowest weight.
     */
    Path getSlicePath(ElementId srcId, ElementId dstId, String sliceType, double sLoss, double sJit, double sLat, double sBw);
}