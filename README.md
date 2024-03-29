闲着无聊倒腾出来的东西，大概有点作用吧



平时写插件的时候，代码可能充斥着下面这坨东西



``` java

var permission = javaPlugin#getConfig("req-permission")

.......

```



这样重复度高的代码，你可以使用 BukkitIoc 干掉它！

BukkitIoc以注解代替这些繁琐的工作，支持以下注入方式



1. @Value(name="path") : 在yaml中读取



比如这样的yaml结构:

[CODE]shop:

  title: '商店'

  player: 20

[/CODE]

对应的组件实现



[CODE=java]import com.meteor.bukkitioc.annotation.Component;

import com.meteor.bukkitioc.annotation.Value;



@Component("shop")

public class Shop {



    @Value("shop.title")

    private String title;



    @Value("shop.player")

    private int player;

}[/CODE]



2. @Bean: 工厂方法



[CODE=java]@Configuration

public class ShopItemFactory {



    // 工厂方法注入

    @Bean(value = "fake_dick")

    public ShopItem getFakeDick(){

        return new ShopItem("fake_dick",20000,"a fake dick");

    }



}

[/CODE]



被@Configuration类中的@Bean注解方法所返回的对象，会被BukkitIoc全局唯一托管



3. @Autowired: 自动注入



当你要在其他地方使用的时候，只需要使用@Autowired 注入

[CODE=java]import com.meteor.bukkitioc.annotation.Autowired;

import com.meteor.bukkitioc.annotation.Configuration;

import com.meteor.bukkitiocexample.components.ShopItem;



@Configuration

public class BaseConfig {

    @Autowired(name = "fake_dick")

    public static ShopItem fakeDick;

}

[/CODE]



另外，也可以在构造器和set方法中使用注解注入



怎么使用?



在plugin.yml中加入 `depend: BukkitIoc` 后，对主类使用@ComponentScan("xxxx")注解，xxxx为ioc托管的作用域，建议为整个包。



如果你熟悉Spring的话,这个项目提供了仿Spring最小的IOC容器，你完全可以像使用Spring一样熟悉它

(请看AnnotationConfigApplicationContext类)
