// 두 번째 API 완료 응답 DTO
package com.khu.cloudcomputing.khuropbox.stt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranscribeResultDTO {
    private String id;
    private String status;
    private List<Utterance> utterances;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Utterance {
        private long start_at;
        private long duration;
        private String msg;
        private int spk;
    }
}