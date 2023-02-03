package com.cloud.Enchere.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cloud.Enchere.exceptions.NotEnoughSoldException;
import com.cloud.Enchere.exceptions.SelfEnchereException;
import com.cloud.Enchere.exceptions.SurEnchereMontantException;
import com.cloud.Enchere.model.Enchere;
import com.cloud.Enchere.model.SearchModel;
import com.cloud.Enchere.model.Categorie;
import com.cloud.Enchere.model.Data;
import com.cloud.Enchere.model.SurEnchere;
import com.cloud.Enchere.service.EnchereService;
import com.cloud.Enchere.service.SurEnchereService;

import java.sql.SQLException;
import java.util.List;

@RestController
@CrossOrigin
public class EnchereController {
    @Autowired
    private EnchereService enchereService;

    @Autowired
    private SurEnchereService surEnchereService;

    @GetMapping("encheres")
    public List<Enchere> all_enchere() throws ClassNotFoundException, SQLException {
        return enchereService.fetchAll();
    }

    // new
    @GetMapping("encheres/{userid}/status/{status_value}")
    public List<Enchere> all_enchere(@PathVariable("userid") int userid, @PathVariable("status_value") int status) throws ClassNotFoundException, SQLException {
        return enchereService.findByUserAndStatus(userid, status);
    }

    @GetMapping("encheres/{enchereid}") 
    public Enchere get_enchere_by_id(@PathVariable("enchereid") int id) throws ClassNotFoundException, SQLException {
        return enchereService.fetchById(id);
    }

    @PostMapping("encheres/surencheres")
    public Data rencherir(@RequestBody SurEnchere surEnchere) throws ClassNotFoundException, SQLException {
        Data data = null;
        try {
			surEnchereService.rencherir(surEnchere);
		} catch (ClassNotFoundException | SQLException e) {
			throw e;
		} catch (SurEnchereMontantException | NotEnoughSoldException | SelfEnchereException e) {
            data = new Data();
			data.setException(e.getMessage());
		}

        return data;
    }

    @PostMapping("encheres/search")
    public Data search(@RequestBody SearchModel searchModel) throws ClassNotFoundException, SQLException {
        return enchereService.search(searchModel);
    }

    @GetMapping("encheres/{userid}/history")
    public List<Enchere> findHistory(@PathVariable("userid") int userId) throws ClassNotFoundException, SQLException {
        return enchereService.findByUser(userId);
    }

    @PostMapping("encheres")
    public Enchere saveEnchere(@RequestBody Enchere newEnchere) throws ClassNotFoundException, SQLException {
        return enchereService.saveEnchere(newEnchere);
    }

    @GetMapping("encheres/{userid}/expired")
    public List<Enchere> getExpired(@PathVariable("userid") int userid) throws ClassNotFoundException, SQLException {
        return enchereService.getExpiredByUser(userid);
    }

    // new
    @GetMapping("encheres/categories")
    public List<Categorie> getCategorie() throws ClassNotFoundException, SQLException {
        return enchereService.findAllCategorie();
    }
}