package com.dh.msusers.repository;

import com.dh.msusers.model.Bill;

import java.util.List;

public interface IBillsRepository {

    List<Bill> findByCustomerId(String customerId);

}
