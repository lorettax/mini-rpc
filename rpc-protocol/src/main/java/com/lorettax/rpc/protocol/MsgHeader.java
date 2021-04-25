package com.lorettax.rpc.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ljg
 */
@Data
public class MsgHeader implements Serializable {

    private short magic;    //魔数
    private byte version;   //协议版本号
    private byte serialization; //序列化算法
    private byte msgType;   //报文类型
    private byte status;    //状态
    private long requestId; //消息 ID
    private int msgLen; //数据长度

}
