# Change Log

### dev - Pending

Pending changes for the next dev release.

##### NEW

* **LoD Changes**: Added support for LoD changes in geometries,
  see [`e59b819`](https://github.com/tum-gis/citymodel-compare/commit/e59b81922dfff9a335cc442b6e4fd95087f8e91b).
  The difference in LoDs is calculated based on the highest available LoD of the two compared geometries.
  If such a deviation is detected, a corresponding change node is created and attached to the source nodes in the graph
  database.

* **CSV Export of Changes**: Detected changes are now exported to CSV files,
  see [`190ca36`](https://github.com/tum-gis/citymodel-compare/commit/190ca366c9156d32953ef0b84e5e6e7d1671edee).
  The location of the directory of these CSV files is specified in the config file in the directory `config`. 

### v1.0.0-dev - Released

This is a development release containing additional features, which extend from the
base [release v1.0.0](https://github.com/tum-gis/citymodel-compare/releases/tag/v1.0.0). The added features are
summarized as follows:

+ **Multi-module Support**: CityGML top-level features from most thematic modules, such as buildings, bridges, tunnels,
  vegetation, etc. can now be mapped and matched using the same graph database. Currently, city object groups are
  excluded from the matching process.

+ **3D Spatial Matching**: In addition to R-tree of 2D footprints, the height information is also considered while
  finding the best match for 3D CityGML top-level features, such as tunnels and trees.

+ **Accurate Bounding Boxes**: The bounding box of top-level features are normally given in the input CityGML documents.
  They may however be missing or not fully represent their geometric contents, especially in trees and city furniture
  with XLinks or implicit geometries (with transformation matrix and reference point). Since bounding boxes play a
  crucial role in subsequent analyses, the current implementation recalculates these bounding boxes after all XLinks
  have been resolved and replaces and stores the new bounding box also in the same graphs.

+ **Matching of Trees**: Trees often have very small footprints or bounding volume. Finding matching candidates solely
  based on their overlapping footprints or volumes may still result in a large number of potential matches. To minimize
  this number further, ideally to a single optimal match, additional checks are introduced, such as weighted values for
  class, species, height, and especially names of each tree.

+ **Other Improvements**: Additional improvements include:

    + The reconstruction methods can now produce the correct type for double and integer arrays directly, without
      relying on string casting.

    + Matching of attributed nodes now counts the number of their common properties with the same names and values.

    + Further refactoring and introduction of new helper functions.

### v1.0.0 - Released