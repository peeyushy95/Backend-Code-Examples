package com.web;

import com.model.Billionaire;
import com.repository.BillionairesRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class apiController {

    @Autowired
    BillionairesRepository billionairesRepository;

//    Logger logger = LoggerFactory.getLogger(apiController.class);

    @GetMapping("/health")
    public ResponseEntity<String> health(){

        log.info("Health");

        List<Billionaire> b = billionairesRepository.findAll();
        b.forEach(x->{
            System.out.println(x);
        });
        return new ResponseEntity<>("Its working", HttpStatus.OK);
    }

}
