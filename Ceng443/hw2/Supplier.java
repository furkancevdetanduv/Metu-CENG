package project.parts.logics;

import project.SimulationRunner;
import project.components.Factory;
import project.components.ProductionLine;
import project.components.Robot;
import project.parts.Arm;
import project.parts.Base;
import project.parts.Part;
import project.parts.payloads.Camera;
import project.parts.payloads.Gripper;
import project.parts.payloads.MaintenanceKit;
import project.parts.payloads.Welder;
import project.utility.Common;

import java.util.List;

public class Supplier extends Logic
{
    @Override public void run ( Robot robot )
    {
        // TODO
        // Following messages are appropriate for this class
        // System.out.printf( "Robot %02d : Supplying a random part on production line.%n", ...);
        // System.out.printf( "Robot %02d : Production line is full, removing a random part from production line.%n", ...);
        // System.out.printf( "Robot %02d : Waking up waiting builders.%n", ...);
        //get productionLine
        ProductionLine productionLine = (ProductionLine) Common.get(SimulationRunner.factory,"productionLine");
        List<Part> parts = (List<Part>) Common.get(productionLine,"parts");
        // if production line is full then remove a random part
        if((int) Common.get(productionLine,"maxCapacity") == parts.size() ){
            System.out.printf( "Robot %02d : Production line is full, removing a random part from production line.%n", Common.get(robot,"serialNo"));
            synchronized (parts){
                int randToRemove = Common.random.nextInt((int) Common.get(productionLine,"maxCapacity"));
                parts.remove(randToRemove);
            }
            SimulationRunner.productionLineDisplay.repaint() ;
        }
        // if productionLine is not full add new part
        else{
            System.out.printf( "Robot %02d : Supplying a random part on production line.%n",Common.get(robot,"serialNo"));
            synchronized (parts){
                int randToCreatePart = Common.random.nextInt(4);
                if(randToCreatePart == 0){
                    parts.add(new Arm());
                }
                else if(randToCreatePart ==1){
                    parts.add(Factory.createBase());
                }
                else if(randToCreatePart==2){
                    int randToCreatePayload = Common.random.nextInt(4);
                    if (randToCreatePayload == 0){
                        parts.add(new Camera());
                    }
                    else if(randToCreatePayload == 1){
                        parts.add(new Gripper());
                    }
                    else if (randToCreatePayload == 2){
                        parts.add(new MaintenanceKit());
                    }
                    else if(randToCreatePayload == 3){
                        parts.add(new Welder());
                    }
                }
                else if(randToCreatePart==3){
                    int randToCreateLogic = Common.random.nextInt(4);
                    if (randToCreateLogic == 0){
                        parts.add(new Builder());
                    }
                    else if(randToCreateLogic == 1){
                        parts.add(new Fixer());
                    }
                    else if (randToCreateLogic == 2){
                        parts.add(new Inspector());
                    }
                    else if(randToCreateLogic == 3){
                        parts.add(new Supplier());
                    }
                }
            }
            SimulationRunner.productionLineDisplay.repaint() ;
        }
        // notify Builders
        System.out.printf( "Robot %02d : Waking up waiting builders.%n",Common.get(robot,"serialNo"));
        List<Robot> robots = (List<Robot>) Common.get(SimulationRunner.factory,"robots");
        for(Robot r : robots){
            synchronized (r){
                if(Common.get(r,"logic") instanceof Builder){
                    r.notifyAll();
                }
            }
        }
    }
}