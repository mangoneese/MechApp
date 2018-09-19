package com.mercury.mechanize.Model;

public class Providers {
    private String name;
    private String date;
    private int status;
    private String service;

    public Providers(String name, String date, int status, String service) {
        this.name = name;
        this.date = date;
        this.status = status;
        this.service = service;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public int getStatus() {
        return status;
    }

    public String getService() {
        return service;
    }
}
