package com.patye.schema.validation.annotation;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes(
    "com.patye.schema.validation.annotation.SchemaValidationEnabled")
@AutoService(Processor.class)
@Slf4j
public class SchemaValidationEnabledProcessor extends AbstractProcessor {
    public static final List<ValidationCodeGenerator> CODE_GENERATOR_LIST = ImmutableList.of(
        new NumberRangeValidationCodeGenerator()
    );

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            for (TypeElement annotation : annotations) {
                log.info("annotation = {}", annotation);
                Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
                for (Element element : annotatedElements) {
                    log.info("Annotated element: {}, element kind = {}", element, element.getKind());
                    if (ElementKind.CLASS == element.getKind()) {
                        log.info("About to process class {}, with members {}, element type: {}",
                            element.getSimpleName(), element.getEnclosedElements(), element.getClass());

                        final String parameterName = element.getSimpleName().toString().toLowerCase();

                        MethodSpec.Builder validateMethodBuilder = MethodSpec.methodBuilder("validate")
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                            .addParameter(ParameterSpec.builder(TypeName.get(element.asType()), parameterName)
                                .addModifiers(Modifier.FINAL)
                                .build())
                            .returns(TypeName.VOID);

                        for (Element childElement : element.getEnclosedElements()) {
                            if (ElementKind.FIELD == childElement.getKind()) {
                                log.info("Field child element: {}", childElement.getAnnotation(RealNumberMinValue.class));
                                log.info("Field child element: {}", childElement.getAnnotation(Override.class));
                                List<CodeBlock> codeBlockList = CODE_GENERATOR_LIST.stream()
                                    .map(generator -> generator.generateCodeBlocks(parameterName, childElement.getSimpleName().toString(), childElement))
                                    .flatMap(Collection::stream)
                                    .collect(Collectors.toList());
                                for (CodeBlock codeBlock : codeBlockList) {
                                    validateMethodBuilder.addCode(codeBlock);
                                }
                            }
                        }

                        final String validatorClassName = element.getSimpleName() + "Validator";

                        TypeSpec validator = TypeSpec
                            .classBuilder(validatorClassName)
                            .addModifiers(Modifier.PUBLIC)
                            .addMethod(validateMethodBuilder.build())
                            .build();

                        final String packageName = element.getEnclosingElement().toString();
                        JavaFile javaFile = JavaFile.builder(packageName, validator)
                            .build();
                        JavaFileObject validateFile = processingEnv.getFiler().createSourceFile(packageName + "." + validatorClassName);
                        try (PrintWriter out = new PrintWriter(validateFile.openWriter())) {
                            out.println(javaFile.toString());
                        }
                    }
                }
            }
            return true;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }
}
