package com.meteor.bukkitioc.bukkit;


import com.meteor.bukkitioc.context.AnnotationConfigApplicationContext;
import com.meteor.bukkitioc.context.ApplicationContext;
import com.meteor.bukkitioc.io.PropertyResolver;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对插件的Bean上下文进行管理
 */
public class BukkitIocManager {

    private Map<JavaPlugin, ApplicationContext> applicationContextMap =
            new ConcurrentHashMap<>();

    public static BukkitIocManager bukkitIocManager;

    public static void init(){
        bukkitIocManager = new BukkitIocManager();
    }

    private ClassLoader getClassloader(JavaPlugin javaPlugin){
        try {
            Field classLoader = JavaPlugin.class.getDeclaredField("classLoader");
            classLoader.setAccessible(true);
            Object o = classLoader.get(javaPlugin);
            return (ClassLoader) o;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void register(JavaPlugin javaPlugin,Class<?> configClass){

        Properties properties = new Properties();
        Map<String,Object> objectMap = new HashMap<>();
        javaPlugin.getConfig().getKeys(true)
                        .forEach(
                                s->{
                                    objectMap.put(s,javaPlugin.getConfig().get(s));
                                }
                        );
        properties.putAll(objectMap);
        PropertyResolver propertyResolver = new PropertyResolver(properties);

        AnnotationConfigApplicationContext annotationConfigApplicationContext =
                new AnnotationConfigApplicationContext(configClass, propertyResolver, getClassloader(javaPlugin), javaPlugin);


        applicationContextMap.put(javaPlugin,annotationConfigApplicationContext);

    }


}
