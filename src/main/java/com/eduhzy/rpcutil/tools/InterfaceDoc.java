package com.eduhzy.rpcutil.tools;

/**
 * 接口简单描述
 *
 * @author lewis ren
 * Created on 2018-11-27.
 */
public class InterfaceDoc {
    /**
     * 接口名字（如果是其他模块提供的接口请务必要填上）
     */
    private String interfaceName;

    /**
     * 方法名字（如果是当前使用RpcUtil的模块提供的接口，只需要填方法名，否则请填写其他模块的URL）
     */
    private String methodName;

    /**
     * 描述
     */
    private String desc;

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
