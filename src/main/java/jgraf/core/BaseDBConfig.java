package jgraf.core;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class BaseDBConfig {
    protected Config config;
    public final String DB_PATH;
    public final String DB_NAME;
    public final int DB_BATCH_SIZE;
    public final List<String> MAPPER_DATASET_PATHS;
    public final Set<Class<?>> MAPPER_EXCLUDE_VERTEX_CLASSES;
    public final Set<String> MAPPER_EXCLUDE_EDGE_TYPES;
    public final int MAPPER_CONCURRENT_TIMEOUT;
    public final double MATCHER_TOLERANCE_LENGTHS;
    public final double MATCHER_TOLERANCE_ANGLES;
    public final double MATCHER_TOLERANCE_SURFACES;
    public final double MATCHER_TOLERANCE_SOLIDS;
    public final double MATCHER_TRANSLATION_DISTANCE;
    public final int MATCHER_CONCURRENT_TIMEOUT;
    public final int MATCHER_TOPLEVEL_BATCH_SIZE;
    public final String MATCHER_CHANGES_EXPORT_PATH;

    public BaseDBConfig(String configPath) {
        Config parsedConfig = ConfigFactory.parseFile(new File(configPath));
        config = ConfigFactory.load(parsedConfig);
        DB_PATH = config.getString("db.path");
        DB_NAME = config.getString("db.name");
        DB_BATCH_SIZE = config.getInt("db.batch.size");
        MAPPER_DATASET_PATHS = config.getStringList("mapper.dataset.paths");
        MAPPER_EXCLUDE_VERTEX_CLASSES = new HashSet<>();
        config.getStringList("mapper.exclude.vertex.classes").forEach(label -> {
            try {
                MAPPER_EXCLUDE_VERTEX_CLASSES.add(Class.forName(label));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        MAPPER_EXCLUDE_EDGE_TYPES = new HashSet<>();
        MAPPER_EXCLUDE_EDGE_TYPES.addAll(config.getStringList("mapper.exclude.edge.types"));
        MAPPER_CONCURRENT_TIMEOUT = config.getInt("mapper.concurrent.timeout");
        MATCHER_TOLERANCE_LENGTHS = config.getDouble("matcher.tolerance.lengths");
        MATCHER_TOLERANCE_ANGLES = config.getDouble("matcher.tolerance.angles");
        MATCHER_TOLERANCE_SURFACES = config.getDouble("matcher.tolerance.surfaces");
        MATCHER_TOLERANCE_SOLIDS = config.getDouble("matcher.tolerance.solids");
        MATCHER_TRANSLATION_DISTANCE = config.getDouble("matcher.translation.distance");
        MATCHER_CONCURRENT_TIMEOUT = config.getInt("matcher.concurrent.timeout");
        MATCHER_TOPLEVEL_BATCH_SIZE = config.getInt("matcher.toplevel.batch.size");
        MATCHER_CHANGES_EXPORT_PATH = config.getString("matcher.changes.export.path");
    }
}
