package com.tensquare.friend.service;

import com.tensquare.friend.dao.FriendDao;
import com.tensquare.friend.dao.NoFriendDao;
import com.tensquare.friend.pojo.Friend;
import com.tensquare.friend.pojo.FriendPK;
import com.tensquare.friend.pojo.NoFriend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FriendService {
    @Autowired
    private FriendDao friendDao;
    @Autowired
    private NoFriendDao noFriendDao;

    @Transactional
    public int addFirend(String userid, String friendid, String type) {
        //1）如果type是1，喜欢。向fried表插入数据
        if ("1".equals(type)) {
            //		2、如果好友已经添加，不能再次添加。返回0
            Optional<Friend> optionalFriend = friendDao.findById(new FriendPK(userid, friendid));
            if (optionalFriend.isPresent()) {
                //已经添加过好友不能重复添加
                return 0;
            }
            //		1、向friend表插入数据，islike应该是0
            Friend friend = new Friend();
            friend.setUserid(userid);
            friend.setFriendid(friendid);
            friend.setIslike("0");
            //把对象插入到数据库
            friendDao.save(friend);
            //		3、查询对方是否喜欢当前用户，如果对方也喜欢我，更新双方的islike为1
            Optional<Friend> optional = friendDao.findById(new FriendPK(friendid, userid));
            if (optional.isPresent()) {
                //对方也喜欢我，修改islike为1
                Friend other = optional.get();
                other.setIslike("1");
                friendDao.save(other);
                friend.setIslike("1");
                friendDao.save(friend);
            }

        }else {
            //	2）如果type是2，不喜欢。向nofriend表插入数据。
            NoFriend noFriend = new NoFriend();
            noFriend.setUserid(userid);
            noFriend.setFriendid(friendid);
            //		1、向nofriend表中插入数据。
            noFriendDao.save(noFriend);

        }
        //	3）返回1
        return 1;
    }

    @Transactional
    public int deleteFriend(String userid, String friendid) {
        //1、根据用户id、好友id，到friend表中查询数据。
        Optional<Friend> optional = friendDao.findById(new FriendPK(userid, friendid));
        if (!optional.isPresent()) {
            //好友不存在
            return 0;
        }
        Friend friend = optional.get();
        //	2、删除数据。
        friendDao.delete(friend);
        //	3、把好友添加到nofriend表中。
        NoFriend noFriend = new NoFriend();
        noFriend.setUserid(userid);
        noFriend.setFriendid(friendid);
        noFriendDao.save(noFriend);
        //	4、更新对方记录的islike为“0”
        Optional<Friend> optional1 = friendDao.findById(new FriendPK(friendid, userid));
        Friend other = optional1.get();
        other.setIslike("0");
        friendDao.save(other);
        return 1;
    }
}
