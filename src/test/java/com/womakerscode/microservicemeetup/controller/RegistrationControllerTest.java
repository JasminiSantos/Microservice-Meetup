package com.womakerscode.microservicemeetup.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.womakerscode.microservicemeetup.controller.resource.RegistrationController;
import com.womakerscode.microservicemeetup.exception.BusinessException;
import com.womakerscode.microservicemeetup.controller.dto.RegistrationDTO;
import com.womakerscode.microservicemeetup.model.entity.Registration;
import com.womakerscode.microservicemeetup.service.RegistrationService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = {RegistrationController.class})
@AutoConfigureMockMvc
public class RegistrationControllerTest {
    static String REGISTRATION_API = "/api/registration";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    RegistrationService registrationService;

    @Test
    @DisplayName("Should create a registration with success")
    public void createRegistrationTest() throws Exception{

        RegistrationDTO registrationDTOBuilder = createNewRegistration();
        Registration savedRegistration = Registration.builder()
                .id(101)
                .name("Jasmini Santos")
                .dateOfRegistration(LocalDate.now().toString())
                .registration("001")
                .build();

        BDDMockito.given(registrationService.save(any(Registration.class))).willReturn(savedRegistration);

        String json = new ObjectMapper().writeValueAsString(registrationDTOBuilder);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(REGISTRATION_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(registrationDTOBuilder.getId()))
                .andExpect(jsonPath("name").value(registrationDTOBuilder.getName()))
                .andExpect(jsonPath("dateOfRegistration").value(registrationDTOBuilder.getDateOfRegistration()))
                .andExpect(jsonPath("registration").value(registrationDTOBuilder.getRegistration()));
    }

    @Test
    @DisplayName("Should throw an exception when not have data enough for the test")
    public void createInvalidRegistration() throws Exception{
        String json = new ObjectMapper().writeValueAsString(new RegistrationDTO());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(REGISTRATION_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get registration information")
    public void getRegistrationTest() throws Exception{
        Integer id = 101;

        Registration registration = Registration.builder()
                .id(id)
                .name(createNewRegistration().getName())
                .dateOfRegistration(createNewRegistration().getDateOfRegistration())
                .registration(createNewRegistration().getRegistration())
                .build();

        BDDMockito.given(registrationService.getRegistrationById(id)).willReturn(Optional.of(registration));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(REGISTRATION_API.concat("/"+ id))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(createNewRegistration().getId()))
                .andExpect(jsonPath("name").value(createNewRegistration().getName()))
                .andExpect(jsonPath("dateOfRegistration").value(createNewRegistration().getDateOfRegistration()))
                .andExpect(jsonPath("registration").value(createNewRegistration().getRegistration()));
    }

    @Test
    @DisplayName("Should throw an exception when try to create a new registration with another registration created")
    public void createRegistrationDuplicated() throws Exception{
        RegistrationDTO dto = createNewRegistration();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(registrationService.save(any(Registration.class)))
                .willThrow(new BusinessException("Registration already created"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(REGISTRATION_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Registration already created"));

    }

    @Test
    @DisplayName("Should return not found when the registration doesn't exist")
    public void registrationNotFoundTest(){
            BDDMockito.given(registrationService.getRegistrationById(anyInt())).willReturn(Optional.empty());

            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get(REGISTRATION_API.concat("/"+1))
                    .accept(MediaType.APPLICATION_JSON);

        try {
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    @DisplayName("Should delete the registration")
    public void deleteRegistrationTest() throws Exception{
        BDDMockito.given(registrationService
                .getRegistrationById(anyInt()))
                .willReturn(Optional.of(Registration.builder().id(11).build()));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(REGISTRATION_API.concat("/"+1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return resource not found when no registration is found to delete")
    public void deleteNonExistentRegistrationTest() throws Exception{
        BDDMockito.given(registrationService
                        .getRegistrationById(anyInt()))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(REGISTRATION_API.concat("/"+1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should update when registration info")
    public void updateRegistrationTest() throws Exception {
        Integer id = 101;
        String json = new ObjectMapper().writeValueAsString(createNewRegistration());

        Registration updatingRegistration =
                Registration.builder()
                        .id(id)
                        .name("Rory")
                        .dateOfRegistration(LocalDate.now().toString())
                        .registration("323")
                        .build();

        BDDMockito.given(registrationService.getRegistrationById(anyInt()))
                .willReturn(Optional.of(updatingRegistration));

        Registration updatedRegistration =
                Registration.builder()
                        .id(id)
                        .name("Jasmini Santos")
                        .dateOfRegistration(LocalDate.now().toString())
                        .registration("001")
                        .build();

        BDDMockito.given(registrationService.
                update(updatingRegistration))
                .willReturn(updatedRegistration);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(REGISTRATION_API.concat("/"+1))
                .contentType(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(createNewRegistration().getId()))
                .andExpect(jsonPath("name").value(createNewRegistration().getName()))
                .andExpect(jsonPath("dateOfRegistration").value(createNewRegistration().getDateOfRegistration()))
                .andExpect(jsonPath("registration").value(createNewRegistration().getRegistration()));

    }

    @Test
    @DisplayName("Should return 404 when trying to update a registration no existent")
    public void updateNonExistentRegistrationTest() throws Exception{
        String json = new ObjectMapper().writeValueAsString(createNewRegistration());
        BDDMockito.given(registrationService.getRegistrationById(anyInt()))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(REGISTRATION_API.concat("/"+1))
                .contentType(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should filter registration")
    public void findRegistrationTest() throws Exception {
        Integer id = 11;

        Registration registration = Registration.builder()
                .id(id)
                .name(createNewRegistration().getName())
                .dateOfRegistration(createNewRegistration().getDateOfRegistration())
                .registration(createNewRegistration().getRegistration())
                .build();

        BDDMockito.given(registrationService.find(Mockito.any(Registration.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Registration>(Arrays.asList(registration), PageRequest.of(0,100), 1));

        String queryString = String.format("?name=%s&dateOfRegistration=%s&page=0&size=100",
                registration.getRegistration(), registration.getDateOfRegistration());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(REGISTRATION_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }

    private RegistrationDTO createNewRegistration(){
        return RegistrationDTO.builder()
                .id(101)
                .name("Jasmini Santos")
                .dateOfRegistration(LocalDate.now().toString())
                .registration("001")
                .build();
    }
}
