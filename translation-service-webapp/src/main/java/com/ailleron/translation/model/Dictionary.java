package com.ailleron.translation.model;

import lombok.Builder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.Objects;

@Builder
@Document("dictionary")
@CompoundIndexes({
        @CompoundIndex(name = "product_language_idx", unique = true, def = "{'language' : 1, 'product' : 1}")
})
public class Dictionary {
    @Id
    private ObjectId id;
    @Indexed
    private String language;
    @Indexed
    private String product;
    @Version
    private Long version = 1L;

    private Map<String, String> labels;
    private Map<String, String> custom;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getLanguage() {
        return language.toLowerCase();
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    public Map<String, String> getCustom() {
        return custom;
    }

    public void setCustom(Map<String, String> custom) {
        this.custom = custom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dictionary that = (Dictionary) o;
        return language.equals(that.language) && product.equals(that.product) && version.equals(that.version) && Objects.equals(labels, that.labels) && Objects.equals(custom, that.custom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(language, product, version, labels, custom);
    }
}
