package com.oreofactory.oreofactory;

import org.springframework.boot.SpringApplication;

public class TestOreoFactoryApplication {

    public static void main(String[] args) {
        SpringApplication.from(OreoFactoryApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
