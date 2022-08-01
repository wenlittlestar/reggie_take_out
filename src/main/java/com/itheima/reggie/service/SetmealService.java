package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {
    //保存套餐信息，并且保存套餐相关的菜品信息
    void saveWithDish(SetmealDto setmealDto);

    //根据id查找套餐信息和对应的菜品信息
    SetmealDto getWithDish(Long id);

    //修改setmeal表中套餐信息，同时修改setmeal_dish中菜品信息
    void updateWithDish(SetmealDto setmealDto);

    //删除套餐信息，同时删除对应菜品信息
    R<String> removeWithDish(Long[] ids);
}
