package com.powernode.workbench.service;

import com.powernode.utils.PageResult;
import com.powernode.workbench.pojo.TblActivity;
import com.powernode.workbench.pojo.TblClue;

import java.util.List;
import java.util.Map;

public interface TblClueActivityRelationService {

    List<TblActivity> selectByName(String activityname, String clueid);

    List<Map<String, Object>> getRelaActivity(String clueid);

    void delRelation(String id);

    void addRelation(String[] ids, String clueid);

    PageResult selectByName(String activityname, String clueid, int pageNo, int pageSize);

    PageResult selectById(String clueId, String activityName, int pageNo, int pageSize);

}
