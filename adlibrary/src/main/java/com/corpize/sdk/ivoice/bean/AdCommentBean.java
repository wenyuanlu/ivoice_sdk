package com.corpize.sdk.ivoice.bean;

import java.util.List;

/**
 * author: yh
 * date: 2020-02-17 22:01
 * description: TODO:
 */
public class AdCommentBean {
    /**
     * status : 200
     * upvote : 0
     * content : ["{\"avatar\":\"\",\"timestamp\":1608873005,\"content\":\"哈哈哈哈\",\"imei\":\"\",\"time\":0,\"userid\":\"\",\"oaid\":\"148481afb04be830\"}","{\"avatar\":\"\",\"timestamp\":1608873013,\"content\":\"哈海bill萨阿\",\"imei\":\"\",\"time\":0,\"userid\":\"\",\"oaid\":\"148481afb04be830\"}","{\"avatar\":\"\",\"timestamp\":1608873106,\"content\":\"这给这个，哈怕怕哈哈哈\",\"imei\":\"\",\"time\":0,\"userid\":\"\",\"oaid\":\"148481afb04be830\"}","{\"avatar\":\"\",\"timestamp\":1608862560,\"content\":\"哈哈哈哈哈哈啊哈\",\"imei\":\"\",\"time\":2,\"userid\":\"\",\"oaid\":\"148481afb04be830\"}","{\"avatar\":\"\",\"timestamp\":1608862970,\"content\":\"发送了弹幕啦\",\"imei\":\"\",\"time\":2,\"userid\":\"\",\"oaid\":\"148481afb04be830\"}","{\"avatar\":\"\",\"timestamp\":1608873124,\"content\":\"舒肤佳，12小时小时\",\"imei\":\"\",\"time\":5,\"userid\":\"\",\"oaid\":\"148481afb04be830\"}","{\"avatar\":\"\",\"timestamp\":1608863114,\"content\":\"哈哈还把还哈\",\"imei\":\"\",\"time\":7,\"userid\":\"\",\"oaid\":\"148481afb04be830\"}"]
     * contents : 7
     */
    private int status;
    private int upvote;
    private int contents;
    private List<AdCommentItemBean> content;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUpvote() {
        return upvote;
    }

    public void setUpvote(int upvote) {
        this.upvote = upvote;
    }

    public int getContents() {
        return contents;
    }

    public void setContents(int contents) {
        this.contents = contents;
    }

    public List<AdCommentItemBean> getContent() {
        return content;
    }

    public void setContent(List<AdCommentItemBean> content) {
        this.content = content;
    }
}
