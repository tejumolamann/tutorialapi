package com.tutorialapi.rest.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponse {
    private final int status;
    private final String message;

    @JsonCreator
    public ErrorResponse(@JsonProperty("status") int status, @JsonProperty("message") String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ErrorResponse that = (ErrorResponse) o;
        return status == that.status && message.equals(that.message);
    }

    @Override
    public int hashCode() {
        int result = status;
        result = 31 * result + message.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
