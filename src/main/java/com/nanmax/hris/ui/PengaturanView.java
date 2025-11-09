package com.nanmax.hris.ui;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import java.util.Map;
@PageTitle("Pengaturan - HRIS Jasamedika")
@Route("pengaturan")
public class PengaturanView extends VerticalLayout {
    private Map<String, Object> currentUser;
    private boolean isAdmin = false;
    public PengaturanView() {
        checkAuthentication();
        getCurrentUserInfo();
        setupLayout();
        createHeader();
        createContent();
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
    private void getCurrentUserInfo() {
        Object user = VaadinSession.getCurrent().getAttribute("user");
        if (user instanceof Map) {
            currentUser = (Map<String, Object>) user;
            Object profile = currentUser.get("profile");
            isAdmin = "ADMIN".equals(profile) || "HRD".equals(currentUser.get("namaDepartemen"));
        }
    }
    private void setupLayout() {
        setSpacing(false);
        setPadding(false);
        setMargin(false);
        setSizeFull();
        getStyle().set("background", "linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%)");
        getStyle().set("min-height", "100vh");
    }
    private void createHeader() {
        Div header = new Div();
        header.getStyle()
                .set("background", "white")
                .set("box-shadow", "0 2px 10px rgba(0,0,0,0.1)")
                .set("margin-bottom", "30px")
                .set("width", "100%");
        Div headerContent = new Div();
        headerContent.getStyle()
                .set("max-width", "1400px")
                .set("margin", "0 auto")
                .set("padding", "20px 40px")
                .set("width", "100%");
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        headerLayout.setAlignItems(Alignment.CENTER);
        HorizontalLayout leftSection = new HorizontalLayout();
        leftSection.setAlignItems(Alignment.CENTER);
        Button backButton = new Button("Kembali", new Icon(VaadinIcon.ARROW_LEFT));
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.addClickListener(e -> UI.getCurrent().navigate("dashboard"));
        H2 title = new H2("Pengaturan Sistem");
        title.getStyle()
                .set("color", "#1f2937")
                .set("margin", "0 0 0 20px")
                .set("font-weight", "600");
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
        headerContent.add(headerLayout);
        header.add(headerContent);
        add(header);
    }
    private void createContent() {
        Div content = new Div();
        content.getStyle()
                .set("padding", "0 40px 40px 40px")
                .set("max-width", "1400px")
                .set("margin", "0 auto")
                .set("width", "100%");
        add(createSection("Pengaturan Pengguna", createUserSettings()));
        if (isAdmin) {
            add(createSection("Pengaturan Sistem", createSystemSettings()));
            add(createSection("Master Data", createMasterDataSettings()));
        }
        content.add(
            createSection("Pengaturan Pengguna", createUserSettings())
        );
        if (isAdmin) {
            content.add(
                createSection("Pengaturan Sistem", createSystemSettings()),
                createSection("Master Data", createMasterDataSettings())
            );
        }
        add(content);
    }
    private Div createSection(String title, VerticalLayout content) {
        Div section = new Div();
        section.getStyle()
                .set("background", "white")
                .set("border-radius", "12px")
                .set("box-shadow", "0 4px 20px rgba(0, 0, 0, 0.08)")
                .set("margin-bottom", "30px")
                .set("overflow", "hidden");
        Div sectionHeader = new Div();
        sectionHeader.getStyle()
                .set("background", "#f8fafc")
                .set("padding", "20px 30px")
                .set("border-bottom", "1px solid #e5e7eb");
        H3 sectionTitle = new H3(title);
        sectionTitle.getStyle()
                .set("color", "#1f2937")
                .set("margin", "0")
                .set("font-size", "18px")
                .set("font-weight", "600");
        sectionHeader.add(sectionTitle);
        Div sectionContent = new Div();
        sectionContent.getStyle()
                .set("padding", "30px");
        sectionContent.add(content);
        section.add(sectionHeader, sectionContent);
        return section;
    }
    private VerticalLayout createUserSettings() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(false);
        Div passwordCard = createSettingCard(
                "Ubah Password",
                "Ganti password akun Anda untuk keamanan yang lebih baik",
                VaadinIcon.LOCK,
                "#f59e0b",
                e -> showNotification("Fitur ubah password sedang dalam pengembangan", NotificationVariant.LUMO_CONTRAST)
        );
        Div profileCard = createSettingCard(
                "Pengaturan Profil",
                "Edit informasi pribadi dan data kontak",
                VaadinIcon.USER,
                "#3b82f6",
                e -> showNotification("Fitur pengaturan profil sedang dalam pengembangan", NotificationVariant.LUMO_CONTRAST)
        );
        Div notificationCard = createSettingCard(
                "Pengaturan Notifikasi",
                "Atur preferensi notifikasi dan pemberitahuan",
                VaadinIcon.BELL,
                "#10b981",
                e -> showNotification("Fitur pengaturan notifikasi sedang dalam pengembangan", NotificationVariant.LUMO_CONTRAST)
        );
        layout.add(passwordCard, profileCard, notificationCard);
        return layout;
    }
    private VerticalLayout createSystemSettings() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(false);
        Div companyCard = createSettingCard(
                "Pengaturan Perusahaan",
                "Konfigurasi informasi perusahaan dan kebijakan",
                VaadinIcon.BUILDING,
                "#8b5cf6",
                e -> showNotification("Fitur pengaturan perusahaan sedang dalam pengembangan", NotificationVariant.LUMO_CONTRAST)
        );
        Div workingHoursCard = createSettingCard(
                "Jam Kerja",
                "Atur jam kerja standar dan waktu operasional",
                VaadinIcon.CLOCK,
                "#ef4444",
                e -> showNotification("Fitur pengaturan jam kerja sedang dalam pengembangan", NotificationVariant.LUMO_CONTRAST)
        );
        Div holidayCard = createSettingCard(
                "Hari Libur",
                "Kelola daftar hari libur nasional dan cuti bersama",
                VaadinIcon.CALENDAR,
                "#06b6d4",
                e -> showNotification("Fitur pengaturan hari libur sedang dalam pengembangan", NotificationVariant.LUMO_CONTRAST)
        );
        Div backupCard = createSettingCard(
                "Backup & Restore",
                "Backup data sistem dan restore dari cadangan",
                VaadinIcon.HARDDRIVE,
                "#6b7280",
                e -> showNotification("Fitur backup & restore sedang dalam pengembangan", NotificationVariant.LUMO_CONTRAST)
        );
        layout.add(companyCard, workingHoursCard, holidayCard, backupCard);
        return layout;
    }
    private VerticalLayout createMasterDataSettings() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(false);
        Div departmentCard = createSettingCard(
                "Kelola Departemen",
                "Tambah, edit, dan hapus data departemen",
                VaadinIcon.BUILDING,
                "#3b82f6",
                e -> showNotification("Fitur kelola departemen sedang dalam pengembangan", NotificationVariant.LUMO_CONTRAST)
        );
        Div positionCard = createSettingCard(
                "Kelola Jabatan",
                "Manajemen data jabatan dan struktur organisasi",
                VaadinIcon.ACADEMY_CAP,
                "#10b981",
                e -> showNotification("Fitur kelola jabatan sedang dalam pengembangan", NotificationVariant.LUMO_CONTRAST)
        );
        Div workUnitCard = createSettingCard(
                "Kelola Unit Kerja",
                "Pengaturan unit kerja dan divisi",
                VaadinIcon.GROUP,
                "#f59e0b",
                e -> showNotification("Fitur kelola unit kerja sedang dalam pengembangan", NotificationVariant.LUMO_CONTRAST)
        );
        Div statusCard = createSettingCard(
                "Kelola Status",
                "Pengaturan status karyawan dan presensi",
                VaadinIcon.CHECK_CIRCLE,
                "#8b5cf6",
                e -> showNotification("Fitur kelola status sedang dalam pengembangan", NotificationVariant.LUMO_CONTRAST)
        );
        layout.add(departmentCard, positionCard, workUnitCard, statusCard);
        return layout;
    }
    private Div createSettingCard(String title, String description, VaadinIcon icon, 
                                 String color, ComponentEventListener<ClickEvent<Button>> clickListener) {
        Div card = new Div();
        card.getStyle()
                .set("border", "1px solid #e5e7eb")
                .set("border-radius", "8px")
                .set("padding", "20px")
                .set("margin-bottom", "15px")
                .set("transition", "border-color 0.3s ease, box-shadow 0.3s ease")
                .set("cursor", "pointer");
        card.getElement().addEventListener("mouseenter", e -> {
            card.getStyle()
                    .set("border-color", color)
                    .set("box-shadow", "0 4px 12px rgba(0, 0, 0, 0.1)");
        });
        card.getElement().addEventListener("mouseleave", e -> {
            card.getStyle()
                    .set("border-color", "#e5e7eb")
                    .set("box-shadow", "none");
        });
        HorizontalLayout cardLayout = new HorizontalLayout();
        cardLayout.setWidthFull();
        cardLayout.setAlignItems(Alignment.CENTER);
        cardLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        HorizontalLayout leftSection = new HorizontalLayout();
        leftSection.setAlignItems(Alignment.CENTER);
        leftSection.setSpacing(true);
        Div iconContainer = new Div();
        iconContainer.getStyle()
                .set("background", color)
                .set("width", "40px")
                .set("height", "40px")
                .set("border-radius", "8px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center");
        Icon cardIcon = new Icon(icon);
        cardIcon.getStyle()
                .set("color", "white")
                .set("font-size", "18px");
        iconContainer.add(cardIcon);
        VerticalLayout textContainer = new VerticalLayout();
        textContainer.setSpacing(false);
        textContainer.setPadding(false);
        H3 cardTitle = new H3(title);
        cardTitle.getStyle()
                .set("color", "#1f2937")
                .set("margin", "0")
                .set("font-weight", "600")
                .set("font-size", "16px");
        Div cardDescription = new Div(description);
        cardDescription.getStyle()
                .set("color", "#6b7280")
                .set("margin", "0")
                .set("font-size", "14px");
        textContainer.add(cardTitle, cardDescription);
        leftSection.add(iconContainer, textContainer);
        Button actionButton = new Button("Kelola", new Icon(VaadinIcon.ARROW_RIGHT));
        actionButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        actionButton.getStyle().set("color", color);
        if (clickListener != null) {
            actionButton.addClickListener(clickListener);
        }
        cardLayout.add(leftSection, actionButton);
        card.add(cardLayout);
        return card;
    }
    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message, 4000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(variant);
    }
}