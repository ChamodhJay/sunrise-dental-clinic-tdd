package com.icbt.service;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PasswordHasherTest {
    private final PasswordHasher hasher = new PasswordHasher();

    @Test
    public void generatedHashVerifiesOnlyTheOriginalPassword() {
        String encoded = hasher.hash("StrongPassword!".toCharArray());
        assertTrue(hasher.verify("StrongPassword!".toCharArray(), encoded));
        assertFalse(hasher.verify("WrongPassword!".toCharArray(), encoded));
    }

    @Test
    public void seededReceptionistPasswordMatchesSchemaHash() {
        String seedHash = "pbkdf2$120000$cmVjZXB0aW9uLXNlZWQwMQ==$"
                + "lzoN9GEuNxyuqj9Lb0bLw8ag+zGzjuUnqtCBafem6KU=";
        assertTrue(hasher.verify("Reception@123".toCharArray(), seedHash));
        assertFalse(hasher.verify("reception@123".toCharArray(), seedHash));
        assertTrue(hasher.verify("Dentist@123".toCharArray(),
                "pbkdf2$120000$ZGVudGlzdC1zZWVkMDAwMQ==$zKmap+JKr/EpYdZKnq/vMr8H2wTOzj/JTe5N+kY/+7o="));
        assertTrue(hasher.verify("Manager@123".toCharArray(),
                "pbkdf2$120000$bWFuYWdlci1zZWVkMDAwMQ==$3ykvhzaog703PFA9ERGh9nZGoMIYgNV06EitF+oqaag="));
    }
}
