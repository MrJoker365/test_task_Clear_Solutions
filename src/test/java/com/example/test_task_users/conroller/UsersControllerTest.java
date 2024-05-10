package com.example.test_task_users.conroller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;

import com.example.test_task_users.controller.UsersController;
import com.example.test_task_users.dto.UsersDto;
import com.example.test_task_users.model.Users;
import com.example.test_task_users.repo.UsersRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UsersControllerTest {


    @Mock
    private UsersRepo usersRepo;

    @InjectMocks
    private UsersController usersController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Users user;

    private static final int MOCKED_AGE_LIMIT = 18;


    @BeforeEach
    void setUp() throws JsonProcessingException {

        MockitoAnnotations.initMocks(this);

        usersController.setAgeLimit(MOCKED_AGE_LIMIT);


        mockMvc = MockMvcBuilders.standaloneSetup(usersController).build();
        objectMapper = new ObjectMapper();

        user = Users.builder()
                .firstName("Taras")
                .lastName("Bogdanovych")
                .birthDate(Date.from(LocalDate.now().minusYears(20).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .email("tstr123@gmail.com")
                .build();

    }

    @Test
    public void UsersController_create_ResponseOk() throws Exception {


        mockMvc.perform(post("/api/create_user")
                        .param("user", objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

    }

    @Test
    public void UsersController_create_WhenAgeIsInvalid() throws Exception {
        
        Users user_1 = user;

//        user_1.setBirthDate(new SimpleDateFormat("yyyy-MM-dd").parse("2007-01-01"));

        user_1.setBirthDate(Date.from(LocalDate.now().minusYears(MOCKED_AGE_LIMIT).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        mockMvc.perform(post("/api/create_user")
                        .param("user", objectMapper.writeValueAsString(user_1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user_1)))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void UsersController_create_WhenEmailIsNotValid() throws Exception {

        Users user_1 = user;

        user_1.setEmail("1234");

        mockMvc.perform(post("/api/create_user")
                        .param("user", objectMapper.writeValueAsString(user_1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user_1)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void UsersController_create_WhenEmailIsEmpty() throws Exception {

        Users user_1 = user;

        user_1.setEmail("");

        mockMvc.perform(post("/api/create_user")
                        .param("user", objectMapper.writeValueAsString(user_1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user_1)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void UsersController_create_WhenFirstNameIsEmpty() throws Exception {

        Users user_1 = user;

        user_1.setFirstName("");

        mockMvc.perform(post("/api/create_user")
                        .param("user", objectMapper.writeValueAsString(user_1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user_1)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void UsersController_create_WhenLastNameIsEmpty() throws Exception {

        Users user_1 = user;

        user_1.setLastName("");

        mockMvc.perform(post("/api/create_user")
                        .param("user", objectMapper.writeValueAsString(user_1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user_1)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void UsersController_create_WhenBirthdateIsEmpty() throws Exception {

        Users user_1 = user;

        user_1.setBirthDate(null);

        mockMvc.perform(post("/api/create_user")
                        .param("user", objectMapper.writeValueAsString(user_1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user_1)))
                .andExpect(status().isBadRequest());

    }



    @Test
    public void UsersController_searchByBirthDay_ReturnListOfObjects() throws Exception {

        mockMvc.perform(get("/api/search_by_birth_date")
                        .param("fromDate", "2003-01-01")
                        .param("toDate", "2009-01-01")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[" + objectMapper.writeValueAsString(user) + "]"))
                .andExpect(status().isOk());


    }

    @Test
    public void UsersController_searchByBirthDay_WhenFromDateMoreThenToDate() throws Exception {


        mockMvc.perform(get("/api/search_by_birth_date")
                        .param("fromDate", "2009-01-01")
                        .param("toDate", "2003-01-01")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[" + objectMapper.writeValueAsString(user) + "]"))
                .andExpect(status().isBadRequest());


    }


//    @Test
//    public void UsersController_updateSomeUserField_ResponseOk() throws Exception {
//
////        Mockito.when(usersRepo.findAllByEmail(Mockito.any(String.class))).thenReturn(Optional.of(user));
////
////        UsersDto usersDto = new UsersDto();
////        usersDto.setEmail("tstr123@gmail.com");
////        usersDto.setFirstName("Oleh");
////
////        Users user_1 = user;
////        user_1.setFirstName("Oleh");
////
//////        Mockito.when(usersRepo.save(Mockito.any(Users.class))).thenReturn(user_1);
////
////        mockMvc.perform(put("/api/update_some")
////                        .param("userDto", objectMapper.writeValueAsString(usersDto))
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .content(objectMapper.writeValueAsString(user_1)))
////                .andExpect(status().isOk());
//
//
//
//        mockMvc = MockMvcBuilders.standaloneSetup(usersController).build();
//
//        UsersDto usersDto = new UsersDto();
//        usersDto.setEmail("test@example.com");
//        Users users = new Users(); // Assuming Users is a valid entity class
//
//        given(usersRepo.findAllByEmail(anyString())).willReturn(Optional.of(users));
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String json = objectMapper.writeValueAsString(usersDto);
//
//        ResultActions result = mockMvc.perform(put("/api/update_some")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(json));
//
//        result.andExpect(status().isOk());
//
//
//
//    }







}
