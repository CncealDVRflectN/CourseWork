package by.bsu.dcm.coursework.util;

public class Pair<F, S> {
    public F first;
    public S second;

    public Pair() {
        first = null;
        second = null;
    }

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }
}
