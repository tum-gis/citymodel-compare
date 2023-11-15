...
<core:cityObjectMember>
    <bldg:Building gml:id="FZK_HAUS_LOD2">
        <bldg:lod2Solid>
            <gml:Solid>
                <gml:exterior>
                    <gml:CompositeSurface>
                        <!-- XLink references to the surfaces -->
                        <gml:surfaceMember xlink:href="#Roof_Surface_1_North"/>
                        <gml:surfaceMember xlink:href="#Roof_Surface_2_South"/>
                        <gml:surfaceMember xlink:href="#Outer_Wall_Surface_1_West"/>
                        <gml:surfaceMember xlink:href="#Outer_Wall_Surface_2_South"/>
                        <gml:surfaceMember xlink:href="#Outer_Wall_Surface_3_East"/>
                        <gml:surfaceMember xlink:href="#Outer_Wall_Surface_4_North"/>
                        <gml:surfaceMember xlink:href="#Ground_Surface"/>
                    </gml:CompositeSurface>
                </gml:exterior>
            </gml:Solid>
        </bldg:lod2Solid>
        <bldg:boundedBy>
            <!-- Content of Roof Surface 1 (North) -->
            <bldg:RoofSurface gml:id="...">
                <bldg:lod2MultiSurface>
                    <gml:MultiSurface>
                        <gml:surfaceMember>
                            <gml:Polygon gml:id="Roof_Surface_1_North">
                                ...
                            </gml:Polygon>
                        </gml:surfaceMember>
                    </gml:MultiSurface>
                </bldg:lod2MultiSurface>
            </bldg:RoofSurface>
        </bldg:boundedBy>
        ... <!-- Content of Roof Surface 2 (South) -->
        <bldg:boundedBy>
            <!-- Content of Wall Surface 1 (West) -->
            <bldg:WallSurface gml:id="...">
                <bldg:lod2MultiSurface>
                    <gml:MultiSurface>
                        <gml:surfaceMember>
                            <gml:Polygon gml:id="Outer_Wall_Surface_1_West">
                                ...
                            </gml:Polygon>
                        </gml:surfaceMember>
                    </gml:MultiSurface>
                </bldg:lod2MultiSurface>
            </bldg:WallSurface>
        </bldg:boundedBy>
        ... <!-- Content of Wall Surface 2 (South) -->
        ... <!-- Content of Wall Surface 3 (East) -->
        ... <!-- Content of Wall Surface 4 (North) -->
        <bldg:boundedBy>
            <!-- Content of Ground Surface -->
            <bldg:GroundSurface gml:id="...">
                <bldg:lod2MultiSurface>
                    <gml:MultiSurface>
                        <gml:surfaceMember>
                            <gml:Polygon gml:id="Ground_Surface">
                                ...
                            </gml:Polygon>
                        </gml:surfaceMember>
                    </gml:MultiSurface>
                </bldg:lod2MultiSurface>
            </bldg:GroundSurface>
        </bldg:boundedBy>
    </bldg:Building>
</core:cityObjectMember>
...