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
    public static Object instance(Class cls) throws Exception {
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
            Object instance = cls.newInstance();

            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {

                RpcField annotation = field.getAnnotation(RpcField.class);
                if (annotation == null) {
                    continue;
                }
                // set 方法
                Class clazz = annotation.paramClass();

                if (clazz != null && clazz != Object.class) {
                    String name = "set_" + field.getName();
                    // 调用 set 方法
                    Object o = InstanceUtil.newInstance(clazz, annotation.collectionType());
                    Method method = cls.getMethod(StringUtil.toCamelCase(name), field.getType());
                    method.invoke(instance, o);
                }
            }
            return instance;
        }
    }

}
