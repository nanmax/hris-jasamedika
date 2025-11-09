package com.nanmax.hris.ui;
import com.nanmax.hris.ui.components.ResponsiveHeader;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import com.vaadin.flow.component.UI;
@PageTitle("Dashboard - HRIS Jasamedika")
@Route("dashboard")
public class JasamedikaDashboardView extends VerticalLayout {
    private final ResponsiveHeader responsiveHeader;
    public JasamedikaDashboardView() {
        this.responsiveHeader = new ResponsiveHeader(UI.getCurrent())
                .withPageTitle("Dashboard - HRIS")
                .withBackButton(false, "")
                .withHamburgerMenu(true); // Enable hamburger menu only for dashboard
        checkAuthentication();
        setupLayout();
        createHeaders();
        createContent();
    }
    private void checkAuthentication() {
        try {
            Object token = VaadinSession.getCurrent().getAttribute("token");
            Object user = VaadinSession.getCurrent().getAttribute("user");
            if (token == null || user == null) {
                Notification.show("Sesi telah berakhir, silakan login kembali", 
                                3000, Notification.Position.TOP_CENTER)
                           .addThemeVariants(NotificationVariant.LUMO_ERROR);
                VaadinSession.getCurrent().setAttribute("token", null);
                VaadinSession.getCurrent().setAttribute("user", null);
                UI.getCurrent().navigate("auth");
                return;
            }
            if (!(token instanceof String) || ((String) token).trim().isEmpty()) {
                Notification.show("Sesi tidak valid, silakan login kembali", 
                                3000, Notification.Position.TOP_CENTER)
                           .addThemeVariants(NotificationVariant.LUMO_ERROR);
                VaadinSession.getCurrent().getSession().invalidate();
                UI.getCurrent().navigate("auth");
                return;
            }
        } catch (Exception e) {
            Notification.show("Terjadi kesalahan sesi: " + e.getMessage(), 
                            3000, Notification.Position.TOP_CENTER)
                       .addThemeVariants(NotificationVariant.LUMO_ERROR);
            UI.getCurrent().navigate("auth");
        }
    }
    private void setupLayout() {
        ResponsiveHeader.applyMobileLayout(this);
    }
    private void createHeaders() {
        add(responsiveHeader.createMobileHeader());
        add(responsiveHeader.createDesktopHeader());
    }
    private void createContent() {
        Div mainContent = createMainContent();
        add(mainContent);
    }
    private Div createMainContent() {
        Div mainContent = new Div();
        mainContent.addClassName("dashboard-main-content");
        mainContent.addClassName("mobile-full-width");
        mainContent.getStyle()
                .set("padding", "40px")
                .set("max-width", "1400px")
                .set("margin", "0 auto")
                .set("width", "100%")
                .set("box-sizing", "border-box");
        Div welcomeSection = createWelcomeSection();
        welcomeSection.addClassName("mobile-full-width");
        Div statsSection = createStatsSection();
        statsSection.addClassName("mobile-full-width");
        statsSection.addClassName("stats-grid");
        Div actionsSection = createQuickActionsSection();
        actionsSection.addClassName("mobile-full-width");
        Div activitiesSection = createRecentActivitiesSection();
        activitiesSection.addClassName("mobile-full-width");
        mainContent.add(welcomeSection, statsSection, actionsSection, activitiesSection);
        return mainContent;
    }
    private Div createWelcomeSection() {
        Div welcomeSection = new Div();
        welcomeSection.getStyle()
                .set("background", "linear-gradient(135deg, #1e3a8a 0%, #3b82f6 100%)")
                .set("color", "white")
                .set("padding", "40px")
                .set("border-radius", "20px")
                .set("margin-bottom", "40px")
                .set("text-align", "center")
                .set("box-shadow", "0 10px 40px rgba(30, 58, 138, 0.2)");
        H2 welcomeTitle = new H2("Selamat Datang di HRIS Jasamedika Saranatama");
        welcomeTitle.getStyle()
                .set("font-size", "2.2rem")
                .set("font-weight", "600")
                .set("margin", "0 0 15px 0")
                .set("color", "white");
        Paragraph welcomeDesc = new Paragraph(
                "Sistem Informasi Manajemen SDM yang berkomitmen untuk terus melakukan " +
                "pengembangan berkelanjutan dan menghadirkan inovasi untuk kebutuhan HR modern"
        );
        welcomeDesc.getStyle()
                .set("font-size", "1.1rem")
                .set("opacity", "0.9")
                .set("margin", "0")
                .set("max-width", "800px")
                .set("margin", "0 auto");
        welcomeSection.add(welcomeTitle, welcomeDesc);
        return welcomeSection;
    }
    private Div createStatsSection() {
        Div statsSection = new Div();
        statsSection.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(auto-fit, minmax(250px, 1fr))")
                .set("gap", "25px")
                .set("margin-bottom", "40px");
        statsSection.add(
                createStatCard("Karyawan Aktif", "248", VaadinIcon.USERS, "#10b981", "#d1fae5"),
                createStatCard("Departemen", "12", VaadinIcon.BUILDING, "#3b82f6", "#dbeafe"),
                createStatCard("Hadir Hari Ini", "232", VaadinIcon.CHECK_CIRCLE, "#f59e0b", "#fef3c7"),
                createStatCard("Cuti Pending", "5", VaadinIcon.CALENDAR_CLOCK, "#ef4444", "#fee2e2")
        );
        return statsSection;
    }
    private Div createStatCard(String title, String value, VaadinIcon icon, String primaryColor, String bgColor) {
        Div card = new Div();
        card.addClassName("stats-card");
        card.addClassName("mobile-full-width");
        card.getStyle()
                .set("background", "white")
                .set("padding", "30px 25px")
                .set("border-radius", "16px")
                .set("box-shadow", "0 4px 20px rgba(0, 0, 0, 0.08)")
                .set("border", "1px solid #f1f5f9")
                .set("transition", "all 0.3s ease")
                .set("cursor", "pointer");
        Div iconSection = new Div();
        iconSection.getStyle()
                .set("background", bgColor)
                .set("width", "60px")
                .set("height", "60px")
                .set("border-radius", "12px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("margin-bottom", "20px");
        Icon cardIcon = new Icon(icon);
        cardIcon.getStyle()
                .set("color", primaryColor)
                .set("width", "28px")
                .set("height", "28px");
        iconSection.add(cardIcon);
        H3 cardValue = new H3(value);
        cardValue.getStyle()
                .set("font-size", "2.5rem")
                .set("font-weight", "700")
                .set("color", "#1f2937")
                .set("margin", "0 0 8px 0")
                .set("line-height", "1");
        Paragraph cardTitle = new Paragraph(title);
        cardTitle.getStyle()
                .set("color", "#6b7280")
                .set("font-size", "1rem")
                .set("margin", "0")
                .set("font-weight", "500");
        card.add(iconSection, cardValue, cardTitle);
        card.getElement().addEventListener("mouseenter", e -> {
            card.getStyle().set("transform", "translateY(-4px)");
            card.getStyle().set("box-shadow", "0 8px 30px rgba(0, 0, 0, 0.12)");
        });
        card.getElement().addEventListener("mouseleave", e -> {
            card.getStyle().set("transform", "translateY(0)");
            card.getStyle().set("box-shadow", "0 4px 20px rgba(0, 0, 0, 0.08)");
        });
        return card;
    }
    private Div createQuickActionsSection() {
        Div actionsSection = new Div();
        actionsSection.getStyle().set("margin-bottom", "40px");
        H3 sectionTitle = new H3("Aksi Cepat");
        sectionTitle.getStyle()
                .set("color", "#1f2937")
                .set("font-size", "1.5rem")
                .set("font-weight", "600")
                .set("margin", "0 0 25px 0");
        Div actionsGrid = new Div();
        actionsGrid.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(auto-fit, minmax(280px, 1fr))")
                .set("gap", "20px");
        actionsGrid.add(
                createActionCard("Kelola Karyawan", "Tambah, edit, dan kelola data karyawan", 
                    VaadinIcon.USERS, "#3b82f6", () -> UI.getCurrent().navigate("pegawai")),
                createActionCard("Presensi & Absensi", "Lihat dan kelola data kehadiran karyawan", 
                    VaadinIcon.CALENDAR_CLOCK, "#10b981", () -> UI.getCurrent().navigate("presensi")),
                createActionCard("Master Data", "Kelola data jabatan, departemen, dan referensi lainnya", 
                    VaadinIcon.DATABASE, "#f97316", () -> UI.getCurrent().navigate("master-data")),
                createActionCard("Penggajian", "Proses dan kelola penggajian karyawan", 
                    VaadinIcon.WALLET, "#f59e0b", () -> showComingSoon("Penggajian")),
                createActionCard("Laporan", "Generate laporan HR dan analytics", 
                    VaadinIcon.CHART, "#8b5cf6", () -> UI.getCurrent().navigate("laporan")),
                createActionCard("Pengaturan", "Kelola konfigurasi sistem dan master data", 
                    VaadinIcon.COG, "#ef4444", () -> UI.getCurrent().navigate("pengaturan")),
                createActionCard("Evaluasi Kinerja", "Sistem penilaian kinerja karyawan", 
                    VaadinIcon.TROPHY, "#06b6d4", () -> showComingSoon("Evaluasi Kinerja"))
        );
        actionsSection.add(sectionTitle, actionsGrid);
        return actionsSection;
    }
    private Div createActionCard(String title, String description, VaadinIcon icon, String color, Runnable action) {
        Div card = new Div();
        card.getStyle()
                .set("background", "white")
                .set("padding", "25px")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 12px rgba(0, 0, 0, 0.06)")
                .set("border", "1px solid #f1f5f9")
                .set("cursor", "pointer")
                .set("transition", "all 0.3s ease");
        HorizontalLayout cardHeader = new HorizontalLayout();
        cardHeader.setAlignItems(Alignment.CENTER);
        cardHeader.setSpacing(true);
        Icon cardIcon = new Icon(icon);
        cardIcon.getStyle()
                .set("color", color)
                .set("width", "24px")
                .set("height", "24px");
        H4 cardTitle = new H4(title);
        cardTitle.getStyle()
                .set("color", "#1f2937")
                .set("font-size", "1.2rem")
                .set("font-weight", "600")
                .set("margin", "0");
        cardHeader.add(cardIcon, cardTitle);
        Paragraph cardDesc = new Paragraph(description);
        cardDesc.getStyle()
                .set("color", "#6b7280")
                .set("font-size", "0.95rem")
                .set("margin", "10px 0 0 0")
                .set("line-height", "1.5");
        card.add(cardHeader, cardDesc);
        card.addClickListener(e -> action.run());
        card.getElement().addEventListener("mouseenter", e -> {
            card.getStyle().set("transform", "translateY(-2px)");
            card.getStyle().set("box-shadow", "0 8px 25px rgba(0, 0, 0, 0.1)");
            card.getStyle().set("border-color", color);
        });
        card.getElement().addEventListener("mouseleave", e -> {
            card.getStyle().set("transform", "translateY(0)");
            card.getStyle().set("box-shadow", "0 2px 12px rgba(0, 0, 0, 0.06)");
            card.getStyle().set("border-color", "#f1f5f9");
        });
        return card;
    }
    private Div createRecentActivitiesSection() {
        Div activitiesSection = new Div();
        H3 sectionTitle = new H3("Aktivitas Terbaru");
        sectionTitle.getStyle()
                .set("color", "#1f2937")
                .set("font-size", "1.5rem")
                .set("font-weight", "600")
                .set("margin", "0 0 25px 0");
        Div activitiesCard = new Div();
        activitiesCard.getStyle()
                .set("background", "white")
                .set("padding", "30px")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 12px rgba(0, 0, 0, 0.06)")
                .set("border", "1px solid #f1f5f9");
        Paragraph placeholder = new Paragraph("🔄 Sistem sedang memuat aktivitas terbaru...");
        placeholder.getStyle()
                .set("color", "#6b7280")
                .set("text-align", "center")
                .set("padding", "40px")
                .set("margin", "0")
                .set("font-style", "italic");
        activitiesCard.add(placeholder);
        activitiesSection.add(sectionTitle, activitiesCard);
        return activitiesSection;
    }
    private void showComingSoon(String feature) {
        com.vaadin.flow.component.notification.Notification.show(
                "🚧 Fitur " + feature + " sedang dalam pengembangan",
                3000,
                com.vaadin.flow.component.notification.Notification.Position.TOP_CENTER
        );
    }
}