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
import org.onosproject.net.flow.FlowRule;
import org.uc.dei.mei.framework.flowrule.FlowRuleInterface;



/*
*    93  flowrules -addD 1 of:0000000000000004 -1 9 MAC AA:BB:CC:DD:00:02 AA:BB:CC:DD:00:01 2
   94  flowrules -addD 1 of:0000000000000004 -1 9 MAC AA:BB:CC:DD:00:02 AA:BB:CC:DD:00:01 3
   95  flowrules -addD 1 of:0000000000000004 -1 9 MAC AA:BB:CC:DD:00:02 AA:BB:CC:DD:00:01 4
   96  flowrules -addD 1 of:0000000000000004 -1 9 MAC AA:BB:CC:DD:00:01 AA:BB:CC:DD:00:02 4
   97  flowrules -addD 1 of:0000000000000004 -1 9 MAC AA:BB:CC:DD:00:01 AA:BB:CC:DD:00:02 3
   98  flowrules -addD 1 of:0000000000000004 -1 9 MAC AA:BB:CC:DD:00:01 AA:BB:CC:DD:00:02 2
   99  flowrules -add 1 of:1000000000000001 -1 9 MAC AA:BB:CC:DD:00:01 AA:BB:CC:DD:00:02 1
  100  flowrules -add 1 of:1000000000000002 -1 9 MAC AA:BB:CC:DD:00:02 AA:BB:CC:DD:00:01 1
  101  flowrules -addD 1 of:1000000000000003 -1 9 MAC AA:BB:CC:DD:00:01 AA:BB:CC:DD:00:02 2
  102  flowrules -addD 1 of:1000000000000003 -1 9 MAC AA:BB:CC:DD:00:02 AA:BB:CC:DD:00:01 2
  *
* */

/**
 * Apache Karaf CLI commands for interacting with the flow rules.
 */
@Service
@Command(scope = "onos", name = "flowrules",
         description = "Prints the current topology nodes specification")

//Class que o onos usa para remover/ listar flos no cli:
//https://github.com/opennetworkinglab/onos/blob/dc08c95eed882936edf47527ca64d416009e8514/cli/src/main/java/org/onosproject/cli/net/FlowsListCommand.java#L177

public class AppCommandFlowRules extends AbstractShellCommand {

    @Option( name="-getFlows",description = "gets the flow entries of a device") boolean getFlowsBoll;
    @Option( name="-getFlowsPackets",description = "gets the flow entries of a device") boolean getFlowPacketBoll;


    @Option( name="-addIntraAM",description = "preset rules for testing") boolean addIntraMBoll;
    @Option( name="-addIntraAW",description = "preset rules for testing") boolean addIntraWBoll;


    @Option( name="-rmR",description = "removes flow rule ao a App ID") boolean rmRuleBoll;


    @Option( name="-add",description = "adds a flow rule to a device") boolean addBoll;
    @Option( name="-addD",description = "adds a flow rule to a device to Drop the packet") boolean addDropBoll;
    @Option( name="-addArp",description = "adds a flow rule to a device to arp") boolean addArpBoll;



    @Argument(index = 0, name = "appId", description = "AppId of the flowRule (can be cookie-TODO)", required = false)
    String app_id_str = "1";

    @Argument(index = 1, name = "DeviceURI", description = "device identifier", required = false)
    String device_uri_str;


    @Argument(index = 2, name = "timeout", description = "flow timeout in seconds [0-60] (-1 to make it permanent)", required = false)
    String timeout_str;

    @Argument(index = 3, name = "priority", description = "flow priority [0-~65300]", required = false)
    String priority_str;

    @Argument(index = 4, name = "match_type", description = "defines the mathing is by IP or MAC", required = false)
    String match_str = "";

    @Argument(index = 5, name = "ether_src_str", description = "teste", required = false)
    String ether_src_str = "";


    @Argument(index = 6, name = "ether_dst_str", description = "teste", required = false)
    String ether_dst_str = "";

    @Argument(index = 7, name = "in_port_str", description = "teste", required = false)
    int in_port_str = 1;


    @Argument(index = 8, name = "out_port_str", description = "teste", required = false)
    int out_port_str = 2;


    @Override
    protected void doExecute() {


        System.out.println("EEEE ether_src_str: "+ether_src_str+" dst: "+ether_dst_str);

        FlowRuleInterface flowService = get(FlowRuleInterface.class);





        //GETS
        if(getFlowsBoll == true){
            flowService.getFlowRulesDevice(device_uri_str);
        }else if(getFlowPacketBoll == true){
            flowService.getFlowRulesDevicePacketCount((short) 22, device_uri_str, ether_src_str, ether_dst_str);
            flowService.getFlowRulesDevicePacketCount((short) 23, device_uri_str, ether_src_str, ether_dst_str);
        }




        if(addIntraMBoll == true){

            int pp = 15;
            short appi = 22;

            //sta1 <-> sat4 mesh
            flowService.addFlowMAC("of:0000000000000001",appi,(short) -1,pp,2,5, "AA:BB:CC:DD:00:01","AA:BB:CC:DD:00:04");
            flowService.addFlowMAC("of:0000000000000001",appi,(short) -1,pp,5,2, "AA:BB:CC:DD:00:04","AA:BB:CC:DD:00:01");

            flowService.addFlowMAC("of:0000000000000004",appi,(short) -1,pp,2,3, "AA:BB:CC:DD:00:04","AA:BB:CC:DD:00:01");
            flowService.addFlowMAC("of:0000000000000004",appi,(short) -1,pp,3,2, "AA:BB:CC:DD:00:01","AA:BB:CC:DD:00:04");


            //sta2 <-> sat5 mesh
            flowService.addFlowMAC("of:0000000000000002",appi,(short) -1,pp,2,6, "AA:BB:CC:DD:00:02","AA:BB:CC:DD:00:05");
            flowService.addFlowMAC("of:0000000000000002",appi,(short) -1,pp,6,2, "AA:BB:CC:DD:00:05","AA:BB:CC:DD:00:02");

            flowService.addFlowMAC("of:0000000000000005",appi,(short) -1,pp,2,4, "AA:BB:CC:DD:00:05","AA:BB:CC:DD:00:02");
            flowService.addFlowMAC("of:0000000000000005",appi,(short) -1,pp,4,2, "AA:BB:CC:DD:00:02","AA:BB:CC:DD:00:05");


            //sta3 <-> sat6 mesh
            flowService.addFlowMAC("of:0000000000000003",appi,(short) -1,pp,2,7, "AA:BB:CC:DD:00:03","AA:BB:CC:DD:00:06");
            flowService.addFlowMAC("of:0000000000000003",appi,(short) -1,pp,7,2, "AA:BB:CC:DD:00:06","AA:BB:CC:DD:00:03");

            flowService.addFlowMAC("of:0000000000000006",appi,(short) -1,pp,2,5, "AA:BB:CC:DD:00:06","AA:BB:CC:DD:00:03");
            flowService.addFlowMAC("of:0000000000000006",appi,(short) -1,pp,5,2, "AA:BB:CC:DD:00:03","AA:BB:CC:DD:00:06");


        }

        if(addIntraWBoll == true){

            int pp = 16;
            short appi = 23;

            //sta1 <-> sat4 Wired
            flowService.addFlowMAC("of:0000000000000001",appi,(short) -1,pp,2,1, "AA:BB:CC:DD:00:01","AA:BB:CC:DD:00:04");
            flowService.addFlowMAC("of:0000000000000001",appi,(short) -1,pp,1,2, "AA:BB:CC:DD:00:04","AA:BB:CC:DD:00:01");

            flowService.addFlowMAC("of:0000000000000004",appi,(short) -1,pp,2,1, "AA:BB:CC:DD:00:04","AA:BB:CC:DD:00:01");
            flowService.addFlowMAC("of:0000000000000004",appi,(short) -1,pp,1,2, "AA:BB:CC:DD:00:01","AA:BB:CC:DD:00:04");

            flowService.addFlowMAC("of:0000000000000026",appi,(short) -1,pp,5,2, "AA:BB:CC:DD:00:04","AA:BB:CC:DD:00:01");
            flowService.addFlowMAC("of:0000000000000026",appi,(short) -1,pp,2,5, "AA:BB:CC:DD:00:01","AA:BB:CC:DD:00:04");


            //sta2 <-> sat5 Wired
            flowService.addFlowMAC("of:0000000000000002",appi,(short) -1,pp,2,1, "AA:BB:CC:DD:00:02","AA:BB:CC:DD:00:05");
            flowService.addFlowMAC("of:0000000000000002",appi,(short) -1,pp,1,2, "AA:BB:CC:DD:00:05","AA:BB:CC:DD:00:02");

            flowService.addFlowMAC("of:0000000000000005",appi,(short) -1,pp,2,1, "AA:BB:CC:DD:00:05","AA:BB:CC:DD:00:02");
            flowService.addFlowMAC("of:0000000000000005",appi,(short) -1,pp,1,2, "AA:BB:CC:DD:00:02","AA:BB:CC:DD:00:05");

            flowService.addFlowMAC("of:0000000000000026",appi,(short) -1,pp,6,3, "AA:BB:CC:DD:00:05","AA:BB:CC:DD:00:02");
            flowService.addFlowMAC("of:0000000000000026",appi,(short) -1,pp,3,6, "AA:BB:CC:DD:00:02","AA:BB:CC:DD:00:05");



            //sta3 <-> sat6 Wired
            flowService.addFlowMAC("of:0000000000000003",appi,(short) -1,pp,2,1, "AA:BB:CC:DD:00:03","AA:BB:CC:DD:00:06");
            flowService.addFlowMAC("of:0000000000000003",appi,(short) -1,pp,1,2, "AA:BB:CC:DD:00:06","AA:BB:CC:DD:00:03");

            flowService.addFlowMAC("of:0000000000000006",appi,(short) -1,pp,2,1, "AA:BB:CC:DD:00:06","AA:BB:CC:DD:00:03");
            flowService.addFlowMAC("of:0000000000000006",appi,(short) -1,pp,1,2, "AA:BB:CC:DD:00:03","AA:BB:CC:DD:00:06");

            flowService.addFlowMAC("of:0000000000000026",appi,(short) -1,pp,7,4, "AA:BB:CC:DD:00:06","AA:BB:CC:DD:00:03");
            flowService.addFlowMAC("of:0000000000000026",appi,(short) -1,pp,4,7, "AA:BB:CC:DD:00:03","AA:BB:CC:DD:00:06");


        }





        if(rmRuleBoll==true){
            flowService.removeFlowRulesApp(Short.parseShort(app_id_str));
        }

        if(validArguments() == false){
            //arguments are not ok, program stops
            log.info("ERROR in "+this.getClass().getSimpleName()+" bad input arguments");
            return;
        }




        short appID = Short.parseShort(app_id_str);
        short timeout = Short.parseShort(timeout_str);
        int priority = Integer.parseInt(priority_str);




        if(addDropBoll==true) {

            flowService.addFlowMAC(device_uri_str, appID, timeout, priority, in_port_str, -1, ether_src_str, ether_dst_str);

        }

        if(addArpBoll == true){
            //port 1 in -> port 2
            //flowService.addFlowMACARP(device_uri_str, appID, timeout, priority, 1, 2, ether_src_str, "");
            //flowService.addFlowMACARP(device_uri_str, appID, timeout, priority, 4, 2, "", "");
            flowService.addFlowMACARP(device_uri_str, appID, timeout, priority, in_port_str, out_port_str, "", "");


            //port 2 in -> port 1
            flowService.addFlowMACARP(device_uri_str, appID, timeout, priority, out_port_str, in_port_str, "", "");
        }


        if(addBoll==true) {

            if (match_str.equals("IP")) {

                //port 1 in -> port 2
                flowService.addFlowIP(device_uri_str, appID, timeout, priority, 1, 2, ether_src_str, ether_dst_str);

                //port 2 in -> port 1
                flowService.addFlowIP(device_uri_str, appID, timeout, priority, 2, 1, ether_dst_str, ether_src_str);

            } else if (match_str.equals("MAC")) {

                //port 1 in -> port 2
                flowService.addFlowMAC(device_uri_str, appID, timeout, priority, in_port_str, out_port_str, ether_src_str, ether_dst_str);

                //port 2 in -> port 1
                flowService.addFlowMAC(device_uri_str, appID, timeout, priority, out_port_str, in_port_str, ether_dst_str, ether_src_str);

            }

        }





    }

    
    /** 
     * Verifies if the arguments are valid.
     * @return boolean true if the arguments are valid.
     */
    private boolean validArguments(){
        short appID;
        short timeout;
        int priority;

        //app ID
        try {
            appID = Short.parseShort(app_id_str);
        }catch(Exception e) {
            log.info("appID must be short");
            return false;
        }

        //TimeOut
        try {
            timeout = Short.parseShort(timeout_str);
        }catch(Exception e) {
            log.info("timeout must be short");
            return false;
        }
        if(timeout < -1 || timeout > FlowRule.MAX_TIMEOUT){
            log.info("timeout must be [-1,"+FlowRule.MAX_TIMEOUT+"]");
            return false;
        }



        //Priority
        try {
            priority = Integer.parseInt(priority_str);
        }catch(Exception e) {
            log.info("priority must be short");
            return false;
        }
        if(priority<FlowRule.MIN_PRIORITY || priority > FlowRule.MAX_PRIORITY){
            log.info("priority must be ["+FlowRule.MIN_PRIORITY+","+FlowRule.MAX_PRIORITY+"]");
            return false;
        }

        return true;
    }


}
