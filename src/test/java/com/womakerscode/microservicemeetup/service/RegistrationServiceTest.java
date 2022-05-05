package com.womakerscode.microservicemeetup.service;

import com.womakerscode.microservicemeetup.exception.BusinessException;
import com.womakerscode.microservicemeetup.model.entity.Registration;
import com.womakerscode.microservicemeetup.repository.RegistrationRepository;
import com.womakerscode.microservicemeetup.service.impl.RegistrationServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class RegistrationServiceTest {
    RegistrationService registrationService;

    @MockBean
    RegistrationRepository repository;

    @BeforeEach
    public void setUp(){
        this.registrationService = new RegistrationServiceImpl(repository);
    }

    @Test
    @DisplayName("Should save a registration")
    public void saveStudent(){
        Registration registration = createValidRegistration();

        Mockito.when(repository.existsByRegistration(Mockito.anyString())).thenReturn(false);
        Mockito.when(repository.save(registration)).thenReturn(createValidRegistration());

        Registration savedRegistration = registrationService.save(registration);
        assertThat(savedRegistration.getId()).isEqualTo(101);
        assertThat(savedRegistration.getName()).isEqualTo("Jasmini Santos");
        assertThat(savedRegistration.getDateOfRegistration()).isEqualTo(LocalDate.now().toString());
        assertThat(savedRegistration.getRegistration()).isEqualTo("001");
    }

    @Test
    @DisplayName("Should throw business error when try to save a new registration with a registration duplicated")

    public void shouldNotSaveRegistrationDuplicated(){
        Registration registration = createValidRegistration();
        Mockito.when(repository.existsByRegistration(Mockito.any())).thenReturn(true);

        Throwable exception = Assertions.catchThrowable(()-> registrationService.save(registration));
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Registration already created");
        Mockito.verify(repository, Mockito.never()).save(registration);
    }

    @Test
    @DisplayName("Should get a registration by id")
    public void getRegistrationByIdTest(){
        Integer id = 11;
        Registration registration = createValidRegistration();
        registration.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(registration));

        Optional<Registration> foundRegistration = registrationService.getRegistrationById(id);

        assertThat(foundRegistration.isPresent()).isTrue();
        assertThat(foundRegistration.get().getId()).isEqualTo(id);
        assertThat(foundRegistration.get().getName()).isEqualTo(registration.getName());
        assertThat(foundRegistration.get().getRegistration()).isEqualTo(registration.getRegistration());
    }

    @Test
    @DisplayName("Should return empty when get a regisration by id")
    public void registrationNotFoundById(){
        Integer id = 11;
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Registration> registration = registrationService.getRegistrationById(id);

        assertThat(registration.isPresent()).isFalse();

    }

    @Test
    @DisplayName("Should delete a student")
    public void deleteRegistrationTest(){
        Registration registration = Registration.builder().id(11).build();

        assertDoesNotThrow(()-> registrationService.delete(registration));
        Mockito.verify(repository, Mockito.times(1)).delete(registration);

    }

    @Test
    @DisplayName("Should update a registration")
    public void updateRegistration(){
        Integer id = 11;
        Registration updatingregistration = Registration.builder().id(11).build();

        Registration updatedRegistration = createValidRegistration();
        updatedRegistration.setId(11);

        Mockito.when(repository.save(updatingregistration)).thenReturn(updatedRegistration);
        Registration registration = registrationService.update(updatingregistration);

        assertThat(registration.getId()).isEqualTo(updatedRegistration.getId());
        assertThat(registration.getName()).isEqualTo(updatedRegistration.getName());
        assertThat(registration.getDateOfRegistration()).isEqualTo(updatedRegistration.getDateOfRegistration());
        assertThat(registration.getRegistration()).isEqualTo(updatedRegistration.getRegistration());

    }

    @Test
    @DisplayName("Should filter registrations must by properties")
    public void findRegistrationTest(){

        Registration registration = createValidRegistration();
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Registration> listRegistration = Arrays.asList(registration);
        Page<Registration> page = new PageImpl<Registration>(Arrays.asList(registration),
                PageRequest.of(0, 10), 1);

        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        Page<Registration> result = registrationService.find(registration, pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(listRegistration);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);

    }

    @Test
    @DisplayName("Should get a registration model by registration attribute")
    public void getRegistrationByRegistration(){
        String registrationAttribute = "1234";

        Mockito.when(repository.findByRegistration(registrationAttribute))
                .thenReturn(Optional.of(Registration.builder().id(1).registration(registrationAttribute).build()));

        Optional<Registration> registration = registrationService.getRegistrationByRegistrationAttrb(registrationAttribute);

        assertThat(registration.isPresent()).isTrue();
        assertThat(registration.get().getId()).isEqualTo(1);
        assertThat(registration.get().getRegistration()).isEqualTo(registrationAttribute);

        Mockito.verify(repository, Mockito.times(1)).findByRegistration(registrationAttribute);
    }

    private Registration createValidRegistration() {
        return Registration.builder()
                .id(101)
                .name("Jasmini Santos")
                .dateOfRegistration(LocalDate.now().toString())
                .registration("001")
                .build();
    }
}
