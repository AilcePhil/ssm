package com.powernode.workbench.service;


import com.powernode.settings.pojo.TblUser;
import com.powernode.utils.PageResult;
import com.powernode.workbench.pojo.TblActivity;
import com.powernode.workbench.pojo.TblActivityRemark;

import java.util.List;

public interface TblActivityService {
    //获得所有用户
    List<TblUser> getAllUsers();
    //增加市场活动
    void addActivity(TblActivity tblActivity);
    //分页查找市场活动
    PageResult  pageFindActivity(int pageSize, int pageNo, String name, String owner, String startdate, String enddate);
    //批量删除
    void delActivity(String[] ids);
    //修改
    TblActivity selectActById(String id);
    //保存修改
    void updateActivity(TblActivity tblActivity);

    //detail信息
    TblActivity detailById(String id);




}
