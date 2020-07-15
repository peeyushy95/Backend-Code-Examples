package com.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.model.Billionaire;
import com.web.filter.BillionaireResponseFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.web.manager.ApiManager;
import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class Controller {

    @Autowired
    private ApiManager apiManager;

    @GetMapping("/billionaires")
    public ResponseEntity<List<Billionaire>> getAllBillionaires(){
        return new ResponseEntity<>(apiManager.getAllBillionaires(), HttpStatus.OK);
    }

    // /api//billionaires/{name}?fields=id,career
    @GetMapping("/billionaires/{name}")
    public ResponseEntity<String> getAllBillionairesByFirstName(@PathVariable final String name,
              @DefaultValue("firstName,id,lastName,career") @RequestParam("fields") final String fields
                                                                           ) throws JsonProcessingException {
        List<Billionaire> billionaires = apiManager.getAllBillionairesByFirstName(name);
        final String response = BillionaireResponseFilter.filterFields(billionaires, fields);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
