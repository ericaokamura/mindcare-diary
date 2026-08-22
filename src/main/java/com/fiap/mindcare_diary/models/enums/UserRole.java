package com.fiap.mindcare_diary.models.enums;

import java.util.Arrays;
import java.util.List;

public enum UserRole {

    PACIENTE(0, Arrays.asList(Authority.PATIENT_READ_PROFILE, Authority.PATIENT_DOWNLOAD_PRESCRIPTION, Authority.PATIENT_UPDATE_PROFILE, Authority.PATIENT_SEARCH_PROFESSIONAL)),
    PROFISSIONAL(1, Arrays.asList(Authority.PROFESSIONAL_UPDATE_PROFILE, Authority.PROFESSIONAL_READ_PATIENT, Authority.PROFESSIONAL_UPLOAD_PRESCRIPTION, Authority.PROFESSIONAL_READ_PROFILE)),
    CLINICA_ADMIN(2, Arrays.asList(Authority.USER_CREATE, Authority.USER_READ, Authority.USER_UPDATE, Authority.USER_DELETE));

    private Integer codigo;
    private List<Authority> authorities;

    UserRole(Integer codigo, List<Authority> authorities) {
        this.codigo = codigo;
        this.authorities = authorities;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public List<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }
}
