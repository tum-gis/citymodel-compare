// Change long labels to shorter ones (for CityGML 3.0)

// Change
MATCH (n:`jgraf.neo4j.diff.Change`)
REMOVE n:`jgraf.neo4j.diff.Change`
SET n:Change

// Index Change
CREATE INDEX simple_change_change_type IF NOT EXISTS
FOR (n:Change) ON (n.change_type)

// InsertNodeChange
MATCH (n:`jgraf.neo4j.diff.InsertNodeChange`)
REMOVE n:`jgraf.neo4j.diff.InsertNodeChange`
SET n:InsertNodeChange

// DeleteNodeChange
MATCH (n:`jgraf.neo4j.diff.DeleteNodeChange`)
REMOVE n:`jgraf.neo4j.diff.DeleteNodeChange`
SET n:DeleteNodeChange

// InsertPropChange
MATCH (n:`jgraf.neo4j.diff.InsertPropChange`)
REMOVE n:`jgraf.neo4j.diff.InsertPropChange`
SET n:InsertPropChange

// DeletePropChange
MATCH (n:`jgraf.neo4j.diff.DeletePropChange`)
REMOVE n:`jgraf.neo4j.diff.DeletePropChange`
SET n:DeletePropChange

// UpdatePropChange
MATCH (n:`jgraf.neo4j.diff.UpdatePropChange`)
REMOVE n:`jgraf.neo4j.diff.UpdatePropChange`
SET n:UpdatePropChange

// SizeChange
MATCH (n:`jgraf.neo4j.diff.SizeChange`)
REMOVE n:`jgraf.neo4j.diff.SizeChange`
SET n:SizeChange

// TranslationChange
MATCH (n:`jgraf.neo4j.diff.TranslationChange`)
REMOVE n:`jgraf.neo4j.diff.TranslationChange`
SET n:TranslationChange

// TopSplitChange
MATCH (n:`jgraf.neo4j.diff.TopSplitChange`)
REMOVE n:`jgraf.neo4j.diff.TopSplitChange`
SET n:TopSplitChange

// CityModel
MATCH (n:`org.citygml4j.model.citygml.core.CityModel`)
REMOVE n:`org.citygml4j.model.citygml.core.CityModel`
SET n:CityModel

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
