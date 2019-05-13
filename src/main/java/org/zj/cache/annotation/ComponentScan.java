package org.zj.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @BelongsProject: cache
 * @BelongsPackage: org.zj.cache.annotation
 * @Author: ZhangJun
 * @CreateTime: 2019/5/13
 * @Description: 组件扫描
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ComponentScan {
    String packageName();
}
