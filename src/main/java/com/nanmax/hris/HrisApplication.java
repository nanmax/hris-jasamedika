package com.nanmax.hris;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
@SpringBootApplication
@PWA(
    name = "HRIS Jasamedika",
    shortName = "HRIS",
    description = "Human Resource Information System - Jasamedika",
    backgroundColor = "#ffffff",
    themeColor = "#2196F3"
)
@Theme("lumo")
public class HrisApplication {
    public static void main(String[] args) {
        SpringApplication.run(HrisApplication.class, args);
    }
}
