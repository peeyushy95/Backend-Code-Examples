package com.repository;

import com.model.Billionaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BillionairesRepository extends JpaRepository<Billionaire, Integer> {
    List<Billionaire> findByFirstName(final String firstName);

    @Query("SELECT * FROM User u WHERE u.status = :status and u.name = :name")
    List<Billionaire> findByFirstNameAndLastNameNamedParam(@Param("fName") final String firstName,
                                                           @Param("lName") final String LastName);
}
