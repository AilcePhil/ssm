package com.powernode.workbench.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.powernode.exception.ResultException;
import com.powernode.settings.mapper.TblUserMapper;
import com.powernode.settings.pojo.TblDicValue;
import com.powernode.settings.pojo.TblUser;
import com.powernode.settings.pojo.TblUserExample;
import com.powernode.utils.DateTimeUtil;
import com.powernode.utils.PageResult;
import com.powernode.utils.Result;
import com.powernode.utils.UUIDUtil;
import com.powernode.workbench.mapper.*;
import com.powernode.workbench.pojo.*;
import com.powernode.workbench.service.TblTranService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
@Service
public class TblTranServiceImpl implements TblTranService {

    @Autowired
    private TblTranMapper tblTranMapper;
    @Autowired
    private TblCustomerMapper tblCustomerMapper;
    @Autowired
    private TblContactsMapper tblContactsMapper;
    @Autowired
    private TblUserMapper tblUserMapper;
    @Autowired
    private TblActivityMapper tblActivityMapper;
    @Autowired
    private TblTranHistoryMapper tblTranHistoryMapper;
    @Autowired
    private TblContactsActivityRelationMapper tblContactsActivityRelationMapper;
    @Autowired
    private TblTranRemarkMapper tblTranRemarkMapper;

    @Override
    public PageResult pageAllTran(int pageSize, int pageNum, String owner, String name, String customerId, String stage, String type, String source, String contactsId) {
        TblTranExample tblTranExample = new TblTranExample();
        TblTranExample.Criteria tranExampleCriteria = tblTranExample.createCriteria();

        if (name!=null && !"".equals(name)){
            tranExampleCriteria.andNameLike("%"+name+"%");
        }

        if (owner!=null && !"".equals(owner)){
            TblUserExample tblUserExample = new TblUserExample();
            TblUserExample.Criteria userExampleCriteria = tblUserExample.createCriteria();
            userExampleCriteria.andNameLike("%"+owner+"%");
            List<TblUser> tblUsers = tblUserMapper.selectByExample(tblUserExample);
            if (tblUsers==null || tblUsers.size()==0){
                throw new ResultException("用户无数据，查询失败");
            }
            //包含用户id的集合
            List<String> userIds = new ArrayList<String>();
            for (TblUser tblUser : tblUsers) {
                userIds.add(tblUser.getId());
            }
            tranExampleCriteria.andOwnerIn(userIds);
        }

        if (customerId!=null && !"".equals(customerId)){
            TblCustomerExample tblCustomerExample = new TblCustomerExample();
            TblCustomerExample.Criteria customerExampleCriteria = tblCustomerExample.createCriteria();
            customerExampleCriteria.andNameLike("%"+customerId+"%");
            List<TblCustomer> tblCustomers = tblCustomerMapper.selectByExample(tblCustomerExample);
            if (tblCustomers==null || tblCustomers.size()==0){
                throw new ResultException("客户无数据，查询失败");
            }

            //包含客户id的集合
            List<String> customerIds = new ArrayList<String>();
            for (TblCustomer tblCustomer : tblCustomers) {
                customerIds.add(tblCustomer.getId());
            }
            tranExampleCriteria.andCustomeridIn(customerIds);
        }

        if (contactsId!=null && !"".equals(contactsId)){
            TblContactsExample tblContactsExample = new TblContactsExample();
            TblContactsExample.Criteria contactsExampleCriteria = tblContactsExample.createCriteria();
            contactsExampleCriteria.andFullnameLike("%"+contactsId+"%");
            List<TblContacts> tblContacts = tblContactsMapper.selectByExample(tblContactsExample);
            if (tblContacts==null || tblContacts.size()==0){
                throw new ResultException("联系人无数据，查询失败");
            }
            //包含联系人id的集合
            List<String> contactsIds = new ArrayList<String>();
            for (TblContacts tblContact : tblContacts) {
                contactsIds.add(tblContact.getId());
            }
            tranExampleCriteria.andContactsidIn(contactsIds);
        }

        if (stage!=null && !"".equals(stage)){
            tranExampleCriteria.andStageEqualTo(stage);
        }

        if (type!=null && !"".equals(type)){
            tranExampleCriteria.andStageEqualTo(type);
        }

        if (source!=null && !"".equals(source)){
            tranExampleCriteria.andStageEqualTo(source);
        }

        PageHelper.startPage(pageNum, pageSize);
        List<TblTran> tblTrans = tblTranMapper.selectByExample(tblTranExample);
        if (tblTrans==null || tblTrans.size()==0){
            throw new ResultException("无数据，查询失败");
        }

        //转换id值
        for (TblTran tblTran : tblTrans) {
            TblUser tblUser = tblUserMapper.selectByPrimaryKey(tblTran.getOwner());
            TblCustomer tblCustomer = tblCustomerMapper.selectByPrimaryKey(tblTran.getCustomerid());
            TblContacts tblContacts = tblContactsMapper.selectByPrimaryKey(tblTran.getContactsid());
            tblTran.setOwner(tblUser.getName());
            tblTran.setCustomerid(tblCustomer.getName());
            tblTran.setContactsid(tblContacts.getFullname());
        }
        PageInfo<TblTran> pageInfo = new PageInfo<TblTran>(tblTrans);
        PageResult pageResult = new PageResult(pageInfo.getTotal(), pageInfo.getList());

        return pageResult;
    }

    @Override
    public List<TblActivity> getAllActivity() {
        List<TblActivity> tblActivities = tblActivityMapper.selectByExample(null);
        if (tblActivities==null || tblActivities.size()==0){
            throw new ResultException("无市场活动，查新失败");
        }
        for (TblActivity tblActivity : tblActivities) {
            TblUser tblUser = tblUserMapper.selectByPrimaryKey(tblActivity.getOwner());
            tblActivity.setOwner(tblUser.getName());
        }
        return tblActivities;
    }

    @Override
    public List<TblContacts> getAllContacts() {
        List<TblContacts> tblContacts = tblContactsMapper.selectByExample(null);
        if (tblContacts==null || tblContacts.size()==0){
            throw new ResultException("无联系人，查新失败");
        }
        for (TblContacts tblContact : tblContacts) {
            TblUser tblUser = tblUserMapper.selectByPrimaryKey(tblContact.getOwner());
            tblContact.setOwner(tblUser.getName());
        }
        return tblContacts;
    }

    @Override
    public List<Map<String, String>> getCustomer(String name) {
        TblCustomerExample tblCustomerExample = new TblCustomerExample();
        TblCustomerExample.Criteria criteria = tblCustomerExample.createCriteria();
        criteria.andNameLike("%"+name+"%");
        List<TblCustomer> tblCustomers = tblCustomerMapper.selectByExample(tblCustomerExample);
        if (tblCustomers==null || tblCustomers.size()==0){
            throw new ResultException("无客户数据，查询失败");
        }
        List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
        for (TblCustomer tblCustomer : tblCustomers) {
            Map<String, String> map = new HashMap<>();
            map.put("name",tblCustomer.getName());
            mapList.add(map);
        }
        return mapList;
    }

    @Override
    public String getCustomerId(String customerName, String customerOwner, String createby) {
        TblCustomerExample tblCustomerExample = new TblCustomerExample();
        TblCustomerExample.Criteria criteria = tblCustomerExample.createCriteria();
        criteria.andNameEqualTo(customerName);
        List<TblCustomer> tblCustomers = tblCustomerMapper.selectByExample(tblCustomerExample);
        if (tblCustomers == null || tblCustomers.size()==0){
            TblCustomer tblCustomer = new TblCustomer();
            tblCustomer.setId(UUIDUtil.getUUID());
            tblCustomer.setName(customerName);
            tblCustomer.setOwner(customerOwner);
            tblCustomer.setCreateby(createby);
            tblCustomer.setCreatetime(DateTimeUtil.getSysTime());
            tblCustomerMapper.insertSelective(tblCustomer);
            return tblCustomer.getId();
        }else {
           return tblCustomers.get(0).getId();
        }
    }

    @Override
    public void addTran(TblTran tblTran, String createby) {
        String curTime = DateTimeUtil.getSysTime();
        String curTranId = UUIDUtil.getUUID();
        //创建交易
        if (tblTran == null){
            throw new ResultException("创建失败，无交易信息");
        }
        tblTran.setCreatetime(curTime);
        tblTran.setId(curTranId);
        tblTran.setCreateby(createby);
        if (tblTran.getOwner()==null || "".equals(tblTran.getOwner())){
            throw new ResultException("创建失败，所有者不能为空");
        }
        if (tblTran.getName()==null || "".equals(tblTran.getName())){
            throw new ResultException("创建失败，名称不能为空");
        }
        if (tblTran.getExpecteddate()==null || "".equals(tblTran.getExpecteddate())){
            throw new ResultException("创建失败，预计成交时间不能为空");
        }
        if (tblTran.getCustomerid()==null || "".equals(tblTran.getCustomerid())){
            throw new ResultException("创建失败，客户不能为空");
        }
        if (tblTran.getStage()==null || "".equals(tblTran.getStage())){
            throw new ResultException("创建失败，状态不能为空");
        }
        try {
            tblTranMapper.insertSelective(tblTran);
        } catch (ResultException e) {
            throw new ResultException("创建失败，请重试");
        }


        //创建交易历史
        TblTranHistory tblTranHistory = new TblTranHistory();
        tblTranHistory.setId(UUIDUtil.getUUID());
        tblTranHistory.setTranid(curTranId);
        tblTranHistory.setCreateby(createby);
        tblTranHistory.setCreatetime(curTime);
        tblTranHistory.setExpecteddate(tblTran.getExpecteddate());
        tblTranHistory.setMoney(tblTran.getMoney());
        tblTranHistory.setStage(tblTran.getStage());

        tblTranHistoryMapper.insertSelective(tblTranHistory);

        /*//创建联系人市场活动关系
        TblContactsActivityRelation tblContactsActivityRelation = new TblContactsActivityRelation();
        tblContactsActivityRelation.setId(UUIDUtil.getUUID());
        tblContactsActivityRelation.setActivityid(tblTran.getActivityid());
        tblContactsActivityRelation.setContactsid(tblTran.getContactsid());

        tblContactsActivityRelationMapper.insertSelective(tblContactsActivityRelation);*/


    }

    @Override
    public TblTran getTranById(String id) {
        TblTran tblTran = tblTranMapper.selectByPrimaryKey(id);
        if (tblTran == null){
            throw new ResultException("无交易数据，查询失败");
        }

        TblCustomer tblCustomer = tblCustomerMapper.selectByPrimaryKey(tblTran.getCustomerid());
        if (tblCustomer == null){
            throw new ResultException("无客户数据，查询失败");
        }
        tblTran.setCustomername(tblCustomer.getName());

        TblContacts tblContacts = tblContactsMapper.selectByPrimaryKey(tblTran.getContactsid());
        if (tblContacts == null){
            throw new ResultException("无联系人数据，查询失败");
        }
        tblTran.setContactsname(tblContacts.getFullname());

        TblActivity tblActivity = tblActivityMapper.selectByPrimaryKey(tblTran.getActivityid());
        if (tblActivity == null){
            throw new ResultException("无市场活动数据，查询失败");
        }
        tblTran.setActivityname(tblActivity.getName());

        TblUser tblUser = tblUserMapper.selectByPrimaryKey(tblTran.getOwner());
        if (tblUser == null){
            throw new ResultException("无所有者数据，查询失败");
        }
        tblTran.setOwnername(tblUser.getName());

        return tblTran;
    }

    @Override
    public void updateTran(TblTran tblTran, String name) {
        String curTime = DateTimeUtil.getSysTime();
        //更新交易信息
        tblTran.setEdittime(curTime);
        tblTran.setEditby(name);
        tblTranMapper.updateByPrimaryKeySelective(tblTran);
        //交易历史
        TblTranHistory tblTranHistory = new TblTranHistory();
        tblTranHistory.setId(UUIDUtil.getUUID());
        tblTranHistory.setStage(tblTran.getStage());
        tblTranHistory.setMoney(tblTran.getMoney());
        tblTranHistory.setExpecteddate(tblTran.getExpecteddate());
        tblTranHistory.setCreatetime(curTime);
        tblTranHistory.setCreateby(name);
        tblTranHistory.setTranid(tblTran.getId());

        tblTranHistoryMapper.insert(tblTranHistory);

    }

    @Override
    public void delTranById(String[] ids) {
        if (ids==null || ids.length==0){
            throw new ResultException("无数据可删除");
        }

        //先删除备注表
        for (String id : ids) {
            TblTranRemarkExample tblTranRemarkExample = new TblTranRemarkExample();
            TblTranRemarkExample.Criteria criteria = tblTranRemarkExample.createCriteria();
            criteria.andTranidEqualTo(id);

            try {
                tblTranRemarkMapper.deleteByExample(tblTranRemarkExample);
                tblTranMapper.deleteByPrimaryKey(id);
            } catch (ResultException e) {
                throw new ResultException("删除失败");
            }
        }
    }


    /*
    * 返回数据的结构
    *
    * Map<String, Object>
    *     [
    *       dicValues:{},
    *       point:7,
    *       result:[
    *           text：当前状态
    *           index：当前下标
    *           type：标志（1绿圈， 2锚点， 3黑圈， 4红叉， 5黑叉）
    *              ]
    *     ]
    *
    *
    *
    * */
    @Override
    public Map<String, Object> getStages(String tranId, Set<TblDicValue> dicSet,  Map<String, String> pMap) {

        Map<String, Object> ObjMap = new HashMap<String, Object>();
        //字典stage的集合
        List<TblDicValue> dicValues = new ArrayList<>(dicSet);

        //获取0的分界点
        int point = 0;
        for (int i=0; i<dicValues.size(); i++){
            //遍历所有tbldicValue
            TblDicValue tblDicValue = dicValues.get(i);
            //遍历所有tblDicValue的value值
            String value = tblDicValue.getValue();
            //遍历所有可能性的值
            String poss = pMap.get(value);

            //判断可能性为0分界点的下标
            if ("0".equals(poss)){
                point = i;
                break;
            }
        }

        //获取当前的状态 和 可能性
        TblTran tblTran = tblTranMapper.selectByPrimaryKey(tranId);
        if (tblTran == null){
            throw new ResultException("无数据");
        }
        String currStage = tblTran.getStage();
        String currPoss = pMap.get(currStage);


        List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();


        //判断各种可能性

        if ("0".equals(currPoss)){//锚点在最后两个
            for (int i=0; i<dicValues.size(); i++) {
                TblDicValue tblDicValue = dicValues.get(i);
                String stage = tblDicValue.getValue();
                String poss = pMap.get(stage);
                if (poss.equals(currPoss)) {//当前可能性为0的两种情况
                    if (stage.equals(currStage)) {
                        //红叉
                        Map<String, String> resultMap = new HashMap<String, String>();
                        resultMap.put("text", stage);
                        resultMap.put("index", i + "");
                        resultMap.put("type", "4");
                        resultList.add(resultMap);
                    } else {
                        //黑叉
                        Map<String, String> resultMap = new HashMap<String, String>();
                        resultMap.put("text", stage);
                        resultMap.put("index", i + "");
                        resultMap.put("type", "5");
                        resultList.add(resultMap);
                    }
                } else {
                    //前面七个都为黑圈
                    Map<String, String> resultMap = new HashMap<String, String>();
                    resultMap.put("text", stage);
                    resultMap.put("index", i + "");
                    resultMap.put("type", "3");
                    resultList.add(resultMap);

                }
            }
        }else {
            //锚点在前面七个
            //判断锚点位置
            int flag = 0;
            for (int i=0; i<dicValues.size(); i++){
                TblDicValue tblDicValue = dicValues.get(i);
                String stage = tblDicValue.getValue();
                String poss = pMap.get(stage);
                if (currPoss.equals(poss)){
                    flag = i;
                    break;
                }
            }
            for (int i=0; i<dicValues.size(); i++){
                TblDicValue tblDicValue = dicValues.get(i);
                String stage = tblDicValue.getValue();
                String poss = pMap.get(stage);
                if (i < flag){
                    //绿圈
                    Map<String, String> resultMap = new HashMap<String, String>();
                    resultMap.put("text", stage);
                    resultMap.put("index", i + "");
                    resultMap.put("type", "1");
                    resultList.add(resultMap);
                }else if (i == flag){
                    //锚点
                    Map<String, String> resultMap = new HashMap<String, String>();
                    resultMap.put("text", stage);
                    resultMap.put("index", i + "");
                    resultMap.put("type", "2");
                    resultList.add(resultMap);
                }else if ( i > flag && i < point){
                    //黑圈
                    Map<String, String> resultMap = new HashMap<String, String>();
                    resultMap.put("text", stage);
                    resultMap.put("index", i + "");
                    resultMap.put("type", "3");
                    resultList.add(resultMap);
                }else {
                    //黑叉
                    Map<String, String> resultMap = new HashMap<String, String>();
                    resultMap.put("text", stage);
                    resultMap.put("index", i + "");
                    resultMap.put("type", "5");
                    resultList.add(resultMap);
                }
            }
        }
        ObjMap.put("dicValues",dicValues);
        ObjMap.put("point",point);
        ObjMap.put("resultList",resultList);

        return ObjMap;
    }

    @Override
    public TblTran addHistory(TblTranHistory tblTranHistory) {
        String sysTime = DateTimeUtil.getSysTime();
        //创建新交易历史
        tblTranHistory.setId(UUIDUtil.getUUID());
        tblTranHistory.setCreatetime(sysTime);

        //更新交易
        TblTran tblTran = new TblTran();
        tblTran.setId(tblTranHistory.getTranid());
        tblTran.setStage(tblTranHistory.getStage());
        tblTran.setEditby(tblTranHistory.getCreateby());
        tblTran.setEdittime(sysTime);

        try {
            tblTranMapper.updateByPrimaryKeySelective(tblTran);
            tblTranHistoryMapper.insertSelective(tblTranHistory);
        } catch (ResultException e) {
            throw new ResultException("更改失败");
        }

        TblTran tblTranNew = tblTranMapper.selectByPrimaryKey(tblTranHistory.getTranid());

        return tblTranNew;
    }

    @Override
    public List<TblTranHistory> getAllHistory(String tranId) {
        TblTranHistoryExample tblTranHistoryExample = new TblTranHistoryExample();
        TblTranHistoryExample.Criteria criteria = tblTranHistoryExample.createCriteria();
        criteria.andTranidEqualTo(tranId);
        List<TblTranHistory> tblTranHistories = tblTranHistoryMapper.selectByExample(tblTranHistoryExample);
        if (tblTranHistories == null || tblTranHistories.size()==0){
            throw new ResultException("无数据，查询失败");
        }

        return tblTranHistories;
    }

    @Override
    public List<TblTranRemark> getAllRemark(String tranId) {
        TblTranRemarkExample tblTranRemarkExample = new TblTranRemarkExample();
        TblTranRemarkExample.Criteria criteria = tblTranRemarkExample.createCriteria();
        criteria.andTranidEqualTo(tranId);
        List<TblTranRemark> tblTranRemarks = tblTranRemarkMapper.selectByExample(tblTranRemarkExample);
        if (tblTranRemarks==null || tblTranRemarks.size()==0){
             throw new ResultException("无数据，查询失败");
        }
        return tblTranRemarks;
    }

    @Override
    public void delRemark(String id) {
        try {
            tblTranRemarkMapper.deleteByPrimaryKey(id);
        } catch (ResultException e) {
            throw new ResultException("删除失败");
        }
    }

    @Override
    public TblTranRemark editRemark(String id) {
        TblTranRemark tblTranRemark = tblTranRemarkMapper.selectByPrimaryKey(id);
        if (tblTranRemark == null){
            throw new ResultException("无数据，查询失败");
        }
        return tblTranRemark;
    }

    @Override
    public void updateRemark(TblTranRemark tblTranRemark) {
        tblTranRemark.setEditflag("1");
        tblTranRemark.setEdittime(DateTimeUtil.getSysTime());
        try {
            tblTranRemarkMapper.updateByPrimaryKeySelective(tblTranRemark);
        } catch (ResultException e) {
            throw new ResultException("更新失败");
        }
    }

    @Override
    public void addRemark(TblTranRemark tblTranRemark) {
        tblTranRemark.setCreatetime(DateTimeUtil.getSysTime());
        tblTranRemark.setEditflag("0");
        tblTranRemark.setId(UUIDUtil.getUUID());
        try {
            tblTranRemarkMapper.insertSelective(tblTranRemark);
        } catch (ResultException e) {
            throw new ResultException("增加失败");
        }
    }
}
