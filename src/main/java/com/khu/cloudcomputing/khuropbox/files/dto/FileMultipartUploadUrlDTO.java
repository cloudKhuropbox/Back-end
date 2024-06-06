package com.khu.cloudcomputing.khuropbox.files.dto;

import java.io.Serializable;

public class FileMultipartUploadUrlDTO implements Serializable {
    private int partNumber;
    private String uploadUrl;

    public FileMultipartUploadUrlDTO(int partNumber, String uploadUrl) {
        this.partNumber = partNumber;
        this.uploadUrl = uploadUrl;
    }

    public int getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }
}
