package com.powernode.workbench.service;

import com.powernode.workbench.pojo.TblActivityRemark;

import java.util.List;

public interface TblRemarkService {
    //查询所有备注
    List<TblActivityRemark> selectAllRemark(String activityId);
    //增加备注
    void addRemark(TblActivityRemark tblActivityRemark);
    //删除备注
    void delRemark(String id);
    //修改备注
    TblActivityRemark editRemark(String id);
    //保存修改备注
    void editSaveRemark(TblActivityRemark tblActivityRemark);
}
