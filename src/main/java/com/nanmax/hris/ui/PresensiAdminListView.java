package com.nanmax.hris.ui;

import com.nanmax.hris.config.ApiConfig;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import org.springframework.web.reactive.function.client.WebClient;
@Route("presensi-admin")
public class PresensiAdminListView extends VerticalLayout {
    private TextField tglAwal = new TextField("Tgl Awal (Epoch)");
    private TextField tglAkhir = new TextField("Tgl Akhir (Epoch)");
    private Button btnLoad = new Button("Load Presensi");
    private Grid<PresensiAdmin> grid = new Grid<>(PresensiAdmin.class);
    private WebClient client = WebClient.create(ApiConfig.getBaseUrl());
    public PresensiAdminListView() {
        setSizeFull();
        add(new H2("Presensi - Semua Pegawai"), tglAwal, tglAkhir, btnLoad, grid);
        btnLoad.addClickListener(e -> {
            String url = "/presensi/daftar/admin?tglAwal=" + tglAwal.getValue() + "&tglAkhir=" + tglAkhir.getValue();
            client.get().uri(url)
                    .retrieve().bodyToFlux(PresensiAdmin.class)
                    .collectList().subscribe(
                            list -> grid.setItems(list),
                            err -> Notification.show("Gagal ambil presensi: " + err.getMessage())
                    );
        });
    }
    static class PresensiAdmin {
        public Long idUser;
        public String namaLengkap;
        public Long tglAbsensi;
        public String jamMasuk, jamKeluar, namaStatus;
    }
}