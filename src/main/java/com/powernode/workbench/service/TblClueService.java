package com.powernode.workbench.service;

import com.powernode.utils.PageResult;
import com.powernode.workbench.pojo.TblClue;


public interface TblClueService {
    void saveCreateClue(TblClue tblClue);

    PageResult pageFindClues(int pageSize, int pageNo, String fullname, String company, String phone,
                             String source, String owner, String mphone, String state);

    void delClueById(String[] ids);

    TblClue selectClueById(String id);

    void updateClue(TblClue tblClue);

    TblClue getAllDetails(String id);

    void convert(String clueId, String activityId, String stage, String money, String tranName, String exDate, String createby, String customerName, String tranFlag);
}
