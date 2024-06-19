package com.khu.cloudcomputing.khuropbox.stt.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SttRequestConfig {
    private boolean useDiarization;
    private DiarizationConfig diarization;
    private boolean useMultiChannel;
    private boolean useItn;
    private boolean useDisfluencyFilter;
    private boolean useProfanityFilter;
    private boolean useParagraphSplitter;
    private int paragraphSplitterMax;
}

