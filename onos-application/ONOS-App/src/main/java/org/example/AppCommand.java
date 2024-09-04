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

import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.onosproject.cli.AbstractShellCommand;
import org.uc.dei.mei.framework.SomeInterface;

/**
 * Apache Karaf CLI commands.
 */
@Service
@Command(scope = "onos", name = "sample",
         description = "Prints the current topology nodes specification")

public class AppCommand extends AbstractShellCommand {



    @Option(name="-hosts",description = "returns information of hosts in topology ") boolean hostBoll;
    @Option(name="-links",description = "returns information of links in topology") boolean linkBoll;
    @Option(name="-flows", description = "returns information of flowTables of devices in topology") boolean flowBoll;
    @Option(name="-devices", description = "returns information of the devices in the topology") boolean deviceBoll;


    @Option(name="-sw1", description = "") boolean sw1;
    @Option(name="-sw2", description = "") boolean sw2;
    @Option(name="-sw3", description = "") boolean sw3;

    @Override
    protected void doExecute() {


        SomeInterface service = get(SomeInterface.class);

        if(hostBoll == true){
            log.info(service.getTopologyHosts());
        }else if(linkBoll == true){
            log.info(service.getTopologyLinks());
        }else if(flowBoll == true) {
            log.info(service.getTopologyFlow());
        }else if(deviceBoll == true) {
            log.info(service.getTopologyDevices());

        }else if(sw1 == true) {
            print(service.testar1());
        }else if(sw2 == true) {
            print(service.testar2());
        }else if(sw3 == true) {

        }else{
            print(service.getTopologyElements());
            print(service.testar());
        }
    }

}
