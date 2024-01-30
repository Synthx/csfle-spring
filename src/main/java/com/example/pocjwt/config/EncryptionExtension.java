package com.example.pocjwt.config;

import com.mongodb.client.model.vault.DataKeyOptions;
import com.mongodb.client.vault.ClientEncryption;
import lombok.RequiredArgsConstructor;
import org.springframework.data.spel.spi.EvaluationContextExtension;
import org.springframework.data.spel.spi.Function;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class EncryptionExtension implements EvaluationContextExtension {
    private final ClientEncryption clientEncryption;

    @Override
    public String getExtensionId() {
        return "mongocrypt";
    }

    @Override
    public Map<String, Function> getFunctions() {
        try {
            return Collections.singletonMap("keyId", new Function(getClass().getMethod("computeKeyId", String.class), this));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public String computeKeyId(String target) {
        final var options = new DataKeyOptions().keyAltNames(List.of(target));
        final var key = clientEncryption.getKeyByAltName(target);
        if (key == null) {
            final var dataKeyId = clientEncryption.createDataKey("local", options);
            return Base64.getEncoder().encodeToString(dataKeyId.getData());
        }

        final var dataKeyId = key.getBinary("_id");
        return Base64.getEncoder().encodeToString(dataKeyId.getData());
    }
}
