package com.powernode.settings.service.impl;

import com.powernode.exception.ResultException;
import com.powernode.utils.DicComparator;
import com.powernode.settings.mapper.TblDicTypeMapper;
import com.powernode.settings.mapper.TblDicValueMapper;
import com.powernode.settings.pojo.TblDicType;
import com.powernode.settings.pojo.TblDicValue;
import com.powernode.settings.pojo.TblDicValueExample;
import com.powernode.settings.service.TblDicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TblDicServiceImpl implements TblDicService {
    @Autowired
    private TblDicTypeMapper dicTypeMapper;
    @Autowired
    private TblDicValueMapper dicValueMapper;

    @Override
    public Map<String, Set<TblDicValue>> getDicMap() {
        Map<String, Set<TblDicValue>> dicMap = new HashMap<>();
        List<TblDicType> dicTypeList = dicTypeMapper.selectByExample(null);


        for (TblDicType dicType: dicTypeList){
            String code = dicType.getCode();
            TblDicValueExample tblDicValueExample = new TblDicValueExample();
            TblDicValueExample.Criteria criteria = tblDicValueExample.createCriteria();
            criteria.andTypecodeEqualTo(code);
            List<TblDicValue> valueList = dicValueMapper.selectByExample(tblDicValueExample);
            Set<TblDicValue> valueSet = new TreeSet<>(new DicComparator());
            valueSet.addAll(valueList);
            dicMap.put(code,valueSet);
        }
        if (dicMap==null || dicMap.size()==0){
            throw new ResultException("为找到数据");
        }
        return dicMap;
    }
}
