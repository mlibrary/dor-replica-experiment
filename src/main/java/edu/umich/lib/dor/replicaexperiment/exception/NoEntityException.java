package edu.umich.lib.dor.replicaexperiment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NoEntityException extends RuntimeException {
    public NoEntityException(String message) {
        super(message);
    }
}
