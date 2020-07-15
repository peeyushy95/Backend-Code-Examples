package com.web.manager;

import com.exception.UserNotFoundException;
import com.google.common.base.Preconditions;
import com.model.Billionaire;
import com.repository.BillionairesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApiManager {

    @Autowired
    private BillionairesRepository billionairesRepository;

    public List<Billionaire> getAllBillionaires(){
        List<Billionaire> billionaires = billionairesRepository.findAll();
        return billionaires;
    }

    public List<Billionaire> getAllBillionairesByFirstName(String name){
        List<Billionaire> billionaires = billionairesRepository.findByFirstName(name);

        if(billionaires.size() == 0){
            throw  new UserNotFoundException("No Billionaire Found!!");
        }

        return billionaires;
    }
}
