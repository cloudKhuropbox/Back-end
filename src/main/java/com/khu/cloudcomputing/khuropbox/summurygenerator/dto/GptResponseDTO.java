package com.khu.cloudcomputing.khuropbox.summurygenerator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GptResponseDTO {
    private Message message;
}
