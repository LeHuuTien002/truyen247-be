package com.tien.truyen247be.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

public class ControllerExceptionHandler {

    @ExceptionHandler(value = BadCredentialsException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    @ResponseBody
    public ApiResponse handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        return new ApiResponse(false, "Tài khoản hoặc mật khẩu không chính xác. Hãy nhập lại", ex.getClass().getName(), resolvePathFromRequest(request));
    }

    private String resolvePathFromRequest(WebRequest request) {
        return ((ServletWebRequest) request).getRequest().getRequestURI();
    }
}
