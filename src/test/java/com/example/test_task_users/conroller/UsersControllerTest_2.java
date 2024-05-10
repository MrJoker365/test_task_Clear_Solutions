package com.example.test_task_users.conroller;

import com.example.test_task_users.controller.UsersController;
import com.example.test_task_users.dto.UsersDto;
import com.example.test_task_users.model.Users;
import com.example.test_task_users.repo.UsersRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
@ContextConfiguration(classes = UsersController.class)
public class UsersControllerTest_2 {

    @Autowired
    private UsersController usersController;

    @MockBean
    private UsersRepo usersRepo;

    private MockMvc mockMvc;




    private ObjectMapper objectMapper;

    private Users user;

    private static final int MOCKED_AGE_LIMIT = 18;


    @BeforeEach
    void setUp() throws JsonProcessingException {


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
    public void testUpdateSomeUserField() throws Exception {

        UsersDto usersDto = new UsersDto();
        usersDto.setEmail("tstr123@gmail.com");
        usersDto.setFirstName("Oleh");

        given(usersRepo.findAllByEmail(anyString())).willReturn(Optional.of(user));

        Users user_1 = user;
        user_1.setFirstName("Oleh");


        ResultActions result = mockMvc.perform(put("/api/update_some")
                .param("usersDto", objectMapper.writeValueAsString(usersDto))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user_1)));

        result.andExpect(status().isOk());
    }
}

