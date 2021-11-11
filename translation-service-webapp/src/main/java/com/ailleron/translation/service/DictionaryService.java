package com.ailleron.translation.service;

import com.ailleron.translation.api.dto.*;
import com.ailleron.translation.model.Dictionary;
import com.ailleron.translation.repository.DictionaryRepository;
import com.ailleron.translation.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class DictionaryService {
    private final DictionaryRepository repository;

    private final Pattern propertiesPattern = Pattern.compile("^([a-z]+).properties$");

    @Autowired
    public DictionaryService(DictionaryRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    private void loadCustom() {
        log.info("Loading custom dictionaries...");
        System.out.println("Loading custom dictionaries...");
        File customDir = new File("/dictionaries/custom");
        File[] customFiles = customDir.listFiles(File::isDirectory);
        if (customFiles == null) return;
        for (File productDir : customFiles) {
            String product = productDir.getName();
            Collection<String> languages = new ArrayList<>();
            File[] languageFiles = productDir.listFiles(File::isFile);
            if (languageFiles == null) break;
            for (File languageFile : languageFiles) {
                Matcher matcher = propertiesPattern.matcher(languageFile.getName());
                if (matcher.matches()) {
                    String language = Utils.caseString(matcher.group(1));
                    languages.add(language);

                    Map<String, String> custom = null;
                    try {
                        custom = Utils.readPropertiesFile(languageFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Dictionary dict = Dictionary.builder()
                            .language(language)
                            .product(product)
                            .custom(custom)
                            .build();
                    repository.save(dict, true);
                }
            }
            repository.clearCustomNotIn(product, languages);
        }
        repository.clear();
    }

    public void save(String language, String product, MultipartFile file) {
        // Transform to lower case
        language = Utils.caseString(language);
        product = Utils.caseString(product);

        Properties props = new Properties();
        try {
            props = new Properties();
            props.load(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Dictionary dict = Dictionary.builder()
                .language(language)
                .product(product)
                .labels(Utils.propertiesToMap(props))
                .build();
        repository.save(dict, false);
    }

    public DictionariesDTO getDictionaries(Collection<String> languages, Collection<String> products) {
        // Transform to lower case
        languages = Utils.caseCollection(Utils.nonEmptyCollection(languages));
        products = Utils.caseCollection(products);

        DictionariesDTO dicts = DictionariesDTO.builder().dictionaries(new HashMap<>()).build();

        for (Dictionary dict : repository.findBy(languages, products, null)) {
            Dictionary defaultDict = repository.getDefault(dict.getProduct(), null);

            if (!dicts.getDictionaries().containsKey(dict.getProduct())) {
                dicts.getDictionaries().put(dict.getProduct(), TranslationsDTO.builder().values(new HashMap<>()).build());
            }
            Map<String, DictionaryDTO> productMap = dicts.getDictionaries().get(dict.getProduct()).getValues();

            if (!productMap.containsKey(dict.getLanguage().toUpperCase())) {
                productMap.put(dict.getLanguage().toUpperCase(), DictionaryDTO.builder().values(new HashMap<>()).version(dict.getVersion().toString()).build());
            }

            // Load labels and merge with custom
            Map<String, String> values = productMap.get(dict.getLanguage().toUpperCase()).getValues();
            System.out.println("Default Labels " + defaultDict.getLabels());
            System.out.println("Default Custom " + defaultDict.getCustom());
            if (defaultDict.getLabels() != null) {
                values.putAll(defaultDict.getLabels());
            }
            if (defaultDict.getCustom() != null) {
                values.putAll(defaultDict.getCustom());
            }
            if (dict.getLabels() != null) {
                values.putAll(dict.getLabels());
            }
            if (dict.getCustom() != null) {
                values.putAll(dict.getCustom());
            }
        }
        return dicts;
    }

    public DictionaryVersionsDTO getLabels(String product, Collection<String> languages, Collection<String> labels) {
        // Transform to lower case
        languages = Utils.caseCollection(Utils.nonEmptyCollection(languages));
        labels = Utils.caseCollection(labels);
        product = Utils.caseString(product);

        DictionaryVersionsDTO dictVersions = DictionaryVersionsDTO.builder().dictionaries(new HashMap<>()).build();
        Dictionary defaultDict = repository.getDefault(product, labels);

        for (Dictionary dict : repository.findBy(languages, new ArrayList<>(List.of(product)), labels)) {
            String language = dict.getLanguage().toUpperCase();
            if (!dictVersions.getDictionaries().containsKey(language)) {
                // Load labels and merge with custom
                Map<String, String> values = new HashMap<>();
                if (defaultDict.getLabels() != null) {
                    values.putAll(defaultDict.getLabels());
                }
                if (defaultDict.getCustom() != null) {
                    values.putAll(defaultDict.getCustom());
                }
                if (dict.getLabels() != null) {
                    values.putAll(dict.getLabels());
                }
                if (dict.getCustom() != null) {
                    values.putAll(dict.getCustom());
                }
                dictVersions.getDictionaries().put(language, VersionDTO.builder().values(values).build());
            }
        }

        return dictVersions;
    }

    public DictionaryVersionsDTO getVersions(Collection<String> languages, Collection<String> products) {
        // Transform to lower case
        languages = Utils.caseCollection(Utils.nonEmptyCollection(languages));
        products = Utils.caseCollection(products);

        DictionaryVersionsDTO dictVersions = DictionaryVersionsDTO.builder().dictionaries(new HashMap<>()).build();
        for (Dictionary dict : repository.findBy(languages, products, null)) {
            if (!dictVersions.getDictionaries().containsKey(dict.getProduct())) {
                dictVersions.getDictionaries().put(dict.getProduct(), VersionDTO.builder().values(new HashMap<>()).build());
            }
            Map<String, String> versions = dictVersions.getDictionaries().get(dict.getProduct()).getValues();

            String language = dict.getLanguage().toUpperCase();
            versions.put(language, dict.getVersion().toString());
        }

        return dictVersions;
    }
}
