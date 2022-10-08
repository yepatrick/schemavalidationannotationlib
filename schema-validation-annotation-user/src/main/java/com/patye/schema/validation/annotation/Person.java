package com.patye.schema.validation.annotation;

import lombok.Getter;
import lombok.Setter;

@SchemaValidationEnabled
@Getter
@Setter
public class Person {
    @RealNumberMinValue(value = 1)
    @RealNumberMaxValue(value = 100)
    int age;
}
