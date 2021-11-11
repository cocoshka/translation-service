package com.ailleron.translation.repository;

import com.ailleron.translation.model.Dictionary;

import java.util.Collection;
import java.util.List;

public interface DictionaryRepository {
    void clear();
    void clearCustomNotIn(String product, Collection<String> languages);
    void save(Dictionary trans, boolean custom);
    List<Dictionary> findBy(Collection<String> languages, Collection<String> products, Collection<String> labels);
    Dictionary getDefault(String product, Collection<String> labels);
}
