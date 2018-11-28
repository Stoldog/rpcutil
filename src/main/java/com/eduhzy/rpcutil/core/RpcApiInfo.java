package com.eduhzy.rpcutil.core;

import java.util.List;

/**
 * API信息
 *
 * @author lewis ren
 * @date 2018-11-15
 */
public class RpcApiInfo {
    /**
     * 应用id
     */
    private int appId;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 模块名
     */
    private String moduleName;

    /**
     * 服务名
     */
    private String serviceName;

    /**
     * 方法列表
     */
    private List<RpcMethodInfo> methodInfos;

    /**
     * 包含有一些相关的配置信息
     */
    private RpcConfig config;

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public List<RpcMethodInfo> getMethodInfos() {
        return methodInfos;
    }

    public void setMethodInfos(List<RpcMethodInfo> methodInfos) {
        this.methodInfos = methodInfos;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RpcConfig getConfig() {
        return config;
    }

    public void setConfig(RpcConfig config) {
        this.config = config;
    }
}
