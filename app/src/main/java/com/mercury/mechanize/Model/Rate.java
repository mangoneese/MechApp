package com.mercury.mechanize.Model;

public class Rate {
    private String rates;
    private String comments;


    public Rate() {
    }

    public String getRates() {
        return rates;
    }

    public void setRates(String rates) {
        this.rates = rates;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Rate(String rates, String comment) {
        this.rates = rates;
        this.comments = comments;
    }
}
