package com.cloud.Enchere.model;

public class Recharge {
    private int id;
    private User user;
    private double montant;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
