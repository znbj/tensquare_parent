package com.tensquare.qa.client;

import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("TENSQUARE-BASE")
public interface LabelClient {

    @RequestMapping(value = "/label/{id}", method = RequestMethod.GET)
    //@PathVariable注解中必须添加映射参数名称
    Result findById(@PathVariable("id") String id);
}
