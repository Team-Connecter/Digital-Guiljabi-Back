package com.connecter.digitalguiljabiback.exception;

public class ReportDuplicatedException extends RuntimeException {
    public ReportDuplicatedException() {
        super();
    }

    public ReportDuplicatedException(String message) {
        super(message);
    }

    public ReportDuplicatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReportDuplicatedException(Throwable cause) {
        super(cause);
    }

    protected ReportDuplicatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
