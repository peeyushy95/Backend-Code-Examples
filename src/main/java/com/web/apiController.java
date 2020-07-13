package com.web;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Slf4j
public class apiController {

//    Logger logger = LoggerFactory.getLogger(apiController.class);

    @GetMapping("/health")
    public ResponseEntity<String> health(){

        log.info("Health");
        return new ResponseEntity<>("Its working", HttpStatus.OK);
    }

}
