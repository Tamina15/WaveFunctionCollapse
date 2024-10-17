package com.tmn.wavefunctioncollapse.util;

import java.util.Random;

public class BenchmarkReverseString {

    static boolean reverseUsingStringAndBytes(String a, String b) {
        byte[] bytes = b.getBytes();
        int length = bytes.length;
        int half = length / 2;
        for (int i = 0; i < half; i++) {
            byte t = bytes[i];
            bytes[i] = bytes[length - 1 - i];
            bytes[length - 1 - i] = t;
        }
        return a.equals(new String(bytes));
    }

    static boolean reverseUsingBytesAndBytes(String a, String b) {
        byte[] bytesa = b.getBytes();
        byte[] bytesb = a.getBytes();
        int length = bytesa.length;
        for (int i = 0; i < length; i++) {
            if (bytesa[i] != bytesb[length - 1 - i]) {
                return false;
            }
        }
        return true;
    }

    static boolean reverseUsingGetBytesAndGetBytes(String a, String b) {
        int length = a.length();
        for (int i = 0; i < length; i++) {
            if (a.charAt(i) != b.charAt(length - 1 - i)) {
                return false;
            }
        }
        return true;
    }

    static boolean reverseUsingStringBuilder(String a, String b) {
        return new StringBuilder(b).reverse().toString().equals(a);
    }

    static String reverse(String s) {
        return new StringBuilder(s).reverse().toString();
    }

    static String alphabet = "1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";

    static Random r = new Random();

    static String makeString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(alphabet.charAt(r.nextInt(alphabet.length())));
        }
        return sb.toString();
    }

    static void checkEquality(String[][] strings) {
        System.out.println("Checking functions output:");
        String t1, t2;
        for (String[] string : strings) {
            t1 = new String(string[0].getBytes());
            t2 = new String(string[0].getBytes());
            boolean a = reverseUsingStringAndBytes(t1, t2);
            t1 = new String(string[0].getBytes());
            t2 = new String(string[0].getBytes());
            boolean b = reverseUsingBytesAndBytes(t1, t2);
            t1 = new String(string[0].getBytes());
            t2 = new String(string[0].getBytes());
            boolean c = reverseUsingStringBuilder(t1, t2);
            t1 = new String(string[0].getBytes());
            t2 = new String(string[0].getBytes());
            boolean d = reverseUsingGetBytesAndGetBytes(t1, t2);
            if (a && b && c && d) {
                continue;
            }
            if (!a && !b && !c && !d) {
                continue;
            }
            System.out.println("Fail: " + a + " " + b + " " + c + " " + d + " " + string[0] + " " + string[1]);
        }
        System.out.println("Done");
    }

    static long doReverseUsingStringAndBytes(String[][] strings) {
        long start, end;
        start = System.nanoTime();
        for (String[] string : strings) {
            reverseUsingStringAndBytes(string[0], string[1]);
        }
        end = System.nanoTime();
        return end - start;
    }

    static long doReverseUsingBytesAndBytes(String[][] strings) {
        long start, end;
        start = System.nanoTime();
        for (String[] string : strings) {
            reverseUsingBytesAndBytes(string[0], string[1]);
        }
        end = System.nanoTime();
        return end - start;
    }

    static long doReverseUsingGetBytesAndGetBytes(String[][] strings) {
        long start, end;
        start = System.nanoTime();
        for (String[] string : strings) {
            reverseUsingGetBytesAndGetBytes(string[0], string[1]);
        }
        end = System.nanoTime();
        return end - start;
    }

    static long doReverseUsingStringBuilder(String[][] strings) {
        long start, end;
        start = System.nanoTime();
        for (String[] string : strings) {
            reverseUsingStringBuilder(string[0], string[1]);
        }
        end = System.nanoTime();
        return end - start;
    }

    public static void main(String[] args) {
        int stringArrayLength = 10;
        int stringLength = 10000;
        double similarPosibility = 0.5;
        int loopLength = 10;
        String[][] strings = new String[stringArrayLength][2];
        for (String[] string : strings) {
            int length = r.nextInt(stringLength) + 1;
            string[0] = makeString(length);
            if (r.nextFloat() < similarPosibility) {
                string[1] = reverse(string[0]);
            } else {
                string[1] = makeString(length);
            }
        }
        checkEquality(strings);
        long ta = 0, tb = 0, tc = 0, td = 0;
        for (int i = 0; i < loopLength; i++) {
            System.out.println(i);
            // checkEquality(strings);
            ta += doReverseUsingBytesAndBytes(strings);
            tb += doReverseUsingStringAndBytes(strings);
            tc += doReverseUsingStringBuilder(strings);
            td += doReverseUsingGetBytesAndGetBytes(strings);
        }
        System.out.println("doReverseUsingBytesAndBytes: " + ta * 1.0 / 1000000000);
        System.out.println("doReverseUsingStringAndBytes: " + tb * 1.0 / 1000000000);
        System.out.println("doReverseUsingStringBuilder: " + tc * 1.0 / 1000000000);
        System.out.println("doReverseUsingGetBytesAndGetBytes: " + td * 1.0 / 1000000000);
    }
}
