package by.bsu.dcm.coursework.math.fluid;

public class Axisymmetric extends EquilibriumFluid {
    private double[] coefs;

    private void calcCoefs(double step) {
        double halfStep = step / 2.0;

        coefs[0] = 0.0;

        for (int i = 1; i < coefs.length; i++) {
            coefs[i] = (nodes[i] - halfStep) / Math.sqrt(1.0 + Math.pow((prevApprox[i] - prevApprox[i - 1]) / step, 2.0));
        }
    }

    private double calcIntegralTrapeze(double[] values, double[] nodes, double step) {
        double result = 0.0;
        double length = values.length - 1;

        for (int i = 1; i < length; i++) {
            result += values[i] * nodes[i];
        }

        return 2.0 * Math.PI * result * step;
    }

    @Override
    protected void calcNextApproximation(ProblemParams params) {
        double integralCbrt;
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

        integralCbrt = Math.cbrt(calcIntegralTrapeze(prevApprox, nodes, step));
        q = -2.0 * Math.sin(params.alpha) - params.bond * integralCbrt / Math.PI;

        coefsMtr[0][0] = -(1.0 / step + (step / 4.0) * (params.bond / Math.pow(integralCbrt, 2.0)));
        coefsMtr[0][1] = 1.0 / step;
        coefsMtr[params.splitNum][params.splitNum] = 1.0;

        rightVect[0] = step * q / 4.0;
        rightVect[params.splitNum] = 0.0;

        for (int i = 1; i < params.splitNum; i++) {
            coefsMtr[i][i - 1] = coefs[i];
            coefsMtr[i][i] = -(coefs[i] + coefs[i + 1]);
            coefsMtr[i][i + 1] = coefs[i + 1];

            rightVect[i] = nodes[i] * step * step * (params.bond * prevApprox[i] / Math.pow(integralCbrt, 2.0) + q);
        }

        rightSweep.calcRightSweep(coefsMtr, rightVect, nextApprox);
    }

    @Override
    protected double calcVolumeNondimMul(double[] func) {
        return 1.0 / Math.cbrt(calcIntegralTrapeze(func, nodes, step));
    }
}
