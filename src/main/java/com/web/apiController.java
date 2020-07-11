package com.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class apiController {

    @GetMapping("/health")
    public ResponseEntity<String> health(){
        return new ResponseEntity<>("Its working", HttpStatus.OK);
    }

}
