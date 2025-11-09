package com.nanmax.hris;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;

@SpringBootApplication
@PWA(
    name = "HRIS Jasamedika",
    shortName = "HRIS",
    description = "Human Resource Information System - Jasamedika",
    backgroundColor = "#ffffff",
    themeColor = "#2196F3"
)
public class HrisApplication implements AppShellConfigurator {
    public static void main(String[] args) {
        SpringApplication.run(HrisApplication.class, args);
    }
}
