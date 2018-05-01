package by.bsu.dcm.coursework.math.fluid;

import by.bsu.dcm.coursework.math.Function;
import by.bsu.dcm.coursework.math.Util;

public class Plain extends EquilibriumFluid {
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

    public Plain() {
        super(new Integrand());
    }

    @Override
    protected void calcNextApproximation(double step, ProblemParams params) {
        double coef = 8.0 * step;
        double integral;

        for (int i = 0; i < coefsMtr.length; i++) {
            for (int j = 0; j < coefsMtr[i].length; j++) {
                coefsMtr[i][i] = 0.0;
            }
            rightVect[i] = 0.0;
        }

        integrand.setParams(prevApprox);

        integral = 2.0 * Util.calcIntegralTrapeze(integrand, step);

        coefsMtr[0][0] = -(1.0 - (params.bond * step * step) / (2.0 * integral));
        coefsMtr[0][1] = 1.0;
        coefsMtr[params.splitNum][params.splitNum] = 1.0;

        rightVect[0] = (step * step / 2.0) * (-params.bond / 2.0 - Math.sin(params.alpha));
        rightVect[params.splitNum] = 0.0;

        for (int i = 1; i < params.splitNum; i++) {
            coefsMtr[i][i - 1] = coef;
            coefsMtr[i][i] = -2.0 * coef;
            coefsMtr[i][i + 1] = coef;

            rightVect[i] = Math.pow(4.0 * step * step + Math.pow(prevApprox[i + 1] - prevApprox[i - 1], 2.0), 3.0 / 2.0) *
                    (params.bond * prevApprox[i] / integral - params.bond / 2.0 - Math.sin(params.alpha));
        }

        Util.calcRightSweep(coefsMtr, rightVect, nextApprox);
    }
}