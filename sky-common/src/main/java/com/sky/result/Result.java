package com.sky.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 后端统一返回结果
 * @param <T>
 */
@Data
public class Result<T> implements Serializable{

    private  T data;  //return data

    private String msg; //return error msg

    private Integer code; // return 1 represent success ,0 and other is failure

    public static <T> Result<T> success(){
        Result<T> result = new Result<>();
        result.code = 1;
        return result;
    }
    public static <T> Result<T> success( T object){
        Result<T> result = new Result<>();
        result.code = 1;
        result.data = object;
        return result;
    }
    public static <T> Result<T> error(String msg){
        Result result = new Result();
        result.code = 0;
        result.msg = msg;
        return result;
    }
}