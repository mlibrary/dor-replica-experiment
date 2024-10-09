package edu.umich.lib.dor.replicaexperiment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class EntityAlreadyExistsException extends BusinessException {
    public EntityAlreadyExistsException(String message) {
        super(message);
    }
}
