package com.tensquare.friend.controller;

import com.tensquare.friend.client.UserClient;
import com.tensquare.friend.service.FriendService;
import entity.Result;
import entity.StatusCode;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/friend")
public class FriendController {

    @Autowired
    private FriendService friendService;
    @Autowired
    private UserClient userClient;

    @RequestMapping(value = "/like/{friendid}/{type}", method = RequestMethod.PUT)
    public Result addFriend(@PathVariable String friendid, @PathVariable String type, HttpServletRequest request) {
        //从request对象中取claims对象
        Claims claims = (Claims) request.getAttribute("claims");
        //从claims对象中取用户id
        String userId = claims.getId();
        //调用service
        int result = friendService.addFirend(userId, friendid, type);
        //返回结果
        if (result == 0) {
            return new Result(false, StatusCode.ERROR, "此好友已经添加");
        }
        userClient.incFansCount(friendid, 1);
        userClient.incFlowCount(userId, 1);
        return new Result(true, StatusCode.OK, "好友添加成功");

    }

    @RequestMapping(value = "/{friendid}", method = RequestMethod.DELETE)
    public Result deleteFriend(@PathVariable String friendid, HttpServletRequest request) {
        //取用户id
        Claims claims = (Claims) request.getAttribute("claims");
        String userId = claims.getId();
        //删除好友
        int result = friendService.deleteFriend(userId, friendid);
        if (result == 0) {
            return new Result(false, StatusCode.ERROR, "好友不存在");
        }
        userClient.incFansCount(friendid, -1);
        userClient.incFlowCount(userId, -1);
        return new Result(true, StatusCode.OK, "删除成功");
    }

}
