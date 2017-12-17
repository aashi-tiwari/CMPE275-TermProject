package edu.sjsu.cmpe275.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import edu.sjsu.cmpe275.repository.TrainRepository;

@RestController
public class TrainController {
	
	@Autowired
	private TrainRepository trainRepository;
}
