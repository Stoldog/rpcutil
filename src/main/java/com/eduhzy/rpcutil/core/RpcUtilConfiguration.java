package com.eduhzy.rpcutil.core;

import com.alibaba.fastjson.JSON;
import com.eduhzy.rpcutil.annotations.EnableRpcUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 主工作区
 * Created by lewis ren on 2018-11-15.
 */
@Configuration
public class RpcUtilConfiguration implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        //目前暂实现扫描包获取（从spring容器中获取会导致获取不到方法参数上的注解，暂时未找到解决办法）
//        Map<String, Object> beans = context.getBeansWithAnnotation(RpcApi.class);
        if(context.getBeansWithAnnotation(EnableRpcUtil.class).keySet().size()==0){
            return;
        }
        //todo: 该处可以做成可配置项 由使用者自定义扫描器
        ApiScanner scanner = new RpcApiInfoScanner();
        //注解获取类的每个方法注解
        List<RpcApiInfo> list = new ArrayList<>();
        RpcConfig rpcConfig = context.getBean(RpcConfig.class);
        //todo: 该处RpcConfig内 可做成数组 可扫描多个包
        Package aPackage = rpcConfig.getAPackage();
        if(aPackage == null){
            System.out.println("无扫描包路径");
            return;
        }
        for (Class<?> aClass : ClassUtil.getAllClassByPackageName(aPackage)) {
            //扫描
            RpcApiInfo apiInfo = (RpcApiInfo) scanner.scan(rpcConfig, aClass);
            if (apiInfo != null) {
                list.add(apiInfo);
            }
        }
        //todo: 该处可将数据导出、自动化发布（暂时注册中心未提供提交数据的方式）
        System.out.println("Rpc-Util: "+JSON.toJSON(list));
        //todo: 该处可以做成可配置项 由使用者自定义生成文档类型、结构等等
        if (rpcConfig.getDocPath() != null && !rpcConfig.getDocPath().equals(""))
            for (RpcApiInfo rpcApiInfo : list) {
                DocUtil.generateMarkDownDoc(rpcApiInfo, rpcConfig.getDocPath(), rpcConfig.getApiHost());
            }

    }
}
