package com.powernode.workbench.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.powernode.exception.ResultException;
import com.powernode.settings.mapper.TblUserMapper;
import com.powernode.settings.pojo.TblUser;
import com.powernode.utils.PageResult;
import com.powernode.utils.Result;
import com.powernode.utils.UUIDUtil;
import com.powernode.workbench.mapper.TblActivityMapper;
import com.powernode.workbench.mapper.TblClueActivityRelationMapper;
import com.powernode.workbench.mapper.TblClueMapper;
import com.powernode.workbench.pojo.*;
import com.powernode.workbench.service.TblClueActivityRelationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TblClueActivityRelationServiceImpl implements TblClueActivityRelationService {

    @Autowired
    private TblActivityMapper tblActivityMapper;
    @Autowired
    private TblClueActivityRelationMapper relationMapper;
    @Autowired
    private TblUserMapper tblUserMapper;
    @Autowired
    private TblClueMapper tblClueMapper;


    @Override
    public  List<TblActivity> selectByName(String activityname, String clueid) {

        TblActivityExample tblActivityExample = new TblActivityExample();
        TblActivityExample.Criteria criteria = tblActivityExample.createCriteria();
        if (activityname != null && !"".equals(activityname)){
            criteria.andNameLike("%"+activityname+"%");
        }

        //获得包含该线索的所有中间表集合
        TblClueActivityRelationExample tblClueActivityRelationExample = new TblClueActivityRelationExample();
        TblClueActivityRelationExample.Criteria relationExampleCriteria = tblClueActivityRelationExample.createCriteria();
        relationExampleCriteria.andClueidEqualTo(clueid);
        List<TblClueActivityRelation> tblClueActivityRelations = relationMapper.selectByExample(tblClueActivityRelationExample);

        //获得包含该线索的所活动id的集合
        List<String> aidList = new ArrayList<>();
        for (TblClueActivityRelation tblClueActivityRelation : tblClueActivityRelations){
            String activityid = tblClueActivityRelation.getActivityid();
            aidList.add(activityid);
        }

        if (aidList.size() != 0){
            //搜索不包含已有活动的其他活动
            criteria.andIdNotIn(aidList);
            List<TblActivity> tblActivities = tblActivityMapper.selectByExample(tblActivityExample);
        }
        List<TblActivity> tblActivities = tblActivityMapper.selectByExample(tblActivityExample);


        //转换活动的owner
        for (TblActivity tblActivity : tblActivities){
            TblUser tblUser = tblUserMapper.selectByPrimaryKey(tblActivity.getOwner());
            tblActivity.setOwner(tblUser.getName());
        }

        return tblActivities;
}
    //实现分页的方法
    @Override
    public PageResult selectByName(String activityname, String clueid, int pageNo, int pageSize) {

        TblActivityExample tblActivityExample = new TblActivityExample();
        TblActivityExample.Criteria criteria = tblActivityExample.createCriteria();
        if (activityname != null && !"".equals(activityname)){
            criteria.andNameLike("%"+activityname+"%");
        }

        //获得包含该线索的所有中间表集合
        TblClueActivityRelationExample tblClueActivityRelationExample = new TblClueActivityRelationExample();
        TblClueActivityRelationExample.Criteria relationExampleCriteria = tblClueActivityRelationExample.createCriteria();
        relationExampleCriteria.andClueidEqualTo(clueid);
        List<TblClueActivityRelation> tblClueActivityRelations = relationMapper.selectByExample(tblClueActivityRelationExample);

        //获得包含该线索的所活动id的集合
        List<String> aidList = new ArrayList<>();
        for (TblClueActivityRelation tblClueActivityRelation : tblClueActivityRelations){
            String activityid = tblClueActivityRelation.getActivityid();
            aidList.add(activityid);
        }


        if (aidList.size() != 0){
            //搜索不包含已有活动的其他活动
            criteria.andIdNotIn(aidList);
        }
       PageHelper.startPage(pageNo, pageSize);
       List<TblActivity> tblActivities = tblActivityMapper.selectByExample(tblActivityExample);


        //转换活动的owner
        for (TblActivity tblActivity : tblActivities){
            TblUser tblUser = tblUserMapper.selectByPrimaryKey(tblActivity.getOwner());
            tblActivity.setOwner(tblUser.getName());
        }


        PageInfo<TblActivity> pageInfo = new PageInfo<>(tblActivities);
        PageResult pageResult = new PageResult(pageInfo.getTotal(), pageInfo.getList());

        return pageResult;



    }

    @Override
    public List<Map<String, Object>> getRelaActivity(String clueid) {

        //根据线索查询中间表
        TblClueActivityRelationExample relationExample = new TblClueActivityRelationExample();
        TblClueActivityRelationExample.Criteria criteria = relationExample.createCriteria();
        criteria.andClueidEqualTo(clueid);

        List<TblClueActivityRelation> tblClueActivityRelations = relationMapper.selectByExample(relationExample);
        if (tblClueActivityRelations == null || tblClueActivityRelations.size() == 0){
            throw new ResultException("未找到数据");
        }
        //根据查询到的中间表获得市场活动信息
        List<Map<String, Object>> activityList = new ArrayList<>();
        for (TblClueActivityRelation tblClueActivityRelation : tblClueActivityRelations){
            Map<String, Object> map = new HashMap<>();
            TblActivity tblActivity = tblActivityMapper.selectByPrimaryKey(tblClueActivityRelation.getActivityid());
            if (tblActivity == null){
                throw new ResultException("无数据，查询失败");
            }
            TblUser tblUser = tblUserMapper.selectByPrimaryKey(tblActivity.getOwner());
            tblActivity.setOwner(tblUser.getName());

            map.put("caid",tblClueActivityRelation.getId());
            map.put("activity", tblActivity);
            activityList.add(map);
        }
        return activityList;
    }

    @Override
    public void delRelation(String id) {
        try {
            relationMapper.deleteByPrimaryKey(id);
        } catch (ResultException e) {
            throw new ResultException("删除失败");
        }
    }

    @Override
    public void addRelation(String[] aids, String clueid) {

        try {
            for (int i = 0; i<aids.length; i++){
                TblClueActivityRelation tblClueActivityRelation = new TblClueActivityRelation();
                tblClueActivityRelation.setActivityid(aids[i]);
                tblClueActivityRelation.setClueid(clueid);
                tblClueActivityRelation.setId(UUIDUtil.getUUID());
                relationMapper.insert(tblClueActivityRelation);
            }
        } catch (ResultException e) {
            throw new ResultException("关联失败");
        }
    }

    @Override
    public PageResult selectById(String clueId, String activityName, int pageNo, int pageSize) {
        //被当前线索关联的所有活动
        TblClueActivityRelationExample relationExample = new TblClueActivityRelationExample();
        TblClueActivityRelationExample.Criteria relationCriteria = relationExample.createCriteria();
        relationCriteria.andClueidEqualTo(clueId);
        List<TblClueActivityRelation> tblClueActivityRelations = relationMapper.selectByExample(relationExample);
        if (tblClueActivityRelations == null || tblClueActivityRelations.size() == 0){
            throw new ResultException("无数据，查询失败");
        }

        //所有关联的市场活动的id集合
        List<String> activityIds = new ArrayList<String>();
        for (TblClueActivityRelation tblClueActivityRelation : tblClueActivityRelations){
            activityIds.add(tblClueActivityRelation.getActivityid());
        }

        //查询满足条件的所有活动
        TblActivityExample tblActivityExample = new TblActivityExample();
        TblActivityExample.Criteria activityCriteria = tblActivityExample.createCriteria();
        if (activityName == null || !"".equals(activityName)){
            activityCriteria.andNameLike("%"+activityName+"%");
        }
        activityCriteria.andIdIn(activityIds);

        PageHelper.startPage(pageNo, pageSize);
        List<TblActivity> tblActivities = tblActivityMapper.selectByExample(tblActivityExample);
        PageInfo<TblActivity> pageInfo = new PageInfo<>(tblActivities);
        PageResult pageResult = new PageResult(pageInfo.getTotal(), pageInfo.getList());
        return pageResult;
    }


}
