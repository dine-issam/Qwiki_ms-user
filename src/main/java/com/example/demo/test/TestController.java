package com.example.demo.test;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("api/v1/demo-controller")
public class TestController {

    @GetMapping
    public ResponseEntity<String> sayHello() {
      return ResponseEntity.ok("Hello from secured endpoint");
    }

}
