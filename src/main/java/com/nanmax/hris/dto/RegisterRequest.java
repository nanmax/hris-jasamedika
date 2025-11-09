package com.nanmax.hris.dto;
public class RegisterRequest {
    private String namaLengkap;
    private String email;
    private String password;
    private String profile;
    private String nikUser;
    private String tempatLahir;
    private Long tanggalLahir;
    public RegisterRequest() {}
    public String getNamaLengkap() {
        return namaLengkap;
    }
    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getProfile() {
        return profile;
    }
    public void setProfile(String profile) {
        this.profile = profile;
    }
    public String getNikUser() {
        return nikUser;
    }
    public void setNikUser(String nikUser) {
        this.nikUser = nikUser;
    }
    public String getTempatLahir() {
        return tempatLahir;
    }
    public void setTempatLahir(String tempatLahir) {
        this.tempatLahir = tempatLahir;
    }
    public Long getTanggalLahir() {
        return tanggalLahir;
    }
    public void setTanggalLahir(Long tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }
}