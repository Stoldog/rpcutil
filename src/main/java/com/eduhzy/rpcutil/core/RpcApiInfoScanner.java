package com.eduhzy.rpcutil.core;

import com.alibaba.fastjson.JSON;
import com.eduhzy.rpcutil.annotations.RpcApi;
import com.eduhzy.rpcutil.annotations.RpcField;
import com.eduhzy.rpcutil.annotations.RpcMethod;
import com.eduhzy.rpcutil.annotations.RpcParam;
import com.eduhzy.rpcutil.tools.InstanceUtil;
import com.eduhzy.rpcutil.tools.StringUtil;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.alibaba.fastjson.serializer.SerializerFeature.*;


/**
 * @author lewis ren
 * @date 2018-11-15
 */
public class RpcApiInfoScanner implements ApiScanner<RpcApiInfo> {

    private static final int STRING_LENGTH = 50;

    private static final int LONG_LENGTH = 20;

    private static final int INT_LENGTH = 11;

    private static final String JSON_START = "{,]";

    private static final String JSON_END = "}";

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
            methodInfo.setApiInfo(info);
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
                    String json = getJsonSample(rpcParam.cls(), rpcParam.collectionType());
                    paramInfo.setDesc(json);
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


    /**
     * 获取  json  示例
     *
     * @param cls
     * @param collectionType
     * @return
     * @throws Exception
     */
    private String getJsonSample(Class cls, boolean collectionType) throws Exception {
        Object instance = InstanceUtil.newInstance(cls, collectionType);

        // 格式化 json
        String sample = JSON.toJSONString(instance,
                PrettyFormat, WriteMapNullValue,
                WriteNullNumberAsZero, WriteNullListAsEmpty,
                WriteNullStringAsEmpty, WriteNullBooleanAsFalse);

        return putJsonFieldComment(cls, sample);
//        // 添加 json 注释
//        List<String> list = putJsonFieldComment(cls, sample);
//        // 重新组装 json
//        // TODO: 2018-11-30  如果需要改成功 java 7,此处需更改 api
//        sample = String.join("", list);
//        return sample;
    }


    /**
     * 添加 json 字段注释
     *
     * @param cls
     * @param sample jsonSample
     * @return jsonList
     */
    private String putJsonFieldComment(Class cls, String sample) throws Exception {

        Field[] fields = cls.getDeclaredFields();

        byte[] bytes = sample.getBytes("utf-8");
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)));
        StringBuilder builder = new StringBuilder();
        String line;
        Stack<String> stack = new Stack<>();
        while ((line = reader.readLine()) != null) {
            String noBlankStr = line.trim();
            if (noBlankStr.equals("[") || noBlankStr.equals("{")) {
                stack.push("");
                builder.append(line).append("\r\n");
            } else if ("false".equals(noBlankStr)) {
                builder.append(line).append("\r\n");
            } else if (noBlankStr.contains("}") || noBlankStr.contains("]")) {
                // 删除父节点
                builder.append(line).append("\r\n");
                stack.pop();
            } else if (noBlankStr.length() > 2 && (noBlankStr.contains("[") || noBlankStr.contains("{"))) {
                // 添加父节点
                String key = noBlankStr.split("\"")[1];
                builder.append(line).append("\r\n");
                stack.push(key);
            } else {
                AtomicInteger count = new AtomicInteger(0);
                String parentKey = findRecentlyNode(stack, noBlankStr, count);
                pushFieldComment(fields, builder, line, parentKey);
                for (int i = 0; i < count.get(); i++) {
                    stack.push("");
                }
                stack.push(parentKey);
            }
        }

        return builder.toString();
    }

    // TODO: 2018-11-30 有bug
    
    private String findRecentlyNode(Stack<String> stack, String noBlankStr, AtomicInteger count) {
        String parentKey = "";

        int size = stack.size();
        for (int i = 0; i < stack.size(); i++) {
            String a = stack.pop();
            count.incrementAndGet();
            if (Objects.equals(a, "")) {
                continue;
            }
            parentKey = a;
            break;
        }
        String s = noBlankStr.split("\"")[1];
        if ("".equals(parentKey)) {
            return s;
        } else if (count.get() ==size ) {
            return s;
        } else {
            return parentKey;
        }
    }

    private void pushFieldComment(Field[] fields, StringBuilder builder, String line, String parentKey) {
        for (Field field : fields) {
            if (Objects.equals(field.getName(), parentKey)) {
                RpcField annotation = field.getAnnotation(RpcField.class);

                if (annotation == null) {
                    return;
                }
                Class paramClass = annotation.paramClass();
                String str = "//" + annotation.description();
                if (paramClass != null && paramClass != Object.class) {
//                    builder.append(StringUtil.fillBlank(line, 25) + str).append("\r\n");
                    pushFieldComment(paramClass.getDeclaredFields(), builder, line, line.split("\"")[1]);
                } else {
                    builder.append(StringUtil.fillBlank(line, 25) + str).append("\r\n");
                }
            }
        }
    }

    /**
     * 添加 方法 返回值
     *
     * @param rpcMethod
     * @param methodInfo
     */
    private void putMethodReturnValue(RpcMethod rpcMethod, RpcMethodInfo methodInfo) {
        Class clazz = rpcMethod.returnClass();
        if (clazz != Object.class) {
            try {
                String json = getJsonSample(clazz, rpcMethod.collectionType());
                methodInfo.setReturnJson(json);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            methodInfo.setReturnJson(rpcMethod.returnJson());
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
