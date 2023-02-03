package com.cloud.Enchere.model;

import java.sql.Date;

public class SearchModel {
    private String description;
    private Date date;
    private Double prix;
    private Integer categorieId;
    private Integer statut;

    public Integer getStatut() {
        return statut;
    }

    public void setStatut(Integer statut) {
        this.statut = statut;
    }

    public Integer getCategorieId() {
        return categorieId;
    }

    public void setCategorieId(Integer categorieId) {
        this.categorieId = categorieId;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
