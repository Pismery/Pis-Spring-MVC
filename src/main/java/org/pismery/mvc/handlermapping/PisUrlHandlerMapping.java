package org.pismery.mvc.handlermapping;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.pismery.annotation.PisController;
import org.pismery.annotation.PisRequestMapping;
import org.pismery.mvc.PisApplicationContext;
import org.pismery.mvc.PisHandler;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class PisUrlHandlerMapping implements PisHandlerMapping {

    private final Map<String, PisHandler> handlerMapping = new HashMap<>();

    public PisUrlHandlerMapping(PisApplicationContext ctx) {
        ctx.getIoc().forEach((clazzName, instance) -> {
            Class<?> clazz = instance.getClass();
            if (clazz.isAnnotationPresent(PisController.class)) {
                StringBuilder url = new StringBuilder();

                if (clazz.isAnnotationPresent(PisRequestMapping.class)) {
                    PisRequestMapping clazzAnnotation = clazz.getAnnotation(PisRequestMapping.class);
                    url = new StringBuilder(clazzAnnotation.value());
                }

                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(PisRequestMapping.class)) {
                        PisRequestMapping methodAnnotation = method.getAnnotation(PisRequestMapping.class);
                        url.append(methodAnnotation.value());
                        PisHandler handler = new PisHandler(instance, method, url.toString());
                        handlerMapping.put(url.toString(), handler);
                    }
                }
            }
        });
    }

    @Override
    public PisHandler getHandler(HttpServletRequest req) {
        String url = req.getRequestURI();
        return handlerMapping.get(url);
    }
}
