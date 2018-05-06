package by.bsu.dcm.coursework.math;

public class RightSweep {
    private double[] alpha;
    private double[] beta;

    private void calcAlpha(double[][] mtr) {
        alpha[0] = -mtr[0][1] / mtr[0][0];
        for (int i = 1; i < alpha.length; i++) {
            alpha[i] = -mtr[i][i + 1] / (mtr[i][i] + mtr[i][i - 1] * alpha[i - 1]);
        }
    }

    private void calcBeta(double[][] mtr, double[] vect) {
        beta[0] = vect[0] / mtr[0][0];
        for (int i = 1; i < beta.length; i++) {
            beta[i] = (vect[i] - mtr[i][i - 1] * beta[i - 1]) / (mtr[i][i] + mtr[i][i - 1] * alpha[i - 1]);
        }
    }

    private double[] calcSolution(double[] alpha, double[] beta, double[] solutionDest) {
        solutionDest[solutionDest.length - 1] = beta[beta.length - 1];
        for (int i = solutionDest.length - 2; i >= 0; i--) {
            solutionDest[i] = alpha[i] * solutionDest[i + 1] + beta[i];
        }

        return solutionDest;
    }

    private boolean isCorrect(double[][] mtr) {
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

    public double[] calcRightSweep(double[][] mtr, double[] vect, double[] solutionDest) {
        if (!isCorrect(mtr)) {
            System.out.println("Warning: right sweep matrix incorrect!");
        }

        if (beta == null || beta.length != mtr.length) {
            alpha = new double[mtr.length - 1];
            beta = new double[mtr.length];
        }

        calcAlpha(mtr);
        calcBeta(mtr, vect);

        return calcSolution(alpha, beta, solutionDest);
    }
}
