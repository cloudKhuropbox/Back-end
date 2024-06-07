// 첫 번째 API 성공 응답 DTO
package com.khu.cloudcomputing.khuropbox.stt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranscribeResponseDTO {
    private String transcribeId;
}