package com.example.support;

public class SupportTicket {
    private String id;
    private String title;
    private String status;

    public SupportTicket(String id, String title) {
        this.id = id;
        this.title = title;
        this.status = "NEW";
    }

    public void openTicket(String status1) {
        this.status = status1;
    }

    public void printUpdate() {
        System.out.println(id + " - " + title + " [" + status + "]");
    }

    public void changeStatus(String status2) {
        this.status = status2;
    }

    public void printSummary() {
        System.out.println(id + " - " + title + " [" + status + "]");
    }
}