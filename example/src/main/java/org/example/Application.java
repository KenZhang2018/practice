package org.example;

import org.example.service.CurrencyServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {


    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class, args);
        CurrencyServiceImpl.consoleOutput();
        CurrencyServiceImpl.handInputOfTerminal();
        int exit = SpringApplication.exit(applicationContext);
        System.exit(exit);
    }

}
