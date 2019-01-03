package org.talend.dataquality.semantic;

import org.apache.commons.math3.exception.NotANumberException;
import org.apache.commons.math3.exception.NotFiniteNumberException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.Pair;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Distribution<T> implements Serializable {

    private final double[] probabilities;

    private Random random;

    private final List<T> singletons;

    public Distribution(List<Pair<T, Double>> pmf) {
        this(pmf, new SecureRandom());
    }

    public Distribution(List<Pair<T, Double>> pmf, Random rnd) {
        this.random = rnd;
        this.singletons = new ArrayList(pmf.size());

        double[] probs = new double[pmf.size()];
        for (int i = 0; i < pmf.size(); ++i) {
            Pair<T, Double> sample = (Pair) pmf.get(i);
            this.singletons.add(sample.getKey());
            double p = (Double) sample.getValue();
            if (p < 0.0D) {
                throw new NotPositiveException((Number) sample.getValue());
            }

            if (Double.isInfinite(p)) {
                throw new NotFiniteNumberException(p, new Object[0]);
            }

            if (Double.isNaN(p)) {
                throw new NotANumberException();
            }

            probs[i] = p;
        }

        this.probabilities = MathArrays.normalizeArray(probs, 1.0D);
    }

    public T sample() {
        double randomValue = this.random.nextDouble();
        double sum = 0.0D;

        for (int i = 0; i < this.probabilities.length; ++i) {
            sum += this.probabilities[i];
            if (randomValue < sum) {
                return this.singletons.get(i);
            }
        }

        return this.singletons.get(this.singletons.size() - 1);
    }
}
