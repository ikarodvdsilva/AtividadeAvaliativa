package br.com.ikaro.atividadeavaliativa.utils;

public class Result<T> {
    private final T data;
    private final Exception error;

    private Result(T data, Exception error) {
        this.data = data;
        this.error = error;
    }

    public static <T> Result<T> success(T data) {
        return new Success<>(data);
    }

    public static <T> Result<T> error(Exception error) {
        return new Error<>(error);
    }

    public boolean isSuccess() {
        return error == null;
    }

    public T getData() {
        return data;
    }

    public Exception getError() {
        return error;
    }

    public static class Success<T> extends Result<T> {
        public Success(T data) {
            super(data, null);
        }
    }

    public static class Error<T> extends Result<T> {
        public Error(Exception error) {
            super(null, error);
        }
    }
} 