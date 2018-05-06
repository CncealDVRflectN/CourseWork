package by.bsu.dcm.coursework.math;

import com.badlogic.gdx.math.Vector2;

public final class Util {
    private Util() {
    }

    public static double norm(double[] next, double[] prev) {
        double max = Double.NEGATIVE_INFINITY;
        double tmp;

        for (int i = 0; i < next.length; i++) {
            tmp = Math.abs(next[i] - prev[i]);
            if (tmp > max) {
                max = tmp;
            }
        }

        return max;
    }

    public static float[] normalize(float[] arr, float min, float max) {
        float[] result = new float[arr.length];

        for (int i = 0; i < arr.length; i++) {
            result[i] = (arr[i] - min) / (max - min);
        }

        return result;
    }

    public static Vector2[] normalize(Vector2[] arr, Vector2 min, Vector2 max) {
        Vector2[] result = new Vector2[arr.length];

        for (int i = 0; i < arr.length; i++) {
            result[i] = new Vector2((arr[i].x - min.x) / (max.x - min.x), (arr[i].y - min.y) / (max.y - min.y));
        }

        return result;
    }

    public static Vector2 normalize(Vector2 vect, Vector2 min, Vector2 max) {
        return new Vector2((vect.x - min.x) / (max.x - min.x), (vect.y - min.y) / (max.y - min.y));
    }
}
