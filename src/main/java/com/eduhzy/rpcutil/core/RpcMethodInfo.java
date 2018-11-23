package com.eduhzy.rpcutil.core;

import java.util.List;

/**
 * 方法信息
 * Created by lewis ren on 2018-11-15.
 */
public class RpcMethodInfo {
    /**
     * 应用id
     */
    private int appId;

    /**
     * 模块名
     */
    private String moduleName;

    /**
     * 服务名
     */
    private String serviceName;

    /**
     * 接口名
     */
    private String apiName;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 接口描述
     */
    private String description;

    /**
     * 是否需要鉴权
     */
    private Integer isNeedAuth;

    /**
     * 是否需要客户端IP
     */
    private Integer isNeedIP;

    /**
     * 是否展示
     */
    private Integer isShow;

    /**
     * 参数列表
     */
    private List<RpcParamInfo> paramList;

    /**
     * 返回值列表
     */
    private List<RpcReturnValue> returnValues;

    /**
     * 返回值json
     */
    private String returnJson;

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

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getIsNeedAuth() {
        return isNeedAuth;
    }

    public void setIsNeedAuth(Integer isNeedAuth) {
        this.isNeedAuth = isNeedAuth;
    }

    public Integer getIsNeedIP() {
        return isNeedIP;
    }

    public void setIsNeedIP(Integer isNeedIP) {
        this.isNeedIP = isNeedIP;
    }

    public Integer getIsShow() {
        return isShow;
    }

    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }

    public List<RpcParamInfo> getParamList() {
        return paramList;
    }

    public void setParamList(List<RpcParamInfo> paramList) {
        this.paramList = paramList;
    }

    public List<RpcReturnValue> getReturnValues() {
        return returnValues;
    }

    public void setReturnValues(List<RpcReturnValue> returnValues) {
        this.returnValues = returnValues;
    }

    public String getReturnJson() {
        return returnJson;
    }

    public void setReturnJson(String returnJson) {
        this.returnJson = returnJson;
    }
}
