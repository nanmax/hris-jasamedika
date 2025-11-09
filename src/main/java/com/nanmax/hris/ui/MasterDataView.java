package com.nanmax.hris.ui;
import com.nanmax.hris.entity.MasterData;
import com.nanmax.hris.ui.components.ResponsiveHeader;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;
@Route(value = "master-data")
@PageTitle("Master Data | HRIS Jasamedika")
public class MasterDataView extends VerticalLayout {
    private final ResponsiveHeader responsiveHeader;
    private final WebClient webClient;
    private final UI currentUI;
    private Grid<MasterData> grid;
    private String currentMasterType = "jabatan";
    private final String[] masterTypes = {"jabatan", "departemen", "unitkerja", "jeniskelamin", "pendidikan"};
    private final String[] masterLabels = {"Jabatan", "Departemen", "Unit Kerja", "Jenis Kelamin", "Pendidikan"};
    public MasterDataView() {
        this.responsiveHeader = new ResponsiveHeader(UI.getCurrent())
                .withPageTitle("Master Data")
                .withBackButton(true, "dashboard");
        this.currentUI = UI.getCurrent();
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8080")
                .build();
        setupLayout();
        createHeaders();
        add(createMasterTypeSelector(), createContent());
        loadMasterData();
    }
    private void setupLayout() {
        ResponsiveHeader.applyMobileLayout(this);
        getStyle().set("background", "#f8f9fa");
    }
    private void createHeaders() {
        add(responsiveHeader.createMobileHeader());
        add(responsiveHeader.createDesktopHeader());
    }
    private HorizontalLayout createMasterTypeSelector() {
        HorizontalLayout selector = new HorizontalLayout();
        selector.setWidthFull();
        selector.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);
        selector.getStyle()
                .set("background", "white")
                .set("padding", "15px 40px")
                .set("margin-bottom", "20px")
                .set("box-shadow", "0 1px 5px rgba(0,0,0,0.1)")
                .set("width", "100%");
        HorizontalLayout typeButtons = new HorizontalLayout();
        for (int i = 0; i < masterTypes.length; i++) {
            String type = masterTypes[i];
            String label = masterLabels[i];
            Button typeButton = new Button(label);
            if (type.equals(currentMasterType)) {
                typeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            } else {
                typeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            }
            typeButton.addClickListener(e -> switchMasterType(type));
            typeButtons.add(typeButton);
        }
        Button addButton = new Button("Tambah Data", VaadinIcon.PLUS.create());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> openAddDialog());
        selector.add(typeButtons, addButton);
        return selector;
    }
    private void switchMasterType(String type) {
        currentMasterType = type;
        refreshView();
        loadMasterData();
    }
    private void refreshView() {
        removeAll();
        createHeaders();
        add(createMasterTypeSelector(), createContent());
    }
    private VerticalLayout createContent() {
        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setMargin(false);
        content.getStyle()
                .set("background", "white")
                .set("margin", "0 20px")
                .set("padding", "30px")
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 10px rgba(0,0,0,0.1)")
                .set("width", "calc(100% - 40px)");
        H3 subtitle = new H3("Data " + getCurrentMasterLabel());
        subtitle.getStyle().set("margin-top", "0");
        grid = new Grid<>(MasterData.class, false);
        grid.addColumn(MasterData::getId).setHeader("ID").setWidth("100px");
        grid.addColumn(MasterData::getNama).setHeader("Nama").setFlexGrow(1);
        grid.addComponentColumn(this::createActionButtons)
                .setHeader("Aksi")
                .setWidth("150px");
        grid.getStyle()
                .set("border", "1px solid #e1e5e9")
                .set("border-radius", "8px");
        content.add(subtitle, grid);
        return content;
    }
    private String getCurrentMasterLabel() {
        for (int i = 0; i < masterTypes.length; i++) {
            if (masterTypes[i].equals(currentMasterType)) {
                return masterLabels[i];
            }
        }
        return "Data";
    }
    private HorizontalLayout createActionButtons(MasterData masterData) {
        Button editButton = new Button(VaadinIcon.EDIT.create());
        editButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        editButton.addClickListener(e -> openEditDialog(masterData));
        Button deleteButton = new Button(VaadinIcon.TRASH.create());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        deleteButton.addClickListener(e -> deleteMasterData(masterData));
        HorizontalLayout actions = new HorizontalLayout(editButton, deleteButton);
        actions.setSpacing(false);
        return actions;
    }
    private void openAddDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Tambah " + getCurrentMasterLabel());
        FormLayout formLayout = new FormLayout();
        TextField namaField = new TextField("Nama");
        namaField.setRequired(true);
        namaField.setWidthFull();
        formLayout.add(namaField);
        Button saveButton = new Button("Simpan", e -> {
            if (namaField.isEmpty()) {
                Notification.show("Nama harus diisi", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            saveMasterData(namaField.getValue(), dialog);
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Batal", e -> dialog.close());
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setJustifyContentMode(HorizontalLayout.JustifyContentMode.END);
        VerticalLayout dialogLayout = new VerticalLayout(formLayout, buttonLayout);
        dialog.add(dialogLayout);
        dialog.open();
    }
    private void openEditDialog(MasterData masterData) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Edit " + getCurrentMasterLabel());
        FormLayout formLayout = new FormLayout();
        TextField namaField = new TextField("Nama");
        namaField.setValue(masterData.getNama());
        namaField.setRequired(true);
        namaField.setWidthFull();
        formLayout.add(namaField);
        Button saveButton = new Button("Simpan", e -> {
            if (namaField.isEmpty()) {
                Notification.show("Nama harus diisi", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            updateMasterData(masterData.getId(), namaField.getValue(), dialog);
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Batal", e -> dialog.close());
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setJustifyContentMode(HorizontalLayout.JustifyContentMode.END);
        VerticalLayout dialogLayout = new VerticalLayout(formLayout, buttonLayout);
        dialog.add(dialogLayout);
        dialog.open();
    }
    private void loadMasterData() {
        webClient.get()
                .uri("/api/combo/" + currentMasterType)
                .retrieve()
                .bodyToFlux(MasterData.class)
                .collectList()
                .subscribe(
                        this::updateGrid,
                        error -> {
                            currentUI.access(() -> {
                                Notification.show("Gagal memuat data: " + error.getMessage(), 
                                        3000, Notification.Position.MIDDLE)
                                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                            });
                        }
                );
    }
    private void updateGrid(List<MasterData> data) {
        currentUI.access(() -> {
            if (grid != null) {
                grid.setItems(data);
            }
        });
    }
    private void saveMasterData(String nama, Dialog dialog) {
        Map<String, Object> requestBody = Map.of("nama", nama);
        webClient.post()
                .uri("/api/auth/master/" + currentMasterType)
                .body(Mono.just(requestBody), Map.class)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                        response -> {
                            currentUI.access(() -> {
                                Notification.show("Data berhasil disimpan", 3000, Notification.Position.MIDDLE)
                                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                dialog.close();
                                loadMasterData();
                            });
                        },
                        error -> {
                            currentUI.access(() -> {
                                Notification.show("Gagal menyimpan data: " + error.getMessage(), 
                                        3000, Notification.Position.MIDDLE)
                                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                            });
                        }
                );
    }
    private void updateMasterData(Integer id, String nama, Dialog dialog) {
        Map<String, Object> requestBody = Map.of("id", id, "nama", nama);
        webClient.put()
                .uri("/api/auth/master/" + currentMasterType + "/" + id)
                .body(Mono.just(requestBody), Map.class)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                        response -> {
                            currentUI.access(() -> {
                                Notification.show("Data berhasil diupdate", 3000, Notification.Position.MIDDLE)
                                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                dialog.close();
                                loadMasterData();
                            });
                        },
                        error -> {
                            currentUI.access(() -> {
                                Notification.show("Gagal mengupdate data: " + error.getMessage(), 
                                        3000, Notification.Position.MIDDLE)
                                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                            });
                        }
                );
    }
    private void deleteMasterData(MasterData masterData) {
        webClient.delete()
                .uri("/api/auth/master/" + currentMasterType + "/" + masterData.getId())
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                        response -> {
                            currentUI.access(() -> {
                                Notification.show("Data berhasil dihapus", 3000, Notification.Position.MIDDLE)
                                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                loadMasterData();
                            });
                        },
                        error -> {
                            currentUI.access(() -> {
                                Notification.show("Gagal menghapus data: " + error.getMessage(), 
                                        3000, Notification.Position.MIDDLE)
                                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                            });
                        }
                );
    }
}