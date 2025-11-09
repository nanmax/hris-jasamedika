package com.nanmax.hris.ui;

import com.nanmax.hris.config.ApiConfig;

import com.nanmax.hris.config.ApiConfig;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.Route;
import org.springframework.web.reactive.function.client.WebClient;
@Route("pegawai-add")
public class AdminPegawaiAddView extends VerticalLayout {
    TextField namaLengkap = new TextField("Nama Lengkap");
    TextField email = new TextField("Email");
    TextField tempatLahir = new TextField("Tempat Lahir");
    TextField tanggalLahir = new TextField("Tanggal Lahir (Epoch detik)");
    TextField kdJenisKelamin = new TextField("Kode Jenis Kelamin");
    TextField kdPendidikan = new TextField("Kode Pendidikan");
    TextField kdJabatan = new TextField("Kode Jabatan");
    TextField kdDepartemen = new TextField("Kode Departemen");
    TextField kdUnitKerja = new TextField("Kode Unit Kerja");
    PasswordField password = new PasswordField("Password");
    PasswordField passwordC = new PasswordField("Konfirmasi Password");
    Button btnAdd = new Button("Tambah Pegawai");
    WebClient client = WebClient.create(ApiConfig.getBaseUrl());
    public AdminPegawaiAddView() {
        setSizeFull();
        add(new H2("Tambah Pegawai (Admin/HRD)"));
        FormLayout form = new FormLayout();
        form.add(namaLengkap, email, tempatLahir, tanggalLahir,
                kdJenisKelamin, kdPendidikan, kdJabatan, kdDepartemen, kdUnitKerja,
                password, passwordC, btnAdd);
        add(form);
        btnAdd.addClickListener(e -> {
            var req = new AddPegReq(
                    namaLengkap.getValue(), email.getValue(), tempatLahir.getValue(),
                    parseLong(tanggalLahir.getValue()), parseInt(kdJenisKelamin.getValue()),
                    parseInt(kdPendidikan.getValue()), parseInt(kdJabatan.getValue()),
                    parseInt(kdDepartemen.getValue()), parseInt(kdUnitKerja.getValue()),
                    password.getValue(), passwordC.getValue()
            );
            client.post().uri("/pegawai/admin-tambah-pegawai")
                    .bodyValue(req)
                    .retrieve().bodyToMono(String.class)
                    .subscribe(
                            result -> Notification.show("Berhasil tambah pegawai."),
                            err -> Notification.show("Gagal tambah: " + err.getMessage())
                    );
        });
    }
    static class AddPegReq {
        public String namaLengkap, email, tempatLahir, password, passwordC;
        public Long tanggalLahir;
        public Integer kdJenisKelamin, kdPendidikan, kdJabatan, kdDepartemen, kdUnitKerja;
        public AddPegReq(String namaLengkap, String email, String tempatLahir, Long tanggalLahir,
                         Integer kdJenisKelamin, Integer kdPendidikan, Integer kdJabatan,
                         Integer kdDepartemen, Integer kdUnitKerja, String password, String passwordC) {
            this.namaLengkap = namaLengkap; this.email = email; this.tempatLahir = tempatLahir; this.tanggalLahir = tanggalLahir;
            this.kdJenisKelamin = kdJenisKelamin; this.kdPendidikan = kdPendidikan; this.kdJabatan = kdJabatan;
            this.kdDepartemen = kdDepartemen; this.kdUnitKerja = kdUnitKerja; this.password = password; this.passwordC = passwordC;
        }
    }
    private Integer parseInt(String v) { try { return Integer.parseInt(v); } catch(Exception e) { return null; } }
    private Long parseLong(String v) { try { return Long.parseLong(v); } catch(Exception e) { return null; } }
}