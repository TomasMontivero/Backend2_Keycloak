package com.dh.msusers.repository.feign;

import com.dh.msusers.configuration.OAuthFeignConfig;
import com.dh.msusers.model.Bill;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ms-bill", /*url = "http://localhost:8090/" ,*/ configuration = OAuthFeignConfig.class)
public interface IBillsFeignClient {

    @GetMapping("/api/v1/bills/findBy/{customerId}")
    ResponseEntity<List<Bill>> getBillsByCustomerId(@PathVariable String customerId);

    @GetMapping("/api/v1/bills/hello")
    ResponseEntity getHelloBills();

}
