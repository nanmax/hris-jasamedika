package com.nanmax.hris.ui;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
@PageTitle("HRIS PT Jasamedika")
public class MainLayout extends AppLayout {
    public MainLayout() {
        addClassName("main-layout");
        getUI().ifPresent(ui -> {
            ui.getPage().addStyleSheet("styles/app.css");
        });
        createHeader();
        createDrawer();
    }
    private void createHeader() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");
        H3 appTitle = new H3("HRIS PT Jasamedika");
        appTitle.addClassName("app-title");
        String userEmail = (String) getUI().get().getSession().getAttribute("userEmail");
        String userRole = (String) getUI().get().getSession().getAttribute("userRole");
        Span userInfo = new Span(userEmail != null ? userEmail : "User");
        userInfo.addClassName("user-info-text");
        Span roleInfo = new Span(userRole != null ? "(" + userRole + ")" : "");
        roleInfo.addClassName("user-role-text");
        Button logoutButton = new Button("Logout", VaadinIcon.SIGN_OUT.create());
        logoutButton.addClassName("logout-button");
        logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        logoutButton.addClickListener(e -> logout());
        Div userSection = new Div(userInfo, roleInfo);
        userSection.addClassName("user-section");
        HorizontalLayout userLayout = new HorizontalLayout(userSection, logoutButton);
        userLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        userLayout.addClassName("user-layout");
        HorizontalLayout headerContent = new HorizontalLayout(toggle, appTitle, userLayout);
        headerContent.addClassName("header-content");
        headerContent.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        headerContent.expand(appTitle);
        headerContent.setWidthFull();
        addToNavbar(headerContent);
    }
    private void createDrawer() {
        SideNav nav = new SideNav();
        nav.addClassName("main-navigation");
        SideNavItem dashboard = new SideNavItem("Dashboard", "dashboard", VaadinIcon.DASHBOARD.create());
        SideNavItem employeeList = new SideNavItem("Daftar Pegawai", "pegawai", VaadinIcon.USERS.create());
        SideNavItem addEmployee = new SideNavItem("Tambah Pegawai", "pegawai/add", VaadinIcon.PLUS.create());
        SideNavItem myAttendance = new SideNavItem("Presensi Saya", "presensi-pegawai", VaadinIcon.CLOCK.create());
        SideNavItem attendanceRequest = new SideNavItem("Absen & Izin", "absen", VaadinIcon.CALENDAR.create());
        String userRole = (String) getUI().get().getSession().getAttribute("userRole");
        boolean isAdmin = "admin".equals(userRole);
        SideNavItem profile = new SideNavItem("Profil Saya", "profile", VaadinIcon.USER.create());
        nav.addItem(dashboard);
        if (isAdmin) {
            nav.addItem(employeeList);
            nav.addItem(addEmployee);
        }
        nav.addItem(myAttendance);
        nav.addItem(attendanceRequest);
        nav.addItem(profile);
        Div drawerContent = new Div();
        drawerContent.addClassName("drawer-content");
        drawerContent.add(nav);
        addToDrawer(drawerContent);
    }
    private void logout() {
        getUI().ifPresent(ui -> {
            ui.getSession().getSession().invalidate();
            ui.navigate("login");
            Notification.show("Anda telah berhasil logout");
        });
    }
}