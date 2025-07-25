package com.zvonbot.interview.dto.zvonobot;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ZvonobotCallRequest {
    
    @JsonProperty("apiKey")
    private String apiKey;
    
    @JsonProperty("phone")
    private String phone;
    
    @JsonProperty("dutyPhone")
    private Integer dutyPhone;
    
    @JsonProperty("outgoingPhone")
    private String outgoingPhone;
    
    @JsonProperty("record")
    private Record record;
    
    @JsonProperty("recordId")
    private Integer recordId;
    
    @JsonProperty("ivrs")
    private Ivr[] ivrs;
    
    public ZvonobotCallRequest() {}
    
    public ZvonobotCallRequest(String apiKey, String phone, String text) {
        this.apiKey = apiKey;
        this.phone = phone;
        this.dutyPhone = 1; // Используем случайный дежурный номер
        this.record = new Record(text);
    }
    
    public ZvonobotCallRequest(String apiKey, String phone, String text, String outgoingPhone) {
        this.apiKey = apiKey;
        this.phone = phone;
        this.dutyPhone = 1; // Используем случайный дежурный номер
        this.record = new Record(text);
        this.outgoingPhone = outgoingPhone;
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public Integer getDutyPhone() {
        return dutyPhone;
    }
    
    public void setDutyPhone(Integer dutyPhone) {
        this.dutyPhone = dutyPhone;
    }
    
    public String getOutgoingPhone() {
        return outgoingPhone;
    }
    
    public void setOutgoingPhone(String outgoingPhone) {
        this.outgoingPhone = outgoingPhone;
    }
    
    public Record getRecord() {
        return record;
    }
    
    public void setRecord(Record record) {
        this.record = record;
    }
    
    public Integer getRecordId() {
        return recordId;
    }
    
    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }
    
    public Ivr[] getIvrs() {
        return ivrs;
    }
    
    public void setIvrs(Ivr[] ivrs) {
        this.ivrs = ivrs;
    }
    
    @Override
    public String toString() {
        return "ZvonobotCallRequest{" +
                "apiKey='" + (apiKey != null ? apiKey.substring(0, Math.min(10, apiKey.length())) + "..." : "null") + '\'' +
                ", phone='" + phone + '\'' +
                ", dutyPhone=" + dutyPhone +
                ", outgoingPhone='" + outgoingPhone + '\'' +
                ", record=" + record +
                ", recordId=" + recordId +
                ", ivrs=" + (ivrs != null ? ivrs.length + " items" : "null") +
                '}';
    }
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Record {
        @JsonProperty("text")
        private String text;
        
        @JsonProperty("gender")
        private Integer gender;
        
        @JsonProperty("id")
        private Integer id;
        
        public Record() {}
        
        public Record(String text) {
            this.text = text;
            this.gender = 0; // Женский голос по умолчанию
        }
        
        public String getText() {
            return text;
        }
        
        public void setText(String text) {
            this.text = text;
        }
        
        public Integer getGender() {
            return gender;
        }
        
        public void setGender(Integer gender) {
            this.gender = gender;
        }
        
        public Integer getId() {
            return id;
        }
        public void setId(Integer id) {
            this.id = id;
        }
        
        @Override
        public String toString() {
            return "Record{" +
                    "text='" + text + '\'' +
                    ", gender=" + gender +
                    '}';
        }
    }
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Ivr {
        @JsonProperty("digit")
        private Integer digit;
        
        @JsonProperty("smsText")
        private String smsText;
        
        @JsonProperty("needBlock")
        private Integer needBlock;
        
        public Ivr() {}
        
        public Integer getDigit() {
            return digit;
        }
        
        public void setDigit(Integer digit) {
            this.digit = digit;
        }
        
        public String getSmsText() {
            return smsText;
        }
        
        public void setSmsText(String smsText) {
            this.smsText = smsText;
        }
        
        public Integer getNeedBlock() {
            return needBlock;
        }
        
        public void setNeedBlock(Integer needBlock) {
            this.needBlock = needBlock;
        }
        
        @Override
        public String toString() {
            return "Ivr{" +
                    "digit=" + digit +
                    ", smsText='" + smsText + '\'' +
                    ", needBlock=" + needBlock +
                    '}';
        }
    }
} 