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
@Command(scope = "onos", name = "slice",
         description = "Handles database slice comunications")

public class AppCommandSlicesDebug extends AbstractShellCommand {
    
    @Option(name = "-l", aliases = {"--list"}, description = "Lists all the slices", required = false, multiValued = false)
    boolean list = false;

    @Option(name = "-q", aliases = {"--query"}, description = "Query the database", required = false, multiValued = false)
    boolean query = false;

    @Option(name = "-t", aliases = {"--link"}, description = "Query the database by link", required = false, multiValued = false)
    boolean link = false;

    @Argument(index = 0, name = "ssl_type", description = "", required = false)
    String ssl_type = null;

    @Override
    protected void doExecute() {
        if (list) {
            DataBaseWriteInterface serviceDB = get(DataBaseWriteInterface.class);
            serviceDB.getSlices();
        }
        else if (query) {
            DataBaseWriteInterface serviceDB = get(DataBaseWriteInterface.class);
            System.out.println(serviceDB.getSliceByType(ssl_type));
        }
        else if (link) {
            DataBaseWriteInterface serviceDB = get(DataBaseWriteInterface.class);
            System.out.println(serviceDB.getLinkBySrcToDst("of:0000000000000001", "of:0000000000000002", 3, 1));
        }
    }
}
