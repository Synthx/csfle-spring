package com.example.pocjwt.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Encrypted;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document("users")
@Encrypted(keyId = "#{mongocrypt.keyId(#target)}")
public class UserEntity {
    @Id
    private String id;

    @Encrypted(algorithm = "AEAD_AES_256_CBC_HMAC_SHA_512-Random")
    private String name;

    private boolean active;
}
