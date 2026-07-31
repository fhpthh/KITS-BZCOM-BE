package org.example.besmarthelpdesk.exception;

import org.example.besmarthelpdesk.enums.ErrorCode;

public class UnauthorizedException extends BaseException {
    public java.util.UUID userId; // Optional context if needed, otherwise keeping it simple

    public UnauthorizedException(String message) {
        super(message, ErrorCode.AUTH_UNAUTHORIZED);
    }
}
