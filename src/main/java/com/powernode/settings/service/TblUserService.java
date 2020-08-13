package com.powernode.settings.service;

import com.powernode.settings.mapper.TblUserMapper;
import com.powernode.settings.pojo.TblUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


public interface TblUserService {
    //登陆接口，返回数据是为了在session的域中存储已经登入的用户的值
    TblUser login(String loginAct, String loginPwd, String ip);
}
