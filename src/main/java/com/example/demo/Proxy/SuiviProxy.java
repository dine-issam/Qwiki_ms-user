package com.example.demo.Proxy;


import com.example.demo.Entity.Commercant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.CollectionModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ms-suivi")
public interface SuiviProxy {
    @PostMapping("commercents")
    void addCommercents(@RequestBody Commercant commercant);
}
