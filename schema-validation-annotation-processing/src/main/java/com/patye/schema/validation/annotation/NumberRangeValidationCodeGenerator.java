package com.patye.schema.validation.annotation;

import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.CodeBlock;

import javax.swing.text.Element;
import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.Set;

/***
 * <pre>
 *     if (a.getMeow() >= b) {
 *         String message = "The field meow has the value of " + a.getMeow() + " which is not >= " + b;
 *
 *         throw new IllegalArgumentException(message);
 *     }
 * </pre>
 */
public class NumberRangeValidationCodeGenerator extends ValidationCodeGenerator {
    @Override
    protected Optional<CodeBlock> doGenerateCodeBlock(String getterExpression, String fieldName, Annotation annotation) {
        if (annotation.annotationType().equals(RealNumberMinValue.class)) {
            RealNumberMinValue minValue = (RealNumberMinValue) annotation;
            final double value = minValue.value();
            final String comparisonOperator = minValue.inclusive() ? "<" : "<=";
            final String violationOperator = minValue.inclusive() ? ">=" : ">";

            return Optional.of(CodeBlock.builder()
                .beginControlFlow("if (" + getterExpression + " " + comparisonOperator + " " + value + ")")
                .addStatement(generateExceptionMessage(getterExpression, fieldName, violationOperator, value))
                .addStatement("throw new IllegalArgumentException(message)")
                .endControlFlow()
                .build());
        } else if (annotation.annotationType().equals(RealNumberMaxValue.class)) {
            RealNumberMaxValue maxValue = (RealNumberMaxValue) annotation;
            final double value = maxValue.value();
            final String comparisonOperator = maxValue.inclusive() ? ">" : ">=";
            final String violationOperator = maxValue.inclusive() ? "<=" : "<";

            return Optional.of(CodeBlock.builder()
                .beginControlFlow("if (" + getterExpression + " " + comparisonOperator + " " + value + ")")
                .addStatement(generateExceptionMessage(getterExpression, fieldName, violationOperator, value))
                .addStatement("throw new IllegalArgumentException(message)")
                .endControlFlow()
                .build());
        }
        return Optional.empty();
    }

    private String generateExceptionMessage(String getterExpression, String fieldName, String operator, double value) {
        return "String message = \"The field \" + \"" + fieldName + "\""
            + " + \" has the value of \" + " + getterExpression
            + " + \" which violate the constraint of being  \" + \"" + operator + " \" + " + value;
    }

    @Override
    protected String getGetterOverrideName(Annotation annotation) {
        if (annotation.annotationType().equals(RealNumberMinValue.class)) {
            return ((RealNumberMinValue) annotation).getterName();
        } else if (annotation.annotationType().equals(RealNumberMaxValue.class)) {
            return ((RealNumberMaxValue) annotation).getterName();
        }
        return null;
    }

    @Override
    protected Set<Class<? extends Annotation>> getApplicableAnnotations() {
        return ImmutableSet.of(RealNumberMinValue.class, RealNumberMaxValue.class);
    }
}
