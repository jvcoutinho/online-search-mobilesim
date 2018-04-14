/* Projeto de Sistemas Inteligentes
 * Integrantes:
 *  João Victor
 *  Josenildo Vicente 
 *  Rebeca Oliveira
 *  Renato Ferreira */

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
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
    private static Map<String, Integer> actions;
    
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

        s.setPossibleActions(actions);
            
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

    /* FUNÇÃO HEURÍSTICA (distância entre 2 pontos) */
    private static double heuristic(State s) {
        return Math.sqrt(Math.pow(s.getX() - finalState.getX(), 2) + Math.pow(s.getY() - finalState.getY(), 2));
    }

    /* ALGORITMO LRTA* (busca online) */
    private static State previousState;
    private static String nextAction;

    private static String chooseAction(State currentState, ArSonarDevice sonar) {

        if(goalTest(currentState)) // Estou no estado objetivo.
            return "stop";
        if(currentState.getEstimatedCost() == -1) // Sou um estado novo (ainda não descoberto).
            currentState.setEstimatedCost(heuristic(currentState));

        nextAction = selectLowestCostAction(currentState, sonar);    
        previousState = currentState;

        return "";

    }

    private static String selectLowestCostAction(State s, ArSonarDevice sonar) {

        double cost = 99999999, tmpCost;
        String action = "", tmpAction;

        Iterator<String> i = restrictActions(s, sonar).iterator();        
        while(i.hasNext()) {

            tmpAction = i.next();      
            tmpCost = calculateCost(s, s.getResultState(actions.get(tmpAction)));
            
            if(tmpCost < cost) {
                cost = tmpCost;
                action = tmpAction;
            }
        }

        return action;
    } 

    private static double calculateCost(State s, State nextS) {
        if(nextS == null)
            return heuristic(s);
        return 1 + heuristic(nextS);
    }

    public static void main(String argv[]) {

        // Mapping the actions.
        actions = new HashMap<String, Integer>();
        actions.put("left", 0);
        actions.put("forward", 1);
        actions.put("right", 2);
        actions.put("backwards", 3);

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
        moveBackwards(1, robot);

        finalState = new State(2000, 0);
        nextAction = chooseAction(new State(robot.getX(), robot.getY()), sonar);
        while(nextAction != "stop") {

        }
        
        // Disconnecting.
        robot.stopRunning();
        robot.disconnect();
    }
}