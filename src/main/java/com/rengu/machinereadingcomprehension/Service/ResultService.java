package com.rengu.machinereadingcomprehension.Service;

import com.rengu.machinereadingcomprehension.Entity.ResultEntity;

public class ResultService {

    public static ResultEntity<Object> resultBuilder(Object data) {
        ResultEntity<Object> resultEntity = new ResultEntity<>();
        resultEntity.setData(data);
        return resultEntity;
    }
}
