package org.uc.dei.mei.framework.sliceAlgo;

import org.onlab.packet.Ethernet;
import org.onlab.packet.IPv4;
import org.onlab.packet.MacAddress;
import org.onosproject.core.DefaultApplicationId;
import org.onosproject.core.HybridLogicalClockService;
import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import org.onosproject.net.ElementId;
import org.onosproject.net.Host;
import org.onosproject.net.Link;
import org.onosproject.net.Path;
import org.onosproject.net.PortNumber;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.flow.FlowEntry;
import org.onosproject.net.host.HostService;
import org.onosproject.net.packet.InboundPacket;
import org.onosproject.net.packet.PacketContext;
import org.onosproject.net.packet.PacketProcessor;
import org.onosproject.net.packet.PacketService;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uc.dei.mei.framework.database.DataBaseWriteInterface;
import org.uc.dei.mei.framework.flowrule.FlowRuleInterface;
import org.uc.dei.mei.framework.onospath.PathInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

@Component(immediate = true,
        service = {SliceProcessorInterface.class}
)

public class ONOSAppSliceProcessor implements SliceProcessorInterface {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private PacketProcessorNetworkSlice sliceProcessor = new PacketProcessorNetworkSlice();

    private Map<MacAddress, List<MacAddress>> flowCombinations;

    private Object mutex;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected PacketService packetService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected HostService hostService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DataBaseWriteInterface dbService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected PathInterface pathService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected FlowRuleInterface flowRuleService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected HybridLogicalClockService hybridLogicalClockService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DeviceService deviceService;

    @Activate
    protected void activate() {
        packetService.addProcessor(sliceProcessor, PacketProcessor.director(2));
        mutex = new Object();
        flowCombinations = new HashMap<>();

        // Install initial flow rule for all devices
        for (Device device : deviceService.getAvailableDevices()) {
            flowRuleService.installInitialFlowRuleSlice(device.id());
        }

        log.info("Started Slice Component - PROCESSOR");
    }

    @Deactivate
    protected void deactivate() {
        packetService.removeProcessor(sliceProcessor);
        sliceProcessor = null;

        // Remove flow rules
        for (Device device : deviceService.getAvailableDevices()) {
            flowRuleService.removeInitialFlowRuleSlice(device.id());
        }

        log.info("Stopped Slice Component - PROCESSOR");
    }

    @Override
    public void addProcess(){
        packetService.addProcessor(sliceProcessor, PacketProcessor.director(2));
        log.info("Added Slice Processor");
    }

    @Override
    public void removeProcess(){
        packetService.removeProcessor(sliceProcessor);
        log.info("Removed Slice Processor");
    }

    /**
     * Check if the flow rules are already installed
     * @param flowEntries Flow entries to check
     * @param srcMac Source MAC address
     * @param dstMac Destination MAC address
     * @param dscp DSCP value
     * @return True if the flow rules are already installed, false otherwise
     */
    private boolean checkFlowRules(Iterable<FlowEntry> flowEntries, MacAddress srcMac, MacAddress dstMac, Byte dscp){
        for(FlowEntry flowEntry : flowEntries){
            if(flowEntry.selector().criteria().toString().contains("ETH_DST:" + dstMac.toString()) && flowEntry.selector().criteria().toString().contains("ETH_SRC:" + srcMac.toString()) && flowEntry.selector().criteria().toString().contains("IP_DSCP:" + dscp)){
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the flow combinations are still valid
     * @param flowEntries Flow entries to check
     */
    private void checkFlowCombinations(Iterable<FlowEntry> flowEntries){
        //Clear the flow combinations if the uplink flow isn't installed
        log.info("Checking flow combinations!!!");
        for(MacAddress srcMac : flowCombinations.keySet()){
            List<MacAddress> valuesToRemove = new ArrayList<>();
            for(MacAddress dstMac : flowCombinations.get(srcMac)){
                boolean found = false;
                for(FlowEntry flowEntry : flowEntries){
                    if(flowEntry.selector().criteria().toString().contains("ETH_DST:" + dstMac.toString()) && flowEntry.selector().criteria().toString().contains("ETH_SRC:" + srcMac.toString())){
                        log.info("Flow uplink combination exists in entry: "+srcMac.toString()+" -> "+dstMac.toString());
                        found = true;
                        break;
                    }
                }
                if(!found){
                    valuesToRemove.add(dstMac);
                    log.info("Flow uplink combination added to remove: "+srcMac.toString()+" -> "+dstMac.toString());
                }
            }
            List<MacAddress> aux = flowCombinations.get(srcMac);
            aux.removeAll(valuesToRemove);
            flowCombinations.put(srcMac, aux);
        }
        log.info("Finished checking flow combinations!!!");
    }

    /**
     * Install the flow rules in the switches
     * @param path Path to install the flow rules
     * @param srcMac Source MAC address
     * @param dstMac Destination MAC address
     * @param firstPort First port of the path
     * @param dscp DSCP value
     * @param timeout Timeout value
     */
    private void installRules(Path path, MacAddress srcMac, MacAddress dstMac, PortNumber firstPort, Byte dscp, short timeout){
        //To add a flow we need to know the device we at and the next device in the path
        //We also need to know the input and output ports (I think they are in the path object)
        
        //Get the application ID
        short appID = 24; //because it was found in AppDataBaseComponent innitTableGeneralConfig
        //Get the priority
        int priority = 16; //because it was found in AppDataBaseComponent innitTableGeneralConfig
        
        flowRuleService.addFlowRuleSlice(path.src().deviceId(), appID, timeout, priority, (int) firstPort.toLong(), (int) path.src().port().toLong(), srcMac, dstMac, dscp);

        List<Link> links = path.links();
        
        for(int i=0; i<links.size(); i++){
            //Get the source and destination devices
            DeviceId device = links.get(i).dst().deviceId();
            //Get the input and output ports
            int inPort = (int) links.get(i).dst().port().toLong();
            int outPort = -1;

            if (i+1 == links.size()){
                //is the last link so go look for host port
                Set<Host> dstHost = hostService.getHostsByMac(dstMac);
                if(dstHost.size() != 1){
                    log.info("No destination host found in the topology.");
                    return;
                }

                outPort = (int) dstHost.iterator().next().location().port().toLong();

            }
            else{
                outPort = (int) links.get(i+1).src().port().toLong();
            }

            //Add the flow rule to the source device
            flowRuleService.addFlowRuleSlice(device, appID, timeout, priority, inPort, outPort, srcMac, dstMac, dscp);
        }
    }

    /**
     * Packet processor to handle the packets and install the flow rules for network slicing
     */
    private class PacketProcessorNetworkSlice implements PacketProcessor {
            
        @Override
        public void process(PacketContext context) {
            // Stop processing if the packet has been handled, since we
            // can't do any more to it.

            if (context.isHandled()) {
                return;
            }

            InboundPacket pkt = context.inPacket();
            Ethernet ethPkt = pkt.parsed();

            if (ethPkt == null) {
                //This processor only handles ethernet packets
                log.info("Packet In:" + pkt.toString() + " - Error parsing Ethernet frame");
                return;
            }

            // Check if the packet contains IPv4 payload
            if (ethPkt.getEtherType() == Ethernet.TYPE_IPV4){
                IPv4 ipv4Packet = (IPv4) ethPkt.getPayload();
                byte dscp = (byte) 0000000; //6 bits of DSCP
                ElementId srcID = null;
                ElementId dstID = null;
                double up_bw = 0;
                double dw_bw = 0;
                double lat = 0;
                double loss = 0;
                double jit = 0;

                if(ipv4Packet.getProtocol() == IPv4.PROTOCOL_ICMP){
                    //log.info("ICMP packet");
                    return;
                }

                try{
                    dscp = ipv4Packet.getDscp();
                    log.info("DSCP VALUE: " + dscp);
                }
                catch(Exception e){
                    log.info("Packet In:" + pkt.toString() + " - No dscp found");
                    return;
                }

                MacAddress dstMac;
                MacAddress srcMac;

                //Obtain the Element Id of the source and destination
                try{
                    dstMac = ethPkt.getDestinationMAC();
                    srcMac = ethPkt.getSourceMAC();

                    Set<Host> dstHost = hostService.getHostsByMac(dstMac);
                    Set<Host> srcHost = hostService.getHostsByMac(srcMac);

                    if(dstHost.size() != 1 || srcHost.size() != 1){
                        log.info("No source or destination host found in the topology. - dstHostsize: "+dstHost.size()+" - srcHostsize: "+srcHost.size()+" - dstMac: "+dstMac.toString()+" - srcMac: "+srcMac.toString());
                        return;
                    }

                    dstID = dstHost.iterator().next().location().elementId();
                    srcID = srcHost.iterator().next().location().elementId();

                    if(dstID == null || srcID == null){
                        log.info("No source or destination ID found in the topology.");
                        return;
                    }

                    log.info("Source ID: " + srcID + " - Destination ID: " + dstID);
                }
                catch (Exception e){
                    log.info("Error getting source and destination MAC addresses");
                    return;
                }

                synchronized (mutex) {

                    //verify if the flow is already installed (NOT WORKING I think)
                    Iterable<FlowEntry> flowEntries = flowRuleService.getFlowRulesAppId(new DefaultApplicationId(24, "Network_slicing"));

                    checkFlowCombinations(flowEntries);

                    log.info(flowCombinations.toString());

                    if(checkFlowRules(flowEntries, srcMac, dstMac, dscp)){
                        log.info("Flow already installed");
                        return;
                    }

                    int dscp_int = -1;

                    try{
                        dscp_int = Byte.toUnsignedInt(dscp);
                    }catch(Exception e){
                        log.info("Error converting DSCP to int");
                        return;
                    }

                    //Query DB for slice properties
                    Map<String, String> sliceProperties = dbService.getSliceByDSCP(dscp_int);
                    String slice = "BE";

                    if(sliceProperties.isEmpty()){
                        log.info("NO SLICE FOUND USING BE");
                        sliceProperties = dbService.getSliceByType("BE");
                        //return;
                    }
                    else{
                        log.info("SLICE FOUND: "+sliceProperties.toString());
                    }

                    try{
                        up_bw = Double.parseDouble(sliceProperties.get("up_bw"));
                        dw_bw = Double.parseDouble(sliceProperties.get("dw_bw"));
                        loss = Double.parseDouble(sliceProperties.get("loss_prob"));
                        lat = Double.parseDouble(sliceProperties.get("latency"));
                        jit = Double.parseDouble(sliceProperties.get("jitter"));
                        slice = sliceProperties.get("sst_type");
                    }
                    catch(Exception e){
                        log.info("Error parsing slice properties");
                        return;
                    }


                    //Check if is uplink or downlink - we are considering that the first device to make a connection is the uplink
                    List<MacAddress> uplinkList = flowCombinations.get(dstMac);

                    log.info("UPLINK LIST: "+uplinkList);

                    double bw = 0;
                    boolean isUplink = true;
                    if(uplinkList != null && uplinkList.contains(srcMac)){
                        bw = dw_bw;
                        isUplink = false;
                        log.info("USING DOWNLINK: "+ dw_bw);
                    }
                    else{
                        bw = up_bw;
                        isUplink = true;
                        log.info("USING UPLINK: "+ up_bw);
                    }
                    

                    Path pathFound = null;
                    //Call the slice path selection algorithm
                    try{
                        pathFound = pathService.getSlicePath(srcID, dstID, slice, loss, jit, lat, bw);
                    }catch(NoSuchElementException e){
                        log.info("No path was found!!");
                        return;
                    }catch(IllegalArgumentException e){
                        log.info("No path was found!! (Maybe connected by the same switch)");
                        return;
                    }

                    //print path
                    for(Link link : pathFound.links()){
                        log.info("Link: " + link.src().deviceId() + " - " + link.dst().deviceId());
                    }

                    short timeout = 0;

                    try{
                        timeout = (short)((lat + jit)/1000 + 5);
                        log.info("Timeout: "+timeout+" s");
                    }catch(Exception e){
                        log.info("Error calculating timeout");
                        return;
                    }
                    

                    //Apply the flow rules to the switches
                    installRules(pathFound, srcMac, dstMac, pkt.receivedFrom().port(), dscp, timeout);
                    
                    //Add the flow combination to the list
                    if(isUplink){
                        List<MacAddress> aux = flowCombinations.get(srcMac);
                        if (aux == null){
                            aux = new ArrayList<>(Collections.singletonList(dstMac));
                        }
                        else{
                            aux.add(dstMac);
                            
                        }
                        flowCombinations.put(srcMac, aux);
                        log.info("Updating list with new uplink: "+srcMac.toString()+" -> "+dstMac.toString());
                    }
                    
                    context.block();
                }
            }
            else {
                //This processor only handles ipv4 packets
                return;
            }
        }
    }
}
