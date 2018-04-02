package by.bsu.dcm.coursework.math;

import by.bsu.dcm.coursework.graphs.GraphPoints;
import com.badlogic.gdx.math.Vector2;

public class Axisymmetric {
    public class Integrand implements Function {
        private double[] prev;
        private double[] nodes;

        @Override
        public double calc(int index) {
            return prev[index] * nodes[index];
        }

        @Override
        public int getLength() {
            return nodes.length;
        }

        public void setPrev(double[] prev) {
            this.prev = prev;
        }

        public void setNodes(double[] nodes) {
            this.nodes = nodes;
        }
    }

    private final Integrand integrand;

    private double epsilon;
    private double bond;
    private double alpha;
    private int nodesNum;

    private GraphPoints graphPoints;

    public Axisymmetric() {
        integrand = new Integrand();
        graphPoints = new GraphPoints();
    }

    private double[] calcCoefs(double[] prev, double[] nodes, double step) {
        double[] coefs = new double[nodesNum];
        double halfStep = step / 2.0;

        coefs[0] = 0.0;

        for (int i = 1; i < coefs.length; i++) {
            coefs[i] = (nodes[i] - halfStep) / Math.sqrt(1.0 + Math.pow((prev[i] - prev[i - 1]) / step, 2.0));
        }

        return coefs;
    }

    private double[] calcNext(double[] prev, double[] nodes, double step) {
        double[][] mtr = new double[nodesNum][nodesNum];
        double[] vect = new double[nodesNum];
        double[] coefs = calcCoefs(prev, nodes, step);
        double integral;
        double q;

        integrand.setPrev(prev);
        integrand.setNodes(nodes);

        integral = 2.0 * Math.PI * Util.calcIntegralTrapeze(integrand, step);
        q = -2.0 * Math.sin(alpha) - bond * Math.pow(integral, 1.0 / 3.0) / Math.PI;

        mtr[0][0] = -(1.0 / step + (step / 4.0) * (bond / Math.pow(integral, 2.0 / 3.0)));
        mtr[0][1] = 1.0 / step;
        mtr[nodesNum - 1][nodesNum - 1] = 1.0;
        vect[0] = step * q / 4.0;
        vect[nodesNum - 1] = 0.0;

        for (int i = 1; i < nodesNum - 1; i++) {
            mtr[i][i - 1] = coefs[i];
            mtr[i][i] = -(coefs[i] + coefs[i + 1]);
            mtr[i][i + 1] = coefs[i + 1];
            vect[i] = nodes[i] * Math.pow(step, 2.0) * (bond * prev[i] / Math.pow(integral, 2.0 / 3.0) + q);
        }

        return Util.calcRightSweep(mtr, vect);
    }

    private double[] calcInitialValues(double[] nodes) {
        double[] init = new double[nodesNum];

        for (int i = 0; i < nodesNum; i++) {
            init[i] = Math.sqrt(1.0 - Math.pow(nodes[i], 2.0));
        }

        return init;
    }

    public double[] calcResult() {
        Vector2[] points = new Vector2[nodesNum];
        double[] nodes = new double[nodesNum];
        double[] prev;
        double[] next;
        double step = 1.0 / (nodesNum - 1);
        int interations = 0;

        for (int i = 0; i < nodesNum; i++) {
            nodes[i] = i * step;
        }

        next = calcInitialValues(nodes);

        do {
            prev = next;
            next = calcNext(prev, nodes, step);
            interations++;
        } while (Util.norm(next, prev) > epsilon);

        for (int i = 0; i < nodesNum; i++) {
            points[i] = new Vector2((float)nodes[i], (float)next[i]);
        }
        graphPoints.points = points;

        System.out.println(interations);

        return next;
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

    public void setNodesNum(int num) {
        nodesNum = num;
    }
}
