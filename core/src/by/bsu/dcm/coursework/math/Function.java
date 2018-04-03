package by.bsu.dcm.coursework.math;

public interface Function {
    double calc(int index);

    int getLength();

    void setParams(Object... params);
}
