package com.univus.project.dto.schedule;

import com.univus.project.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ScheduleResDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dateTime;

    public static ScheduleResDto fromEntity(Schedule s) {
        return new ScheduleResDto(
                s.getId(),
                s.getTitle(),
                s.getDescription(),
                s.getDateTime()
        );
    }
}
