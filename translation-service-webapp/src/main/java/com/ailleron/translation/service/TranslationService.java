package com.ailleron.translation.service;

import com.ailleron.translation.entity.Translation;
import com.ailleron.translation.repository.TranslationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TranslationService {
    private TranslationRepository repository;

    @Autowired
    public TranslationService(TranslationRepository repository) {
        this.repository = repository;
    }

    public boolean save(Translation trans) {
        return repository.findAndSave(trans);
    }
}
