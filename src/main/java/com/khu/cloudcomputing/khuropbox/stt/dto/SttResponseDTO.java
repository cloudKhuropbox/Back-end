package com.khu.cloudcomputing.khuropbox.stt.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SttResponseDTO {
    private String requestId;
    private String status;
    private String message;
}
