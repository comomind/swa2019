package com.lg.sixsenses.willi.logic.servercommmanager;

import java.io.Serializable;

public class TcpCallSignalRequest implements Serializable {
    TcpCallSignalHeader header;
    TcpCallSignalBody body;

    public TcpCallSignalHeader getHeader() {
        return header;
    }

    public void setHeader(TcpCallSignalHeader header) {
        this.header = header;
    }

    public TcpCallSignalBody getBody() {
        return body;
    }

    public void setBody(TcpCallSignalBody body) {
        this.body = body;
    }
}
