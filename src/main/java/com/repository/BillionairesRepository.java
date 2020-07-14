package com.repository;

import com.model.Billionaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillionairesRepository extends JpaRepository<Billionaire, Integer> {
    List<Billionaire> findByFirstName(String firstName);
}
