package com.nanmax.hris.ui;

import com.nanmax.hris.config.ApiConfig;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.LocalDate;
import java.util.Arrays;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
@PageTitle("Tambah Pegawai | HRIS PT Jasamedika")
@Route(value = "pegawai/add", layout = MainLayout.class)
public class PegawaiFormView extends VerticalLayout {
    private WebClient client = WebClient.create(ApiConfig.getBaseUrl());
    private TextField namaLengkap = new TextField("Nama Lengkap");
    private EmailField email = new EmailField("Email");
    private PasswordField password = new PasswordField("Password");
    private TextField nikUser = new TextField("NIK");
    private TextField tempatLahir = new TextField("Tempat Lahir");
    private DatePicker tanggalLahir = new DatePicker("Tanggal Lahir");
    private ComboBox<String> profile = new ComboBox<>("Role");
    private ComboBox<MasterData> jabatan = new ComboBox<>("Jabatan");
    private ComboBox<MasterData> departemen = new ComboBox<>("Departemen");
    private ComboBox<MasterData> unitKerja = new ComboBox<>("Unit Kerja");
    private ComboBox<MasterData> jenisKelamin = new ComboBox<>("Jenis Kelamin");
    private ComboBox<MasterData> pendidikan = new ComboBox<>("Pendidikan");
    private Button saveButton = new Button("Simpan", VaadinIcon.CHECK.create());
    private Button cancelButton = new Button("Batal", VaadinIcon.CLOSE.create());
    private MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
    private Upload upload = new Upload(buffer);
    public PegawaiFormView() {
        setSizeFull();
        addClassName("pegawai-form-view");
        add(createTitle(), createForm(), createButtonLayout());
        loadMasterData();
        configureForm();
    }
    private H2 createTitle() {
        H2 title = new H2("Tambah Pegawai Baru");
        title.addClassName("form-title");
        return title;
    }
    private FormLayout createForm() {
        FormLayout formLayout = new FormLayout();
        formLayout.addClassName("pegawai-form");
        namaLengkap.setRequired(true);
        email.setRequired(true);
        password.setRequired(true);
        nikUser.setRequired(true);
        tempatLahir.setRequired(true);
        tanggalLahir.setRequired(true);
        profile.setRequired(true);
        jabatan.setRequired(true);
        departemen.setRequired(true);
        unitKerja.setRequired(true);
        jenisKelamin.setRequired(true);
        pendidikan.setRequired(true);
        namaLengkap.setPlaceholder("Masukkan nama lengkap");
        email.setPlaceholder("user@example.com");
        password.setPlaceholder("Minimal 6 karakter");
        nikUser.setPlaceholder("16 digit NIK");
        tempatLahir.setPlaceholder("Kota tempat lahir");
        tanggalLahir.setPlaceholder("dd/MM/yyyy");
        tanggalLahir.setLocale(java.util.Locale.forLanguageTag("id-ID"));
        DatePicker.DatePickerI18n i18n = new DatePicker.DatePickerI18n();
        i18n.setDateFormat("dd/MM/yyyy");
        i18n.setFirstDayOfWeek(1); // Monday
        tanggalLahir.setI18n(i18n);
        tanggalLahir.setMax(java.time.LocalDate.now().minusYears(17)); // Minimum age 17
        tanggalLahir.setMin(java.time.LocalDate.now().minusYears(65)); // Maximum age 65
        profile.setItems("admin", "pegawai");
        profile.setItemLabelGenerator(item -> item.equals("admin") ? "Administrator" : "Pegawai");
        jabatan.setItemLabelGenerator(MasterData::getNama);
        departemen.setItemLabelGenerator(MasterData::getNama);
        unitKerja.setItemLabelGenerator(MasterData::getNama);
        jenisKelamin.setItemLabelGenerator(MasterData::getNama);
        pendidikan.setItemLabelGenerator(MasterData::getNama);
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        upload.setMaxFileSize(2 * 1024 * 1024); // 2MB
        upload.setDropLabelIcon(VaadinIcon.UPLOAD.create());
        upload.setUploadButton(new Button("Pilih File"));
        upload.setDropLabel(new Span("Drag & drop foto profil di sini"));
        formLayout.add(
            namaLengkap, email, password,
            nikUser, tempatLahir, tanggalLahir,
            profile, jabatan, departemen,
            unitKerja, jenisKelamin, pendidikan,
            upload
        );
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2)
        );
        formLayout.setColspan(namaLengkap, 2);
        formLayout.setColspan(email, 2);
        formLayout.setColspan(upload, 2);
        return formLayout;
    }
    private HorizontalLayout createButtonLayout() {
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        saveButton.addClickListener(e -> savePegawai());
        cancelButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("pegawai")));
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.addClassName("button-layout");
        return buttonLayout;
    }
    private void configureForm() {
        email.setErrorMessage("Email tidak valid");
        password.setMinLength(6);
        password.setErrorMessage("Password minimal 6 karakter");
        nikUser.setMaxLength(16);
        nikUser.setPattern("[0-9]*");
        nikUser.setErrorMessage("NIK harus 16 digit angka");
    }
    private void loadMasterData() {
        String authToken = (String) getUI().get().getSession().getAttribute("authToken");
        if (authToken == null) {
            getUI().ifPresent(ui -> ui.navigate("login"));
            return;
        }
        loadJabatan(authToken);
        loadDepartemen(authToken);
        loadUnitKerja(authToken);
        loadJenisKelamin(authToken);
        loadPendidikan(authToken);
    }
    private void loadJabatan(String authToken) {
        client.get()
            .uri("/api/master/jabatan/daftar")
            .header("Authorization", "Bearer " + authToken)
            .retrieve()
            .bodyToFlux(MasterData.class)
            .collectList()
            .subscribe(
                list -> jabatan.setItems(list),
                err -> showError("Gagal memuat data jabatan")
            );
    }
    private void loadDepartemen(String authToken) {
        client.get()
            .uri("/api/master/departemen/daftar")
            .header("Authorization", "Bearer " + authToken)
            .retrieve()
            .bodyToFlux(MasterData.class)
            .collectList()
            .subscribe(
                list -> departemen.setItems(list),
                err -> showError("Gagal memuat data departemen")
            );
    }
    private void loadUnitKerja(String authToken) {
        client.get()
            .uri("/api/master/unitkerja/daftar")
            .header("Authorization", "Bearer " + authToken)
            .retrieve()
            .bodyToFlux(MasterData.class)
            .collectList()
            .subscribe(
                list -> unitKerja.setItems(list),
                err -> showError("Gagal memuat data unit kerja")
            );
    }
    private void loadJenisKelamin(String authToken) {
        client.get()
            .uri("/api/master/jeniskelamin/daftar")
            .header("Authorization", "Bearer " + authToken)
            .retrieve()
            .bodyToFlux(MasterData.class)
            .collectList()
            .subscribe(
                list -> jenisKelamin.setItems(list),
                err -> showError("Gagal memuat data jenis kelamin")
            );
    }
    private void loadPendidikan(String authToken) {
        client.get()
            .uri("/api/master/pendidikan/daftar")
            .header("Authorization", "Bearer " + authToken)
            .retrieve()
            .bodyToFlux(MasterData.class)
            .collectList()
            .subscribe(
                list -> pendidikan.setItems(list),
                err -> showError("Gagal memuat data pendidikan")
            );
    }
    private void savePegawai() {
        if (!validateForm()) {
            return;
        }
        String authToken = (String) getUI().get().getSession().getAttribute("authToken");
        if (authToken == null) {
            getUI().ifPresent(ui -> ui.navigate("login"));
            return;
        }
        PegawaiRequest pegawaiData = new PegawaiRequest();
        pegawaiData.setProfile(profile.getValue());
        pegawaiData.setNamaLengkap(namaLengkap.getValue());
        pegawaiData.setTempatLahir(tempatLahir.getValue());
        pegawaiData.setTanggalLahir(tanggalLahir.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
        pegawaiData.setEmail(email.getValue());
        pegawaiData.setPassword(password.getValue());
        pegawaiData.setNikUser(nikUser.getValue());
        pegawaiData.setKdJabatan(jabatan.getValue().getKode());
        pegawaiData.setKdDepartemen(departemen.getValue().getKode());
        pegawaiData.setKdUnitKerja(unitKerja.getValue().getKode());
        pegawaiData.setKdJenisKelamin(jenisKelamin.getValue().getKode());
        pegawaiData.setKdPendidikan(pendidikan.getValue().getKode());
        saveButton.setEnabled(false);
        saveButton.setText("Menyimpan...");
        client.post()
            .uri("/api/pegawai/tambah")
            .header("Authorization", "Bearer " + authToken)
            .body(Mono.just(pegawaiData), PegawaiRequest.class)
            .retrieve()
            .bodyToMono(String.class)
            .subscribe(
                result -> {
                    Notification notification = Notification.show("Pegawai berhasil ditambahkan");
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    getUI().ifPresent(ui -> ui.navigate("pegawai"));
                },
                err -> {
                    showError("Gagal menambahkan pegawai: " + err.getMessage());
                    saveButton.setEnabled(true);
                    saveButton.setText("Simpan");
                }
            );
    }
    private boolean validateForm() {
        boolean valid = true;
        if (namaLengkap.getValue() == null || namaLengkap.getValue().trim().isEmpty()) {
            namaLengkap.setErrorMessage("Nama lengkap harus diisi");
            namaLengkap.setInvalid(true);
            valid = false;
        }
        if (email.getValue() == null || email.getValue().trim().isEmpty()) {
            email.setErrorMessage("Email harus diisi");
            email.setInvalid(true);
            valid = false;
        }
        if (password.getValue() == null || password.getValue().length() < 6) {
            password.setErrorMessage("Password minimal 6 karakter");
            password.setInvalid(true);
            valid = false;
        }
        if (nikUser.getValue() == null || nikUser.getValue().length() != 16) {
            nikUser.setErrorMessage("NIK harus 16 digit");
            nikUser.setInvalid(true);
            valid = false;
        }
        if (profile.getValue() == null) {
            showError("Role harus dipilih");
            valid = false;
        }
        if (jabatan.getValue() == null) {
            showError("Jabatan harus dipilih");
            valid = false;
        }
        if (departemen.getValue() == null) {
            showError("Departemen harus dipilih");
            valid = false;
        }
        return valid;
    }
    private void showError(String message) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
    public static class MasterData {
        private Integer kode;
        private String nama;
        public Integer getKode() { return kode; }
        public void setKode(Integer kode) { this.kode = kode; }
        public String getNama() { return nama; }
        public void setNama(String nama) { this.nama = nama; }
    }
    public static class PegawaiRequest {
        private String profile, namaLengkap, tempatLahir, email, password, nikUser;
        private Long tanggalLahir;
        private Integer kdJabatan, kdDepartemen, kdUnitKerja, kdJenisKelamin, kdPendidikan;
        public String getProfile() { return profile; }
        public void setProfile(String profile) { this.profile = profile; }
        public String getNamaLengkap() { return namaLengkap; }
        public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }
        public String getTempatLahir() { return tempatLahir; }
        public void setTempatLahir(String tempatLahir) { this.tempatLahir = tempatLahir; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getNikUser() { return nikUser; }
        public void setNikUser(String nikUser) { this.nikUser = nikUser; }
        public Long getTanggalLahir() { return tanggalLahir; }
        public void setTanggalLahir(Long tanggalLahir) { this.tanggalLahir = tanggalLahir; }
        public Integer getKdJabatan() { return kdJabatan; }
        public void setKdJabatan(Integer kdJabatan) { this.kdJabatan = kdJabatan; }
        public Integer getKdDepartemen() { return kdDepartemen; }
        public void setKdDepartemen(Integer kdDepartemen) { this.kdDepartemen = kdDepartemen; }
        public Integer getKdUnitKerja() { return kdUnitKerja; }
        public void setKdUnitKerja(Integer kdUnitKerja) { this.kdUnitKerja = kdUnitKerja; }
        public Integer getKdJenisKelamin() { return kdJenisKelamin; }
        public void setKdJenisKelamin(Integer kdJenisKelamin) { this.kdJenisKelamin = kdJenisKelamin; }
        public Integer getKdPendidikan() { return kdPendidikan; }
        public void setKdPendidikan(Integer kdPendidikan) { this.kdPendidikan = kdPendidikan; }
    }
}