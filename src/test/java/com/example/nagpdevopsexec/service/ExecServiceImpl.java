package com.example.nagpdevopsexec.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.nagpdevopsexec.service.impl.ExecImpl;

@SpringBootTest
public class ExecServiceImpl {

	@Autowired
	private ExecImpl execImpl;
	
	@Test
	public void testHelloWorld() {
		String testName = execImpl.helloWorld();
		Assert.assertEquals("Hello World, I'm Nagp Exercise", testName);
	}
}
