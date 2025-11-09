package com.nanmax.hris.ui;

import com.nanmax.hris.config.ApiConfig;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
@PageTitle("Presensi Saya | HRIS PT Jasamedika")
@Route(value = "presensi-pegawai", layout = MainLayout.class)
public class PresensiPegawaiListView extends VerticalLayout {
    private DatePicker tglAwal = new DatePicker("Tanggal Awal");
    private DatePicker tglAkhir = new DatePicker("Tanggal Akhir");
    private Button btnLoad = new Button("Tampilkan", VaadinIcon.SEARCH.create());
    private Button btnAbsen = new Button("Absen Sekarang", VaadinIcon.CLOCK.create());
    private Grid<PresensiPegawai> grid = new Grid<>(PresensiPegawai.class, false);
    private WebClient client = WebClient.create(ApiConfig.getBaseUrl());
    public PresensiPegawaiListView() {
        setSizeFull();
        addClassName("presensi-pegawai-view");
        configureGrid();
        configureForm();
        add(createTitle(), createFilterLayout(), createButtonLayout(), grid);
        LocalDate now = LocalDate.now();
        tglAwal.setValue(now.withDayOfMonth(1));
        tglAkhir.setValue(now);
        loadPresensi();
    }
    private H2 createTitle() {
        H2 title = new H2("Riwayat Presensi Saya");
        title.addClassName("view-title");
        return title;
    }
    private void configureGrid() {
        grid.addClassName("presensi-grid");
        grid.setSizeFull();
        grid.addColumn(presensi -> {
            if (presensi.getTglAbsensi() != null) {
                LocalDate date = LocalDate.ofEpochDay(presensi.getTglAbsensi() / (1000 * 60 * 60 * 24));
                return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }
            return "-";
        }).setHeader("Tanggal").setSortable(true);
        grid.addColumn(PresensiPegawai::getJamMasuk).setHeader("Jam Masuk").setSortable(true);
        grid.addColumn(PresensiPegawai::getJamKeluar).setHeader("Jam Keluar").setSortable(true);
        grid.addColumn(PresensiPegawai::getNamaStatus).setHeader("Status").setSortable(true);
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }
    private void configureForm() {
        tglAwal.setPlaceholder("Pilih tanggal awal");
        tglAkhir.setPlaceholder("Pilih tanggal akhir");
        btnLoad.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnLoad.addClickListener(e -> loadPresensi());
        btnAbsen.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        btnAbsen.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("absen")));
    }
    private HorizontalLayout createFilterLayout() {
        HorizontalLayout filterLayout = new HorizontalLayout(tglAwal, tglAkhir, btnLoad);
        filterLayout.setDefaultVerticalComponentAlignment(Alignment.END);
        filterLayout.addClassName("filter-layout");
        return filterLayout;
    }
    private HorizontalLayout createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout(btnAbsen);
        buttonLayout.addClassName("action-layout");
        return buttonLayout;
    }
    private void loadPresensi() {
        String authToken = (String) getUI().get().getSession().getAttribute("authToken");
        if (authToken == null) {
            getUI().ifPresent(ui -> ui.navigate("login"));
            return;
        }
        if (tglAwal.getValue() == null || tglAkhir.getValue() == null) {
            showError("Silakan pilih rentang tanggal");
            return;
        }
        long epochAwal = tglAwal.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long epochAkhir = tglAkhir.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        btnLoad.setEnabled(false);
        btnLoad.setText("Memuat...");
        String url = "/api/presensi/daftar/pegawai?tglAwal=" + epochAwal + "&tglAkhir=" + epochAkhir;
        client.get()
            .uri(url)
            .header("Authorization", "Bearer " + authToken)
            .retrieve()
            .bodyToFlux(PresensiPegawai.class)
            .collectList()
            .subscribe(
                list -> {
                    grid.setItems(list);
                    btnLoad.setEnabled(true);
                    btnLoad.setText("Tampilkan");
                    if (list.isEmpty()) {
                        Notification.show("Tidak ada data presensi untuk periode tersebut");
                    }
                },
                err -> {
                    showError("Gagal memuat data presensi: " + err.getMessage());
                    btnLoad.setEnabled(true);
                    btnLoad.setText("Tampilkan");
                }
            );
    }
    private void showError(String message) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
    public static class PresensiPegawai {
        private Long tglAbsensi;
        private String jamMasuk, jamKeluar, namaStatus;
        public Long getTglAbsensi() { return tglAbsensi; }
        public void setTglAbsensi(Long tglAbsensi) { this.tglAbsensi = tglAbsensi; }
        public String getJamMasuk() { return jamMasuk; }
        public void setJamMasuk(String jamMasuk) { this.jamMasuk = jamMasuk; }
        public String getJamKeluar() { return jamKeluar; }
        public void setJamKeluar(String jamKeluar) { this.jamKeluar = jamKeluar; }
        public String getNamaStatus() { return namaStatus; }
        public void setNamaStatus(String namaStatus) { this.namaStatus = namaStatus; }
    }
}