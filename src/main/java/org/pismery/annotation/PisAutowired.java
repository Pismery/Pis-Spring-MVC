package org.pismery.annotation;


import java.lang.annotation.*;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PisAutowired {
    String value() default "";
}
