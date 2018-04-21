package by.bsu.dcm.coursework.math.fluid;

import by.bsu.dcm.coursework.math.Function;
import by.bsu.dcm.coursework.math.Util;
import com.badlogic.gdx.math.Vector2;

import java.util.Arrays;

public abstract class EquilibriumFluid {
    protected Function integrand;

    protected double[] nodes;
    protected double[] nextApprox;
    protected double[] prevApprox;

    protected double[][] coefsMtr;
    protected double[] rightVect;

    protected int iterationsLimit;

    public EquilibriumFluid(Function integrand) {
        this.integrand = integrand;
        iterationsLimit = 10000;
    }

    protected abstract void calcNextApproximation(double step, ProblemParams params);

    protected void calcInitialApproximation() {
        for (int i = 0; i < nodes.length; i++) {
            nextApprox[i] = Math.sqrt(1.0 - Math.pow(nodes[i], 2.0));
        }
    }

    public ProblemResult calcResult(ProblemParams params) throws IterationsLimitException {
        Vector2[] points = new Vector2[params.splitNum + 1];
        double[] tmp;
        double curEpsilon = params.epsilon / params.relaxationCoef;
        double step = 1.0 / params.splitNum;
        int iterations = 0;

        if (nodes == null || nodes.length != params.splitNum + 1) {
            nodes = new double[params.splitNum + 1];
            for (int i = 0; i < nodes.length; i++) {
                nodes[i] = i * step;
            }

            nextApprox = new double[params.splitNum + 1];
            prevApprox = new double[params.splitNum + 1];
            coefsMtr = new double[params.splitNum + 1][params.splitNum + 1];
            rightVect = new double[params.splitNum + 1];
        }

        if (params.pointsInit == null || params.pointsInit.length != params.splitNum + 1) {
            calcInitialApproximation();
        } else {
            System.arraycopy(params.pointsInit, 0, nextApprox, 0, params.pointsInit.length);
        }

        do {
            tmp = prevApprox;
            prevApprox = nextApprox;
            nextApprox = tmp;
            calcNextApproximation(step, params);

            for (int i = 0; i < nextApprox.length; i++) {
                nextApprox[i] = (1.0 - params.relaxationCoef) * prevApprox[i] + params.relaxationCoef * nextApprox[i];
            }

            iterations++;
        } while (Util.norm(nextApprox, prevApprox) > curEpsilon && iterations < iterationsLimit);

        if (iterations >= iterationsLimit) {
            throw new IterationsLimitException();
        }

        for (int i = 0; i < points.length; i++) {
            points[i] = new Vector2((float) nodes[i], (float) nextApprox[i]);
        }

        return new ProblemResult(points, Arrays.copyOf(nextApprox, nextApprox.length), params.alpha, params.bond, params.relaxationCoef, iterations);
    }

    public void setIterationsLimit(int iterNum) {
        iterationsLimit = iterNum;
    }

    public static ProblemResult calcRelaxation(EquilibriumFluid equilibriumFluid, RelaxationParams params) throws TargetBondException {
        ProblemResult tmp;
        ProblemResult result = null;
        ProblemParams problemParams = new ProblemParams(params.alpha, 0.0, 1.0, params.epsilon, params.splitNum);

        do {
            try {
                tmp = equilibriumFluid.calcResult(problemParams);

                if (isCorrect(tmp.points)) {
                    result = tmp;
                    problemParams.bond = Math.min(problemParams.bond + params.bondStep, params.bondTarget);
                    problemParams.pointsInit = tmp.result;
                } else {
                    problemParams.relaxationCoef /= 2.0;
                }

                if (tmp.bond >= params.bondTarget) {
                    break;
                }
            } catch (IterationsLimitException e) {
                problemParams.relaxationCoef /= 2.0;
            }
        } while (problemParams.relaxationCoef >= params.relaxationCoefMin);

        if (result.bond < params.bondTarget) {
            throw new TargetBondException();
        }

        return result;
    }

    private static boolean isCorrect(Vector2[] points) {
        for (Vector2 point : points) {
            if (Float.isNaN(point.y) || Float.isInfinite(point.y)) {
                return false;
            }
        }
        return true;
    }
}
