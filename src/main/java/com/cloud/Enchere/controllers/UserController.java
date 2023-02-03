package com.cloud.Enchere.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cloud.Enchere.database.DatabaseConnection;
import com.cloud.Enchere.exceptions.AuthenticationException;
import com.cloud.Enchere.model.AuthModel;
import com.cloud.Enchere.model.Recharge;
import com.cloud.Enchere.model.User;
import com.cloud.Enchere.service.CompteService;
import com.cloud.Enchere.service.UsersService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
@CrossOrigin
public class UserController {

	@Autowired
	UsersService usersService;

	@Autowired
	CompteService compteService;

	// new
	@GetMapping("users/{userid}")
	public User getById(@PathVariable("userid") int userid) throws ClassNotFoundException, SQLException {
		User user = new User();
		user.setIdUser(userid);
		user.findById();
		user.setPassword(null);
		
		return user;
	}

	// new
	@PostMapping("users/{userid}")
	public AuthModel relog(@PathVariable("userid") int userid) throws SQLException, ClassNotFoundException {
		Connection connection = null;
		AuthModel authModel = new AuthModel();

		try {
			connection = new DatabaseConnection().connect();
			User auth = new User();
			auth.setIdUser(userid);
			auth.findById(connection);
			User authenticated_User = null;
			try {
				authenticated_User = usersService.authenticate(connection, auth);
			} catch (AuthenticationException e) {
				authModel.setErrorMessage(e.getMessage());
				return authModel;
			} 
			String token = getJWTToken(auth.getUsername());
			authModel.setUserId(authenticated_User.getIdUser());
			authModel.setAccessToken(token);
		} catch (ClassNotFoundException | SQLException e) {
			throw e;
		} finally {
			if(connection != null && !connection.isClosed()) connection.close();
		}
		return authModel;
	}

	@PostMapping("users/recharge")
	public Recharge recharger(@RequestBody Recharge recharge) throws SQLException, ClassNotFoundException {
		return compteService.demande_recharge(recharge);
	}

	@PostMapping("users/register")
	public User sign_up(@RequestBody User newUser) throws SQLException, ClassNotFoundException {
		return usersService.sign_up(newUser);
	}

    @PostMapping("users/authenticate")
	public AuthModel login(@RequestBody User auth) throws ClassNotFoundException, SQLException {
		AuthModel authModel = new AuthModel();
		User authenticated_User = null;
		try {
			authenticated_User = usersService.authenticate(auth);
		} catch (AuthenticationException e) {
			authModel.setErrorMessage(e.getMessage());
			return authModel;
		} 
		String token = getJWTToken(auth.getUsername());
		authModel.setUserId(authenticated_User.getIdUser());
		authModel.setAccessToken(token);

		return authModel;
	}

	private String getJWTToken(String username) {
		String secretKey = "mySecretKey";
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils
				.commaSeparatedStringToAuthorityList("ROLE_USER");
		
		String token = Jwts
				.builder()
				.setId("softtekJWT")
				.setSubject(username)
				.claim("authorities",
						grantedAuthorities.stream()
								.map(GrantedAuthority::getAuthority)
								.collect(Collectors.toList()))
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 600000))
				.signWith(SignatureAlgorithm.HS512,
						secretKey.getBytes()).compact();

		return "Bearer " + token;
	}
}
