package com.nanmax.hris.ui;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.Route;
import org.springframework.web.reactive.function.client.WebClient;
@Route("change-password")
public class ChangePasswordView extends VerticalLayout {
    private PasswordField passwordAsli = new PasswordField("Password Asli");
    private PasswordField passwordBaru1 = new PasswordField("Password Baru 1");
    private PasswordField passwordBaru2 = new PasswordField("Password Baru 2");
    private Button btnUbah = new Button("Ubah Password");
    private WebClient client = WebClient.create("http://localhost:8080");
    public ChangePasswordView() {
        setSizeFull();
        add(new H2("Ubah Password"));
        FormLayout form = new FormLayout();
        form.add(passwordAsli, passwordBaru1, passwordBaru2, btnUbah);
        add(form);
        btnUbah.addClickListener(e -> {
            if (passwordAsli.isEmpty() || passwordBaru1.isEmpty() || passwordBaru2.isEmpty()) {
                Notification.show("Semua password wajib diisi!");
                return;
            }
            var payload = new ChangePwdReq(passwordAsli.getValue(), passwordBaru1.getValue(), passwordBaru2.getValue());
            client.post().uri("/api/auth/ubah-password-sendiri")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(
                            result -> Notification.show("Berhasil ubah password."),
                            err -> Notification.show("Gagal ubah password: " + err.getMessage())
                    );
        });
    }
    static class ChangePwdReq {
        public String passwordAsli, passwordBaru1, passwordBaru2;
        public ChangePwdReq(String passwordAsli, String passwordBaru1, String passwordBaru2) {
            this.passwordAsli = passwordAsli; this.passwordBaru1 = passwordBaru1; this.passwordBaru2 = passwordBaru2;
        }
    }
}