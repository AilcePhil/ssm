package com.powernode.workbench.service.impl;

import com.powernode.exception.ResultException;
import com.powernode.utils.DateTimeUtil;
import com.powernode.utils.Result;
import com.powernode.utils.UUIDUtil;
import com.powernode.workbench.mapper.TblClueRemarkMapper;
import com.powernode.workbench.pojo.TblClueRemark;
import com.powernode.workbench.pojo.TblClueRemarkExample;
import com.powernode.workbench.service.TblClueRemarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TblClueRemarkServiceImpl implements TblClueRemarkService {

    @Autowired
    private TblClueRemarkMapper tblClueRemarkMapper;

    @Override
    public void addRemark(TblClueRemark tblClueRemark) {
        tblClueRemark.setId(UUIDUtil.getUUID());
        tblClueRemark.setCreatetime(DateTimeUtil.getSysTime());
        tblClueRemark.setEditflag("0");
        try {
            tblClueRemarkMapper.insertSelective(tblClueRemark);
        } catch (ResultException e) {
            throw  new ResultException("添加失败");
        }
    }

    @Override
    public List<TblClueRemark> selectAllReamark(String clueid) {
        TblClueRemarkExample tblClueRemarkExample = new TblClueRemarkExample();
        TblClueRemarkExample.Criteria criteria = tblClueRemarkExample.createCriteria();
        criteria.andClueidEqualTo(clueid);

        List<TblClueRemark> tblClueRemarks = tblClueRemarkMapper.selectByExample(tblClueRemarkExample);
        if (tblClueRemarks == null || tblClueRemarks.size() == 0){
            throw new ResultException("查询失败");
        }
        return tblClueRemarks;
    }

    @Override
    public TblClueRemark selectRemarkById(String id) {
        TblClueRemark tblClueRemark = tblClueRemarkMapper.selectByPrimaryKey(id);
        if (tblClueRemark == null){
            throw new ResultException("无数据");
        }
        return tblClueRemark;
    }

    @Override
    public void updateClueRemark(TblClueRemark tblClueRemark) {
        tblClueRemark.setEditflag("1");
        tblClueRemark.setEdittime(DateTimeUtil.getSysTime());
        try {
            tblClueRemarkMapper.updateByPrimaryKeySelective(tblClueRemark);
        } catch (ResultException e) {
            throw new ResultException("更新失败");
        }
    }

    @Override
    public void delRemarkById(String id) {
        try {
            tblClueRemarkMapper.deleteByPrimaryKey(id);
        } catch (ResultException e) {
            throw new ResultException("删除失败");
        }
    }
}
