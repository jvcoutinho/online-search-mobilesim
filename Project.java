/* Projeto de Sistemas Inteligentes
 * Integrantes:
 *  João Victor
 *  Josenildo Vicente 
 *  Rebeca Oliveira
 *  Renato Ferreira */

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
        robot.move(-distance);
        ArUtil.sleep(3000);
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
        moveLeft(500, robot);
        moveRight(500, robot);
        moveBackwards(500, robot);
        moveForward(500, robot);
        

        // Disconnecting.
        robot.stopRunning();
        robot.disconnect();
    }
}