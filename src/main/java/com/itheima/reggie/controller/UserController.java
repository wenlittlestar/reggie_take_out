package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;



@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送短信
     * @param map
     * @param session
     * @return
     */
    //前段自动生成短信验证码（方便测试）
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody Map map,HttpSession session){
        //获取手机号
        Object phone = map.get("phone");
        //获取验证码
        Object code = map.get("code");
        if (StringUtils.isNotEmpty(phone.toString())){

           //需要将生成的验证码保存到Session
            session.setAttribute("phone",phone);
            session.setAttribute("code",code);

            return R.success("手机短信发送成功");
        }
        return R.success("手机短信发送失败");
    }
    //使用阿里云发送短信
   /* @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user,HttpSession session){
        //获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)){
           //生成的四位验证码
            String code = ValidateCodeUtils.generateValidateCode4String(4);

            //调用阿里云提供的短信服务API完成短信发送
            SMSUtils.sendMessage("阿里云短信测试","SMS_154950909","19967294807",code);
            log.info("code:{}",code);

           //需要将生成的验证码保存到Session
            session.setAttribute("phone",phone);
            session.setAttribute("code",code);

            return R.success("手机短信发送成功");
        }
        return R.success("手机短信发送失败");
    }*/

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());

        //获取手机号
        String phone = map.get("phone").toString();
        Object phone1 = session.getAttribute("phone");
        if (phone != null && !phone.equals(phone1)){
            return R.error("手机号错误");
        }

        //获取验证码
        String code = map.get("code").toString();
        Object code1 = session.getAttribute("code");
        if (code != null && !code.equals(code1)){
            return R.error("验证码错误");
        }


        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone,phone1);
        User user = new User();
        user.setPhone((String) phone1);
        user.setStatus(1);
        //查询是否存在改手机号的用户，没有则注册
        if (userService.count(queryWrapper) == 0){
            userService.save(user);
        }
        //取到该手机号对应的用户id
        User user1 = userService.getOne(queryWrapper);
        session.setAttribute("user",user1.getId());
        return R.success(user);
    }

    /**
     * 移动端用户登出
     * @param session
     * @return
     */
    @PostMapping("/loginout")
    public R<String> loginout(HttpSession session){
        //清理Session中保存的当前员工的id
        session.removeAttribute("user");
        return R.success("退出成功");
    }

}
