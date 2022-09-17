package project.parts.logics;

import project.SimulationRunner;
import project.components.Robot;
import project.parts.payloads.Camera;
import project.parts.payloads.Gripper;
import project.parts.payloads.MaintenanceKit;
import project.parts.payloads.Welder;
import project.utility.Common;
import java.util.List;

public class Fixer extends Logic
{
    @Override public void run ( Robot robot )
    {
        // TODO
        // Following messages are appropriate for this class
        // System.out.printf("Robot %02d : Fixed and waken up robot (%02d).%n", ...);
        // System.out.printf("Robot %02d : Nothing to fix, waiting!%n", ...);
        // System.out.printf("Robot %02d : Fixer woke up, going back to work.%n", ...);
        // get brokenRobot list
        List<Robot> brokenRobots = (List<Robot>) Common.get(SimulationRunner.factory,"brokenRobots");
        synchronized (robot){
            // while there is no robot to fix wait
            while (brokenRobots.isEmpty()){
                try{
                    System.out.printf("Robot %02d : Nothing to fix, waiting!%n", Common.get(robot,"serialNo"));
                    robot.wait() ;
                }
                catch ( InterruptedException ex ) { /* Do nothing */ }

                if ( SimulationRunner.factory.stopProduction == true )  { break ; }
            }
        }
        synchronized (System.out){ System.out.printf("Robot %02d : Fixer woke up, going back to work.%n", Common.get(robot,"serialNo"));}
        Robot brokenRobot;
        // If there is a robot to fix get it and remove it from the list else return
        synchronized (brokenRobots) {
            if (brokenRobots.size() > 0) {
                brokenRobot = brokenRobots.get(0);
                brokenRobots.remove(0);
            }
            else return;
        }
        synchronized (brokenRobot){
            //if broken part is arm create new arm and attach it to the robot and notify it
            if(Common.get(brokenRobot, "arm") == null){
                Common.set(brokenRobot,"arm",Common.factoryPart("Arm"));
                SimulationRunner.robotsDisplay.repaint();
                System.out.printf("Robot %02d : Fixed and waken up robot (%02d).%n",Common.get(robot,"serialNo"),Common.get(brokenRobot,"serialNo"));
                brokenRobot.notifyAll();
            }
            //if broken part is payload create new appropriate payload for its logic and attach it to the robot and notify it
            else if(Common.get(brokenRobot,"payload") == null){
                if(Common.get(brokenRobot,"logic") instanceof Builder ){
                    Common.set( brokenRobot , "payload" , Common.factoryPart( "Welder"         ) ) ;
                    SimulationRunner.robotsDisplay.repaint();
                    System.out.printf("Robot %02d : Fixed and waken up robot (%02d).%n",Common.get(robot,"serialNo"),Common.get(brokenRobot,"serialNo"));
                    brokenRobot.notifyAll();
                }
                else if(Common.get(brokenRobot,"logic") instanceof Fixer){
                    Common.set( brokenRobot , "payload" , Common.factoryPart( "MaintenanceKit"         ) ) ;
                    SimulationRunner.robotsDisplay.repaint();
                    System.out.printf("Robot %02d : Fixed and waken up robot (%02d).%n",Common.get(robot,"serialNo"),Common.get(brokenRobot,"serialNo"));
                    brokenRobot.notifyAll();
                }
                else if(Common.get(brokenRobot,"logic") instanceof Supplier ){
                    Common.set( brokenRobot , "payload" , Common.factoryPart( "Gripper"         ) ) ;
                    SimulationRunner.robotsDisplay.repaint();
                    System.out.printf("Robot %02d : Fixed and waken up robot (%02d).%n",Common.get(robot,"serialNo"),Common.get(brokenRobot,"serialNo"));
                    brokenRobot.notifyAll();
                }
                else if(Common.get(brokenRobot,"logic") instanceof Inspector ){
                    Common.set( brokenRobot , "payload" , Common.factoryPart( "Camera"         ) ) ;
                    SimulationRunner.robotsDisplay.repaint();
                    System.out.printf("Robot %02d : Fixed and waken up robot (%02d).%n",Common.get(robot,"serialNo"),Common.get(brokenRobot,"serialNo"));
                    brokenRobot.notifyAll();
                }
            }
            //if broken part is logic create new appropriate logic for its payload and attach it to the robot and notify it
            else if(Common.get(brokenRobot,"logic") == null){
                if(Common.get(brokenRobot,"payload") instanceof Welder){
                    Common.set( brokenRobot , "logic" , Common.factoryPart( "Builder"         ) ) ;
                    SimulationRunner.robotsDisplay.repaint();
                    System.out.printf("Robot %02d : Fixed and waken up robot (%02d).%n",Common.get(robot,"serialNo"),Common.get(brokenRobot,"serialNo"));
                    brokenRobot.notifyAll();
                }
                else if(Common.get(brokenRobot,"payload") instanceof MaintenanceKit ){
                    Common.set( brokenRobot , "logic" , Common.factoryPart( "Fixer"         ) ) ;
                    SimulationRunner.robotsDisplay.repaint();
                    System.out.printf("Robot %02d : Fixed and waken up robot (%02d).%n",Common.get(robot,"serialNo"),Common.get(brokenRobot,"serialNo"));
                    brokenRobot.notifyAll();
                }
                else if(Common.get(brokenRobot,"payload") instanceof Gripper){
                    Common.set( brokenRobot , "logic" , Common.factoryPart( "Supplier"         ) ) ;
                    SimulationRunner.robotsDisplay.repaint();
                    System.out.printf("Robot %02d : Fixed and waken up robot (%02d).%n",Common.get(robot,"serialNo"),Common.get(brokenRobot,"serialNo"));
                    brokenRobot.notifyAll();
                }
                else if(Common.get(brokenRobot,"payload") instanceof Camera ){
                    Common.set( brokenRobot , "logic" , Common.factoryPart( "Inspector"         ) ) ;
                    SimulationRunner.robotsDisplay.repaint();
                    System.out.printf("Robot %02d : Fixed and waken up robot (%02d).%n",Common.get(robot,"serialNo"),Common.get(brokenRobot,"serialNo"));
                    brokenRobot.notifyAll();
                }
            }
        }
    }
}