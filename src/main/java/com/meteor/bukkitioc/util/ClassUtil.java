package com.meteor.bukkitioc.util;

import com.meteor.bukkitioc.annotation.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 一些反射工具
 */
public class ClassUtil {


    /**
     * 注意，spring中如@Service等注解也被@Component二次注解
     * 那么在查找时，要递归查找当前候选注解是否被其他注解标注 (有点绕)
     * @param target
     * @param annotation
     * @return
     * @param <A>
     */
    public static <A extends Annotation> A findAnnotation(Class<?> target, Class<A> annoClass) {
        A a = target.getAnnotation(annoClass);
        for (Annotation anno : target.getAnnotations()) {
            Class<? extends Annotation> annoType = anno.annotationType();
            if (!annoType.getPackage().getName().equals("java.lang.annotation")) {
                A found = findAnnotation(annoType, annoClass);
                if (found != null) {
                    a = found;
                }
            }
        }
        return a;
    }


    public static String getBeanName(Method method){
        return method.getName();
    }

    public static String getBeanName(Class<?> aClass){

//        System.out.println("getBeanName" + aClass.getName());

        String name = null;
        Component annotation = aClass.getAnnotation(Component.class);
        if(annotation!=null) name = annotation.value();
        else {
            // 如果未找到@Component，则继续再其他注解中查找
            for (Annotation aClassAnnotation : aClass.getAnnotations()) {

                if(findAnnotation(aClassAnnotation.annotationType(), Component.class)!=null){
                    try {
                        name = (String)aClassAnnotation.getClass().getMethod("value").invoke(aClassAnnotation);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        }
        if(name == null || name.isEmpty()) {
            name = aClass.getSimpleName();
            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        }

//        System.out.println("返回: "+ name);
        return name;
    }


    /**
     * 查找给定集合是否拥有指定注解
     * @param aClass
     * @return
     */
    public static <A extends Annotation> A getAnnotation(Annotation[] annotations,Class<A> aClass){
        for (Annotation annotation : annotations) {
            if(aClass.isInstance(annotation)) return aClass.cast(annotation);
        }
        return null;
    }


    /**
     * 获取指定注解标注的方法
     * @param aClass
     * @param annotation
     * @return
     */
    private static Method findAnnotationMethod(Class<?> aClass,Class<? extends Annotation> annotation){
        List<Method> collect = Arrays.stream(aClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(annotation))
                .collect(Collectors.toList());

        if(collect.isEmpty()) return null;

        return collect.get(0);
    }

    /**
     * 获取init方法
     */
    public static Method getInitMethod(Class<?> aClass){
        return findAnnotationMethod(aClass, PostConstruct.class);
    }

    /**
     * 获取Destroy方法
     */
    public static Method getDestroyMethod(Class<?> aCalss){
        return findAnnotationMethod(aCalss, PreDestroy.class);
    }
}
