package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@Api(tags = "C端购物车模块")
@RequestMapping("/user/shoppingCart")
public class ShoppingCartController {

        @Resource
        private ShoppingCartService shoppingCartService;

        @PostMapping("/add")
        @ApiOperation("添加购物车")
        public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
            log.info("添加购物车,商品信息为:{}",shoppingCartDTO);
            shoppingCartService.addShoppingCart(shoppingCartDTO);
            return Result.success();
        }
        @GetMapping("/list")
        @ApiOperation("查看购物车")
        public Result<List<ShoppingCart>>  list(){
            List<ShoppingCart> list = shoppingCartService.showShoppingCart();
            return Result.success(list);
        }
        /**
         * 清空购物车
         * @return
         */
        @DeleteMapping("/clean")
        @ApiOperation("清空购物车")
        public Result clean(){
            shoppingCartService.cleanShoppingCart();
            return Result.success();
        }

}
