package com.univus.project.dto.schedule;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class HomeScheduleResDto {
    private List<ScheduleResDto> todaySchedules;
    private List<ScheduleResDto> upcomingSchedules; // 7일 이내
}