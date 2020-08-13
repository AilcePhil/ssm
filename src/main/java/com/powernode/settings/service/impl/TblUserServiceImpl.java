package com.powernode.settings.service.impl;

import com.powernode.settings.mapper.TblUserMapper;
import com.powernode.settings.pojo.TblUser;
import com.powernode.settings.pojo.TblUserExample;
import com.powernode.settings.service.TblUserService;
import com.powernode.utils.DateTimeUtil;
import com.powernode.exception.ResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TblUserServiceImpl implements TblUserService {

    @Autowired
    private TblUserMapper tblUserMapper;

    //判断登陆成功和失败的条件
    @Override
    public TblUser login(String loginAct, String loginPwd, String ip) {

        //判断是否账号密码是否为空
        if(loginAct == null || loginPwd == null || "".equals(loginAct) || "".equals(loginPwd)){
            throw new ResultException("账号密码不能为空");
        }

        //查询数据
        TblUserExample tblUserExample = new TblUserExample();
        TblUserExample.Criteria criteria = tblUserExample.createCriteria();
        criteria.andLoginactEqualTo(loginAct);
        criteria.andLoginpwdEqualTo(loginPwd);
        List<TblUser> tblUsers = tblUserMapper.selectByExample(tblUserExample);
        if (tblUsers==null || tblUsers.size()==0){
            throw new ResultException("登入失败，用户不存在");
        }
        //如果存在，取出该用户
        TblUser tblUser = tblUsers.get(0);

        //判断ip是否受限制
        if (!tblUser.getAllowips().contains(ip)){
            throw new ResultException("ip受限，登入失败");
        }

        //判断账号是否逾期
        String nowDate = DateTimeUtil.getSysTime();
        if (nowDate.compareTo(tblUser.getExpiretime()) > 0){
            throw new ResultException("账号已逾期");
        }

        //判断账号状态  0表示正常，1表示受限
        if ("1".equals(tblUser.getLockstate())){
            throw new ResultException("账号状态异常");
        }

        return tblUser;
    }
}
