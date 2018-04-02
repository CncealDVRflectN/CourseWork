package by.bsu.dcm.coursework.math;

import by.bsu.dcm.coursework.graphs.GraphPoints;
import com.badlogic.gdx.math.Vector2;

public class Flat {
    private class Integrand implements Function {
        private double[] approx;

        @Override
        public double calc(int index) {
            return approx[index];
        }

        @Override
        public int getLength() {
            return approx.length;
        }

        public void setApprox(double[] approx) {
            this.approx = approx;
        }
    }

    private final Integrand integrand;

    private double epsilon;
    private double bond;
    private double alpha;
    private int splitNum;

    private GraphPoints graphPoints;

    public Flat(double alpha, double bond, double epsilon, int splitNum){
        integrand = new Integrand();
        graphPoints = new GraphPoints();

        this.alpha = alpha;
        this.bond = bond;
        this.epsilon = epsilon;
        this.splitNum = splitNum;
    }

    private double[] calcNextApproximation(double[] prevApprox, double step){
        double[][] leftPart = new double[splitNum + 1][splitNum + 1];
        double[] rightPart = new double[splitNum + 1];
        double coef = 8.0 * step;
        double integral;

        integrand.setApprox(prevApprox);

        integral = 2.0 * Util.calcIntegralTrapeze(integrand, step);

        leftPart[0][0] = -(1.0 - (bond * step * step) / (2.0 * integral));
        leftPart[0][1] = 1.0;
        leftPart[splitNum][splitNum] = 1.0;

        rightPart[0] = (step * step / 2.0) * (-bond / 2.0 - Math.sin(alpha));
        rightPart[splitNum] = 0;

        for (int i = 1; i < splitNum; i++) {
            leftPart[i][i - 1] = coef;
            leftPart[i][i] = -2.0 * coef;
            leftPart[i][i + 1] = coef;

            rightPart[i] = Math.pow(4.0 * step * step + Math.pow(prevApprox[i + 1] - prevApprox[i - 1], 2.0) , 3.0 / 2.0) *
                    (bond * prevApprox[i] / integral - bond / 2.0 - Math.sin(alpha));
        }

        return Util.calcRightSweep(leftPart, rightPart);
    }

    private double[] calcInitialValues(double[] nodes) {
        double[] init = new double[splitNum + 1];

        for (int i = 0; i < init.length; i++) {
            init[i] = Math.sqrt(1.0 - Math.pow(nodes[i], 2.0));
        }

        return init;
    }

    public void calcResult() {
        Vector2[] points = new Vector2[splitNum + 1];
        double[] nodes = new double[splitNum + 1];
        double[] prevApprox;
        double[] nextApprox;
        double step = 1.0 / splitNum;
        int iterations = 0;

        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = i * step;
        }

        nextApprox = calcInitialValues(nodes);

        do {
            prevApprox = nextApprox;
            nextApprox = calcNextApproximation(prevApprox, step);
            iterations++;
        } while(Util.norm(nextApprox, prevApprox) >= epsilon);

        for (int i = 0; i < points.length; i++) {
            points[i] = new Vector2((float)nodes[i], (float) nextApprox[i]);
        }
        graphPoints.points = points;

        System.out.println("Flat iterations result: " + iterations);
    }

    public GraphPoints getGraphPoints() {
        return graphPoints;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    public void setBond(double bond) {
        this.bond = bond;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public void setSplitNum(int num) {
        splitNum = num;
    }
}