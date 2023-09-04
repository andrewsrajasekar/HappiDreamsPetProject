package com.happidreampets.app.email;

import java.util.List;

public class EmailModel {
    private List<String> recipients;
    private String msgBody;
    private String subject;
    private Boolean isHtml = Boolean.FALSE;

    public Boolean getIsHtml() {
        return isHtml;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public void setBody(String msgBody, Boolean isHtml) {
        this.isHtml = isHtml;
        this.msgBody = msgBody;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
