package com.example.support;

public class main{

    public static void main(String[] args){
        SupportTicket ticket = new SupportTicket();

        ticket.id = "INC-12345";
        ticket.title = "Unable to access account";
        ticket.status = "Open";

        ticket.printSummary();
    }
}

