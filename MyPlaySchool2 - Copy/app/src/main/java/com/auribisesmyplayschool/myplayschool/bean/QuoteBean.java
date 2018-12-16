package com.auribisesmyplayschool.myplayschool.bean;

/**
 * Created by Eshaan on 06-Jan-17.
 */
public class QuoteBean {
    int quote_id,status;
    String quote;

    public QuoteBean() {
    }

    public QuoteBean(int quote_id, int status, String quote) {
        this.quote_id = quote_id;
        this.status = status;
        this.quote = quote;
    }

    public int getQuote_id() {
        return quote_id;
    }

    public void setQuote_id(int quote_id) {
        this.quote_id = quote_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }
}
