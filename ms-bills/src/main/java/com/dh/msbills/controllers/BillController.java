package com.dh.msbills.controllers;

import com.dh.msbills.models.Bill;
import com.dh.msbills.services.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService service;

    // URL: http://localhost:8090/api/v1/bills/all
    // Con user "user" (password:password), el endpoint devuelve informacion
    // Usando "user2" (password:password), el endpoint devuelve 403 por no tener el rol USER
    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<Bill>> getAll() {
        return ResponseEntity.ok().body(service.getAllBill());
    }

    @PostMapping()
    @PreAuthorize("hasAnyAuthority('/PROVIDERS')")
    public ResponseEntity<Bill> save(@RequestBody  Bill bill){
        return ResponseEntity.ok().body(service.save(bill));
    }

    // URL: http://localhost:8090/api/v1/bills/findBy/Aaron
    @GetMapping("/findBy/{customerBill}")
    public ResponseEntity<List<Bill>> getAll(@PathVariable String customerBill) {
        return ResponseEntity.ok().body(service.findByCustomerId(customerBill));
    }

    // URL: http://localhost:8090/api/v1/bills/hello
    @GetMapping("/hello")
    public ResponseEntity getHello() {
        return ResponseEntity.ok("Hello Bills");
    }

}
