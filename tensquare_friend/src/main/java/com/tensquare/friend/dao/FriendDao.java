package com.tensquare.friend.dao;

import com.tensquare.friend.pojo.Friend;
import com.tensquare.friend.pojo.FriendPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendDao extends JpaRepository<Friend, FriendPK> {
}
