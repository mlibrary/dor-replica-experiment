package edu.umich.lib.dor.replicaexperiment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class RepositoryNotRegisteredException extends RuntimeException {
    public RepositoryNotRegisteredException(String message) {
        super(message);
    }
}
