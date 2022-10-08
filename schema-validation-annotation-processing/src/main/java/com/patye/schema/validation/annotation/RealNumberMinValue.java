package com.patye.schema.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Applicable to only members that represent real values.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface RealNumberMinValue {
    /**
     * The min value of the number.
     */
    double value();

    /**
     * Whether the mean value is inclusive or exclusive.
     */
    boolean inclusive() default  true;

    /**
     * The optional override name of the getter method. If not specified, will be {@code get<FieldName>}.
     */
    String getterName() default "";
}
