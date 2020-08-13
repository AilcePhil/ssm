package com.powernode.utils;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.powernode.exception.ResultException;

/**
 * 自定义响应结构
 */
public class Result {

    // 定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();

    //响应结果判断
    private boolean success;

       // 响应业务状态
    private Integer status;

    // 响应消息
    private String msg;

    // 响应中的数据
    private Object data;


    private ResultException resultException;

    public static Result build(boolean success,Integer status, String msg, Object data) {
        return new Result(success, status, msg, data);
    }
    public static Result build(boolean success,Integer status, String msg) {
        return new Result(success, status, msg);
    }


    public static Result ok(Object data) {
        return new Result(data);
    }

    public static Result ok() {
        return new Result(null);
    }

    public Result() {

    }


    public static Result build(Integer status,ResultException resultException) {

        return  new Result(false, status,resultException.getMessage());
    }

    public static Result build(ResultException resultException) {

        return  new Result(false, resultException.getExceptionEnum().getCode(), resultException.getExceptionEnum().getMsg());
    }


    public static Result build(Integer status, String msg) {
        return new Result(false, status, msg, null);
    }

    public Result(boolean success, Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
        this.success = success;
    }

    public Result(boolean success, Integer status, String msg) {
        this.status = status;
        this.msg = msg;
        this.success = success;
    }

    public Result(Object data) {
        this.status = 200;
        this.msg = "OK";
        this.data = data;
        this.success = true;
    }




    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * 将json结果集转化为Result对象
     * 
     * @param jsonData json数据
     * @param clazz Result中的object类型
     * @return
     */
    public static Result formatToPojo(String jsonData, Class<?> clazz) {
        try {
            if (clazz == null) {
                return MAPPER.readValue(jsonData, Result.class);
            }
            JsonNode jsonNode = MAPPER.readTree(jsonData);
            JsonNode data = jsonNode.get("data");
            Object obj = null;
            if (clazz != null) {
                if (data.isObject()) {
                    obj = MAPPER.readValue(data.traverse(), clazz);
                } else if (data.isTextual()) {
                    obj = MAPPER.readValue(data.asText(), clazz);
                }
            }
            return build(jsonNode.get("success").asBoolean(),jsonNode.get("status").intValue(), jsonNode.get("msg").asText(), obj);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 没有object对象的转化
     * 
     * @param json
     * @return
     */
    public static Result format(String json) {
        try {
            return MAPPER.readValue(json, Result.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Object是集合转化
     * 
     * @param jsonData json数据
     * @param clazz 集合中的类型
     * @return
     */
    public static Result formatToList(String jsonData, Class<?> clazz) {
        try {
            JsonNode jsonNode = MAPPER.readTree(jsonData);
            JsonNode data = jsonNode.get("data");
            Object obj = null;
            if (data.isArray() && data.size() > 0) {
                obj = MAPPER.readValue(data.traverse(),
                        MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
            }
            return build(jsonNode.get("success").asBoolean(), jsonNode.get("status").intValue(), jsonNode.get("msg").asText(), obj);
        } catch (Exception e) {
            return null;
        }
    }

}
