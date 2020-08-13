package com.powernode.settings.controller;

import com.powernode.settings.pojo.TblUser;
import com.powernode.settings.service.TblUserService;
import com.powernode.utils.GetCheckMessageUtil;
import com.powernode.utils.MD5Util;
import com.powernode.utils.PhoneCheckUtil;
import com.powernode.utils.Result;
import com.powernode.exception.ResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class TblUserController {

    @Autowired
    private TblUserService tblUserService;

    @Value("${sessionUser}")
    private String SESSION_USER;

    //登陆页面
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result login( String loginAct, String loginPwd, HttpServletRequest request){
        //获取ip地址
        String ipAddr = request.getRemoteAddr();

        TblUser user = tblUserService.login(loginAct, MD5Util.getMD5(loginPwd), ipAddr);
        //存储用户名
        request.getSession().setAttribute(SESSION_USER,user);

        return Result.ok();

    }

    @RequestMapping("/currentUser")
    public Result getCurrentUser(HttpServletRequest request){
        TblUser currentUser = (TblUser) request.getSession().getAttribute(SESSION_USER);
        return Result.ok(currentUser);
    }

    @RequestMapping("/message")
    public Result getMessage(String phone, HttpServletRequest request){

        String message = "";
        try {
            message = PhoneCheckUtil.SendCode(phone);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> checkMessage = GetCheckMessageUtil.getCheckMessage(message);

        String checkCode = checkMessage.get("obj");
        request.getSession().getServletContext().setAttribute("checkCode",checkCode);
        return Result.ok();
    }

    @RequestMapping("/check")
    public Result check(String currMessage, HttpServletRequest request){
        String checkCode = (String)request.getSession().getServletContext().getAttribute("checkCode");
        if (checkCode.equals(currMessage)){
            return Result.ok();
        }else {
            return Result.build(1500,"验证错误，验证失败");
        }

    }


}
