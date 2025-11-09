package com.nanmax.hris.ui;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
@Route("checkin")
public class CheckinView extends VerticalLayout {
    private Button btnIn = new Button("Check-In");
    private WebClient client = WebClient.create("http://localhost:8080");
    public CheckinView() {
        setSizeFull(); add(new H2("Presensi Masuk"), btnIn);
        btnIn.addClickListener(e -> {
            client.get().uri("/presensi/in")
                    .retrieve().bodyToMono(Result.class)
                    .subscribe(
                            result -> Notification.show("Jam Masuk: " + result.jamMasuk),
                            err -> Notification.show("Check-in gagal: " + err.getMessage())
                    );
        });
    }
    static class Result { public String jamMasuk; }
}