package com.nanmax.hris.ui;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.stream.Collectors;
@PageTitle("Daftar Pegawai | HRIS PT Jasamedika")
@Route(value = "pegawai-list", layout = MainLayout.class)
public class PegawaiListView extends VerticalLayout {
    private Grid<Pegawai> grid = new Grid<>(Pegawai.class, false);
    private TextField searchField = new TextField();
    private Button addButton = new Button("Tambah Pegawai", VaadinIcon.PLUS.create());
    private List<Pegawai> allPegawai;
    private WebClient client = WebClient.create("http://localhost:8080");
    public PegawaiListView() {
        setSizeFull();
        addClassName("pegawai-list-view");
        configureGrid();
        configureForm();
        add(getToolbar(), grid);
        updateList();
    }
    private void configureGrid() {
        grid.addClassNames("pegawai-grid");
        grid.setSizeFull();
        grid.addColumn(Pegawai::getNamaLengkap).setHeader("Nama").setSortable(true);
        grid.addColumn(Pegawai::getEmail).setHeader("Email").setSortable(true);
        grid.addColumn(Pegawai::getNamaJabatan).setHeader("Jabatan").setSortable(true);
        grid.addColumn(Pegawai::getNamaDepartemen).setHeader("Departemen").setSortable(true);
        grid.addColumn(Pegawai::getProfile).setHeader("Role").setSortable(true);
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }
    private void configureForm() {
        searchField.setPlaceholder("Cari pegawai...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> updateList());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("pegawai/add")));
    }
    private HorizontalLayout getToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout(searchField, addButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }
    private void updateList() {
        String authToken = (String) getUI().get().getSession().getAttribute("authToken");
        if (authToken == null) {
            getUI().ifPresent(ui -> ui.navigate("login"));
            return;
        }
        client.get()
            .uri("/api/pegawai/daftar")
            .header("Authorization", "Bearer " + authToken)
            .retrieve()
            .bodyToFlux(Pegawai.class)
            .collectList()
            .subscribe(
                list -> {
                    this.allPegawai = list;
                    filterPegawai();
                },
                err -> {
                    Notification notification = Notification.show("Gagal memuat data pegawai: " + err.getMessage());
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            );
    }
    private void filterPegawai() {
        if (allPegawai == null) return;
        String searchTerm = searchField.getValue();
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            grid.setItems(allPegawai);
        } else {
            List<Pegawai> filtered = allPegawai.stream()
                .filter(pegawai -> 
                    pegawai.getNamaLengkap().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    pegawai.getEmail().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    (pegawai.getNamaJabatan() != null && pegawai.getNamaJabatan().toLowerCase().contains(searchTerm.toLowerCase())) ||
                    (pegawai.getNamaDepartemen() != null && pegawai.getNamaDepartemen().toLowerCase().contains(searchTerm.toLowerCase()))
                )
                .collect(Collectors.toList());
            grid.setItems(filtered);
        }
    }
    public static class Pegawai {
        private String profile, idUser, namaLengkap, tempatLahir, email, password, nikUser,
                namaJabatan, namaDepartemen, namaUnitKerja, namaJenisKelamin, namaPendidikan, photo;
        private Long tanggalLahir;
        private Integer kdJabatan, kdDepartemen, kdUnitKerja, kdJenisKelamin, kdPendidikan;
        public String getProfile() { return profile; }
        public void setProfile(String profile) { this.profile = profile; }
        public String getIdUser() { return idUser; }
        public void setIdUser(String idUser) { this.idUser = idUser; }
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
        public String getNamaJabatan() { return namaJabatan; }
        public void setNamaJabatan(String namaJabatan) { this.namaJabatan = namaJabatan; }
        public String getNamaDepartemen() { return namaDepartemen; }
        public void setNamaDepartemen(String namaDepartemen) { this.namaDepartemen = namaDepartemen; }
        public String getNamaUnitKerja() { return namaUnitKerja; }
        public void setNamaUnitKerja(String namaUnitKerja) { this.namaUnitKerja = namaUnitKerja; }
        public String getNamaJenisKelamin() { return namaJenisKelamin; }
        public void setNamaJenisKelamin(String namaJenisKelamin) { this.namaJenisKelamin = namaJenisKelamin; }
        public String getNamaPendidikan() { return namaPendidikan; }
        public void setNamaPendidikan(String namaPendidikan) { this.namaPendidikan = namaPendidikan; }
        public String getPhoto() { return photo; }
        public void setPhoto(String photo) { this.photo = photo; }
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