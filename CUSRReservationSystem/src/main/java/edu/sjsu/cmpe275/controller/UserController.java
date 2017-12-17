package edu.sjsu.cmpe275.controller;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import edu.sjsu.cmpe275.model.User;
import edu.sjsu.cmpe275.model.Response;
import edu.sjsu.cmpe275.model.User;
import edu.sjsu.cmpe275.repository.UserRepository;


@RestController 
public class UserController {

	@Autowired
	private UserRepository userRepository;
	
	@Transactional(Transactional.TxType.REQUIRED)
	@RequestMapping(value="/user", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createUser(@RequestParam(value = "name", required = true) String name,
												@RequestParam(value = "email", required = true) String email,
												@RequestParam(value = "password", required = true) String password){
		if(name == null || password == null || email == null){
			return new ResponseEntity<>(new Response(400, "Missing required parameter"), HttpStatus.BAD_REQUEST);
		}
		User user = userRepository.save(new User(name, email, password));
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
}
