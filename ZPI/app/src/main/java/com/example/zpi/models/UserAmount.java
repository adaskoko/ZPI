package com.example.zpi.models;

public class UserAmount implements Comparable<UserAmount> {

    private User user;
    private Double amount;

    public UserAmount(User user, Double amount) {
        this.user = user;
        this.amount = amount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void addAmount(Double amount) {
        this.amount += amount;
    }

    public void subtractAmount(Double amount) {
        this.amount -= amount;
    }


    @Override
    public int compareTo(UserAmount userAmount) {
        return Double.compare(amount, userAmount.getAmount());
    }
}
