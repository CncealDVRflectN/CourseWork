package by.bsu.dcm.coursework.math.fluid;

public class IncorrectResultException extends Exception {
    public IncorrectResultException() {
        super("Result contains NaN or Infinity");
    }
}
