package com.zqq.common.core.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageInfo;
import com.zqq.common.core.domain.R;
import com.zqq.common.core.domain.TableDataInfo;

import java.util.List;

public class BaseController {

     public R<Void> toR(int rows){
         return rows>0?R.ok():R.fail();
     }

     public R<Void> toR(boolean result){
         return result?R.ok():R.fail();
     }

     public TableDataInfo getTableDataInfo(List<?> list){
         if(CollectionUtil.isEmpty(list)){
             return TableDataInfo.empty();
         }
         long total = new PageInfo<>(list).getTotal();
         return TableDataInfo.success(list,total);
     }

}
