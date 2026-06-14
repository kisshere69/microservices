package com.example.support;

public class SupportTicket {
    public String id;
    public String title;
    public String status;

    void printSummary() {
        System.out.println(id + " - " + title + " [" + status + "]");
    }
}
