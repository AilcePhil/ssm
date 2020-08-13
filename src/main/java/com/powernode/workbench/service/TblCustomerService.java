package com.powernode.workbench.service;

import com.powernode.utils.PageResult;
import com.powernode.workbench.mapper.TblCustomerRemarkMapper;
import com.powernode.workbench.pojo.TblContacts;
import com.powernode.workbench.pojo.TblCustomer;
import com.powernode.workbench.pojo.TblCustomerRemark;
import com.powernode.workbench.pojo.TblTran;

import java.util.List;

public interface TblCustomerService {

    PageResult selectAllCust(int pageNum, int pageSize, String name, String owner, String phone, String website);

    void addCustomer(TblCustomer tblCustomer);

    TblCustomer updateCustomer(String id);

    void updateSave(TblCustomer tblCustomer);

    void deleteCustomer(String[] ids);

    TblCustomer selectById(String customerId);

    List<TblCustomerRemark> getAllRemark(String customerId);

    void addRemark(TblCustomerRemark tblCustomerRemark);

    TblCustomerRemark editRemark(String id);

    void delRemark(String id);

    void updateRemark(TblCustomerRemark tblCustomerRemark);

    List<TblTran> getAllTran(String customerId);

    List<TblContacts> getAllContacts(String customerId);

}
