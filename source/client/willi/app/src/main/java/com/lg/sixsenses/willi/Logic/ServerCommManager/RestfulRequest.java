package com.lg.sixsenses.willi.logic.ServerCommManager;

public class RestfulRequest {

    public String token;
    public Object body;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
