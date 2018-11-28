package com.eduhzy.rpcutil.core;

/**
 * 使用者的基础配置类，可由使用者自定义配置
 *
 * @author lewis ren
 * @date 2018-11-22
 */
public class RpcConfig {

    /**
     * 应用id
     */
    private int appId;

    /**
     * 扫描包路径
     */
    private String apiPackPath;

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

    /**
     * 接口文档生成路径
     */
    private String docPath;

    /**
     * 原型文档生成路径
     */
    private String protoTypeGenPath;

    /**
     * 原型所使用的接口文档地址
     */
    private String protoTypeInterfaceURL;

    /**
     * 原型的配置
     */
    private String protoTypeDocJson;

    public RpcConfig(String serviceName) {
        this.serviceName = serviceName;
    }

    public RpcConfig(String apiPackPath, String serviceName) {
        this.apiPackPath = apiPackPath;
        this.serviceName = serviceName;
        this.aPackage = Package.getPackage(apiPackPath);
    }

    public RpcConfig docPath(String docPath) {
        this.docPath = docPath;
        return this;
    }

    public RpcConfig apiHost(String apiHost) {
        this.apiHost = apiHost;
        return this;
    }

    public RpcConfig serviceName(String serviceName) {
        this.apiHost = serviceName == null || "".equals(serviceName) ? this.apiHost : serviceName;
        return this;
    }

    public RpcConfig aPackage(Package aPackage) {
        this.aPackage = aPackage == null ? this.aPackage : aPackage;
        return this;
    }

    public RpcConfig protoTypeGenPath(String protoTypeGenPath) {
        this.protoTypeGenPath = protoTypeGenPath;
        return this;
    }

    public RpcConfig protoTypeInterfaceURL(String protoTypeInterfaceURL) {
        this.protoTypeInterfaceURL = protoTypeInterfaceURL;
        return this;
    }

    public RpcConfig protoTypeDocJson(String protoTypeDocJson) {
        this.protoTypeDocJson = protoTypeDocJson;
        return this;
    }

    public RpcConfig appId(int appId) {
        this.appId = appId;
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

    public int getAppId() {
        return appId;
    }

    public String getProtoTypeGenPath() {
        return protoTypeGenPath;
    }

    public String getProtoTypeInterfaceURL() {
        return protoTypeInterfaceURL;
    }

    public String getProtoTypeDocJson() {
        return protoTypeDocJson;
    }
}
