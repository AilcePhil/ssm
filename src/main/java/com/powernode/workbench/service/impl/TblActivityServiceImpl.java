package com.powernode.workbench.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.powernode.exception.ResultException;
import com.powernode.settings.mapper.TblUserMapper;
import com.powernode.settings.pojo.TblUser;
import com.powernode.settings.pojo.TblUserExample;
import com.powernode.utils.DateTimeUtil;
import com.powernode.utils.PageResult;
import com.powernode.utils.UUIDUtil;
import com.powernode.workbench.mapper.TblActivityMapper;
import com.powernode.workbench.mapper.TblActivityRemarkMapper;
import com.powernode.workbench.pojo.TblActivity;
import com.powernode.workbench.pojo.TblActivityExample;
import com.powernode.workbench.pojo.TblActivityRemark;
import com.powernode.workbench.pojo.TblActivityRemarkExample;
import com.powernode.workbench.service.TblActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TblActivityServiceImpl implements TblActivityService {

    @Autowired
    private TblUserMapper tblUserMapper;
    @Autowired
    private TblActivityMapper tblActivityMapper;
    @Autowired
    private TblActivityRemarkMapper tblActivityRemarkMapper;

    @Override
    public List<TblUser> getAllUsers() {
        List<TblUser> tblUsers = tblUserMapper.selectByExample(null);

        if (tblUsers==null || tblUsers.size()==0){
            throw new ResultException("用户为空");
        }

        return tblUsers;
    }

    @Override
    public void addActivity(TblActivity tblActivity) {
        //补全属性值，id值   createby创建人 createtime创建时间
        tblActivity.setId(UUIDUtil.getUUID());
        tblActivity.setCreatetime(DateTimeUtil.getSysTime());
        try {
            tblActivityMapper.insert(tblActivity);
        } catch (Exception e) {
            throw new ResultException("添加失败");
        }

    }

    @Override
    public PageResult pageFindActivity(int pageSize, int pageNo, String name, String owner, String startdate, String enddate) {
        PageHelper.startPage(pageNo, pageSize);


        TblActivityExample tblActivityExample = new TblActivityExample();
        TblActivityExample.Criteria criteria = tblActivityExample.createCriteria();

        //模糊查询
        if (name!=null && !"".equals(name)){
            criteria.andNameLike("%"+name+"%");
        }

        if (owner!=null && !"".equals(owner)){
            //关联tbluser表对所有者判断
            TblUserExample example = new TblUserExample();
            TblUserExample.Criteria criteria1 = example.createCriteria();
            criteria1.andNameLike("%"+owner+"%");
            List<TblUser> tblUsers = tblUserMapper.selectByExample(example);

            List<String> uuids = new ArrayList<>();
            for(TblUser tblUser : tblUsers){
                uuids.add(tblUser.getId());
            }
            criteria.andOwnerIn(uuids);

        }

        if (startdate!=null && !"".equals(startdate)){
            criteria.andStartdateLike("%"+startdate+"%");
        }

        if (enddate!=null && !"".equals(enddate)){
            criteria.andEnddateLike("%"+enddate+"%");
        }

        List<TblActivity> tblActivities = tblActivityMapper.selectByExample(tblActivityExample);

        if (tblActivities==null || tblActivities.size()==0) {
            System.out.println("未找到数据");
            throw new ResultException("未找到数据");
        }

        //转换所有者从用户的uuid到name
        for (TblActivity tblActivity : tblActivities){
            TblUser tblUser = tblUserMapper.selectByPrimaryKey(tblActivity.getOwner());
            tblActivity.setOwner(tblUser.getName());
        }

        PageInfo<TblActivity> pageInfo = new PageInfo<>(tblActivities);
        PageResult pageResult = new PageResult(pageInfo.getTotal(), pageInfo.getList());
        return pageResult;
    }

    @Override
    public void delActivity(String[] ids) {
        if (ids == null || ids.length == 0){
            throw new ResultException("参数不能为空");
        }

        try {
            for (String id : ids){
                //由于关联了remake表，所以先删除remake表
                TblActivityRemarkExample tblActivityRemarkExample = new TblActivityRemarkExample();
                TblActivityRemarkExample.Criteria criteria = tblActivityRemarkExample.createCriteria();
                criteria.andActivityidEqualTo(id);

                tblActivityRemarkMapper.deleteByExample(tblActivityRemarkExample);
                tblActivityMapper.deleteByPrimaryKey(id);
            }
        } catch (ResultException e) {
            throw new  ResultException("删除失败");
        }
    }

    @Override
    public TblActivity selectActById(String id) {
        TblActivity tblActivity = tblActivityMapper.selectByPrimaryKey(id);
        if (tblActivity == null){
            throw new RuntimeException("查询失败");
        }
        return tblActivity;
    }

    @Override
    public void updateActivity(TblActivity tblActivity) {
        tblActivity.setEdittime(DateTimeUtil.getSysTime());
        try {
            tblActivityMapper.updateByPrimaryKeySelective(tblActivity);
        } catch (ResultException e) {
            throw new ResultException("修改失败");
        }

    }

    @Override
    public TblActivity detailById(String id) {
        TblActivity tblActivity = tblActivityMapper.selectByPrimaryKey(id);
        String userId = tblActivity.getOwner();
        TblUser tblUser = tblUserMapper.selectByPrimaryKey(userId);
        tblActivity.setOwner(tblUser.getName());
        if (tblActivity == null){
            throw new ResultException("查询失败");
        }
        return tblActivity;

    }


}
