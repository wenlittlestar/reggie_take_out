package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Orders;

public interface OrderService extends IService<Orders> {
    //保存订单内容并且保存相关的订单明细
    void saveWithDetails(Orders orders);
}
