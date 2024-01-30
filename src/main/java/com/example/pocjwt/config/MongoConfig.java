package com.example.pocjwt.config;

import com.example.pocjwt.dao.entity.UserEntity;
import com.mongodb.AutoEncryptionSettings;
import com.mongodb.ClientEncryptionSettings;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.vault.ClientEncryption;
import com.mongodb.client.vault.ClientEncryptions;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.MongoJsonSchemaCreator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MongoConfig {
    private static final String keyVaultDb = "encryption";
    private static final String keyVaultColl = "__keyVault";
    private static final String keyVaultNamespace = keyVaultDb + "." + keyVaultColl;

    private static final String db = "poc";

    @Value("${spring.data.mongodb.uri}")
    private String connectionString;

    @Bean
    MongoClientSettingsBuilderCustomizer customizer(MappingContext mappingContext) {
        return (builder) -> {
            final var schemaCreator = MongoJsonSchemaCreator.create(mappingContext);
            final var schema = schemaCreator
                    .filter(MongoJsonSchemaCreator.encryptedOnly())
                    .createSchemaFor(UserEntity.class);

            AutoEncryptionSettings autoEncryptionSettings;
            try {
                final Map<String, Object> options = Map.of(
                        "cryptSharedLibPath", "/Users/pinpin/Downloads/mongo_crypt_shared_v1-macos-arm64-enterprise-7.0.5/lib/mongo_crypt_v1.dylib"
                );

                autoEncryptionSettings = AutoEncryptionSettings.builder()
                        .keyVaultNamespace(keyVaultNamespace)
                        .kmsProviders(getKmsProviders())
                        .extraOptions(options)
                        .schemaMap(Collections.singletonMap(db + ".users", schema.schemaDocument().toBsonDocument()))
                        .build();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            builder.autoEncryptionSettings(autoEncryptionSettings);
        };
    }

    public Map<String, Map<String, Object>> getKmsProviders() throws IOException {
        byte[] localMasterKey = new byte[96];
        try (FileInputStream fis = new FileInputStream("master-key.txt")) {
            fis.readNBytes(localMasterKey, 0, 96);
        }

        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("key", localMasterKey);

        Map<String, Map<String, Object>> kmsProviders = new HashMap<>();
        kmsProviders.put("local", keyMap);

        return kmsProviders;
    }

    @Bean
    public ClientEncryption clientEncryption() throws IOException {
        final var settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .build();

        final var ces = ClientEncryptionSettings.builder()
                .keyVaultNamespace(keyVaultNamespace)
                .keyVaultMongoClientSettings(settings)
                .kmsProviders(getKmsProviders())
                .build();

        return ClientEncryptions.create(ces);
    }

    @Bean
    public EncryptionExtension encryptionExtension(ClientEncryption clientEncryption) {
        return new EncryptionExtension(clientEncryption);
    }

    // @PostConstruct
    public void setKeyVaultNamespace() {
        MongoClient keyVaultClient = MongoClients.create(connectionString);

        keyVaultClient.getDatabase(keyVaultDb).getCollection(keyVaultColl).drop();
        keyVaultClient.getDatabase(db).getCollection("users").drop();

        MongoCollection<Document> keyVaultCollection = keyVaultClient.getDatabase(keyVaultDb).getCollection(keyVaultColl);
        IndexOptions indexOpts = new IndexOptions().partialFilterExpression(new BsonDocument("keyAltNames", new BsonDocument("$exists", new BsonBoolean(true) ))).unique(true);
        keyVaultCollection.createIndex(new BsonDocument("keyAltNames", new BsonInt32(1)), indexOpts);
        keyVaultClient.close();
    }
}
