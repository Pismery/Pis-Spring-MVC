package org.pismery.mvc.util;

import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import jdk.internal.org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class ParameterNameUtil {

    /**
     * 获取指定类指定方法的参数名
     *
     * @param method 要获取参数名的方法
     * @return 按参数顺序排列的参数名列表，如果没有参数，则返回null
     */
    public static String[] getMethodParameterNamesJava8(final Method method) {
        Parameter[] parameters = method.getParameters();
        if (parameters == null || parameters.length == 0) {
            return null;
        }
        String[] result = new String[parameters.length];
        for (int i = 0, length = parameters.length; i < length; i++) {
            result[i] = parameters[i].getName();
        }
        return result;
    }

    /**
     * 获取指定类指定方法的参数名
     *
     * @param method 要获取参数名的方法
     * @return 按参数顺序排列的参数名列表，如果没有参数，则返回null
     */
    public static String[] getMethodParameterNamesByAsm5(final Method method) {
        Class<?> clazz = method.getDeclaringClass();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes == null || parameterTypes.length == 0) {
            return null;
        }
        // 获取参数类型
        final Type[] types = new Type[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            types[i] = Type.getType(parameterTypes[i]);
        }

        final String[] parameterNames = new String[parameterTypes.length];

        String className = clazz.getSimpleName() + ".class";
        InputStream is = clazz.getResourceAsStream(className);
        try {
            ClassReader classReader = new ClassReader(is);
            classReader.accept(new ClassVisitor(Opcodes.ASM5) {
                //当访问方法时，调用以下重写方法
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    // 只处理指定的方法
                    Type[] argumentTypes = Type.getArgumentTypes(desc);
                    if (!method.getName().equals(name) || !Arrays.equals(argumentTypes, types)) {
                        return null;
                    }
                    return new MethodVisitor(Opcodes.ASM5) {
                        AtomicInteger integer = new AtomicInteger();

                        // 当访问方法参数时，调用以下重写方法
                        @Override
                        public void visitParameter(String name, int index) {
                            parameterNames[integer.getAndIncrement()] = name;
                        }
                    };

                }
            }, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parameterNames;
    }


    /**
     * 获取指定类指定方法的参数名
     *
     * @param method 要获取参数名的方法
     * @return 按参数顺序排列的参数名列表，如果没有参数，则返回null
     */
    public static String[] getParameterNamesByJavaAssist(Method method) {
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(method.getDeclaringClass()));
        String[] result = new String[method.getParameters().length];
        try {
            CtClass ctClass = classPool.get(method.getDeclaringClass().getName());
            CtMethod ctMethod = ctClass.getDeclaredMethod(method.getName());


            MethodInfo methodInfo = ctMethod.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute
                    .getAttribute(LocalVariableAttribute.tag);

            if (attr == null) {
                return null;
            }

            // 非静态的成员函数的第一个参数是this
            int pos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;
            for (int i = 0, len = ctMethod.getParameterTypes().length; i < len; i++) {
                result[i] = attr.variableName(i + pos);
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }


}
