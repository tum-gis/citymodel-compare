// Change long labels to shorter ones (for CityGML 3.0)

// Building
MATCH (b:`org.citygml4j.core.model.building.Building`)
REMOVE b:`org.citygml4j.core.model.building.Building`
SET b:Building

// Index Building
CREATE INDEX simple_building_id IF NOT EXISTS
FOR (n:Building) ON (n.id)

// BuildingRoom
MATCH (b:`org.citygml4j.core.model.building.BuildingRoom`)
REMOVE b:`org.citygml4j.core.model.building.BuildingRoom`
SET b:BuildingRoom

// Index BuildingRoom
CREATE INDEX simple_building_room_id IF NOT EXISTS
FOR (n:BuildingRoom) ON (n.id)

// Door
MATCH (b:`org.citygml4j.core.model.construction.Door`)
REMOVE b:`org.citygml4j.core.model.construction.Door`
SET b:Door

// Index Door
CREATE INDEX simple_door_id IF NOT EXISTS
FOR (n:Door) ON (n.id)

// Window
MATCH (b:`org.citygml4j.core.model.construction.Window`)
REMOVE b:`org.citygml4j.core.model.construction.Window`
SET b:Window

// Index Window
CREATE INDEX simple_window_id IF NOT EXISTS
FOR (n:Window) ON (n.id)

// Code
MATCH (b:`org.xmlobjects.gml.model.basictypes.Code`)
REMOVE b:`org.xmlobjects.gml.model.basictypes.Code`
SET b:Code

// Area
MATCH (b:`org.xmlobjects.gml.model.measures.Area`)
REMOVE b:`org.xmlobjects.gml.model.measures.Area`
SET b:Area

// Volume
MATCH (b:`org.xmlobjects.gml.model.measures.Volume`)
REMOVE b:`org.xmlobjects.gml.model.measures.Volume`
SET b:Volume
