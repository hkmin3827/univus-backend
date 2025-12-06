package com.univus.project.service;

import com.univus.project.dto.user.UserModifyReqDto;
import com.univus.project.dto.user.UserResDto;
import com.univus.project.entity.User;
import com.univus.project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);

        user1 = new User();
        user1.setId(1L);
        user1.setEmail("test1@test.com");
        user1.setName("홍길동");
        user1.setActive(true);

        user2 = new User();
        user2.setId(2L);
        user2.setEmail("test2@test.com");
        user2.setName("김철수");
        user2.setActive(false);
    }

    @Test
    void findAll_success() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserResDto> result = userService.findAll();

        assertEquals(1, result.size());
        assertEquals("홍길동", result.get(0).getName());
    }

    @Test
    void findByEmail_success() {
        when(userRepository.findByEmailAndActiveTrue("test1@test.com"))
                .thenReturn(Optional.of(user1));

        UserResDto result = userService.findByEmail("test1@test.com");

        assertNotNull(result);
        assertEquals("홍길동", result.getName());
    }

    @Test
    void findByEmail_fail() {
        when(userRepository.findByEmailAndActiveTrue("notfound@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userService.findByEmail("notfound@test.com"));
    }

    @Test
    void updateUserProfile_success() {
        when(userRepository.findByEmail("test1@test.com")).thenReturn(Optional.of(user1));

        UserModifyReqDto dto = new UserModifyReqDto();
        dto.setName("수정됨");
        dto.setImage("img.png");
        dto.setPhone("01000000000");

        userService.updateUserProfile("test1@test.com", dto);

        assertEquals("수정됨", user1.getName());
        assertEquals("img.png", user1.getImage());
        assertEquals("01000000000", user1.getPhone());
        verify(userRepository, times(1)).findByEmail("test1@test.com");
    }

    @Test
    void withdrawUser_success() {
        when(userRepository.findByEmailAndActiveTrue("test1@test.com"))
                .thenReturn(Optional.of(user1));

        boolean result = userService.withdrawUser("test1@test.com");

        assertTrue(result);
        assertFalse(user1.isActive());
    }

    @Test
    void withdrawUser_fail() {
        when(userRepository.findByEmailAndActiveTrue("no@test.com"))
                .thenReturn(Optional.empty());

        boolean result = userService.withdrawUser("no@test.com");

        assertFalse(result);
    }

    @Test
    void deleteUserByAdmin_success() {
        when(userRepository.findByEmail("test1@test.com"))
                .thenReturn(Optional.of(user1));

        boolean result = userService.deleteUserByAdmin("test1@test.com");

        assertTrue(result);
        verify(userRepository, times(1)).delete(user1);
    }

    @Test
    void deleteUserByAdmin_fail() {
        when(userRepository.findByEmail("no@test.com"))
                .thenReturn(Optional.empty());

        boolean result = userService.deleteUserByAdmin("no@test.com");

        assertFalse(result);
        verify(userRepository, never()).delete(any());
    }
}
