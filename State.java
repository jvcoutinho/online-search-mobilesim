import java.awt.Point;

/* ESTADOS */
public class State {
    private Point coordinates;
    private State result[]; // Modelo de transição: 0 - esquerda; 1 - frente; 2 - direita; 3 - trás.
    private double estimatedCost; // Função de custo f

    State(double x, double y) {
        coordinates = new Point();
        coordinates.setLocation(x, y);
        result = new State[4];
        estimatedCost = -1;
    }

    public double getX() {
        return coordinates.getX();
    }

    public double getY() {
        return coordinates.getY();
    }

    public State getResultState(int action) {
        return result[action];
    }

    public void setResultState(State s, int action) {
        result[action] = s;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(double cost) {
        estimatedCost = cost;
    }
}