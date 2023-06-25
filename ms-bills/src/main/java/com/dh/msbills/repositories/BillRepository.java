package com.dh.msbills.repositories;

import com.dh.msbills.models.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface BillRepository extends JpaRepository<Bill, String> {

    List<Bill> findAllByCustomerBill(String customerId);

}
