package com.powernode.workbench.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.powernode.exception.ResultException;
import com.powernode.settings.mapper.TblUserMapper;
import com.powernode.settings.pojo.TblUser;
import com.powernode.settings.pojo.TblUserExample;
import com.powernode.utils.DateTimeUtil;
import com.powernode.utils.PageResult;
import com.powernode.utils.Result;
import com.powernode.utils.UUIDUtil;
import com.powernode.workbench.mapper.*;
import com.powernode.workbench.pojo.*;
import com.powernode.workbench.service.TblContactsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TblContactsServiceImpl implements TblContactsService {

    @Autowired
    private TblContactsMapper tblContactsMapper;
    @Autowired
    private TblUserMapper tblUserMapper;
    @Autowired
    private TblCustomerMapper tblCustomerMapper;
    @Autowired
    private TblContactsRemarkMapper tblContactsRemarkMapper;
    @Autowired
    private TblTranMapper tblTranMapper;
    @Autowired
    private TblActivityMapper tblActivityMapper;
    @Autowired
    private TblContactsActivityRelationMapper tblContactsActivityRelationMapper;



    @Override
    public PageResult getAllContacts(int pageNum, int pageSize, String owner, String fullname, String customerid, String source, String birth) {
        TblContactsExample tblContactsExample = new TblContactsExample();
        TblContactsExample.Criteria criteria = tblContactsExample.createCriteria();

        if (owner!=null && !"".equals(owner)){
            TblUserExample tblUserExample = new TblUserExample();
            TblUserExample.Criteria userExampleCriteria = tblUserExample.createCriteria();
            userExampleCriteria.andNameLike("%"+owner+"%");
            List<TblUser> tblUsers = tblUserMapper.selectByExample(tblUserExample);
            if (tblUsers==null || tblUsers.size()==0){
                throw new ResultException("无所有者数据，查询失败");
            }

            List<String> userIds = new ArrayList<String>();
            for (TblUser tblUser : tblUsers) {
                userIds.add(tblUser.getId());
            }

            criteria.andOwnerIn(userIds);
        }
        if (fullname!=null && !"".equals(fullname)){
            criteria.andFullnameLike("%"+fullname+"%");
        }
        if (customerid!=null && !"".equals(customerid)){
            TblCustomerExample tblCustomerExample = new TblCustomerExample();
            TblCustomerExample.Criteria customerExampleCriteria = tblCustomerExample.createCriteria();
            customerExampleCriteria.andNameLike("%"+customerid+"%");
            List<TblCustomer> tblCustomers = tblCustomerMapper.selectByExample(tblCustomerExample);
            if (tblCustomers==null || tblCustomers.size()==0){
                throw new ResultException("无客户数据，查询失败");
            }
            List<String> customerIds = new ArrayList<String>();
            for (TblCustomer tblCustomer : tblCustomers) {
                customerIds.add(tblCustomer.getId());
            }
            criteria.andCustomeridIn(customerIds);
        }
        if (source!=null && !"".equals(source)){
            criteria.andSourceEqualTo(source);
        }
        if (birth!=null && !"".equals(birth)){
            criteria.andBirthLike("%"+birth+"%");
        }


        PageHelper.startPage(pageNum, pageSize);
        List<TblContacts> tblContactsList = tblContactsMapper.selectByExample(tblContactsExample);
        if (tblContactsList==null || tblContactsList.size()==0){
            throw new ResultException("无联系人数据，查询失败");
        }

        for (TblContacts tblContact : tblContactsList) {
            TblUser tblUser = tblUserMapper.selectByPrimaryKey(tblContact.getOwner());
            if (tblUser==null){
                throw new ResultException("无所有人数据，查询失败");
            }
            tblContact.setOwner(tblUser.getName());

            TblCustomer tblCustomer = tblCustomerMapper.selectByPrimaryKey(tblContact.getCustomerid());
            if (tblCustomer==null){
                throw new ResultException("无客户数据，查询失败");
            }
            tblContact.setCustomerid(tblCustomer.getName());
        }

        PageInfo<TblContacts> pageInfo = new PageInfo<TblContacts>(tblContactsList);
        PageResult pageResult = new PageResult(pageInfo.getTotal(),pageInfo.getList());

        return pageResult;
    }


    @Override
    public void addContacts(TblContacts tblContacts) {
        tblContacts.setCreatetime(DateTimeUtil.getSysTime());
        tblContacts.setId(UUIDUtil.getUUID());
        try {
            tblContactsMapper.insertSelective(tblContacts);
        } catch (ResultException e) {
            throw new ResultException("创建失败");
        }
    }

    @Override
    public TblContacts getContactsById(String contactsId) {
        TblContacts tblContacts = tblContactsMapper.selectByPrimaryKey(contactsId);
        if (tblContacts == null){
            throw new ResultException("无联系人数据");
        }

        TblCustomer tblCustomer = tblCustomerMapper.selectByPrimaryKey(tblContacts.getCustomerid());
        if (tblCustomer == null){
            throw new ResultException("无客户数据");
        }

        TblUser tblUser = tblUserMapper.selectByPrimaryKey(tblContacts.getOwner());
        if (tblUser == null){
            throw new ResultException("无所有者数据");
        }

        tblContacts.setOwner(tblUser.getName());
        tblContacts.setCustomerid(tblCustomer.getName());
        return tblContacts;
    }

    @Override
    public void updateSave(TblContacts tblContacts) {
        tblContacts.setEdittime(DateTimeUtil.getSysTime());
        try {
            tblContactsMapper.updateByPrimaryKeySelective(tblContacts);
        } catch (ResultException e) {
            throw new ResultException("更新失败");
        }
    }

    @Override
    public void delContacts(String[] ids) {
        if (ids ==null || ids.length==0){
            throw new ResultException("参数不能为空");
        }

        for (String id : ids) {
            //先删除关联的remark表
            TblContactsRemarkExample tblContactsRemarkExample = new TblContactsRemarkExample();
            TblContactsRemarkExample.Criteria criteria = tblContactsRemarkExample.createCriteria();
            criteria.andContactsidEqualTo(id);
            try {
                tblContactsRemarkMapper.deleteByExample(tblContactsRemarkExample);
                tblContactsMapper.deleteByPrimaryKey(id);
            } catch (ResultException e) {
                throw new ResultException("删除失败");
            }
        }
    }

    @Override
    public List<TblContactsRemark> getAllRemark(String contactsId) {
        TblContactsRemarkExample tblContactsRemarkExample = new TblContactsRemarkExample();
        TblContactsRemarkExample.Criteria criteria = tblContactsRemarkExample.createCriteria();
        criteria.andContactsidEqualTo(contactsId);
        List<TblContactsRemark> tblContactsRemarks = tblContactsRemarkMapper.selectByExample(tblContactsRemarkExample);
        return tblContactsRemarks;
    }

    @Override
    public void addRemark(TblContactsRemark tblContactsRemark) {
        tblContactsRemark.setEditflag("0");
        tblContactsRemark.setId(UUIDUtil.getUUID());
        tblContactsRemark.setCreatetime(DateTimeUtil.getSysTime());
        try {
            tblContactsRemarkMapper.insertSelective(tblContactsRemark);
        } catch (ResultException e) {
            throw new ResultException("增加失败");
        }
    }

    @Override
    public TblContactsRemark editRemark(String id) {
        TblContactsRemark tblContactsRemark = tblContactsRemarkMapper.selectByPrimaryKey(id);
        if (tblContactsRemark == null){
            throw new ResultException("无数据，查询失败");
        }
        return tblContactsRemark;
    }

    @Override
    public void updateRemark(TblContactsRemark tblContactsRemark) {
        tblContactsRemark.setEditflag("1");
        tblContactsRemark.setEdittime(DateTimeUtil.getSysTime());
        try {
            tblContactsRemarkMapper.updateByPrimaryKeySelective(tblContactsRemark);
        } catch (ResultException e) {
            throw new ResultException("更新失败");
        }
    }

    @Override
    public void delRemark(String id) {
        try {
            tblContactsRemarkMapper.deleteByPrimaryKey(id);
        } catch (ResultException e) {
            throw new ResultException("删除失败");
        }
    }

    @Override
    public List<TblTran> getAllTran(String contactsId) {
        TblTranExample tblTranExample = new TblTranExample();
        TblTranExample.Criteria criteria = tblTranExample.createCriteria();
        criteria.andContactsidEqualTo(contactsId);
        List<TblTran> tblTrans = tblTranMapper.selectByExample(tblTranExample);
        if (tblTrans==null || tblTrans.size()==0){
            throw new ResultException("无交易数据，查询失败");
        }
        return tblTrans;
    }

    @Override
    public List<TblActivity> getAllActivity(String contactsId) {
        TblContactsActivityRelationExample tblContactsActivityRelationExample = new TblContactsActivityRelationExample();
        TblContactsActivityRelationExample.Criteria criteria = tblContactsActivityRelationExample.createCriteria();
        criteria.andContactsidEqualTo(contactsId);
        List<TblContactsActivityRelation> tblContactsActivityRelations = tblContactsActivityRelationMapper.selectByExample(tblContactsActivityRelationExample);
        if (tblContactsActivityRelations==null || tblContactsActivityRelations.size()==0){
            throw new ResultException("无数据，查询失败");
        }
        //获得活动id集合
        List<TblActivity> activityList = new ArrayList<TblActivity>();
        for (TblContactsActivityRelation tblContactsActivityRelation : tblContactsActivityRelations) {
            TblActivity tblActivity = tblActivityMapper.selectByPrimaryKey(tblContactsActivityRelation.getActivityid());
            TblUser tblUser = tblUserMapper.selectByPrimaryKey(tblActivity.getOwner());
            tblActivity.setOwner(tblUser.getName());
            activityList.add(tblActivity);
        }
        return activityList;
    }

    @Override
    public void delActivity(String activityId) {
        //删除联系人市场活动关联关系表
        TblContactsActivityRelationExample tblContactsActivityRelationExample = new TblContactsActivityRelationExample();
        TblContactsActivityRelationExample.Criteria criteria = tblContactsActivityRelationExample.createCriteria();
        criteria.andActivityidEqualTo(activityId);
        List<TblContactsActivityRelation> tblContactsActivityRelations = tblContactsActivityRelationMapper.selectByExample(tblContactsActivityRelationExample);

        for (TblContactsActivityRelation tblContactsActivityRelation : tblContactsActivityRelations) {
            try {
                tblContactsActivityRelationMapper.deleteByPrimaryKey(tblContactsActivityRelation.getId());
            } catch (ResultException e) {
                throw new ResultException("删除失败");
            }
        }
    }

    @Override
    public List<TblActivity> unActivity(String contactsId) {
        TblContactsActivityRelationExample tblContactsActivityRelationExample = new TblContactsActivityRelationExample();
        TblContactsActivityRelationExample.Criteria criteria = tblContactsActivityRelationExample.createCriteria();
        criteria.andContactsidEqualTo(contactsId);
        List<TblContactsActivityRelation> tblContactsActivityRelations = tblContactsActivityRelationMapper.selectByExample(tblContactsActivityRelationExample);
        if (tblContactsActivityRelations==null || tblContactsActivityRelations.size()==0){

            List<TblActivity> activityList = tblActivityMapper.selectByExample(null);
            for (TblActivity tblActivity : activityList) {
                TblUser tblUser = tblUserMapper.selectByPrimaryKey(tblActivity.getOwner());
                tblActivity.setOwner(tblUser.getName());
            }
            return activityList;
        }
        //获得所有关联市场活动的id集合
        List<String> ids = new ArrayList<String>();
        for (TblContactsActivityRelation tblContactsActivityRelation : tblContactsActivityRelations) {
            ids.add(tblContactsActivityRelation.getActivityid());
        }

        TblActivityExample tblActivityExample = new TblActivityExample();
        TblActivityExample.Criteria criteria1 = tblActivityExample.createCriteria();
        criteria1.andIdNotIn(ids);
        List<TblActivity> activityList = tblActivityMapper.selectByExample(tblActivityExample);
        if (activityList==null || activityList.size()==0){
            throw new ResultException("无数据，查询失败");
        }
        for (TblActivity tblActivity : activityList) {
            TblUser tblUser = tblUserMapper.selectByPrimaryKey(tblActivity.getOwner());
            tblActivity.setOwner(tblUser.getName());
        }
        return activityList;
    }

    @Override
    public void relation(String[] aids, String contactsId) {
        if (aids == null || aids.length==0){
            throw new ResultException("无参数，关联失败");
        }
        for (String aid : aids) {
            TblContactsActivityRelation tblContactsActivityRelation = new TblContactsActivityRelation();
            tblContactsActivityRelation.setId(UUIDUtil.getUUID());
            tblContactsActivityRelation.setContactsid(contactsId);
            tblContactsActivityRelation.setActivityid(aid);
            try {
                tblContactsActivityRelationMapper.insert(tblContactsActivityRelation);
            } catch (Exception e) {
                throw new ResultException("关联失败");
            }
        }
    }
}
