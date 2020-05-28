package kelly;

public class MatrixUtil {
    public static double[] cloneArray(double[] original) {
        double[] clone = new double[original.length];
        System.arraycopy(original, 0, clone, 0, original.length);
        return clone;
    }

    public static double distancedSquare(double[] v1, double[] v2) {
        double result = 0;
        for(int i = 0; i < v1.length; i++) {
            result += Math.pow(v1[i] - v2[i], 2);
        }
        return result;
    }

    public static void addTo(double[] target, double[] add) {
        for(int i = 0; i < target.length; i++) {
            target[i] += add[i];
        }
    }
}
