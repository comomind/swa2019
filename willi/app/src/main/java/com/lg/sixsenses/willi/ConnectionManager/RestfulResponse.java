package com.lg.sixsenses.willi.ConnectionManager;

public class RestfulResponse<T> {
    private String result;
    private String message;
    private String type;
    private String token;
    private T body;


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "RestfulResponse{" +
                "result='" + result + '\'' +
                ", message='" + message + '\'' +
                ", type='" + type + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
