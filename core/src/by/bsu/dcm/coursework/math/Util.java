package by.bsu.dcm.coursework.math;

import com.badlogic.gdx.math.Vector2;

public final class Util {
    private Util() {
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

    public static float[] normalize(float[] arr, float min, float max) {
        float[] result = new float[arr.length];

        for (int i = 0; i < arr.length; i++) {
            result[i] = (arr[i] - min) / (max - min);
        }

        return result;
    }

    public static Vector2[] normalize(Vector2[] arr, Vector2 min, Vector2 max) {
        Vector2[] result = new Vector2[arr.length];

        for (int i = 0; i < arr.length; i++) {
            result[i] = new Vector2((arr[i].x - min.x) / (max.x - min.x), (arr[i].y - min.y) / (max.y - min.y));
        }

        return result;
    }

    public static Vector2 normalize(Vector2 vect, Vector2 min, Vector2 max) {
        return new Vector2((vect.x - min.x) / (max.x - min.x), (vect.y - min.y) / (max.y - min.y));
    }

    public static double[] calcRightSweep(double[][] mtr, double[] vect) throws RightSweepExceprion {
        return RightSweep.calcRightSweep(mtr, vect, new double[vect.length]);
    }

    public static double[] calcRightSweep(double[][] mtr, double[] vect, double[] solutionDest) throws RightSweepExceprion {
        return RightSweep.calcRightSweep(mtr, vect, solutionDest);
    }

    private static class RightSweep {
        private static double[] alpha;
        private static double[] beta;

        private static void calcAlpha(double[][] mtr) {
            alpha[0] = -mtr[0][1] / mtr[0][0];
            for (int i = 1; i < alpha.length; i++) {
                alpha[i] = -mtr[i][i + 1] / (mtr[i][i] + mtr[i][i - 1] * alpha[i - 1]);
            }
        }

        private static void calcBeta(double[][] mtr, double[] vect) {
            beta[0] = vect[0] / mtr[0][0];
            for (int i = 1; i < beta.length; i++) {
                beta[i] = (vect[i] - mtr[i][i - 1] * beta[i - 1]) / (mtr[i][i] + mtr[i][i - 1] * alpha[i - 1]);
            }
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

        public static double[] calcRightSweep(double[][] mtr, double[] vect, double[] solutionDest) throws RightSweepExceprion {
            if (!isCorrect(mtr)) {
                throw new RightSweepExceprion();
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
}
