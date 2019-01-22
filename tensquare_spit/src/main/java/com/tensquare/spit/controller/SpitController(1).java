package com.tensquare.spit.controller;

import com.tensquare.spit.pojo.Spit;
import com.tensquare.spit.service.SpitService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/spit")
@CrossOrigin
public class SpitController {
    @Autowired
    private SpitService spitService;
    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(method = RequestMethod.POST)
    public Result addSpit(@RequestBody Spit spit) {
        spitService.addSpit(spit);
        //返回结果
        return new Result(true, StatusCode.OK, "添加成功");
    }

    @RequestMapping(value = "/{spitId}", method = RequestMethod.PUT)
    public Result updateSpit(@PathVariable String spitId, @RequestBody Spit spit) {
        spit.set_id(spitId);
        //更新
        spitService.updateSpit(spit);
        //返回成功
        return new Result(true, StatusCode.OK, "更新成功");
    }

    @RequestMapping(value = "/{spitId}", method = RequestMethod.DELETE)
    public Result deleteSpit(@PathVariable String spitId) {
        spitService.deleteSpit(spitId);
        return new Result(true, StatusCode.OK,"删除成功");
    }

    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功",
                spitService.findAll());
    }

    @RequestMapping(value = "/{spitId}", method = RequestMethod.GET)
    public Result getSpitById(@PathVariable String spitId) {
        Spit spit = spitService.getSpitById(spitId);
        return new Result(true, StatusCode.OK, "查询成功", spit);
    }

    @RequestMapping(value = "/comment/{parentid}/{page}/{size}", method = RequestMethod.GET)
    public Result getSpitListByParentId(@PathVariable String parentid,
                                        @PathVariable int page,
                                        @PathVariable int size) {
        PageResult pageResult = spitService.getSpitListByParentId(parentid, page, size);
        return new Result(true, StatusCode.OK, "查询成功", pageResult);
    }

    @RequestMapping(value = "/thumbup/{spitId}", method = RequestMethod.PUT)
    public Result thumbUp(@PathVariable String spitId) {
        String userid = "100";
        //判断是否已经点过赞
        String flag = (String) redisTemplate.opsForValue().get("thumbup_" + userid + "_" + spitId);
        if (flag != null) {
            return new Result(false, StatusCode.REPERROR, "您已经点过赞了");
        }
        spitService.thumbUp(spitId);
        //在redis中设置标记
        redisTemplate.opsForValue().set("thumbup_" + userid + "_" + spitId, "1");
        return new Result(true, StatusCode.OK, "点赞成功");
    }



}
