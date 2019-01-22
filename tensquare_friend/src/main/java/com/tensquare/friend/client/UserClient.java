package com.tensquare.friend.client;

import entity.Result;
import entity.StatusCode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("TENSQUARE-USER")
public interface UserClient {
    @RequestMapping(value = "/user/incfans/{userid}/{num}", method = RequestMethod.POST)
    public Result incFansCount(@PathVariable("userid") String userid, @PathVariable("num") int num);

    @RequestMapping(value = "/user/incfollow/{userid}/{num}", method = RequestMethod.POST)
    public Result incFlowCount(@PathVariable("userid") String userid, @PathVariable("num") int num);
}
