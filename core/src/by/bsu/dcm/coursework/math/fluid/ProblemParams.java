package by.bsu.dcm.coursework.math.fluid;

public class ProblemParams {
    public double alpha;
    public double bond;
    public double relaxationCoef;
    public double epsilon;
    public int splitNum;
    public boolean volumeNondim;

    public ProblemParams() {
        alpha = 0.0;
        bond = 0.0;
        relaxationCoef = 0.0;
        epsilon = 0.0;
        splitNum = 0;
        volumeNondim = false;
    }

    public ProblemParams(double alpha, double bond, double relaxationCoef, double epsilon, int splitNum, boolean volumeNondim) {
        this.alpha = alpha;
        this.bond = bond;
        this.relaxationCoef = relaxationCoef;
        this.epsilon = epsilon;
        this.splitNum = splitNum;
        this.volumeNondim = volumeNondim;
    }

    public void setParams(double alpha, double bond, double relaxationCoef, double epsilon, int splitNum, boolean volumeNondim) {
        this.alpha = alpha;
        this.bond = bond;
        this.relaxationCoef = relaxationCoef;
        this.epsilon = epsilon;
        this.splitNum = splitNum;
        this.volumeNondim = volumeNondim;
    }

    public void setParams(ProblemParams params) {
        this.alpha = params.alpha;
        this.bond = params.bond;
        this.relaxationCoef = params.relaxationCoef;
        this.epsilon = params.epsilon;
        this.splitNum = params.splitNum;
        this.volumeNondim = params.volumeNondim;
    }
}
