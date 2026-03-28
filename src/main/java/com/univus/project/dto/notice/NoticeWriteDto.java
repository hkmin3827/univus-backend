package com.univus.project.dto.notice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class NoticeWriteDto {
    private Long teamId;
    private String email;
    private String title;
    private String content;
    private String fileUrl;
    private String fileName;
}
