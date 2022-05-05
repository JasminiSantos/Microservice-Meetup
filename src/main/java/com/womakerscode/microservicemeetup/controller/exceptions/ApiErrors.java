package com.womakerscode.microservicemeetup.controller.exceptions;

import com.womakerscode.microservicemeetup.exception.BusinessException;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApiErrors {
    private final List<String> errors;

    public ApiErrors(BindingResult bindingResult){
        this.errors = new ArrayList<>();
        bindingResult.getAllErrors()
                .forEach(objectError -> this.errors.add(objectError.getDefaultMessage()));
    }

    public ApiErrors(BusinessException e){
        this.errors = Arrays.asList(e.getMessage());
    }

    public ApiErrors(ResponseStatusException e){
        this.errors = Arrays.asList(e.getMessage());
    }

    public List<String> getErrors(){
        return errors;
    }
}
