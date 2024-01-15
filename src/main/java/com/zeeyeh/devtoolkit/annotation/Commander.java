package com.zeeyeh.devtoolkit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Commander {

    /**
     * 是否拥有子命令
     */
    boolean son() default false;

    /**
     * 指令名称
     */
    String name();

    /**
     * 指令简介
     */
    String description() default "";

    /**
     * 指令别名
     */
    String[] aliases() default {};

    /**
     * 执行所需所有权限
     */
    String permission() default "";

    /**
     * 无权限时提示文本。默认: ${default}  - 表示使用原版默认
     * ${lang:noPermissionMessage}  - 表示使用语言文件中的noPermissionMessage语言配置项
     */
    String permissionMessage() default "${default}";

    /**
     * 指令的用法提示
     */
    String usage() default "";
}
