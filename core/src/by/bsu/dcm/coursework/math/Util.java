package by.bsu.dcm.coursework.math;

public final class Util {
    private Util() {
    }

    private static double[] calcAlpha(double[][] mtr) {
        double[] alpha = new double[mtr.length - 1];

        alpha[0] = -mtr[0][1] / mtr[0][0];
        for (int i = 1; i < alpha.length; i++) {
            alpha[i] = -mtr[i][i + 1] / (mtr[i][i] + mtr[i][i - 1] * alpha[i - 1]);
        }

        return alpha;
    }

    private static double[] calcBeta(double[][] mtr, double[] vect, double[] alpha) {
        double[] beta = new double[mtr.length];

        beta[0] = vect[0] / mtr[0][0];
        for (int i = 1; i < beta.length; i++) {
            beta[i] = (vect[i] - mtr[i][i - 1] * beta[i - 1]) / (mtr[i][i] + mtr[i][i - 1] * alpha[i - 1]);
        }

        return beta;
    }

    private static double[] calcSolution(double[] alpha, double[] beta, double[] solutionDest) {
        solutionDest[solutionDest.length - 1] = beta[beta.length - 1];
        for (int i = solutionDest.length - 2; i >= 0; i--) {
            solutionDest[i] = alpha[i] * solutionDest[i + 1] + beta[i];
        }

        return solutionDest;
    }

    private static boolean isCorrect(double[][] mtr) {
        for (int i = 0; i < mtr.length; i++) {
            if (mtr.length != mtr[i].length) {
                return false;
            }
        }
        if (Math.abs(mtr[0][0]) < Math.abs(mtr[0][1])) {
            return false;
        }
        if (Math.abs(mtr[mtr.length - 1][mtr.length - 1]) < Math.abs(mtr[mtr.length - 1][mtr.length - 2])) {
            return false;
        }
        for (int i = 1; i < mtr.length - 1; i++) {
            if (Math.abs(mtr[i][i]) < (Math.abs(mtr[i][i - 1]) + Math.abs(mtr[i][i + 1]))) {
                return false;
            }
        }
        return true;
    }

    public static double[] calcRightSweep(double[][] mtr, double[] vect) {
        return calcRightSweep(mtr, vect, new double[vect.length]);
    }

    public static double[] calcRightSweep(double[][] mtr, double[] vect, double[] solutionDest) {
        double[] alpha;

        alpha = calcAlpha(mtr);

        return calcSolution(alpha, calcBeta(mtr, vect, alpha), solutionDest);
    }

    public static double calcIntegralTrapeze(Function func, double step) {
        int nodesNum = func.getLength();
        double result = (func.calc(0) + func.calc(nodesNum - 1)) / 2.0;

        for (int i = 1; i < nodesNum - 1; i++) {
            result += func.calc(i);
        }

        return result * step;
    }

    public static double norm(double[] next, double[] prev) {
        double max = Double.NEGATIVE_INFINITY;
        double tmp;

        for (int i = 0; i < next.length; i++) {
            tmp = Math.abs(next[i] - prev[i]);
            if (tmp > max) {
                max = tmp;
            }
        }

        return max;
    }
}
