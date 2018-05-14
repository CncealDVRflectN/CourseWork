package by.bsu.dcm.coursework.math.fluid;

public class Plain extends EquilibriumFluid {
    private double calcIntegralTrapeze(double[] values, double step) {
        double result = values[0] / 2.0;
        double length = values.length - 1;

        for (int i = 1; i < length; i++) {
            result += values[i];
        }

        return 2.0 * result * step;
    }

    @Override
    protected void calcNextApproximation(ProblemParams params) {
        double coef = 8.0 * step;
        double integral;

        integral = calcIntegralTrapeze(prevApprox, step);

        coefsMainDiagonal[0] = -1.0;
        coefsUpperDiagonal[0] = 1.0;
        coefsMainDiagonal[params.splitNum] = 1.0;

        rightVect[0] = (step * step / 2.0) * (params.bond / 2.0 + Math.sin(params.alpha));
        rightVect[params.splitNum] = 0.0;

        for (int i = 1; i < params.splitNum; i++) {
            coefsLowerDiagonal[i - 1] = coef;
            coefsMainDiagonal[i] = -2.0 * coef;
            coefsUpperDiagonal[i] = coef;

            rightVect[i] = Math.pow(4.0 * step * step + Math.pow(prevApprox[i + 1] - prevApprox[i - 1], 2.0), 3.0 / 2.0) *
                    (params.bond * prevApprox[i] / integral - params.bond / 2.0 - Math.sin(params.alpha));
        }

        rightSweep.calcRightSweep(coefsLowerDiagonal, coefsMainDiagonal, coefsUpperDiagonal, rightVect, nextApprox);
    }

    @Override
    protected double calcVolumeNondimMul(double[] func) {
        return 1.0 / Math.sqrt(calcIntegralTrapeze(func, step));
    }
}