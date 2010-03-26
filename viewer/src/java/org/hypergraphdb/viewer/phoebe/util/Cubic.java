package org.hypergraphdb.viewer.phoebe.util;

/**
 * This class represents a cubic polynomial data structure. The function is:  a
 * + bu + cu^2 +du^3.
 *
 * @author Originally written by Tim Lambert (lambert
 *
 * @see.unsw.edu.au).
 */
public class Cubic {
    float a;
    float b;
    float c;
    float d;

    /* a + b*u + c*u^2 +d*u^3 */
    public Cubic(
        float a,
        float b,
        float c,
        float d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    /**
     * Evaluates cubic.
     */
    public float eval(float u) {
        return (((((d * u) + c) * u) + b) * u) + a;
    }
}
