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
import com.powernode.workbench.mapper.*;
import com.powernode.workbench.pojo.*;
import com.powernode.workbench.service.TblClueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TblClueServiceImpl implements TblClueService {

    @Autowired
    private TblClueMapper tblClueMapper;
    @Autowired
    private TblUserMapper tblUserMapper;
    @Autowired
    private TblClueRemarkMapper tblClueRemarkMapper;
    @Autowired
    private TblActivityMapper tblActivityMapper;
    @Autowired
    private TblClueActivityRelationMapper tblClueActivityRelationMapper;
    @Autowired
    private TblCustomerMapper tblCustomerMapper;
    @Autowired
    private TblCustomerRemarkMapper tblCustomerRemarkMapper;
    @Autowired
    private TblContactsMapper tblContactsMapper;
    @Autowired
    private TblContactsRemarkMapper tblContactsRemarkMapper;
    @Autowired
    private TblContactsActivityRelationMapper tblContactsActivityRelationMapper;
    @Autowired
    private TblTranMapper tblTranMapper;
    @Autowired
    private TblTranHistoryMapper tblTranHistoryMapper;


    //保存创建线索信息
    @Override
    public void saveCreateClue(TblClue tblClue) {
        tblClue.setId(UUIDUtil.getUUID());
        tblClue.setCreatetime(DateTimeUtil.getSysTime());
        try {
            tblClueMapper.insertSelective(tblClue);
        } catch (ResultException e) {
            throw new ResultException("增加失败");
        }
    }

    @Override
    public PageResult pageFindClues(int pageSize, int pageNo, String fullname, String company, String phone, String source, String owner, String mphone, String state) {
        PageHelper.startPage(pageNo, pageSize);

        TblClueExample tblClueExample = new TblClueExample();
        TblClueExample.Criteria criteria = tblClueExample.createCriteria();
        //对参数的判断
        if (fullname!=null && !"".equals(fullname)){
            criteria.andFullnameLike("%"+fullname+"%");
        }

        if (company!=null && !"".equals(company)){
            criteria.andCompanyLike("%"+company+"%");
        }

        if (phone!=null && !"".equals(phone)){
            criteria.andPhoneLike("%"+phone+"%");
        }

        if (source!=null && !"".equals(source)){
            criteria.andSourceEqualTo(source);
        }

        if (owner!=null && !"".equals(owner)){
            TblUserExample tblUserExample = new TblUserExample();
            TblUserExample.Criteria userCriteria = tblUserExample.createCriteria();
            userCriteria.andNameLike("%"+owner+"%");
            List<TblUser> tblUsers = tblUserMapper.selectByExample(tblUserExample);

            List<String> id = new ArrayList<>();
            for (TblUser tblUser : tblUsers){
                id.add(tblUser.getId());
            }
            criteria.andOwnerIn(id);
        }

        if (mphone!=null && !"".equals(mphone)){
            criteria.andMphoneLike("%"+mphone+"%");
        }

        if (state!=null && !"".equals(state)){
            criteria.andStateEqualTo(state);
        }
        //结果数组
        List<TblClue> tblClues = tblClueMapper.selectByExample(tblClueExample);

        if (tblClues==null || tblClues.size()==0){
            throw new ResultException("未找到数据");
        }
        //把owner的id转换为name
        for (TblClue tblClue : tblClues){
            TblUser tblUser = tblUserMapper.selectByPrimaryKey(tblClue.getOwner());

            tblClue.setOwner(tblUser.getName());
        }
        //使用分页插件转换
        PageInfo<TblClue> pageInfo = new PageInfo<>(tblClues);
        PageResult pageResult = new PageResult(pageInfo.getTotal(), pageInfo.getList());
        return pageResult;
    }

    @Override
    public void delClueById(String[] ids) {
        if (ids == null || ids.length == 0){
            throw new ResultException("参数不能为空");
        }

        try {
            for (String id : ids){
                //由于关联了remake表，所以先删除remake表
                TblClueRemarkExample tblClueRemarkExample = new TblClueRemarkExample();
                TblClueRemarkExample.Criteria criteria = tblClueRemarkExample.createCriteria();
                criteria.andClueidEqualTo(id);

                tblClueRemarkMapper.deleteByExample(tblClueRemarkExample);
                tblClueMapper.deleteByPrimaryKey(id);
            }
        } catch (ResultException e) {
            throw new  ResultException("删除失败");
        }
    }

    @Override
    public TblClue selectClueById(String id) {
        try {
            TblClue tblClue = tblClueMapper.selectByPrimaryKey(id);
            TblUser tblUser = tblUserMapper.selectByPrimaryKey(tblClue.getOwner());
            tblClue.setOwner(tblUser.getName());
            return tblClue;
        } catch (ResultException e) {
            throw new  ResultException("查询失败");
        }

    }

    @Override
    public void updateClue(TblClue tblClue) {
        tblClue.setEdittime(DateTimeUtil.getSysTime());
        try {
            tblClueMapper.updateByPrimaryKeySelective(tblClue);
        } catch (ResultException e) {
            throw new ResultException("更新失败");
        }
    }

    @Override
    public TblClue getAllDetails(String id) {
        try {

            TblClue tblClue = tblClueMapper.selectByPrimaryKey(id);
            TblUser tblUser = tblUserMapper.selectByPrimaryKey(tblClue.getOwner());
            tblClue.setOwner(tblUser.getName());
            return tblClue;
        } catch (ResultException e) {
            throw new ResultException("查询失败");
        }
    }

    @Override
    public void convert(String clueId, String activityId, String stage, String money, String tranName, String exDate, String createby, String customerName, String tranFlag) {
        String curTime = DateTimeUtil.getSysTime();
        //根据线索id,获取线索信息
        TblClue tblClue = tblClueMapper.selectByPrimaryKey(clueId);
        if (tblClue == null){
            throw new ResultException("无数据，查询失败");
        }

        //根据客户名称判断是否已经有当前客户，如果没有就新建客户
        TblCustomerExample customerExample = new TblCustomerExample();
        TblCustomerExample.Criteria customerCriteria = customerExample.createCriteria();
        customerCriteria.andNameEqualTo(customerName);
        List<TblCustomer> tblCustomers = tblCustomerMapper.selectByExample(customerExample);
        TblCustomer tblCustomer = null;
        if (tblCustomers == null || tblCustomers.size() == 0){
            //没有联系人，则创建一个新的联系人
            tblCustomer = new TblCustomer();
            tblCustomer.setId(UUIDUtil.getUUID());
            tblCustomer.setOwner(tblClue.getOwner());
            tblCustomer.setName(customerName);
            tblCustomer.setWebsite(tblClue.getWebsite());
            tblCustomer.setPhone(tblClue.getPhone());
            tblCustomer.setCreateby(createby);
            tblCustomer.setCreatetime(curTime);
            tblCustomer.setContactsummary(tblClue.getContactsummary());
            tblCustomer.setNextcontacttime(tblClue.getNextcontacttime());
            tblCustomer.setDescription(tblClue.getDescription());
            tblCustomer.setAddress(tblClue.getAddress());

            tblCustomerMapper.insert(tblCustomer);

        }else {
            tblCustomer = tblCustomers.get(0);
        }

        //创建联系人信息
        TblContacts tblContacts = new TblContacts();
        tblContacts.setId(UUIDUtil.getUUID());
        tblContacts.setOwner(tblClue.getOwner());
        tblContacts.setSource(tblClue.getSource());
        tblContacts.setCustomerid(tblCustomer.getId());
        tblContacts.setFullname(tblClue.getFullname());
        tblContacts.setAppellation(tblClue.getAppellation());
        tblContacts.setEmail(tblClue.getEmail());
        tblContacts.setMphone(tblClue.getMphone());
        tblContacts.setJob(tblClue.getJob());
        tblContacts.setCreateby(createby);
        tblContacts.setCreatetime(curTime);
        tblContacts.setDescription(tblClue.getDescription());
        tblContacts.setContactsummary(tblClue.getContactsummary());
        tblContacts.setNextcontacttime(tblClue.getNextcontacttime());
        tblContacts.setAddress(tblClue.getAddress());

        tblContactsMapper.insertSelective(tblContacts);


        //把线索备注信息转换为联系人和客户备注信息
        TblClueRemarkExample clueRemarkExample = new TblClueRemarkExample();
        TblClueRemarkExample.Criteria clueRemarkCriteria = clueRemarkExample.createCriteria();
        clueRemarkCriteria.andClueidEqualTo(clueId);
        List<TblClueRemark> tblClueRemarks = tblClueRemarkMapper.selectByExample(clueRemarkExample);
        if (tblClueRemarks != null){
            for (TblClueRemark tblClueRemark : tblClueRemarks) {
                TblContactsRemark tblContactsRemark = new TblContactsRemark();
                tblContactsRemark.setId(UUIDUtil.getUUID());
                tblContactsRemark.setNotecontent(tblClueRemark.getNotecontent());
                tblContactsRemark.setCreateby(createby);
                tblContactsRemark.setCreatetime(curTime);
                tblContactsRemark.setEditflag("0");
                tblContactsRemark.setContactsid(tblContacts.getId());

                tblContactsRemarkMapper.insert(tblContactsRemark);


                TblCustomerRemark tblCustomerRemark = new TblCustomerRemark();
                tblCustomerRemark.setId(UUIDUtil.getUUID());
                tblCustomerRemark.setNotecontent(tblClueRemark.getNotecontent());
                tblCustomerRemark.setCreateby(createby);
                tblCustomerRemark.setCreatetime(curTime);
                tblCustomerRemark.setEditflag("0");
                tblCustomerRemark.setCustomerid(tblCustomer.getId());

                tblCustomerRemarkMapper.insert(tblCustomerRemark);

            }
        }


        //把线索市场关联关系 转化为 联系人市场关联关系
        TblClueActivityRelationExample tblClueActivityRelationExample = new TblClueActivityRelationExample();
        TblClueActivityRelationExample.Criteria clueActivityRelationCriteria = tblClueActivityRelationExample.createCriteria();
        clueActivityRelationCriteria.andClueidEqualTo(clueId);
        List<TblClueActivityRelation> tblClueActivityRelations = tblClueActivityRelationMapper.selectByExample(tblClueActivityRelationExample);
        for (TblClueActivityRelation tblClueActivityRelation : tblClueActivityRelations) {
            TblContactsActivityRelation tblContactsActivityRelation = new TblContactsActivityRelation();
            tblContactsActivityRelation.setId(UUIDUtil.getUUID());
            tblContactsActivityRelation.setContactsid(tblContacts.getId());
            tblContactsActivityRelation.setActivityid(tblClueActivityRelation.getActivityid());

            tblContactsActivityRelationMapper.insert(tblContactsActivityRelation);

        }

        //判断是否存在交易，参数为空时加以不存在
        if ("1".equals(tranFlag)){
            if (money==null || "".equals(money) || activityId==null || "".equals(activityId) || stage==null || "".equals(stage) || tranName==null || "".equals(tranName) || exDate==null || "".equals(exDate)){
                throw new ResultException("信息不完整，交易失败");
            }
            //交易
            TblTran tblTran = new TblTran();
            tblTran.setId(UUIDUtil.getUUID());
            tblTran.setOwner(tblClue.getOwner());
            tblTran.setMoney(money);
            tblTran.setName(tranName);
            tblTran.setExpecteddate(exDate);
            tblTran.setCustomerid(tblCustomer.getId());
            tblTran.setStage(stage);
            tblTran.setSource(tblClue.getSource());
            tblTran.setActivityid(activityId);
            tblTran.setContactsid(tblContacts.getId());
            tblTran.setCreateby(createby);
            tblTran.setCreatetime(curTime);
            tblTran.setDescription(tblClue.getDescription());
            tblTran.setContactsummary(tblClue.getContactsummary());
            tblTran.setNextcontacttime(tblClue.getNextcontacttime());

            tblTranMapper.insert(tblTran);


            //加以历史创建
            TblTranHistory tblTranHistory = new TblTranHistory();
            tblTranHistory.setId(UUIDUtil.getUUID());
            tblTranHistory.setStage(tblTran.getStage());
            tblTranHistory.setMoney(tblTran.getMoney());
            tblTranHistory.setExpecteddate(tblTran.getExpecteddate());
            tblTranHistory.setCreatetime(curTime);
            tblTranHistory.setCreateby(createby);
            tblTranHistory.setTranid(tblTran.getId());

            tblTranHistoryMapper.insert(tblTranHistory);

        }

        //删除线索的备注信息表
        TblClueRemarkExample tblClueRemarkExample = new TblClueRemarkExample();
        TblClueRemarkExample.Criteria tblClueRemarkExampleCriteria = tblClueRemarkExample.createCriteria();
        tblClueRemarkExampleCriteria.andClueidEqualTo(clueId);
        tblClueRemarkMapper.deleteByExample(tblClueRemarkExample);
        //删除线索与市场的关联关系表
        TblClueActivityRelationExample tblClueActivityRelationExample1 = new TblClueActivityRelationExample();
        TblClueActivityRelationExample.Criteria relationExample1Criteria = tblClueActivityRelationExample1.createCriteria();
        relationExample1Criteria.andClueidEqualTo(clueId);
        tblClueActivityRelationMapper.deleteByExample(tblClueActivityRelationExample1);
        //删除线索表
        tblClueMapper.deleteByPrimaryKey(clueId);

    }




}
