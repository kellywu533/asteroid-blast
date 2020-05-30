package kelly;

/**
 * Collection of miscellaneous matrix/vector manipulation methods.
 */
public class MatrixUtil {
    /**
     * Clones the given array.
     * @param original The original array.
     * @return A copy of the original array.
     */
    public static double[] cloneArray(double[] original) {
        double[] clone = new double[original.length];
        System.arraycopy(original, 0, clone, 0, original.length);
        return clone;
    }

    /**
     * Returns the distance squared of two given vectors.
     * @param v1 Vector 1.
     * @param v2 Vector 2.
     * @return The distance squared of the vectors.
     */
    public static double distancedSquare(double[] v1, double[] v2) {
        double result = 0;
        for(int i = 0; i < v1.length; i++) {
            result += Math.pow(v1[i] - v2[i], 2);
        }
        return result;
    }

    /**
     * Adds the add array into the target array.
     * @param target The target array that is to be changed.
     * @param add The added array that is to be added to the target.
     */
    public static void addTo(double[] target, double[] add) {
        for(int i = 0; i < target.length; i++) {
            target[i] += add[i];
        }
    }
}
