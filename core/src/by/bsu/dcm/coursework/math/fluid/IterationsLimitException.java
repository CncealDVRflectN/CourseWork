package by.bsu.dcm.coursework.math.fluid;

public class IterationsLimitException extends Exception {
    public IterationsLimitException() {
        super("Iterations limit exceeded");
    }
}
