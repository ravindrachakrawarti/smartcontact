package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.entity.Contact;
import com.smart.entity.User;
import com.smart.repository.Contactrepository;
import com.smart.repository.StudentRepo;

@RestController
public class SearchController {

	@Autowired
	private StudentRepo studentrepo;
	@Autowired
	private Contactrepository contactrepository;
	
	
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query,Principal principal)
	{
		System.out.println(query);
		//principal current user dega
		User user= this.studentrepo.getUserByUserName(principal.getName());
		
		List<Contact> contacts=  this.contactrepository.findByNameContainingAndUser(query, user);
		
		return ResponseEntity.ok(contacts);
	}
}
