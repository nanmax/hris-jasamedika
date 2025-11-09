package com.nanmax.hris.ui;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.HashMap;
import java.util.Map;
@PageTitle("Login | HRIS PT Jasamedika")
@Route("complex-login")
public class SimpleLoginView extends VerticalLayout {
    private EmailField email = new EmailField("Email");
    private PasswordField password = new PasswordField("Password");
    private Button btnLogin = new Button("Login");
    private Button btnBack = new Button("← Back to Home");
    private WebClient client = WebClient.create("http://localhost:8080");
    public SimpleLoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        add(createLoginForm());
        getStyle().set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)");
    }
    private VerticalLayout createLoginForm() {
        VerticalLayout form = new VerticalLayout();
        form.setWidth("400px");
        form.getStyle()
            .set("background", "white")
            .set("border-radius", "12px")
            .set("padding", "32px")
            .set("box-shadow", "0 8px 32px rgba(0, 0, 0, 0.1)");
        H1 title = new H1("🔐 Login HRIS");
        title.getStyle()
            .set("text-align", "center")
            .set("color", "#1e293b")
            .set("margin", "0 0 24px 0");
        email.setWidthFull();
        email.setValue("admin@jasamedika.com");
        email.getStyle().set("margin-bottom", "16px");
        password.setWidthFull();
        password.setValue("admin123");
        password.getStyle().set("margin-bottom", "24px");
        btnLogin.setWidthFull();
        btnLogin.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnLogin.getStyle()
            .set("background", "#2563eb")
            .set("height", "48px")
            .set("font-weight", "600")
            .set("margin-bottom", "16px");
        btnBack.setWidthFull();
        btnBack.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnBack.getStyle().set("height", "40px");
        Paragraph info = new Paragraph("Demo Credentials:\nEmail: admin@jasamedika.com\nPassword: admin123");
        info.getStyle()
            .set("background", "#f1f5f9")
            .set("padding", "12px")
            .set("border-radius", "8px")
            .set("font-size", "14px")
            .set("color", "#64748b")
            .set("white-space", "pre-line")
            .set("text-align", "center")
            .set("margin", "16px 0 0 0");
        btnLogin.addClickListener(e -> performLogin());
        btnBack.addClickListener(e -> UI.getCurrent().navigate(""));
        password.addKeyPressListener(e -> {
            if (e.getKey().getKeys().contains("Enter")) {
                performLogin();
            }
        });
        form.add(title, email, password, btnLogin, btnBack, info);
        return form;
    }
    private void performLogin() {
        if (email.getValue().trim().isEmpty() || password.getValue().trim().isEmpty()) {
            showNotification("Please fill in all fields", NotificationVariant.LUMO_ERROR);
            return;
        }
        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");
        try {
            Map<String, String> loginRequest = new HashMap<>();
            loginRequest.put("email", email.getValue().trim());
            loginRequest.put("password", password.getValue().trim());
            UI currentUI = UI.getCurrent();
            client.post()
                    .uri("/api/auth/login")
                    .bodyValue(loginRequest)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .subscribe(
                            response -> {
                                currentUI.access(() -> {
                                    try {
                                        VaadinSession.getCurrent().setAttribute("token", response.get("token"));
                                        VaadinSession.getCurrent().setAttribute("user", response.get("user"));
                                        showNotification("Login successful! Welcome to HRIS", NotificationVariant.LUMO_SUCCESS);
                                        currentUI.navigate("dashboard");
                                        btnLogin.setEnabled(true);
                                        btnLogin.setText("Login");
                                    } catch (Exception e) {
                                        showNotification("Session error: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
                                    }
                                });
                            },
                            error -> {
                                currentUI.access(() -> {
                                    showNotification("Login failed: Invalid credentials", NotificationVariant.LUMO_ERROR);
                                    btnLogin.setEnabled(true);
                                    btnLogin.setText("Login");
                                });
                            }
                    );
        } catch (Exception e) {
            showNotification("Login error: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
            btnLogin.setEnabled(true);
            btnLogin.setText("Login");
        }
    }
    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = new Notification(message, 3000);
        notification.addThemeVariants(variant);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.open();
    }
}