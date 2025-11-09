package com.nanmax.hris.ui;

import com.nanmax.hris.config.ApiConfig;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
@PageTitle("Login - HRIS Jasamedika")
@Route("auth")
public class JasamedikaLoginView extends VerticalLayout {
    private static final Logger logger = LoggerFactory.getLogger(JasamedikaLoginView.class);
    private final WebClient client;
    private EmailField email;
    private PasswordField password;
    private TextField namaLengkap;
    private TextField nikUser;
    private TextField tempatLahir;
    private Button btnPrimary;
    private Button btnToggle;
    private H3 modeTitle;
    private Paragraph modeDescription;
    private boolean isLoginMode = true;
    public JasamedikaLoginView() {
        this.client = WebClient.builder()
                .baseUrl(ApiConfig.getBaseUrl())
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();
        setupLayout();
        createLoginRegisterForm();
    }
    private void setupLayout() {
        setSpacing(false);
        setPadding(false);
        setMargin(false);
        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background", "linear-gradient(135deg, #1e3a8a 0%, #3b82f6 50%, #60a5fa 100%)");
        getStyle().set("min-height", "100vh");
        getStyle().set("position", "relative");
        getStyle().set("background-image", 
            "radial-gradient(circle at 25% 25%, rgba(255,255,255,0.1) 0%, transparent 50%), " +
            "radial-gradient(circle at 75% 75%, rgba(255,255,255,0.05) 0%, transparent 50%)");
    }
    private void createLoginRegisterForm() {
        Div container = new Div();
        container.getStyle()
                .set("background", "rgba(255, 255, 255, 0.98)")
                .set("border-radius", "24px")
                .set("padding", "50px 45px")
                .set("box-shadow", "0 25px 80px rgba(0, 0, 0, 0.15)")
                .set("max-width", "500px")
                .set("width", "100%")
                .set("backdrop-filter", "blur(20px)")
                .set("border", "1px solid rgba(255, 255, 255, 0.2)");
        Div headerSection = createHeader();
        Div formSection = createForm();
        Div footerSection = createFooter();
        container.add(headerSection, formSection, footerSection);
        add(container);
    }
    private Div createHeader() {
        Div header = new Div();
        header.getStyle()
                .set("text-align", "center")
                .set("margin-bottom", "35px");
        H1 brand = new H1("JASAMEDIKA");
        brand.getStyle()
                .set("color", "#1e3a8a")
                .set("font-size", "2.2rem")
                .set("font-weight", "800")
                .set("letter-spacing", "1px")
                .set("margin", "0 0 5px 0");
        H2 subtitle = new H2("SARANATAMA");
        subtitle.getStyle()
                .set("color", "#3b82f6")
                .set("font-size", "1rem")
                .set("font-weight", "400")
                .set("letter-spacing", "2px")
                .set("margin", "0 0 25px 0");
        modeTitle = new H3();
        updateModeTitle();
        modeDescription = new Paragraph();
        updateModeDescription();
        header.add(brand, subtitle, modeTitle, modeDescription);
        return header;
    }
    private void updateModeTitle() {
        if (isLoginMode) {
            modeTitle.setText("Masuk ke Sistem HRIS");
        } else {
            modeTitle.setText("Daftar Sistem HRIS");
        }
        modeTitle.getStyle()
                .set("color", "#1f2937")
                .set("font-size", "1.4rem")
                .set("font-weight", "600")
                .set("margin", "0 0 8px 0");
    }
    private void updateModeDescription() {
        if (isLoginMode) {
            modeDescription.setText("Silakan masukkan kredensial Anda");
        } else {
            modeDescription.setText("Buat akun baru untuk mengakses sistem");
        }
        modeDescription.getStyle()
                .set("color", "#6b7280")
                .set("font-size", "1rem")
                .set("margin", "0");
    }
    private Div createForm() {
        Div formDiv = new Div();
        formDiv.getStyle().set("margin-bottom", "25px");
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        email = new EmailField("Email");
        email.setPlaceholder("nama@perusahaan.com");
        email.setValue("admin@jasamedika.com"); // Default untuk testing
        email.getStyle()
                .set("width", "100%")
                .set("margin-bottom", "15px");
        password = new PasswordField("Password");
        password.setPlaceholder("Masukkan password");
        password.setValue("admin123"); // Default untuk testing
        password.getStyle()
                .set("width", "100%")
                .set("margin-bottom", "15px");
        namaLengkap = new TextField("Nama Lengkap");
        namaLengkap.setPlaceholder("Masukkan nama lengkap");
        namaLengkap.getStyle()
                .set("width", "100%")
                .set("margin-bottom", "15px");
        namaLengkap.setVisible(false);
        nikUser = new TextField("NIK");
        nikUser.setPlaceholder("Nomor Induk Kependudukan");
        nikUser.getStyle()
                .set("width", "100%")
                .set("margin-bottom", "15px");
        nikUser.setVisible(false);
        tempatLahir = new TextField("Tempat Lahir");
        tempatLahir.setPlaceholder("Kota tempat lahir");
        tempatLahir.getStyle()
                .set("width", "100%")
                .set("margin-bottom", "20px");
        tempatLahir.setVisible(false);
        btnPrimary = new Button();
        updatePrimaryButton();
        btnPrimary.addClickListener(e -> {
            if (isLoginMode) {
                performLogin();
            } else {
                performRegister();
            }
        });
        btnToggle = new Button();
        updateToggleButton();
        btnToggle.addClickListener(e -> toggleMode());
        formLayout.add(email, password, namaLengkap, nikUser, tempatLahir, btnPrimary, btnToggle);
        formDiv.add(formLayout);
        return formDiv;
    }
    private void updatePrimaryButton() {
        if (isLoginMode) {
            btnPrimary.setText("Masuk ke Sistem");
        } else {
            btnPrimary.setText("Daftar Sekarang");
        }
        btnPrimary.removeThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        btnPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        btnPrimary.getStyle()
                .set("width", "100%")
                .set("background", "linear-gradient(135deg, #1e3a8a 0%, #3b82f6 100%)")
                .set("border", "none")
                .set("color", "white")
                .set("padding", "16px")
                .set("border-radius", "12px")
                .set("font-weight", "600")
                .set("font-size", "1.1rem")
                .set("cursor", "pointer")
                .set("transition", "all 0.3s ease")
                .set("box-shadow", "0 4px 20px rgba(30, 58, 138, 0.3)")
                .set("margin-bottom", "15px");
    }
    private void updateToggleButton() {
        if (isLoginMode) {
            btnToggle.setText("Belum punya akun? Daftar disini");
        } else {
            btnToggle.setText("Sudah punya akun? Masuk disini");
        }
        btnToggle.removeThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnToggle.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnToggle.getStyle()
                .set("width", "100%")
                .set("background", "transparent")
                .set("border", "2px solid #e5e7eb")
                .set("color", "#6b7280")
                .set("padding", "12px")
                .set("border-radius", "12px")
                .set("font-weight", "500")
                .set("font-size", "0.95rem")
                .set("cursor", "pointer")
                .set("transition", "all 0.3s ease");
    }
    private void toggleMode() {
        isLoginMode = !isLoginMode;
        namaLengkap.setVisible(!isLoginMode);
        nikUser.setVisible(!isLoginMode);
        tempatLahir.setVisible(!isLoginMode);
        updateModeTitle();
        updateModeDescription();
        updatePrimaryButton();
        updateToggleButton();
        if (isLoginMode) {
            email.setValue("admin@jasamedika.com");
            password.setValue("admin123");
        } else {
            email.setValue("");
            password.setValue("");
            namaLengkap.setValue("");
            nikUser.setValue("");
            tempatLahir.setValue("");
        }
    }
    private Div createFooter() {
        Div footer = new Div();
        footer.getStyle()
                .set("text-align", "center")
                .set("padding-top", "25px")
                .set("border-top", "1px solid #e5e7eb");
        Div devInfo = new Div();
        devInfo.getStyle()
                .set("background", "linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%)")
                .set("padding", "18px")
                .set("border-radius", "12px")
                .set("border", "1px solid #bfdbfe")
                .set("margin-bottom", "20px");
        Paragraph devTitle = new Paragraph("🏥 Sistem Development");
        devTitle.getStyle()
                .set("color", "#1e40af")
                .set("font-weight", "600")
                .set("margin", "0 0 8px 0");
        Paragraph devDesc = new Paragraph("Default login: admin@jasamedika.com / admin123");
        devDesc.getStyle()
                .set("color", "#3b82f6")
                .set("font-size", "0.9rem")
                .set("margin", "0");
        devInfo.add(devTitle, devDesc);
        Div copyrightSection = new Div();
        copyrightSection.getStyle()
                .set("padding", "15px 0")
                .set("border-top", "1px solid #f1f5f9")
                .set("margin-top", "15px");
        Paragraph jasamedikaCopyright = new Paragraph("© 2025 PT. Jasamedika Saranatama");
        jasamedikaCopyright.getStyle()
                .set("color", "#9ca3af")
                .set("font-size", "0.85rem")
                .set("margin", "0 0 5px 0");
        Paragraph nanmaxCredit = new Paragraph("💻 Developed by nanmax");
        nanmaxCredit.getStyle()
                .set("color", "#6b7280")
                .set("font-size", "0.8rem")
                .set("font-weight", "500")
                .set("margin", "0");
        copyrightSection.add(jasamedikaCopyright, nanmaxCredit);
        footer.add(devInfo, copyrightSection);
        return footer;
    }
    private void performLogin() {
        if (email.getValue() == null || email.getValue().trim().isEmpty()) {
            showNotification("Email harus diisi", NotificationVariant.LUMO_ERROR);
            email.focus();
            return;
        }
        if (password.getValue() == null || password.getValue().trim().isEmpty()) {
            showNotification("Password harus diisi", NotificationVariant.LUMO_ERROR);
            password.focus();
            return;
        }
        if (!email.getValue().trim().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showNotification("Format email tidak valid", NotificationVariant.LUMO_ERROR);
            email.focus();
            return;
        }
        showNotification("Memproses login...", NotificationVariant.LUMO_PRIMARY);
        btnPrimary.setEnabled(false);
        btnPrimary.setText("Memproses...");
        try {
            Map<String, String> loginRequest = new HashMap<>();
            loginRequest.put("email", email.getValue().trim());
            loginRequest.put("password", password.getValue().trim());
            loginRequest.put("profile", "ADMIN"); // Default profile
            UI currentUI = UI.getCurrent();
            client.post()
                    .uri("/api/auth/login-json")
                    .bodyValue(loginRequest)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(10))
                    .subscribe(
                            response -> {
                                logger.info("Login response received: {}", response);
                                currentUI.access(() -> {
                                    try {
                                        btnPrimary.setEnabled(true);
                                        updatePrimaryButton();
                                        VaadinSession.getCurrent().setAttribute("token", response.get("token"));
                                        VaadinSession.getCurrent().setAttribute("user", response.get("user"));
                                        logger.info("Showing success notification");
                                        showNotification("Login berhasil! Selamat datang di HRIS Jasamedika", NotificationVariant.LUMO_SUCCESS);
                                        currentUI.navigate("dashboard");
                                    } catch (Exception e) {
                                        logger.error("Error processing login response", e);
                                        showNotification("Terjadi kesalahan sesi: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
                                        btnPrimary.setEnabled(true);
                                        updatePrimaryButton();
                                    }
                                });
                            },
                            error -> {
                                logger.error("Login error occurred", error);
                                currentUI.access(() -> {
                                    btnPrimary.setEnabled(true);
                                    updatePrimaryButton();
                                    String errorMessage = "Login gagal: ";
                                    if (error instanceof org.springframework.web.reactive.function.client.WebClientResponseException) {
                                        org.springframework.web.reactive.function.client.WebClientResponseException webError = 
                                            (org.springframework.web.reactive.function.client.WebClientResponseException) error;
                                        if (webError.getStatusCode().value() == 401) {
                                            try {
                                                String responseBody = webError.getResponseBodyAsString();
                                                if (responseBody.contains("Email tidak ditemukan")) {
                                                    errorMessage += "Email tidak terdaftar";
                                                } else if (responseBody.contains("Password salah")) {
                                                    errorMessage += "Password salah";
                                                } else if (responseBody.contains("Profile tidak sesuai")) {
                                                    errorMessage += "Profile tidak sesuai";
                                                } else {
                                                    errorMessage += "Email atau password salah";
                                                }
                                            } catch (Exception e) {
                                                errorMessage += "Email atau password salah";
                                            }
                                        } else if (webError.getStatusCode().value() == 404) {
                                            errorMessage += "Server tidak dapat dihubungi";
                                        } else if (webError.getStatusCode().value() == 500) {
                                            errorMessage += "Terjadi kesalahan server, coba lagi nanti";
                                        } else {
                                            errorMessage += "Server error (" + webError.getStatusCode().value() + ")";
                                        }
                                    } else if (error instanceof java.util.concurrent.TimeoutException) {
                                        errorMessage += "Koneksi timeout, coba lagi";
                                    } else {
                                        errorMessage += "Koneksi bermasalah: " + error.getMessage();
                                    }
                                    logger.info("Showing login error notification: {}", errorMessage);
                                    showNotification(errorMessage, NotificationVariant.LUMO_ERROR);
                                });
                            }
                    );
        } catch (Exception e) {
            showNotification("Terjadi kesalahan: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
            btnPrimary.setEnabled(true);
            updatePrimaryButton();
        }
    }
    private void performRegister() {
        if (namaLengkap.getValue() == null || namaLengkap.getValue().trim().isEmpty()) {
            showNotification("Nama lengkap harus diisi", NotificationVariant.LUMO_ERROR);
            namaLengkap.focus();
            return;
        }
        if (email.getValue() == null || email.getValue().trim().isEmpty()) {
            showNotification("Email harus diisi", NotificationVariant.LUMO_ERROR);
            email.focus();
            return;
        }
        if (password.getValue() == null || password.getValue().trim().isEmpty()) {
            showNotification("Password harus diisi", NotificationVariant.LUMO_ERROR);
            password.focus();
            return;
        }
        if (!email.getValue().trim().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showNotification("Format email tidak valid", NotificationVariant.LUMO_ERROR);
            email.focus();
            return;
        }
        if (password.getValue().trim().length() < 6) {
            showNotification("Password minimal 6 karakter", NotificationVariant.LUMO_ERROR);
            password.focus();
            return;
        }
        if (namaLengkap.getValue().trim().length() < 3) {
            showNotification("Nama lengkap minimal 3 karakter", NotificationVariant.LUMO_ERROR);
            namaLengkap.focus();
            return;
        }
        showNotification("Memproses registrasi...", NotificationVariant.LUMO_PRIMARY);
        btnPrimary.setEnabled(false);
        btnPrimary.setText("Mendaftar...");
        try {
            Map<String, Object> registerRequest = new HashMap<>();
            registerRequest.put("namaLengkap", namaLengkap.getValue().trim());
            registerRequest.put("email", email.getValue().trim());
            registerRequest.put("password", password.getValue().trim());
            registerRequest.put("profile", "KARYAWAN");
            registerRequest.put("nikUser", nikUser.getValue().trim());
            registerRequest.put("tempatLahir", tempatLahir.getValue().trim());
            UI currentUI = UI.getCurrent();
            client.post()
                    .uri("/api/auth/register")
                    .bodyValue(registerRequest)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(10))
                    .subscribe(
                            response -> {
                                logger.info("Register response received: {}", response);
                                currentUI.access(() -> {
                                    try {
                                        btnPrimary.setEnabled(true);
                                        updatePrimaryButton();
                                        VaadinSession.getCurrent().setAttribute("token", response.get("token"));
                                        VaadinSession.getCurrent().setAttribute("user", response.get("user"));
                                        showNotification("Registrasi berhasil! Selamat datang di HRIS Jasamedika", NotificationVariant.LUMO_SUCCESS);
                                        currentUI.navigate("dashboard");
                                    } catch (Exception e) {
                                        logger.error("Error processing register response", e);
                                        showNotification("Registrasi berhasil! Silakan login", NotificationVariant.LUMO_SUCCESS);
                                        toggleMode(); // Switch back to login mode
                                        btnPrimary.setEnabled(true);
                                        updatePrimaryButton();
                                    }
                                });
                            },
                            error -> {
                                logger.error("Register error occurred", error);
                                currentUI.access(() -> {
                                    btnPrimary.setEnabled(true);
                                    updatePrimaryButton();
                                    String errorMessage = "Registrasi gagal: ";
                                    if (error instanceof org.springframework.web.reactive.function.client.WebClientResponseException) {
                                        org.springframework.web.reactive.function.client.WebClientResponseException webError = 
                                            (org.springframework.web.reactive.function.client.WebClientResponseException) error;
                                        if (webError.getStatusCode().value() == 400) {
                                            try {
                                                String responseBody = webError.getResponseBodyAsString();
                                                if (responseBody.contains("Email sudah terdaftar")) {
                                                    errorMessage += "Email sudah terdaftar! Gunakan email lain";
                                                } else if (responseBody.contains("harus diisi")) {
                                                    errorMessage += "Data tidak lengkap, pastikan semua field diisi";
                                                } else {
                                                    errorMessage += "Data tidak valid";
                                                }
                                            } catch (Exception e) {
                                                errorMessage += "Email sudah terdaftar atau data tidak valid";
                                            }
                                        } else if (webError.getStatusCode().value() == 500) {
                                            errorMessage += "Terjadi kesalahan server, coba lagi nanti";
                                        } else {
                                            errorMessage += "Server error (" + webError.getStatusCode().value() + ")";
                                        }
                                    } else if (error instanceof java.util.concurrent.TimeoutException) {
                                        errorMessage += "Koneksi timeout, coba lagi";
                                    } else {
                                        errorMessage += "Koneksi bermasalah: " + error.getMessage();
                                    }
                                    logger.info("Showing register error notification: {}", errorMessage);
                                    showNotification(errorMessage, NotificationVariant.LUMO_ERROR);
                                });
                            }
                    );
        } catch (Exception e) {
            logger.error("Register exception occurred", e);
            showNotification("Terjadi kesalahan sistem: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
            btnPrimary.setEnabled(true);
            updatePrimaryButton();
        }
    }
    private void showNotification(String message, NotificationVariant variant) {
        logger.info("Creating notification: {} with variant: {}", message, variant);
        try {
            Notification notification = Notification.show(message, 5000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(variant);
            logger.info("Notification shown successfully");
        } catch (Exception e) {
            logger.error("Error showing notification", e);
        }
    }
}