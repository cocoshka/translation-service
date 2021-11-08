package com.ailleron.translation.repository;

import com.ailleron.translation.entity.Translation;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Slf4j
public class TranslationRepositoryImpl implements TranslationRepositoryCustom {

    private MongoTemplate template;

    @Autowired
    public TranslationRepositoryImpl(MongoTemplate template) {
        this.template = template;
    }

    @Override
    public boolean findAndSave(Translation trans) {
        Translation t = findByLanguageAndProduct(trans.getLanguage(), trans.getProduct());
        Query query = null;
        if (t != null) {
            query = new Query(where("id").is(t.getId()));

            boolean valuesSame = trans.getValues().equals(t.getValues());
            boolean customSame = trans.getCustom().equals(t.getCustom());

            if (valuesSame && customSame) return false;
        }

        Document doc = new Document();
        template.getConverter().write(trans, doc);
        Update update = Update.fromDocument(doc, "version");

        UpdateResult result = template.upsert(query, update, Translation.class);
        return result.wasAcknowledged();
    }

    @Override
    public Translation findByLanguageAndProduct(String language, String product) {
        Query query = new Query(where("language").is(language)
                .and("product").is(product)
        );
        return template.findOne(query, Translation.class);
    }
}
