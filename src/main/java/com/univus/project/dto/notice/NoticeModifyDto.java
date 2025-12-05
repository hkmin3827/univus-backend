package com.univus.project.dto.notice;
// 공지사항 수정
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class NoticeModifyDto {
    private String title;       // 공지 제목
    private String content;     // 공지 내용
    private String fileUrl;
    private String fileName;

}
