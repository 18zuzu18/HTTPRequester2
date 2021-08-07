package de.reetmeyer.db;

import java.time.LocalDateTime;

public class DbResponse {
    public DbResponse(int id, int code, String response, String key) {
        this.id = id;
        this.code = code;
        this.response = response;
        this.key = key;
    }

    public int id;
    public int code;
    public String response;
    public LocalDateTime responseTime;
    public String key;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DbResponse{");
        sb.append("id=").append(id);
        sb.append(", code=").append(code);
        sb.append(", response='").append(response).append('\'');
        sb.append(", responseTime=").append(responseTime);
        sb.append(", key='").append(key).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
