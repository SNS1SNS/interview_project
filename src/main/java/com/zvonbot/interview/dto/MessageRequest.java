package com.zvonbot.interview.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO для запросов на отправку сообщений (голосовых и SMS)
 */
public class MessageRequest {
    
    @NotBlank(message = "Номер телефона обязателен")
    @Pattern(regexp = "^\\+?7\\d{10}$", message = "Номер телефона должен быть в формате +7XXXXXXXXXX или 7XXXXXXXXXX")
    private String phone;
    
    @NotBlank(message = "Текст сообщения обязателен")
    private String text;
    
    @JsonProperty("recordId")
    private Integer recordId;
    
    @JsonProperty("outgoingPhone")
    private String outgoingPhone;
    
    public MessageRequest() {}
    
    public MessageRequest(String phone, String text) {
        this.phone = phone;
        this.text = text;
    }
    
    public MessageRequest(String phone, String text, Integer recordId) {
        this.phone = phone;
        this.text = text;
        this.recordId = recordId;
    }
    
    public MessageRequest(String phone, String text, Integer recordId, String outgoingPhone) {
        this.phone = phone;
        this.text = text;
        this.recordId = recordId;
        this.outgoingPhone = outgoingPhone;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    /**
     * Получает очищенный номер телефона (только цифры)
     * @return номер телефона без символов форматирования
     */
    public String getCleanPhone() {
        if (phone == null) return null;
        return phone.replaceAll("[^0-9]", "");
    }
    
    public Integer getRecordId() {
        return recordId;
    }
    
    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }
    
    public String getOutgoingPhone() {
        return outgoingPhone;
    }
    
    public void setOutgoingPhone(String outgoingPhone) {
        this.outgoingPhone = outgoingPhone;
    }
    
    @Override
    public String toString() {
        return "MessageRequest{" +
                "phone='" + phone + '\'' +
                ", text='" + text + '\'' +
                ", recordId=" + recordId +
                ", outgoingPhone='" + outgoingPhone + '\'' +
                '}';
    }
} 