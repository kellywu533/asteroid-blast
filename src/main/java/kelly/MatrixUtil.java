package kelly;

public class MatrixUtil {
    public static double[] cloneArray(double[] original) {
        double[] clone = new double[original.length];
        for(int i = 0; i < original.length; i++) {
            clone[i] = original[i];
        }
        return clone;
    }

    public static void assignTo(double[] target, double[] source) {
        for (int i = 0; i<target.length; i++) {
            target[i] = source[i];
        }
    }

    public static void addToArray(double[] target, double[] add) {
        for(int i = 0; i < target.length; i++) {
            target[i] += add[i];
        }
    }
}
