package com.bruceycode.Medical_Service.repository;

import com.bruceycode.Medical_Service.model.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
