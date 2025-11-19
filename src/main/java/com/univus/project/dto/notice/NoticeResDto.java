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
    private String name;    // 작성자 이름
    private LocalDateTime createTime;

    public NoticeResDto(Notice notice) {
        this.id = notice.getId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.name = notice.getUser().getName();
        this.createTime = notice.getCreateTime();
    }
}
