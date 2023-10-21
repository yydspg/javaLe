package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Api(tags = "购物车模块")
@RequestMapping("/user/shoppingCart")
public class ShoppingCartController {

        public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
            log.info("添加购物车,商品信息为:{}",shoppingCartDTO);
            return Result.success();
        }
}
