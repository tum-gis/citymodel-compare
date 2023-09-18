package jgraf.citygml;

import jgraf.neo4j.Neo4jDBConfig;
import org.citygml4j.core.model.CityGMLVersion;

// Map CityGML datasets onto graphs using neo4j database
public class CityGMLNeo4jDBConfig extends Neo4jDBConfig {
    public final String INTERPRETATION_RULES_PATH;
    public final String INTERPRETATION_FUNCTIONS_PATH;
    public final CityGMLVersion CITYGML_VERSION;

    public CityGMLNeo4jDBConfig(String configPath) {
        super(configPath);
        INTERPRETATION_RULES_PATH = config.getString("interpretation.rules.path");
        INTERPRETATION_FUNCTIONS_PATH = config.getString("interpretation.functions.path");
        CITYGML_VERSION = config.getEnum(CityGMLVersion.class, "citygml.version");
    }
}
