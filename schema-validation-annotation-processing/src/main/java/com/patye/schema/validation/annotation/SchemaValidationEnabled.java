package com.patye.schema.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Data classes with this annotation will have additional code generated
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface SchemaValidationEnabled {
}
