package project.parts.logics;

import project.SimulationRunner;
import project.components.Factory;
import project.components.Robot;
import project.utility.Common;

import java.util.List;

public class Inspector extends Logic
{
    @Override public void run ( Robot robot )
    {
        // TODO
        // Following messages are appropriate for this class
        // System.out.printf( "Robot %02d : Detected a broken robot (%02d), adding it to broken robots list.%n", ...);
        // System.out.printf( "Robot %02d : Notifying waiting fixers.%n", ...);
        // Get robot list
        List<Robot> robots = (List<Robot>) Common.get(SimulationRunner.factory,"robots");
        // Get broken robot list
        List<Robot> brokenRobots = (List<Robot>) Common.get(SimulationRunner.factory,"brokenRobots");
        for (Robot r : robots){
            // Check if a robot has a broken part
            if((Common.get(r,"arm") == null ||Common.get(r,"payload") == null || Common.get(r,"logic") == null) && !brokenRobots.contains(r)){
                // If a robot has a broken part add it to the brokenRobots list
                synchronized ( brokenRobots   )  { brokenRobots .add(r) ; }
                SimulationRunner.robotsDisplay.repaint() ;
                synchronized ( System.out ) {
                    System.out.printf("Robot %02d : Detected a broken robot (%02d), adding it to broken robots list.%n", Common.get(robot, "serialNo"), Common.get(r, "serialNo"));
                }
                // Notify fixers to fix it
                for(Robot r2 : robots){
                    synchronized (r2){
                        if(Common.get(r2,"logic") instanceof Fixer){
                            r2.notifyAll();
                        }
                    }
                }
                synchronized ( System.out ) {
                    System.out.printf("Robot %02d : Notifying waiting fixers.%n", Common.get(robot, "serialNo"), Common.get(r, "serialNo"));
                }
            }
        }
    }
}