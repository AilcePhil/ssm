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
import com.powernode.workbench.mapper.TblContactsMapper;
import com.powernode.workbench.mapper.TblCustomerMapper;
import com.powernode.workbench.mapper.TblCustomerRemarkMapper;
import com.powernode.workbench.mapper.TblTranMapper;
import com.powernode.workbench.pojo.*;
import com.powernode.workbench.service.TblCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TblCustomerServiceImpl implements TblCustomerService {

    @Autowired
    private TblCustomerMapper tblCustomerMapper;
    @Autowired
    private TblUserMapper tblUserMapper;
    @Autowired
    private TblCustomerRemarkMapper tblCustomerRemarkMapper;
    @Autowired
    private TblTranMapper tblTranMapper;
    @Autowired
    private TblContactsMapper tblContactsMapper;


    @Override
    public PageResult selectAllCust(int pageNum, int pageSize, String name, String owner, String phone, String website) {
        TblCustomerExample tblCustomerExample = new TblCustomerExample();
        TblCustomerExample.Criteria customerCriteria = tblCustomerExample.createCriteria();

        //条件查询
        if (name != null && !"".equals(name)){
            customerCriteria.andNameLike("%"+name+"%");
        }
        if (owner != null && !"".equals(owner)){
            TblUserExample tblUserExample = new TblUserExample();
            TblUserExample.Criteria userCriteria = tblUserExample.createCriteria();
            userCriteria.andNameLike("%"+owner+"%");
            List<TblUser> tblUsers = tblUserMapper.selectByExample(tblUserExample);
            if (tblUsers == null || tblUsers.size() == 0){
                throw new ResultException("未找到数据，查询失败");
            }

            List<String> usersIds = new ArrayList<String>();
            for (TblUser tblUser : tblUsers){
                usersIds.add(tblUser.getId());
            }
            customerCriteria.andOwnerIn(usersIds);
        }
        if (phone != null && !"".equals(phone)){
            customerCriteria.andPhoneLike("%"+phone+"%");
        }
        if (website != null && !"".equals(website)){
            customerCriteria.andWebsiteLike("%"+website+"%");
        }

        PageHelper.startPage(pageNum, pageSize);
        List<TblCustomer> tblCustomers = tblCustomerMapper.selectByExample(tblCustomerExample);
        for (TblCustomer tblCustomer : tblCustomers) {
            TblUser tblUser = tblUserMapper.selectByPrimaryKey(tblCustomer.getOwner());
            tblCustomer.setOwner(tblUser.getName());
        }
        if (tblCustomers==null || tblCustomers.size()==0){
            throw new ResultException("未找到数据，查询失败");
        }
        PageInfo<TblCustomer> pageInfo = new PageInfo<>(tblCustomers);
        PageResult pageResult = new PageResult(pageInfo.getTotal(), pageInfo.getList());

        return pageResult;
    }

    @Override
    public void addCustomer(TblCustomer tblCustomer) {
        tblCustomer.setId(UUIDUtil.getUUID());
        tblCustomer.setCreatetime(DateTimeUtil.getSysTime());
        try {
            tblCustomerMapper.insertSelective(tblCustomer);
        } catch (ResultException e) {
            throw new ResultException("增加失败");
        }
    }

    @Override
    public TblCustomer updateCustomer(String id) {
        TblCustomer tblCustomer = tblCustomerMapper.selectByPrimaryKey(id);
        TblUser tblUser = tblUserMapper.selectByPrimaryKey(tblCustomer.getOwner());
        tblCustomer.setOwner(tblUser.getName());
        if (tblCustomer == null){
            throw new ResultException("无数据，查询失败");
        }
        return tblCustomer;
    }

    @Override
    public void updateSave(TblCustomer tblCustomer) {
        tblCustomer.setEdittime(DateTimeUtil.getSysTime());
        try {
            tblCustomerMapper.updateByPrimaryKeySelective(tblCustomer);
        } catch (ResultException e) {
            throw new ResultException("跟新失败");
        }
    }

    @Override
    public void deleteCustomer(String[] ids) {
        if (ids == null || ids.length == 0){
            throw new ResultException("参数不能为空");
        }
        try {
            for (String id : ids){
                //由于关联了remake表，所以先删除remake表
                TblCustomerRemarkExample tblCustomerRemarkExample = new TblCustomerRemarkExample();
                TblCustomerRemarkExample.Criteria criteria = tblCustomerRemarkExample.createCriteria();
                criteria.andIdEqualTo(id);

                tblCustomerRemarkMapper.deleteByExample(tblCustomerRemarkExample);
                tblCustomerMapper.deleteByPrimaryKey(id);
            }
        } catch (ResultException e) {
            throw new  ResultException("删除失败");
        }
    }

    @Override
    public TblCustomer selectById(String customerId) {
        TblCustomer tblCustomer = tblCustomerMapper.selectByPrimaryKey(customerId);
        TblUser tblUser = tblUserMapper.selectByPrimaryKey(tblCustomer.getOwner());
        tblCustomer.setOwner(tblUser.getName());
        if (tblCustomer == null){
            throw new ResultException("无数据，查询失败");
        }
        return tblCustomer;
    }

    @Override
    public List<TblCustomerRemark> getAllRemark(String customerId) {
        TblCustomerRemarkExample tblCustomerRemarkExample = new TblCustomerRemarkExample();
        TblCustomerRemarkExample.Criteria criteria = tblCustomerRemarkExample.createCriteria();
        criteria.andCustomeridEqualTo(customerId);
        List<TblCustomerRemark> tblCustomerRemarks = tblCustomerRemarkMapper.selectByExample(tblCustomerRemarkExample);
        if (tblCustomerRemarks==null || tblCustomerRemarks.size()==0){
            throw new ResultException("无数据，查询失败");
        }
        return tblCustomerRemarks;
    }

    @Override
    public void addRemark(TblCustomerRemark tblCustomerRemark) {
        tblCustomerRemark.setId(UUIDUtil.getUUID());
        tblCustomerRemark.setCreatetime(DateTimeUtil.getSysTime());
        tblCustomerRemark.setEditflag("0");
        try {
            tblCustomerRemarkMapper.insertSelective(tblCustomerRemark);
        } catch (ResultException e) {
            throw new ResultException("增加失败");
        }
    }

    @Override
    public TblCustomerRemark editRemark(String id) {
        TblCustomerRemark tblCustomerRemark = tblCustomerRemarkMapper.selectByPrimaryKey(id);
        if (tblCustomerRemark == null){
            throw new ResultException("无数据，查询失败");
        }
        return tblCustomerRemark;
    }

    @Override
    public void delRemark(String id) {
        try {
            tblCustomerRemarkMapper.deleteByPrimaryKey(id);
        } catch (ResultException e) {
            throw new ResultException("删除失败");
        }
    }

    @Override
    public void updateRemark(TblCustomerRemark tblCustomerRemark) {
        tblCustomerRemark.setEditflag("1");
        tblCustomerRemark.setEdittime(DateTimeUtil.getSysTime());
        try {
            tblCustomerRemarkMapper.updateByPrimaryKeySelective(tblCustomerRemark);
        } catch (ResultException e) {
            throw new ResultException("更新失败");
        }
    }

    @Override
    public List<TblTran> getAllTran(String customerId) {
        TblTranExample tblTranExample = new TblTranExample();
        TblTranExample.Criteria criteria = tblTranExample.createCriteria();
        criteria.andCustomeridEqualTo(customerId);
        List<TblTran> tblTrans = tblTranMapper.selectByExample(tblTranExample);
        if (tblTrans==null || tblTrans.size()==0){
            throw new ResultException("无数据，查询失败");
        }
        return tblTrans;
    }

    @Override
    public List<TblContacts> getAllContacts(String customerId) {
        TblContactsExample tblContactsExample = new TblContactsExample();
        TblContactsExample.Criteria criteria = tblContactsExample.createCriteria();
        criteria.andCustomeridEqualTo(customerId);
        List<TblContacts> tblContacts = tblContactsMapper.selectByExample(tblContactsExample);
        if (tblContacts==null || tblContacts.size()==0){
            throw new ResultException("无数据，查询失败");
        }
        return tblContacts;
    }


}
