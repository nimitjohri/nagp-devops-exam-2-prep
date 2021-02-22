package com.example.nagpdevopsexec.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.nagpdevopsexec.service.ExecService;

@RestController
public class ExecController {
	@Autowired
	ExecService execService;
	
	@GetMapping("/")
	public String helloWorld() {
		return execService.helloWorld();
	}
}
