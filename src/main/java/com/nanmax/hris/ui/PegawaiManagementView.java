package com.nanmax.hris.ui;

import com.nanmax.hris.config.ApiConfig;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@PageTitle("Kelola Pegawai - HRIS Jasamedika")
@Route("pegawai")
public class PegawaiManagementView extends VerticalLayout {
    private static final Logger logger = LoggerFactory.getLogger(PegawaiManagementView.class);
    private final WebClient client;
    private Grid<Map<String, Object>> pegawaiGrid;
    private Button tambahButton, refreshButton, backButton;
    private TextField namaLengkapField;
    private EmailField emailField;
    private TextField tempatLahirField;
    private DatePicker tanggalLahirField;
    private ComboBox<Map<String, Object>> jenisKelaminCombo;
    private ComboBox<Map<String, Object>> pendidikanCombo;
    private ComboBox<Map<String, Object>> jabatanCombo;
    private ComboBox<Map<String, Object>> departemenCombo;
    private ComboBox<Map<String, Object>> unitKerjaCombo;
    private PasswordField passwordField;
    private PasswordField passwordConfirmField;
    private boolean isFormVisible = false;
    private VerticalLayout formLayout;
    public PegawaiManagementView() {
        this.client = WebClient.builder()
                .baseUrl(ApiConfig.getBaseUrl())
                .defaultHeader("Authorization", "Bearer " + getToken())
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
        checkAuthentication();
        setupLayout();
        createHeader();
        createContent();
        loadComboData();
        loadPegawaiData();
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
    private String getToken() {
        Object token = VaadinSession.getCurrent().getAttribute("token");
        return token != null ? token.toString() : "";
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
        backButton = new Button("Kembali", new Icon(VaadinIcon.ARROW_LEFT));
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.addClickListener(e -> UI.getCurrent().navigate("dashboard"));
        H2 title = new H2("Kelola Pegawai");
        title.getStyle()
                .set("color", "#1f2937")
                .set("margin", "0 0 0 20px")
                .set("font-weight", "600");
        leftSection.add(backButton, title);
        HorizontalLayout rightSection = new HorizontalLayout();
        rightSection.setAlignItems(Alignment.CENTER);
        rightSection.setSpacing(true);
        refreshButton = new Button("Refresh", new Icon(VaadinIcon.REFRESH));
        refreshButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        refreshButton.addClickListener(e -> loadPegawaiData());
        tambahButton = new Button("Tambah Pegawai", new Icon(VaadinIcon.PLUS));
        tambahButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        tambahButton.addClickListener(e -> toggleForm());
        rightSection.add(refreshButton, tambahButton);
        headerLayout.add(leftSection, rightSection);
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
        createFormLayout();
        createGridLayout();
        content.add(formLayout, createGridContainer());
        add(content);
    }
    private void createFormLayout() {
        formLayout = new VerticalLayout();
        formLayout.setVisible(false);
        formLayout.getStyle()
                .set("background", "white")
                .set("padding", "30px")
                .set("border-radius", "12px")
                .set("box-shadow", "0 4px 20px rgba(0, 0, 0, 0.08)")
                .set("margin-bottom", "30px");
        H3 formTitle = new H3("Tambah Pegawai Baru");
        formTitle.getStyle()
                .set("color", "#1f2937")
                .set("margin", "0 0 25px 0");
        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2),
                new FormLayout.ResponsiveStep("1000px", 3)
        );
        namaLengkapField = new TextField("Nama Lengkap");
        namaLengkapField.setRequired(true);
        emailField = new EmailField("Email");
        emailField.setRequired(true);
        tempatLahirField = new TextField("Tempat Lahir");
        tanggalLahirField = new DatePicker("Tanggal Lahir");
        jenisKelaminCombo = new ComboBox<>("Jenis Kelamin");
        jenisKelaminCombo.setItemLabelGenerator(item -> {
            Object value = item.get("namaJenisKelamin");
            return value != null ? value.toString() : "N/A";
        });
        pendidikanCombo = new ComboBox<>("Pendidikan");
        pendidikanCombo.setItemLabelGenerator(item -> {
            Object value = item.get("namaPendidikan");
            return value != null ? value.toString() : "N/A";
        });
        jabatanCombo = new ComboBox<>("Jabatan");
        jabatanCombo.setItemLabelGenerator(item -> {
            Object value = item.get("namaJabatan");
            return value != null ? value.toString() : "N/A";
        });
        departemenCombo = new ComboBox<>("Departemen");
        departemenCombo.setItemLabelGenerator(item -> {
            Object value = item.get("namaDepartemen");
            return value != null ? value.toString() : "N/A";
        });
        unitKerjaCombo = new ComboBox<>("Unit Kerja");
        unitKerjaCombo.setItemLabelGenerator(item -> {
            Object value = item.get("namaUnitKerja");
            return value != null ? value.toString() : "N/A";
        });
        passwordField = new PasswordField("Password");
        passwordField.setRequired(true);
        passwordConfirmField = new PasswordField("Konfirmasi Password");
        passwordConfirmField.setRequired(true);
        form.add(
            namaLengkapField, emailField, tempatLahirField,
            tanggalLahirField, jenisKelaminCombo, pendidikanCombo,
            jabatanCombo, departemenCombo, unitKerjaCombo,
            passwordField, passwordConfirmField
        );
        HorizontalLayout formActions = new HorizontalLayout();
        formActions.setSpacing(true);
        formActions.getStyle().set("margin-top", "20px");
        Button saveButton = new Button("Simpan", new Icon(VaadinIcon.CHECK));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> savePegawai());
        Button cancelButton = new Button("Batal", new Icon(VaadinIcon.CLOSE));
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClickListener(e -> toggleForm());
        formActions.add(saveButton, cancelButton);
        formLayout.add(formTitle, form, formActions);
    }
    private Div createGridContainer() {
        Div gridContainer = new Div();
        gridContainer.getStyle()
                .set("background", "white")
                .set("border-radius", "12px")
                .set("box-shadow", "0 4px 20px rgba(0, 0, 0, 0.08)")
                .set("overflow", "hidden");
        H3 gridTitle = new H3("Daftar Pegawai");
        gridTitle.getStyle()
                .set("color", "#1f2937")
                .set("margin", "0")
                .set("padding", "25px 30px");
        gridContainer.add(gridTitle, pegawaiGrid);
        return gridContainer;
    }
    private void createGridLayout() {
        pegawaiGrid = new Grid<>();
        pegawaiGrid.setHeight("600px");
        pegawaiGrid.addColumn(item -> {
            Object value = item.get("namaLengkap");
            return value != null ? value.toString() : "N/A";
        }).setHeader("Nama Lengkap").setFlexGrow(2);
        pegawaiGrid.addColumn(item -> {
            Object value = item.get("email");
            return value != null ? value.toString() : "N/A";
        }).setHeader("Email").setFlexGrow(2);
        pegawaiGrid.addColumn(item -> {
            Object value = item.get("namaJabatan");
            return value != null ? value.toString() : "N/A";
        }).setHeader("Jabatan").setFlexGrow(1);
        pegawaiGrid.addColumn(item -> {
            Object value = item.get("namaDepartemen");
            return value != null ? value.toString() : "N/A";
        }).setHeader("Departemen").setFlexGrow(1);
        pegawaiGrid.addColumn(item -> {
            Object value = item.get("profile");
            return value != null ? value.toString() : "N/A";
        }).setHeader("Profile").setFlexGrow(1);
        pegawaiGrid.getStyle()
                .set("border", "none");
    }
    private void toggleForm() {
        isFormVisible = !isFormVisible;
        formLayout.setVisible(isFormVisible);
        if (isFormVisible) {
            tambahButton.setText("Tutup Form");
            tambahButton.setIcon(new Icon(VaadinIcon.CLOSE));
            clearForm();
        } else {
            tambahButton.setText("Tambah Pegawai");
            tambahButton.setIcon(new Icon(VaadinIcon.PLUS));
        }
    }
    private void clearForm() {
        namaLengkapField.clear();
        emailField.clear();
        tempatLahirField.clear();
        tanggalLahirField.clear();
        jenisKelaminCombo.clear();
        pendidikanCombo.clear();
        jabatanCombo.clear();
        departemenCombo.clear();
        unitKerjaCombo.clear();
        passwordField.clear();
        passwordConfirmField.clear();
    }
    private void loadComboData() {
        final UI ui = UI.getCurrent();
        client.get()
                .uri("/api/pegawai/combo/jenis-kelamin")
                .retrieve()
                .bodyToFlux(Map.class)
                .collectList()
                .subscribe(
                    items -> {
                        List<Map<String, Object>> list = (List<Map<String, Object>>)(List<?>)items;
                        if (ui != null) {
                            ui.access(() -> jenisKelaminCombo.setItems(list));
                        } else {
                            jenisKelaminCombo.setItems(list);
                        }
                    },
                    error -> logger.error("Error loading jenis kelamin", error)
                );
        client.get()
                .uri("/api/pegawai/combo/pendidikan")
                .retrieve()
                .bodyToFlux(Map.class)
                .collectList()
                .subscribe(
                    items -> {
                        List<Map<String, Object>> list = (List<Map<String, Object>>)(List<?>)items;
                        if (ui != null) {
                            ui.access(() -> pendidikanCombo.setItems(list));
                        } else {
                            pendidikanCombo.setItems(list);
                        }
                    },
                    error -> logger.error("Error loading pendidikan", error)
                );
        client.get()
                .uri("/api/pegawai/combo/jabatan")
                .retrieve()
                .bodyToFlux(Map.class)
                .collectList()
                .subscribe(
                    items -> {
                        List<Map<String, Object>> list = (List<Map<String, Object>>)(List<?>)items;
                        if (ui != null) {
                            ui.access(() -> jabatanCombo.setItems(list));
                        } else {
                            jabatanCombo.setItems(list);
                        }
                    },
                    error -> logger.error("Error loading jabatan", error)
                );
        client.get()
                .uri("/api/pegawai/combo/departemen")
                .retrieve()
                .bodyToFlux(Map.class)
                .collectList()
                .subscribe(
                    items -> {
                        List<Map<String, Object>> list = (List<Map<String, Object>>)(List<?>)items;
                        if (ui != null) {
                            ui.access(() -> departemenCombo.setItems(list));
                        } else {
                            departemenCombo.setItems(list);
                        }
                    },
                    error -> logger.error("Error loading departemen", error)
                );
        client.get()
                .uri("/api/pegawai/combo/unit-kerja")
                .retrieve()
                .bodyToFlux(Map.class)
                .collectList()
                .subscribe(
                    items -> {
                        List<Map<String, Object>> list = (List<Map<String, Object>>)(List<?>)items;
                        if (ui != null) {
                            ui.access(() -> unitKerjaCombo.setItems(list));
                        } else {
                            unitKerjaCombo.setItems(list);
                        }
                    },
                    error -> logger.error("Error loading unit kerja", error)
                );
    }
    private void loadPegawaiData() {
        final UI ui = UI.getCurrent();
        client.get()
                .uri("/api/pegawai/daftar")
                .retrieve()
                .bodyToFlux(Map.class)
                .collectList()
                .subscribe(
                    pegawaiList -> {
                        List<Map<String, Object>> list = (List<Map<String, Object>>)(List<?>)pegawaiList;
                        if (ui != null) {
                            ui.access(() -> {
                                pegawaiGrid.setItems(list);
                                showNotification("Data pegawai berhasil dimuat", NotificationVariant.LUMO_SUCCESS);
                            });
                        } else {
                            pegawaiGrid.setItems(list);
                            showNotification(ui, "Data pegawai berhasil dimuat", NotificationVariant.LUMO_SUCCESS);
                        }
                    },
                    error -> {
                        if (ui != null) {
                            ui.access(() -> {
                                logger.error("Error loading pegawai data", error);
                                showNotification("Gagal memuat data pegawai: " + error.getMessage(), NotificationVariant.LUMO_ERROR);
                            });
                        } else {
                            logger.error("Error loading pegawai data", error);
                        }
                    }
                );
    }
    private void savePegawai() {
        if (!validateForm()) {
            return;
        }
        try {
            final UI currentUI = UI.getCurrent();
            Map<String, Object> pegawaiData = new HashMap<>();
            pegawaiData.put("namaLengkap", namaLengkapField.getValue());
            pegawaiData.put("email", emailField.getValue());
            pegawaiData.put("tempatLahir", tempatLahirField.getValue());
            if (tanggalLahirField.getValue() != null) {
                long epoch = tanggalLahirField.getValue().atStartOfDay(ZoneOffset.UTC).toEpochSecond();
                pegawaiData.put("tanggalLahir", epoch);
            }
            if (jenisKelaminCombo.getValue() != null) {
                pegawaiData.put("kdJenisKelamin", jenisKelaminCombo.getValue().get("kdJenisKelamin"));
            }
            if (pendidikanCombo.getValue() != null) {
                pegawaiData.put("kdPendidikan", pendidikanCombo.getValue().get("kdPendidikan"));
            }
            if (jabatanCombo.getValue() != null) {
                pegawaiData.put("kdJabatan", jabatanCombo.getValue().get("kdJabatan"));
            }
            if (departemenCombo.getValue() != null) {
                pegawaiData.put("kdDepartemen", departemenCombo.getValue().get("kdDepartemen"));
            }
            if (unitKerjaCombo.getValue() != null) {
                pegawaiData.put("kdUnitKerja", unitKerjaCombo.getValue().get("kdUnitKerja"));
            }
            pegawaiData.put("password", passwordField.getValue());
            pegawaiData.put("passwordC", passwordConfirmField.getValue());
            client.post()
                    .uri("/api/pegawai/admin-tambah-pegawai")
                    .bodyValue(pegawaiData)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .subscribe(
                            response -> {
                                if (currentUI != null) {
                                    currentUI.access(() -> {
                                        showNotification("Pegawai berhasil ditambahkan", NotificationVariant.LUMO_SUCCESS);
                                        toggleForm();
                                        loadPegawaiData();
                                    });
                                } else {
                                    showNotification(currentUI, "Pegawai berhasil ditambahkan", NotificationVariant.LUMO_SUCCESS);
                                    logger.info("Pegawai berhasil ditambahkan (no UI refresh)");
                                }
                            },
                            error -> {
                                if (currentUI != null) {
                                    currentUI.access(() -> {
                                        logger.error("Error saving pegawai", error);
                                        showNotification("Gagal menambahkan pegawai: " + error.getMessage(), NotificationVariant.LUMO_ERROR);
                                    });
                                } else {
                                    logger.error("Error saving pegawai", error);
                                    showNotification(currentUI, "Gagal menambahkan pegawai: " + error.getMessage(), NotificationVariant.LUMO_ERROR);
                                }
                            }
                    );
        } catch (Exception e) {
            logger.error("Error in savePegawai", e);
            showNotification("Terjadi kesalahan: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }
    private boolean validateForm() {
        if (namaLengkapField.getValue() == null || namaLengkapField.getValue().trim().isEmpty()) {
            showNotification("Nama lengkap harus diisi", NotificationVariant.LUMO_ERROR);
            namaLengkapField.focus();
            return false;
        }
        if (emailField.getValue() == null || emailField.getValue().trim().isEmpty()) {
            showNotification("Email harus diisi", NotificationVariant.LUMO_ERROR);
            emailField.focus();
            return false;
        }
        if (passwordField.getValue() == null || passwordField.getValue().trim().isEmpty()) {
            showNotification("Password harus diisi", NotificationVariant.LUMO_ERROR);
            passwordField.focus();
            return false;
        }
        if (!passwordField.getValue().equals(passwordConfirmField.getValue())) {
            showNotification("Konfirmasi password tidak sama", NotificationVariant.LUMO_ERROR);
            passwordConfirmField.focus();
            return false;
        }
        if (jenisKelaminCombo.getValue() == null) {
            showNotification("Jenis kelamin harus dipilih", NotificationVariant.LUMO_ERROR);
            jenisKelaminCombo.focus();
            return false;
        }
        if (jabatanCombo.getValue() == null) {
            showNotification("Jabatan harus dipilih", NotificationVariant.LUMO_ERROR);
            jabatanCombo.focus();
            return false;
        }
        if (departemenCombo.getValue() == null) {
            showNotification("Departemen harus dipilih", NotificationVariant.LUMO_ERROR);
            departemenCombo.focus();
            return false;
        }
        if (unitKerjaCombo.getValue() == null) {
            showNotification("Unit kerja harus dipilih", NotificationVariant.LUMO_ERROR);
            unitKerjaCombo.focus();
            return false;
        }
        if (pendidikanCombo.getValue() == null) {
            showNotification("Pendidikan harus dipilih", NotificationVariant.LUMO_ERROR);
            pendidikanCombo.focus();
            return false;
        }
        return true;
    }
    private void showNotification(String message, NotificationVariant variant) {
        try {
            UI currentUI = UI.getCurrent();
            if (currentUI != null) {
                Notification notification = Notification.show(message, 4000, Notification.Position.TOP_CENTER);
                notification.addThemeVariants(variant);
                return;
            }
        } catch (IllegalStateException e) {
        }
        showNotification(null, message, variant);
    }
    private void showNotification(UI ui, String message, NotificationVariant variant) {
        if (ui != null) {
            ui.access(() -> {
                try {
                    Notification notification = Notification.show(message, 4000, Notification.Position.TOP_CENTER);
                    notification.addThemeVariants(variant);
                } catch (Exception e) {
                    logger.warn("Error showing notification in UI.access: {}", e.getMessage());
                }
            });
        } else {
            try {
                UI currentUI = UI.getCurrent();
                if (currentUI != null) {
                    Notification notification = Notification.show(message, 4000, Notification.Position.TOP_CENTER);
                    notification.addThemeVariants(variant);
                } else {
                    logger.info("Notification would be shown: {}", message);
                }
            } catch (Exception e) {
                logger.info("Notification would be shown: {}", message);
            }
        }
    }
}