package com.tensquare.user.service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import entity.Result;
import entity.StatusCode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import util.IdWorker;

import com.tensquare.user.dao.UserDao;
import com.tensquare.user.pojo.User;

/**
 * 服务层
 * 
 * @author Administrator
 *
 */
@Service
public class UserService {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private IdWorker idWorker;
	@Autowired
	private RabbitTemplate rabbitTemplate;
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	/**
	 * 查询全部列表
	 * @return
	 */
	public List<User> findAll() {
		return userDao.findAll();
	}

	
	/**
	 * 条件查询+分页
	 * @param whereMap
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<User> findSearch(Map whereMap, int page, int size) {
		Specification<User> specification = createSpecification(whereMap);
		PageRequest pageRequest =  PageRequest.of(page-1, size);
		return userDao.findAll(specification, pageRequest);
	}

	
	/**
	 * 条件查询
	 * @param whereMap
	 * @return
	 */
	public List<User> findSearch(Map whereMap) {
		Specification<User> specification = createSpecification(whereMap);
		return userDao.findAll(specification);
	}

	/**
	 * 根据ID查询实体
	 * @param id
	 * @return
	 */
	public User findById(String id) {
		return userDao.findById(id).get();
	}

	/**
	 * 增加
	 * @param user
	 */
	public void add(User user) {
		user.setId( idWorker.nextId()+"" );
		userDao.save(user);
	}

	/**
	 * 修改
	 * @param user
	 */
	public void update(User user) {
		userDao.save(user);
	}

	/**
	 * 删除
	 * @param id
	 */
	public void deleteById(String id) {
		userDao.deleteById(id);
	}

	/**
	 * 动态条件构建
	 * @param searchMap
	 * @return
	 */
	private Specification<User> createSpecification(Map searchMap) {

		return new Specification<User>() {

			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
                // ID
                if (searchMap.get("id")!=null && !"".equals(searchMap.get("id"))) {
                	predicateList.add(cb.like(root.get("id").as(String.class), "%"+(String)searchMap.get("id")+"%"));
                }
                // 手机号码
                if (searchMap.get("mobile")!=null && !"".equals(searchMap.get("mobile"))) {
                	predicateList.add(cb.like(root.get("mobile").as(String.class), "%"+(String)searchMap.get("mobile")+"%"));
                }
                // 密码
                if (searchMap.get("password")!=null && !"".equals(searchMap.get("password"))) {
                	predicateList.add(cb.like(root.get("password").as(String.class), "%"+(String)searchMap.get("password")+"%"));
                }
                // 昵称
                if (searchMap.get("nickname")!=null && !"".equals(searchMap.get("nickname"))) {
                	predicateList.add(cb.like(root.get("nickname").as(String.class), "%"+(String)searchMap.get("nickname")+"%"));
                }
                // 性别
                if (searchMap.get("sex")!=null && !"".equals(searchMap.get("sex"))) {
                	predicateList.add(cb.like(root.get("sex").as(String.class), "%"+(String)searchMap.get("sex")+"%"));
                }
                // 头像
                if (searchMap.get("avatar")!=null && !"".equals(searchMap.get("avatar"))) {
                	predicateList.add(cb.like(root.get("avatar").as(String.class), "%"+(String)searchMap.get("avatar")+"%"));
                }
                // E-Mail
                if (searchMap.get("email")!=null && !"".equals(searchMap.get("email"))) {
                	predicateList.add(cb.like(root.get("email").as(String.class), "%"+(String)searchMap.get("email")+"%"));
                }
                // 兴趣
                if (searchMap.get("interest")!=null && !"".equals(searchMap.get("interest"))) {
                	predicateList.add(cb.like(root.get("interest").as(String.class), "%"+(String)searchMap.get("interest")+"%"));
                }
                // 个性
                if (searchMap.get("personality")!=null && !"".equals(searchMap.get("personality"))) {
                	predicateList.add(cb.like(root.get("personality").as(String.class), "%"+(String)searchMap.get("personality")+"%"));
                }
				
				return cb.and( predicateList.toArray(new Predicate[predicateList.size()]));

			}
		};

	}

	public void sendSmsCode(String mobile) {
		//1）生成验证码，一般6位
		int max = 999999;
		int min = 100000;
		int code = new Random().nextInt(max);
		if (code < min) {
			code += min;
		}
		//2）把手机号和验证码封装到一个java对象中。
		Map data = new HashMap();
		data.put("mobile", mobile);
		data.put("code", "" + code);
		//3) 使用mq把消息发送给sms系统。
		rabbitTemplate.convertAndSend("queue-sms", data);
		//使用docker中的mq
		//4）把发送验证码需要记录，可以把验证码放到redis中。并且设置过期时间。
		redisTemplate.opsForValue().set("sms_" + mobile, code + "", 5, TimeUnit.MINUTES);

	}

	public Result userRegister(User user, String code) {
		//1、判断验证码是否正确。
		String regcode = (String) redisTemplate.opsForValue().get("sms_" + user.getMobile());
		if (regcode == null) {
			return new Result(false, StatusCode.ERROR, "验证码已经过期");
		}
		//2、如果不正确，返回失败
		if (!code.equals(regcode)) {
			return new Result(false, StatusCode.ERROR, "验证码不正确");
		}
		//3、如果正确，执行注册操作。
		//4、生成新的id
		String userId = idWorker.nextId() + "";
		//密码需要加密处理
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		//5、设置默认值。
		user.setId(userId);
		user.setRegdate(new Date());
		user.setUpdatedate(new Date());
		user.setLastdate(new Date());
		user.setOnline(0l);
		user.setFanscount(0);
		user.setFollowcount(0);
		//6、把用户信息插入到数据库
		userDao.save(user);
		//7、返回成功
		return new Result(true, StatusCode.OK, "用户注册成功");
	}

	public User login(String mobile, String password) {
		//根据手机号查询用户数据
		User user = userDao.findByMobile(mobile);
		if (user == null) {
			return null;
		}
		//判断密码是否正确
		if (!passwordEncoder.matches(password, user.getPassword())) {
			return null;
		}
		//正确返回用户对象
		return user;
	}

	@Transactional
	public void updateFansCount(int count, String id) {
		userDao.updateFansCount(count, id);
	}

	@Transactional
	public void updateFolowCount(int count, String id) {
		userDao.updateFollowCount(count, id);
	}

}
