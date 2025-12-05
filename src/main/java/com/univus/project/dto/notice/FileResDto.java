package com.univus.project.dto.notice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter @Setter @ToString
public class FileResDto {
    private String fileName;
    private String fileUrl;

}
