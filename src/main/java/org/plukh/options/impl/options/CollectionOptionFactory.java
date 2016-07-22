package org.plukh.options.impl.options;

import org.plukh.options.UnsupportedOptionClassException;
import org.plukh.options.impl.collections.CollectionBackedOption;
import org.plukh.options.impl.collections.OptionsQueue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import static java.text.MessageFormat.format;

public class CollectionOptionFactory {
    private static final Map<Class<? extends Collection>, Class<? extends CollectionBackedOption>> COLLECTION_CLASSES = new HashMap<>();

    static {
        registerCollectionOptionClassForType(Queue.class, OptionsQueue.class);
    }

    private static void registerCollectionOptionClassForType(Class<? extends Collection> collectionClass, Class<? extends CollectionBackedOption> optionCollectionClass) {
        COLLECTION_CLASSES.put(collectionClass, optionCollectionClass);
    }

    public static CollectionOption getCollectionOption(Class elementClass, Class collectionClass) throws UnsupportedOptionClassException {
        Class optionCollectionClass = COLLECTION_CLASSES.get(collectionClass);
        if (optionCollectionClass == null) {
            throw new UnsupportedOptionClassException(format("There is no supported options collection class for {0}", collectionClass.getName()));
        }
        return collectionClass.isInterface() ? new CollectionOption(elementClass, optionCollectionClass) :
                new CollectionOption(collectionClass, elementClass, optionCollectionClass, collectionClass);
    }

    public static CollectionOption getCollectionOption(Class elementClass, Class collectionClass, Class backingClass) throws UnsupportedOptionClassException {
        Class optionCollectionClass = COLLECTION_CLASSES.get(collectionClass);
        if (optionCollectionClass == null) {
            throw new UnsupportedOptionClassException(format("There is no supported options collection class for {0}", collectionClass.getName()));
        }
        return new CollectionOption(collectionClass, elementClass, optionCollectionClass, backingClass);
    }
}
