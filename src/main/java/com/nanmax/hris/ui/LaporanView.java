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
@PageTitle("Laporan - HRIS Jasamedika")
@Route("laporan")
public class LaporanView extends VerticalLayout {
    private Map<String, Object> currentUser;
    private boolean isAdmin = false;
    public LaporanView() {
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
        H2 title = new H2("Laporan & Analisis");
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
        Div gridContainer = new Div();
        gridContainer.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(auto-fit, minmax(350px, 1fr))")
                .set("gap", "25px")
                .set("margin-bottom", "30px");
        gridContainer.add(createReportCard(
                "Laporan Presensi",
                "Rekap data kehadiran karyawan harian, mingguan, dan bulanan",
                VaadinIcon.CALENDAR_CLOCK,
                "#10b981",
                e -> showNotification("Fitur laporan presensi sedang dalam pengembangan", NotificationVariant.LUMO_CONTRAST)
        ));
        gridContainer.add(createReportCard(
                "Laporan Karyawan",
                "Data lengkap karyawan dan statistik kepegawaian",
                VaadinIcon.USERS,
                "#3b82f6",
                e -> showNotification("Fitur laporan karyawan sedang dalam pengembangan", NotificationVariant.LUMO_CONTRAST)
        ));
        gridContainer.add(createReportCard(
                "Laporan Absensi",
                "Rekap data absensi dan ijin karyawan",
                VaadinIcon.CALENDAR_USER,
                "#f59e0b",
                e -> showNotification("Fitur laporan absensi sedang dalam pengembangan", NotificationVariant.LUMO_CONTRAST)
        ));
        if (isAdmin) {
            gridContainer.add(createReportCard(
                    "Dashboard Analytics",
                    "Visualisasi data dan trend kepegawaian",
                    VaadinIcon.CHART_LINE,
                    "#8b5cf6",
                    e -> showNotification("Fitur analytics dashboard sedang dalam pengembangan", NotificationVariant.LUMO_CONTRAST)
            ));
            gridContainer.add(createReportCard(
                    "Laporan Departemen",
                    "Analisis data per departemen dan unit kerja",
                    VaadinIcon.BUILDING,
                    "#ef4444",
                    e -> showNotification("Fitur laporan departemen sedang dalam pengembangan", NotificationVariant.LUMO_CONTRAST)
            ));
            gridContainer.add(createReportCard(
                    "Export Data",
                    "Ekspor data ke Excel, PDF, dan format lainnya",
                    VaadinIcon.DOWNLOAD,
                    "#6b7280",
                    e -> showNotification("Fitur export data sedang dalam pengembangan", NotificationVariant.LUMO_CONTRAST)
            ));
        }
        content.add(gridContainer);
        add(content);
    }
    private Div createReportCard(String title, String description, VaadinIcon icon, 
                                String color, ComponentEventListener<ClickEvent<Button>> clickListener) {
        Div card = new Div();
        card.getStyle()
                .set("background", "white")
                .set("border-radius", "12px")
                .set("box-shadow", "0 4px 20px rgba(0, 0, 0, 0.08)")
                .set("padding", "30px")
                .set("transition", "transform 0.3s ease, box-shadow 0.3s ease")
                .set("cursor", "pointer");
        card.addClickListener(e -> {
            if (clickListener != null) {
                clickListener.onComponentEvent(null);
            }
        });
        card.getElement().addEventListener("mouseenter", e -> {
            card.getStyle()
                    .set("transform", "translateY(-2px)")
                    .set("box-shadow", "0 8px 25px rgba(0, 0, 0, 0.15)");
        });
        card.getElement().addEventListener("mouseleave", e -> {
            card.getStyle()
                    .set("transform", "translateY(0)")
                    .set("box-shadow", "0 4px 20px rgba(0, 0, 0, 0.08)");
        });
        Div iconSection = new Div();
        iconSection.getStyle()
                .set("background", color)
                .set("width", "60px")
                .set("height", "60px")
                .set("border-radius", "12px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("margin-bottom", "20px");
        Icon cardIcon = new Icon(icon);
        cardIcon.getStyle()
                .set("color", "white")
                .set("font-size", "24px");
        iconSection.add(cardIcon);
        H4 cardTitle = new H4(title);
        cardTitle.getStyle()
                .set("color", "#1f2937")
                .set("margin", "0 0 10px 0")
                .set("font-weight", "600")
                .set("font-size", "18px");
        Paragraph cardDescription = new Paragraph(description);
        cardDescription.getStyle()
                .set("color", "#6b7280")
                .set("margin", "0 0 20px 0")
                .set("font-size", "14px")
                .set("line-height", "1.5");
        Button actionButton = new Button("Lihat Detail", new Icon(VaadinIcon.ARROW_RIGHT));
        actionButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        actionButton.getStyle()
                .set("color", color)
                .set("padding", "0");
        if (clickListener != null) {
            actionButton.addClickListener(clickListener);
        }
        card.add(iconSection, cardTitle, cardDescription, actionButton);
        return card;
    }
    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message, 4000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(variant);
    }
}