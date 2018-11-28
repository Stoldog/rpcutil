package com.eduhzy.rpcutil.core;

import com.eduhzy.rpcutil.annotations.EnableRpcUtil;
import com.eduhzy.rpcutil.tools.ClassUtil;
import com.eduhzy.rpcutil.tools.DocUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 主工作区（与公司开发流程相关 可替换）
 *
 * @author lewis ren
 * Created on 2018-11-27.
 */
@Configuration
public class RpcUtilConfiguration implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        //目前暂实现扫描包获取（从spring容器中获取会导致获取不到方法参数上的注解，暂时未找到解决办法）
        if (context.getBeansWithAnnotation(EnableRpcUtil.class).keySet().size() == 0) {
            return;
        }
        //todo: 该处可以做成可配置项 由使用者自定义扫描器
        ApiScanner scanner = new RpcApiInfoScanner();
        //注解获取类的每个方法注解
        List<RpcApiInfo> list = new ArrayList<>();
        RpcConfig rpcConfig = context.getBean(RpcConfig.class);
        if(!checkInit(rpcConfig)){
            return;
        }
        //todo: 该处RpcConfig内 可做成数组 可扫描多个包
        Package aPackage = rpcConfig.getAPackage();
        for (Class<?> aClass : ClassUtil.getAllClassByPackageName(aPackage)) {
            //扫描
            RpcApiInfo apiInfo = (RpcApiInfo) scanner.scan(rpcConfig, aClass);
            if (apiInfo != null) {
                list.add(apiInfo);
            }
        }

        //todo: 该处可以做成可配置项 由使用者自定义生成文档类型、结构等等
        if (rpcConfig.getDocPath() != null && !"".equals(rpcConfig.getDocPath())) {
            for (RpcApiInfo rpcApiInfo : list) {
                DocUtil.generateMarkDownDoc(rpcApiInfo, rpcConfig.getDocPath(), rpcConfig.getApiHost());
            }
        }

        //判断是否需要生成原型接口
        if(rpcConfig.getProtoTypeDocJson() != null && !"".equals(rpcConfig.getProtoTypeDocJson())
                && rpcConfig.getProtoTypeGenPath() != null && !"".equals(rpcConfig.getProtoTypeGenPath())
                && rpcConfig.getProtoTypeInterfaceURL() != null && !"".equals(rpcConfig.getProtoTypeInterfaceURL())){

            DocUtil.generateMarkDownPrototypeListDoc(rpcConfig.getProtoTypeDocJson(),list,
                    rpcConfig.getProtoTypeGenPath(), rpcConfig.getProtoTypeInterfaceURL());
        }

    }

    private boolean checkInit(RpcConfig config){

        if (config.getServiceName() == null || "".equals(config.getServiceName())) {
            System.out.println("无服务名,终止扫描");
            return false;
        }

        if (config.getAPackage() == null) {
            System.out.println("无扫描包路径,终止扫描");
            return false;
        }

        if (config.getAppId() <= 0){
            System.out.println("无AppId,终止扫描");
            return false;
        }

        return true;
    }
}
