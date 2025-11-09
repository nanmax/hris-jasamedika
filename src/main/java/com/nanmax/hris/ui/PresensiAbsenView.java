package com.nanmax.hris.ui;

import com.nanmax.hris.config.ApiConfig;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
@PageTitle("Absen & Izin | HRIS PT Jasamedika")
@Route(value = "absen", layout = MainLayout.class)
public class PresensiAbsenView extends VerticalLayout {
    private DatePicker tglAbsensi = new DatePicker("Tanggal");
    private ComboBox<StatusAbsen> kdStatus = new ComboBox<>("Jenis Absen/Izin");
    private TextArea keterangan = new TextArea("Keterangan");
    private Button btnAbsen = new Button("Submit", VaadinIcon.CHECK.create());
    private Button btnPresensiHadir = new Button("Presensi Hadir", VaadinIcon.CLOCK.create());
    private WebClient client = WebClient.create(ApiConfig.getBaseUrl());
    public PresensiAbsenView() {
        setSizeFull();
        addClassName("presensi-absen-view");
        configureForm();
        add(createTitle(), createInfoSection(), createForm(), createButtonLayout());
        tglAbsensi.setValue(LocalDate.now());
        loadStatusOptions();
    }
    private H2 createTitle() {
        H2 title = new H2("Presensi & Pengajuan Izin");
        title.addClassName("view-title");
        return title;
    }
    private VerticalLayout createInfoSection() {
        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.addClassName("info-section");
        Paragraph info1 = new Paragraph("• Gunakan tombol 'Presensi Hadir' untuk absen masuk/keluar hari ini");
        Paragraph info2 = new Paragraph("• Gunakan form di bawah untuk mengajukan izin atau absen pada tanggal tertentu");
        infoLayout.add(info1, info2);
        return infoLayout;
    }
    private FormLayout createForm() {
        FormLayout formLayout = new FormLayout();
        formLayout.addClassName("absen-form");
        tglAbsensi.setRequired(true);
        kdStatus.setRequired(true);
        keterangan.setRequired(true);
        tglAbsensi.setPlaceholder("Pilih tanggal");
        kdStatus.setPlaceholder("Pilih jenis absen/izin");
        keterangan.setPlaceholder("Masukkan keterangan atau alasan...");
        keterangan.setMaxLength(500);
        keterangan.setMinHeight("100px");
        formLayout.add(tglAbsensi, kdStatus, keterangan);
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2)
        );
        formLayout.setColspan(keterangan, 2);
        return formLayout;
    }
    private void configureForm() {
        btnAbsen.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnAbsen.addClickListener(e -> submitAbsen());
        btnPresensiHadir.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        btnPresensiHadir.addClickListener(e -> presensiHadir());
        kdStatus.setItemLabelGenerator(StatusAbsen::getNama);
    }
    private HorizontalLayout createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout(btnPresensiHadir, btnAbsen);
        buttonLayout.addClassName("button-layout");
        return buttonLayout;
    }
    private void loadStatusOptions() {
        StatusAbsen[] statuses = {
            new StatusAbsen(2, "Sakit"),
            new StatusAbsen(3, "Izin"),
            new StatusAbsen(4, "Cuti"),
            new StatusAbsen(5, "Alpha/Tanpa Keterangan")
        };
        kdStatus.setItems(statuses);
    }
    private void presensiHadir() {
        String authToken = (String) getUI().get().getSession().getAttribute("authToken");
        if (authToken == null) {
            getUI().ifPresent(ui -> ui.navigate("login"));
            return;
        }
        btnPresensiHadir.setEnabled(false);
        btnPresensiHadir.setText("Memproses...");
        long currentTime = System.currentTimeMillis();
        AbsenRequest req = new AbsenRequest(currentTime, 1); // 1 = Hadir
        client.post()
            .uri("/api/presensi/abseni")
            .header("Authorization", "Bearer " + authToken)
            .body(Mono.just(req), AbsenRequest.class)
            .retrieve()
            .bodyToMono(String.class)
            .subscribe(
                result -> {
                    Notification notification = Notification.show(
                        "Presensi berhasil dicatat pada " + 
                        LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    );
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    btnPresensiHadir.setEnabled(true);
                    btnPresensiHadir.setText("Presensi Hadir");
                },
                err -> {
                    showError("Gagal mencatat presensi: " + err.getMessage());
                    btnPresensiHadir.setEnabled(true);
                    btnPresensiHadir.setText("Presensi Hadir");
                }
            );
    }
    private void submitAbsen() {
        if (!validateForm()) {
            return;
        }
        String authToken = (String) getUI().get().getSession().getAttribute("authToken");
        if (authToken == null) {
            getUI().ifPresent(ui -> ui.navigate("login"));
            return;
        }
        btnAbsen.setEnabled(false);
        btnAbsen.setText("Mengirim...");
        long epochTime = tglAbsensi.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        AbsenRequest req = new AbsenRequest(epochTime, kdStatus.getValue().getKode());
        client.post()
            .uri("/api/presensi/abseni")
            .header("Authorization", "Bearer " + authToken)
            .body(Mono.just(req), AbsenRequest.class)
            .retrieve()
            .bodyToMono(String.class)
            .subscribe(
                result -> {
                    Notification notification = Notification.show(
                        "Pengajuan " + kdStatus.getValue().getNama().toLowerCase() + 
                        " berhasil dikirim untuk tanggal " + 
                        tglAbsensi.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    );
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    clearForm();
                    btnAbsen.setEnabled(true);
                    btnAbsen.setText("Submit");
                },
                err -> {
                    showError("Gagal mengirim pengajuan: " + err.getMessage());
                    btnAbsen.setEnabled(true);
                    btnAbsen.setText("Submit");
                }
            );
    }
    private boolean validateForm() {
        boolean valid = true;
        if (tglAbsensi.getValue() == null) {
            showError("Tanggal harus dipilih");
            valid = false;
        }
        if (kdStatus.getValue() == null) {
            showError("Jenis absen/izin harus dipilih");
            valid = false;
        }
        if (keterangan.getValue() == null || keterangan.getValue().trim().isEmpty()) {
            keterangan.setErrorMessage("Keterangan harus diisi");
            keterangan.setInvalid(true);
            valid = false;
        }
        return valid;
    }
    private void clearForm() {
        tglAbsensi.setValue(LocalDate.now());
        kdStatus.setValue(null);
        keterangan.setValue("");
        keterangan.setInvalid(false);
    }
    private void showError(String message) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
    public static class StatusAbsen {
        private Integer kode;
        private String nama;
        public StatusAbsen(Integer kode, String nama) {
            this.kode = kode;
            this.nama = nama;
        }
        public Integer getKode() { return kode; }
        public void setKode(Integer kode) { this.kode = kode; }
        public String getNama() { return nama; }
        public void setNama(String nama) { this.nama = nama; }
    }
    public static class AbsenRequest {
        private Long tglAbsensi;
        private Integer kdStatus;
        public AbsenRequest(Long tglAbsensi, Integer kdStatus) {
            this.tglAbsensi = tglAbsensi;
            this.kdStatus = kdStatus;
        }
        public Long getTglAbsensi() { return tglAbsensi; }
        public void setTglAbsensi(Long tglAbsensi) { this.tglAbsensi = tglAbsensi; }
        public Integer getKdStatus() { return kdStatus; }
        public void setKdStatus(Integer kdStatus) { this.kdStatus = kdStatus; }
    }
}