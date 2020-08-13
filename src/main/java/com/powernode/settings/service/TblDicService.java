package com.powernode.settings.service;

import com.powernode.settings.pojo.TblDicValue;

import java.util.Map;
import java.util.Set;

public interface TblDicService {
    //获得TblDicType和TblDicValue的集合
    Map<String, Set<TblDicValue>> getDicMap();

}
