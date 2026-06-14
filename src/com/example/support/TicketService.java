package com.example.support;

public class TicketService {

    public SupportTicket createTicket(String id, String title)
    {
        return new SupportTicket(id, title);
    }

    public void openTicket(SupportTicket ticket)
    {
        ticket.openTicket("OPEN");
    }

    public void printTicketUpdate(SupportTicket ticket)
    {
        ticket.printUpdate();
    }

    public void startInvestigation(SupportTicket ticket)
    {
        ticket.changeStatus("INVESTIGATING");
    }

    public void printTicketSummary(SupportTicket ticket)
    {
        ticket.printSummary();
    }
}
