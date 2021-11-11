package com.ailleron.translation.repository;

import com.ailleron.translation.model.Dictionary;
import com.ailleron.translation.util.Utils;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Slf4j
@Repository
public class DictionaryRepositoryImpl implements DictionaryRepository {

    private final MongoTemplate template;

    @Autowired
    public DictionaryRepositoryImpl(MongoTemplate template) {
        this.template = template;
    }

    @Override
    public void clear() {
        Query query = new Query(where("labels").is(null).and("custom").is(null));
        template.remove(query, Dictionary.class);
    }

    @Override
    public void clearCustomNotIn(String product, Collection<String> languages) {
        Query query = new Query(where("product").is(product).and("language").not().in(languages));
        Update update = new Update()
                .set("custom", null);
        template.findAndModify(query, update, Dictionary.class);
    }

    @Override
    public void save(Dictionary trans, boolean custom) {
        Dictionary t = getByLanguageAndProduct(trans.getLanguage(), trans.getProduct());

        if (t != null) {
            if (!custom && Utils.isMapUnchanged(trans.getLabels(), t.getLabels())) return;
            if (custom && Utils.isMapUnchanged(trans.getCustom(), t.getCustom())) return;
        }

        Query query = new Query(where("language").is(trans.getLanguage())
                .and("product").is(trans.getProduct()));

        Document doc = new Document();
        template.getConverter().write(trans, doc);
        Update update = Update.fromDocument(doc, "version", custom ? "labels" : "custom");

        template.upsert(query, update, Dictionary.class);
    }

    private Dictionary getByLanguageAndProduct(String language, String product) {
        Query query = new Query(where("language").is(language)
                .and("product").is(product));
        return template.findOne(query, Dictionary.class);
    }

    @Override
    public List<Dictionary> findBy(Collection<String> languages, Collection<String> products, Collection<String> labels) {
        boolean langSpecified = false;
        boolean langExists = false;

        // Check if any language specified and found
        if (languages != null) {
            if (languages.size() > 0) {
                langSpecified = true;
                for (String lang : template.getCollection(template.getCollectionName(Dictionary.class)).distinct("language", String.class)) {
                    if (languages.contains(lang)) {
                        langExists = true;
                        break;
                    }
                }
            }
        }

        Map<String, Dictionary> defaults = new HashMap<>();
        Criteria criteria = new Criteria();
        if (langExists) {
            criteria = where("language").in(languages);
            if (!languages.contains(Utils.caseString("en"))) {
                Criteria defaultCriteria = where("language").is("en");
                if (products != null && products.size() > 0) {
                    defaultCriteria = defaultCriteria.and("product").in(products);
                }
                Query query = new Query(defaultCriteria);
                if (labels != null && labels.size() > 0) {
                    query.fields().include("language", "product", "version");
                    for (String label : labels) {
                        query.fields().include("labels."+label, "custom."+label);
                    }
                }
                for (Dictionary dict : template.find(query, Dictionary.class)) {
                    defaults.put(dict.getProduct(), dict);
                }
            }
        } else if (langSpecified) {
            criteria = where("language").is(Utils.caseString("en"));
        }

        if (products != null && products.size() > 0) {
            criteria = criteria.and("product").in(products);
        }

        Query query = new Query(criteria);
        if (labels != null && labels.size() > 0) {
            query.fields().include("language", "product", "version");
            for (String label : labels) {
                query.fields().include("labels."+label, "custom."+label);
            }
        }
        List<Dictionary> result = new ArrayList<>();
        for (Dictionary dict : template.find(query, Dictionary.class)) {
            result.add(dict);
            defaults.remove(dict.getProduct());
        }
        result.addAll(defaults.values());
        return result;
    }

    public Dictionary getDefault(String product, Collection<String> labels) {
        Query query = new Query(where("product").is(product).and("language").is(Utils.caseString("en")));
        if (labels != null && labels.size() > 0) {
            query.fields().include("language", "product", "version");
            for (String label : labels) {
                query.fields().include("labels."+label, "custom."+label);
            }
        }
        return template.findOne(query, Dictionary.class);
    }
}