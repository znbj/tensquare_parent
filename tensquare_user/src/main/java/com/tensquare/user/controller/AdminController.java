package com.tensquare.user.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tensquare.user.pojo.Admin;
import com.tensquare.user.service.AdminService;

import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import util.IdWorker;
import util.JwtUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * 控制器层
 * @author Administrator
 *
 */
@RestController
@CrossOrigin
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private IdWorker idWorker;
	@Autowired
	private JwtUtil jwtUtil;
	
	
	/**
	 * 查询全部数据
	 * @return
	 */
	@RequestMapping(method= RequestMethod.GET)
	public Result findAll(){
		return new Result(true,StatusCode.OK,"查询成功",adminService.findAll());
	}
	
	/**
	 * 根据ID查询
	 * @param id ID
	 * @return
	 */
	@RequestMapping(value="/{id}",method= RequestMethod.GET)
	public Result findById(@PathVariable String id){
		return new Result(true,StatusCode.OK,"查询成功",adminService.findById(id));
	}


	/**
	 * 分页+多条件查询
	 * @param searchMap 查询条件封装
	 * @param page 页码
	 * @param size 页大小
	 * @return 分页结果
	 */
	@RequestMapping(value="/search/{page}/{size}",method=RequestMethod.POST)
	public Result findSearch(@RequestBody Map searchMap , @PathVariable int page, @PathVariable int size){
		Page<Admin> pageList = adminService.findSearch(searchMap, page, size);
		return  new Result(true,StatusCode.OK,"查询成功",  new PageResult<Admin>(pageList.getTotalElements(), pageList.getContent()) );
	}

	/**
     * 根据条件查询
     * @param searchMap
     * @return
     */
    @RequestMapping(value="/search",method = RequestMethod.POST)
    public Result findSearch( @RequestBody Map searchMap){
        return new Result(true,StatusCode.OK,"查询成功",adminService.findSearch(searchMap));
    }
	
	/**
	 * 增加
	 * @param admin
	 */
	@RequestMapping(method=RequestMethod.POST)
	public Result add(@RequestBody Admin admin  ){
		//生成id
		String adminId = idWorker.nextId() + "";
		admin.setId(adminId);
		//添加管理员之前应该对密码进行加密处理
		String rawPassword = admin.getPassword();
		//对密码进行加密
		String encodePass = passwordEncoder.encode(rawPassword);
		admin.setPassword(encodePass);
		//把管理员数据添加到数据库
		adminService.add(admin);
		return new Result(true,StatusCode.OK,"增加成功");
	}
	
	/**
	 * 修改
	 * @param admin
	 */
	@RequestMapping(value="/{id}",method= RequestMethod.PUT)
	public Result update(@RequestBody Admin admin, @PathVariable String id ){
		admin.setId(id);
		adminService.update(admin);		
		return new Result(true,StatusCode.OK,"修改成功");
	}
	
	/**
	 * 删除
	 * @param id
	 */
	@RequestMapping(value="/{id}",method= RequestMethod.DELETE)
	public Result delete(@PathVariable String id, HttpServletRequest request){
		//1）从请求头中取Authorization，判断头是否存在
		String authorization = request.getHeader("Authorization");
		//	2）如果头不存在，认证失败。
		if (authorization == null || "".equals(authorization)) {
			return new Result(false, StatusCode.ACCESSERROR, "认证失败");
		}
		//	3）如果有头，是否是以“Bearer ”开头。
		if (!authorization.startsWith("Bearer ")) {
		//	4）如果不是认证失败
			return new Result(false, StatusCode.ACCESSERROR, "认证失败");
		}
		//	5）如果是以“Bearer ”开头
		//	6）取token
		String token = authorization.substring(7);
		//	7）对token进行校验，判断当前用户是否有admin角色。
		Claims claims = jwtUtil.parseJWT(token);
		//	8）如果不是admin角色也是认证失败
		String roles = (String) claims.get("roles");
		if (!"admin".equals(roles)) {
			return new Result(false, StatusCode.ACCESSERROR, "权限不足");
		}
		//	9）如果是，执行删除处理。
		adminService.deleteById(id);
		return new Result(true,StatusCode.OK,"删除成功");
	}

	/**
	 * admin登录处理
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Result doLogin(@RequestBody Map<String, String> param) {
		Admin admin = adminService.login(param.get("loginname"), param.get("password"));
		if (admin == null) {
			return new Result(false, StatusCode.LOGINERROR, "用户名或密码错误");
		}
		//生成token，由客户端进行保存，请求其他处理时，把token带到服务端。
		String token = jwtUtil.createJWT(admin.getId(), admin.getLoginname(), "admin");
		Map map = new HashMap();
		map.put("name", admin.getLoginname());
		map.put("token", token);
		return new Result(true, StatusCode.OK, "登录成功", map);
	}
}
