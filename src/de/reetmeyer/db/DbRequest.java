package de.reetmeyer.db;

public class DbRequest {
    public int id;
    public String path;
    public int status;

    public DbRequest(int id, String path, int status) {
        this.id = id;
        this.path = path;
        this.status = status;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DbRequest{");
        sb.append("id=").append(id);
        sb.append(", path='").append(path).append('\'');
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }
}
