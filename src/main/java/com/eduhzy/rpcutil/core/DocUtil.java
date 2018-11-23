package com.eduhzy.rpcutil.core;


import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * 文档生成（目前暂时只支持markdown）
 * Created by lewis ren on 2018-11-21.
 */
public class DocUtil {
    public static void generateMarkDownDoc(RpcApiInfo rpcApiInfo,String path,String interfacePath){
        path += StringUtils.endsWith(path,"/") ? "" : "/";//linux 暂时未考虑
        interfacePath += StringUtils.endsWith(interfacePath,"/") ? "" : "/";//linux 暂时未考虑
        Path file = Paths.get(path +rpcApiInfo.getTitle()+"_"+ rpcApiInfo.getModuleName()+"_接口文档.md");

        List<String> lines = new LinkedList<>();
        for (RpcMethodInfo methodInfo : rpcApiInfo.getMethodInfos()) {
            lines.add("## "+methodInfo.getApiName());
            lines.add("### 接口地址");
            lines.add("    " + interfacePath +rpcApiInfo.getServiceName()+"/"+rpcApiInfo.getModuleName()+"/"+methodInfo.getMethodName()+".jspx");
            lines.add("       ");
            lines.add("### 参数说明");
            lines.add("```");
            lines.add("参数");
            for (RpcParamInfo paramInfo : methodInfo.getParamList()) {
                lines.add(fillBlank(paramInfo.getName(),20) + fillBlank(paramInfo.getType(),20)+fillBlank(paramInfo.getDesc(),20));
            }
            lines.add("```");
            lines.add("       ");
            lines.add("### 返回值说明");
            lines.add("```");
            lines.add(methodInfo.getReturnJson());
            lines.add("```");
            lines.add("                           ");
        }
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String fillBlank(String str,int length){
        if(str.length() >= length){return str;}
        for (int i = 0; i < (length-str.length()); i++) {
            str += " ";
        }
        return str;
    }
}
