package com.example.demo;

import com.example.demo.menu.LibraryMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    private final LibraryMenu libraryMenu;
    private final Environment environment;

    @Autowired
    public DemoApplication(LibraryMenu libraryMenu, Environment environment) {
        this.libraryMenu = libraryMenu;
        this.environment = environment;
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (!Arrays.asList(environment.getActiveProfiles()).contains("test")) {
            libraryMenu.start();
        }
    }
}
