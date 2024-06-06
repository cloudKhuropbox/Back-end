package com.khu.cloudcomputing.khuropbox.summurygenerator.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class GptRequestDTO {
    private String model;
    private Message message;
    private float temperature;
    private float topP;
    private float frequencyPenalty;
    private float presencePenalty;
}
