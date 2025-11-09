package com.nanmax.hris.ui;
import com.nanmax.hris.ui.components.ResponsiveHeader;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
@PageTitle("Registrasi | HRIS PT Jasamedika")
@Route("register")
public class RegisterView extends VerticalLayout {
    private final ResponsiveHeader responsiveHeader;
    private WebClient client = WebClient.create("http://localhost:8080");
    private TextField namaAdmin = new TextField("Nama Administrator");
    private TextField perusahaan = new TextField("Nama Perusahaan");
    private Button registerButton = new Button("Daftar Sekarang", VaadinIcon.USER_CHECK.create());
    public RegisterView() {
        this.responsiveHeader = new ResponsiveHeader(UI.getCurrent())
                .withPageTitle("Registrasi - HRIS")
                .withBackButton(true, "auth");
        setupLayout();
        createHeaders();
        add(createRegistrationCard());
    }
    private void setupLayout() {
        ResponsiveHeader.applyMobileLayout(this);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        addClassName("register-view");
        getUI().ifPresent(ui -> {
            ui.getPage().addStyleSheet("styles/login.css");
        });
    }
    private void createHeaders() {
        add(responsiveHeader.createMobileHeader());
        add(responsiveHeader.createDesktopHeader());
    }
    private Div createRegistrationCard() {
        Div card = new Div();
        card.addClassName("register-card");
        Icon icon = VaadinIcon.BUILDING.create();
        icon.addClassName("register-icon");
        H1 title = new H1("Registrasi Perusahaan");
        title.addClassName("register-title");
        H2 subtitle = new H2("HRIS PT Jasamedika");
        subtitle.addClassName("register-subtitle");
        Div header = new Div(icon, title, subtitle);
        header.addClassName("register-header");
        Paragraph info = new Paragraph("Daftarkan perusahaan Anda untuk memulai menggunakan sistem HRIS");
        info.addClassName("register-info");
        FormLayout formLayout = createForm();
        HorizontalLayout buttonLayout = createButtonLayout();
        RouterLink loginLink = new RouterLink("Sudah punya akun? Login di sini", LoginView.class);
        loginLink.addClassName("login-link");
        card.add(header, info, formLayout, buttonLayout, loginLink);
        return card;
    }
    private FormLayout createForm() {
        FormLayout formLayout = new FormLayout();
        formLayout.addClassName("register-form");
        namaAdmin.setRequired(true);
        namaAdmin.setPlaceholder("Nama lengkap administrator");
        namaAdmin.setPrefixComponent(VaadinIcon.USER.create());
        perusahaan.setRequired(true);
        perusahaan.setPlaceholder("Nama perusahaan/organisasi");
        perusahaan.setPrefixComponent(VaadinIcon.BUILDING.create());
        formLayout.add(namaAdmin, perusahaan);
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1)
        );
        return formLayout;
    }
    private HorizontalLayout createButtonLayout() {
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        registerButton.addClassName("register-button");
        registerButton.addClickListener(e -> performRegistration());
        HorizontalLayout buttonLayout = new HorizontalLayout(registerButton);
        buttonLayout.addClassName("button-layout");
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        return buttonLayout;
    }
    private void performRegistration() {
        if (!validateFields()) {
            return;
        }
        registerButton.setEnabled(false);
        registerButton.setText("Memproses...");
        InitRequest payload = new InitRequest(namaAdmin.getValue().trim(), perusahaan.getValue().trim());
        client.post()
            .uri("/api/auth/init-data")
            .body(Mono.just(payload), InitRequest.class)
            .retrieve()
            .bodyToMono(RegisterResult.class)
            .subscribe(
                result -> {
                    showSuccess("Registrasi berhasil!\n" +
                              "Email: " + result.getEmail() + "\n" +
                              "Password: " + result.getPassword() + "\n" +
                              "Silakan login menggunakan kredensial tersebut.");
                    clearForm();
                    resetButton();
                },
                error -> {
                    showError("Registrasi gagal: " + error.getMessage());
                    resetButton();
                }
            );
    }
    private boolean validateFields() {
        boolean valid = true;
        if (namaAdmin.getValue() == null || namaAdmin.getValue().trim().isEmpty()) {
            namaAdmin.setErrorMessage("Nama administrator wajib diisi");
            namaAdmin.setInvalid(true);
            valid = false;
        } else {
            namaAdmin.setInvalid(false);
        }
        if (perusahaan.getValue() == null || perusahaan.getValue().trim().isEmpty()) {
            perusahaan.setErrorMessage("Nama perusahaan wajib diisi");
            perusahaan.setInvalid(true);
            valid = false;
        } else {
            perusahaan.setInvalid(false);
        }
        return valid;
    }
    private void clearForm() {
        namaAdmin.setValue("");
        perusahaan.setValue("");
        namaAdmin.setInvalid(false);
        perusahaan.setInvalid(false);
    }
    private void resetButton() {
        registerButton.setEnabled(true);
        registerButton.setText("Daftar Sekarang");
    }
    private void showSuccess(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }
    private void showError(String message) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
    public static class InitRequest {
        private String namaAdmin;
        private String perusahaan;
        public InitRequest(String namaAdmin, String perusahaan) {
            this.namaAdmin = namaAdmin;
            this.perusahaan = perusahaan;
        }
        public String getNamaAdmin() { return namaAdmin; }
        public void setNamaAdmin(String namaAdmin) { this.namaAdmin = namaAdmin; }
        public String getPerusahaan() { return perusahaan; }
        public void setPerusahaan(String perusahaan) { this.perusahaan = perusahaan; }
    }
    public static class RegisterResult {
        private String email;
        private String password;
        private String profile;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getProfile() { return profile; }
        public void setProfile(String profile) { this.profile = profile; }
    }
}