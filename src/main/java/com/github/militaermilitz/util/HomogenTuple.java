package com.github.militaermilitz.util;

import java.util.Comparator;
import java.util.function.Consumer;

/**
 * @author Alexander Ley
 * @version 1.1
 *
 * This class simulates a 2-homogeneousTuple.
 * @param <T> type of first and second.
 */
public class HomogenTuple<T> extends Tuple<T, T> {

    public HomogenTuple(){

    }

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

    /**
     * Perform @param action to key and value.
     */
    public void forEach(Consumer<? super T> action) {
        action.accept(getKey());
        action.accept(getValue());
    }
}
