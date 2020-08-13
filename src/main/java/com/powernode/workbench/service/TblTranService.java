package com.powernode.workbench.service;

import com.powernode.settings.pojo.TblDicValue;
import com.powernode.utils.PageResult;
import com.powernode.workbench.pojo.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TblTranService {
    PageResult pageAllTran(int pageSize, int pageNum, String owner, String name, String customerId, String stage, String type, String source, String contactsId);

    List<TblActivity> getAllActivity();

    List<TblContacts> getAllContacts();

    List<Map<String, String>> getCustomer(String name);

    String getCustomerId(String customerName, String customerOwner, String createby);

    void addTran(TblTran tblTran, String createby);

    TblTran getTranById(String id);

    void updateTran(TblTran tblTran, String name);

    void delTranById(String[] ids);

    Map<String, Object> getStages(String tranId, Set<TblDicValue> dicSet,  Map<String, String> pMap);

    TblTran addHistory(TblTranHistory tblTranHistory);

    List<TblTranHistory> getAllHistory(String tranId);

    List<TblTranRemark> getAllRemark(String tranId);

    void delRemark(String id);

    TblTranRemark editRemark(String id);

    void updateRemark(TblTranRemark tblTranRemark);

    void addRemark(TblTranRemark tblTranRemark);
}
