package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.AddressBookService;
import com.itheima.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("addressBook")
@Slf4j
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private UserService userService;

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    @Transactional
    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());  //填充当前操作用户的id
        log.info("addressBook:{}",addressBook);
        addressBookService.save(addressBook);
        return R.success("添加成功");
    }

    /**
     * 查询当前用户的所有保存了的地址
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(){
        Long currentId = BaseContext.getCurrentId();
        User currentUser = userService.getById(currentId);
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(currentUser != null,AddressBook::getUserId,currentId);
        queryWrapper.orderByDesc(AddressBook::getCreateTime);
        List<AddressBook> list = addressBookService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @Transactional
    @PutMapping("/default")
    public R<String> setDefaultAddress(@RequestBody AddressBook addressBook){
        log.info("addressBook:{}",addressBook);
        AddressBook addressBook1 = addressBookService.getById(addressBook.getId());
        BeanUtils.copyProperties(addressBook1,addressBook);
        if (addressBook.getIsDefault() == 0){
            //如果要将改地址设为默认地址，则要把其他已经设为默认地址的isdefault设置为0
            addressBook.setIsDefault(1);
            LambdaUpdateWrapper<AddressBook> updateWrapper =new LambdaUpdateWrapper<>();
            updateWrapper.set(AddressBook::getIsDefault,0).eq(AddressBook::getIsDefault,1);
            addressBookService.update(updateWrapper);
        }else{
            addressBook.setIsDefault(0);
        }

        addressBookService.updateById(addressBook);
        return R.success("修改成功");
    }

    /**
     * 获取默认地址
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefaultAddress(){
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook defaultAddress = addressBookService.getOne(queryWrapper);
        return R.success(defaultAddress);
    }

    /**
     * 修改地址簿
     * @param addressBook
     * @return
     */
    @Transactional
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){
        log.info(addressBook.toString());
        if (addressBook == null){
           return R.error("修改失败");
        }
        addressBookService.updateById(addressBook);
        return R.success("修改成功");
    }

    /**
     * 根据id查询地址簿
     * @return
     */
    @GetMapping("{id}")
    public R<AddressBook> getById(@PathVariable Long id){
        log.info("id:{}",id);
        AddressBook addressBook = addressBookService.getById(id);
        return R.success(addressBook);
    }

    /**
     * 根据id删除地址簿
     * @param id
     * @return
     */
    @Transactional
    @DeleteMapping
    public R<String> deleteById(Long id){
        log.info("id:{}",id);
        addressBookService.removeById(id);
        return R.success("删除成功");
    }


}
