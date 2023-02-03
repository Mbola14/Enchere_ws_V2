package com.cloud.Enchere.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
@CrossOrigin(origins = "*", maxAge = 3600)
public class EnchereController {
    @Autowired
    private EnchereService enchereService;

    @Autowired
    private SurEnchereService surEnchereService;


    // new
    @GetMapping("encheres/{userid}/status/{status_value}")
    public List<Enchere> all_enchere(@PathVariable("userid") int userid, @PathVariable("status_value") int status) throws ClassNotFoundException, SQLException {
        return enchereService.findByUserAndStatus(userid, status);
    }

    @GetMapping("encheres")
    public List<Enchere> all_enchere() throws ClassNotFoundException, SQLException {
        return enchereService.fetchAll();
    }

    // @GetMapping("categories")
    // public List<Categorie> all_categorie() throws ClassNotFoundException, SQLException {

    // }

    @GetMapping("encheres/{enchereid}") 
    public Enchere get_enchere_by_id(@PathVariable("enchereid") int id) throws ClassNotFoundException, SQLException {
        return enchereService.fetchById(id);
    }

    @PostMapping("encheres/surencheres")
    public ResponseEntity<Data> rencherir(@RequestBody SurEnchere surEnchere) throws ClassNotFoundException, SQLException {
        Data data = null;
        try {
            System.out.println("sousous");
			surEnchereService.rencherir(surEnchere);
            data = new Data();
            data.setResult("coucou");
		} catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
			throw e;
		} catch (SurEnchereMontantException | NotEnoughSoldException | SelfEnchereException e) {
            e.printStackTrace();
            data = new Data();
			data.setException(e.getMessage());
		}
        return new ResponseEntity<Data>(data, HttpStatus.OK);
    }

    @PostMapping("encheres/search")
    public Data search(@RequestBody SearchModel searchModel) throws Exception {
        return enchereService.search(searchModel);
    }

    @GetMapping("encheres/{userid}/history")
    public List<Enchere> findHistory(@PathVariable("userid") int userId) throws Exception {
        return enchereService.findByUser(userId);
    }

    @PostMapping("encheres")
    public Enchere saveEnchere(@RequestBody Enchere newEnchere) throws Exception {
        return enchereService.saveEnchere(newEnchere);
    }

    @GetMapping("encheres/{userid}/expired")
    public List<Enchere> getExpired(@PathVariable("userid") int userid) throws Exception {
        return enchereService.getExpiredByUser(userid);
    }

    // new
    @GetMapping("encheres/categories")
    public List<Categorie> getCategorie() throws ClassNotFoundException, SQLException {
        return enchereService.findAllCategorie();
    }
    }
}
