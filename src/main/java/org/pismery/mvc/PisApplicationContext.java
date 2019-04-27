package org.pismery.mvc;

import com.sun.istack.internal.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.pismery.annotation.PisAutowired;
import org.pismery.annotation.PisController;
import org.pismery.annotation.PisService;
import org.pismery.exception.MyException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

@Getter
@Setter
public class PisApplicationContext {
    private Map<String, Object> ioc = new HashMap<>();
    private List<String> clazzNames = new ArrayList<>();
    private Properties properties;


    public PisApplicationContext(String propertiesPath) {
        //1. 读取 application.properties
        properties = new Properties();
        ClassLoader classLoader = PisDispatcherServlet.class.getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(propertiesPath)) {
            properties.load(is);
            String basePath = properties.getProperty("scanPackage");
            //2. 获取所有类名
            scanPackage(basePath);
            //3. 反射初始化类
            initClazz();
            //4. 注入依赖类
            injectAutowired();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void injectAutowired() {
        ioc.forEach((clazzName, instance) -> {
            Field[] fields = instance.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(PisAutowired.class)) {
                    PisAutowired fieldAnnotation = field.getAnnotation(PisAutowired.class);
                    String value = fieldAnnotation.value().trim();
                    if ("".equals(value)) {
                        value = field.getType().getSimpleName();
                    }

                    field.setAccessible(true);
                    try {
                        field.set(instance, ioc.get(lowerFirstChar(value)));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void initClazz() {
        for (String clazzName : clazzNames) {
            try {
                Class<?> clazz = Class.forName(clazzName);
                if (clazz.isAnnotationPresent(PisController.class)) {
                    PisController controller = clazz.getAnnotation(PisController.class);
                    String value = controller.value();
                    if (!"".equals(value)) {
                        ioc.put(value, clazz.newInstance());
                        continue;
                    }
                    ioc.put(clazz.getCanonicalName(), clazz.newInstance());
                } else if (clazz.isAnnotationPresent(PisService.class)) {
                    PisService service = clazz.getAnnotation(PisService.class);
                    String value = service.value();
                    if (!"".equals(value)) {
                        ioc.put(value, clazz.newInstance());
                        continue;
                    }

                    Class<?>[] interfaces = clazz.getInterfaces();
                    if (null != interfaces && interfaces.length != 0) {
                        for (Class<?> interfaceClazz : interfaces) {
                            ioc.put(interfaceClazz.getSimpleName(), clazz.newInstance());
                        }
                    }
                }

            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }

        }
    }

    private void scanPackage(String basePath) {
        String urlPath = "/" + basePath.replace(".", "/");
        URL resource = this.getClass().getClassLoader().getResource(urlPath);
        if (resource == null) {
            throw new MyException("Base Path is not existed");
        }

        File file = new File(resource.getFile());
        for (File e : Objects.requireNonNull(file.listFiles())) {
            if (e.isDirectory()) {
                scanPackage(basePath + "." + e.getName());
            } else {
                this.clazzNames.add(basePath + "." + e.getName().replace(".class", "").trim());
            }
        }
    }

    private String lowerFirstChar(@NotNull String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
