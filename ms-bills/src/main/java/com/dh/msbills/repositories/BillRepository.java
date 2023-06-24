package com.dh.msbills.repositories;

import com.dh.msbills.models.Bill;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BillRepository extends JpaRepository<Bill, String> {
}
