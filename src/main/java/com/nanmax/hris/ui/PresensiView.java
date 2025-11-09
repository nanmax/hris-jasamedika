package com.nanmax.hris.ui;

import com.nanmax.hris.config.ApiConfig;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@PageTitle("Presensi & Absensi - HRIS Jasamedika")
@Route("presensi")
public class PresensiView extends VerticalLayout {
    private static final Logger logger = LoggerFactory.getLogger(PresensiView.class);
    private final WebClient client;
    private final UI currentUI;  // Store UI reference to avoid null pointer
    private Grid<Map<String, Object>> presensiGrid;
    private Button checkInButton, checkOutButton, absensiButton, refreshButton, backButton;
    private DatePicker tglAwalFilter, tglAkhirFilter;
    private Button filterButton;
    private Map<String, Object> currentUser;
    private boolean isAdmin = false;
    public PresensiView() {
        this.currentUI = UI.getCurrent(); // Store UI reference at construction time
        this.client = WebClient.builder()
                .baseUrl(ApiConfig.getBaseUrl())
                .defaultHeader("Authorization", "Bearer " + getToken())
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
        checkAuthentication();
        getCurrentUserInfo();
        setupMobileLayout(); // Setup mobile-friendly layout
        createHeader();
        createActionSection();
        createFilterSection();
        createContent();
        loadPresensiData();
    }
    private void checkAuthentication() {
        Object token = VaadinSession.getCurrent().getAttribute("token");
        if (token == null) {
            Notification.show("Sesi telah berakhir, silakan login kembali", 
                            3000, Notification.Position.TOP_CENTER)
                       .addThemeVariants(NotificationVariant.LUMO_ERROR);
            UI.getCurrent().navigate("auth");
        }
    }
    private String getToken() {
        Object token = VaadinSession.getCurrent().getAttribute("token");
        return token != null ? token.toString() : "";
    }
    private void getCurrentUserInfo() {
        Object user = VaadinSession.getCurrent().getAttribute("user");
        if (user instanceof Map) {
            currentUser = (Map<String, Object>) user;
            Object profile = currentUser.get("profile");
            isAdmin = "ADMIN".equals(profile) || "HRD".equals(currentUser.get("namaDepartemen"));
        }
    }
    private void setupMobileLayout() {
        setSpacing(false);
        setPadding(false);
        setMargin(false);
        setSizeFull();
        getStyle()
            .set("background", "linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%)")
            .set("min-height", "100vh")
            .set("font-size", "14px")
            .set("overflow-x", "hidden"); // Prevent horizontal scroll
        if (currentUI != null) {
            currentUI.getPage().executeJs("document.querySelector('meta[name=viewport]') || " +
                "document.head.appendChild(Object.assign(document.createElement('meta'), " +
                "{name: 'viewport', content: 'width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no'}))");
            currentUI.getPage().addStyleSheet("https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap");
            currentUI.getPage().addStyleSheet("./styles/mobile.css");
        }
    }
    private void createHeader() {
        createMobileHeader();
        createDesktopHeader();
    }
    private void createMobileHeader() {
        Div mobileHeader = new Div();
        mobileHeader.addClassName("mobile-header");
        H3 title = new H3("HRIS Jasamedika");
        title.getStyle()
                .set("margin", "0")
                .set("color", "#1f2937")
                .set("font-size", "18px")
                .set("font-weight", "600");
        Div hamburgerMenu = new Div();
        hamburgerMenu.addClassName("hamburger-menu");
        hamburgerMenu.getElement().setAttribute("onclick", "toggleMobileMenu()");
        Span line1 = new Span();
        Span line2 = new Span();  
        Span line3 = new Span();
        hamburgerMenu.add(line1, line2, line3);
        mobileHeader.add(title, hamburgerMenu);
        Div menuOverlay = new Div();
        menuOverlay.addClassName("mobile-menu-overlay");
        menuOverlay.getElement().setAttribute("onclick", "closeMobileMenu()");
        Div mobileMenu = new Div();
        mobileMenu.addClassName("mobile-menu");
        Div menuContent = new Div();
        menuContent.addClassName("mobile-menu-content");
        Div userInfo = new Div();
        userInfo.addClassName("mobile-user-info");
        String userName = currentUser != null ? currentUser.get("namaLengkap").toString() : "User";
        String userRole = isAdmin ? "Admin/HRD" : "Karyawan";
        Div userNameDiv = new Div();
        userNameDiv.addClassName("mobile-user-name");
        userNameDiv.setText("Selamat datang, " + userName);
        Div userRoleDiv = new Div();
        userRoleDiv.addClassName("mobile-user-role");
        userRoleDiv.setText(userRole);
        userInfo.add(userNameDiv, userRoleDiv);
        Anchor dashboardLink = new Anchor("/dashboard", "Dashboard");
        dashboardLink.addClassName("mobile-menu-item");
        Anchor presensiLink = new Anchor("/presensi", "Presensi");
        presensiLink.addClassName("mobile-menu-item");
        Anchor masterDataLink = new Anchor("/master-data", "Master Data");
        masterDataLink.addClassName("mobile-menu-item");
        Button logoutBtn = new Button("Logout");
        logoutBtn.addClassName("mobile-logout-btn");
        logoutBtn.addClickListener(e -> {
            VaadinSession.getCurrent().getSession().invalidate();
            UI.getCurrent().navigate("auth");
        });
        menuContent.add(userInfo, dashboardLink, presensiLink, masterDataLink, logoutBtn);
        mobileMenu.add(menuContent);
        if (currentUI != null) {
            currentUI.getPage().executeJs(
                "function toggleMobileMenu() {" +
                "  const menu = document.querySelector('.mobile-menu');" +
                "  const overlay = document.querySelector('.mobile-menu-overlay');" +
                "  const hamburger = document.querySelector('.hamburger-menu');" +
                "  menu.classList.toggle('active');" +
                "  overlay.classList.toggle('active');" +
                "  hamburger.classList.toggle('active');" +
                "}" +
                "function closeMobileMenu() {" +
                "  const menu = document.querySelector('.mobile-menu');" +
                "  const overlay = document.querySelector('.mobile-menu-overlay');" +
                "  const hamburger = document.querySelector('.hamburger-menu');" +
                "  menu.classList.remove('active');" +
                "  overlay.classList.remove('active');" +
                "  hamburger.classList.remove('active');" +
                "}"
            );
        }
        add(mobileHeader, menuOverlay, mobileMenu);
    }
    private void createDesktopHeader() {
        Div header = new Div();
        header.addClassName("desktop-header");
        header.getStyle()
                .set("background", "white")
                .set("padding", "20px 0")
                .set("box-shadow", "0 2px 10px rgba(0,0,0,0.1)")
                .set("margin-bottom", "30px")
                .set("width", "100%");
        Div headerContainer = new Div();
        headerContainer.getStyle()
                .set("max-width", "1200px")
                .set("margin", "0 auto")
                .set("padding", "0 15px")
                .set("width", "100%");
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        headerLayout.setAlignItems(Alignment.CENTER);
        HorizontalLayout leftSection = new HorizontalLayout();
        leftSection.setAlignItems(Alignment.CENTER);
        backButton = new Button("Kembali", new Icon(VaadinIcon.ARROW_LEFT));
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.addClickListener(e -> UI.getCurrent().navigate("dashboard"));
        H2 title = new H2("Presensi & Absensi");
        title.getStyle()
                .set("color", "#1f2937")
                .set("margin", "0 0 0 10px")
                .set("font-weight", "600")
                .set("font-size", "1.5rem");
        leftSection.add(backButton, title);
        Div userInfo = new Div();
        userInfo.getStyle()
                .set("background", "#f8fafc")
                .set("padding", "10px 20px")
                .set("border-radius", "8px")
                .set("color", "#374151");
        String userName = currentUser != null ? currentUser.get("namaLengkap").toString() : "User";
        String userRole = isAdmin ? "Admin/HRD" : "Karyawan";
        userInfo.setText(userName + " (" + userRole + ")");
        headerLayout.add(leftSection, userInfo);
        headerContainer.add(headerLayout);
        header.add(headerContainer);
        add(header);
    }
    private void createActionSection() {
        Div actionSection = new Div();
        actionSection.getStyle()
                .set("margin", "20px 0 30px 0") // Adjusted for mobile header
                .set("display", "flex")
                .set("justify-content", "center")
                .set("width", "100%")
                .set("padding", "0 10px"); // Add padding for mobile
        Div actionContainer = new Div();
        actionContainer.getStyle()
                .set("background", "white")
                .set("padding", "20px")
                .set("border-radius", "12px")
                .set("box-shadow", "0 4px 20px rgba(0, 0, 0, 0.08)")
                .set("max-width", "500px")
                .set("width", "100%")
                .set("margin", "0 auto");
        H3 actionTitle = new H3("Aksi Presensi");
        actionTitle.getStyle()
                .set("color", "#1f2937")
                .set("margin", "0 0 25px 0")
                .set("text-align", "center")
                .set("font-size", "1.2rem");
        Div actionButtonsContainer = new Div();
        actionButtonsContainer.getStyle()
                .set("display", "flex")
                .set("flex-wrap", "wrap")
                .set("justify-content", "center")
                .set("align-items", "center")
                .set("gap", "12px");
        checkInButton = new Button("Check In", new Icon(VaadinIcon.SIGN_IN));
        checkInButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        checkInButton.getStyle()
                .set("min-width", "100px")
                .set("flex", "1 1 auto")
                .set("max-width", "140px")
                .set("height", "48px") // Larger touch target
                .set("font-size", "14px");
        checkInButton.addClickListener(e -> performCheckIn());
        checkOutButton = new Button("Check Out", new Icon(VaadinIcon.SIGN_OUT));
        checkOutButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        checkOutButton.getStyle()
                .set("min-width", "100px")
                .set("flex", "1 1 auto")
                .set("max-width", "140px")
                .set("height", "48px") // Larger touch target
                .set("font-size", "14px");
        checkOutButton.addClickListener(e -> performCheckOut());
        absensiButton = new Button("Ajukan Absensi", new Icon(VaadinIcon.CALENDAR_CLOCK));
        absensiButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        absensiButton.getStyle()
                .set("min-width", "120px")
                .set("flex", "1 1 100%") // Full width on small screens
                .set("max-width", "200px")
                .set("height", "48px") // Larger touch target
                .set("font-size", "14px")
                .set("margin-top", "8px");
        absensiButton.addClickListener(e -> showAbsensiDialog());
        actionButtonsContainer.add(checkInButton, checkOutButton, absensiButton);
        Div timeDisplay = new Div();
        timeDisplay.getStyle()
                .set("background", "#f8fafc")
                .set("padding", "15px 20px")
                .set("border-radius", "8px")
                .set("margin-top", "20px")
                .set("text-align", "center")
                .set("color", "#374151")
                .set("font-weight", "500")
                .set("font-size", "14px");
        String currentTime = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy"));
        timeDisplay.setText("Hari ini: " + currentTime);
        actionContainer.add(actionTitle, actionButtonsContainer, timeDisplay);
        actionSection.add(actionContainer);
        add(actionSection);
    }
    private void createFilterSection() {
        Div filterSection = new Div();
        filterSection.getStyle()
                .set("margin-bottom", "30px")
                .set("display", "flex")
                .set("justify-content", "center")
                .set("width", "100%")
                .set("padding", "0 10px"); // Add padding for mobile
        Div filterContainer = new Div();
        filterContainer.getStyle()
                .set("background", "white")
                .set("padding", "20px")
                .set("border-radius", "12px")
                .set("box-shadow", "0 4px 20px rgba(0, 0, 0, 0.08)")
                .set("max-width", "600px")
                .set("width", "100%")
                .set("margin", "0 auto");
        H4 filterTitle = new H4("Filter Data Presensi");
        filterTitle.getStyle()
                .set("color", "#1f2937")
                .set("margin", "0 0 20px 0")
                .set("text-align", "center")
                .set("font-size", "1.1rem");
        Div filterFieldsContainer = new Div();
        filterFieldsContainer.getStyle()
                .set("display", "flex")
                .set("flex-wrap", "wrap")
                .set("justify-content", "center")
                .set("align-items", "end")
                .set("gap", "12px");
        tglAwalFilter = new DatePicker("Tanggal Awal");
        tglAwalFilter.setValue(LocalDate.now().minusDays(30)); // Default 30 hari terakhir
        tglAwalFilter.getStyle()
                .set("min-width", "140px")
                .set("flex", "1 1 auto")
                .set("max-width", "200px");
        tglAkhirFilter = new DatePicker("Tanggal Akhir");
        tglAkhirFilter.setValue(LocalDate.now());
        tglAkhirFilter.getStyle()
                .set("min-width", "140px")
                .set("flex", "1 1 auto")
                .set("max-width", "200px");
        filterButton = new Button("Filter", new Icon(VaadinIcon.SEARCH));
        filterButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        filterButton.addClickListener(e -> loadPresensiData());
        filterButton.getStyle()
                .set("min-width", "80px")
                .set("height", "40px");
        refreshButton = new Button("Refresh", new Icon(VaadinIcon.REFRESH));
        refreshButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        refreshButton.addClickListener(e -> loadPresensiData());
        refreshButton.getStyle()
                .set("min-width", "80px")
                .set("height", "40px");
        filterFieldsContainer.add(tglAwalFilter, tglAkhirFilter, filterButton, refreshButton);
        filterContainer.add(filterTitle, filterFieldsContainer);
        filterSection.add(filterContainer);
        add(filterSection);
    }
    private void createContent() {
        Div content = new Div();
        content.getStyle()
                .set("padding", "0 10px 20px 10px") // Mobile-friendly padding
                .set("max-width", "1200px")
                .set("margin", "0 auto")
                .set("width", "100%");
        Div gridContainer = new Div();
        gridContainer.getStyle()
                .set("background", "white")
                .set("border-radius", "12px")
                .set("box-shadow", "0 4px 20px rgba(0, 0, 0, 0.08)")
                .set("overflow", "hidden"); // Better mobile scrolling
        String gridTitleText = isAdmin ? "Data Presensi Seluruh Karyawan" : "Data Presensi Anda";
        H3 gridTitle = new H3(gridTitleText);
        gridTitle.getStyle()
                .set("color", "#1f2937")
                .set("margin", "0")
                .set("padding", "20px 15px")
                .set("font-size", "1.1rem") // Mobile-friendly font size
                .set("border-bottom", "1px solid #f3f4f6");
        createGrid();
        gridContainer.add(gridTitle, presensiGrid);
        content.add(gridContainer);
        add(content);
    }
    private void createGrid() {
        presensiGrid = new Grid<>();
        presensiGrid.setHeight("400px"); // Reduced height for mobile
        presensiGrid.getStyle()
                .set("font-size", "13px") // Smaller font for mobile
                .set("border", "none");
        if (isAdmin) {
            presensiGrid.addColumn(item -> item.get("namaLengkap"))
                    .setHeader("Nama")
                    .setFlexGrow(2)
                    .setWidth("150px"); // Fixed width for mobile
        }
        presensiGrid.addColumn(item -> {
            Object tglAbsensi = item.get("tglAbsensi");
            if (tglAbsensi instanceof Number) {
                long epoch = ((Number) tglAbsensi).longValue();
                return LocalDate.ofEpochDay(epoch / 86400).format(DateTimeFormatter.ofPattern("dd/MM/yy")); // Shorter format
            }
            return tglAbsensi != null ? tglAbsensi.toString() : "-";
        }).setHeader("Tanggal")
          .setFlexGrow(1)
          .setWidth("80px"); // Fixed width for mobile
        presensiGrid.addColumn(item -> item.get("jamMasuk") != null ? item.get("jamMasuk").toString() : "-")
                .setHeader("Masuk")
                .setFlexGrow(1)
                .setWidth("80px"); // Fixed width for mobile
        presensiGrid.addColumn(item -> item.get("jamKeluar") != null ? item.get("jamKeluar").toString() : "-")
                .setHeader("Keluar")
                .setFlexGrow(1)
                .setWidth("80px"); // Fixed width for mobile
        presensiGrid.addColumn(item -> item.get("namaStatus") != null ? item.get("namaStatus").toString() : "Hadir")
                .setHeader("Status")
                .setFlexGrow(1);
        presensiGrid.getStyle().set("border", "none");
    }
    private void loadPresensiData() {
        if (tglAwalFilter.getValue() == null || tglAkhirFilter.getValue() == null) {
            showNotification("Pilih tanggal awal dan akhir terlebih dahulu", NotificationVariant.LUMO_ERROR);
            return;
        }
        long tglAwal = tglAwalFilter.getValue().atStartOfDay(ZoneOffset.UTC).toEpochSecond();
        long tglAkhir = tglAkhirFilter.getValue().atStartOfDay(ZoneOffset.UTC).toEpochSecond();
        String endpoint = isAdmin ? "/api/presensi/daftar/admin" : "/api/presensi/daftar/pegawai";
        client.get()
                .uri(uriBuilder -> uriBuilder
                        .path(endpoint)
                        .queryParam("tglAwal", tglAwal)
                        .queryParam("tglAkhir", tglAkhir)
                        .build())
                .retrieve()
                .bodyToFlux(Map.class)
                .collectList()
                .subscribe(
                    presensiList -> {
                        if (currentUI != null) {
                            currentUI.access(() -> {
                                presensiGrid.setItems((List<Map<String, Object>>)(List<?>)presensiList);
                                showNotification("Data presensi berhasil dimuat", NotificationVariant.LUMO_SUCCESS);
                            });
                        }
                    },
                    error -> {
                        logger.error("Error loading presensi data", error);
                        if (currentUI != null) {
                            currentUI.access(() -> {
                                showNotification("Gagal memuat data presensi: " + error.getMessage(), 
                                               NotificationVariant.LUMO_ERROR);
                            });
                        }
                    }
                );
    }
    private void performCheckIn() {
        if (currentUser == null) {
            showNotification("User information tidak tersedia", NotificationVariant.LUMO_ERROR);
            return;
        }
        client.get()  // Changed from POST to GET
                .uri("/api/presensi/in")  // Changed from /checkin to /in
                .header("Authorization", "Bearer " + getToken())
                .retrieve()
                .bodyToMono(Map.class)  // Back to Map.class since controller returns Map<String, String>
                .subscribe(
                    response -> {
                        if (currentUI != null) {
                            currentUI.access(() -> {
                                String jamMasuk = response.get("jamMasuk").toString();
                                showNotification("Check In berhasil pada jam: " + jamMasuk, 
                                               NotificationVariant.LUMO_SUCCESS);
                                loadPresensiData();
                            });
                        }
                    },
                    error -> {
                        logger.error("Error performing check in", error);
                        if (currentUI != null) {
                            currentUI.access(() -> {
                                showNotification("Gagal melakukan check in: " + error.getMessage(), 
                                               NotificationVariant.LUMO_ERROR);
                            });
                        }
                    }
                );
    }
    private void performCheckOut() {
        if (currentUser == null) {
            showNotification("User information tidak tersedia", NotificationVariant.LUMO_ERROR);
            return;
        }
        client.get()  // Changed from POST to GET
                .uri("/api/presensi/out")  // Changed from /checkout to /out
                .header("Authorization", "Bearer " + getToken())
                .retrieve()
                .bodyToMono(Map.class)  // Back to Map.class since controller returns Map<String, String>
                .subscribe(
                    response -> {
                        if (currentUI != null) {
                            currentUI.access(() -> {
                                String jamKeluar = response.get("jamKeluar").toString();
                                showNotification("Check Out berhasil pada jam: " + jamKeluar, 
                                               NotificationVariant.LUMO_SUCCESS);
                                loadPresensiData();
                            });
                        }
                    },
                    error -> {
                        logger.error("Error performing check out", error);
                        if (currentUI != null) {
                            currentUI.access(() -> {
                                showNotification("Gagal melakukan check out: " + error.getMessage(), 
                                               NotificationVariant.LUMO_ERROR);
                            });
                        }
                    }
                );
    }
    private void showAbsensiDialog() {
        VerticalLayout dialogContent = new VerticalLayout();
        dialogContent.setSpacing(true);
        dialogContent.setPadding(true);
        H4 dialogTitle = new H4("Ajukan Absensi");
        dialogTitle.getStyle().set("margin", "0 0 20px 0");
        DatePicker tglAbsensi = new DatePicker("Tanggal Absensi");
        tglAbsensi.setValue(LocalDate.now());
        ComboBox<Map<String, Object>> statusCombo = new ComboBox<>("Status Absensi");
        statusCombo.setItemLabelGenerator(item -> item.get("namaStatus").toString());
        loadStatusAbsensi(statusCombo);
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        Button submitButton = new Button("Ajukan", new Icon(VaadinIcon.CHECK));
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.addClickListener(e -> {
            if (tglAbsensi.getValue() != null && statusCombo.getValue() != null) {
                submitAbsensi(tglAbsensi.getValue(), statusCombo.getValue());
            } else {
                showNotification("Pilih tanggal dan status absensi", NotificationVariant.LUMO_ERROR);
            }
        });
        Button cancelButton = new Button("Batal", new Icon(VaadinIcon.CLOSE));
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        buttonLayout.add(submitButton, cancelButton);
        dialogContent.add(dialogTitle, tglAbsensi, statusCombo, buttonLayout);
        showNotification("Fitur dialog absensi akan ditambahkan dalam pengembangan selanjutnya", 
                       NotificationVariant.LUMO_CONTRAST);
    }
    private void loadStatusAbsensi(ComboBox<Map<String, Object>> statusCombo) {
        long tglAwal = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toEpochSecond();
        long tglAkhir = tglAwal;
        client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/presensi/combo/status-absen")
                        .queryParam("tglAwal", tglAwal)
                        .queryParam("tglAkhir", tglAkhir)
                        .build())
                .retrieve()
                .bodyToFlux(Map.class)
                .collectList()
                .subscribe(
                    statusList -> {
                        if (currentUI != null) {
                            currentUI.access(() -> {
                                statusCombo.setItems((List<Map<String, Object>>)(List<?>)statusList);
                            });
                        }
                    },
                    error -> logger.error("Error loading status absensi", error)
                );
    }
    private void submitAbsensi(LocalDate tanggal, Map<String, Object> status) {
        try {
            Map<String, Object> absensiData = new HashMap<>();
            absensiData.put("tglAbsensi", tanggal.atStartOfDay(ZoneOffset.UTC).toEpochSecond());
            absensiData.put("kdStatus", status.get("kdStatus"));
            client.post()
                    .uri("/api/presensi/absensi")
                    .bodyValue(absensiData)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .subscribe(
                        response -> {
                            if (currentUI != null) {
                                currentUI.access(() -> {
                                    showNotification("Absensi berhasil diajukan", NotificationVariant.LUMO_SUCCESS);
                                    loadPresensiData();
                                });
                            }
                        },
                        error -> {
                            logger.error("Error submitting absensi", error);
                            if (currentUI != null) {
                                currentUI.access(() -> {
                                    showNotification("Gagal mengajukan absensi: " + error.getMessage(), 
                                                   NotificationVariant.LUMO_ERROR);
                                });
                            }
                        }
                    );
        } catch (Exception e) {
            logger.error("Error in submitAbsensi", e);
            showNotification("Terjadi kesalahan: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }
    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message, 4000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(variant);
    }
}