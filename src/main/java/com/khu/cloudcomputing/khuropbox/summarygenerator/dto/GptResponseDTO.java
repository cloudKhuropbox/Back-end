package com.khu.cloudcomputing.khuropbox.summarygenerator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GptResponseDTO {
    private Message message;
}
