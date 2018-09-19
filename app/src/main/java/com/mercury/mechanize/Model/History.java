package com.mercury.mechanize.Model;

public class History {
    private String jobId;
    private String time;

    public History(String JobId, String time){
        this.jobId = JobId;
        this.time = time;

    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
