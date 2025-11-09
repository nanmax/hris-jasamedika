package com.nanmax.hris.security;
import lombok.Data;
@Data
public class AuthenticationContext {
    private String idUser;
    private String profile;
    private String namaLengkap;
    private String email;
    private Integer kdDepartemen;
    private String namaDepartemen;
    public boolean isAdmin() {
        return "ADMIN".equals(profile);
    }
    public boolean isHRD() {
        return isAdmin() || "HRD".equalsIgnoreCase(namaDepartemen);
    }
}