package com.eduhzy.rpcutil.tools;

import java.util.List;

/**
 * 原型文档配置
 *
 * @author lewis ren
 * Created on 2018-11-27.
 */
public class PrototypeDoc {
    /**
     * 原型编号（产品给出）
     */
    private String prototypeNo;

    /**
     * 原型地址
     */
    private String prototypeURL;

    /**
     * 原型名字
     */
    private String prototypeName;

    /**
     * 该原型所用到的接口列表
     */
    private List<InterfaceDoc> interfaceDocList;

    public String getPrototypeNo() {
        return prototypeNo;
    }

    public void setPrototypeNo(String prototypeNo) {
        this.prototypeNo = prototypeNo;
    }

    public String getPrototypeURL() {
        return prototypeURL;
    }

    public void setPrototypeURL(String prototypeURL) {
        this.prototypeURL = prototypeURL;
    }

    public String getPrototypeName() {
        return prototypeName;
    }

    public void setPrototypeName(String prototypeName) {
        this.prototypeName = prototypeName;
    }

    public List<InterfaceDoc> getInterfaceDocList() {
        return interfaceDocList;
    }

    public void setInterfaceDocList(List<InterfaceDoc> interfaceDocList) {
        this.interfaceDocList = interfaceDocList;
    }
}
