package by.bsu.dcm.coursework.math;

public class RightSweep {
    private double[] alpha;
    private double[] beta;

    private void calcAlpha(double[] lowerDiagonal, double[] mainDiagonal, double[] upperDiagonal) {
        alpha[0] = -upperDiagonal[0] / mainDiagonal[0];
        for (int i = 1; i < alpha.length; i++) {
            alpha[i] = -upperDiagonal[i] / (mainDiagonal[i] + lowerDiagonal[i - 1] * alpha[i - 1]);
        }
    }

    private void calcBeta(double[] lowerDiagonal, double[] mainDiagonal, double[] vect) {
        beta[0] = vect[0] / mainDiagonal[0];
        for (int i = 1; i < beta.length; i++) {
            beta[i] = (vect[i] - lowerDiagonal[i - 1] * beta[i - 1]) / (mainDiagonal[i] + lowerDiagonal[i - 1] * alpha[i - 1]);
        }
    }

    private double[] calcSolution(double[] alpha, double[] beta, double[] solutionDest) {
        solutionDest[solutionDest.length - 1] = beta[beta.length - 1];
        for (int i = solutionDest.length - 2; i >= 0; i--) {
            solutionDest[i] = alpha[i] * solutionDest[i + 1] + beta[i];
        }

        return solutionDest;
    }

    private boolean isCorrect(double[] lowerDiagonal, double[] mainDiagonal, double[] upperDiagonal) {
        for (int i = 0; i < mainDiagonal.length; i++) {
            if (lowerDiagonal.length != mainDiagonal.length - 1 || upperDiagonal.length != mainDiagonal.length - 1) {
                return false;
            }
        }
        if (Math.abs(mainDiagonal[0]) < Math.abs(upperDiagonal[0])) {
            return false;
        }
        if (Math.abs(mainDiagonal[mainDiagonal.length - 1]) < Math.abs(lowerDiagonal[lowerDiagonal.length - 1])) {
            return false;
        }
        for (int i = 1; i < upperDiagonal.length; i++) {
            if (Math.abs(mainDiagonal[i]) < (Math.abs(lowerDiagonal[i - 1]) + Math.abs(upperDiagonal[i]))) {
                return false;
            }
        }
        return true;
    }

    public double[] calcRightSweep(double[] lowerDiagonal, double[] mainDiagonal, double[] upperDiagonal, double[] vect, double[] solutionDest) {
        if (!isCorrect(lowerDiagonal, mainDiagonal, upperDiagonal)) {
            System.out.println("Warning: right sweep matrix incorrect!");
        }

        if (beta == null || beta.length != mainDiagonal.length) {
            alpha = new double[mainDiagonal.length - 1];
            beta = new double[mainDiagonal.length];
        }

        calcAlpha(lowerDiagonal, mainDiagonal, upperDiagonal);
        calcBeta(lowerDiagonal, mainDiagonal, vect);

        return calcSolution(alpha, beta, solutionDest);
    }
}
