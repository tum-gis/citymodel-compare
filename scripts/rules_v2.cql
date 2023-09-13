// ---------------------------------------------------------------------------------------------------------------------
// TODO Document this

// next_content_type
// The type of the next content node

// search_length
// Maximum search path length while traversing to the next content node.

// not_contains
// While searching for the next content type, do NOT traverse paths containing the given content type.

// conditions
// Conditions that must ALL be fulfilled, e.g., 'PROPERTY_NAME=id;RIGHT_PROPERTY_VALUE=$value'
// Can use variables such as $value. Variables are shared and matched among all converging edges.
// Can use the wildcard * to represent a fixed value FOR ALL objects, e.g., 'RIGHT_PROPERTY_VALUE=*'.
// In this case, the weight must be 1 or *.
// Variables and wildcards can be combined, e.g., 'RIGHT_PROPERTY_VALUE=$value;RIGHT_PROPERTY_VALUE=*'.

// weight
// The number of occurrences of the current change required to activate the next rule.
// Can be a number or the wildcard *, which means that ALL occurrences of current change are required.
// The weight 0 can be used as a NOT EXIST condition.
// ---------------------------------------------------------------------------------------------------------------------

// TODO Define a graph database schema for this structure?

// ---------------------------------------------------------------------------------------------------------------------

CALL{

// Rules for updated building IDs
MERGE (updated_prop:RULE {
  change_type: 'UpdatePropChange' // TODO These classes are from the implementation for changes
// TODO No need for tags here, as it is the literal change node
})-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.Building',
  search_length:     1,
  conditions:        'PROPERTY_NAME === "id"', // TODO Use the same syntax as in JavaScript, e.g., '(value1 == 1 || value2 == 2) && value3 == 3'
  propagate:         'PROPERTY_NAME',
  weight:            1
}]->(updated_building_id:RULE {
  change_type: 'UpdatedBuildingId',
  calc_scope:  'PROPERTY_NAME', // TODO Post calculation of scope
  tags:        'update;thematic;toplevel'
// TODO Scope is a special memory node. Memory node is updated every time, while scope is only updated when all previous aggregations are done.
// TODO Scope will automatically calculate and select the best spatial bounding shape (bbox, convex hull, circle, buffered zone, etc.)
// TODO Will calculate and store the bounding shape, as well as type_coverage, bounding_coverage, type_in_bounding_coverage -> these are used to determine the best bounding shape
})-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.core.CityModel',
  scope:             'global'
// TODO Save all properties and their values available in the current memory node in script engine (such as PROPERTY_NAMe and SCOPE) -> then eval
// TODO weight no longer needed when processing scope
}]->(global_updated_building_ids:RULE {
// TODO Remove epoch in implementation since FIRST LAST are no longer used
  change_type: 'GlobalUpdatedBuildingIds',
  tags:        'update;thematic;toplevel;scope'
})
MERGE (updated_building_id)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.core.CityModel',
  scope:             'clustered',
  propagate:         '*' // TODO Means ALL attributes of scope will be propagated
}]->(clustered_updated_building_ids:RULE {
  change_type: 'ClusteredUpdatedBuildingIds',
  tags:        'update;thematic;toplevel;scope'
})

// TODO bbox and circle are typically a result of a database modification action, while more complex shapes such as buffered zones are a result of changes in the real world (such as changes along a street, in a district)

// Rules for updated building creation dates
MERGE (updated_prop)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.Building',
  search_length:     1,
  conditions:        'PROPERTY_NAME === "creationDate"',
  propagate:         'RIGHT_PROPERTY_VALUE',
  weight:            1
}]->(updated_building_creation_date:RULE {
  change_type: 'UpdatedBuildingCreationDate',
  calc_scope:  'RIGHT_PROPERTY_VALUE',
  tags:        'update;thematic;toplevel'
})-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.core.CityModel',
  scope:             'global',
  propagate:         'RIGHT_PROPERTY_VALUE'
}]->(global_updated_building_creation_dates:RULE {
  change_type: 'GlobalUpdatedBuildingCreationDates',
  tags:        'update;thematic;toplevel;scope'
})
MERGE (updated_building_creation_date)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.core.CityModel',
  scope:             'clustered',
  propagate:         '*'
}]->(clustered_updated_building_creation_dates:RULE {
  change_type: 'ClusteredUpdatedBuildingCreationDates',
  tags:        'update;thematic;toplevel;scope'
})

// Rules for global updated buildings (with both updated IDs and creation dates)
MERGE (global_updated_building_ids)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.core.CityModel',
  name:              'rule_ids', // TODO
  weight:            1
}]->(global_updated_buildings:RULE {
  change_type: 'GlobalUpdatedBuildings',
  join:        'spatialEquals(rule_ids.scope.spatial, rule_creation_dates.scope.spatial)', // TODO
  tags:        'update;thematic;toplevel;scope'
})
MERGE (global_updated_building_creation_dates)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.core.CityModel',
  name:              'rule_creation_dates',
  weight:            1
}]->(global_updated_buildings)
// TODO This means that both rules must satisfy, any other changes will further increase the certainty, but are optional

// TODO Rules for updated building creationDates along a street

// TODO Rules for changes in a district? The scope bounding polygon is compared with predefined district polygons?

// Rules for updated building measured heights
MERGE (updated_prop)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.gml.measures.Length',
  search_length:     1,
  conditions:        'PROPERTY_NAME === "value"',
  propagate:         'LEFT_PROPERTY_VALUE;RIGHT_PROPERTY_VALUE',
  weight:            1
}]->(updated_measured_height:RULE {
  change_type: 'UpdatedMeasuredHeight',
  tags:        'update;thematic;measurement;vertical'
})-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.Building',
  search_length:     1,
  propagate:         'LEFT_PROPERTY_VALUE;RIGHT_PROPERTY_VALUE',
  weight:            1
}]->(updated_building_measured_height:RULE {
  change_type: 'UpdatedBuildingMeasuredHeight',
  calc_scope:  'LEFT_PROPERTY_VALUE;RIGHT_PROPERTY_VALUE',
  tags:        'update;thematic;measurement;vertical;toplevel'
})-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.core.CityModel',
  scope:             'global',
  propagate:         'LEFT_PROPERTY_VALUE;RIGHT_PROPERTY_VALUE'
}]->(global_updated_building_measured_heights:RULE {
  change_type: 'GlobalUpdatedBuildingMeasuredHeights',
  tags:        'update;thematic;measurement;vertical;toplevel;scope'
})
MERGE (scope_updated_building_measured_heights)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.core.CityModel',
  scope:             'clustered',
  propagate:         '*'
}]->(clustered_updated_building_measured_heights:RULE {
  change_type: 'ClusteredUpdatedBuildingMeasuredHeights',
  tags:        'update;thematic;measurement;vertical;toplevel;scope'
})

// ---------------------------------------------------------------------------------------------------------------------

// Rules for translated polygon surfaces
MERGE (translated:RULE {
  change_type: 'TranslationChange'
})-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.gml.geometry.primitives.SurfaceProperty',
  search_length:     1,
  propagate:         'x;y;z',
  weight:            1
}]->(translated_surface:RULE {
  change_type: 'TranslatedSurface',
  tags:        'translation;geometric;surface;'
})

// Rules for translated roofs
MERGE (translated_surface)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.RoofSurface',
  propagate:         'x;y;z',
  weight:            1
}]->(translated_roof:RULE {
  change_type: 'TranslatedRoof',
  tags:        'translation;geometric;surface;roof'
})
// Rules for translated walls
MERGE (translated_surface)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.WallSurface',
  propagate:         'x;y;z',
  weight:            1
}]->(translated_wall:RULE {
  change_type: 'TranslatedWall',
  tags:        'translation;geometric;surface;wall'
})
// Rules for translated grounds
MERGE (translated_surface)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.GroundSurface',
  propagate:         'x;y;z',
  weight:            1
}]->(translated_ground:RULE {
  change_type: 'TranslatedGround',
  tags:        'translation;geometric;surface;ground'
})

// Rules for translated building part roofs
MERGE (translated_roof)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.BuildingPart',
  conditions:        'x;y;z',
  propagate:         'x;y;z',
  weight:            '*'
}]->(translated_building_part_roofs:RULE {
  change_type: 'TranslatedBuildingPartRoofs',
  tags:        'translation;geometric;surface;roof;bpart'
})-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.Building',
  conditions:        'x;y;z',
  propagate:         'x;y;z', // TODO Union by names and round by values
  weight:            '*'
}]->(translated_building_roofs_with_bparts {
  change_type: 'TranslatedBuildingRoofs_WithBParts',
  tags:        'translation;geometric;surface;roof;bpart;toplevel'
})
// Rules for translated building part walls
MERGE (translated_wall)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.BuildingPart',
  conditions:        'x;y;z',
  propagate:         'x;y;z',
  weight:            '*'
}]->(translated_building_part_walls:RULE {
  change_type: 'TranslatedBuildingPartWalls',
  tags:        'translation;geometric;surface;wall;bpart'
})-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.Building',
  conditions:        'x;y;z',
  propagate:         'x;y;z',
  weight:            '*'
}]->(translated_building_walls_with_bparts {
  change_type: 'TranslatedBuildingWalls_WithBParts',
  tags:        'translation;geometric;surface;wall;bpart;toplevel'
})
// Rules for translated building part grounds
MERGE (translated_ground)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.BuildingPart',
  conditions:        'x;y;z',
  propagate:         'x;y;z',
  weight:            '*'
}]->(translated_building_part_grounds:RULE {
  change_type: 'TranslatedBuildingPartGrounds',
  tags:        'translation;geometric;surface;ground;bpart'
})-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.Building',
  conditions:        'x;y;z',
  propagate:         'x;y;z',
  weight:            '*'
}]->(translated_building_grounds_with_bparts {
  change_type: 'TranslatedBuildingGrounds_WithBParts',
  tags:        'translation;geometric;surface;ground;bpart;toplevel'
})

// Rules for translated building part
MERGE (translated_building_part_roofs)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.BuildingPart',
  name:              'rule_roofs',
  propagate:         'x;y;z',
  weight:            1
}]->(translated_building_part:RULE {
  change_type: 'TranslatedBuildingPart',
  join:        'fuzzyEquals(rule_roofs.x, rule_walls.x) && fuzzyEquals(rule_roofs.y, rule_walls.y) && fuzzyEquals(rule_roofs.z, rule_walls.z) && fuzzyEquals(rule_roofs.x, rule_grounds.x) && fuzzyEquals(rule_roofs.y, rule_grounds.y) && fuzzyEquals(rule_roofs.z, rule_grounds.z)',
  tags:        'translation;geometric;bpart'
})
MERGE (translated_building_part_walls)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.BuildingPart',
  name:              'rule_walls',
  propagate:         'x;y;z',
  weight:            1
}]->(translated_building_part)
MERGE (translated_building_part_grounds)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.BuildingPart',
  name:              'rule_grounds',
  propagate:         'x;y;z',
  weight:            1
}]->(translated_building_part)

// Rules for translated buildings (with building parts)
MERGE (translated_building_part)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.Building',
  conditions:        'x;y;z',
  propagate:         'x;y;z',
  weight:            '*'
}]->(translated_building_with_bparts:RULE {
  change_type: 'TranslatedBuilding_WithBParts',
  calc_scope:  'x;y;z',
  tags:        'translation;geometric;bpart;toplevel'
})-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.core.CityModel',
  scope:             'global',
  propagate:         'x;y;z'
}]->(global_translated_buildings_with_bparts:RULE {
  change_type: 'GlobalTranslatedBuildings_WithBParts',
  tags:        'translation;geometric;bpart;toplevel;scope'
})
MERGE (translated_building_with_bparts)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.core.CityModel',
  scope:             'clustered',
  propagate:         '*'
}]->(clustered_translated_buildings_with_bparts:RULE {
  change_type: 'ClusteredTranslatedBuildings_WithBParts',
  tags:        'translation;geometric;bpart;toplevel;scope'
})

// Rules for translated building surfaces (without building parts)
MERGE (translated_roof)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.Building',
  not_contains:      'org.citygml4j.model.citygml.building.BuildingPart',
  conditions:        'x;y;z',
  propagate:         'x;y;z',
  weight:            '*'
}]->(translated_building_roofs_no_bparts:RULE {
  change_type: 'TranslatedBuildingRoofs_NoBParts',
  tags:        'translation;geometric;surface;roof'
})
MERGE (translated_wall)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.Building',
  not_contains:      'org.citygml4j.model.citygml.building.BuildingPart',
  conditions:        'x;y;z',
  propagate:         'x;y;z',
  weight:            '*'
}]->(translated_building_walls_no_bparts:RULE {
  change_type: 'TranslatedBuildingWalls_NoBParts',
  tags:        'translation;geometric;surface;wall'
})
MERGE (translated_ground)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.Building',
  not_contains:      'org.citygml4j.model.citygml.building.BuildingPart',
  conditions:        'x;y;z',
  propagate:         'x;y;z',
  weight:            '*'
}]->(translated_building_grounds_no_bparts:RULE {
  change_type: 'TranslatedBuildingGrounds_NoBParts',
  tags:        'translation;geometric;surface;ground'
})

// Rules for translated building (without building parts)
MERGE (translated_building_roofs_no_bparts)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.Building',
  name:              'rule_roofs',
  propagate:         'x;y;z',
  weight:            1
}]->(translated_building_no_bparts:RULE {
  change_type: 'TranslatedBuilding_NoBParts',
  join:        'fuzzyEquals(rule_roofs.x, rule_walls.x) && fuzzyEquals(rule_roofs.y, rule_walls.y) && fuzzyEquals(rule_roofs.z, rule_walls.z) && fuzzyEquals(rule_roofs.x, rule_grounds.x) && fuzzyEquals(rule_roofs.y, rule_grounds.y) && fuzzyEquals(rule_roofs.z, rule_grounds.z)',
  calc_scope:  'x;y;z',
  tags:        'translation;geometric;toplevel'
})
MERGE (translated_building_walls_no_bparts)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.Building',
  name:              'rule_walls',
  propagate:         'x;y;z', // TODO Only properties that have the same values for all changes are propagated (this is recognized by the consistent use of variables in conditions across changes)
  weight:            1
}]->(translated_building_no_bparts)
MERGE (translated_building_grounds_no_bparts)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.Building',
  name:              'rule_grounds',
  propagate:         'x;y;z',
  weight:            1
}]->(translated_building_no_bparts)
MERGE (translated_building_no_bparts)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.core.CityModel',
  scope:             'global',
  propagate:         'x;y;z'
}]->(global_translated_buildings_no_bparts:RULE {
  change_type: 'GlobalTranslatedBuildings_NoBParts',
  tags:        'translation;geometric;toplevel;scope'
})
MERGE (translated_building_no_bparts)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.core.CityModel',
  scope:             'clustered',
  propagate:         '*'
}]->(clustered_translated_buildings_no_bparts:RULE {
  change_type: 'ClusteredTranslatedBuildings_NoBParts',
  tags:        'translation;geometric;toplevel;scope'
})

// Rules for translated buildings (with or without building parts)
MERGE (translated_building_with_bparts)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.Building',
  name:              'rule_with_bparts',
  propagate:         'x;y;z',
  weight:            1
}]->(translated_building:RULE {
  change_type: 'TranslatedBuildings',
  join:        'rule_with_bparts.satisfied || rule_no_bparts.satisfied', // TODO
// TODO Conditions in edges are evaluated first before conditions in nodes.
// TODO They can then be combined to form more complex logical expressions, such as (condition1 of edge1 AND condition2 of edge2) OR (condition1 of edge2 AND condition2 of edge2)
  calc_scope:  'x;y;z',
  tags:        'translation;geometric;toplevel'
})
MERGE (translated_building_no_bparts)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.Building',
  name:              'rule_no_bparts',
  propagate:         'x;y;z',
  weight:            1
}]->(translated_building)
MERGE (translated_building)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.core.CityModel',
  scope:             'global',
  propagate:         'x;y;z'
}]->(global_translated_buildings:RULE {
  change_type: 'GlobalTranslatedBuildings',
  tags:        'translation;geometric;toplevel;scope'
})
MERGE (translated_building)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.core.CityModel',
  scope:             'clustered',
  propagate:         '*'
}]->(clustered_translated_buildings:RULE {
  change_type: 'ClusteredTranslatedBuildings',
  tags:        'translation;geometric;toplevel;scope'
})

// TODO Document: The structure of the rule network becomes "wider" as higher levels are achieved,
// in contrast to the resulting network of the interpretation nodes (like two compatible combs)

// TODO Document: Properties of changes are stored in the counter/memory nodes and forwarded to next changes,
// based on conditions given in the rules

// ---------------------------------------------------------------------------------------------------------------------

// Rules for size-changed polygon surfaces
MERGE (:RULE {change_type: 'SizeChange'})-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.gml.geometry.primitives.SurfaceProperty',
  propagate:         'x;y;z',
  weight:            1
}]->(size_changed_surface:RULE {change_type: 'SizeChangedSurface'})

// Rules for size-changed boundary surfaces
MERGE (size_changed_surface)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.RoofSurface',
  propagate:         'x;y;z',
  weight:            1
}]->(size_changed_roof:RULE {change_type: 'SizeChangedRoof'})
MERGE (size_changed_surface)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.WallSurface',
  propagate:         'x;y;z',
  weight:            1
}]->(size_changed_wall:RULE {change_type: 'SizeChangedWall'})
MERGE (size_changed_surface)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.GroundSurface',
  propagate:         'x;y;z',
  weight:            1
}]->(size_changed_ground:RULE {change_type: 'SizeChangedGround'})

// Rules for size-changed building part roofs
MERGE (size_changed_roof)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.BuildingPart',
  conditions:        'x;y;z',
  weight:            '*'
}]->(size_changed_building_part_roofs:RULE {
  change_type: 'SizeChangedBuildingPartRoofs',
  tags:        'size;geometric;surface;roof;bpart'
})-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.Building',
  conditions:        'x;y;z',
  weight:            '*'
}]->(size_changed_building_roofs_with_bparts:RULE {
  change_type: 'SizeChangedBuildingRoofs_WithBParts',
  calc_scope:  'x;y;z',
  tags:        'size;geometric;surface;roof;bpart;toplevel'
})-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.core.CityModel',
  scope:             'global',
  propagate:         'x;y;z'
}]->(global_size_changed_building_roofs_with_bparts:RULE {
  change_type: 'GlobalSizeChangedBuildingRoofs_WithBParts',
  tags:        'size;geometric;surface;roof;bpart;toplevel;scope'
})
MERGE (size_changed_building_roofs_with_bparts)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.core.CityModel',
  scope:             'clustered',
  propagate:         '*'
}]->(clustered_size_changed_building_roofs_with_bparts:RULE {
  change_type: 'ClusteredSizeChangedBuildingRoofs_WithBParts',
  tags:        'size;geometric;surface;roof;bpart;toplevel;scope'
})

// Rules for size-changed building part walls
MERGE (size_changed_wall)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.BuildingPart',
  conditions:        'x;y;z',
  weight:            '*'
}]->(size_changed_building_part_walls:RULE {
  change_type: 'SizeChangedBuildingPartWalls',
  tags:        'size;geometric;surface;wall;bpart'
})-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.Building',
  conditions:        'x;y;z',
  weight:            '*'
}]->(size_changed_building_walls_with_bparts:RULE {
  change_type: 'SizeChangedBuildingWalls_WithBParts',
  calc_scope:  'x;y;z',
  tags:        'size;geometric;surface;wall;bpart;toplevel'
})-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.core.CityModel',
  scope:             'global',
  propagate:         'x;y;z'
}]->(global_size_changed_building_walls_with_bparts:RULE {
  change_type: 'GlobalSizeChangedBuildingWalls_WithBParts',
  tags:        'size;geometric;surface;wall;bpart;toplevel;scope'
})
MERGE (size_changed_building_walls_with_bparts)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.core.CityModel',
  scope:             'clustered',
  propagate:         '*'
}]->(clustered_size_changed_building_walls_with_bparts:RULE {
  change_type: 'ClusteredSizeChangedBuildingWalls_WithBParts',
  tags:        'size;geometric;surface;wall;bpart;toplevel;scope'
})

// Rules for size-changed building part grounds
MERGE (size_changed_ground)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.BuildingPart',
  conditions:        'x;y;z',
  weight:            '*'
}]->(size_changed_building_part_grounds:RULE {
  change_type: 'SizeChangedBuildingPartGrounds',
  tags:        'size;geometric;surface;ground;bpart'
})-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.Building',
  conditions:        'x;y;z',
  weight:            '*'
}]->(size_changed_building_grounds_with_bparts:RULE {
  change_type: 'SizeChangedBuildingGrounds_WithBParts',
  calc_scope:  'x;y;z',
  tags:        'size;geometric;surface;ground;bpart;toplevel'
})-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.core.CityModel',
  scope:             'global',
  propagate:         'x;y;z'
}]->(global_size_changed_building_grounds_with_bparts:RULE {
  change_type: 'GlobalSizeChangedBuildingGrounds_WithBParts',
  tags:        'size;geometric;surface;ground;bpart;toplevel;scope'
})
MERGE (size_changed_building_grounds_with_bparts)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.core.CityModel',
  scope:             'clustered',
  propagate:         '*'
}]->(clustered_size_changed_building_grounds_with_bparts:RULE {
  change_type: 'ClusteredSizeChangedBuildingGrounds_WithBParts',
  tags:        'size;geometric;surface;ground;bpart;toplevel;scope'
})

// Rules for size-changed building roof surfaces (without building parts)
MERGE (size_changed_roof)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.Building',
  not_contains:      'org.citygml4j.model.citygml.building.BuildingPart',
  conditions:        'x;y;z',
  weight:            '*'
}]->(size_changed_building_roofs_no_bparts:RULE {
  change_type: 'SizeChangedBuildingRoofs_NoBParts',
  calc_scope:  'x;y;z',
  tags:        'size;geometric;surface;roof;toplevel'
})-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.core.CityModel',
  scope:             'global',
  propagate:         'x;y;z'
}]->(global_size_changed_buildings_roofs_no_bparts:RULE {
  change_type: 'GlobalSizeChangedBuildingRoofs_NoBParts',
  tags:        'size;geometric;surface;roof;toplevel;scope'
})
MERGE (size_changed_building_roofs_no_bparts)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.core.CityModel',
  scope:             'clustered',
  propagate:         '*'
}]->(clustered_size_changed_buildings_roofs_no_bparts:RULE {
  change_type: 'ClusteredSizeChangedBuildingRoofs_NoBParts',
  tags:        'size;geometric;surface;roof;toplevel;scope'
})

// Rules for size-changed building wall surfaces (without building parts)
MERGE (size_changed_wall)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.Building',
  not_contains:      'org.citygml4j.model.citygml.building.BuildingPart',
  conditions:        'x;y;z',
  weight:            '*'
}]->(size_changed_building_walls_no_bparts:RULE {
  change_type: 'SizeChangedBuildingWalls_NoBParts',
  calc_scope:  'x;y;z',
  tags:        'size;geometric;surface;wall;toplevel'
})-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.core.CityModel',
  scope:             'global',
  propagate:         'x;y;z'
}]->(global_size_changed_buildings_walls_no_bparts:RULE {
  change_type: 'GlobalSizeChangedBuildingWalls_NoBParts',
  tags:        'size;geometric;surface;wall;toplevel;scope'
})
MERGE (size_changed_building_walls_no_bparts)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.core.CityModel',
  scope:             'clustered',
  propagate:         '*'
}]->(clustered_size_changed_buildings_walls_no_bparts:RULE {
  change_type: 'ClusteredSizeChangedBuildingWalls_NoBParts',
  tags:        'size;geometric;surface;wall;toplevel;scope'
})

// Rules for size-changed building ground surfaces (without building parts)
MERGE (size_changed_ground)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.Building',
  not_contains:      'org.citygml4j.model.citygml.building.BuildingPart',
  conditions:        'x;y;z',
  weight:            '*'
}]->(size_changed_building_grounds_no_bparts:RULE {
  change_type: 'SizeChangedBuildingGrounds_NoBParts',
  calc_scope:  'x;y;z',
  tags:        'size;geometric;surface;ground;toplevel'
})-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.core.CityModel',
  scope:             'global',
  propagate:         'x;y;z'
}]->(global_size_changed_building_grounds_no_bparts:RULE {
  change_type: 'GlobalSizeChangedBuildingGrounds_NoBParts',
  tags:        'size;geometric;surface;ground;toplevel;scope'
})
MERGE (size_changed_building_grounds_no_bparts)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.core.CityModel',
  scope:             'clustered ',
  propagate:         '*'
}]->(clustered_size_changed_buildings_grounds_no_bparts:RULE {
  change_type: 'ClusteredSizeChangedBuildingGrounds_NoBParts',
  tags:        'size;geometric;surface;ground;toplevel;scope'
})

// ---------------------------------------------------------------------------------------------------------------------

// Rules for raised building roofs
MERGE (updated_building_measured_height)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.Building',
  name:              'rule_measured_height',
  conditions:        'RIGHT_PROPERTY_VALUE - LEFT_PROPERTY_VALUE > 0',
  propagate:         'LEFT_PROPERTY_VALUE;RIGHT_PROPERTY_VALUE', // TODO Option to rename properties (in case edges have the same named properties)
  weight:            '1'
}]->(raised_building_roofs:RULE {
  change_type: 'RaisedBuildingRoofs',
  join:        'fuzzyEquals(parseFloat(rule_measured_height.RIGHT_PROPERTY_VALUE) - parseFloat(rule_measured_height.LEFT_PROPERTY_VALUE), rule_size_changed_walls.z) && fuzzyEquals(rule_translated_roofs.z, parseFloat(rule_size_changed_walls.z) + parseFloat(rule_translated_grounds.z))',
  calc_scope:  'x;y;z',
  tags:        'update;thematic;measurement;geometric;vertical;toplevel'
})
MERGE (translated_building_roofs_no_bparts)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.Building',
  name:              'rule_translated_roofs',
  conditions:        'fuzzyEquals(x, 0) && fuzzyEquals(y, 0)',
  propagate:         'x;y;z',
  weight:            '1'
}]->(raised_building_roofs)
MERGE (translated_building_grounds_no_bparts)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.Building',
  name:              'rule_translated_grounds',
  conditions:        'fuzzyEquals(x, 0) && fuzzyEquals(y, 0)',
  propagate:         'x;y;z',
  weight:            '1'
}]->(raised_building_roofs)
MERGE (size_changed_building_walls_no_bparts)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.building.Building',
  name:              'rule_size_changed_walls',
  conditions:        'fuzzyEquals(x, 0) && fuzzyEquals(y, 0)',
  propagate:         'x;y;z',
  weight:            '1'
}]->(raised_building_roofs)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.core.CityModel',
  scope:             'global',
  propagate:         'z'
}]->(global_raised_building_roofs:RULE {
  change_type: 'GlobalRaisedBuildingRoofs',
  tags:        'update;thematic;measurement;geometric;vertical;toplevel;scope'
})
MERGE (raised_building_roofs)-[:AGGREGATED_TO {
  next_content_type: 'org.citygml4j.model.citygml.core.CityModel',
  scope:             'clustered',
  propagate:         '*'
}]->(clustered_raised_building_roofs:RULE {
  change_type: 'ClusteredRaisedBuildingRoofs',
  tags:        'update;thematic;measurement;geometric;vertical;toplevel;scope'
})

}

// ---------------------------------------------------------------------------------------------------------------------

CALL {
MATCH (rule:RULE)
RETURN count(rule) AS result
}

RETURN result
