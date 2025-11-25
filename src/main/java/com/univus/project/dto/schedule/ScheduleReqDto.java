package com.univus.project.dto.schedule;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class ScheduleReqDto {
    private String title;
    private String description;
    private LocalDateTime dateTime;
}
