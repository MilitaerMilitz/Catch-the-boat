package com.github.militaermilitz.util;

import java.util.Comparator;

/**
 * @author Alexander Ley
 * @version 1.0
 *
 * This class simulates a 2-homogeneousTuple.
 * @param <T> type of first and second.
 */
public class HomogenTuple<T> extends Tuple<T, T> {

    /**
     * Creates a new homogeneous tuple.
     * @param e1 First element
     * @param e2 Second element
     */
    public HomogenTuple(T e1, T e2) {
        super(e1, e2);
    }

    /**
     * Creates a new homogen tuple from @param tuple (shallow copy).
     */
    public HomogenTuple(Tuple<T, T> tuple) {
        super(tuple);
    }

    /**
     * @return Returns the max value concerning the @param comparator
     */
    public T max (Comparator<T> comparator){
        T max = getKey();
        if (comparator.compare(max, getValue()) < 0) max = getValue();
        return max;
    }
}
