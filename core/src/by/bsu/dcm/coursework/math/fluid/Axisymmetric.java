package by.bsu.dcm.coursework.math.fluid;

import by.bsu.dcm.coursework.math.Function;
import by.bsu.dcm.coursework.math.RightSweepExceprion;
import by.bsu.dcm.coursework.math.Util;

public class Axisymmetric extends EquilibriumFluid {
    private static class Integrand implements Function {
        private double[] approx;
        private double[] nodes;

        @Override
        public double calc(int index) {
            return approx[index] * nodes[index];
        }

        @Override
        public int getLength() {
            return nodes.length;
        }

        @Override
        public void setParams(Object... params) {
            this.approx = (double[]) params[0];
            this.nodes = (double[]) params[1];
        }
    }

    private double[] coefs;

    public Axisymmetric() {
        super(new Integrand());
    }

    private void calcCoefs(double step) {
        double halfStep = step / 2.0;

        coefs[0] = 0.0;

        for (int i = 1; i < coefs.length; i++) {
            coefs[i] = (nodes[i] - halfStep) / Math.sqrt(1.0 + Math.pow((prevApprox[i] - prevApprox[i - 1]) / step, 2.0));
        }
    }

    @Override
    protected void calcNextApproximation(double step, ProblemParams params) throws RightSweepExceprion {
        double integral;
        double q;

        if (coefs == null || coefs.length != params.splitNum + 1) {
            coefs = new double[params.splitNum + 1];
        }

        calcCoefs(step);

        for (int i = 0; i < coefsMtr.length; i++) {
            for (int j = 0; j < coefsMtr[i].length; j++) {
                coefsMtr[i][i] = 0.0;
            }
            rightVect[i] = 0.0;
        }

        integrand.setParams(prevApprox, nodes);

        integral = 2.0 * Math.PI * Util.calcIntegralTrapeze(integrand, step);
        q = -2.0 * Math.sin(params.alpha) - params.bond * Math.pow(integral, 1.0 / 3.0) / Math.PI;

        coefsMtr[0][0] = -(1.0 / step + (step / 4.0) * (params.bond / Math.pow(integral, 2.0 / 3.0)));
        coefsMtr[0][1] = 1.0 / step;
        coefsMtr[params.splitNum][params.splitNum] = 1.0;

        rightVect[0] = step * q / 4.0;
        rightVect[params.splitNum] = 0.0;

        for (int i = 1; i < params.splitNum; i++) {
            coefsMtr[i][i - 1] = coefs[i];
            coefsMtr[i][i] = -(coefs[i] + coefs[i + 1]);
            coefsMtr[i][i + 1] = coefs[i + 1];

            rightVect[i] = nodes[i] * step * step * (params.bond * prevApprox[i] / Math.pow(integral, 2.0 / 3.0) + q);
        }

        Util.calcRightSweep(coefsMtr, rightVect, nextApprox);
    }
}
