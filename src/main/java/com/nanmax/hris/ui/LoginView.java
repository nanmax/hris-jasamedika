package com.nanmax.hris.ui;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
@PageTitle("Login | HRIS PT Jasamedika")
@Route("old-complex-login")
@CssImport("./styles/login.css")
public class LoginView extends VerticalLayout {
    private EmailField email = new EmailField("Email");
    private PasswordField password = new PasswordField("Password");
    private Select<String> profile = new Select<>();
    private Button btnLogin = new Button("Login", VaadinIcon.SIGN_IN.create());
    private Button btnRegister = new Button("Register", VaadinIcon.USER_CHECK.create());
    private WebClient client = WebClient.create("http://localhost:8080");
    public LoginView() {
        setSizeFull();
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        addClassName("login-view");
        add(createLoginForm());
    }
    private VerticalLayout createLoginForm() {
        VerticalLayout headerLayout = createHeader();
        VerticalLayout formLayout = createForm();
        VerticalLayout footerLayout = createFooter();
        VerticalLayout loginContainer = new VerticalLayout(headerLayout, formLayout, footerLayout);
        loginContainer.setSpacing(true);
        loginContainer.setPadding(true);
        loginContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        loginContainer.addClassNames(
            LumoUtility.Background.BASE,
            LumoUtility.BorderRadius.LARGE,
            LumoUtility.BoxShadow.MEDIUM,
            LumoUtility.Padding.XLARGE
        );
        loginContainer.setWidth("400px");
        loginContainer.getStyle().set("box-shadow", "0 4px 20px rgba(0,0,0,0.1)");
        return loginContainer;
    }
    private VerticalLayout createHeader() {
        Icon logoIcon = VaadinIcon.BUILDING.create();
        logoIcon.setSize("64px");
        logoIcon.setColor("var(--lumo-primary-color)");
        H1 title = new H1("HRIS");
        title.addClassNames(LumoUtility.Margin.NONE, LumoUtility.TextColor.PRIMARY);
        H2 subtitle = new H2("PT Jasamedika");
        subtitle.addClassNames(LumoUtility.Margin.NONE, LumoUtility.FontSize.MEDIUM, LumoUtility.TextColor.SECONDARY);
        Paragraph description = new Paragraph("Silakan login untuk mengakses sistem");
        description.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Bottom.LARGE);
        VerticalLayout header = new VerticalLayout(logoIcon, title, subtitle, description);
        header.setSpacing(false);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        return header;
    }
    private VerticalLayout createForm() {
        email.setWidthFull();
        email.setPlaceholder("nama@ptjasamedika.local");
        email.setPrefixComponent(VaadinIcon.ENVELOPE.create());
        email.setRequiredIndicatorVisible(true);
        email.setErrorMessage("Email harus diisi dengan format yang benar");
        password.setWidthFull();
        password.setPlaceholder("Masukkan password");
        password.setPrefixComponent(VaadinIcon.LOCK.create());
        password.setRequiredIndicatorVisible(true);
        password.setRevealButtonVisible(true);
        profile.setLabel("Role");
        profile.setWidthFull();
        profile.setItems("ADMIN", "STAFF", "HRD");
        profile.setValue("STAFF");
        profile.setPrefixComponent(VaadinIcon.USER.create());
        profile.setPlaceholder("Pilih role");
        btnLogin.setWidthFull();
        btnLogin.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        btnLogin.addClickListener(e -> handleLogin());
        FormLayout formLayout = new FormLayout();
        formLayout.setWidthFull();
        formLayout.add(email, password, profile);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        VerticalLayout form = new VerticalLayout(formLayout, btnLogin);
        form.setSpacing(true);
        form.setWidthFull();
        return form;
    }
    private VerticalLayout createFooter() {
        btnRegister.setWidthFull();
        btnRegister.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnRegister.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("register")));
        Div divider = new Div();
        divider.addClassNames(LumoUtility.Height.XSMALL, LumoUtility.Background.CONTRAST_10, LumoUtility.Margin.Vertical.MEDIUM);
        VerticalLayout footer = new VerticalLayout(divider, btnRegister);
        footer.setAlignItems(FlexComponent.Alignment.CENTER);
        footer.setWidthFull();
        return footer;
    }
    private void handleLogin() {
        if (email.isEmpty() || password.isEmpty() || profile.isEmpty()) {
            showErrorNotification("Semua field harus diisi!");
            return;
        }
        if (email.isInvalid()) {
            showErrorNotification("Format email tidak valid!");
            return;
        }
        btnLogin.setEnabled(false);
        btnLogin.setText("Memproses...");
        var payload = new LoginRequest(email.getValue(), password.getValue(), profile.getValue());
        client.post()
            .uri("/api/auth/login")
            .body(Mono.just(payload), LoginRequest.class)
            .retrieve()
            .bodyToMono(LoginResult.class)
            .subscribe(
                result -> {
                    getUI().ifPresent(ui -> {
                        ui.getSession().setAttribute("authToken", result.hasil.token);
                        ui.getSession().setAttribute("userInfo", result.hasil.info);
                        ui.navigate("");
                    });
                    showSuccessNotification("Login berhasil! Selamat datang.");
                },
                error -> {
                    showErrorNotification("Login gagal: " + error.getMessage());
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Login");
                }
            );
    }
    private void showSuccessNotification(String message) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.TOP_CENTER);
    }
    private void showErrorNotification(String message) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.TOP_CENTER);
    }
    public static class LoginRequest {
        private String email;
        private String password;
        private String profile;
        public LoginRequest(String email, String password, String profile) {
            this.email = email;
            this.password = password;
            this.profile = profile;
        }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getProfile() { return profile; }
        public void setProfile(String profile) { this.profile = profile; }
    }
    public static class LoginResult {
        private LoginHasil hasil;
        public LoginHasil getHasil() { return hasil; }
        public void setHasil(LoginHasil hasil) { this.hasil = hasil; }
        public static class LoginHasil {
            private String token;
            private UserInfo info;
            public String getToken() { return token; }
            public void setToken(String token) { this.token = token; }
            public UserInfo getInfo() { return info; }
            public void setInfo(UserInfo info) { this.info = info; }
        }
        public static class UserInfo {
            private String profile, idUser, namaLengkap, tempatLahir, email, password, nikUser,
                    namaJabatan, namaDepartemen, namaUnitKerja, namaJenisKelamin, namaPendidikan, photo;
            private Long tanggalLahir;
            private Integer kdJabatan, kdDepartemen, kdUnitKerja, kdJenisKelamin, kdPendidikan;
            public String getProfile() { return profile; }
            public void setProfile(String profile) { this.profile = profile; }
            public String getIdUser() { return idUser; }
            public void setIdUser(String idUser) { this.idUser = idUser; }
            public String getNamaLengkap() { return namaLengkap; }
            public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }
            public String getEmail() { return email; }
            public void setEmail(String email) { this.email = email; }
        }
    }
}