package com.example.zpi.models;

public class Debt {

    private User from;
    private User to;
    private Double amount;

    public Debt(User debtor, User payer, Double amount) {
        this.from = debtor;
        this.to = payer;
        this.amount = amount;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
