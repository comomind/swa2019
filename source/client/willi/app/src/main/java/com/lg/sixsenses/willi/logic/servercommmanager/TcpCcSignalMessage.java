package com.lg.sixsenses.willi.logic.servercommmanager;

public class TcpCcSignalMessage
{
    private TcpCallSignalHeader header;
    private TcpCcSignalBody body;

    public TcpCallSignalHeader getHeader() {
        return header;
    }

    public void setHeader(TcpCallSignalHeader header) {
        this.header = header;
    }

    public TcpCcSignalBody getBody() {
        return body;
    }

    public void setBody(TcpCcSignalBody body) {
        this.body = body;
    }

}
