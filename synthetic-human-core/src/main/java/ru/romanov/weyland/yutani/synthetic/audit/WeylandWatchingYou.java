package ru.romanov.weyland.yutani.synthetic.audit;

import ru.romanov.weyland.yutani.synthetic.utils.AuditLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WeylandWatchingYou {

    String description() default "";

    boolean includeParameters() default true;

    boolean includeResult() default true;

    AuditLevel level() default AuditLevel.STANDARD;
}
