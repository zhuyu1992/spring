package com.allweing.spring;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @auther: zzzgyu
 */

public class AllweingApplicationContext {

    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();
    //单例池
    private ConcurrentHashMap<String, Object> singletonObject = new ConcurrentHashMap<String, Object>();

    public AllweingApplicationContext(Class ConfigClass) {

        List<Class> classList = scan(ConfigClass);

        for (Class aClass : classList) {
            if (aClass.isAnnotationPresent(Component.class)) {
                BeanDefinition beanDefinition = new BeanDefinition();

                Component annotation1 = (Component) aClass.getAnnotation(Component.class);
                String beanName = annotation1.value();
                System.out.println(beanName);
                if (aClass.isAnnotationPresent(Scope.class)) {

                    Scope annotation = (Scope) aClass.getAnnotation(Scope.class);
                    beanDefinition.setScope(annotation.value());
                }else {
                    beanDefinition.setScope("singleton");
                }

                beanDefinition.setBeanClass(aClass);

                beanDefinitionMap.put(beanName, beanDefinition);
            }

        }

        //将单例bean 放到单例池中
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")) {

                Object beanClass = createBean(beanName,beanDefinition);


                singletonObject.put(beanName,beanClass);

            }
        }


        //扫描到这个类之后，解析这个类，Component ,BeanDefinition


        //生成单例bean-------->单例连接池

    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) {

        //生成bean


        Class beanClass = beanDefinition.getBeanClass();


        try {

            //实例化
            Object bean = beanClass.getDeclaredConstructor().newInstance();
            //填充属性 依赖注入

            Field[] declaredFields = beanClass.getDeclaredFields();
            if (declaredFields.length==0) {
                return bean;
            }
            for (Field field : declaredFields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Object bean1 = getBean(field.getName());  //byname 获得bean
                    field.setAccessible(true);
                    field.set(bean,bean1);
                    //依赖注入

                }
            }
            if (bean instanceof InitializingBean) {
                ((InitializingBean)bean).afterPropertiesSet();
            }
            if (bean instanceof BeanNameAware) {
                ((BeanNameAware)bean).setBeanName(beanName);
            }
            return bean;
            //Aware

            //初始化

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 扫描 添加class 到list
     *
     * @param ConfigClass
     * @return
     */
    private List<Class> scan(Class ConfigClass) {
        //扫描类
        /*if (ConfigClass.isAnnotationPresent(ComponentScan.class)) {

        }*/
        List<Class> classList = new ArrayList<Class>();

        ComponentScan annotation = (ComponentScan) ConfigClass.getAnnotation(ComponentScan.class);
        String scanPath = annotation.value();
        //System.out.println(scanPath);
        scanPath = scanPath.replace(".", "/");

        // 如何扫描类
        ClassLoader classLoader = AllweingApplicationContext.class.getClassLoader();
        URL resource = classLoader.getResource(scanPath);

        File file = new File(resource.getFile());  //目录
        //目录下的文件列表,可能是class,可能是txt
        File[] files = file.listFiles();

        for (File f : files) {

            //System.out.println(f);
            String absolutePath = f.getAbsolutePath();

            absolutePath = absolutePath.substring(absolutePath.indexOf("com"), absolutePath.indexOf(".class"));

            absolutePath = absolutePath.replace("\\", ".");
            //System.out.println(absolutePath);

            try {
                Class<?> aClass = classLoader.loadClass(absolutePath);
                /*if (aClass.isAnnotationPresent(Component.class)) {
                    Component annotation1 = aClass.getAnnotation(Component.class);
                    String beanName = annotation1.value();
                    System.out.println(beanName);
                }*/
                classList.add(aClass);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return classList;
    }

    public Object getBean(String beanName) {

        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition.getScope().equals("prototype")) {
            return createBean(beanName, beanDefinition);
        } else {
            Object o = singletonObject.get(beanName);
            if (o==null) {
                Object bean = createBean(beanName, beanDefinition);
                singletonObject.put(beanName,bean);
                return bean;

            }
            return o;
        }
    }
}
