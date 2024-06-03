package jgraf.app;

import jgraf.citygml.CityGMLNeo4jDBConfig;
import jgraf.citygml.CityGMLNeo4jDBV3;

public class CityGMLExporterV3 {
    public static void main(String[] args) throws InterruptedException {
        CityGMLNeo4jDBV3 cityGMLNeo4jDB
                = new CityGMLNeo4jDBV3(new CityGMLNeo4jDBConfig("config/citygmlv3.conf")); // testMapv2.conf
        cityGMLNeo4jDB.openExisting();
        cityGMLNeo4jDB.exportCityGML(0, "output/citygml/exportV3.gml");
        cityGMLNeo4jDB.close();
    }
}
