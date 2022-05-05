package com.womakerscode.microservicemeetup.repository;

import com.womakerscode.microservicemeetup.model.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Integer> {

    boolean existsByRegistration(String registration);

    Optional<Registration> findByRegistration(String registrationAttribute);
}
