package by.bsu.dcm.coursework.math.fluid;

import com.badlogic.gdx.math.Vector2;

public class ProblemResult {
    public final Vector2[] points;
    public final double[] result;
    public final double alpha;
    public final double bond;
    public final double relaxationCoef;
    public final int iterationsNum;

    public ProblemResult(Vector2[] points, double[] result, double alpha, double bond, double relaxationCoef, int iterationsNum) {
        this.points = points;
        this.result = result;
        this.alpha = alpha;
        this.bond = bond;
        this.relaxationCoef = relaxationCoef;
        this.iterationsNum = iterationsNum;
    }
}
