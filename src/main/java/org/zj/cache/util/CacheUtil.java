package org.zj.cache.util;

import org.zj.cache.annotation.Cacheable;
import org.zj.cache.annotation.ComponentScan;
import org.zj.cache.annotation.NeedCache;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: cache
 * @BelongsPackage: org.zj.cache.util
 * @Author: ZhangJun
 * @CreateTime: 2019/5/13
 * @Description: 缓存工具类
 */
public class CacheUtil {

    private static Map<Class,Object> instanceMap=new HashMap<Class, Object>();

    private static Map<String,Object> cacheMap=new HashMap<String, Object>();

    ComponentScan componentScan;
    private static List<Class> classes=new ArrayList<Class>();
    public static void initCache(Class clazz) {
        ComponentScan componentScan = (ComponentScan) clazz.getAnnotation(ComponentScan.class);
        if(componentScan==null){
            return;
        }
        String packageName = componentScan.packageName();
        System.out.println(packageName);
        inflateClass(packageName);
        initObject();
        scanClassAndSetCache();
    }

    /**
     * 初始化class
     */
    private static void initObject() {
        for(Class c:classes){
            if(c.isInterface()){
                continue;
            }
            try {
                instanceMap.put(c,c.newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static void scanClassAndSetCache() {
        for(Class c:classes){
            if(c.isAnnotationPresent(NeedCache.class)){
                for(Method m:c.getDeclaredMethods()){
                    if(m.isAnnotationPresent(Cacheable.class)){
                        Cacheable cacheable=m.getAnnotation(Cacheable.class);
                        //获得注解上的名字和刷新时间，然后开启定时任务
                        startTask(cacheable.name(),instanceMap.get(c),m,cacheable.frequency());
                    }
                }
            }
        }
    }

    /**
     * 扫描这个包下面的所有class
     * @param packageName
     */
    private static void inflateClass(String packageName) {
        // 是否循环搜索子包
        boolean recursive = true;
        // 包名对应的路径名称
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;

        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {

                URL url = dirs.nextElement();
                String protocol = url.getProtocol();

                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findClassInPackageByFile(packageName, filePath, recursive, classes);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 在package对应的路径下找到所有的class
     */
    public static void findClassInPackageByFile(String packageName, String filePath, final boolean recursive,
                                                List<Class> clazzs) {
        File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 在给定的目录下找到所有的文件，并且进行条件过滤
        File[] dirFiles = dir.listFiles(new FileFilter() {

            public boolean accept(File file) {
                boolean acceptDir = recursive && file.isDirectory();// 接受dir目录
                boolean acceptClass = file.getName().endsWith("class");// 接受class文件
                return acceptDir || acceptClass;
            }
        });

        for (File file : dirFiles) {
            if (file.isDirectory()) {
                findClassInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, clazzs);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    clazzs.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + "." + className));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 开启定时任务缓存方法返回值
     * @param name
     * @param obj
     * @param method
     * @param time
     */
    private static void startTask(final String name, final Object obj, final Method method, int time){
        try {
            //创建scheduler
            Runnable runnable=new Runnable() {
                public void run() {
                    try {
                        System.out.println("我执行了");
                        Object invoke = method.invoke(obj);
                        //把这个结果缓存起来
                        cacheMap.put(name,invoke);
                        System.out.println("我结束了，放进去了"+invoke);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleAtFixedRate(runnable,0,time, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
