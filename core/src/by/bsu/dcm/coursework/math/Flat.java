package by.bsu.dcm.coursework.math;

public class Flat {
    private double step;
    private double alpha;
    private double bond;
    private double epsilon;
    private double[] rightPart;
    private double[][] leftPart;
    private double[] previousApproximation;
    private double[] nextApproximation;
    private int N;
    private double[] alphas;
    private double[] betas;

    Flat(double alpha, double bond, double epsilon, int N){
        this.alpha = alpha;
        this.bond = bond;
        this.epsilon = epsilon;
        this.N = N;
        this.step = 1.0 / N;
    }

    public static double norm(double[] a, double[] b){
        double ans = Math.abs(a[0] - b[0]);
        for (int i = 1; i < a.length; i++){
            ans = Math.max(ans, Math.abs(a[i] - b[i]));
        }
        return ans;
    }

    public double calculateIntegral(double[] y){
        double ans = 0;
        ans += step * y[0] / 2;
        for (int i = 1; i < y.length - 1; i++){
            ans += step * y[i];
        }
        return ans * 2;
    }

    public void createLeftPart(){
        leftPart = new double[N + 1][N + 1];
        leftPart[0][0] = -(1 - bond * step * step / 2 / calculateIntegral(previousApproximation));
        leftPart[0][1] = 1;
        leftPart[N][N] = 1;
        for (int i = 1; i < N; i++) {
            leftPart[i][i - 1] = - 8 * step;
            leftPart[i][i] = -2 * 8 * step;
            leftPart[i][i + 1] = - 8 * step;
        }
    }

    public void createRightPart(){
        rightPart = new double[N + 1];
        rightPart[0] = step * step / 2 * (-bond / 2 - Math.sin(alpha));
        rightPart[N] = 0;
        for (int i = 1; i < N; i++){
            rightPart[i] = Math.pow(4 * step * step + Math.pow(previousApproximation[i + 1] - previousApproximation[i - 1], 2) , 3.0 / 2.0) *
                    (bond * previousApproximation[i] / calculateIntegral(previousApproximation) - bond / 2 - Math.sin(alpha));
        }
    }
    public double getNode(int index){
        return index * step;
    }

    public void setApproximations(){
        previousApproximation = new double[N + 1];
        nextApproximation = new double[N + 1];
        for (int i = 0; i < N + 1; i++){
            nextApproximation[i] =
                    Math.sqrt(1 - Math.pow(getNode(i), 2));
        }
    }

    public void copyApproximation(){
        previousApproximation = nextApproximation.clone();
    }

    public void calculateNextApproximation(){
        calculateAlphas();
        calculateBetas();
        calculateSolution();
    }

    private void calculateAlphas() {
        alphas = new double[N + 1];
        alphas[1] = -leftPart[0][1] / leftPart[0][0];
        for (int i = 1; i < N; i++) {
            alphas[i + 1] = leftPart[i][i + 1] / (leftPart[i][i] - leftPart[i][i - 1] * alphas[i]);
        }
    }

    private void calculateBetas() {
        betas = new double[N + 2];
        betas[1] = rightPart[0] / leftPart[0][0];
        for (int i = 1; i <= N; i++) {
            betas[i + 1] = (rightPart[i] + betas[i] * (leftPart[i][i - 1])) / (leftPart[i][i] - alphas[i] * leftPart[i][i - 1]);
        }
    }

    private void calculateSolution() {
        nextApproximation = new double[N + 1];
        nextApproximation[N] = betas[N + 1];
        for (int i = N - 1; i >= 0; i--) {
            nextApproximation[i] = alphas[i + 1] * nextApproximation[i + 1] + betas[i + 1];
        }
    }

    public void printSolution(){
        for (double v : nextApproximation) {
            System.out.print(v + " ");
        }
    }

    public static void main(String[] args) {
        Flat flat = new Flat(Math.PI / 4, 0, 0.00001, 100);
        flat.setApproximations();
        int numberOfIterations = 0;
        while (Flat.norm(flat.nextApproximation, flat.previousApproximation) >= flat.epsilon){
            flat.copyApproximation();
            flat.createLeftPart();
            flat.createRightPart();
            flat.calculateNextApproximation();
            numberOfIterations++;
        }
        flat.printSolution();
        System.out.println();
        System.out.println(numberOfIterations);
    }
}