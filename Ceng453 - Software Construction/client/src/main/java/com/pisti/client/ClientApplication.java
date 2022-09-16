package com.pisti.client;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class ClientApplication {

	public static void main(String[] args) {
		Application.launch(PistiApplication.class, args);
	}

}
