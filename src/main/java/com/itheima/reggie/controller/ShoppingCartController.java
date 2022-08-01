package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.ShoppingCartService;
import com.itheima.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 显示购物车的套餐和菜品
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(currentId != null,ShoppingCart::getUserId,currentId);
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 添加套餐或菜品到购物车
     * @return
     */
    @PostMapping("/add")
    public R<String> add(@RequestBody ShoppingCart shoppingCart){
        /*log.info(shoppingCart.toString());
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        shoppingCartService.save(shoppingCart);
        return R.success("添加成功");*/   //一个一个添加至购物车，没有考虑number标签
        log.info(shoppingCart.toString());
        LambdaQueryWrapper<ShoppingCart> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(shoppingCart.getName() != null,ShoppingCart::getName,shoppingCart.getName());
        ShoppingCart shoppingCartOne = shoppingCartService.getOne(queryWrapper);
        if (shoppingCartOne == null){
            Long userId = BaseContext.getCurrentId();
            shoppingCart.setUserId(userId);
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
        }else{
            shoppingCartOne.setNumber(shoppingCartOne.getNumber() + 1);
            shoppingCartService.remove(queryWrapper);
            shoppingCartService.save(shoppingCartOne);
        }
        return R.success("添加成功");
    }

    /**
     * 删除一个套餐或者菜品
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart){
        log.info(shoppingCart.toString());
        if (shoppingCart.getSetmealId() != null){
            /*Long setmealId = shoppingCart.getSetmealId();
            LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
            Map<String, Object> map = shoppingCartService.getMap(queryWrapper);
            log.info("map:{}",map);
            Long id = (Long) map.get("id");
            shoppingCartService.removeById(id);*/  //删除一个套餐，没有考虑number标签
            Long setmealId = shoppingCart.getSetmealId();
            LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(setmealId != null,ShoppingCart::getSetmealId,setmealId);
            ShoppingCart shoppingCart1 = shoppingCartService.getOne(queryWrapper);
            if (shoppingCart1.getNumber() == 1){
                shoppingCartService.remove(queryWrapper);
            }else {
                shoppingCart1.setNumber(shoppingCart1.getNumber() - 1);
                shoppingCartService.remove(queryWrapper);
                shoppingCartService.save(shoppingCart1);
            }
        }
        if (shoppingCart.getDishId() != null){
            /*Long dishId = shoppingCart.getDishId();
            LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
            Map<String, Object> map = shoppingCartService.getMap(queryWrapper);
            Long id = (Long) map.get("id");
            shoppingCartService.removeById(id);*/ //删除一个菜品，没有考虑number标签
            Long dishId = shoppingCart.getDishId();
            LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(dishId != null,ShoppingCart::getDishId,dishId);
            ShoppingCart shoppingCart1 = shoppingCartService.getOne(queryWrapper);
            if (shoppingCart1.getNumber() == 1){
                shoppingCartService.remove(queryWrapper);
            }else {
                shoppingCart1.setNumber(shoppingCart1.getNumber() - 1);
                shoppingCartService.remove(queryWrapper);
                shoppingCartService.save(shoppingCart1);
            }
        }

        return R.success("删除成功");
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功");
    }
}
