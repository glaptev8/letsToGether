package com.letstogether.authentication.annotation;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.JavaFile;
import org.springframework.javapoet.ParameterizedTypeName;
import org.springframework.javapoet.TypeSpec;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SupportedAnnotationTypes("com.letstogether.authentication.annotation.CreateRepository")
@Component
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class RepositoryGeneratorProcessor extends AbstractProcessor {
  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (Element element : roundEnv.getElementsAnnotatedWith(CreateRepository.class)) {
      if (element.getKind() == ElementKind.RECORD || element.getKind() == ElementKind.CLASS) {
        generateRepositoryForRecord((TypeElement) element);
      }
    }
    return true;
  }

  private void generateRepositoryForRecord(TypeElement element) {
    String packageName = processingEnv.getElementUtils().getPackageOf(element).getQualifiedName().toString();
    String name = element.getSimpleName().toString();
    String repositoryName = name + "Repository";

    TypeSpec repositoryInterface = TypeSpec.interfaceBuilder(repositoryName)
      .addSuperinterface(ParameterizedTypeName.get(
        ClassName.get("org.springframework.data.r2dbc.repository", "R2dbcRepository"),
        ClassName.get(packageName, name),
        ClassName.get(Long.class)
      ))
      .addModifiers(Modifier.PUBLIC)
      .build();

    JavaFile javaFile = JavaFile.builder(packageName, repositoryInterface)
      .build();

    try {
      javaFile.writeTo(processingEnv.getFiler());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
