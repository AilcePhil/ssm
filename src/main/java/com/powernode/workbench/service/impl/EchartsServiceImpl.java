package com.powernode.workbench.service.impl;

import com.powernode.exception.ResultException;
import com.powernode.workbench.mapper.TblActivityMapper;
import com.powernode.workbench.mapper.TblClueMapper;
import com.powernode.workbench.mapper.TblContactsMapper;
import com.powernode.workbench.mapper.TblTranMapper;
import com.powernode.workbench.service.EchartsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EchartsServiceImpl implements EchartsService {

    @Autowired
    private TblActivityMapper tblActivityMapper;
    @Autowired
    private TblClueMapper tblClueMapper;
    @Autowired
    private TblContactsMapper tblContactsMapper;
    @Autowired
    private TblTranMapper tblTranMapper;

    @Override
    public Map<String, Object> getOwnerCount() {

        List<Map<String, String>> owner = tblActivityMapper.getOwnerCount();
        int count = tblActivityMapper.count();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("owner", owner);
        map.put("rows",count);

        return map;
    }

    @Override
    public List<List<Integer>> getActCost() {
        List<Integer> actCost = tblActivityMapper.getActCost();
        if (actCost==null || actCost.size()==0){
            throw new ResultException("无数据，查询失败");
        }

        List<List<Integer>> allCostList = new ArrayList<List<Integer>>();
        int i = 1;
        for (Integer integer : actCost) {
            List<Integer> costList = new ArrayList<Integer>();
            costList.add(i);
            costList.add(integer);
            allCostList.add(costList);
            i++;
        }

        return allCostList;
    }

    @Override
    public Map actAvgCost() {
        int avg = tblActivityMapper.actAvgCost();
        List<Integer> actCost = tblActivityMapper.getActCost();
        if (actCost==null || actCost.size()==0){
            throw new ResultException("无数据，查询失败");
        }

        List<Integer> cost = new ArrayList<Integer>();
        List<Integer> count = new ArrayList<Integer>();
        int i=1;
        for (Integer integer : actCost) {
            count.add(i);
            i++;
            cost.add(integer - avg);
        }
        Map map = new HashMap();
        map.put("cost",cost);
        map.put("count", count);

        return map;
    }

    @Override
    public List<Map<String, String>> getClueSource() {
        List<Map<String, String>> clueSource = tblClueMapper.getClueSource();
        if (clueSource==null || clueSource.size()==0){
            throw new ResultException("无数据，查询失败");
        }

        return clueSource;
    }

    @Override
    public List<Map<String, String>> getContactsSource() {
        List<Map<String, String>> contactsSource = tblContactsMapper.getContactsSource();
        if (contactsSource==null || contactsSource.size()==0){
            throw new ResultException("无数据，查询失败");
        }
        return contactsSource;
    }

    @Override
    public Map getClueState() {
        List<String> clueState = tblClueMapper.getClueState();
        if (clueState==null || clueState.size()==0){
            throw new ResultException("无数据，查询失败");
        }
        List<Integer> clueStateCount = tblClueMapper.getClueStateCount();
        if (clueStateCount==null || clueStateCount.size()==0){
            throw new ResultException("无数据，查询失败");
        }
        Map map = new HashMap();
        map.put("state", clueState);
        map.put("stateCount", clueStateCount);
        return map;
    }


    @Override
    public  Map getClueAddress() {
        List<Map<String, String>> clueAddress = tblClueMapper.getClueAddress();
        if (clueAddress==null || clueAddress.size()==0){
            throw new ResultException("无数据，查询失败");
        }
        List<String> clueAddresses = tblClueMapper.getClueAddresses();
        if (clueAddresses==null || clueAddresses.size()==0){
            throw new ResultException("无数据，查询失败");
        }
        Integer count = tblClueMapper.clueCount();

        Map map = new HashMap();
        map.put("clueAddress",clueAddress);
        map.put("clueAddresses", clueAddresses);
        map.put("clueCount", count);
        return map;
    }

    @Override
    public Map contactsAddress() {
        int count = tblContactsMapper.count();

        List<Map<String,Object>> orgList = new ArrayList<>();
        List<String> strings = tblContactsMapper.contactsAddress();
        for (String string : strings) {
            Map<String,Object> orgMap = new HashMap<>();
            orgMap.put("name",string);
            orgMap.put("max",count);
            orgList.add(orgMap);
        }

        List<Integer> addressCount = tblContactsMapper.contactsAddressCount();
        Map map = new HashMap();
        map.put("orgList",orgList);
        map.put("addressCount",addressCount);

        return map;
    }

    @Override
    public Map<String, Object> getTranMoney() {
        List<Integer> money = tblTranMapper.tranMoney();
        List<String> name = tblTranMapper.tranName();
        Map map = new HashMap();
        map.put("money",money);
        map.put("name", name);
        return map;
    }

    @Override
    public Map getTranMoney2() {
        List<String> name = tblTranMapper.tranName();
        List<Map<String, String>> maps = tblTranMapper.tranMoney2();
        Map map = new HashMap();
        map.put("maps",maps);
        map.put("name", name);
        return map;
    }
}
