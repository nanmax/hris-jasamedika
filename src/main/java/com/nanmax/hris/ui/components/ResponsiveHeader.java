package com.nanmax.hris.ui.components;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinSession;
import java.util.Map;
public class ResponsiveHeader {
    private final UI currentUI;
    private Map<String, Object> currentUser;
    private boolean isAdmin = false;
    private boolean showBackButton = true;
    private boolean showHamburgerMenu = false;
    private String pageTitle = "HRIS";
    private String backNavigationTarget = "";
    public ResponsiveHeader(UI ui) {
        this.currentUI = ui;
        getCurrentUserInfo();
        setupMobileCSS();
    }
    public ResponsiveHeader withPageTitle(String title) {
        this.pageTitle = title;
        return this;
    }
    public ResponsiveHeader withBackButton(boolean show, String navigationTarget) {
        this.showBackButton = show;
        this.backNavigationTarget = navigationTarget;
        return this;
    }
    public ResponsiveHeader withHamburgerMenu(boolean show) {
        this.showHamburgerMenu = show;
        return this;
    }
    private void getCurrentUserInfo() {
        Object user = VaadinSession.getCurrent().getAttribute("user");
        if (user instanceof Map) {
            currentUser = (Map<String, Object>) user;
            Object profile = currentUser.get("profile");
            isAdmin = "ADMIN".equals(profile) || "HRD".equals(currentUser.get("namaDepartemen"));
        }
    }
    private void setupMobileCSS() {
        if (currentUI != null) {
            currentUI.getPage().executeJs("document.querySelector('meta[name=viewport]') || " +
                "document.head.appendChild(Object.assign(document.createElement('meta'), " +
                "{name: 'viewport', content: 'width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no'}))");
            currentUI.getPage().addStyleSheet("https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap");
            currentUI.getPage().addStyleSheet("./styles/mobile.css");
        }
    }
    public Div createMobileHeader() {
        Div mobileHeader = new Div();
        mobileHeader.addClassName("mobile-header");
        mobileHeader.addClassName("mobile-full-width");
        H3 title = new H3("HRIS Jasamedika");
        title.getStyle()
                .set("margin", "0")
                .set("color", "#1f2937")
                .set("font-size", "18px")
                .set("font-weight", "600");
        if (showHamburgerMenu) {
            Div hamburgerMenu = new Div();
            hamburgerMenu.addClassName("hamburger-menu");
            hamburgerMenu.getElement().setAttribute("onclick", "toggleMobileMenu()");
            hamburgerMenu.addClickListener(e -> {
                if (currentUI != null) {
                    currentUI.getPage().executeJs("toggleMobileMenu()");
                }
            });
            hamburgerMenu.getStyle()
                    .set("cursor", "pointer")
                    .set("user-select", "none")
                    .set("z-index", "1001");
            Span line1 = new Span();
            Span line2 = new Span();  
            Span line3 = new Span();
            hamburgerMenu.add(line1, line2, line3);
            mobileHeader.add(title, hamburgerMenu);
            addMobileMenuComponents(mobileHeader);
        } else {
            mobileHeader.add(title);
        }
        return mobileHeader;
    }
    private void addMobileMenuComponents(Div container) {
        Div menuOverlay = new Div();
        menuOverlay.addClassName("mobile-menu-overlay");
        menuOverlay.getElement().setAttribute("onclick", "closeMobileMenu()");
        Div mobileMenu = new Div();
        mobileMenu.addClassName("mobile-menu");
        mobileMenu.setId("hris-mobile-menu"); // Unique ID
        Div menuContent = new Div();
        menuContent.addClassName("mobile-menu-content");
        Button closeBtn = new Button("×");
        closeBtn.getStyle()
                .set("position", "absolute")
                .set("top", "10px")
                .set("right", "15px")
                .set("background", "none")
                .set("border", "none")
                .set("font-size", "24px")
                .set("color", "#666")
                .set("cursor", "pointer")
                .set("padding", "5px")
                .set("line-height", "1");
        closeBtn.addClickListener(e -> {
            if (currentUI != null) {
                currentUI.getPage().executeJs("closeMobileMenu()");
            }
        });
        H3 menuTitle = new H3("Dashboard HRIS");
        menuTitle.getStyle()
                .set("margin", "0 0 20px 0")
                .set("color", "#1e3a8a")
                .set("font-size", "20px")
                .set("font-weight", "600")
                .set("text-align", "center")
                .set("border-bottom", "2px solid #e5e7eb")
                .set("padding-bottom", "15px");
        Div userInfo = new Div();
        userInfo.addClassName("mobile-user-info");
        String userName = currentUser != null ? currentUser.get("namaLengkap").toString() : "Administrator";
        String userRole = isAdmin ? "Admin/HRD" : "Karyawan";
        Div userNameDiv = new Div();
        userNameDiv.addClassName("mobile-user-name");
        userNameDiv.setText("👤 " + userName);
        Div userRoleDiv = new Div();
        userRoleDiv.addClassName("mobile-user-role");
        userRoleDiv.setText("🏢 " + userRole);
        userInfo.add(userNameDiv, userRoleDiv);
        Button logoutBtn = new Button("🚪 Logout dari Sistem");
        logoutBtn.addClassName("mobile-logout-btn");
        logoutBtn.getStyle()
                .set("width", "100%")
                .set("margin-top", "20px")
                .set("background", "#dc2626")
                .set("color", "white")
                .set("border", "none")
                .set("padding", "15px")
                .set("border-radius", "8px")
                .set("font-size", "16px")
                .set("font-weight", "600")
                .set("cursor", "pointer")
                .set("text-align", "center");
        logoutBtn.addClickListener(e -> {
            VaadinSession.getCurrent().setAttribute("token", null);
            VaadinSession.getCurrent().setAttribute("user", null);
            VaadinSession.getCurrent().getSession().invalidate();
            UI.getCurrent().navigate("auth");
            if (currentUI != null) {
                currentUI.getPage().executeJs("closeMobileMenu()");
            }
        });
        menuContent.add(closeBtn, menuTitle, userInfo, logoutBtn);
        mobileMenu.add(menuContent);
        if (container.getParent().isPresent()) {
            var parent = container.getParent().get();
            parent.getElement().appendChild(menuOverlay.getElement());
            parent.getElement().appendChild(mobileMenu.getElement());
        } else {
            if (currentUI != null) {
                currentUI.add(menuOverlay, mobileMenu);
            }
        }
        if (currentUI != null) {
            currentUI.getPage().executeJs("""
                console.log('🚀 Initializing HRIS mobile menu...');
                function toggleMobileMenu() {
                    console.log('🍔 toggleMobileMenu called');
                    const menu = document.getElementById('hris-mobile-menu');
                    const overlay = document.querySelector('.mobile-menu-overlay');
                    const hamburger = document.querySelector('.hamburger-menu');
                    console.log('Elements found:', {
                        menu: !!menu,
                        overlay: !!overlay, 
                        hamburger: !!hamburger
                    });
                    if (menu && overlay && hamburger) {
                        const isActive = menu.classList.contains('active');
                        console.log('Current state - isActive:', isActive);
                        if (isActive) {
                            menu.classList.remove('active');
                            overlay.classList.remove('active');
                            hamburger.classList.remove('active');
                            document.body.style.overflow = 'auto';
                            console.log('✅ Menu closed');
                        } else {
                            menu.classList.add('active');
                            overlay.classList.add('active'); 
                            hamburger.classList.add('active');
                            document.body.style.overflow = 'hidden';
                            console.log('✅ Menu opened');
                        }
                    } else {
                        console.error('❌ Required elements not found:', {menu, overlay, hamburger});
                        if (!menu) {
                            console.log('🔍 Looking for menu with different selectors...');
                            const altMenu = document.querySelector('[id*="mobile-menu"], .mobile-menu');
                            if (altMenu) {
                                console.log('📱 Found alternative menu element');
                                altMenu.style.display = altMenu.style.display === 'block' ? 'none' : 'block';
                            }
                        }
                    }
                }
                function closeMobileMenu() {
                    console.log('❌ closeMobileMenu called');
                    const menu = document.getElementById('hris-mobile-menu');
                    const overlay = document.querySelector('.mobile-menu-overlay');
                    const hamburger = document.querySelector('.hamburger-menu');
                    if (menu) menu.classList.remove('active');
                    if (overlay) overlay.classList.remove('active');
                    if (hamburger) hamburger.classList.remove('active');
                    document.body.style.overflow = 'auto';
                    console.log('✅ Menu closed via closeMobileMenu');
                }
                window.toggleMobileMenu = toggleMobileMenu;
                window.closeMobileMenu = closeMobileMenu;
                document.addEventListener('DOMContentLoaded', function() {
                    console.log('📱 Setting up additional mobile menu listeners...');
                    const hamburger = document.querySelector('.hamburger-menu');
                    if (hamburger) {
                        hamburger.addEventListener('click', function(e) {
                            e.preventDefault();
                            e.stopPropagation();
                            console.log('🍔 Hamburger clicked via event listener');
                            toggleMobileMenu();
                        });
                        console.log('✅ Hamburger click listener added');
                    }
                    const overlay = document.querySelector('.mobile-menu-overlay');
                    if (overlay) {
                        overlay.addEventListener('click', closeMobileMenu);
                        console.log('✅ Overlay click listener added');
                    }
                });
                console.log('🎉 HRIS mobile menu JavaScript initialized successfully');
            """);
        }
    }
    public Div createDesktopHeader() {
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
        headerLayout.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);
        headerLayout.setAlignItems(HorizontalLayout.Alignment.CENTER);
        HorizontalLayout leftSection = new HorizontalLayout();
        leftSection.setAlignItems(HorizontalLayout.Alignment.CENTER);
        if (showBackButton && !backNavigationTarget.isEmpty()) {
            Button backButton = new Button("Kembali", new Icon(VaadinIcon.ARROW_LEFT));
            backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            backButton.addClickListener(e -> UI.getCurrent().navigate(backNavigationTarget));
            leftSection.add(backButton);
        }
        H2 title = new H2(pageTitle);
        title.getStyle()
                .set("color", "#1f2937")
                .set("margin", "0 0 0 10px")
                .set("font-weight", "600")
                .set("font-size", "1.5rem");
        leftSection.add(title);
        HorizontalLayout rightSection = new HorizontalLayout();
        rightSection.setAlignItems(HorizontalLayout.Alignment.CENTER);
        if (currentUser != null) {
            String userName = currentUser.get("namaLengkap").toString();
            String userRole = isAdmin ? "Admin/HRD" : "Karyawan";
            Span userInfo = new Span("Selamat datang, " + userName);
            userInfo.getStyle()
                    .set("color", "#6b7280")
                    .set("margin-right", "20px")
                    .set("font-weight", "500");
            rightSection.add(userInfo);
            if (showHamburgerMenu) {
                Button logoutBtn = new Button("Logout", new Icon(VaadinIcon.SIGN_OUT));
                logoutBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                logoutBtn.getStyle()
                        .set("color", "#ef4444")
                        .set("border", "1px solid #fecaca")
                        .set("border-radius", "8px")
                        .set("padding", "8px 16px");
                logoutBtn.addClickListener(e -> {
                    VaadinSession.getCurrent().getSession().invalidate();
                    UI.getCurrent().navigate("auth");
                });
                rightSection.add(logoutBtn);
            }
            headerLayout.add(leftSection, rightSection);
        } else {
            headerLayout.add(leftSection);
        }
        headerContainer.add(headerLayout);
        header.add(headerContainer);
        return header;
    }
    public static void applyMobileLayout(com.vaadin.flow.component.orderedlayout.VerticalLayout layout) {
        layout.setSpacing(false);
        layout.setPadding(false);
        layout.setMargin(false);
        layout.setSizeFull();
        layout.getStyle()
            .set("background", "linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%)")
            .set("min-height", "100vh")
            .set("font-size", "14px")
            .set("overflow-x", "hidden");
    }
}