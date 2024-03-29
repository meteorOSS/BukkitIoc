package com.meteor.bukkitioc.bukkit;

import com.meteor.bukkitioc.annotation.ComponentScan;
import com.meteor.bukkitioc.context.AnnotationConfigApplicationContext;
import com.meteor.bukkitioc.context.ApplicationContext;
import com.meteor.bukkitioc.io.PropertyResolver;
import lombok.AllArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class PluginListener implements Listener {


    private Map<JavaPlugin,ApplicationContext> applicationContextMap = new ConcurrentHashMap<>();

    public static ClassLoader getClassLoader(JavaPlugin plugin) {
        try {
            Field classLoaderField = JavaPlugin.class.getDeclaredField("classLoader");
            classLoaderField.setAccessible(true);
            ClassLoader pluginClassLoader = (ClassLoader) classLoaderField.get(plugin);
            return pluginClassLoader;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent disableEvent){
        Plugin plugin = disableEvent.getPlugin();
        if(applicationContextMap.containsKey((JavaPlugin) plugin)){
            ApplicationContext applicationContext = applicationContextMap.get((JavaPlugin) plugin);
            applicationContext.close();
            applicationContextMap.remove((JavaPlugin) plugin);
        }
    }


    @EventHandler
    public void onPluginEnable(PluginEnableEvent pluginEnableEvent){
        Plugin plugin = pluginEnableEvent.getPlugin();
        List<String> depend = plugin.getDescription().getDepend();
        if(depend.contains("BukkitIoc")){

            ClassLoader classLoader = null;
            while (classLoader==null){
                classLoader = getClassLoader((JavaPlugin) plugin);
            }

            // 如果包含了ComponentScan注解
            if(plugin.getClass().getAnnotation(ComponentScan.class)!=null){

                FileConfiguration config = plugin.getConfig();
                Properties properties = new Properties();
                Map<String,Object> objectMap = new HashMap<>();
                for (String key : config.getKeys(true)) {
                    objectMap.put(key,config.get(key));
                }
                properties.putAll(objectMap);
                AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(plugin.getClass(),new PropertyResolver(properties),classLoader);
                if(BukkitIoc.debug){
                    System.out.println("托管自" + plugin.getName() + " 的Bean:");
                    applicationContext.getBeans().keySet().forEach(System.out::println);
                }
                BukkitIoc.getPlugin(BukkitIoc.class).getLogger().info("已托管来自 " + plugin.getName()+" 的 "+applicationContext.getBeans().size()+" 个Bean");
                applicationContextMap.put((JavaPlugin) plugin,applicationContext);

            }

        }

    }



}
