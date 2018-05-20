package by.bsu.dcm.coursework.math.fluid;

import by.bsu.dcm.coursework.math.RightSweep;
import by.bsu.dcm.coursework.math.Util;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public abstract class EquilibriumFluid {
    protected RightSweep rightSweep;

    protected ProblemParams lastParams;
    protected double[] lastCorrectResult;

    protected double[] nodes;
    protected double[] nextApprox;
    protected double[] prevApprox;
    protected double step;

    protected double[] coefsLowerDiagonal;
    protected double[] coefsMainDiagonal;
    protected double[] coefsUpperDiagonal;
    protected double[] rightVect;

    protected int iterationsCounter;
    protected int iterationsLimit;

    public EquilibriumFluid() {
        rightSweep = new RightSweep();
        lastParams = new ProblemParams();

        iterationsLimit = 10000;
    }

    protected abstract void calcNextApproximation(ProblemParams params);

    protected abstract void calcInitialApproximation(ProblemParams params);

    private boolean isCorrect(double[] result) {
        for (double point : result) {
            if (Double.isNaN(point) || Double.isInfinite(point)) {
                return false;
            }
        }
        return true;
    }

    protected void calcFunctionValues(ProblemParams params) throws IterationsLimitException, IncorrectResultException {
        double[] tmp;
        double curEpsilon = params.epsilon * params.relaxationCoef;

        if (nodes == null || nodes.length != params.splitNum + 1) {
            step = 1.0 / params.splitNum;

            nodes = new double[params.splitNum + 1];
            for (int i = 0; i < nodes.length; i++) {
                nodes[i] = i * step;
            }

            nextApprox = new double[params.splitNum + 1];
            prevApprox = new double[params.splitNum + 1];
            coefsLowerDiagonal = new double[params.splitNum];
            coefsMainDiagonal = new double[params.splitNum + 1];
            coefsUpperDiagonal = new double[params.splitNum];
            rightVect = new double[params.splitNum + 1];
        }

        if (lastParams.splitNum != params.splitNum || lastParams.bond > params.bond || lastParams.alpha != params.alpha ||
                lastParams.epsilon != params.epsilon) {
            calcInitialApproximation(params);
            lastCorrectResult = new double[params.splitNum + 1];
        } else {
            System.arraycopy(lastCorrectResult, 0, nextApprox, 0, lastCorrectResult.length);
        }

        iterationsCounter = 0;
        do {
            tmp = prevApprox;
            prevApprox = nextApprox;
            nextApprox = tmp;
            calcNextApproximation(params);

            for (int i = 0; i < nextApprox.length; i++) {
                nextApprox[i] = (1.0 - params.relaxationCoef) * prevApprox[i] + params.relaxationCoef * nextApprox[i];
            }

            iterationsCounter++;
        } while (Util.norm(nextApprox, prevApprox) > curEpsilon && iterationsCounter < iterationsLimit);

        if (iterationsCounter >= iterationsLimit) {
            throw new IterationsLimitException();
        }

        if (!isCorrect(nextApprox)) {
            throw new IncorrectResultException();
        }

        lastParams.setParams(params);
        System.arraycopy(nextApprox, 0, lastCorrectResult, 0, nextApprox.length);
    }

    protected abstract double calcVolumeNondimMul(double[] func);

    protected Vector2[] getPoints(double[] func) {
        Vector2[] result = new Vector2[func.length];
        double volumeNondimMul = 1.0f;

        if (lastParams.volumeNondim) {
            volumeNondimMul = calcVolumeNondimMul(func);
        }

        for (int i = 0; i < result.length; i++) {
            result[i] = new Vector2((float) (nodes[i] * volumeNondimMul), (float) (func[i] * volumeNondimMul));
        }

        return result;
    }

    public ProblemResult calcResult(ProblemParams params) throws IterationsLimitException, IncorrectResultException {
        calcFunctionValues(params);
        return new ProblemResult(getPoints(lastCorrectResult), params.alpha, params.bond, params.relaxationCoef, iterationsCounter);
    }

    public ProblemResult[] calcRelaxation(RelaxationParams params) throws TargetBondException {
        return calcRelaxation(params, null);
    }

    public ProblemResult[] calcRelaxation(RelaxationParams params, List<Vector2> heightCoefs) throws TargetBondException {
        ProblemParams problemParams = new ProblemParams(params.alpha, 0.0, 1.0, params.epsilon, params.splitNum, params.volumeNondim);
        ProblemResult[] results = new ProblemResult[params.resultsNum];
        Vector2[] points;
        double[] resultsTargetBond = new double[params.resultsNum];
        double resultsTargetBondStep = params.targetBond / (params.resultsNum - 1);
        int currentResultIndex = 0;

        if (params.resultsNum == 1) {
            resultsTargetBond[currentResultIndex] = params.targetBond;
        } else {
            for (int i = 0; i < params.resultsNum; i++) {
                resultsTargetBond[i] = i * resultsTargetBondStep;
            }
        }

        do {
            try {
                calcFunctionValues(problemParams);
                points = getPoints(lastCorrectResult);

                if (problemParams.bond >= resultsTargetBond[currentResultIndex]) {
                    results[currentResultIndex] = new ProblemResult(points, problemParams.alpha,
                            problemParams.bond, problemParams.relaxationCoef, iterationsCounter);
                    currentResultIndex++;
                }

                if (heightCoefs != null) {
                    heightCoefs.add(new Vector2((float) problemParams.bond, points[0].y / points[points.length - 1].x));
                }

                if (currentResultIndex > results.length - 1) {
                    break;
                }

                problemParams.bond = Math.min(problemParams.bond + params.bondStep, params.targetBond);
            } catch (IterationsLimitException | IncorrectResultException e) {
                problemParams.relaxationCoef /= 2.0;
            }
        } while (problemParams.relaxationCoef >= params.relaxationCoefMin);

        if (results[results.length - 1] == null || results[results.length - 1].bond < params.targetBond) {
            throw new TargetBondException();
        }

        return results;
    }

    public void setIterationsLimit(int iterNum) {
        iterationsLimit = iterNum;
    }
}
