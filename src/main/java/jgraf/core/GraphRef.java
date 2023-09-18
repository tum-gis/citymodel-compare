package jgraf.core;

import java.util.UUID;

public class GraphRef {
    protected String uuid;

    public GraphRef() {
        uuid = String.valueOf(UUID.randomUUID());
    }

    public GraphRef(GraphRef that) {
        uuid = that.uuid;
    }

    public GraphRef(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GraphRef ref) return uuid.equals(ref.uuid);
        return false;
    }
}
