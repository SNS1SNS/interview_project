package com.zvonbot.interview.dto.zvonobot;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ZvonobotResponse {
    
    @JsonProperty("result")
    private String result;
    
    @JsonProperty("error")
    private String error;
    
    @JsonProperty("data")
    private Object data;
    
    public ZvonobotResponse() {}
    
    public String getResult() {
        return result;
    }
    
    public void setResult(String result) {
        this.result = result;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public boolean isSuccess() {
        // API Звонобота возвращает "ok" при успехе, но также может возвращать null при успешном ответе
        // если есть данные пользователя и нет ошибки, считаем ответ успешным
        return "ok".equals(result) || (result == null && error == null && data != null);
    }
    
    @Override
    public String toString() {
        return "ZvonobotResponse{" +
                "result='" + result + '\'' +
                ", error='" + error + '\'' +
                ", data=" + data +
                '}';
    }
} 