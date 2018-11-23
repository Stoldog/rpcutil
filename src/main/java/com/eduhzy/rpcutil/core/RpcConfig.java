package com.eduhzy.rpcutil.core;

/**
 * 使用者的基础配置类，可由使用者自定义配置
 * Created by lewis ren on 2018-11-22.
 */
public class RpcConfig {
    /**
     * 扫描包路径
     */
    private String apiPackPath;

    /**
     * 文档生成路径
     */
    private String docPath;

    /**
     * 接口发布地址
     */
    private String apiHost;

    /**
     * 服务名
     */
    private String serviceName;

    /**
     * 包地址
     */
    private Package aPackage;

    public RpcConfig(String apiPackPath,String serviceName) {
        this.apiPackPath = apiPackPath;
        this.serviceName = serviceName;
        this.aPackage = Package.getPackage(apiPackPath);
    }

    public RpcConfig docPath(String docPath){
        this.docPath = docPath;
        return this;
    }

    public RpcConfig apiHost(String apiHost){
        this.apiHost = apiHost;
        return this;
    }

    public RpcConfig serviceName(String serviceName){
        this.apiHost = serviceName == null || serviceName.equals("") ? this.apiHost : serviceName;
        return this;
    }

    public RpcConfig aPackage(Package aPackage){
        this.aPackage = aPackage == null  ? this.aPackage : aPackage;
        return this;
    }

    public String getApiPackPath() {
        return apiPackPath;
    }

    public String getDocPath() {
        return docPath;
    }

    public String getApiHost() {
        return apiHost;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Package getAPackage() {
        return aPackage;
    }
}
