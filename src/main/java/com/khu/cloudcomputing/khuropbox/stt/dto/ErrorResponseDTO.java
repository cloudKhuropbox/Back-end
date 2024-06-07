// 첫 번째 API 실패 응답 DTO
package com.khu.cloudcomputing.khuropbox.stt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {
    private String code;
    private String msg;
}
