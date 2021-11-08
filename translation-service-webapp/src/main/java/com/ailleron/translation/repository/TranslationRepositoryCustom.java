package com.ailleron.translation.repository;

import com.ailleron.translation.entity.Translation;

public interface TranslationRepositoryCustom {
    boolean findAndSave(Translation trans);
    Translation findByLanguageAndProduct(String language, String product);
}
