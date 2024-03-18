<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<!-- written by citygml4j -->
<!-- using a CityGMLWriter instance -->
<!-- Split mode: org.citygml4j.featureWriteMode.noSplit -->
<!-- Split on copy: false -->
<CityModel xmlns:xAL="urn:oasis:names:tc:ciq:xsdschema:xAL:2.0" xmlns:gml="http://www.opengis.net/gml" xmlns:wtr="http://www.opengis.net/citygml/waterbody/2.0" xmlns:app="http://www.opengis.net/citygml/appearance/2.0" xmlns:tex="http://www.opengis.net/citygml/texturedsurface/2.0" xmlns="http://www.opengis.net/citygml/2.0" xmlns:veg="http://www.opengis.net/citygml/vegetation/2.0" xmlns:dem="http://www.opengis.net/citygml/relief/2.0" xmlns:tran="http://www.opengis.net/citygml/transportation/2.0" xmlns:bldg="http://www.opengis.net/citygml/building/2.0" xmlns:grp="http://www.opengis.net/citygml/cityobjectgroup/2.0" xmlns:tun="http://www.opengis.net/citygml/tunnel/2.0" xmlns:frn="http://www.opengis.net/citygml/cityfurniture/2.0" xmlns:brid="http://www.opengis.net/citygml/bridge/2.0" xmlns:gen="http://www.opengis.net/citygml/generics/2.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:luse="http://www.opengis.net/citygml/landuse/2.0">
  <gml:name>AC14-FZK-Haus</gml:name>
  <gml:boundedBy>
    <gml:Envelope srsName="urn:adv:crs:ETRS89_UTM32*DE_DHHN92_NH" srsDimension="3">
      <gml:lowerCorner srsDimension="3">457842.0 5439083.0 111.8</gml:lowerCorner>
      <gml:upperCorner srsDimension="3">457854.0 5439093.0 118.317669</gml:upperCorner>
    </gml:Envelope>
  </gml:boundedBy>
  <cityObjectMember>
    <bldg:Building gml:id="UUID_d281adfc-4901-0f52-540b-4cc1a9325f82">
      <gml:description>FZK-Haus (Forschungszentrum Karlsruhe, now KIT), created by Karl-Heinz
                Haefele
            </gml:description>
      <gml:name>AC14-FZK-Haus</gml:name>
      <gml:boundedBy>
        <gml:Envelope srsDimension="3">
          <gml:lowerCorner>457841.5 5439082.5 111.8</gml:lowerCorner>
          <gml:upperCorner>457854.5 5439093.5 111.8</gml:upperCorner>
        </gml:Envelope>
      </gml:boundedBy>
      <creationDate>2017-01-23</creationDate>
      <relativeToTerrain>entirelyAboveTerrain</relativeToTerrain>
      <gen:measureAttribute name="GrossPlannedArea">
        <gen:value uom="m2">120.0</gen:value>
      </gen:measureAttribute>
      <gen:stringAttribute name="ConstructionMethod">
        <gen:value>New Building</gen:value>
      </gen:stringAttribute>
      <gen:stringAttribute name="IsLandmarked">
        <gen:value>NO</gen:value>
      </gen:stringAttribute>
      <bldg:class codeSpace="http://www.sig3d.org/codelists/citygml/2.0/building/2.0/_AbstractBuilding_class.xml">
                1000
            </bldg:class>
      <bldg:function codeSpace="http://www.sig3d.org/codelists/citygml/2.0/building/2.0/_AbstractBuilding_function.xml">
                1000
            </bldg:function>
      <bldg:usage codeSpace="http://www.sig3d.org/codelists/citygml/2.0/building/2.0/_AbstractBuilding_usage.xml">
                1000
            </bldg:usage>
      <bldg:yearOfConstruction>2020</bldg:yearOfConstruction>
      <bldg:roofType codeSpace="http://www.sig3d.org/codelists/citygml/2.0/building/2.0/_AbstractBuilding_roofType.xml">
                1030
            </bldg:roofType>
      <bldg:measuredHeight uom="m">6.52</bldg:measuredHeight>
      <bldg:storeysAboveGround>2</bldg:storeysAboveGround>
      <bldg:storeysBelowGround>0</bldg:storeysBelowGround>
      <bldg:lod0FootPrint>
        <gml:MultiSurface>
          <gml:surfaceMember>
            <gml:Polygon>
              <gml:exterior>
                <gml:LinearRing>
                  <gml:posList srsDimension="3">457842.0 5439083.0 111.8 457842.0 5439093.0 111.8 457854.0 5439093.0 111.8 457854.0 5439083.0 111.8 457842.0 5439083.0 111.8</gml:posList>
                </gml:LinearRing>
              </gml:exterior>
            </gml:Polygon>
          </gml:surfaceMember>
        </gml:MultiSurface>
      </bldg:lod0FootPrint>
      <bldg:lod0RoofEdge>
        <gml:MultiSurface>
          <gml:surfaceMember>
            <gml:Polygon>
              <gml:exterior>
                <gml:LinearRing>
                  <gml:posList srsDimension="3">457841.5 5439082.5 111.8 457841.5 5439093.5 111.8 457854.5 5439093.5 111.8 457854.5 5439082.5 111.8 457841.5 5439082.5 111.8</gml:posList>
                </gml:LinearRing>
              </gml:exterior>
            </gml:Polygon>
          </gml:surfaceMember>
        </gml:MultiSurface>
      </bldg:lod0RoofEdge>
      <bldg:address>
        <Address>
          <xalAddress>
            <xAL:AddressDetails>
              <xAL:Locality Type="Town">
                <xAL:LocalityName>Eggenstein-Leopoldshafen</xAL:LocalityName>
                <xAL:Thoroughfare Type="Street">
                  <xAL:ThoroughfareNumber>4711</xAL:ThoroughfareNumber>
                  <xAL:ThoroughfareName>Spöcker Straße</xAL:ThoroughfareName>
                </xAL:Thoroughfare>
                <xAL:PostalCode>
                  <xAL:PostalCodeNumber>76344</xAL:PostalCodeNumber>
                </xAL:PostalCode>
              </xAL:Locality>
            </xAL:AddressDetails>
          </xalAddress>
        </Address>
      </bldg:address>
    </bldg:Building>
  </cityObjectMember>
</CityModel>