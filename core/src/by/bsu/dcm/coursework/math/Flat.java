package by.bsu.dcm.coursework.math;

import com.badlogic.gdx.math.Vector2;

public class Flat extends EquilibriumFluid {
    private static class Integrand implements Function {
        private double[] approx;

        @Override
        public double calc(int index) {
            return approx[index];
        }

        @Override
        public int getLength() {
            return approx.length;
        }

        @Override
        public void setParams(Object... params) {
            this.approx = (double[]) params[0];
        }
    }

    public Flat() {
        this(0.0, 0.0, 0.0, 0);
    }

    public Flat(double alpha, double bond, double epsilon, int splitNum) {
        super(new Integrand(), alpha, bond, epsilon, splitNum);
    }

    private double[] calcNextApproximation(double[] prevApprox, double step) {
        double[][] leftPart = new double[splitNum + 1][splitNum + 1];
        double[] rightPart = new double[splitNum + 1];
        double coef = 8.0 * step;
        double integral;

        integrand.setParams(prevApprox);

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

            rightPart[i] = Math.pow(4.0 * step * step + Math.pow(prevApprox[i + 1] - prevApprox[i - 1], 2.0), 3.0 / 2.0) *
                    (bond * prevApprox[i] / integral - bond / 2.0 - Math.sin(alpha));
        }

        return Util.calcRightSweep(leftPart, rightPart);
    }

    private double[] calcInitialApproximation(double[] nodes) {
        double[] init = new double[splitNum + 1];

        for (int i = 0; i < init.length; i++) {
            init[i] = Math.sqrt(1.0 - Math.pow(nodes[i], 2.0));
        }

        return init;
    }

    @Override
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

        nextApprox = calcInitialApproximation(nodes);

        do {
            prevApprox = nextApprox;
            nextApprox = calcNextApproximation(prevApprox, step);
            iterations++;
        } while (Util.norm(nextApprox, prevApprox) >= epsilon);

        for (int i = 0; i < points.length; i++) {
            points[i] = new Vector2((float) nodes[i], (float) nextApprox[i]);
        }
        graphPoints.points = points;

        System.out.println("Flat iterations result: " + iterations);
    }
}