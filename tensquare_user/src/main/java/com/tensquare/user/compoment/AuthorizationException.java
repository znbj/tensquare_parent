package com.tensquare.user.compoment;

/**
 * 认证失败
 */
public class AuthorizationException extends Exception {
    public AuthorizationException() {
        super();
    }
    public AuthorizationException(String message) {
        super(message);
    }
}
