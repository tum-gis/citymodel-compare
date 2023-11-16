package jgraf.app;

import jgraf.citygml.CityGMLNeo4jDBConfig;
import jgraf.citygml.CityGMLNeo4jDBV2;
import jgraf.citygml.CityGMLNeo4jDBV3;

import java.util.Map;

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

    private static void analyseCityGMLV3() throws InterruptedException {
        CityGMLNeo4jDBV3 cityGMLNeo4jDB
                = new CityGMLNeo4jDBV3(new CityGMLNeo4jDBConfig("config/citygmlv3.conf"));
        cityGMLNeo4jDB.open();
        cityGMLNeo4jDB.mapFromConfig(); // Only mapping supports CityGML 3.0
        cityGMLNeo4jDB.summarize();
        cityGMLNeo4jDB.close();
        //cityGMLNeo4jDB.remainOpen();
    }

    private static void analyseFZKHaus() {
        try {
            Map<String, Integer> lod0 = FZKHausUtils.count("input/fzk_haus_lod0_v2.gml");
            Map<String, Integer> lod1 = FZKHausUtils.count("input/fzk_haus_lod1_v2.gml");
            Map<String, Integer> lod2 = FZKHausUtils.count("input/fzk_haus_lod2_v2.gml");
            Map<String, Integer> lod3 = FZKHausUtils.count("input/fzk_haus_lod3_v2.gml");
            Map<String, Integer> lod4 = FZKHausUtils.count("input/fzk_haus_lod4_v2.gml");
            Map<String, Integer[]> merged1 = FZKHausUtils.mergeMaps(lod0, lod1, lod2, lod3, lod4);
            FZKHausUtils.writeMergedMap(merged1, "output/stats/fzk_haus_lods_v2.txt");

            Map<String, Integer> lod3r = FZKHausUtils.count("input/railway_scene_lod3_v2/railway_scene_lod3_v2.gml");
            Map<String, Integer[]> merged2 = FZKHausUtils.mergeMaps(lod3r);
            FZKHausUtils.writeMergedMap(merged2, "output/stats/railway_scene_lod3_v2.txt");

            Map<String, Integer[]> merged3 = FZKHausUtils.mergeMaps(lod0, lod1, lod2, lod3, lod4, lod3r);
            FZKHausUtils.writeMergedMap(merged3, "output/stats/fzk_haus_lods_railway_scene_lod3_v2.txt");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        analyseCityGMLV2();
        //analyseFZKHaus();
    }
}
