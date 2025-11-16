package com.univus.project.dto.notice;
// 공지사항 작성
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class NoticeWriteDto {
    private String email;       // 작성자 이메일
    private String name;        // 작성자
    private String title;       // 공지 제목
    private String content;     // 공지 내용
}
