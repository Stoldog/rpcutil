package com.eduhzy.rpcutil.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eduhzy.rpcutil.core.RpcApiInfo;
import com.eduhzy.rpcutil.core.RpcMethodInfo;
import com.eduhzy.rpcutil.core.RpcParamInfo;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 文档生成（目前暂时只支持markdown）
 *
 * @author lewis ren
 * @date 2018-11-21
 */
public class DocUtil {

    private static final String METHOD_LINK = "- [{{methodName}}](#{{methodName}})";

    private static final String PROTO_TYPE_METHOD_LINK = "[{{methodName}}]({{prototypePath}})";

    private static final String INTERFACE_LINK = "<tr><td>{{desc}}</td><td><a href=\"{{url}}\">{{name}}</td></tr>";

    private static final String HTTP_HEAD = "http://";

    private static final String HTTPS_HEAD = "https://";

    /**
     * 生成接口文档
     *
     * @param rpcApiInfo
     * @param path
     * @param interfacePath
     */
    public static void generateMarkDownDoc(RpcApiInfo rpcApiInfo, String path, String interfacePath) {
        //linux 暂时未考虑
        path += StringUtils.endsWith(path, "/") ? "" : "/";
        interfacePath += StringUtils.endsWith(interfacePath, "/") ? "" : "/";
        Path file = Paths.get(path + rpcApiInfo.getTitle() + "_" + rpcApiInfo.getModuleName() + "_接口文档.md");

        List<String> lines = new LinkedList<>();

        // 添加文件瞄点
        putMethodLink(rpcApiInfo, lines);

        for (RpcMethodInfo methodInfo : rpcApiInfo.getMethodInfos()) {
            // 添加方法信息
            putMethodInfo(rpcApiInfo, interfacePath, lines, methodInfo);

            // 添加方法参数
            putMethodParam(lines, methodInfo);

            // 添加方法返回值
            putMethodReturnValue(lines, methodInfo);

            // 添加api方法分界
            putMethodEndTag(lines);
        }

        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 此方法和公司开发流程耦合过重,且不清楚dec是否允许不同模块存在同名方法的问题，调用需谨慎
     * <p>
     * 根据 prototypeDoc列表 生成原型文档
     *
     * @param prototypeDocs
     * @param apiInfos
     * @param genDocPath
     * @param defaultInterfacePath
     */
    public static void generateMarkDownPrototypeListDoc(List<PrototypeDoc> prototypeDocs, List<RpcApiInfo> apiInfos,
                                                        String genDocPath, String defaultInterfacePath) {

        List<RpcMethodInfo> rpcMethodInfos = new ArrayList<>();
        //将扫描到的所有方法 合并到一个容器里
        apiInfos.stream()
                .forEach(rpcApiInfo -> rpcMethodInfos.addAll(rpcApiInfo.getMethodInfos()));

        //获取 方法名-方法 形式的map
        Map<String, RpcMethodInfo> rpcMethodInfoMap = rpcMethodInfos.stream()
                .collect(Collectors.toMap(RpcMethodInfo::getMethodName, Function.identity(), (oldValue, newValue) -> oldValue));

        //遍历配置文件(生成文档)
        prototypeDocs.stream()
                .forEach(doc -> generateMarkDownPrototypeDoc(doc, rpcMethodInfoMap, genDocPath, defaultInterfacePath));
    }

    /**
     * 根据 json格式的prototypeDoc列表 生成原型文档
     *
     * @param docListJson
     * @param apiInfos
     * @param genDocPath
     * @param defaultInterfacePath
     */
    public static void generateMarkDownPrototypeListDoc(String docListJson, List<RpcApiInfo> apiInfos,
                                                        String genDocPath, String defaultInterfacePath) {
        List<PrototypeDoc> docList = JSON.parseArray(docListJson, PrototypeDoc.class);
        generateMarkDownPrototypeListDoc(docList, apiInfos, genDocPath, defaultInterfacePath);
    }

    /**
     * 根据 prototypeDoc 生成原型文档
     *
     * @param doc
     * @param methodInfoMap
     * @param genDocPath
     * @param defaultInterfacePath
     */
    public static void generateMarkDownPrototypeDoc(PrototypeDoc doc, Map<String, RpcMethodInfo> methodInfoMap,
                                                    String genDocPath, String defaultInterfacePath) {
        List<String> lines = new LinkedList<>();
        Path file = Paths.get(genDocPath + "编号" + doc.getPrototypeNo() + "_" + doc.getPrototypeName() + "_页面接口文档.md");
        //生成原型信息
        putProtoInfoTag(lines, doc);
        //生成原型接口信息
        defaultInterfacePath = StringUtils.endsWith(defaultInterfacePath, "/") ? defaultInterfacePath : defaultInterfacePath + "/";
        putInterfaceInfoListTag(lines, doc, defaultInterfacePath, methodInfoMap);
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据 json格式的prototypeDoc 生成原型文档
     *
     * @param docJson
     * @param methodInfoMap
     * @param genDocPath
     * @param defaultInterfacePath
     */
    public static void generateMarkDownPrototypeDoc(String docJson, Map<String, RpcMethodInfo> methodInfoMap,
                                                    String genDocPath, String defaultInterfacePath) {
        PrototypeDoc prototypeDoc = JSONObject.parseObject(docJson, PrototypeDoc.class);
        generateMarkDownPrototypeDoc(prototypeDoc, methodInfoMap, genDocPath, defaultInterfacePath);
    }

    /**
     * 增加原型文档中 原型 信息
     *
     * @param lines
     * @param doc
     */
    private static void putProtoInfoTag(List<String> lines, PrototypeDoc doc) {
        lines.add("## 原型信息");
        lines.add("**原型编号:** " + (doc == null || doc.getPrototypeNo() == null ? "----" : doc.getPrototypeNo()) + "   ");
        String address = PROTO_TYPE_METHOD_LINK.replace("{{methodName}}", doc.getPrototypeName())
                .replace("{{prototypePath}}", doc.getPrototypeURL());
        lines.add("**原型地址:** " + address);
    }

    /**
     * 增加原型文档中 接口列表 信息
     *
     * @param lines
     * @param doc
     * @param defaultInterfacePath
     * @param map
     */
    private static void putInterfaceInfoListTag(List<String> lines, PrototypeDoc doc, String defaultInterfacePath, Map<String, RpcMethodInfo> map) {
        lines.add("## 接口列表");
        lines.add("<table>");
        lines.add("    <tr><td width=\"20%\">名字(作用)</td><td width=\"80%\">地址</td></tr>");
        for (InterfaceDoc interfaceDoc : doc.getInterfaceDocList()) {
            //添加单个接口信息
            lines.add(putInterfaceInfoTag(interfaceDoc, defaultInterfacePath, map));
        }
        lines.add("</table>");
    }

    /**
     * 增加原型文档中 单个接口 信息
     *
     * @param doc
     * @param defaultInterfacePath
     * @param map
     * @return
     */
    private static String putInterfaceInfoTag(InterfaceDoc doc, String defaultInterfacePath, Map<String, RpcMethodInfo> map) {
        String str = "  <tr><td>未设置接口URL</td><td>未设置接口URL</td></tr>";
        //为空的情况
        if (doc.getMethodName() == null) {
            return str;
        }

        //如果url是http开头 则为 第三方模块的接口
        if (StringUtils.startsWith(doc.getMethodName(), HTTP_HEAD) || StringUtils.startsWith(doc.getMethodName(), HTTPS_HEAD)) {
            str = "  " + INTERFACE_LINK.replace("{{desc}}", doc.getDesc() == null ? "未设置接口描述" : doc.getDesc())
                    .replace("{{name}}", doc.getInterfaceName() == null ? "未设置接口名" : doc.getInterfaceName())
                    .replace("{{url}}", doc.getMethodName());
            return str;
        }

        //如果是本地方法的接口
        RpcMethodInfo rpcMethodInfo = map.get(doc.getMethodName());
        if (rpcMethodInfo != null) {
            RpcApiInfo info = rpcMethodInfo.getApiInfo();
            str = INTERFACE_LINK.replace("{{desc}}", doc.getDesc() == null ? "未设置接口描述" : doc.getDesc())
                    .replace("{{name}}", rpcMethodInfo.getApiName().replaceAll(" ", ""))
                    .replace("{{url}}", defaultInterfacePath + info.getTitle() + "_" + info.getModuleName() + "_接口文档.md#" + rpcMethodInfo.getApiName().replaceAll(" ", ""));
        }
        return str;
    }

    /**
     * 天界方法分界
     *
     * @param lines
     */
    private static void putMethodEndTag(List<String> lines) {
        lines.add("<hr>");
        // 空行必须要,否则可能存在瞄点有误的情况(vs code存在)
        lines.add("");
    }

    /**
     * 添加方法返回值说明
     *
     * @param lines
     * @param methodInfo
     */
    private static void putMethodReturnValue(List<String> lines, RpcMethodInfo methodInfo) {
        lines.add("");
        lines.add("### 返回值说明");
        lines.add("```");
        lines.add(methodInfo.getReturnJson());
        lines.add("```");
    }

    /**
     * 添加方法接口说明
     *
     * @param rpcApiInfo
     * @param interfacePath
     * @param lines
     * @param methodInfo
     */
    private static void putMethodInfo(RpcApiInfo rpcApiInfo, String interfacePath, List<String> lines, RpcMethodInfo methodInfo) {
        lines.add("## " + methodInfo.getApiName().replaceAll(" ", ""));
        lines.add("### 接口地址");
        lines.add("    " + interfacePath + rpcApiInfo.getServiceName() + "/" + rpcApiInfo.getModuleName() + "/" + methodInfo.getMethodName() + ".jspx");
        lines.add("");
    }

    /**
     * 添加方法参数说明
     *
     * @param lines
     * @param methodInfo
     */
    private static void putMethodParam(List<String> lines, RpcMethodInfo methodInfo) {
        lines.add("### 参数说明");
        lines.add("```");
        lines.add("参数说明");
        for (RpcParamInfo paramInfo : methodInfo.getParamList()) {
            String line = StringUtil.fillBlank(paramInfo.getName(), 20) + StringUtil.fillBlank(paramInfo.getType(), 20);
            if (paramInfo.isJsonObj()) {
                lines.add(line + StringUtil.fillBlank("json 参数说明", 20));
                lines.add(StringUtil.fillBlank(paramInfo.getDesc(), 20));
            } else {
                lines.add(line + StringUtil.fillBlank(paramInfo.getDesc(), 20));
            }
        }
        lines.add("```");
    }

    /**
     * 添加文件 方法瞄点
     *
     * @param rpcApiInfo
     * @param lines
     */
    private static void putMethodLink(RpcApiInfo rpcApiInfo, List<String> lines) {
        lines.add("<!-- TOC -->");
        for (RpcMethodInfo methodInfo : rpcApiInfo.getMethodInfos()) {
            lines.add(METHOD_LINK.replace("{{methodName}}", methodInfo.getApiName().replaceAll(" ", "")));
        }
        lines.add("<!-- /TOC -->");
        lines.add("");
    }

}
