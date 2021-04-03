package com.github.militaermilitz.mcUtil;

/**
 * @author Alexander Ley
 * @version 1.0
 * This Interface marks data classes which are saved b gson.
 *
 * @param <T> Object that actually should saved but cannot because of complex data that cannot saved (e.g. a world object).
 */
public interface IFileConstructor<T> {

    /**
     * Save Class data to json file using gson.
     */
    void saveToFile();

    /**
     * Load Object from data the class offers.
     */
    T load();
}
