package com.womakerscode.microservicemeetup.service;

import com.womakerscode.microservicemeetup.model.entity.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface RegistrationService {
    Registration save(Registration any);

    Optional<Registration> getRegistrationById(Integer id);

    void delete(Registration registration);

    Registration update(Registration updatingregistration);

    Page<Registration> find(Registration filter, Pageable pageRequest);

    Optional<Registration> getRegistrationByRegistrationAttrb(String registrationAttribute);
}
