// Change long labels to shorter ones (for CityGML 3.0)

// Building
MATCH (n:`org.citygml4j.model.citygml.building.Building`)
REMOVE n:`org.citygml4j.model.citygml.building.Building`
SET n:Building

// Index Building
CREATE INDEX simple_building_id IF NOT EXISTS
FOR (n:Building) ON (n.id)

// BuildingPart
MATCH (n:`org.citygml4j.model.citygml.building.BuildingPart`)
REMOVE n:`org.citygml4j.model.citygml.building.BuildingPart`
SET n:BuildingPart

// Index BuildingPart
CREATE INDEX simple_buildingpart_id IF NOT EXISTS
FOR (n:BuildingPart) ON (n.id)

// GroundSurface
MATCH (n:`org.citygml4j.model.citygml.building.GroundSurface`)
REMOVE n:`org.citygml4j.model.citygml.building.GroundSurface`
SET n:GroundSurface

// WallSurface
MATCH (n:`org.citygml4j.model.citygml.building.WallSurface`)
REMOVE n:`org.citygml4j.model.citygml.building.WallSurface`
SET n:WallSurface

// RoofSurface
MATCH (n:`org.citygml4j.model.citygml.building.RoofSurface`)
REMOVE n:`org.citygml4j.model.citygml.building.RoofSurface`
SET n:RoofSurface

// Polygon
MATCH (n:`org.citygml4j.model.gml.geometry.primitives.Polygon`)
REMOVE n:`org.citygml4j.model.gml.geometry.primitives.Polygon`
SET n:Polygon

// Index Polygon
CREATE INDEX simple_polygon_id IF NOT EXISTS
FOR (n:Polygon) ON (n.id)
