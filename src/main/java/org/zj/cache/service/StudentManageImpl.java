package org.zj.cache.service;

import org.zj.cache.annotation.Cacheable;
import org.zj.cache.annotation.NeedCache;

/**
 * @BelongsProject: cache
 * @BelongsPackage: org.zj.cache.service
 * @Author: ZhangJun
 * @CreateTime: 2019/5/13
 * @Description: 学生管理实现类
 */
@NeedCache
public class StudentManageImpl implements IStudentManage {
    @Cacheable(name = "getName",frequency = 3000)
    public String getName() {
        return "我猜你不知道我是谁";
    }

    @Cacheable(name = "getAge",frequency = 1000)
    public String getAge() {
        return "我99岁了哦";
    }

}
