package com.ailleron.translation.service;

import com.ailleron.translation.api.dto.*;
import com.ailleron.translation.model.Dictionary;
import com.ailleron.translation.repository.DictionaryRepository;
import com.ailleron.translation.repository.DictionaryRepositoryImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClients;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ImmutableMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.junit.jupiter.api.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.util.SocketUtils;
import org.springframework.mock.web.MockMultipartFile;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DictionaryServiceTest {

    private static final String CONNECTION_STRING = "mongodb://%s:%d";

    private MongodExecutable mongodExe;
    private MongodProcess mongod;
    private DictionaryRepository repository;
    private DictionaryService service;

    @AfterEach
    void clean() {
        if (this.mongod != null) {
            this.mongod.stop();
            this.mongodExe.stop();
        }
    }

    @BeforeEach
    void setup() throws Exception {
        String ip = "localhost";
        int randomPort = SocketUtils.findAvailableTcpPort();

        ImmutableMongodConfig mongodConfig = MongodConfig
                .builder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(ip, randomPort, Network.localhostIsIPv6()))
                .build();

        MongodStarter starter = MongodStarter.getDefaultInstance();
        mongodExe = starter.prepare(mongodConfig);
        mongod = mongodExe.start();
        MongoTemplate template = new MongoTemplate(MongoClients.create(String.format(CONNECTION_STRING, ip, randomPort)), "test");
        repository = new DictionaryRepositoryImpl(template);
        service = new DictionaryService(repository);
    }

    void prepareDatabase() {
        Dictionary a_pl = Dictionary.builder()
                .language("pl")
                .product("a")
                .labels(new HashMap<>(){{
                    put("label1", "a");
                    put("label2", "b");
                    put("label3", "c");
                }})
                .build();
        Dictionary a_en = Dictionary.builder()
                .language("en")
                .product("a")
                .labels(new HashMap<>(){{
                    put("label1", "a");
                    put("label2", "b");
                    put("label3", "c");
                }})
                .build();
        Dictionary b_pl = Dictionary.builder()
                .language("pl")
                .product("b")
                .labels(new HashMap<>(){{
                    put("label1", "a");
                    put("label2", "b");
                    put("label3", "c");
                }})
                .build();
        Dictionary b_en = Dictionary.builder()
                .language("en")
                .product("b")
                .labels(new HashMap<>(){{
                    put("label1", "a");
                    put("label2", "b");
                    put("label3", "c");
                }})
                .build();
        Dictionary c_pl = Dictionary.builder()
                .language("pl")
                .product("c")
                .labels(new HashMap<>(){{
                    put("label1", "a");
                    put("label2", "b");
                    put("label3", "c");
                }})
                .build();
        Dictionary c_en = Dictionary.builder()
                .language("en")
                .product("c")
                .labels(new HashMap<>(){{
                    put("label1", "a");
                    put("label2", "b");
                    put("label3", "c");
                }})
                .build();
        repository.save(a_pl, false);
        repository.save(a_en, false);
        repository.save(b_pl, false);
        repository.save(b_en, false);
        repository.save(c_pl, false);
        repository.save(c_en, false);
    }

    void prepareDatabaseWithOverrides() {
        Dictionary a_en_c = Dictionary.builder()
                .language("en")
                .product("a")
                .custom(new HashMap<>(){{
                    put("label1", "d");
                }})
                .build();
        repository.save(a_en_c, true);
        prepareDatabase();
    }

    void prepareDatabaseWithOverridesAndAdditionalLanguage() {
        Dictionary a_de_c = Dictionary.builder()
                .language("de")
                .product("a")
                .custom(new HashMap<>(){{
                    put("label1", "a2");
                    put("label2", "b2");
                    put("label3", "c2");
                }})
                .build();
        repository.save(a_de_c, true);
        prepareDatabaseWithOverrides();
    }

    @DisplayName("Get dictionaries for PL and EN languages")
    @Test
    void getDictionaries_EnglishAndPolish() {
        prepareDatabase();
        DictionariesDTO expected = DictionariesDTO.builder()
                .dictionaries(new HashMap<>(){{
                    put("a", TranslationsDTO.builder()
                            .values(new HashMap<>(){{
                                put("PL", DictionaryDTO.builder()
                                        .values(new HashMap<>(){{
                                            put("label1", "a");
                                            put("label2", "b");
                                            put("label3", "c");
                                        }})
                                        .version("1")
                                        .build());
                                put("EN", DictionaryDTO.builder()
                                        .values(new HashMap<>(){{
                                            put("label1", "a");
                                            put("label2", "b");
                                            put("label3", "c");
                                        }})
                                        .version("1")
                                        .build());
                            }})
                            .build());
                    put("b", TranslationsDTO.builder()
                            .values(new HashMap<>(){{
                                put("PL", DictionaryDTO.builder()
                                        .values(new HashMap<>(){{
                                            put("label1", "a");
                                            put("label2", "b");
                                            put("label3", "c");
                                        }})
                                        .version("1")
                                        .build());
                                put("EN", DictionaryDTO.builder()
                                        .values(new HashMap<>(){{
                                            put("label1", "a");
                                            put("label2", "b");
                                            put("label3", "c");
                                        }})
                                        .version("1")
                                        .build());
                            }})
                            .build());
                    put("c", TranslationsDTO.builder()
                            .values(new HashMap<>(){{
                                put("PL", DictionaryDTO.builder()
                                        .values(new HashMap<>(){{
                                            put("label1", "a");
                                            put("label2", "b");
                                            put("label3", "c");
                                        }})
                                        .version("1")
                                        .build());
                                put("EN", DictionaryDTO.builder()
                                        .values(new HashMap<>(){{
                                            put("label1", "a");
                                            put("label2", "b");
                                            put("label3", "c");
                                        }})
                                        .version("1")
                                        .build());
                            }})
                            .build());
                }})
                .build();

        DictionariesDTO result = service.getDictionaries(Arrays.asList("pl", "en"), null);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode expectedJson = mapper.convertValue(expected, JsonNode.class);
        JsonNode resultJson = mapper.convertValue(result, JsonNode.class);

        assertEquals(expectedJson, resultJson);
    }

    @DisplayName("Get 'a' and 'b' product dictionaries for PL and END languages")
    @Test
    void getDictionaries_EnglishAndPolishForAAndB() {
        prepareDatabase();
        DictionariesDTO expected = DictionariesDTO.builder()
                .dictionaries(new HashMap<>(){{
                    put("a", TranslationsDTO.builder()
                            .values(new HashMap<>(){{
                                put("PL", DictionaryDTO.builder()
                                        .values(new HashMap<>(){{
                                            put("label1", "a");
                                            put("label2", "b");
                                            put("label3", "c");
                                        }})
                                        .version("1")
                                        .build());
                                put("EN", DictionaryDTO.builder()
                                        .values(new HashMap<>(){{
                                            put("label1", "a");
                                            put("label2", "b");
                                            put("label3", "c");
                                        }})
                                        .version("1")
                                        .build());
                            }})
                            .build());
                    put("b", TranslationsDTO.builder()
                            .values(new HashMap<>(){{
                                put("PL", DictionaryDTO.builder()
                                        .values(new HashMap<>(){{
                                            put("label1", "a");
                                            put("label2", "b");
                                            put("label3", "c");
                                        }})
                                        .version("1")
                                        .build());
                                put("EN", DictionaryDTO.builder()
                                        .values(new HashMap<>(){{
                                            put("label1", "a");
                                            put("label2", "b");
                                            put("label3", "c");
                                        }})
                                        .version("1")
                                        .build());
                            }})
                            .build());
                }})
                .build();

        DictionariesDTO result = service.getDictionaries(Arrays.asList("pl", "en"), Arrays.asList("a", "b"));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode expectedJson = mapper.convertValue(expected, JsonNode.class);
        JsonNode resultJson = mapper.convertValue(result, JsonNode.class);

        assertEquals(expectedJson, resultJson);
    }

    @DisplayName("Get 'a' and 'b' product dictionaries versions for PL and EN languages")
    @Test
    void getVersions_EnglishAndPolishForAAndB() {
        prepareDatabase();
        DictionaryVersionsDTO expected = DictionaryVersionsDTO.builder()
                .dictionaries(new HashMap<>(){{
                    put("a", VersionDTO.builder()
                            .values(new HashMap<>(){{
                                put("PL", "1");
                                put("EN", "1");
                            }})
                            .build());
                    put("b", VersionDTO.builder()
                            .values(new HashMap<>(){{
                                put("PL", "1");
                                put("EN", "1");
                            }})
                            .build());
                }})
                .build();

        DictionaryVersionsDTO result = service.getVersions(Arrays.asList("pl", "en"), Arrays.asList("a", "b"));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode expectedJson = mapper.convertValue(expected, JsonNode.class);
        JsonNode resultJson = mapper.convertValue(result, JsonNode.class);

        assertEquals(expectedJson, resultJson);
    }

    @DisplayName("Get all dictionaries versions for PL and EN languages")
    @Test
    void getVersions_EnglishAndPolish() {
        prepareDatabase();
        DictionaryVersionsDTO expected = DictionaryVersionsDTO.builder()
                .dictionaries(new HashMap<>(){{
                    put("a", VersionDTO.builder()
                            .values(new HashMap<>(){{
                                put("PL", "1");
                                put("EN", "1");
                            }})
                            .build());
                    put("b", VersionDTO.builder()
                            .values(new HashMap<>(){{
                                put("PL", "1");
                                put("EN", "1");
                            }})
                            .build());
                    put("c", VersionDTO.builder()
                            .values(new HashMap<>(){{
                                put("PL", "1");
                                put("EN", "1");
                            }})
                            .build());
                }})
                .build();

        DictionaryVersionsDTO result = service.getVersions(Arrays.asList("pl", "en"), null);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode expectedJson = mapper.convertValue(expected, JsonNode.class);
        JsonNode resultJson = mapper.convertValue(result, JsonNode.class);

        assertEquals(expectedJson, resultJson);
    }

    @DisplayName("Get 'label1' and 'label2' labels for PL and EN languages")
    @Test
    void getLabels_EnglishAndPolishLabelsLabel1AndLabel2() {
        prepareDatabase();
        DictionaryVersionsDTO expected = DictionaryVersionsDTO.builder()
                .dictionaries(new HashMap<>(){{
                    put("PL", VersionDTO.builder()
                            .values(new HashMap<>(){{
                                put("label1", "a");
                                put("label2", "b");
                            }})
                            .build());
                    put("EN", VersionDTO.builder()
                            .values(new HashMap<>(){{
                                put("label1", "a");
                                put("label2", "b");
                            }})
                            .build());
                }})
                .build();

        DictionaryVersionsDTO result = service.getLabels("a",
                Arrays.asList("pl", "en"),
                Arrays.asList("label1", "label2"));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode expectedJson = mapper.convertValue(expected, JsonNode.class);
        JsonNode resultJson = mapper.convertValue(result, JsonNode.class);

        assertEquals(expectedJson, resultJson);
    }

    @DisplayName("Get dictionaries for PL and EN languages with overrides")
    @Test
    void getDictionaries_EnglishAndPolishWithOverrides() {
        prepareDatabaseWithOverrides();
        DictionariesDTO expected = DictionariesDTO.builder()
                .dictionaries(new HashMap<>(){{
                    put("a", TranslationsDTO.builder()
                            .values(new HashMap<>(){{
                                put("PL", DictionaryDTO.builder()
                                        .values(new HashMap<>(){{
                                            put("label1", "a");
                                            put("label2", "b");
                                            put("label3", "c");
                                        }})
                                        .version("1")
                                        .build());
                                put("EN", DictionaryDTO.builder()
                                        .values(new HashMap<>(){{
                                            put("label1", "d"); // Here label should be 'd'
                                            put("label2", "b");
                                            put("label3", "c");
                                        }})
                                        .version("2") // Here version should be incremented to 2
                                        .build());
                            }})
                            .build());
                    put("b", TranslationsDTO.builder()
                            .values(new HashMap<>(){{
                                put("PL", DictionaryDTO.builder()
                                        .values(new HashMap<>(){{
                                            put("label1", "a");
                                            put("label2", "b");
                                            put("label3", "c");
                                        }})
                                        .version("1")
                                        .build());
                                put("EN", DictionaryDTO.builder()
                                        .values(new HashMap<>(){{
                                            put("label1", "a");
                                            put("label2", "b");
                                            put("label3", "c");
                                        }})
                                        .version("1")
                                        .build());
                            }})
                            .build());
                    put("c", TranslationsDTO.builder()
                            .values(new HashMap<>(){{
                                put("PL", DictionaryDTO.builder()
                                        .values(new HashMap<>(){{
                                            put("label1", "a");
                                            put("label2", "b");
                                            put("label3", "c");
                                        }})
                                        .version("1")
                                        .build());
                                put("EN", DictionaryDTO.builder()
                                        .values(new HashMap<>(){{
                                            put("label1", "a");
                                            put("label2", "b");
                                            put("label3", "c");
                                        }})
                                        .version("1")
                                        .build());
                            }})
                            .build());
                }})
                .build();

        DictionariesDTO result = service.getDictionaries(Arrays.asList("pl", "en"), null);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode expectedJson = mapper.convertValue(expected, JsonNode.class);
        JsonNode resultJson = mapper.convertValue(result, JsonNode.class);

        assertEquals(expectedJson, resultJson);
    }

    @DisplayName("Get 'label1' and 'label2' labels for PL and EN languages with overrides")
    @Test
    void getLabels_EnglishAndPolishLabelsLabel1AndLabel2WithOverrides() {
        prepareDatabaseWithOverrides();
        DictionaryVersionsDTO expected = DictionaryVersionsDTO.builder()
                .dictionaries(new HashMap<>(){{
                    put("PL", VersionDTO.builder()
                            .values(new HashMap<>(){{
                                put("label1", "a");
                                put("label2", "b");
                            }})
                            .build());
                    put("EN", VersionDTO.builder()
                            .values(new HashMap<>(){{
                                put("label1", "d"); // Here label should be 'd'
                                put("label2", "b");
                            }})
                            .build());
                }})
                .build();

        DictionaryVersionsDTO result = service.getLabels("a",
                Arrays.asList("pl", "en"),
                Arrays.asList("label1", "label2"));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode expectedJson = mapper.convertValue(expected, JsonNode.class);
        JsonNode resultJson = mapper.convertValue(result, JsonNode.class);

        assertEquals(expectedJson, resultJson);
    }

    @DisplayName("Get dictionaries for non existing DE language with overrides")
    @Test
    void getDictionaries_NonExistingLanguageWithOverrides() {
        prepareDatabaseWithOverrides();
        DictionariesDTO expected = DictionariesDTO.builder()
                .dictionaries(new HashMap<>(){{
                    put("a", TranslationsDTO.builder()
                            .values(new HashMap<>(){{
                                put("EN", DictionaryDTO.builder()
                                        .values(new HashMap<>(){{
                                            put("label1", "d"); // Here label should be 'd'
                                            put("label2", "b");
                                            put("label3", "c");
                                        }})
                                        .version("2") // Here version should be incremented to 2
                                        .build());
                            }})
                            .build());
                    put("b", TranslationsDTO.builder()
                            .values(new HashMap<>(){{
                                put("EN", DictionaryDTO.builder()
                                        .values(new HashMap<>(){{
                                            put("label1", "a");
                                            put("label2", "b");
                                            put("label3", "c");
                                        }})
                                        .version("1")
                                        .build());
                            }})
                            .build());
                    put("c", TranslationsDTO.builder()
                            .values(new HashMap<>(){{
                                put("EN", DictionaryDTO.builder()
                                        .values(new HashMap<>(){{
                                            put("label1", "a");
                                            put("label2", "b");
                                            put("label3", "c");
                                        }})
                                        .version("1")
                                        .build());
                            }})
                            .build());
                }})
                .build();

        DictionariesDTO result = service.getDictionaries(List.of("de"), null);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode expectedJson = mapper.convertValue(expected, JsonNode.class);
        JsonNode resultJson = mapper.convertValue(result, JsonNode.class);

        assertEquals(expectedJson, resultJson);
    }

    @DisplayName("Get 'label1' and 'label2' labels for non existing DE language with overrides")
    @Test
    void getLabels_NonExistingLanguageLabelsLabel1AndLabel2WithOverrides() {
        prepareDatabaseWithOverrides();
        DictionaryVersionsDTO expected = DictionaryVersionsDTO.builder()
                .dictionaries(new HashMap<>(){{
                    put("EN", VersionDTO.builder()
                            .values(new HashMap<>(){{
                                put("label1", "d"); // Here label should be 'd'
                                put("label2", "b");
                            }})
                            .build());
                }})
                .build();

        DictionaryVersionsDTO result = service.getLabels("a",
                List.of("de"),
                Arrays.asList("label1", "label2"));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode expectedJson = mapper.convertValue(expected, JsonNode.class);
        JsonNode resultJson = mapper.convertValue(result, JsonNode.class);

        assertEquals(expectedJson, resultJson);
    }

    @DisplayName("Get dictionaries for DE language with overrides")
    @Test
    void getDictionaries_WithOverridesAndAdditionalLanguage() {
        prepareDatabaseWithOverridesAndAdditionalLanguage();
        DictionariesDTO expected = DictionariesDTO.builder()
                .dictionaries(new HashMap<>(){{
                    put("a", TranslationsDTO.builder()
                            .values(new HashMap<>(){{
                                // Here should be language DE
                                put("DE", DictionaryDTO.builder()
                                        .values(new HashMap<>(){{
                                            put("label1", "a2");
                                            put("label2", "b2");
                                            put("label3", "c2");
                                        }})
                                        .version("1")
                                        .build());
                            }})
                            .build());
                    put("b", TranslationsDTO.builder()
                            .values(new HashMap<>(){{
                                put("EN", DictionaryDTO.builder()
                                        .values(new HashMap<>(){{
                                            put("label1", "a");
                                            put("label2", "b");
                                            put("label3", "c");
                                        }})
                                        .version("1")
                                        .build());
                            }})
                            .build());
                    put("c", TranslationsDTO.builder()
                            .values(new HashMap<>(){{
                                put("EN", DictionaryDTO.builder()
                                        .values(new HashMap<>(){{
                                            put("label1", "a");
                                            put("label2", "b");
                                            put("label3", "c");
                                        }})
                                        .version("1")
                                        .build());
                            }})
                            .build());
                }})
                .build();

        DictionariesDTO result = service.getDictionaries(List.of("de"), null);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode expectedJson = mapper.convertValue(expected, JsonNode.class);
        JsonNode resultJson = mapper.convertValue(result, JsonNode.class);

        assertEquals(expectedJson, resultJson);
    }

    @DisplayName("Get 'label1' and 'label2' labels for DE language with overrides")
    @Test
    void getLabels_LabelsLabel1AndLabel2WithOverridesAndAdditionalLanguage() {
        prepareDatabaseWithOverridesAndAdditionalLanguage();
        DictionaryVersionsDTO expected = DictionaryVersionsDTO.builder()
                .dictionaries(new HashMap<>(){{
                    put("DE", VersionDTO.builder()
                            .values(new HashMap<>(){{
                                put("label1", "a2"); // Here label should be 'a2'
                                put("label2", "b2");
                            }})
                            .build());
                }})
                .build();

        DictionaryVersionsDTO result = service.getLabels("a",
                List.of("de"),
                Arrays.asList("label1", "label2"));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode expectedJson = mapper.convertValue(expected, JsonNode.class);
        JsonNode resultJson = mapper.convertValue(result, JsonNode.class);

        assertEquals(expectedJson, resultJson);
    }

    @DisplayName("Save multipart file dictionary for EN language")
    @Test
    void save_WithIgnoreCase() {
        String propertiesContent = String.join("\n",
                "label1=a",
                "LaBel2=b",
                "LaBel3=c");

        MockMultipartFile mpf = new MockMultipartFile("file", "a_en.properties",
                MediaType.TEXT_PLAIN_VALUE, propertiesContent.getBytes());

        service.save("eN", "aB", mpf);

        DictionariesDTO expected = DictionariesDTO.builder()
                .dictionaries(new HashMap<>(){{
                    put("ab", TranslationsDTO.builder()
                            .values(new HashMap<>(){{
                                // Here should be language DE
                                put("EN", DictionaryDTO.builder()
                                        .values(new HashMap<>(){{
                                            put("label1", "a");
                                            put("label2", "b");
                                            put("label3", "c");
                                        }})
                                        .version("1")
                                        .build());
                            }})
                            .build());
                }})
                .build();

        DictionariesDTO result = service.getDictionaries(List.of("En"), List.of("Ab"));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode expectedJson = mapper.convertValue(expected, JsonNode.class);
        JsonNode resultJson = mapper.convertValue(result, JsonNode.class);

        assertEquals(expectedJson, resultJson);
    }
}