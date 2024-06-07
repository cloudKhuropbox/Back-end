// 두 번째 API 진행 중 응답 DTO
package com.khu.cloudcomputing.khuropbox.stt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranscribeStatusDTO {
    private String id;
    private String status;
}