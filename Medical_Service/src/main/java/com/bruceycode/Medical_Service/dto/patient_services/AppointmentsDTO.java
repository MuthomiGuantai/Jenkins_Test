package com.bruceycode.Medical_Service.dto.patient_services;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;


@Setter
@Getter
public class AppointmentsDTO {
    private Long Id;
    private Long doctorId;
    private Long patientId;
    private Long nurseId;
    private LocalDate appointmentDate;
    private String reason;

    public AppointmentsDTO() {
    }

    public AppointmentsDTO(Long id, Long doctorId, Long patientId, Long nurseId, LocalDate appointmentDate, String reason) {
        Id = id;
        this.doctorId = doctorId;
        this.nurseId = nurseId;
        this.patientId = patientId;
        this.appointmentDate = appointmentDate;
        this.reason = reason;
    }
}
