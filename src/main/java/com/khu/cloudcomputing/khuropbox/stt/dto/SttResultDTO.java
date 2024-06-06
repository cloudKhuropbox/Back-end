package com.khu.cloudcomputing.khuropbox.stt.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SttResultDTO {
    private String id;
    private String status;
    private String results;
}
