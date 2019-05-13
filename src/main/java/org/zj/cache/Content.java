package org.zj.cache;

import org.zj.cache.annotation.ComponentScan;
import org.zj.cache.util.CacheUtil;

/**
 * @BelongsProject: cache
 * @BelongsPackage: org.zj.cache
 * @Author: ZhangJun
 * @CreateTime: 2019/5/13
 * @Description:
 */
@ComponentScan(packageName = "org.zj.cache.service")
public class Content {
    public static void main(String[] args) {
        CacheUtil.initCache(Content.class);
    }
}
