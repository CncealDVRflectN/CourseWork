package by.bsu.dcm.coursework.math.fluid;

import by.bsu.dcm.coursework.math.Function;
import by.bsu.dcm.coursework.math.RightSweepExceprion;
import by.bsu.dcm.coursework.math.Util;
import com.badlogic.gdx.math.Vector2;

public abstract class EquilibriumFluid {
    protected Function integrand;

    protected ProblemParams lastParams;
    protected double[] lastCorrectResult;

    protected double[] nodes;
    protected double[] nextApprox;
    protected double[] prevApprox;

    protected double[][] coefsMtr;
    protected double[] rightVect;

    protected int iterationsLimit;

    public EquilibriumFluid(Function integrand) {
        this.integrand = integrand;
        iterationsLimit = 10000;

        lastParams = new ProblemParams();
    }

    protected abstract void calcNextApproximation(double step, ProblemParams params) throws RightSweepExceprion;

    protected void calcInitialApproximation() {
        for (int i = 0; i < nodes.length; i++) {
            nextApprox[i] = Math.sqrt(1.0 - Math.pow(nodes[i], 2.0));
        }
    }

    public ProblemResult calcResult(ProblemParams params) throws IterationsLimitException, IncorrectResultException, RightSweepExceprion {
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

        if (lastParams.splitNum != params.splitNum || lastParams.bond > params.bond || lastParams.alpha != params.alpha ||
                lastParams.epsilon != params.epsilon) {
            calcInitialApproximation();
            lastCorrectResult = new double[params.splitNum + 1];
        } else {
            System.arraycopy(lastCorrectResult, 0, nextApprox, 0, lastCorrectResult.length);
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

        if (!isCorrect(nextApprox)) {
            throw new IncorrectResultException();
        }

        for (int i = 0; i < points.length; i++) {
            points[i] = new Vector2((float) nodes[i], (float) nextApprox[i]);
        }

        lastParams.setParams(params);
        System.arraycopy(nextApprox, 0, lastCorrectResult, 0, nextApprox.length);

        return new ProblemResult(points, params.alpha, params.bond, params.relaxationCoef, iterations);
    }

    public void setIterationsLimit(int iterNum) {
        iterationsLimit = iterNum;
    }

    public static ProblemResult calcRelaxation(EquilibriumFluid equilibriumFluid, RelaxationParams params) throws TargetBondException, RightSweepExceprion {
        ProblemResult result = null;
        ProblemParams problemParams = new ProblemParams(params.alpha, 0.0, 1.0, params.epsilon, params.splitNum);

        do {
            try {
                result = equilibriumFluid.calcResult(problemParams);

                problemParams.bond = Math.min(problemParams.bond + params.bondStep, params.bondTarget);

                if (result.bond >= params.bondTarget) {
                    break;
                }
            } catch (IterationsLimitException | IncorrectResultException e) {
                problemParams.relaxationCoef /= 2.0;
            }
        } while (problemParams.relaxationCoef >= params.relaxationCoefMin);

        if (result.bond < params.bondTarget) {
            throw new TargetBondException();
        }

        return result;
    }

    private static boolean isCorrect(double[] result) {
        for (double point : result) {
            if (Double.isNaN(point) || Double.isInfinite(point)) {
                return false;
            }
        }
        return true;
    }
}
