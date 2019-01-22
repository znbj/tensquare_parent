package com.tensquare.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.tensquare.user.pojo.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 数据访问接口
 * @author Administrator
 *
 */
public interface UserDao extends JpaRepository<User,String>,JpaSpecificationExecutor<User>{
	User findByMobile(String mobile);

	@Query("update User u set u.fanscount = u.fanscount + ?1 where u.id = ?2")
	@Modifying
	void updateFansCount(int count, String id);

	@Query("update User u set u.followcount = u.followcount + ?1 where u.id = ?2")
	@Modifying
	void updateFollowCount(int count, String id);

}
