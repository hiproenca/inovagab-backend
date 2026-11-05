package com.hig.inovagab.exception;

public class EmailAlreadyRegisteredException extends RuntimeException {
    public EmailAlreadyRegisteredException(String email) {
        super("O e-mail " + email + " já está cadastrado.");
    }
}
