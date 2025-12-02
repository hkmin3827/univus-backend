package com.univus.project.dto.notice;

import com.univus.project.entity.Notice;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @ToString
public class NoticeResDto {
    private Long id;        // 공지 Id
    private String title;   // 공지 제목
    private String content; // 공지 내용
    private String email;    // 작성자 이메일
    private String writerName;
    private LocalDateTime createTime;

    public NoticeResDto(Notice notice) {
        this.id = notice.getId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.email = notice.getUser() != null ? notice.getUser().getEmail() : null;
        this.writerName = notice.getWriterName();
        this.createTime = notice.getCreateTime();
    }
}
