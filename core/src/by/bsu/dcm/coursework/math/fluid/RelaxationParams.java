package by.bsu.dcm.coursework.math.fluid;

public class RelaxationParams {
    public double alpha;
    public double epsilon;
    public double targetBond;
    public double bondStep;
    public double relaxationCoefMin;
    public int splitNum;
    public int resultsNum;
    public boolean volumeNondim;

    public RelaxationParams() {
        alpha = 0.0;
        epsilon = 0.0;
        targetBond = 0.0;
        bondStep = 0.0;
        relaxationCoefMin = 0.0;
        splitNum = 0;
        resultsNum = 1;
        volumeNondim = false;
    }

    public RelaxationParams(double alpha, double epsilon, double targetBond, double bondStep, double relaxationCoefMin,
                            int splitNum, int resultsNum, boolean volumeNondim) {
        this.alpha = alpha;
        this.epsilon = epsilon;
        this.targetBond = targetBond;
        this.bondStep = bondStep;
        this.relaxationCoefMin = relaxationCoefMin;
        this.splitNum = splitNum;
        this.resultsNum = resultsNum;
        this.volumeNondim = volumeNondim;
    }
}
