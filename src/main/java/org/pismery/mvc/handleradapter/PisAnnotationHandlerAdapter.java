package org.pismery.mvc.handleradapter;

import org.pismery.annotation.PisRequestName;
import org.pismery.mvc.PisModelAndView;
import org.pismery.mvc.PisHandler;
import org.pismery.mvc.util.ParameterNameUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;

public class PisAnnotationHandlerAdapter implements PisHandlerAdapter {

    @Override
    public PisModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Method method = ((PisHandler) handler).getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] paramValues = getParameterValues(request, response, method, parameters);

        Object mv= null;
        try {
            // 调用指定的 controller 方法
            mv = method.invoke(((PisHandler) handler).getController(), paramValues);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return (PisModelAndView) mv;
    }

    private Object[] getParameterValues(HttpServletRequest request, HttpServletResponse response, Method method, Parameter[] parameters) {
        Object[] paramValues = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Class<?> paramType = parameters[i].getType();
            //注入HttpServletRequest 和 HttpServletResponse
            if (paramType.equals(HttpServletRequest.class)) {
                paramValues[i] = request;
                continue;
            } else if (paramType.equals(HttpServletResponse.class)) {
                paramValues[i] = response;
                continue;
            }
            //注入 PisRequestName 注解的参数
            if (parameters[i].isAnnotationPresent(PisRequestName.class)) {
                PisRequestName annotation = parameters[i].getAnnotation(PisRequestName.class);
                String value = annotation.value();
                if (!"".equals(value)) {
                    paramValues[i] = getValueFromRequest(request, value);
                } else {
                    //Java 8 + 编译器参数 -parameters 方式获取参数名称
//                    String[] methodParameterNames = ParameterNameUtil.getMethodParameterNamesJava8(method);
                    //通过 ASM 获取参数名
//                    String[] methodParameterNames = ParameterNameUtil.getMethodParameterNamesByAsm5(method);
                    //通过 Javaassist 获取参数名
                    String[] methodParameterNames = ParameterNameUtil.getParameterNamesByJavaAssist(method);

                    paramValues[i] = getValueFromRequest(request, methodParameterNames[i]);
                }
            }
        }
        return paramValues;
    }

    private Object getValueFromRequest(HttpServletRequest request, String paramName) {
        Object result = null;
        Map<String, String[]> params = request.getParameterMap();
        for (Map.Entry<String, String[]> param : params.entrySet()) {
            if (param.getKey().equals(paramName)) {
                result = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll(",\\s", ",");
            }
        }
        return result;
    }
}
