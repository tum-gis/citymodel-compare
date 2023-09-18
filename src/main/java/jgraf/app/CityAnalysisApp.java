package jgraf.app;

import jgraf.citygml.CityGMLNeo4jDBConfig;
import jgraf.citygml.CityGMLNeo4jDBV2;
import jgraf.citygml.CityGMLNeo4jDBV3;

public class CityAnalysisApp {

    private static void analyseCityGMLV2() throws InterruptedException {
        CityGMLNeo4jDBV2 cityGMLNeo4jDB
                = new CityGMLNeo4jDBV2(new CityGMLNeo4jDBConfig("config/citygmlv2.conf"));
        cityGMLNeo4jDB.open();
        cityGMLNeo4jDB.mapFromConfig();
        cityGMLNeo4jDB.diff(0, 1);
        cityGMLNeo4jDB.summarize();
        cityGMLNeo4jDB.interpretDiff();
        //cityGMLNeo4jDB.close();
        cityGMLNeo4jDB.remainOpen();
    }

    public static void main(String[] args) throws InterruptedException {
        analyseCityGMLV2();
    }
}
