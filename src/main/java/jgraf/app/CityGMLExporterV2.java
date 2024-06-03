package jgraf.app;

import jgraf.citygml.CityGMLNeo4jDBConfig;
import jgraf.citygml.CityGMLNeo4jDBV2;

public class CityGMLExporterV2 {
    public static void main(String[] args) throws InterruptedException {
        CityGMLNeo4jDBV2 cityGMLNeo4jDB
                = new CityGMLNeo4jDBV2(new CityGMLNeo4jDBConfig("config/citygmlv2.conf")); // testMapv2.conf
        cityGMLNeo4jDB.openExisting();
        cityGMLNeo4jDB.exportCityGML(0, "output/citygml/exportV2.gml");
        cityGMLNeo4jDB.close();
    }
}
