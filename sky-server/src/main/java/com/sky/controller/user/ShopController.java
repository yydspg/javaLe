package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@Api(tags = "店铺相关接口")
@RequestMapping("/user/shop")
@RestController("userShopController")
public class ShopController {
    public static final String KEY = "SHOP_STATUS";
    @Resource
    private RedisTemplate redisTemplate;
    @GetMapping
    @ApiOperation("获取店铺营业状态")
    public Result<Integer> getStatus(){

        Integer shop_status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("获取店铺的营业状态:{}",shop_status == 1 ? "营业中": "打烊中");
        return Result.success();
    }
}
