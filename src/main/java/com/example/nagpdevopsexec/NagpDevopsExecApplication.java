package com.example.nagpdevopsexec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class NagpDevopsExecApplication extends SpringBootServletInitializer{

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(NagpDevopsExecApplication.class);
   }

	public static void main(String[] args) {
		SpringApplication.run(NagpDevopsExecApplication.class, args);
	}

}
