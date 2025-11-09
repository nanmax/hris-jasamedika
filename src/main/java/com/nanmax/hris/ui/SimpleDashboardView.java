package com.nanmax.hris.ui;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Map;
@PageTitle("Dashboard | HRIS PT Jasamedika")
@Route("simple-dashboard")
public class SimpleDashboardView extends VerticalLayout {
    private WebClient client = WebClient.create("http://localhost:8080");
    public SimpleDashboardView() {
        setSizeFull();
        String token = (String) VaadinSession.getCurrent().getAttribute("token");
        if (token == null) {
            UI.getCurrent().navigate("login");
            return;
        }
        add(createHeader(), createStatsCards(), createQuickActions());
        getStyle()
            .set("background", "#f8fafc")
            .set("padding", "24px")
            .set("min-height", "100vh");
    }
    private HorizontalLayout createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);
        H1 title = new H1("📊 Dashboard HRIS");
        title.getStyle().set("color", "#1e293b").set("margin", "0");
        HorizontalLayout userSection = new HorizontalLayout();
        userSection.setAlignItems(Alignment.CENTER);
        Map<String, Object> user = (Map<String, Object>) VaadinSession.getCurrent().getAttribute("user");
        String userName = user != null ? (String) user.get("name") : "User";
        Span userInfo = new Span("👤 " + userName);
        userInfo.getStyle().set("color", "#64748b").set("margin-right", "16px");
        Button logoutBtn = new Button("Logout");
        logoutBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        logoutBtn.addClickListener(e -> logout());
        userSection.add(userInfo, logoutBtn);
        header.add(title, userSection);
        return header;
    }
    private HorizontalLayout createStatsCards() {
        HorizontalLayout statsLayout = new HorizontalLayout();
        statsLayout.setWidthFull();
        statsLayout.setSpacing(true);
        statsLayout.add(
            createStatCard("👥 Total Pegawai", "0", "#3b82f6"),
            createStatCard("📅 Hadir Hari Ini", "0", "#10b981"),
            createStatCard("⏰ Terlambat", "0", "#f59e0b"),
            createStatCard("🏠 Izin/Sakit", "0", "#ef4444")
        );
        return statsLayout;
    }
    private Div createStatCard(String title, String value, String color) {
        Div card = new Div();
        card.getStyle()
            .set("background", "white")
            .set("border-radius", "12px")
            .set("padding", "24px")
            .set("box-shadow", "0 1px 3px rgba(0, 0, 0, 0.1)")
            .set("flex", "1")
            .set("min-width", "200px");
        H3 cardTitle = new H3(title);
        cardTitle.getStyle()
            .set("color", "#64748b")
            .set("font-size", "14px")
            .set("font-weight", "500")
            .set("margin", "0 0 8px 0");
        H1 cardValue = new H1(value);
        cardValue.getStyle()
            .set("color", color)
            .set("font-size", "32px")
            .set("font-weight", "700")
            .set("margin", "0");
        card.add(cardTitle, cardValue);
        return card;
    }
    private VerticalLayout createQuickActions() {
        VerticalLayout actionsLayout = new VerticalLayout();
        actionsLayout.setSpacing(true);
        H2 actionsTitle = new H2("🚀 Quick Actions");
        actionsTitle.getStyle().set("color", "#1e293b").set("margin", "24px 0 16px 0");
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setSpacing(true);
        Button employeeBtn = createActionButton("👥 Kelola Pegawai", "Tambah, edit, atau lihat data pegawai");
        Button attendanceBtn = createActionButton("📅 Presensi", "Kelola data kehadiran pegawai");
        Button masterDataBtn = createActionButton("⚙️ Master Data", "Kelola jabatan, departemen, dll");
        Button profileBtn = createActionButton("👤 Profile", "Edit profile perusahaan");
        employeeBtn.addClickListener(e -> showNotification("Fitur Kelola Pegawai (Coming Soon)", NotificationVariant.LUMO_PRIMARY));
        attendanceBtn.addClickListener(e -> showNotification("Fitur Presensi (Coming Soon)", NotificationVariant.LUMO_PRIMARY));
        masterDataBtn.addClickListener(e -> showNotification("Fitur Master Data (Coming Soon)", NotificationVariant.LUMO_PRIMARY));
        profileBtn.addClickListener(e -> showNotification("Fitur Profile (Coming Soon)", NotificationVariant.LUMO_PRIMARY));
        buttonsLayout.add(employeeBtn, attendanceBtn, masterDataBtn, profileBtn);
        actionsLayout.add(actionsTitle, buttonsLayout);
        return actionsLayout;
    }
    private Button createActionButton(String title, String description) {
        Button button = new Button(title);
        button.getStyle()
            .set("background", "white")
            .set("border", "1px solid #e2e8f0")
            .set("border-radius", "12px")
            .set("padding", "20px")
            .set("min-width", "200px")
            .set("height", "auto")
            .set("text-align", "left")
            .set("white-space", "normal");
        button.getElement().addEventListener("mouseenter", e -> 
            button.getStyle().set("box-shadow", "0 4px 12px rgba(0, 0, 0, 0.1)")
        );
        button.getElement().addEventListener("mouseleave", e -> 
            button.getStyle().set("box-shadow", "none")
        );
        return button;
    }
    private void logout() {
        VaadinSession.getCurrent().setAttribute("token", null);
        VaadinSession.getCurrent().setAttribute("user", null);
        VaadinSession.getCurrent().close();
        showNotification("Logged out successfully", NotificationVariant.LUMO_SUCCESS);
        UI.getCurrent().navigate("");
    }
    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = new Notification(message, 3000);
        notification.addThemeVariants(variant);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.open();
    }
}