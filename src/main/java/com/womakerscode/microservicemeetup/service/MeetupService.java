package com.womakerscode.microservicemeetup.service;

import com.womakerscode.microservicemeetup.controller.dto.MeetupFilterDTO;
import com.womakerscode.microservicemeetup.model.entity.Meetup;
import com.womakerscode.microservicemeetup.model.entity.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MeetupService {

    Meetup save(Meetup meetup);

    Optional<Meetup> getById(Integer id);

    Meetup update(Meetup loan);

    Page<Meetup> find(MeetupFilterDTO filterDTO, Pageable pageable);

    Page<Meetup> getRegistrationsByMeetup(Registration registration, Pageable pageable);

}
