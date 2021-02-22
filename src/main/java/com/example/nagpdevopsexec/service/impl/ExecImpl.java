package com.example.nagpdevopsexec.service.impl;

import org.springframework.stereotype.Service;

import com.example.nagpdevopsexec.service.ExecService;

@Service
public class ExecImpl implements ExecService {

	@Override
	public String helloWorld() {
		return "Hello World, I'm Nagp Exercise";
	}

}
