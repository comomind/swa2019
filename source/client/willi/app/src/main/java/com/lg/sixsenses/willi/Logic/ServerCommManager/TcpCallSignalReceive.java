package com.lg.sixsenses.willi.logic.ServerCommManager;

public class TcpCallSignalReceive<T>{
    TcpCallSignalHeader header;
    T body;

    public TcpCallSignalHeader getHeader() {
        return header;
    }

    public void setHeader(TcpCallSignalHeader header) {
        this.header = header;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
