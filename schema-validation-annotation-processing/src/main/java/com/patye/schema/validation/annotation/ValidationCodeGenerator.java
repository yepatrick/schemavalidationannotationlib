package com.patye.schema.validation.annotation;

import com.squareup.javapoet.CodeBlock;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ValidationCodeGenerator {
    public List<CodeBlock> generateCodeBlocks(String parameterName, String fieldName, Element fieldElement) {
        return getApplicableAnnotations().stream()
            .map(annoClazz -> fieldElement.getAnnotation(annoClazz))
            .filter(annoClazz -> annoClazz != null)
            .map(annoClazz -> generateCodeBlock(parameterName, fieldName, annoClazz))
            .filter(codeBlock ->  codeBlock.isPresent())
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    private Optional<CodeBlock> generateCodeBlock(String parameterName, String fieldName, Annotation annotation) {
        final String resolvedGetterName;
        final String getterOverrideName = getGetterOverrideName(annotation);
        if (getterOverrideName == null || getterOverrideName.isEmpty()) {
            resolvedGetterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        } else {
            resolvedGetterName = getterOverrideName;
        }

        final String getterExpression = parameterName + "." + resolvedGetterName + "()";

        return doGenerateCodeBlock(getterExpression, fieldName, annotation);
    }

    protected abstract Optional<CodeBlock> doGenerateCodeBlock(String getterExpression, String fieldName, Annotation annotation);

    protected abstract String getGetterOverrideName(Annotation annotation);

    protected abstract Set<Class<? extends Annotation>> getApplicableAnnotations();
}
