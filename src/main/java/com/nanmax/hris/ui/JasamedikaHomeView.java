package com.nanmax.hris.ui;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
@PageTitle("HRIS Jasamedika Saranatama")
@Route("")
public class JasamedikaHomeView extends VerticalLayout implements BeforeEnterObserver {
    public JasamedikaHomeView() {
        setupLayout();
        createContent();
    }
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            Notification.show("Selamat datang di HRIS Jasamedika! Mengalihkan ke halaman login...", 
                            2000, Notification.Position.TOP_CENTER)
                       .addThemeVariants(NotificationVariant.LUMO_PRIMARY);
            UI.getCurrent().getPage().executeJs(
                "setTimeout(() => { window.location.href = '/auth'; }, 2000);"
            );
        } catch (Exception e) {
            Notification.show("Terjadi kesalahan, mengalihkan ke halaman login...", 
                            2000, Notification.Position.TOP_CENTER)
                       .addThemeVariants(NotificationVariant.LUMO_ERROR);
            UI.getCurrent().navigate("auth");
        }
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
    }
    private void createContent() {
        Div container = new Div();
        container.getStyle()
                .set("background", "rgba(255, 255, 255, 0.98)")
                .set("border-radius", "24px")
                .set("padding", "80px 60px")
                .set("box-shadow", "0 25px 80px rgba(0, 0, 0, 0.15)")
                .set("text-align", "center")
                .set("max-width", "900px")
                .set("backdrop-filter", "blur(20px)")
                .set("border", "1px solid rgba(255, 255, 255, 0.2)");
        Div logoArea = new Div();
        logoArea.getStyle()
                .set("margin-bottom", "40px")
                .set("padding-bottom", "30px")
                .set("border-bottom", "2px solid #e5e7eb");
        H1 brandName = new H1("JASAMEDIKA");
        brandName.getStyle()
                .set("color", "#1e3a8a")
                .set("font-size", "3.5rem")
                .set("font-weight", "800")
                .set("letter-spacing", "2px")
                .set("margin", "0 0 10px 0")
                .set("text-shadow", "none");
        H2 brandSubtitle = new H2("SARANATAMA");
        brandSubtitle.getStyle()
                .set("color", "#3b82f6")
                .set("font-size", "1.8rem")
                .set("font-weight", "400")
                .set("letter-spacing", "4px")
                .set("margin", "0");
        logoArea.add(brandName, brandSubtitle);
        H1 title = new H1("Human Resources Information System");
        title.getStyle()
                .set("color", "#1f2937")
                .set("font-size", "2.5rem")
                .set("font-weight", "600")
                .set("margin", "40px 0 20px 0")
                .set("line-height", "1.2");
        H2 subtitle = new H2("Sistem Informasi Manajemen SDM Terintegrasi");
        subtitle.getStyle()
                .set("color", "#4b5563")
                .set("font-size", "1.4rem")
                .set("font-weight", "400")
                .set("margin", "0 0 30px 0")
                .set("line-height", "1.4");
        Paragraph description = new Paragraph(
                "Jasamedika Saranatama menghadirkan sistem HRIS yang berkomitmen " +
                "untuk terus melakukan pengembangan berkelanjutan dan menghadirkan " +
                "inovasi-inovasi yang mampu menjawab kebutuhan manajemen SDM modern."
        );
        description.getStyle()
                .set("color", "#6b7280")
                .set("font-size", "1.1rem")
                .set("line-height", "1.7")
                .set("margin", "0 0 40px 0")
                .set("max-width", "700px")
                .set("text-align", "center");
        Div loadingSection = new Div();
        loadingSection.getStyle()
                .set("margin-top", "50px")
                .set("padding", "30px")
                .set("background", "linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%)")
                .set("border-radius", "16px")
                .set("border", "1px solid #bfdbfe");
        Paragraph loadingText = new Paragraph("🏥 Menginisialisasi sistem...");
        loadingText.getStyle()
                .set("color", "#1e40af")
                .set("font-size", "1.2rem")
                .set("font-weight", "500")
                .set("margin", "0 0 10px 0");
        Paragraph redirectText = new Paragraph("Anda akan diarahkan ke halaman login");
        redirectText.getStyle()
                .set("color", "#3b82f6")
                .set("font-size", "1rem")
                .set("margin", "0");
        loadingSection.add(loadingText, redirectText);
        Div footerInfo = new Div();
        footerInfo.getStyle()
                .set("margin-top", "40px")
                .set("padding-top", "30px")
                .set("border-top", "1px solid #e5e7eb");
        Paragraph footerText = new Paragraph("PT. Jasamedika Saranatama - Healthcare Information Systems");
        footerText.getStyle()
                .set("color", "#9ca3af")
                .set("font-size", "0.9rem")
                .set("margin", "0");
        footerInfo.add(footerText);
        container.add(logoArea, title, subtitle, description, loadingSection, footerInfo);
        add(container);
    }
}