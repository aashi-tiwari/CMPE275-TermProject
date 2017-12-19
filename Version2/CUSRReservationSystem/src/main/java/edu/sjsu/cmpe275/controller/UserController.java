package edu.sjsu.cmpe275.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import edu.sjsu.cmpe275.repository.UserRepository;


@RestController 
public class UserController {

	@Autowired
	private UserRepository userRepository;
}
