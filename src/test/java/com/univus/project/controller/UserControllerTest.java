package com.univus.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.univus.project.config.CustomUserDetails;
import com.univus.project.constant.Role;
import com.univus.project.dto.user.UserModifyReqDto;
import com.univus.project.dto.user.UserResDto;
import com.univus.project.entity.User;
import com.univus.project.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;


    private User testUser;
    private UserResDto userDto;
    private CustomUserDetails customUserDetails;


    @BeforeEach
    void setup() {
        userDto = new UserResDto();
        userDto.setEmail("test@test.com");
        userDto.setName("홍길동");

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("홍길동");
        testUser.setActive(true);
        testUser.setRole(Role.STUDENT);

        customUserDetails = new CustomUserDetails(testUser);
    }

    // 🔥 Security를 통과할 수 있도록 MockUser 사용
    @Test
    @WithMockUser(username = "test@test.com", roles = {"USER"})
    void getUsers_success() throws Exception {
        when(userService.findAll()).thenReturn(List.of(userDto));

        mockMvc.perform(get("/user/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@test.com"))
                .andExpect(jsonPath("$[0].name").value("홍길동"));

        verify(userService, times(1)).findAll();
    }

    @Test
    @WithMockUser(username = "test@test.com", roles = {"USER"})
    void getUserByEmail_success() throws Exception {
        when(userService.findByEmail("test@test.com")).thenReturn(userDto);

        mockMvc.perform(get("/user/email/test@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.name").value("홍길동"));

        verify(userService).findByEmail("test@test.com");
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void getUsersByAdmin_success() throws Exception {
        when(userService.findAllByAdmin()).thenReturn(List.of(userDto));

        mockMvc.perform(get("/user/admin/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@test.com"));

        verify(userService, times(1)).findAllByAdmin();
    }

    @Test
    @WithMockUser(username = "test@test.com", roles = {"USER"})
    void updateUserProfile_success() throws Exception {
        UserModifyReqDto dto = new UserModifyReqDto();
        dto.setName("수정된 이름");

        mockMvc.perform(put("/user/test@test.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(userService).updateUserProfile(eq("test@test.com"), any(UserModifyReqDto.class));
    }

    @Test
    @WithMockUser(username = "test@test.com", roles = {"USER"})
    void withdrawUser_success() throws Exception {
        when(userService.withdrawUser("test@test.com")).thenReturn(true);

        mockMvc.perform(patch("/user/withdraw/test@test.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("회원 탈퇴가 완료되었습니다."));

        verify(userService).withdrawUser("test@test.com");
    }

    // 관리자 삭제
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void deleteUserByAdmin_success() throws Exception {
        when(userService.deleteUserByAdmin("test@test.com")).thenReturn(true);

        mockMvc.perform(delete("/user/admin/test@test.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("관리자에 의해 계정이 삭제되었습니다."));

        verify(userService).deleteUserByAdmin("test@test.com");
    }

    // Security 인증 사용자 정보 조회
    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void getCurrentUser_success() throws Exception {
        when(userService.findByEmail("test@example.com"))
                .thenReturn(new UserResDto(testUser));

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        customUserDetails,
                        null,
                        customUserDetails.getAuthorities()
                );

        when(userService.findByEmail("test@example.com"))
                .thenReturn(new UserResDto(testUser));


        mockMvc.perform(get("/user/me")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("홍길동"));
    }
    }
