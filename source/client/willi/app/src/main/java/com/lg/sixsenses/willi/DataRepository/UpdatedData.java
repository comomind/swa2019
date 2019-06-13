package com.lg.sixsenses.willi.DataRepository;

public class UpdatedData {
    private String type;
    private Object data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UpdatedData{" +
                "type='" + type + '\'' +
                ", data=" + data +
                '}';
    }
}
