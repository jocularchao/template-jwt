package top.jocularchao.entity;

/**
 * @author jocularchao
 * @date 2023-12-28 18:38
 * @description 装一些用于当前响应的基本信息
 */

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.fasterxml.jackson.databind.util.JSONPObject;

/**
 *
 * @param code  状态码
 * @param data  数据
 * @param message   异常信息
 * @param <T>   具体的数据的类型不确定
 */
public record RestBean<T>(int code, T data,String message) {

    //请求成功
    public static <T> RestBean<T> success(T data){
        return new RestBean<>(200,data,"请求成功");
    }

    //默认的请求成功
    public static <T> RestBean<T> success(){
        return success(null);
    }

    //打印
    public String asJsonString(){
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);   //WriteNull 不加上的话，前端的lang就没有了
    }


    //请求失败
    public static <T> RestBean<T> failure(int code, String message){
        return new RestBean<>(code,null,message);
    }

    //简化请求失败  因为只要失败了就是401所以可以简化参数
    public static <T> RestBean<T> unauthorized(String message){
        return failure(401,message);
    }

}
