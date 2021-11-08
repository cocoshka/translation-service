package com.ailleron.translation.repository;

import com.ailleron.translation.entity.Translation;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TranslationRepository extends MongoRepository<Translation, ObjectId>, TranslationRepositoryCustom {

}
