package com.khu.cloudcomputing.khuropbox.apiPayload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse<T> {
    private boolean success;
    private T data;
    private String error;

    public static <T> CommonResponse<T> success(T data) {
        CommonResponse<T> response = new CommonResponse<>();
        response.setSuccess(true);
        response.setData(data);
        return response;
    }

    public static <T> CommonResponse<T> failure(String error) {
        CommonResponse<T> response = new CommonResponse<>();
        response.setSuccess(false);
        response.setError(error);
        return response;
    }
}
