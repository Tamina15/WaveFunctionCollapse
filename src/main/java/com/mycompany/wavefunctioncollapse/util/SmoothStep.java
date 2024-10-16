package com.mycompany.wavefunctioncollapse.util;

/**
 *
 * @author nhat.tranminh
 */
public class SmoothStep {

    public static final long FACTORIAL[] = new long[]{1, 1, 2, 6, 24, 120, 720, 5040, 40320, 362880, 3628800, 39916800, 479001600, 6227020800L, 87178291200L, 1307674368000L, 20922789888000L, 355687428096000L, 6402373705728000L, 121645100408832000L, 2432902008176640000L,};
    public static final int FACTORIAL_LENGTH = FACTORIAL.length;

    public static double generalSmoothstep(int N, double x, double l, double r) {
        x = clamp((x - l) / (r - l), l, r);
        double result = 0;
        for (int n = 0; n <= N; n++) {
            result += binomialCoefficient(-N - 1, n)
                    * binomialCoefficient(2 * N + 1, N - n)
                    * Math.pow(x, N + n + 1);
        }
        return result;
    }

    public static double smoothStep(double x, double l, double r) {
        x = clamp((x - l) / (r - l), l, r);
        return x * x * (3.0 - 2.0 * x);
    }

    public static double smootherStep(double x, double l, double r) {
        x = clamp((x - l) / (r - l), l, r);
        return x * x * x * (x * (6.0f * x - 15.0f) + 10.0f);
    }

    public static double s4Step(double x, double l, double r) {
        x = clamp((x - l) / (r - l), l, r);
        return 70 * Math.pow(x, 9) - 315
                * Math.pow(x, 8) + 540
                * Math.pow(x, 7) - 420
                * Math.pow(x, 6) + 126
                * Math.pow(x, 5);
    }

    public static double clamp(double x, double l, double r) {
        return x < l ? l : x > r ? r : x;
//        if (x < l) {
//            return l;
//        }
//        if (x > r) {
//            return r;
//        }
//        return x;
    }

    /*
        ⎛ n ⎞     n * (n - 1) * ... * (n - k + 1)       (n - k + 1) * (n - (k - 1) + 1) * ... * (n - 1) * n  
        ⎜   ⎟ =  --------------------------------- <=> ----------------------------------------------------- 
        ⎝ k ⎠          k * (k - 1) * ... * 1                         k * (k - 1) * ... * 2 * 1               

                 (n - k + 1)     (n - (k - 1) + 1)           n - 1 <=> {n - (k - (k - 2)) + 1}    n <=> {n - (k - (k - 1)) + 1}
            <=> ------------- * ------------------- + ··· + -------                            + ---      
                      k                k - 1                   2   <=> {k - (k - 2)}              1 <=> {k - (k - 1)} 

                 k  n - i + 1  
            <=>  ∑ ----------- 
                i=1     i      
     */
    public static double binomialCoefficient(int n, int k) {
        double result = 1;
        for (int i = 1; i <= k; i++) { // avoid divided by zero
            result *= (n - i + 1) / i;
        }
        return result;
    }

    public static double inverseSmoothstep(double x) {
        return 0.5 - Math.sin(Math.asin(1.0 - 2.0 * x) / 3.0);
    }
}
