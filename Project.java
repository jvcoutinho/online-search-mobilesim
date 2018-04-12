/* Projeto de Sistemas Inteligentes
 * Integrantes:
 *  João Victor
 *  Josenildo Vicente 
 *  Rebeca Oliveira
 *  Renato Ferreira */

import java.awt.Point;
import java.util.LinkedList;
import com.mobilerobots.Aria.*;

public class Project {

    static {
        try {
            System.loadLibrary("AriaJava");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library libAriaJava failed to load. Make sure that its directory is in your library path; See javaExamples/README.txt and the chapter on Dynamic Linking Problems in the SWIG Java documentation (http://www.swig.org) for help.\n" + e);
            System.exit(1);
        }
    }

    /* ESTADOS */
    private static class State {
        Point coordinates;
        State result[]; // Modelo de transição: 0 - esquerda; 1 - frente; 2 - direita; 3 - trás.
        double estimatedCost; // Função de custo f

        State(double x, double y) {
            coordinates = new Point();
            coordinates.setLocation(x, y);
            result = new State[4];
        }

        public double getX() {
            return coordinates.getX();
        }

        public double getY() {
            return coordinates.getY();
        }
    }

    /* AÇÕES */
    private static void moveLeft(double distance, ArRobot robot) {
        // Gira 90º anti-horário.
        robot.setDeltaHeading(90);
        ArUtil.sleep(8000);

        // Se move.
        robot.move(distance);
        ArUtil.sleep(3000);
    }

    private static void moveRight(double distance, ArRobot robot) {
         // Gira 90º horário.
         robot.setDeltaHeading(-90);
         ArUtil.sleep(8000);
 
         // Se move.
         robot.move(distance);
         ArUtil.sleep(3000);
    }

    private static void moveForward(double distance, ArRobot robot) {
        robot.move(distance);
        ArUtil.sleep(3000);
    }

    private static void moveBackwards(double distance, ArRobot robot) {
        robot.move(-(distance - 50)/2);
        ArUtil.sleep(3000);
    }

    private static LinkedList<String> restrictActions(State s, ArSonarDevice sonar) {
        
        // Checking which directions the robot can take.
        LinkedList<String> actions = new LinkedList<String>();
        
        //System.out.println(sonar.currentReadingPolar(-10, 10));
        if(sonar.currentReadingPolar(-10, 10) > 500) 
            actions.add("forward");
        
        //System.out.println(sonar.currentReadingPolar(70, 110));
        if(sonar.currentReadingPolar(70, 110) > 500)             
            actions.add("left");
       
        //System.out.println(sonar.currentReadingPolar(170, 190));
        if(sonar.currentReadingPolar(170, 190) > 500)  
            actions.add("backwards");
        
        //System.out.println(sonar.currentReadingPolar(250, 290));
        if(sonar.currentReadingPolar(250, 290) > 500) 
            actions.add("right");

        return actions;

        /*Iterator<String> i = actions.iterator();
        while(i.hasNext())
            System.out.print(i.next()+"\t");*/
    }

    /* TESTE DE OBJETIVO */
    private static State finalState;

    private static boolean goalTest(State s) {
        if(Math.abs(finalState.getX() - s.getX()) <= 500 && Math.abs(finalState.getY()- s.getY()) <= 500)
            return true;
        return false;
    }


    public static void main(String argv[]) {

        /* INICIALIZAÇÃO */
		Aria.init();	

		ArRobot robot = new ArRobot();
		ArSimpleConnector conn = new ArSimpleConnector(argv);
		ArSonarDevice sonar = new ArSonarDevice();
	
		// Parsing arguments.
		if(!Aria.parseArgs()) {
			Aria.logOptions();
			System.exit(1);
		}

		// Connecting to the robot.
		if(!conn.connectRobot(robot)) {
			ArLog.log(ArLog.LogLevel.Terse, "actionExample: Could not connect to robot! Exiting.");
      		System.exit(2);
        }

        // Adding range devices & enabling motors.
		robot.addRangeDevice(sonar);
        robot.enableMotors();

        /* EXECUÇÃO */
        robot.runAsync(true);
        
        // Disconnecting.
        robot.stopRunning();
        robot.disconnect();
    }
}