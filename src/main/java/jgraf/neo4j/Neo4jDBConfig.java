package jgraf.neo4j;

import jgraf.core.BaseDBConfig;

public class Neo4jDBConfig extends BaseDBConfig {
    public final String NEO4J_CONFIG_FILE;
    public final String NEO4J_PLUGIN_PATH;
    public final boolean NEO4J_RTREE_STORE;
    public final String NEO4J_RTREE_IMG_PATH;

    public Neo4jDBConfig(String configPath) {
        super(configPath);
        NEO4J_CONFIG_FILE = config.getString("neo4j.config.file");
        NEO4J_PLUGIN_PATH = config.getString("neo4j.plugin.path");
        NEO4J_RTREE_STORE = config.getBoolean("neo4j.rtree.store");
        NEO4J_RTREE_IMG_PATH = config.getString("neo4j.rtree.img.path");
    }
}
