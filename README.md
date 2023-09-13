![Logo citymodel-compare](resources/logo.jpg)

# citymodel-compare

## What it is


A tool to map, match, and interpret changes in CityGML datasets.

+ **MAPPING**: CityGML datasets are represented as **graphs** and stored in the graph database Neo4j. The mapping
  process is very flexible and can support both versions **2.0** and **3.0** of CityGML (as it uses [citygml4j](https://github.com/citygml4j/citygml4j)).

+ **MATCHING**: The graph representations of the mapped CityGML datasets are matched based on both their **semantic**
  and
  **geometric** properties. **Spatial indexing** (such as R-Tree) is used to accelerate matching time in massive
  datasets.

+ **INTERPRETING**: The change detection process often results in a high number of low-semantic-level changes. This
  interpretation process **reduces this number**, while simultaneously **increasing the semantic contents** of changes.

The ultimate goal of this tool is to provide a **precise**, **expressive**, and **human-centered** interpretation of
changes in CityGML.

## How to run it

This tool can be used in two ways: either via Gradle or via Docker. For testing purposes, Docker is recommended.

What is needed:

+ [Docker](https://docs.docker.com/get-docker/)
+ [Neo4j Desktop](https://neo4j.com/download/)

1. Make sure Docker is up and running.

2. Pull the following image from Docker Hub:

   ```shell
   docker pull sonnguyentum/citymodel-compare:1.0.0
   ```

3. Run the image:

   ```shell
    docker run -it --rm -p 7474:7474 -p 7687:7687 sonnguyentum/citymodel-compare:1.0.0
    ```

   This will start a Neo4j instance with all necessary dependencies installed. The parameters are as follows:
    + `-it`: Interactive mode.
    + `--rm`: Remove the container after it exits.
    + `-p 7474:7474`: Expose port 7474 of the container to port 7474 of the host machine. This is the port used by the
      Neo4j browser (such as visualization and inspecting Cypher queries).
    + `-p 7687:7687`: Expose port 7687 of the container to port 7687 of the host machine. This is the port used by the
      Neo4j Bolt connector (such as for RESTful services).

That's it! The old and new CityGML datasets have been mapped, matched, and interpreted. You are now ready to use the
tool.

## How to test it

Now that the tool is up and running, we can test it with a few simple examples. In this section, we will use the
datasets already prepared and available for use in the Docker container.

1. Open Neo4j Desktop, click create new **Remote Datase** and add the following connection details:

   ```shell
   neo4j://localhost:7687
   ```

   If requested, the default username and password are both `neo4j`. The following window should appear:

![Neo4j Browser](resources/neo4j_browser.jpg)

2. In Neo4j Browser, run the following query to count the number of nodes created for the old datasets:

   ```cypher
   MATCH (n:`__PARTITION_INDEX__0`) RETURN count(n)
   ```

   ```Cypher
   Result: 35095
   ```

   Similar, run the following query to count the number of nodes created for the new datasets:

    ```cypher
    MATCH (n:`__PARTITION_INDEX__1`) RETURN count(n)
    ```

    ```Cypher
    Result: 35324
    ```

3. To see how many buildings the old dataset has:

   ```cypher
   MATCH (n:`__PARTITION_INDEX__0`:`org.citygml4j.model.citygml.building.Building`) 
   RETURN count(n)
   ```

   ```Cypher
   Result: 46
   ```

   And the new dataset:

   ```cypher
   MATCH (n:`__PARTITION_INDEX__1`:`org.citygml4j.model.citygml.building.Building`)
   RETURN count(n)
   ```

   ```Cypher
   Result: 47
   ```

4. Now, let's see how many literal changes (i.e. on the lowest semantic level) have been detected:

   ```cypher
   MATCH (c:`jgraf.neo4j.diff.Change`)
   WHERE NOT exists((:`jgraf.neo4j.diff.Change`)-[:AGGREGATED_TO]->(c))
   RETURN count(c)
   ```

   ```Cypher
   Result: 2540
   ```

   All changes are labelled with `jgraf.neo4j.diff.Change`. The second line with `WHERE` is used to filter out changes
   on the lowest level, meaning they do not have any incoming interpretation edges from other changes.

5. To count the number of interpretation nodes created from these literal changes:

   ```cypher
   MATCH (c:`jgraf.neo4j.diff.Change`)
   WHERE exists((:`jgraf.neo4j.diff.Change`)-[:AGGREGATED_TO]->(c))
   RETURN count(c)
   ```

   ```Cypher
   Result: 1512
   ```

5. To see how many literal changes have been interpreted:

   ```cypher
   MATCH (c:`jgraf.neo4j.diff.Change`)
   WHERE NOT exists((:`jgraf.neo4j.diff.Change`)-[:AGGREGATED_TO]->(c))
   AND exists((c)-[:AGGREGATED_TO]->(:`jgraf.neo4j.diff.Change`))
   RETURN count(c)
   ```

   ```Cypher
   Result: 723
   ```

   This means that of the `2540` literal changes, `723`, or `(28%)`, have been interpreted.

6. To visualize a building and its changes:

   ```cypher
   MATCH paths=(:`__PARTITION_INDEX__0`:`org.citygml4j.model.citygml.building.Building` 
   { id:'DEHH_0681b536-7e9c-478f-b131-0f96d1bc7717' })
   <-[]-(:`jgraf.neo4j.diff.Change`)
   RETURN paths
   ```

   The building and its directly connected changes are shown as follows:

   <p align="center">
   <img src="resources/building.svg" alt="An example building and its connected changes" style="width:80%;">
   </p>

---

As shown in the figure, this roofs of this building have been raised by some amount. The **pattern** includes:

+ The building's `measuredHeight` has been changed by `dh_m > 0`

+ The building's roof surfaces have been translated by `dh_r > 0`

+ The building's ground surfaces have been translated by a small `dh_g` (signed, + is upwards, - is downwards)

+ The building's walls have grown in height by `dh_w > 0`, such that `dh_w = dh_m = dh_r + dh_g`

---

7. To find out how many such buildings with raised roofs have been detected:

   ```cypher
   MATCH (c:`jgraf.neo4j.diff.Change` {change_type: 'RaisedBuildingRoofs'})
   -[]->(b:`org.citygml4j.model.citygml.building.Building`)
   RETURN b, c
   ```

   This query returns `6` buildings:

    <p align="center">
    <img src="resources/raised_roofs.svg" alt="Buildings with raised roofs" style="width:80%;">
    </p>

   The raise margins `dh_m` can be queried as follows:

   ```cypher
   MATCH (c:`jgraf.neo4j.diff.Change` {change_type: 'RaisedBuildingRoofs'})
   RETURN round(toFloat(c.RIGHT_PROPERTY_VALUE) - toFloat(c.LEFT_PROPERTY_VALUE), 3) AS dh_m
   ORDER BY dh_m ASC
   ```

   The sorted results are as follows:

   <div style="text-align: center;">

   |   | **dh_m (m)** |
      |---|--------------|
   | 1 | 0.008        |
   | 2 | 0.05         |
   | 3 | 0.12         |
   | 4 | 0.188        |
   | 5 | 0.251        |
   | 6 | 1.025        |

   </div>

8. To list all rule nodes used for all interpretations:

    ```cypher
    MATCH (r:RULE)
    RETURN r
    ```

   The results are as follows:

   <img src="resources/rules.svg" alt="Visualization of all rules" style="width:100%;">

9. To inspect the rules specifically for raised roofs:

   ```cypher
   MATCH (p)-[]->(r:RULE {change_type:'RaisedBuildingRoofs'})
   RETURN p, r
   ```

   In the figure below, the node in the center represents the rule `RaisedBuildingRoofs`, while the others represent:
    + `UpdatedBuildingMeasuredHeight`
    + `TranslatedBuildingRoofs`
    + `SizeChangedBuildingWalls`
    + `TranslatedBuildingGrounds`

   <div style="text-align: center;">
   <img src="resources/raised_roofs_rules.svg" alt="Visualization of rules for raised roofs" style="width:80%;">
   </div>

10. These rules are defined in Cypher. Please refer to this [Cypher file](scripts/rules_v2.cql) for more details.

## What's next

The following features will be added soon:

+ The ability to add **your own datasets** and run the tool on them.
+ A detailed syntactic **description** of how rule nodes can be defined.
+ The ability to **run** the mapping, matching, and interpretation processes separately.

<img src="resources/line.jpg" alt="Buildings with raised roofs" style="width:100%;">

## How to cite it

This tool is part of the following publications:

**Nguyen, Son H.; Kolbe, Thomas H.**: _Identification and Interpretation of Change Patterns in Semantic 3D City Models_.
Proceedings of the 18th International 3D GeoInfo Conference 2023, Springer Verlag, 2023. TBA.

**Nguyen, Son H.; Kolbe, Thomas H.**: [_Path-tracing Semantic Networks to Interpret Changes in Semantic 3D City
Models_](https://www.isprs-ann-photogramm-remote-sens-spatial-inf-sci.net/X-4-W2-2022/217/2022/).
Proceedings of the 17th International 3D GeoInfo Conference 2022 (ISPRS Annals of the Photogrammetry, Remote Sensing and
Spatial Information Sciences), ISPRS, 2022.

**Nguyen, Son H.; Kolbe, Thomas H.**: [_Modelling Changes, Stakeholders and their Relations in Semantic 3D City
Models_](https://www.isprs-ann-photogramm-remote-sens-spatial-inf-sci.net/VIII-4-W2-2021/137/2021/).
Proceedings of the 16th International 3D GeoInfo Conference 2021 (ISPRS Annals of the Photogrammetry, Remote Sensing and
Spatial Information Sciences), ISPRS, 2021, 137-144.

**Nguyen, Son H.; Kolbe, Thomas H.**: [_A Multi-Perspective Approach to Interpreting Spatio-Semantic Changes of Large 3D
City
Models in CityGML using a Graph
Database_](https://www.isprs-ann-photogramm-remote-sens-spatial-inf-sci.net/VI-4-W1-2020/143/2020/). Proceedings of the
15th International 3D GeoInfo Conference 2020 (ISPRS
Annals
of the Photogrammetry, Remote Sensing and Spatial Information Sciences), ISPRS, 2020, 143–150.

**Nguyen, Son H.; Yao, Zhihang; Kolbe, Thomas H.**: [_Spatio-Semantic Comparison of Large 3D City Models in CityGML
Using
a Graph
Database_](https://gispoint.de/artikelarchiv/gis/2018/gisscience-ausgabe-32018/4612-raeumlich-semantischer-vergleich-grosser-3d-stadtmodelle-in-citygml-unter-verwendung-einer-graph-datenbank.html).
gis.Science (3/2018), 2018, 85-100.

**Nguyen, Son H.; Yao, Zhihang; Kolbe, Thomas H.**: [_Spatio-Semantic Comparison of Large 3D City Models in CityGML
Using a Graph Database_](https://mediatum.ub.tum.de/doc/1425153/1425153.pdf). Proceedings of the 12th International 3D
GeoInfo Conference 2017 (ISPRS Annals of the Photogrammetry, Remote Sensing and Spatial Information Sciences), ISPRS,
2017, 99-106.

**Nguyen, Son H.**: [_Spatio-semantic Comparison of 3D City Models in CityGML using a Graph
Database_](https://mediatum.ub.tum.de/doc/1374646/1374646.pdf). Master thesis, 2017.
