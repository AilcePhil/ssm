package com.powernode.workbench.service.impl;

import com.powernode.exception.ResultException;
import com.powernode.utils.DateTimeUtil;
import com.powernode.utils.UUIDUtil;
import com.powernode.workbench.mapper.TblActivityRemarkMapper;
import com.powernode.workbench.pojo.TblActivityRemark;
import com.powernode.workbench.pojo.TblActivityRemarkExample;
import com.powernode.workbench.service.TblRemarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class TblRemarkServiceImpl implements TblRemarkService {

    @Autowired
    private TblActivityRemarkMapper tblActivityRemarkMapper;

    @Override
    public List<TblActivityRemark> selectAllRemark(String activityId) {
        TblActivityRemarkExample tblActivityRemarkExample = new TblActivityRemarkExample();
        TblActivityRemarkExample.Criteria criteria = tblActivityRemarkExample.createCriteria();
        criteria.andActivityidEqualTo(activityId);
        List<TblActivityRemark> remarksList = tblActivityRemarkMapper.selectByExample(tblActivityRemarkExample);
        if (remarksList == null || remarksList.size()==0){
            throw new ResultException("查询失败");
        }
        return remarksList;

    }

    @Override
    public void addRemark(TblActivityRemark tblActivityRemark) {

        tblActivityRemark.setCreatetime(DateTimeUtil.getSysTime());
        String uuid = UUIDUtil.getUUID();
        tblActivityRemark.setId(uuid);
        tblActivityRemark.setEditflag("0");
        String notecontent = tblActivityRemark.getNotecontent();
        if (notecontent == null || "".equals(notecontent)){
            throw new ResultException("备注不能为空");
        }
        try {
            tblActivityRemarkMapper.insertSelective(tblActivityRemark);
        } catch (ResultException e) {
            throw new ResultException("增加失败");
        }


    }

    @Override
    public void delRemark(String id) {
        try {
            tblActivityRemarkMapper.deleteByPrimaryKey(id);
        } catch (ResultException e) {
            throw new ResultException("删除失败");
        }
    }

    @Override
    public TblActivityRemark editRemark(String id) {
        TblActivityRemark tblActivityRemark = tblActivityRemarkMapper.selectByPrimaryKey(id);
        if (tblActivityRemark == null){
            throw new ResultException("为找到数据");
        }
        return tblActivityRemark;
    }

    @Override
    public void editSaveRemark(TblActivityRemark tblActivityRemark) {
        tblActivityRemark.setEditflag("1");
        tblActivityRemark.setEdittime(DateTimeUtil.getSysTime());
        try {
            tblActivityRemarkMapper.updateByPrimaryKeySelective(tblActivityRemark);
        } catch (ResultException e) {
            throw new ResultException("修改失败");
        }
    }
}
