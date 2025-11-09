package com.nanmax.hris.ui;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
@PageTitle("HRIS PT Jasamedika")
@Route("old-home")
public class HomeView extends VerticalLayout {
    public HomeView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        H1 title = new H1("🏢 HRIS PT Jasamedika");
        title.getStyle().set("color", "#2563eb");
        title.getStyle().set("text-align", "center");
        H2 subtitle = new H2("Sistem Manajemen Sumber Daya Manusia");
        subtitle.getStyle().set("color", "#64748b");
        subtitle.getStyle().set("text-align", "center");
        Div apiCard = createInfoCard(
            "🚀 Backend API Ready", 
            "Semua endpoint REST API sudah berfungsi dengan baik",
            "• JWT Authentication\n• Master Data Management\n• Employee Management\n• Attendance System"
        );
        Div statusCard = createInfoCard(
            "📊 Status Aplikasi", 
            "Backend: ✅ Ready | Frontend: ⚠️ Development Mode",
            "• Database H2: Aktif\n• Flyway Migration: Complete\n• Spring Boot: Running"
        );
        Div linksCard = createLinksCard();
        add(title, subtitle, apiCard, statusCard, linksCard);
        getStyle().set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)");
        getStyle().set("min-height", "100vh");
        getStyle().set("padding", "20px");
    }
    private Div createInfoCard(String title, String description, String details) {
        Div card = new Div();
        card.getStyle()
            .set("background", "white")
            .set("border-radius", "12px")
            .set("padding", "24px")
            .set("margin", "12px")
            .set("box-shadow", "0 4px 6px rgba(0, 0, 0, 0.1)")
            .set("max-width", "600px")
            .set("width", "100%");
        H2 cardTitle = new H2(title);
        cardTitle.getStyle().set("margin", "0 0 12px 0").set("color", "#1e293b");
        Paragraph cardDesc = new Paragraph(description);
        cardDesc.getStyle().set("color", "#475569").set("margin", "0 0 12px 0");
        Paragraph cardDetails = new Paragraph(details);
        cardDetails.getStyle()
            .set("color", "#64748b")
            .set("font-size", "14px")
            .set("line-height", "1.6")
            .set("margin", "0")
            .set("white-space", "pre-line");
        card.add(cardTitle, cardDesc, cardDetails);
        return card;
    }
    private Div createLinksCard() {
        Div card = new Div();
        card.getStyle()
            .set("background", "white")
            .set("border-radius", "12px")
            .set("padding", "24px")
            .set("margin", "12px")
            .set("box-shadow", "0 4px 6px rgba(0, 0, 0, 0.1)")
            .set("max-width", "600px")
            .set("width", "100%");
        H2 cardTitle = new H2("🔗 Quick Links");
        cardTitle.getStyle().set("margin", "0 0 16px 0").set("color", "#1e293b");
        Anchor loginLink = new Anchor("login", "🔐 Login System");
        loginLink.getStyle()
            .set("display", "block")
            .set("margin", "8px 0")
            .set("color", "#2563eb")
            .set("text-decoration", "none")
            .set("font-weight", "500");
        Anchor registerLink = new Anchor("register", "📝 Register Company");
        registerLink.getStyle()
            .set("display", "block")
            .set("margin", "8px 0")
            .set("color", "#2563eb")
            .set("text-decoration", "none")
            .set("font-weight", "500");
        Anchor h2Link = new Anchor("/h2-console", "🗄️ H2 Database Console");
        h2Link.getStyle()
            .set("display", "block")
            .set("margin", "8px 0")
            .set("color", "#2563eb")
            .set("text-decoration", "none")
            .set("font-weight", "500");
        h2Link.setTarget("_blank");
        Paragraph apiInfo = new Paragraph("API Base URL: http://localhost:8080/api/*");
        apiInfo.getStyle()
            .set("color", "#64748b")
            .set("font-size", "14px")
            .set("margin", "16px 0 0 0");
        card.add(cardTitle, loginLink, registerLink, h2Link, apiInfo);
        return card;
    }
}