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
package org.uc.dei.mei.framework.flowrule;

import org.onlab.packet.MacAddress;
import org.onosproject.core.DefaultApplicationId;
import org.onosproject.net.DeviceId;
import org.onosproject.net.flow.FlowEntry;
import org.onosproject.net.flow.FlowRule;

/**
 * Service for managing flow rules
 */
public interface FlowRuleInterface {

    /** 
     * Adds a flow rule to the device ARP (Address Resolution Protocol) table.
     * If the timeout is -1, the rule will be permanent.
     * If the out_port is -1, the rule will drop the packet.
     * @param deviceId The device ID.
     * @param appID The application ID.
     * @param timeout The timeout.
     * @param priority The priority level.
     * @param in_port The input port.
     * @param out_port The output port.
     * @param src_mac The source MAC address.
     * @param dst_mac The destination MAC address.
     * @return String Always returns "addFlowRule".
     */
    String addFlowMACARP(String deviceId, short appID, short timeout, int priority, int in_port,int out_port, String src_mac, String dst_mac);
    
    /** 
     * Adds a flow rule to the device using the mac addresses of the source and destination.
     * If the timeout is -1, the rule will be permanent.
     * If the out_port is -1, the rule will drop the packet.
     * @param deviceId The device ID.
     * @param appID The application ID.
     * @param timeout The timeout.
     * @param priority The priority level.
     * @param in_port The input port.
     * @param out_port The output port.
     * @param src_mac The source MAC address.
     * @param dst_mac The destination MAC address.
     * @return FlowRule The added flow rule.
     */
    FlowRule addFlowMAC(String deviceId, short appID, short timeout, int priority, int in_port, int out_port, String src_mac, String dst_mac);
    
    /** 
     * Adds a flow rule to the device using the IP addresses of the source and destination.	
     * If the timeout is -1, the rule will be permanent.
     * If the out_port is -1, the rule will drop the packet.
     * @param deviceId The device ID.
     * @param appID The application ID.
     * @param timeout The timeout.
     * @param priority The priority level.
     * @param in_port The input port.
     * @param out_port The output port.
     * @param src_mac (src_str in the actual function) The source IP address.
     * @param dst_mac (dst_str in the actual function) The destination IP address.
     * @return String Always returns "addFlowRule".
     */
    String addFlowIP(String deviceId, short appID, short timeout, int priority, int in_port,int out_port, String src_mac, String dst_mac);
    
    /**
     * Remove all flow rules from a specific application. 
     * @param appID The application ID.
     */
    void removeFlowRulesApp(short appID);

    /**
     * Gets all flow rules from a specific device.
     * @param deviceId The device ID.
     * @return Iterable of FlowEntry The flow rules from the device.
     */
    Iterable<FlowEntry> getFlowRulesDevice(String deviceId);
    
    /**
     * Returns the number of packet associated with specific flow rules on a given device.
     * @param appID The application ID.
     * @param deviceId The device ID.
     * @param src_mac The source MAC address.
     * @param dst_mac The destination MAC address.
     * @return Integer The packet count.
     */
    Integer getFlowRulesDevicePacketCount(short appID, String deviceId, String src_mac, String dst_mac );

    /**
     * Adds a flow rule to the device using the mac addresses of the source and destination.
     * If the timeout is -1, the rule will be permanent.
     * If the out_port is -1, the rule will drop the packet.
     * @param deviceId The device ID.
     * @param appID The application ID.
     * @param timeout The timeout.
     * @param priority The priority level.
     * @param in_port The input port.
     * @param out_port The output port.
     * @param src_mac The source MAC address.
     * @param dst_mac The destination MAC address.
     * @param dscp The DSCP (Differentiated Services Code Point).
     * @return FlowRule The added flow rule.
     */
    FlowRule addFlowRuleSlice(DeviceId deviceId, short appID, short timeout, int priority, int in_port, int out_port, MacAddress src_mac, MacAddress dst_mac, Byte dscp);

    /**
     * Installs the initial flow rule slice that directs the IPv4 traffict to the packet processor.
     * @param deviceId The device ID.
     */
    void installInitialFlowRuleSlice(DeviceId deviceId);

    /**
     * Removes the initial flow rule slice that directs the IPv4 traffict to the packet processor.
     * @param deviceId The device ID.
     */
    void removeInitialFlowRuleSlice(DeviceId deviceId);
    
    /**
     * Gets all flow rules from a specific application.
     * @param appID The application ID.
     * @return Iterable of FlowEntry The flow rules from the application.
     */
    Iterable<FlowEntry> getFlowRulesAppId(DefaultApplicationId appID);
}