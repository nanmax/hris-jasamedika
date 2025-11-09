package com.nanmax.hris.ui;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
@PageTitle("Dashboard | HRIS PT Jasamedika")
@Route(value = "old-dashboard", layout = MainLayout.class)
public class DashboardView extends VerticalLayout {
    public DashboardView() {
        setSizeFull();
        addClassName("dashboard-view");
        add(createTitle(), createStatsSection(), createChartsSection());
    }
    private H2 createTitle() {
        H2 title = new H2("Dashboard HRIS");
        title.addClassName("view-title");
        return title;
    }
    private HorizontalLayout createStatsSection() {
        HorizontalLayout statsContainer = new HorizontalLayout();
        statsContainer.addClassName("stats-container");
        statsContainer.setWidthFull();
        Div totalPegawaiCard = createStatsCard("Total Pegawai", "25", VaadinIcon.USERS, "primary");
        Div hadirCard = createStatsCard("Hadir Hari Ini", "23", VaadinIcon.CHECK_CIRCLE, "success");
        Div izinCard = createStatsCard("Izin/Sakit", "2", VaadinIcon.EXCLAMATION_CIRCLE, "warning");
        Div alphaCard = createStatsCard("Alpha", "0", VaadinIcon.CLOSE_CIRCLE, "error");
        statsContainer.add(totalPegawaiCard, hadirCard, izinCard, alphaCard);
        return statsContainer;
    }
    private Div createStatsCard(String title, String value, VaadinIcon icon, String theme) {
        Div card = new Div();
        card.addClassName("stats-card");
        Div iconDiv = new Div();
        iconDiv.addClassName("stats-card-icon");
        iconDiv.addClassName(theme);
        iconDiv.add(icon.create());
        Div content = new Div();
        content.addClassName("stats-card-content");
        H3 titleElement = new H3(title);
        titleElement.addClassName("stats-card-title");
        Span valueElement = new Span(value);
        valueElement.addClassName("stats-card-value");
        Div header = new Div(iconDiv, content);
        header.addClassName("stats-card-header");
        content.add(titleElement, valueElement);
        card.add(header);
        return card;
    }
    private HorizontalLayout createChartsSection() {
        HorizontalLayout chartsContainer = new HorizontalLayout();
        chartsContainer.addClassName("charts-container");
        chartsContainer.setWidthFull();
        Div attendanceChart = createChartCard("Tren Kehadiran Mingguan", "Chart akan menampilkan data kehadiran 7 hari terakhir");
        Div departmentChart = createChartCard("Distribusi Departemen", "Chart akan menampilkan distribusi pegawai per departemen");
        chartsContainer.add(attendanceChart, departmentChart);
        return chartsContainer;
    }
    private Div createChartCard(String title, String description) {
        Div card = new Div();
        card.addClassName("chart-card");
        H3 titleElement = new H3(title);
        titleElement.addClassName("chart-title");
        Div placeholder = new Div();
        placeholder.addClassName("chart-placeholder");
        placeholder.add(new Span(description));
        placeholder.getStyle()
            .set("height", "200px")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("background", "#f5f5f5")
            .set("border", "2px dashed #ccc")
            .set("border-radius", "8px")
            .set("color", "#666");
        card.add(titleElement, placeholder);
        return card;
    }
}