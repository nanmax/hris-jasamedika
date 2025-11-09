package com.nanmax.hris.ui;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;
@Route("combo")
public class ComboView extends VerticalLayout {
    private WebClient client = WebClient.create("http://localhost:8080");
    public ComboView() {
        setSizeFull();
        add(new H2("Combo Data"));
        ListBox<String> jabatanCombo = new ListBox<>();
        client.get().uri("/api/pegawai/combo/jabatan")
                .retrieve().bodyToFlux(Jabatan.class)
                .map(j -> j.namaJabatan)
                .collectList()
                .subscribe(jabatanCombo::setItems,
                        err -> Notification.show("Gagal ambil jabatan: " + err.getMessage()));
        add(jabatanCombo);
    }
    static class Jabatan { public Integer kdJabatan; public String namaJabatan; }
}