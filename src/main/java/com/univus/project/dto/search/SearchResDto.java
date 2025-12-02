package com.univus.project.dto.search;

import com.univus.project.dto.comment.CommentResDto;
import com.univus.project.dto.notice.NoticeResDto;
import com.univus.project.dto.post.PostResDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class SearchResDto {
        private List<PostResDto> posts;
        private List<CommentResDto> comments;
        private List<NoticeResDto> notices;
}
