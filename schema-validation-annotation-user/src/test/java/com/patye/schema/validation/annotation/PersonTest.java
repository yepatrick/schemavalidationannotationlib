package com.patye.schema.validation.annotation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PersonTest {
    @Test
    public void testValidatorValidAge() {
        Person goodPerson = new Person();
        goodPerson.setAge(7);
        PersonValidator.validate(goodPerson);
    }

    @Test
    public void testValidatorTooYoung() {
        Person tooYoungPerson = new Person();
        tooYoungPerson.setAge(0);
        Assertions.assertThrowsExactly(
            IllegalArgumentException.class,
            () -> PersonValidator.validate(tooYoungPerson)
        );
    }

    @Test
    public void testValidatorTooOld() {
        Person tooOldPerson = new Person();
        tooOldPerson.setAge(101);
        Assertions.assertThrowsExactly(
            IllegalArgumentException.class,
            () -> PersonValidator.validate(tooOldPerson)
        );
    }
}
