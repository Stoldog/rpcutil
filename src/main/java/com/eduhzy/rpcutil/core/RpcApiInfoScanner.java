package com.eduhzy.rpcutil.core;

import com.alibaba.fastjson.JSON;
import com.eduhzy.rpcutil.annotations.RpcApi;
import com.eduhzy.rpcutil.annotations.RpcMethod;
import com.eduhzy.rpcutil.annotations.RpcParam;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import static com.alibaba.fastjson.serializer.SerializerFeature.*;


/**
 * @author lewis ren
 * @date 2018-11-15
 */
public class RpcApiInfoScanner implements ApiScanner<RpcApiInfo> {

    public static final int STRING_LENGTH = 50;
    public static final int LONG_LENGTH = 20;
    public static final int INT_LENGTH = 11;

    @Override
    public RpcApiInfo scan(RpcConfig rpcConfig, Class<?> cls) {
        RpcApi rpcApi = AnnotationUtils.findAnnotation(cls, RpcApi.class);
        if (rpcApi == null) {
            return null;
        }
        int appId = rpcApi.appId();
        String moduleName = rpcApi.moduleName();
        String serviceName = rpcApi.serviceName().equals("") ? rpcConfig.getServiceName() : rpcApi.serviceName();
        //1.配置基础属性
        RpcApiInfo info = new RpcApiInfo();
        info.setAppId(appId);
        info.setModuleName(moduleName);
        info.setServiceName(serviceName);
        info.setTitle(rpcApi.title());
        info.setDescription(rpcApi.description());
        //2.获取所有的方法
        List<RpcMethodInfo> methodList = new ArrayList<>();
        for (Method method : cls.getDeclaredMethods()) {
            RpcMethod rpcMethod = AnnotationUtils.findAnnotation(method, RpcMethod.class);
            //为空跳过
            if (rpcMethod == null) {
                continue;
            }
            RpcMethodInfo methodInfo = new RpcMethodInfo();
            methodInfo.setAppId(appId);
            methodInfo.setServiceName(serviceName);
            methodInfo.setModuleName(moduleName);
            methodInfo.setApiName(rpcMethod.name());
            methodInfo.setMethodName(method.getName());
            methodInfo.setDescription(rpcMethod.description());
            methodInfo.setIsShow(rpcMethod.isShow() ? 1 : 0);
            methodInfo.setIsNeedAuth(rpcMethod.needAuth() ? 1 : 0);
            methodInfo.setIsNeedIP(rpcMethod.needIP() ? 1 : 0);

            //3.获取该方法得参数列表(有注解，则使用注解；没有注解则使用默认配置；
            List<RpcParamInfo> params = new ArrayList<>();
            putMethodParams(method, params);
            methodInfo.setParamList(params);

            // 添加返回值信息
            putMethodReturnValue(rpcMethod, methodInfo);

            methodList.add(methodInfo);
        }
        info.setMethodInfos(methodList);
        return info;
    }

    /**
     * 添加方法参数
     *
     * @param method
     * @param params
     */
    private void putMethodParams(Method method, List<RpcParamInfo> params) {
        for (Parameter parameter : method.getParameters()) {
            RpcParam rpcParam = AnnotationUtils.findAnnotation(parameter, RpcParam.class);
            RpcParamInfo paramInfo = new RpcParamInfo();
            paramInfo.setName(rpcParam != null ? rpcParam.name() : parameter.getName());
            paramInfo.setDesc(rpcParam != null ? rpcParam.description() : "");
            if (rpcParam != null && rpcParam.cls() != Object.class) {
                try {
                    Object instance = rpcParam.cls().newInstance();
                    paramInfo.setDesc(JSON.toJSONString(instance,
                            PrettyFormat,
                            WriteMapNullValue,
                            WriteNullNumberAsZero,
                            WriteNullListAsEmpty,
                            WriteNullStringAsEmpty,
                            WriteNullBooleanAsFalse));
                    paramInfo.setJsonObj(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //是否必填
            paramInfo.setIsTrue(rpcParam != null && rpcParam.isRequired() ? 1 : 0);
            paramInfo.setLength(rpcParam != null && rpcParam.length() != 0 ? rpcParam.length() : defaultLengthByClass(parameter.getType()));
            paramInfo.setSort(rpcParam != null && rpcParam.sort() != 0 ? rpcParam.sort() : params.size() + 1);
            paramInfo.setType(parameter.getType().getSimpleName());
            paramInfo.setTypeClass(parameter.getType());
            params.add(paramInfo);
        }
    }

    private void putMethodReturnValue(RpcMethod rpcMethod, RpcMethodInfo methodInfo) {
        Class clazz = rpcMethod.returnClass();
        if (clazz != Object.class) {
            try {
                if (rpcMethod.collectionType()) {
                    List list = new ArrayList();
                    Object o = newInstance(clazz);
                    list.add(o);
                    methodInfo.setReturnJson(JSON.toJSONString(list,
                            PrettyFormat,
                            WriteMapNullValue,
                            WriteNullNumberAsZero,
                            WriteNullListAsEmpty,
                            WriteNullStringAsEmpty,
                            WriteNullBooleanAsFalse));
                } else {
                    Object o = newInstance(clazz);
                    methodInfo.setReturnJson(JSON.toJSONString(o,
                            PrettyFormat,
                            WriteMapNullValue,
                            WriteNullNumberAsZero,
                            WriteNullListAsEmpty,
                            WriteNullStringAsEmpty,
                            WriteNullBooleanAsFalse));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            methodInfo.setReturnJson(rpcMethod.returnJson());
        }
    }


    /**
     * 返回值处理
     *
     * @param cls cls
     * @return object
     * @throws Exception exception
     */
    private Object newInstance(Class cls) throws Exception {
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
    private Object instance(Class cls) throws Exception {
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
            return cls.newInstance();
        }
    }

    /**
     * 默认参数长度
     *
     * @param cls
     * @return
     */
    private int defaultLengthByClass(Class cls) {
        if (cls == String.class) {
            return STRING_LENGTH;
        } else if (cls == Long.class || cls == long.class) {
            return LONG_LENGTH;
        } else if (cls == Integer.class || cls == int.class) {
            return INT_LENGTH;
        } else {
            return 16;
        }
    }
}
