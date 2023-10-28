package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
@Mapper
public interface OrderDetailMapper {
    void insertBatch(ArrayList<Object> orderDetailList);
}
