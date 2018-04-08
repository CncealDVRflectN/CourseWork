package by.bsu.dcm.coursework.math;

import by.bsu.dcm.coursework.graphs.GraphPoints;

public abstract class EquilibriumFluid {
    protected GraphPoints graphPoints;
    protected Function integrand;

    protected double epsilon;
    protected double bond;
    protected double alpha;
    protected int splitNum;

    public EquilibriumFluid(Function integrand, double alpha, double bond, double epsilon, int splitNum) {
        this.integrand = integrand;
        graphPoints = new GraphPoints();

        this.alpha = alpha;
        this.bond = bond;
        this.epsilon = epsilon;
        this.splitNum = splitNum;
    }

    public abstract void calcResult();

    public GraphPoints getGraphPoints() {
        return graphPoints;
    }

    public void setParams(double alpha, double bond, double epsilon, int splitNum) {
        this.alpha = alpha;
        this.bond = bond;
        this.epsilon = epsilon;
        this.splitNum = splitNum;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    public void setBond(double bond) {
        this.bond = bond;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public void setSplitNum(int num) {
        splitNum = num;
    }
}
