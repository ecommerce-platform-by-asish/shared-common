package com.ecommerce.common.boot;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@SpringBootApplication
public @interface ECommBootApplication {

    @AliasFor(annotation = SpringBootApplication.class)
    Class<?>[] exclude() default {};

    @AliasFor(annotation = SpringBootApplication.class)
    String[] excludeName() default {};

    @AliasFor(annotation = SpringBootApplication.class)
    String[] scanBasePackages() default {};

    @AliasFor(annotation = SpringBootApplication.class)
    Class<?>[] scanBasePackageClasses() default {};
}
