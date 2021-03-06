package com.eduhzy.rpcutil.tools;

import com.eduhzy.rpcutil.annotations.RpcField;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhongHG
 * @date 2018-11-30
 */
public class InstanceUtil {

    /**
     * 获取 apiMethod 实例
     *
     * @param cls
     * @return
     * @throws Exception
     */
    public static Object newInstance(Class cls, boolean collection) throws Exception {
        if (collection) {
            List list = new ArrayList();
            Object obj = instance(cls);
            list.add(obj);
            return list;
        }

        if (cls.isArray()) {
            Object instance = instance(cls.getComponentType());
            Object[] objects = new Object[1];
            objects[0] = instance;
            return objects;
        } else {
            return instance(cls);
        }
    }

    /**
     * 返回值处理
     *
     * @param cls cls
     * @return object
     * @throws Exception exception
     */
    public static <T> Object instance(Class<T> cls) throws Exception {
        if (cls == Integer.class || cls == Short.class || cls == Byte.class
                || cls == int.class || cls == short.class || cls == byte.class) {
            return 0;
        } else if (cls == Double.class || cls == Float.class || cls == double.class || cls == float.class) {
            return 0.0D;
        } else if (cls == Boolean.class || cls == boolean.class) {
            return false;
        } else if (cls == Long.class || cls == long.class) {
            return 0L;
        } else if (cls == Character.class || cls == char.class) {
            return "";
        } else {

            T instance = cls.newInstance();
            putSomeFieldsValue(cls, instance);
            return instance;
        }
    }


    /**
     * put 部分 javaBean 中集合类型属性的值,
     * 当 javaBean 中都为基础类型(包装类型)时,该方法则无属性满足下述条件
     *
     * @param cls      class
     * @param instance instance
     * @param <T>      T
     * @throws Exception exception
     */
    @SuppressWarnings("unchecked")
    private static <T> void putSomeFieldsValue(Class<T> cls, T instance) throws Exception {
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            RpcField annotation = field.getAnnotation(RpcField.class);

            if (annotation != null && annotation.paramClass() != Object.class) {
                Class fieldClass = annotation.paramClass();
                T fieldInstance;

                if (fieldClass == cls) {
                    fieldInstance = (T) fieldClass.newInstance();
                } else {
                    fieldInstance = (T) newInstance(fieldClass, annotation.collectionType());
                }

                String methodName = StringUtil.toCamelCase("set_" + field.getName());
                Method method = cls.getMethod(methodName, field.getType());
                method.invoke(instance, fieldInstance);
            }
        }
    }
}
