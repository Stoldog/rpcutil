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
        int appId = rpcConfig.getAppId();
        String moduleName = rpcApi.moduleName();
        String serviceName = "".equals(rpcApi.serviceName()) ? rpcConfig.getServiceName() : rpcApi.serviceName();
        //1.配置基础属性
        RpcApiInfo info = new RpcApiInfo();
        info.setAppId(appId);
        info.setModuleName(moduleName);
        info.setServiceName(serviceName);
        info.setTitle(rpcApi.title());
        info.setDescription(rpcApi.description());
        info.setConfig(rpcConfig);
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
            //每个方法都持有公共的配置及其api
            //methodInfo.setConfig(rpcConfig);
            methodInfo.setApiInfo(info);

            List<RpcParamInfo> params = new ArrayList<>();
            //3.获取该方法得参数列表 有注解，则使用注解；没有注解则使用默认配置；
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
            methodInfo.setParamList(params);
            //4.获取返回值列表 todo: 后期可增加返回值列表
            //methodInfo.setReturnValues();
            methodInfo.setReturnJson(rpcMethod.returnJson());
            methodList.add(methodInfo);
        }
        info.setMethodInfos(methodList);
        return info;
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
