package com.tensquare.friend.pojo;

import javax.persistence.Id;
import java.io.Serializable;

/**
 * fried表的主键，必须实现序列化接口。
 */
public class FriendPK implements Serializable{
    private String userid;
    private String friendid;

    public FriendPK(){

    }

    public FriendPK(String userid, String friendid) {
        this.userid = userid;
        this.friendid = friendid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFriendid() {
        return friendid;
    }

    public void setFriendid(String friendid) {
        this.friendid = friendid;
    }
}
