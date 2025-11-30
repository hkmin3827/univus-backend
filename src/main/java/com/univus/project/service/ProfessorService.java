package com.univus.project.service;

import com.univus.project.dto.professor.ProfessorDetailResDto;
import com.univus.project.dto.professor.ProfessorModifyReqDto;
import com.univus.project.dto.user.UserResDto;
import com.univus.project.entity.Professor;
import com.univus.project.repository.ProfessorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProfessorService {

    private final ProfessorRepository professorRepository;

    // 교수 정보 조회
    public ProfessorDetailResDto getProfessorDetailByEmail(String email) {
        Professor professor = professorRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("교수 정보가 없습니다. email=" + email));

        // 공통 정보
        UserResDto userDto = new UserResDto(
                professor.getId(),
                professor.getEmail(),
                professor.getName(),
                professor.getPhone(),   // ✅ 여기
                professor.getImage(),
                professor.getRegDate(),
                professor.getRole(),
                professor.isActive()
        );

        // 교수 전용 정보
        ProfessorDetailResDto dto = new ProfessorDetailResDto();
        dto.setUser(userDto);
        dto.setDepartment(professor.getDepartment());
        dto.setPosition(professor.getPosition());

        return dto;
    }

    // 교수 정보 수정 (email 기준)
    public void updateProfessorProfile(String email, ProfessorModifyReqDto dto) {
        Professor professor = professorRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("교수 정보가 없습니다. email=" + email));

        if (dto.getDepartment() != null && !dto.getDepartment().isBlank()) {
            professor.setDepartment(dto.getDepartment());
        }
        if (dto.getPosition() != null && !dto.getPosition().isBlank()) {
            professor.setPosition(dto.getPosition());
        }

        log.info("교수 정보 수정 완료 → email={}", email);
    }
}
