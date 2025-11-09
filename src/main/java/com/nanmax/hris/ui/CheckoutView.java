package com.nanmax.hris.ui;

import com.nanmax.hris.config.ApiConfig;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.web.reactive.function.client.WebClient;
@Route("checkout")
public class CheckoutView extends VerticalLayout {
    private Button btnOut = new Button("Check-Out");
    private WebClient client = WebClient.create(ApiConfig.getBaseUrl());
    public CheckoutView() {
        setSizeFull(); add(new H2("Presensi Keluar"), btnOut);
        btnOut.addClickListener(e -> {
            client.get().uri("/presensi/out")
                    .retrieve().bodyToMono(Result.class)
                    .subscribe(
                            result -> Notification.show("Jam Keluar: " + result.jamKeluar),
                            err -> Notification.show("Check-out gagal: " + err.getMessage())
                    );
        });
    }
    static class Result { public String jamKeluar; }
}