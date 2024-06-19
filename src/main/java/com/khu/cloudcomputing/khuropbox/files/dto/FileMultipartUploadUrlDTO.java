package com.khu.cloudcomputing.khuropbox.files.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class FileMultipartUploadUrlDTO implements Serializable {
    private int partNumber;
    private String uploadUrl;

    public FileMultipartUploadUrlDTO(int partNumber, String uploadUrl) {
        this.partNumber = partNumber;
        this.uploadUrl = uploadUrl;
    }
}
