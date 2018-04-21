package by.bsu.dcm.coursework.math.fluid;

public class RelaxationParams {
    public double alpha;
    public double epsilon;
    public double bondTarget;
    public double bondStep;
    public double relaxationCoefMin;
    public int splitNum;

    public RelaxationParams() {
        alpha = 0.0;
        epsilon = 0.0;
        bondTarget = 0.0;
        bondStep = 0.0;
        relaxationCoefMin = 0.0;
        splitNum = 0;
    }

    public RelaxationParams(double alpha, double epsilon, double bondTarget, double bondStep, double relaxationCoefMin, int splitNum) {
        this.alpha = alpha;
        this.epsilon = epsilon;
        this.bondTarget = bondTarget;
        this.bondStep = bondStep;
        this.relaxationCoefMin = relaxationCoefMin;
        this.splitNum = splitNum;
    }
}
