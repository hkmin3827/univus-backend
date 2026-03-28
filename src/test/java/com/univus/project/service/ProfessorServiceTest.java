package com.univus.project.service;

import com.univus.project.dto.professor.ProfessorDetailResDto;
import com.univus.project.dto.professor.ProfessorModifyReqDto;
import com.univus.project.dto.user.UserResDto;
import com.univus.project.entity.Professor;
import com.univus.project.repository.ProfessorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfessorServiceTest {

    @Mock
    private ProfessorRepository professorRepository;

    @InjectMocks
    private ProfessorService professorService;

    private Professor professor;

    @BeforeEach
    void setUp() {
        professor = new Professor();
        professor.setId(1L);
        professor.setEmail("prof@test.com");
        professor.setName("홍 교수");
        professor.setDepartment("컴퓨터공학과");
        professor.setPosition("조교수");
    }

    @Test
    void getProfessorDetailByEmail_success() {
        String email = "prof@test.com";
        when(professorRepository.findByEmail(email)).thenReturn(Optional.of(professor));

        ProfessorDetailResDto dto = professorService.getProfessorDetailByEmail(email);

        assertNotNull(dto);
        assertNotNull(dto.getUser());
        assertEquals("컴퓨터공학과", dto.getDepartment());
        assertEquals("조교수", dto.getPosition());

        UserResDto userDto = dto.getUser();
        assertEquals("prof@test.com", userDto.getEmail());
        assertEquals("홍 교수", userDto.getName());

        verify(professorRepository, times(1)).findByEmail(email);
    }

    @Test
    void getProfessorDetailByEmail_notFound_throwsException() {
        String email = "none@test.com";
        when(professorRepository.findByEmail(email)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> professorService.getProfessorDetailByEmail(email));

        assertTrue(ex.getMessage().contains("교수 정보가 없습니다."));
        verify(professorRepository, times(1)).findByEmail(email);
    }

    @Test
    void updateProfessorProfile_success_changeBothFields() {
        String email = "prof@test.com";
        when(professorRepository.findByEmail(email)).thenReturn(Optional.of(professor));

        ProfessorModifyReqDto dto = new ProfessorModifyReqDto();
        dto.setDepartment("소프트웨어학과");
        dto.setPosition("부교수");

        professorService.updateProfessorProfile(email, dto);

        assertEquals("소프트웨어학과", professor.getDepartment());
        assertEquals("부교수", professor.getPosition());

        verify(professorRepository, times(1)).findByEmail(email);
        verify(professorRepository, never()).save(any());
    }

    @Test
    void updateProfessorProfile_blankField_shouldNotChange() {
        String email = "prof@test.com";
        when(professorRepository.findByEmail(email)).thenReturn(Optional.of(professor));

        ProfessorModifyReqDto dto = new ProfessorModifyReqDto();
        dto.setDepartment("   ");
        dto.setPosition(null);

        professorService.updateProfessorProfile(email, dto);

        assertEquals("컴퓨터공학과", professor.getDepartment());
        assertEquals("조교수", professor.getPosition());

        verify(professorRepository, times(1)).findByEmail(email);
        verify(professorRepository, never()).save(any());
    }

    @Test
    void updateProfessorProfile_notFound_throwsException() {
        String email = "none@test.com";
        when(professorRepository.findByEmail(email)).thenReturn(Optional.empty());

        ProfessorModifyReqDto dto = new ProfessorModifyReqDto();
        dto.setDepartment("임의학과");
        dto.setPosition("정교수");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> professorService.updateProfessorProfile(email, dto));

        assertTrue(ex.getMessage().contains("교수 정보가 없습니다."));
        verify(professorRepository, times(1)).findByEmail(email);
    }
}
