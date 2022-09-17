package project.parts.logics;

import project.SimulationRunner;
import project.components.ProductionLine;
import project.components.Robot;
import project.components.Storage;
import project.parts.Arm;
import project.parts.Base;
import project.parts.Part;
import project.parts.payloads.*;
import project.utility.Common;

import java.util.List;

public class Builder extends Logic
{
    @Override public void run ( Robot robot )
    {
        // TODO
        // Following messages are appropriate for this class
        // System.out.printf("Robot %02d : Builder cannot build anything, waiting!%n", ...);
        // System.out.printf("Robot %02d : Builder woke up, going back to work.%n", ...);
        // System.out.printf("Robot %02d : Builder attached some parts or relocated a completed robot.%n", ...);
        // getting parts from productionLine
        ProductionLine productionLine = (ProductionLine) Common.get(SimulationRunner.factory,"productionLine");
        List<Part> parts = (List<Part>) Common.get(productionLine,"parts");
        // if builder has nothing to do set cantWork true and wait for supplier
        boolean cantWork = false;
        synchronized (parts){
            int baseExist = -1;
            int baseWithArmExist = -1;
            int baseWithArmAndPayloadExist = -1 ;
            int readyRobotExist = -1;
            int armExist = -1;
            int payloadExist = -1;
            int builderExist = -1;
            int fixerExist = -1;
            int inspectorExist = -1;
            int supplierExist = -1;
            // traverse the parts and set the index of parts to be attached
            for (int j = 0 ; j < parts.size(); j++){
                if(parts.get(j) instanceof Base && Common.get(parts.get(j),"arm") == null){
                    baseExist = j;
                }
                else if(parts.get(j) instanceof Base && Common.get(parts.get(j),"arm") != null && Common.get(parts.get(j),"payload") == null){
                    baseWithArmExist = j;
                }
                else if(parts.get(j) instanceof Base && Common.get(parts.get(j),"arm") != null && Common.get(parts.get(j),"payload") != null && Common.get(parts.get(j),"logic") == null){
                    baseWithArmAndPayloadExist = j;
                }
                else if(parts.get(j) instanceof Base && Common.get(parts.get(j),"arm") != null && Common.get(parts.get(j),"payload") != null && Common.get(parts.get(j),"logic") != null){
                    readyRobotExist = j;
                }
                else if(parts.get(j) instanceof Arm){
                    armExist = j;
                }
                else if(parts.get(j) instanceof Payload){
                    payloadExist = j;
                }
                else if(parts.get(j) instanceof Builder){
                    builderExist = j;
                }
                else if(parts.get(j) instanceof Fixer){
                    fixerExist = j;
                }
                else if(parts.get(j) instanceof Inspector){
                    inspectorExist = j;
                }
                else if(parts.get(j) instanceof Supplier){
                    supplierExist = j;
                }
            }
            //attach base and arm
            if (armExist != -1 && baseExist !=-1){
                Common.set(parts.get(baseExist),"arm",new Arm());
                parts.remove(armExist);
                System.out.printf("Robot %02d : Builder attached some parts or relocated a completed robot.%n", Common.get(robot,"serialNo"));
                SimulationRunner.productionLineDisplay.repaint() ;
            }
            // attach payload to base and arm
            else if(baseWithArmExist != -1 && payloadExist != -1){
                if (parts.get(payloadExist) instanceof Camera){
                    Common.set(parts.get(baseWithArmExist),"payload",new Camera());
                    parts.remove(payloadExist);
                    System.out.printf("Robot %02d : Builder attached some parts or relocated a completed robot.%n", Common.get(robot,"serialNo"));
                }
                else if (parts.get(payloadExist) instanceof Gripper){
                    Common.set(parts.get(baseWithArmExist),"payload",new Gripper());
                    parts.remove(payloadExist);
                    System.out.printf("Robot %02d : Builder attached some parts or relocated a completed robot.%n", Common.get(robot,"serialNo"));
                }
                else if (parts.get(payloadExist) instanceof MaintenanceKit){
                    Common.set(parts.get(baseWithArmExist),"payload",new MaintenanceKit());
                    parts.remove(payloadExist);
                    System.out.printf("Robot %02d : Builder attached some parts or relocated a completed robot.%n", Common.get(robot,"serialNo"));
                }
                else if (parts.get(payloadExist) instanceof Welder){
                    Common.set(parts.get(baseWithArmExist),"payload",new Welder());
                    parts.remove(payloadExist);
                    System.out.printf("Robot %02d : Builder attached some parts or relocated a completed robot.%n", Common.get(robot,"serialNo"));
                }
                SimulationRunner.productionLineDisplay.repaint() ;
            }
            //attach logic to base,arm and payload with respect to payload
            else if (baseWithArmAndPayloadExist != -1){
                if(Common.get(parts.get(baseWithArmAndPayloadExist),"payload") instanceof Camera && inspectorExist != -1 ){
                    Common.set(parts.get(baseWithArmAndPayloadExist),"logic",new Inspector());
                    parts.remove(inspectorExist);
                    System.out.printf("Robot %02d : Builder attached some parts or relocated a completed robot.%n", Common.get(robot,"serialNo"));
                }
                else if(Common.get(parts.get(baseWithArmAndPayloadExist),"payload") instanceof Gripper && supplierExist != -1 ){
                    Common.set(parts.get(baseWithArmAndPayloadExist),"logic",new Supplier());
                    parts.remove(supplierExist);
                    System.out.printf("Robot %02d : Builder attached some parts or relocated a completed robot.%n", Common.get(robot,"serialNo"));
                }
                else if(Common.get(parts.get(baseWithArmAndPayloadExist),"payload") instanceof MaintenanceKit && fixerExist != -1 ){
                    Common.set(parts.get(baseWithArmAndPayloadExist),"logic",new Fixer());
                    parts.remove(fixerExist);
                    System.out.printf("Robot %02d : Builder attached some parts or relocated a completed robot.%n", Common.get(robot,"serialNo"));
                }
                else if(Common.get(parts.get(baseWithArmAndPayloadExist),"payload") instanceof Welder && builderExist != -1 ){
                    Common.set(parts.get(baseWithArmAndPayloadExist),"logic",new Builder());
                    parts.remove(builderExist);
                    System.out.printf("Robot %02d : Builder attached some parts or relocated a completed robot.%n", Common.get(robot,"serialNo"));
                }
                SimulationRunner.productionLineDisplay.repaint() ;
            }
            // take complete robot and if robot list empty put it robot list
            // else if storage not full put it storage and check storage size if it reached the maximum capacity stop production
            else if(readyRobotExist != -1){
                int maxRobots = (int)Common.get(SimulationRunner.factory,"maxRobots");
                List<Robot> robots = (List<Robot>)Common.get(SimulationRunner.factory,"robots");
                int lenRobots = robots.size();
                Storage storage = (Storage) Common.get(SimulationRunner.factory,"storage");
                int maxCapacity = (int)Common.get(storage,"maxCapacity");
                List<Robot>  robotsOfStorage = (List<Robot>)Common.get(storage,"robots");
                int lenStorageRobots = robotsOfStorage.size();
                if(lenRobots < maxRobots){
                    robots.add((Robot) parts.get(readyRobotExist));
                    Common.set(SimulationRunner.factory,"robots",robots);
                    new Thread( (Robot) parts.get(readyRobotExist) ).start() ;
                    parts.remove(readyRobotExist);
                    SimulationRunner.productionLineDisplay.repaint() ;
                    System.out.printf("Robot %02d : Builder attached some parts or relocated a completed robot.%n", Common.get(robot,"serialNo"));
                    SimulationRunner.robotsDisplay.repaint() ;
                }
                else if(lenStorageRobots < maxCapacity){
                    robotsOfStorage.add((Robot) parts.get(readyRobotExist));
                    parts.remove(readyRobotExist);
                    SimulationRunner.productionLineDisplay.repaint();
                    SimulationRunner.storageDisplay.repaint();
                    System.out.printf("Robot %02d : Builder attached some parts or relocated a completed robot.%n", Common.get(robot,"serialNo"));
                    if(lenStorageRobots + 1 == maxCapacity){
                        SimulationRunner.factory.initiateStop();
                    }
                }
            }
            //there is nothing to do
            else{
                cantWork = true;
            }

        }
        // there is nothing to do wait for supplier
        if (cantWork){
            System.out.printf("Robot %02d : Builder cannot build anything, waiting!%n", Common.get(robot,"serialNo"));
            synchronized (robot){
                while (parts.size() == 0){
                    try{
                        robot.wait() ;
                    }
                    catch ( InterruptedException ex ) { /* Do nothing */ }
                }
            }
            System.out.printf("Robot %02d : Builder woke up, going back to work.%n", Common.get(robot,"serialNo"));
        }
    }
}
