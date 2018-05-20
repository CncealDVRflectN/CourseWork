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
        double result = values[0] * nodes[0] / 2.0;
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

        integralCbrt = Math.cbrt(calcIntegralTrapeze(prevApprox, nodes, step));
        q = -2.0 * Math.sin(params.alpha) - params.bond * integralCbrt / Math.PI;

        coefsMainDiagonal[0] = -(1.0 / step + (step / 4.0) * (params.bond / Math.pow(integralCbrt, 2.0)));
        coefsUpperDiagonal[0] = 1.0 / step;
        coefsMainDiagonal[params.splitNum] = 1.0;

        rightVect[0] = step * q / 4.0;
        rightVect[params.splitNum] = 0.0;

        for (int i = 1; i < params.splitNum; i++) {
            coefsLowerDiagonal[i - 1] = coefs[i];
            coefsMainDiagonal[i] = -(coefs[i] + coefs[i + 1]);
            coefsUpperDiagonal[i] = coefs[i + 1];

            rightVect[i] = nodes[i] * step * step * (params.bond * prevApprox[i] / Math.pow(integralCbrt, 2.0) + q);
        }

        rightSweep.calcRightSweep(coefsLowerDiagonal, coefsMainDiagonal, coefsUpperDiagonal, rightVect, nextApprox);
    }

    @Override
    protected void calcInitialApproximation(ProblemParams params) {
        double sin = Math.sin(params.alpha);
        double ctg = 1.0 / Math.tan(params.alpha);

        for (int i = 0; i < nodes.length; i++) {
            nextApprox[i] = Math.sqrt(1.0 - Math.pow(nodes[i] * sin, 2.0)) / sin - ctg;
        }
    }

    @Override
    protected double calcVolumeNondimMul(double[] func) {
        return 1.0 / Math.cbrt(calcIntegralTrapeze(func, nodes, step));
    }
}
