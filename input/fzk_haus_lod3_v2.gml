<?xml version="1.0" encoding="utf-8"?><!-- IFC to CityGML by IFCExplorer KIT --><!-- CityGML to Sketchup by  Sketchup CityGML Plugin FH GelsenKirchen --><!--CityGML Dataset produced with CityGML Export Plugin for Sketchup by GeoRES --><!--http://www.geores.de --><!-- Edited Manually in Oxygen 8.2 --><!-- Modified by GMLOffset.xslt at Mon Dec 6 2010 --><!-- Version 2 Building located in the area of KIT Campus North)--><!-- Modified by GMLOffset.xslt at Wed Dec 8 2010 --><!-- Modified by GMLOffset.xslt at Wed Mar 29 2017 -->
<core:CityModel
        xsi:schemaLocation="http://www.opengis.net/citygml/2.0 http://schemas.opengis.net/citygml/2.0/cityGMLBase.xsd  http://www.opengis.net/citygml/appearance/2.0 http://schemas.opengis.net/citygml/appearance/2.0/appearance.xsd http://www.opengis.net/citygml/building/2.0 http://schemas.opengis.net/citygml/building/2.0/building.xsd http://www.opengis.net/citygml/generics/2.0 http://schemas.opengis.net/citygml/generics/2.0/generics.xsd"
        xmlns:core="http://www.opengis.net/citygml/2.0" xmlns="http://www.opengis.net/citygml/profiles/base/2.0"
        xmlns:bldg="http://www.opengis.net/citygml/building/2.0" xmlns:gen="http://www.opengis.net/citygml/generics/2.0"
        xmlns:grp="http://www.opengis.net/citygml/cityobjectgroup/2.0"
        xmlns:app="http://www.opengis.net/citygml/appearance/2.0" xmlns:gml="http://www.opengis.net/gml"
        xmlns:xAL="urn:oasis:names:tc:ciq:xsdschema:xAL:2.0" xmlns:xlink="http://www.w3.org/1999/xlink"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <!-- Manually edited by KHH 23.01.2017, CityGML 2.0, Address added, Codespaces added -->
    <gml:name>AC14-FZK-Haus</gml:name>
    <gml:boundedBy>
        <gml:Envelope srsDimension="3" srsName="urn:adv:crs:ETRS89_UTM32*DE_DHHN92_NH">
            <gml:lowerCorner srsDimension="3">457841.5 5439082.5 111.8</gml:lowerCorner>
            <gml:upperCorner srsDimension="3">457854.5 5439093.5 118.317669</gml:upperCorner>
        </gml:Envelope>
    </gml:boundedBy>
    <core:cityObjectMember>
        <bldg:Building gml:id="UUID_d281adfc-4901-0f52-540b-4cc1a9325f82">
            <gml:description>FZK-Haus (Forschungszentrum Karlsruhe, now KIT), created by Karl-Heinz
                Haefele
            </gml:description>
            <gml:name>AC14-FZK-Haus</gml:name>
            <core:creationDate>2017-01-23</core:creationDate>
            <core:relativeToTerrain>entirelyAboveTerrain</core:relativeToTerrain>
            <gen:measureAttribute name="GrossPlannedArea">
                <gen:value uom="m2">120.00</gen:value>
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
            <bldg:function
                    codeSpace="http://www.sig3d.org/codelists/citygml/2.0/building/2.0/_AbstractBuilding_function.xml">
                1000
            </bldg:function>
            <bldg:usage codeSpace="http://www.sig3d.org/codelists/citygml/2.0/building/2.0/_AbstractBuilding_usage.xml">
                1000
            </bldg:usage>
            <bldg:yearOfConstruction>2020</bldg:yearOfConstruction>
            <bldg:roofType
                    codeSpace="http://www.sig3d.org/codelists/citygml/2.0/building/2.0/_AbstractBuilding_roofType.xml">
                1030
            </bldg:roofType>
            <bldg:measuredHeight uom="m">6.52</bldg:measuredHeight>
            <bldg:storeysAboveGround>2</bldg:storeysAboveGround>
            <bldg:storeysBelowGround>0</bldg:storeysBelowGround>
            <bldg:boundedBy>
                <bldg:WallSurface gml:id="GML_5856d7ad-5e34-498a-817b-9544bfbb1475">
                    <gml:name>Outer Wall 1 (West)</gml:name>
                    <bldg:lod3MultiSurface>
                        <gml:MultiSurface>
                            <gml:surfaceMember>
                                <gml:CompositeSurface gml:id="GML_e9240361-e956-421c-bff5-1f1f6d9b59aa">
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58718_509_420914_99840">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58718_509_420914_99840_0">
                                                    <gml:pos>457842 5439088.49240388 115.913175911167</gml:pos>
                                                    <gml:pos>457842.12 5439088.49240388 115.913175911167</gml:pos>
                                                    <gml:pos>457842.12 5439088.49809735 115.956422128626</gml:pos>
                                                    <gml:pos>457842 5439088.49809735 115.956422128626</gml:pos>
                                                    <gml:pos>457842 5439088.49240388 115.913175911167</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58719_1668_843061_5809">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58719_1668_843061_5809_0">
                                                    <gml:pos>457842 5439088.49809735 115.956422128626</gml:pos>
                                                    <gml:pos>457842.12 5439088.49809735 115.956422128626</gml:pos>
                                                    <gml:pos>457842.12 5439088.5 116</gml:pos>
                                                    <gml:pos>457842 5439088.5 116</gml:pos>
                                                    <gml:pos>457842 5439088.49809735 115.956422128626</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58720_254_451830_156398">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58720_254_451830_156398_0">
                                                    <gml:pos>457842 5439088.5 116</gml:pos>
                                                    <gml:pos>457842.12 5439088.5 116</gml:pos>
                                                    <gml:pos>457842.12 5439088.49809735 116.043577871374</gml:pos>
                                                    <gml:pos>457842 5439088.49809735 116.043577871374</gml:pos>
                                                    <gml:pos>457842 5439088.5 116</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58721_1706_427521_368985">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58721_1706_427521_368985_0">
                                                    <gml:pos>457842 5439088.49809735 116.043577871374</gml:pos>
                                                    <gml:pos>457842.12 5439088.49809735 116.043577871374</gml:pos>
                                                    <gml:pos>457842.12 5439088.49240388 116.086824088833</gml:pos>
                                                    <gml:pos>457842 5439088.49240388 116.086824088833</gml:pos>
                                                    <gml:pos>457842 5439088.49809735 116.043577871374</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58722_224_323902_252423">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58722_224_323902_252423_0">
                                                    <gml:pos>457842 5439088.49240388 116.086824088833</gml:pos>
                                                    <gml:pos>457842.12 5439088.49240388 116.086824088833</gml:pos>
                                                    <gml:pos>457842.12 5439088.48296291 116.129409522551</gml:pos>
                                                    <gml:pos>457842 5439088.48296291 116.129409522551</gml:pos>
                                                    <gml:pos>457842 5439088.49240388 116.086824088833</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58723_439_431781_373725">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58723_439_431781_373725_0">
                                                    <gml:pos>457842 5439088.48296291 116.129409522551</gml:pos>
                                                    <gml:pos>457842.12 5439088.48296291 116.129409522551</gml:pos>
                                                    <gml:pos>457842.12 5439088.46984631 116.171010071663</gml:pos>
                                                    <gml:pos>457842 5439088.46984631 116.171010071663</gml:pos>
                                                    <gml:pos>457842 5439088.48296291 116.129409522551</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58724_1967_868315_208008">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58724_1967_868315_208008_0">
                                                    <gml:pos>457842 5439088.46984631 116.171010071663</gml:pos>
                                                    <gml:pos>457842.12 5439088.46984631 116.171010071663</gml:pos>
                                                    <gml:pos>457842.12 5439088.45315389 116.21130913087</gml:pos>
                                                    <gml:pos>457842 5439088.45315389 116.21130913087</gml:pos>
                                                    <gml:pos>457842 5439088.46984631 116.171010071663</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58725_1421_209484_393490">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58725_1421_209484_393490_0">
                                                    <gml:pos>457842 5439088.45315389 116.21130913087</gml:pos>
                                                    <gml:pos>457842.12 5439088.45315389 116.21130913087</gml:pos>
                                                    <gml:pos>457842.12 5439088.4330127 116.25</gml:pos>
                                                    <gml:pos>457842 5439088.4330127 116.25</gml:pos>
                                                    <gml:pos>457842 5439088.45315389 116.21130913087</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58726_671_554234_212436">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58726_671_554234_212436_0">
                                                    <gml:pos>457842 5439088.4330127 116.25</gml:pos>
                                                    <gml:pos>457842.12 5439088.4330127 116.25</gml:pos>
                                                    <gml:pos>457842.12 5439088.40957602 116.286788218176</gml:pos>
                                                    <gml:pos>457842 5439088.40957602 116.286788218176</gml:pos>
                                                    <gml:pos>457842 5439088.4330127 116.25</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58727_657_504736_78856">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58727_657_504736_78856_0">
                                                    <gml:pos>457842 5439088.40957602 116.286788218176</gml:pos>
                                                    <gml:pos>457842.12 5439088.40957602 116.286788218176</gml:pos>
                                                    <gml:pos>457842.12 5439088.38302222 116.321393804843</gml:pos>
                                                    <gml:pos>457842 5439088.38302222 116.321393804843</gml:pos>
                                                    <gml:pos>457842 5439088.40957602 116.286788218176</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58728_577_102541_299483">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58728_577_102541_299483_0">
                                                    <gml:pos>457842 5439088.38302222 116.321393804843</gml:pos>
                                                    <gml:pos>457842.12 5439088.38302222 116.321393804843</gml:pos>
                                                    <gml:pos>457842.12 5439088.35355339 116.353553390593</gml:pos>
                                                    <gml:pos>457842 5439088.35355339 116.353553390593</gml:pos>
                                                    <gml:pos>457842 5439088.38302222 116.321393804843</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58729_1200_447052_104531">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58729_1200_447052_104531_0">
                                                    <gml:pos>457842 5439088.35355339 116.353553390593</gml:pos>
                                                    <gml:pos>457842.12 5439088.35355339 116.353553390593</gml:pos>
                                                    <gml:pos>457842.12 5439088.3213938 116.383022221559</gml:pos>
                                                    <gml:pos>457842 5439088.3213938 116.383022221559</gml:pos>
                                                    <gml:pos>457842 5439088.35355339 116.353553390593</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58730_1683_860946_68139">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58730_1683_860946_68139_0">
                                                    <gml:pos>457842 5439088.3213938 116.383022221559</gml:pos>
                                                    <gml:pos>457842.12 5439088.3213938 116.383022221559</gml:pos>
                                                    <gml:pos>457842.12 5439088.28678822 116.409576022145</gml:pos>
                                                    <gml:pos>457842 5439088.28678822 116.409576022145</gml:pos>
                                                    <gml:pos>457842 5439088.3213938 116.383022221559</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58731_1219_480209_127236">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58731_1219_480209_127236_0">
                                                    <gml:pos>457842 5439088.28678822 116.409576022145</gml:pos>
                                                    <gml:pos>457842.12 5439088.28678822 116.409576022145</gml:pos>
                                                    <gml:pos>457842.12 5439088.25 116.433012701892</gml:pos>
                                                    <gml:pos>457842 5439088.25 116.433012701892</gml:pos>
                                                    <gml:pos>457842 5439088.28678822 116.409576022145</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58732_300_736728_134369">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58732_300_736728_134369_0">
                                                    <gml:pos>457842 5439088.25 116.433012701892</gml:pos>
                                                    <gml:pos>457842.12 5439088.25 116.433012701892</gml:pos>
                                                    <gml:pos>457842.12 5439088.21130913 116.453153893518</gml:pos>
                                                    <gml:pos>457842 5439088.21130913 116.453153893518</gml:pos>
                                                    <gml:pos>457842 5439088.25 116.433012701892</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58733_1677_827795_100700">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58733_1677_827795_100700_0">
                                                    <gml:pos>457842 5439088.21130913 116.453153893518</gml:pos>
                                                    <gml:pos>457842.12 5439088.21130913 116.453153893518</gml:pos>
                                                    <gml:pos>457842.12 5439088.17101007 116.469846310393</gml:pos>
                                                    <gml:pos>457842 5439088.17101007 116.469846310393</gml:pos>
                                                    <gml:pos>457842 5439088.21130913 116.453153893518</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58734_443_173915_418248">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58734_443_173915_418248_0">
                                                    <gml:pos>457842 5439088.17101007 116.469846310393</gml:pos>
                                                    <gml:pos>457842.12 5439088.17101007 116.469846310393</gml:pos>
                                                    <gml:pos>457842.12 5439088.12940952 116.482962913145</gml:pos>
                                                    <gml:pos>457842 5439088.12940952 116.482962913145</gml:pos>
                                                    <gml:pos>457842 5439088.17101007 116.469846310393</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58735_61_408337_179410">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58735_61_408337_179410_0">
                                                    <gml:pos>457842 5439088.12940952 116.482962913145</gml:pos>
                                                    <gml:pos>457842.12 5439088.12940952 116.482962913145</gml:pos>
                                                    <gml:pos>457842.12 5439088.08682409 116.492403876506</gml:pos>
                                                    <gml:pos>457842 5439088.08682409 116.492403876506</gml:pos>
                                                    <gml:pos>457842 5439088.12940952 116.482962913145</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58736_1099_849209_286672">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58736_1099_849209_286672_0">
                                                    <gml:pos>457842 5439088.08682409 116.492403876506</gml:pos>
                                                    <gml:pos>457842.12 5439088.08682409 116.492403876506</gml:pos>
                                                    <gml:pos>457842.12 5439088.04357787 116.498097349046</gml:pos>
                                                    <gml:pos>457842 5439088.04357787 116.498097349046</gml:pos>
                                                    <gml:pos>457842 5439088.08682409 116.492403876506</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58737_1064_836277_325527">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58737_1064_836277_325527_0">
                                                    <gml:pos>457842 5439088.04357787 116.498097349046</gml:pos>
                                                    <gml:pos>457842.12 5439088.04357787 116.498097349046</gml:pos>
                                                    <gml:pos>457842.12 5439088 116.5</gml:pos>
                                                    <gml:pos>457842 5439088 116.5</gml:pos>
                                                    <gml:pos>457842 5439088.04357787 116.498097349046</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58738_1858_410319_138382">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58738_1858_410319_138382_0">
                                                    <gml:pos>457842 5439088 116.5</gml:pos>
                                                    <gml:pos>457842.12 5439088 116.5</gml:pos>
                                                    <gml:pos>457842.12 5439087.95642213 116.498097349046</gml:pos>
                                                    <gml:pos>457842 5439087.95642213 116.498097349046</gml:pos>
                                                    <gml:pos>457842 5439088 116.5</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58739_93_139462_125106">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58739_93_139462_125106_0">
                                                    <gml:pos>457842 5439087.95642213 116.498097349046</gml:pos>
                                                    <gml:pos>457842.12 5439087.95642213 116.498097349046</gml:pos>
                                                    <gml:pos>457842.12 5439087.91317591 116.492403876506</gml:pos>
                                                    <gml:pos>457842 5439087.91317591 116.492403876506</gml:pos>
                                                    <gml:pos>457842 5439087.95642213 116.498097349046</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58740_597_260514_271204">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58740_597_260514_271204_0">
                                                    <gml:pos>457842 5439087.91317591 116.492403876506</gml:pos>
                                                    <gml:pos>457842.12 5439087.91317591 116.492403876506</gml:pos>
                                                    <gml:pos>457842.12 5439087.87059048 116.482962913145</gml:pos>
                                                    <gml:pos>457842 5439087.87059048 116.482962913145</gml:pos>
                                                    <gml:pos>457842 5439087.91317591 116.492403876506</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58741_292_29758_205873">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58741_292_29758_205873_0">
                                                    <gml:pos>457842 5439087.87059048 116.482962913145</gml:pos>
                                                    <gml:pos>457842.12 5439087.87059048 116.482962913145</gml:pos>
                                                    <gml:pos>457842.12 5439087.82898993 116.469846310393</gml:pos>
                                                    <gml:pos>457842 5439087.82898993 116.469846310393</gml:pos>
                                                    <gml:pos>457842 5439087.87059048 116.482962913145</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58742_1305_101478_70299">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58742_1305_101478_70299_0">
                                                    <gml:pos>457842 5439087.82898993 116.469846310393</gml:pos>
                                                    <gml:pos>457842.12 5439087.82898993 116.469846310393</gml:pos>
                                                    <gml:pos>457842.12 5439087.78869087 116.453153893518</gml:pos>
                                                    <gml:pos>457842 5439087.78869087 116.453153893518</gml:pos>
                                                    <gml:pos>457842 5439087.82898993 116.469846310393</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58743_353_267552_336288">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58743_353_267552_336288_0">
                                                    <gml:pos>457842 5439087.78869087 116.453153893518</gml:pos>
                                                    <gml:pos>457842.12 5439087.78869087 116.453153893518</gml:pos>
                                                    <gml:pos>457842.12 5439087.75 116.433012701892</gml:pos>
                                                    <gml:pos>457842 5439087.75 116.433012701892</gml:pos>
                                                    <gml:pos>457842 5439087.78869087 116.453153893518</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58744_151_805227_372589">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58744_151_805227_372589_0">
                                                    <gml:pos>457842 5439087.75 116.433012701892</gml:pos>
                                                    <gml:pos>457842.12 5439087.75 116.433012701892</gml:pos>
                                                    <gml:pos>457842.12 5439087.71321178 116.409576022145</gml:pos>
                                                    <gml:pos>457842 5439087.71321178 116.409576022145</gml:pos>
                                                    <gml:pos>457842 5439087.75 116.433012701892</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58745_1956_497265_192895">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58745_1956_497265_192895_0">
                                                    <gml:pos>457842 5439087.71321178 116.409576022145</gml:pos>
                                                    <gml:pos>457842.12 5439087.71321178 116.409576022145</gml:pos>
                                                    <gml:pos>457842.12 5439087.6786062 116.383022221559</gml:pos>
                                                    <gml:pos>457842 5439087.6786062 116.383022221559</gml:pos>
                                                    <gml:pos>457842 5439087.71321178 116.409576022145</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58746_346_607928_425761">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58746_346_607928_425761_0">
                                                    <gml:pos>457842 5439087.6786062 116.383022221559</gml:pos>
                                                    <gml:pos>457842.12 5439087.6786062 116.383022221559</gml:pos>
                                                    <gml:pos>457842.12 5439087.64644661 116.353553390593</gml:pos>
                                                    <gml:pos>457842 5439087.64644661 116.353553390593</gml:pos>
                                                    <gml:pos>457842 5439087.6786062 116.383022221559</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58747_1503_480064_356412">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58747_1503_480064_356412_0">
                                                    <gml:pos>457842 5439087.64644661 116.353553390593</gml:pos>
                                                    <gml:pos>457842.12 5439087.64644661 116.353553390593</gml:pos>
                                                    <gml:pos>457842.12 5439087.61697778 116.321393804843</gml:pos>
                                                    <gml:pos>457842 5439087.61697778 116.321393804843</gml:pos>
                                                    <gml:pos>457842 5439087.64644661 116.353553390593</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58748_1646_113420_81152">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58748_1646_113420_81152_0">
                                                    <gml:pos>457842 5439087.61697778 116.321393804843</gml:pos>
                                                    <gml:pos>457842.12 5439087.61697778 116.321393804843</gml:pos>
                                                    <gml:pos>457842.12 5439087.59042398 116.286788218176</gml:pos>
                                                    <gml:pos>457842 5439087.59042398 116.286788218176</gml:pos>
                                                    <gml:pos>457842 5439087.61697778 116.321393804843</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58749_1825_295579_315972">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58749_1825_295579_315972_0">
                                                    <gml:pos>457842 5439087.59042398 116.286788218176</gml:pos>
                                                    <gml:pos>457842.12 5439087.59042398 116.286788218176</gml:pos>
                                                    <gml:pos>457842.12 5439087.5669873 116.25</gml:pos>
                                                    <gml:pos>457842 5439087.5669873 116.25</gml:pos>
                                                    <gml:pos>457842 5439087.59042398 116.286788218176</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58750_564_629766_418777">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58750_564_629766_418777_0">
                                                    <gml:pos>457842 5439087.5669873 116.25</gml:pos>
                                                    <gml:pos>457842.12 5439087.5669873 116.25</gml:pos>
                                                    <gml:pos>457842.12 5439087.54684611 116.21130913087</gml:pos>
                                                    <gml:pos>457842 5439087.54684611 116.21130913087</gml:pos>
                                                    <gml:pos>457842 5439087.5669873 116.25</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58751_151_118600_295696">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58751_151_118600_295696_0">
                                                    <gml:pos>457842 5439087.54684611 116.21130913087</gml:pos>
                                                    <gml:pos>457842.12 5439087.54684611 116.21130913087</gml:pos>
                                                    <gml:pos>457842.12 5439087.53015369 116.171010071663</gml:pos>
                                                    <gml:pos>457842 5439087.53015369 116.171010071663</gml:pos>
                                                    <gml:pos>457842 5439087.54684611 116.21130913087</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58752_858_826323_376037">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58752_858_826323_376037_0">
                                                    <gml:pos>457842 5439087.53015369 116.171010071663</gml:pos>
                                                    <gml:pos>457842.12 5439087.53015369 116.171010071663</gml:pos>
                                                    <gml:pos>457842.12 5439087.51703709 116.129409522551</gml:pos>
                                                    <gml:pos>457842 5439087.51703709 116.129409522551</gml:pos>
                                                    <gml:pos>457842 5439087.53015369 116.171010071663</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58753_1323_788001_38056">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58753_1323_788001_38056_0">
                                                    <gml:pos>457842 5439087.51703709 116.129409522551</gml:pos>
                                                    <gml:pos>457842.12 5439087.51703709 116.129409522551</gml:pos>
                                                    <gml:pos>457842.12 5439087.50759612 116.086824088833</gml:pos>
                                                    <gml:pos>457842 5439087.50759612 116.086824088833</gml:pos>
                                                    <gml:pos>457842 5439087.51703709 116.129409522551</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58754_847_898397_204895">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58754_847_898397_204895_0">
                                                    <gml:pos>457842 5439087.50759612 116.086824088833</gml:pos>
                                                    <gml:pos>457842.12 5439087.50759612 116.086824088833</gml:pos>
                                                    <gml:pos>457842.12 5439087.50190265 116.043577871374</gml:pos>
                                                    <gml:pos>457842 5439087.50190265 116.043577871374</gml:pos>
                                                    <gml:pos>457842 5439087.50759612 116.086824088833</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58755_855_543018_273807">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58755_855_543018_273807_0">
                                                    <gml:pos>457842 5439087.50190265 116.043577871374</gml:pos>
                                                    <gml:pos>457842.12 5439087.50190265 116.043577871374</gml:pos>
                                                    <gml:pos>457842.12 5439087.5 116</gml:pos>
                                                    <gml:pos>457842 5439087.5 116</gml:pos>
                                                    <gml:pos>457842 5439087.50190265 116.043577871374</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58756_728_727477_129311">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58756_728_727477_129311_0">
                                                    <gml:pos>457842 5439087.5 116</gml:pos>
                                                    <gml:pos>457842.12 5439087.5 116</gml:pos>
                                                    <gml:pos>457842.12 5439087.50190265 115.956422128626</gml:pos>
                                                    <gml:pos>457842 5439087.50190265 115.956422128626</gml:pos>
                                                    <gml:pos>457842 5439087.5 116</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58757_1190_133733_205599">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58757_1190_133733_205599_0">
                                                    <gml:pos>457842 5439087.50190265 115.956422128626</gml:pos>
                                                    <gml:pos>457842.12 5439087.50190265 115.956422128626</gml:pos>
                                                    <gml:pos>457842.12 5439087.50759612 115.913175911167</gml:pos>
                                                    <gml:pos>457842 5439087.50759612 115.913175911167</gml:pos>
                                                    <gml:pos>457842 5439087.50190265 115.956422128626</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58758_473_866699_76298">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58758_473_866699_76298_0">
                                                    <gml:pos>457842 5439087.50759612 115.913175911167</gml:pos>
                                                    <gml:pos>457842.12 5439087.50759612 115.913175911167</gml:pos>
                                                    <gml:pos>457842.12 5439087.51703709 115.870590477449</gml:pos>
                                                    <gml:pos>457842 5439087.51703709 115.870590477449</gml:pos>
                                                    <gml:pos>457842 5439087.50759612 115.913175911167</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58759_932_219075_423932">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58759_932_219075_423932_0">
                                                    <gml:pos>457842 5439087.51703709 115.870590477449</gml:pos>
                                                    <gml:pos>457842.12 5439087.51703709 115.870590477449</gml:pos>
                                                    <gml:pos>457842.12 5439087.53015369 115.828989928337</gml:pos>
                                                    <gml:pos>457842 5439087.53015369 115.828989928337</gml:pos>
                                                    <gml:pos>457842 5439087.51703709 115.870590477449</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58760_1123_610859_324850">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58760_1123_610859_324850_0">
                                                    <gml:pos>457842 5439087.53015369 115.828989928337</gml:pos>
                                                    <gml:pos>457842.12 5439087.53015369 115.828989928337</gml:pos>
                                                    <gml:pos>457842.12 5439087.54684611 115.78869086913</gml:pos>
                                                    <gml:pos>457842 5439087.54684611 115.78869086913</gml:pos>
                                                    <gml:pos>457842 5439087.53015369 115.828989928337</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58761_360_748179_284772">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58761_360_748179_284772_0">
                                                    <gml:pos>457842 5439087.54684611 115.78869086913</gml:pos>
                                                    <gml:pos>457842.12 5439087.54684611 115.78869086913</gml:pos>
                                                    <gml:pos>457842.12 5439087.5669873 115.75</gml:pos>
                                                    <gml:pos>457842 5439087.5669873 115.75</gml:pos>
                                                    <gml:pos>457842 5439087.54684611 115.78869086913</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58762_1767_354637_153477">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58762_1767_354637_153477_0">
                                                    <gml:pos>457842 5439087.5669873 115.75</gml:pos>
                                                    <gml:pos>457842.12 5439087.5669873 115.75</gml:pos>
                                                    <gml:pos>457842.12 5439087.59042398 115.713211781824</gml:pos>
                                                    <gml:pos>457842 5439087.59042398 115.713211781824</gml:pos>
                                                    <gml:pos>457842 5439087.5669873 115.75</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58763_197_610103_163787">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58763_197_610103_163787_0">
                                                    <gml:pos>457842 5439087.59042398 115.713211781824</gml:pos>
                                                    <gml:pos>457842.12 5439087.59042398 115.713211781824</gml:pos>
                                                    <gml:pos>457842.12 5439087.61697778 115.678606195157</gml:pos>
                                                    <gml:pos>457842 5439087.61697778 115.678606195157</gml:pos>
                                                    <gml:pos>457842 5439087.59042398 115.713211781824</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58764_11_591251_67040">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58764_11_591251_67040_0">
                                                    <gml:pos>457842 5439087.61697778 115.678606195157</gml:pos>
                                                    <gml:pos>457842.12 5439087.61697778 115.678606195157</gml:pos>
                                                    <gml:pos>457842.12 5439087.64644661 115.646446609407</gml:pos>
                                                    <gml:pos>457842 5439087.64644661 115.646446609407</gml:pos>
                                                    <gml:pos>457842 5439087.61697778 115.678606195157</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58765_1620_317404_230187">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58765_1620_317404_230187_0">
                                                    <gml:pos>457842 5439087.64644661 115.646446609407</gml:pos>
                                                    <gml:pos>457842.12 5439087.64644661 115.646446609407</gml:pos>
                                                    <gml:pos>457842.12 5439087.6786062 115.616977778441</gml:pos>
                                                    <gml:pos>457842 5439087.6786062 115.616977778441</gml:pos>
                                                    <gml:pos>457842 5439087.64644661 115.646446609407</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58766_1041_669385_130795">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58766_1041_669385_130795_0">
                                                    <gml:pos>457842 5439087.6786062 115.616977778441</gml:pos>
                                                    <gml:pos>457842.12 5439087.6786062 115.616977778441</gml:pos>
                                                    <gml:pos>457842.12 5439087.71321178 115.590423977855</gml:pos>
                                                    <gml:pos>457842 5439087.71321178 115.590423977855</gml:pos>
                                                    <gml:pos>457842 5439087.6786062 115.616977778441</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58767_309_319898_242176">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58767_309_319898_242176_0">
                                                    <gml:pos>457842 5439087.71321178 115.590423977855</gml:pos>
                                                    <gml:pos>457842.12 5439087.71321178 115.590423977855</gml:pos>
                                                    <gml:pos>457842.12 5439087.75 115.566987298108</gml:pos>
                                                    <gml:pos>457842 5439087.75 115.566987298108</gml:pos>
                                                    <gml:pos>457842 5439087.71321178 115.590423977855</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58768_1040_256187_326219">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58768_1040_256187_326219_0">
                                                    <gml:pos>457842 5439087.75 115.566987298108</gml:pos>
                                                    <gml:pos>457842.12 5439087.75 115.566987298108</gml:pos>
                                                    <gml:pos>457842.12 5439087.78869087 115.546846106482</gml:pos>
                                                    <gml:pos>457842 5439087.78869087 115.546846106482</gml:pos>
                                                    <gml:pos>457842 5439087.75 115.566987298108</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58769_1881_454925_167376">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58769_1881_454925_167376_0">
                                                    <gml:pos>457842 5439087.78869087 115.546846106482</gml:pos>
                                                    <gml:pos>457842.12 5439087.78869087 115.546846106482</gml:pos>
                                                    <gml:pos>457842.12 5439087.82898993 115.530153689607</gml:pos>
                                                    <gml:pos>457842 5439087.82898993 115.530153689607</gml:pos>
                                                    <gml:pos>457842 5439087.78869087 115.546846106482</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58770_1784_618181_39032">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58770_1784_618181_39032_0">
                                                    <gml:pos>457842 5439087.82898993 115.530153689607</gml:pos>
                                                    <gml:pos>457842.12 5439087.82898993 115.530153689607</gml:pos>
                                                    <gml:pos>457842.12 5439087.87059048 115.517037086855</gml:pos>
                                                    <gml:pos>457842 5439087.87059048 115.517037086855</gml:pos>
                                                    <gml:pos>457842 5439087.82898993 115.530153689607</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58771_1187_701223_394355">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58771_1187_701223_394355_0">
                                                    <gml:pos>457842 5439087.87059048 115.517037086855</gml:pos>
                                                    <gml:pos>457842.12 5439087.87059048 115.517037086855</gml:pos>
                                                    <gml:pos>457842.12 5439087.91317591 115.507596123494</gml:pos>
                                                    <gml:pos>457842 5439087.91317591 115.507596123494</gml:pos>
                                                    <gml:pos>457842 5439087.87059048 115.517037086855</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58772_547_637713_390862">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58772_547_637713_390862_0">
                                                    <gml:pos>457842 5439087.91317591 115.507596123494</gml:pos>
                                                    <gml:pos>457842.12 5439087.91317591 115.507596123494</gml:pos>
                                                    <gml:pos>457842.12 5439087.95642213 115.501902650954</gml:pos>
                                                    <gml:pos>457842 5439087.95642213 115.501902650954</gml:pos>
                                                    <gml:pos>457842 5439087.91317591 115.507596123494</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58773_793_593960_380050">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58773_793_593960_380050_0">
                                                    <gml:pos>457842 5439087.95642213 115.501902650954</gml:pos>
                                                    <gml:pos>457842.12 5439087.95642213 115.501902650954</gml:pos>
                                                    <gml:pos>457842.12 5439088 115.5</gml:pos>
                                                    <gml:pos>457842 5439088 115.5</gml:pos>
                                                    <gml:pos>457842 5439087.95642213 115.501902650954</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58774_793_865544_188787">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58774_793_865544_188787_0">
                                                    <gml:pos>457842 5439088 115.5</gml:pos>
                                                    <gml:pos>457842.12 5439088 115.5</gml:pos>
                                                    <gml:pos>457842.12 5439088.04357787 115.501902650954</gml:pos>
                                                    <gml:pos>457842 5439088.04357787 115.501902650954</gml:pos>
                                                    <gml:pos>457842 5439088 115.5</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58775_282_175384_345012">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58775_282_175384_345012_0">
                                                    <gml:pos>457842 5439088.04357787 115.501902650954</gml:pos>
                                                    <gml:pos>457842.12 5439088.04357787 115.501902650954</gml:pos>
                                                    <gml:pos>457842.12 5439088.08682409 115.507596123494</gml:pos>
                                                    <gml:pos>457842 5439088.08682409 115.507596123494</gml:pos>
                                                    <gml:pos>457842 5439088.04357787 115.501902650954</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58776_1552_664428_319210">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58776_1552_664428_319210_0">
                                                    <gml:pos>457842 5439088.08682409 115.507596123494</gml:pos>
                                                    <gml:pos>457842.12 5439088.08682409 115.507596123494</gml:pos>
                                                    <gml:pos>457842.12 5439088.12940952 115.517037086855</gml:pos>
                                                    <gml:pos>457842 5439088.12940952 115.517037086855</gml:pos>
                                                    <gml:pos>457842 5439088.08682409 115.507596123494</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58777_1359_666261_417799">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58777_1359_666261_417799_0">
                                                    <gml:pos>457842 5439088.12940952 115.517037086855</gml:pos>
                                                    <gml:pos>457842.12 5439088.12940952 115.517037086855</gml:pos>
                                                    <gml:pos>457842.12 5439088.17101007 115.530153689607</gml:pos>
                                                    <gml:pos>457842 5439088.17101007 115.530153689607</gml:pos>
                                                    <gml:pos>457842 5439088.12940952 115.517037086855</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58778_1096_548803_126046">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58778_1096_548803_126046_0">
                                                    <gml:pos>457842 5439088.17101007 115.530153689607</gml:pos>
                                                    <gml:pos>457842.12 5439088.17101007 115.530153689607</gml:pos>
                                                    <gml:pos>457842.12 5439088.21130913 115.546846106482</gml:pos>
                                                    <gml:pos>457842 5439088.21130913 115.546846106482</gml:pos>
                                                    <gml:pos>457842 5439088.17101007 115.530153689607</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58779_1428_163641_401174">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58779_1428_163641_401174_0">
                                                    <gml:pos>457842 5439088.21130913 115.546846106482</gml:pos>
                                                    <gml:pos>457842.12 5439088.21130913 115.546846106482</gml:pos>
                                                    <gml:pos>457842.12 5439088.25 115.566987298108</gml:pos>
                                                    <gml:pos>457842 5439088.25 115.566987298108</gml:pos>
                                                    <gml:pos>457842 5439088.21130913 115.546846106482</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58780_568_797601_421115">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58780_568_797601_421115_0">
                                                    <gml:pos>457842 5439088.25 115.566987298108</gml:pos>
                                                    <gml:pos>457842.12 5439088.25 115.566987298108</gml:pos>
                                                    <gml:pos>457842.12 5439088.28678822 115.590423977855</gml:pos>
                                                    <gml:pos>457842 5439088.28678822 115.590423977855</gml:pos>
                                                    <gml:pos>457842 5439088.25 115.566987298108</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58781_1599_837624_120564">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58781_1599_837624_120564_0">
                                                    <gml:pos>457842 5439088.28678822 115.590423977855</gml:pos>
                                                    <gml:pos>457842.12 5439088.28678822 115.590423977855</gml:pos>
                                                    <gml:pos>457842.12 5439088.3213938 115.616977778441</gml:pos>
                                                    <gml:pos>457842 5439088.3213938 115.616977778441</gml:pos>
                                                    <gml:pos>457842 5439088.28678822 115.590423977855</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58782_1554_391576_397213">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58782_1554_391576_397213_0">
                                                    <gml:pos>457842 5439088.3213938 115.616977778441</gml:pos>
                                                    <gml:pos>457842.12 5439088.3213938 115.616977778441</gml:pos>
                                                    <gml:pos>457842.12 5439088.35355339 115.646446609407</gml:pos>
                                                    <gml:pos>457842 5439088.35355339 115.646446609407</gml:pos>
                                                    <gml:pos>457842 5439088.3213938 115.616977778441</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58783_1364_412095_196243">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58783_1364_412095_196243_0">
                                                    <gml:pos>457842 5439088.35355339 115.646446609407</gml:pos>
                                                    <gml:pos>457842.12 5439088.35355339 115.646446609407</gml:pos>
                                                    <gml:pos>457842.12 5439088.38302222 115.678606195157</gml:pos>
                                                    <gml:pos>457842 5439088.38302222 115.678606195157</gml:pos>
                                                    <gml:pos>457842 5439088.35355339 115.646446609407</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58784_1851_450835_195960">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58784_1851_450835_195960_0">
                                                    <gml:pos>457842 5439088.38302222 115.678606195157</gml:pos>
                                                    <gml:pos>457842.12 5439088.38302222 115.678606195157</gml:pos>
                                                    <gml:pos>457842.12 5439088.40957602 115.713211781824</gml:pos>
                                                    <gml:pos>457842 5439088.40957602 115.713211781824</gml:pos>
                                                    <gml:pos>457842 5439088.38302222 115.678606195157</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58785_571_484072_399504">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58785_571_484072_399504_0">
                                                    <gml:pos>457842 5439088.40957602 115.713211781824</gml:pos>
                                                    <gml:pos>457842.12 5439088.40957602 115.713211781824</gml:pos>
                                                    <gml:pos>457842.12 5439088.4330127 115.75</gml:pos>
                                                    <gml:pos>457842 5439088.4330127 115.75</gml:pos>
                                                    <gml:pos>457842 5439088.40957602 115.713211781824</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58786_416_647588_414858">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58786_416_647588_414858_0">
                                                    <gml:pos>457842 5439088.4330127 115.75</gml:pos>
                                                    <gml:pos>457842.12 5439088.4330127 115.75</gml:pos>
                                                    <gml:pos>457842.12 5439088.45315389 115.78869086913</gml:pos>
                                                    <gml:pos>457842 5439088.45315389 115.78869086913</gml:pos>
                                                    <gml:pos>457842 5439088.4330127 115.75</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58787_678_758646_31866">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58787_678_758646_31866_0">
                                                    <gml:pos>457842 5439088.45315389 115.78869086913</gml:pos>
                                                    <gml:pos>457842.12 5439088.45315389 115.78869086913</gml:pos>
                                                    <gml:pos>457842.12 5439088.46984631 115.828989928337</gml:pos>
                                                    <gml:pos>457842 5439088.46984631 115.828989928337</gml:pos>
                                                    <gml:pos>457842 5439088.45315389 115.78869086913</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58788_756_104461_254505">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58788_756_104461_254505_0">
                                                    <gml:pos>457842 5439088.46984631 115.828989928337</gml:pos>
                                                    <gml:pos>457842.12 5439088.46984631 115.828989928337</gml:pos>
                                                    <gml:pos>457842.12 5439088.48296291 115.870590477449</gml:pos>
                                                    <gml:pos>457842 5439088.48296291 115.870590477449</gml:pos>
                                                    <gml:pos>457842 5439088.46984631 115.828989928337</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58789_846_196612_169762">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58789_846_196612_169762_0">
                                                    <gml:pos>457842 5439088.48296291 115.870590477449</gml:pos>
                                                    <gml:pos>457842.12 5439088.48296291 115.870590477449</gml:pos>
                                                    <gml:pos>457842.12 5439088.49240388 115.913175911167</gml:pos>
                                                    <gml:pos>457842 5439088.49240388 115.913175911167</gml:pos>
                                                    <gml:pos>457842 5439088.48296291 115.870590477449</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58790_1248_92472_252525">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58790_1248_92472_252525_0">
                                                    <gml:pos>457842 5439088.505 114.01</gml:pos>
                                                    <gml:pos>457842.2 5439088.505 114.01</gml:pos>
                                                    <gml:pos>457842.2 5439087.495 114.01</gml:pos>
                                                    <gml:pos>457842 5439087.495 114.01</gml:pos>
                                                    <gml:pos>457842 5439088.505 114.01</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58791_120_287257_174378">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58791_120_287257_174378_0">
                                                    <gml:pos>457842 5439084.205 114</gml:pos>
                                                    <gml:pos>457842.12 5439084.205 114</gml:pos>
                                                    <gml:pos>457842.12 5439084.205 112.8</gml:pos>
                                                    <gml:pos>457842 5439084.205 112.8</gml:pos>
                                                    <gml:pos>457842 5439084.205 114</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58792_59_583087_214481">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58792_59_583087_214481_0">
                                                    <gml:pos>457842 5439086.205 114</gml:pos>
                                                    <gml:pos>457842.12 5439086.205 114</gml:pos>
                                                    <gml:pos>457842.12 5439084.205 114</gml:pos>
                                                    <gml:pos>457842 5439084.205 114</gml:pos>
                                                    <gml:pos>457842 5439086.205 114</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58793_416_393222_104842">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58793_416_393222_104842_0">
                                                    <gml:pos>457842 5439084.205 112.8</gml:pos>
                                                    <gml:pos>457842.12 5439084.205 112.8</gml:pos>
                                                    <gml:pos>457842.12 5439086.205 112.8</gml:pos>
                                                    <gml:pos>457842 5439086.205 112.8</gml:pos>
                                                    <gml:pos>457842 5439084.205 112.8</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58794_397_290407_425276">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58794_397_290407_425276_0">
                                                    <gml:pos>457842 5439091.495 114</gml:pos>
                                                    <gml:pos>457842.12 5439091.495 114</gml:pos>
                                                    <gml:pos>457842.12 5439089.495 114</gml:pos>
                                                    <gml:pos>457842 5439089.495 114</gml:pos>
                                                    <gml:pos>457842 5439091.495 114</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58795_1164_574220_320141">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58795_1164_574220_320141_0">
                                                    <gml:pos>457842 5439089.495 114</gml:pos>
                                                    <gml:pos>457842.12 5439089.495 114</gml:pos>
                                                    <gml:pos>457842.12 5439089.495 112.8</gml:pos>
                                                    <gml:pos>457842 5439089.495 112.8</gml:pos>
                                                    <gml:pos>457842 5439089.495 114</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58796_1723_375485_410870">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58796_1723_375485_410870_0">
                                                    <gml:pos>457842 5439086.205 112.8</gml:pos>
                                                    <gml:pos>457842.12 5439086.205 112.8</gml:pos>
                                                    <gml:pos>457842.12 5439086.205 114</gml:pos>
                                                    <gml:pos>457842 5439086.205 114</gml:pos>
                                                    <gml:pos>457842 5439086.205 112.8</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58797_1512_530222_357889">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58797_1512_530222_357889_0">
                                                    <gml:pos>457842 5439088.505 112</gml:pos>
                                                    <gml:pos>457842.2 5439088.505 112</gml:pos>
                                                    <gml:pos>457842.2 5439088.505 114.01</gml:pos>
                                                    <gml:pos>457842 5439088.505 114.01</gml:pos>
                                                    <gml:pos>457842 5439088.505 112</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58798_713_434939_29079">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58798_713_434939_29079_0">
                                                    <gml:pos>457842 5439088.505 112</gml:pos>
                                                    <gml:pos>457842 5439087.495 112</gml:pos>
                                                    <gml:pos>457842.2 5439087.495 112</gml:pos>
                                                    <gml:pos>457842.2 5439088.505 112</gml:pos>
                                                    <gml:pos>457842 5439088.505 112</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58799_1369_443926_175858">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58799_1369_443926_175858_0">
                                                    <gml:pos>457842 5439087.495 114.01</gml:pos>
                                                    <gml:pos>457842.2 5439087.495 114.01</gml:pos>
                                                    <gml:pos>457842.2 5439087.495 112</gml:pos>
                                                    <gml:pos>457842 5439087.495 112</gml:pos>
                                                    <gml:pos>457842 5439087.495 114.01</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58800_1903_739625_389368">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58800_1903_739625_389368_0">
                                                    <gml:pos>457842 5439091.495 112.8</gml:pos>
                                                    <gml:pos>457842.12 5439091.495 112.8</gml:pos>
                                                    <gml:pos>457842.12 5439091.495 114</gml:pos>
                                                    <gml:pos>457842 5439091.495 114</gml:pos>
                                                    <gml:pos>457842 5439091.495 112.8</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58801_882_806674_392883">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58801_882_806674_392883_0">
                                                    <gml:pos>457842 5439089.495 112.8</gml:pos>
                                                    <gml:pos>457842.12 5439089.495 112.8</gml:pos>
                                                    <gml:pos>457842.12 5439091.495 112.8</gml:pos>
                                                    <gml:pos>457842 5439091.495 112.8</gml:pos>
                                                    <gml:pos>457842 5439089.495 112.8</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58802_1543_379123_11561">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58802_1543_379123_11561_0">
                                                    <gml:pos>457842 5439088 118.317691453624</gml:pos>
                                                    <gml:pos>457842 5439093 115.430940107676</gml:pos>
                                                    <gml:pos>457842 5439093 111.8</gml:pos>
                                                    <gml:pos>457842 5439083 111.8</gml:pos>
                                                    <gml:pos>457842 5439083 115.430940107676</gml:pos>
                                                    <gml:pos>457842 5439088 118.317691453624</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                            <gml:interior>
                                                <gml:LinearRing gml:id="PolyID58802_1543_379123_11561_1">
                                                    <gml:pos>457842 5439088.48296291 116.129409522551</gml:pos>
                                                    <gml:pos>457842 5439088.46984631 116.171010071663</gml:pos>
                                                    <gml:pos>457842 5439088.45315389 116.21130913087</gml:pos>
                                                    <gml:pos>457842 5439088.4330127 116.25</gml:pos>
                                                    <gml:pos>457842 5439088.40957602 116.286788218176</gml:pos>
                                                    <gml:pos>457842 5439088.38302222 116.321393804843</gml:pos>
                                                    <gml:pos>457842 5439088.35355339 116.353553390593</gml:pos>
                                                    <gml:pos>457842 5439088.3213938 116.383022221559</gml:pos>
                                                    <gml:pos>457842 5439088.28678822 116.409576022145</gml:pos>
                                                    <gml:pos>457842 5439088.25 116.433012701892</gml:pos>
                                                    <gml:pos>457842 5439088.21130913 116.453153893518</gml:pos>
                                                    <gml:pos>457842 5439088.17101007 116.469846310393</gml:pos>
                                                    <gml:pos>457842 5439088.12940952 116.482962913145</gml:pos>
                                                    <gml:pos>457842 5439088.08682409 116.492403876506</gml:pos>
                                                    <gml:pos>457842 5439088.04357787 116.498097349046</gml:pos>
                                                    <gml:pos>457842 5439088 116.5</gml:pos>
                                                    <gml:pos>457842 5439087.95642213 116.498097349046</gml:pos>
                                                    <gml:pos>457842 5439087.91317591 116.492403876506</gml:pos>
                                                    <gml:pos>457842 5439087.87059048 116.482962913145</gml:pos>
                                                    <gml:pos>457842 5439087.82898993 116.469846310393</gml:pos>
                                                    <gml:pos>457842 5439087.78869087 116.453153893518</gml:pos>
                                                    <gml:pos>457842 5439087.75 116.433012701892</gml:pos>
                                                    <gml:pos>457842 5439087.71321178 116.409576022145</gml:pos>
                                                    <gml:pos>457842 5439087.6786062 116.383022221559</gml:pos>
                                                    <gml:pos>457842 5439087.64644661 116.353553390593</gml:pos>
                                                    <gml:pos>457842 5439087.61697778 116.321393804843</gml:pos>
                                                    <gml:pos>457842 5439087.59042398 116.286788218176</gml:pos>
                                                    <gml:pos>457842 5439087.5669873 116.25</gml:pos>
                                                    <gml:pos>457842 5439087.54684611 116.21130913087</gml:pos>
                                                    <gml:pos>457842 5439087.53015369 116.171010071663</gml:pos>
                                                    <gml:pos>457842 5439087.51703709 116.129409522551</gml:pos>
                                                    <gml:pos>457842 5439087.50759612 116.086824088833</gml:pos>
                                                    <gml:pos>457842 5439087.50190265 116.043577871374</gml:pos>
                                                    <gml:pos>457842 5439087.5 116</gml:pos>
                                                    <gml:pos>457842 5439087.50190265 115.956422128626</gml:pos>
                                                    <gml:pos>457842 5439087.50759612 115.913175911167</gml:pos>
                                                    <gml:pos>457842 5439087.51703709 115.870590477449</gml:pos>
                                                    <gml:pos>457842 5439087.53015369 115.828989928337</gml:pos>
                                                    <gml:pos>457842 5439087.54684611 115.78869086913</gml:pos>
                                                    <gml:pos>457842 5439087.5669873 115.75</gml:pos>
                                                    <gml:pos>457842 5439087.59042398 115.713211781824</gml:pos>
                                                    <gml:pos>457842 5439087.61697778 115.678606195157</gml:pos>
                                                    <gml:pos>457842 5439087.64644661 115.646446609407</gml:pos>
                                                    <gml:pos>457842 5439087.6786062 115.616977778441</gml:pos>
                                                    <gml:pos>457842 5439087.71321178 115.590423977855</gml:pos>
                                                    <gml:pos>457842 5439087.75 115.566987298108</gml:pos>
                                                    <gml:pos>457842 5439087.78869087 115.546846106482</gml:pos>
                                                    <gml:pos>457842 5439087.82898993 115.530153689607</gml:pos>
                                                    <gml:pos>457842 5439087.87059048 115.517037086855</gml:pos>
                                                    <gml:pos>457842 5439087.91317591 115.507596123494</gml:pos>
                                                    <gml:pos>457842 5439087.95642213 115.501902650954</gml:pos>
                                                    <gml:pos>457842 5439088 115.5</gml:pos>
                                                    <gml:pos>457842 5439088.04357787 115.501902650954</gml:pos>
                                                    <gml:pos>457842 5439088.08682409 115.507596123494</gml:pos>
                                                    <gml:pos>457842 5439088.12940952 115.517037086855</gml:pos>
                                                    <gml:pos>457842 5439088.17101007 115.530153689607</gml:pos>
                                                    <gml:pos>457842 5439088.21130913 115.546846106482</gml:pos>
                                                    <gml:pos>457842 5439088.25 115.566987298108</gml:pos>
                                                    <gml:pos>457842 5439088.28678822 115.590423977855</gml:pos>
                                                    <gml:pos>457842 5439088.3213938 115.616977778441</gml:pos>
                                                    <gml:pos>457842 5439088.35355339 115.646446609407</gml:pos>
                                                    <gml:pos>457842 5439088.38302222 115.678606195157</gml:pos>
                                                    <gml:pos>457842 5439088.40957602 115.713211781824</gml:pos>
                                                    <gml:pos>457842 5439088.4330127 115.75</gml:pos>
                                                    <gml:pos>457842 5439088.45315389 115.78869086913</gml:pos>
                                                    <gml:pos>457842 5439088.46984631 115.828989928337</gml:pos>
                                                    <gml:pos>457842 5439088.48296291 115.870590477449</gml:pos>
                                                    <gml:pos>457842 5439088.49240388 115.913175911167</gml:pos>
                                                    <gml:pos>457842 5439088.49809735 115.956422128626</gml:pos>
                                                    <gml:pos>457842 5439088.5 116</gml:pos>
                                                    <gml:pos>457842 5439088.49809735 116.043577871374</gml:pos>
                                                    <gml:pos>457842 5439088.49240388 116.086824088833</gml:pos>
                                                    <gml:pos>457842 5439088.48296291 116.129409522551</gml:pos>
                                                </gml:LinearRing>
                                            </gml:interior>
                                            <gml:interior>
                                                <gml:LinearRing gml:id="PolyID58802_1543_379123_11561_2">
                                                    <gml:pos>457842 5439087.495 114.01</gml:pos>
                                                    <gml:pos>457842 5439087.495 112</gml:pos>
                                                    <gml:pos>457842 5439088.505 112</gml:pos>
                                                    <gml:pos>457842 5439088.505 114.01</gml:pos>
                                                    <gml:pos>457842 5439087.495 114.01</gml:pos>
                                                </gml:LinearRing>
                                            </gml:interior>
                                            <gml:interior>
                                                <gml:LinearRing gml:id="PolyID58802_1543_379123_11561_3">
                                                    <gml:pos>457842 5439089.495 114</gml:pos>
                                                    <gml:pos>457842 5439089.495 112.8</gml:pos>
                                                    <gml:pos>457842 5439091.495 112.8</gml:pos>
                                                    <gml:pos>457842 5439091.495 114</gml:pos>
                                                    <gml:pos>457842 5439089.495 114</gml:pos>
                                                </gml:LinearRing>
                                            </gml:interior>
                                            <gml:interior>
                                                <gml:LinearRing gml:id="PolyID58802_1543_379123_11561_4">
                                                    <gml:pos>457842 5439086.205 114</gml:pos>
                                                    <gml:pos>457842 5439084.205 114</gml:pos>
                                                    <gml:pos>457842 5439084.205 112.8</gml:pos>
                                                    <gml:pos>457842 5439086.205 112.8</gml:pos>
                                                    <gml:pos>457842 5439086.205 114</gml:pos>
                                                </gml:LinearRing>
                                            </gml:interior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                </gml:CompositeSurface>
                            </gml:surfaceMember>
                        </gml:MultiSurface>
                    </bldg:lod3MultiSurface>
                    <bldg:opening>
                        <bldg:Window gml:id="GML_356b85c1-25a0-49f9-b39e-013fbbafcce4">
                            <gml:name>Window Round</gml:name>
                            <bldg:lod3MultiSurface>
                                <gml:MultiSurface>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58803_371_698036_77126">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58803_371_698036_77126_0">
                                                    <gml:pos>457842.12 5439088.40957602 116.286788218176</gml:pos>
                                                    <gml:pos>457842.12 5439088.4330127 116.25</gml:pos>
                                                    <gml:pos>457842.12 5439088.45315389 116.21130913087</gml:pos>
                                                    <gml:pos>457842.12 5439088.46984631 116.171010071663</gml:pos>
                                                    <gml:pos>457842.12 5439088.48296291 116.129409522551</gml:pos>
                                                    <gml:pos>457842.12 5439088.49240388 116.086824088833</gml:pos>
                                                    <gml:pos>457842.12 5439088.49809735 116.043577871374</gml:pos>
                                                    <gml:pos>457842.12 5439088.5 116</gml:pos>
                                                    <gml:pos>457842.12 5439088.49809735 115.956422128626</gml:pos>
                                                    <gml:pos>457842.12 5439088.49240388 115.913175911167</gml:pos>
                                                    <gml:pos>457842.12 5439088.48296291 115.870590477449</gml:pos>
                                                    <gml:pos>457842.12 5439088.46984631 115.828989928337</gml:pos>
                                                    <gml:pos>457842.12 5439088.45315389 115.78869086913</gml:pos>
                                                    <gml:pos>457842.12 5439088.4330127 115.75</gml:pos>
                                                    <gml:pos>457842.12 5439088.40957602 115.713211781824</gml:pos>
                                                    <gml:pos>457842.12 5439088.38302222 115.678606195157</gml:pos>
                                                    <gml:pos>457842.12 5439088.35355339 115.646446609407</gml:pos>
                                                    <gml:pos>457842.12 5439088.3213938 115.616977778441</gml:pos>
                                                    <gml:pos>457842.12 5439088.28678822 115.590423977855</gml:pos>
                                                    <gml:pos>457842.12 5439088.25 115.566987298108</gml:pos>
                                                    <gml:pos>457842.12 5439088.21130913 115.546846106482</gml:pos>
                                                    <gml:pos>457842.12 5439088.17101007 115.530153689607</gml:pos>
                                                    <gml:pos>457842.12 5439088.12940952 115.517037086855</gml:pos>
                                                    <gml:pos>457842.12 5439088.08682409 115.507596123494</gml:pos>
                                                    <gml:pos>457842.12 5439088.04357787 115.501902650954</gml:pos>
                                                    <gml:pos>457842.12 5439088 115.5</gml:pos>
                                                    <gml:pos>457842.12 5439087.95642213 115.501902650954</gml:pos>
                                                    <gml:pos>457842.12 5439087.91317591 115.507596123494</gml:pos>
                                                    <gml:pos>457842.12 5439087.87059048 115.517037086855</gml:pos>
                                                    <gml:pos>457842.12 5439087.82898993 115.530153689607</gml:pos>
                                                    <gml:pos>457842.12 5439087.78869087 115.546846106482</gml:pos>
                                                    <gml:pos>457842.12 5439087.75 115.566987298108</gml:pos>
                                                    <gml:pos>457842.12 5439087.71321178 115.590423977855</gml:pos>
                                                    <gml:pos>457842.12 5439087.6786062 115.616977778441</gml:pos>
                                                    <gml:pos>457842.12 5439087.64644661 115.646446609407</gml:pos>
                                                    <gml:pos>457842.12 5439087.61697778 115.678606195157</gml:pos>
                                                    <gml:pos>457842.12 5439087.59042398 115.713211781824</gml:pos>
                                                    <gml:pos>457842.12 5439087.5669873 115.75</gml:pos>
                                                    <gml:pos>457842.12 5439087.54684611 115.78869086913</gml:pos>
                                                    <gml:pos>457842.12 5439087.53015369 115.828989928337</gml:pos>
                                                    <gml:pos>457842.12 5439087.51703709 115.870590477449</gml:pos>
                                                    <gml:pos>457842.12 5439087.50759612 115.913175911167</gml:pos>
                                                    <gml:pos>457842.12 5439087.50190265 115.956422128626</gml:pos>
                                                    <gml:pos>457842.12 5439087.5 116</gml:pos>
                                                    <gml:pos>457842.12 5439087.50190265 116.043577871374</gml:pos>
                                                    <gml:pos>457842.12 5439087.50759612 116.086824088833</gml:pos>
                                                    <gml:pos>457842.12 5439087.51703709 116.129409522551</gml:pos>
                                                    <gml:pos>457842.12 5439087.53015369 116.171010071663</gml:pos>
                                                    <gml:pos>457842.12 5439087.54684611 116.21130913087</gml:pos>
                                                    <gml:pos>457842.12 5439087.5669873 116.25</gml:pos>
                                                    <gml:pos>457842.12 5439087.59042398 116.286788218176</gml:pos>
                                                    <gml:pos>457842.12 5439087.61697778 116.321393804843</gml:pos>
                                                    <gml:pos>457842.12 5439087.64644661 116.353553390593</gml:pos>
                                                    <gml:pos>457842.12 5439087.6786062 116.383022221559</gml:pos>
                                                    <gml:pos>457842.12 5439087.71321178 116.409576022145</gml:pos>
                                                    <gml:pos>457842.12 5439087.75 116.433012701892</gml:pos>
                                                    <gml:pos>457842.12 5439087.78869087 116.453153893518</gml:pos>
                                                    <gml:pos>457842.12 5439087.82898993 116.469846310393</gml:pos>
                                                    <gml:pos>457842.12 5439087.87059048 116.482962913145</gml:pos>
                                                    <gml:pos>457842.12 5439087.91317591 116.492403876506</gml:pos>
                                                    <gml:pos>457842.12 5439087.95642213 116.498097349046</gml:pos>
                                                    <gml:pos>457842.12 5439088 116.5</gml:pos>
                                                    <gml:pos>457842.12 5439088.04357787 116.498097349046</gml:pos>
                                                    <gml:pos>457842.12 5439088.08682409 116.492403876506</gml:pos>
                                                    <gml:pos>457842.12 5439088.12940952 116.482962913145</gml:pos>
                                                    <gml:pos>457842.12 5439088.17101007 116.469846310393</gml:pos>
                                                    <gml:pos>457842.12 5439088.21130913 116.453153893518</gml:pos>
                                                    <gml:pos>457842.12 5439088.25 116.433012701892</gml:pos>
                                                    <gml:pos>457842.12 5439088.28678822 116.409576022145</gml:pos>
                                                    <gml:pos>457842.12 5439088.3213938 116.383022221559</gml:pos>
                                                    <gml:pos>457842.12 5439088.35355339 116.353553390593</gml:pos>
                                                    <gml:pos>457842.12 5439088.38302222 116.321393804843</gml:pos>
                                                    <gml:pos>457842.12 5439088.40957602 116.286788218176</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                </gml:MultiSurface>
                            </bldg:lod3MultiSurface>
                        </bldg:Window>
                    </bldg:opening>
                    <bldg:opening>
                        <bldg:Window gml:id="GML_868be7d3-16c7-4dec-9ac6-5bb8ceb545bb">
                            <gml:name>Window North</gml:name>
                            <bldg:lod3MultiSurface>
                                <gml:MultiSurface>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58804_647_880710_163324">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58804_647_880710_163324_0">
                                                    <gml:pos>457842.12 5439089.495 114</gml:pos>
                                                    <gml:pos>457842.12 5439091.495 114</gml:pos>
                                                    <gml:pos>457842.12 5439091.495 112.8</gml:pos>
                                                    <gml:pos>457842.12 5439089.495 112.8</gml:pos>
                                                    <gml:pos>457842.12 5439089.495 114</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                </gml:MultiSurface>
                            </bldg:lod3MultiSurface>
                        </bldg:Window>
                    </bldg:opening>
                    <bldg:opening>
                        <bldg:Door gml:id="GML_c137f11d-9a8c-4126-9aeb-9a6c9b4e1cbd">
                            <gml:name>Door West</gml:name>
                            <bldg:lod3MultiSurface>
                                <gml:MultiSurface>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58805_1881_773628_351228">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58805_1881_773628_351228_0">
                                                    <gml:pos>457842.2 5439087.495 114.01</gml:pos>
                                                    <gml:pos>457842.2 5439088.505 114.01</gml:pos>
                                                    <gml:pos>457842.2 5439088.505 112</gml:pos>
                                                    <gml:pos>457842.2 5439087.495 112</gml:pos>
                                                    <gml:pos>457842.2 5439087.495 114.01</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                </gml:MultiSurface>
                            </bldg:lod3MultiSurface>
                        </bldg:Door>
                    </bldg:opening>
                    <bldg:opening>
                        <bldg:Window gml:id="GML_9e0e6137-a907-4e4b-bc30-a6b95641f4c0">
                            <gml:name>Window South</gml:name>
                            <bldg:lod3MultiSurface>
                                <gml:MultiSurface>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58806_328_642559_374120">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58806_328_642559_374120_0">
                                                    <gml:pos>457842.12 5439086.205 112.8</gml:pos>
                                                    <gml:pos>457842.12 5439084.205 112.8</gml:pos>
                                                    <gml:pos>457842.12 5439084.205 114</gml:pos>
                                                    <gml:pos>457842.12 5439086.205 114</gml:pos>
                                                    <gml:pos>457842.12 5439086.205 112.8</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                </gml:MultiSurface>
                            </bldg:lod3MultiSurface>
                        </bldg:Window>
                    </bldg:opening>
                </bldg:WallSurface>
            </bldg:boundedBy>
            <bldg:boundedBy>
                <bldg:WallSurface gml:id="GML_d38cf762-c29d-4491-88c9-bdc89e141978">
                    <gml:name>Outer Wall 2 (South)</gml:name>
                    <bldg:lod3MultiSurface>
                        <gml:MultiSurface>
                            <gml:surfaceMember>
                                <gml:CompositeSurface gml:id="GML_4726d5c0-dfa2-4777-b1da-24798d72c27a">
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58807_717_125437_84247">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58807_717_125437_84247_0">
                                                    <gml:pos>457849.005 5439083 114.375</gml:pos>
                                                    <gml:pos>457849.005 5439083.2 114.375</gml:pos>
                                                    <gml:pos>457849.005 5439083.2 112</gml:pos>
                                                    <gml:pos>457849.005 5439083 112</gml:pos>
                                                    <gml:pos>457849.005 5439083 114.375</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58808_349_692294_125678">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58808_349_692294_125678_0">
                                                    <gml:pos>457852.21 5439083 114</gml:pos>
                                                    <gml:pos>457852.21 5439083.12 114</gml:pos>
                                                    <gml:pos>457852.21 5439083.12 112.8</gml:pos>
                                                    <gml:pos>457852.21 5439083 112.8</gml:pos>
                                                    <gml:pos>457852.21 5439083 114</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58809_472_527501_416856">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58809_472_527501_416856_0">
                                                    <gml:pos>457845.79 5439083 114</gml:pos>
                                                    <gml:pos>457845.79 5439083.12 114</gml:pos>
                                                    <gml:pos>457845.79 5439083.12 112.8</gml:pos>
                                                    <gml:pos>457845.79 5439083 112.8</gml:pos>
                                                    <gml:pos>457845.79 5439083 114</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58810_1807_553097_148846">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58810_1807_553097_148846_0">
                                                    <gml:pos>457845.79 5439083 112.8</gml:pos>
                                                    <gml:pos>457845.79 5439083.12 112.8</gml:pos>
                                                    <gml:pos>457843.79 5439083.12 112.8</gml:pos>
                                                    <gml:pos>457843.79 5439083 112.8</gml:pos>
                                                    <gml:pos>457845.79 5439083 112.8</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58811_1622_73903_56220">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58811_1622_73903_56220_0">
                                                    <gml:pos>457846.995 5439083 114.375</gml:pos>
                                                    <gml:pos>457846.995 5439083.2 114.375</gml:pos>
                                                    <gml:pos>457849.005 5439083.2 114.375</gml:pos>
                                                    <gml:pos>457849.005 5439083 114.375</gml:pos>
                                                    <gml:pos>457846.995 5439083 114.375</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58812_795_350114_216214">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58812_795_350114_216214_0">
                                                    <gml:pos>457843.79 5439083 112.8</gml:pos>
                                                    <gml:pos>457843.79 5439083.12 112.8</gml:pos>
                                                    <gml:pos>457843.79 5439083.12 114</gml:pos>
                                                    <gml:pos>457843.79 5439083 114</gml:pos>
                                                    <gml:pos>457843.79 5439083 112.8</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58813_1099_461650_222485">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58813_1099_461650_222485_0">
                                                    <gml:pos>457846.995 5439083 112</gml:pos>
                                                    <gml:pos>457846.995 5439083.2 112</gml:pos>
                                                    <gml:pos>457846.995 5439083.2 114.375</gml:pos>
                                                    <gml:pos>457846.995 5439083 114.375</gml:pos>
                                                    <gml:pos>457846.995 5439083 112</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58814_1459_649731_52436">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58814_1459_649731_52436_0">
                                                    <gml:pos>457850.21 5439083 114</gml:pos>
                                                    <gml:pos>457850.21 5439083.12 114</gml:pos>
                                                    <gml:pos>457852.21 5439083.12 114</gml:pos>
                                                    <gml:pos>457852.21 5439083 114</gml:pos>
                                                    <gml:pos>457850.21 5439083 114</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58815_691_101880_418020">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58815_691_101880_418020_0">
                                                    <gml:pos>457850.21 5439083 112.8</gml:pos>
                                                    <gml:pos>457850.21 5439083.12 112.8</gml:pos>
                                                    <gml:pos>457850.21 5439083.12 114</gml:pos>
                                                    <gml:pos>457850.21 5439083 114</gml:pos>
                                                    <gml:pos>457850.21 5439083 112.8</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58816_858_312337_86583">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58816_858_312337_86583_0">
                                                    <gml:pos>457846.995 5439083 112</gml:pos>
                                                    <gml:pos>457849.005 5439083 112</gml:pos>
                                                    <gml:pos>457849.005 5439083.2 112</gml:pos>
                                                    <gml:pos>457846.995 5439083.2 112</gml:pos>
                                                    <gml:pos>457846.995 5439083 112</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58817_701_101369_361161">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58817_701_101369_361161_0">
                                                    <gml:pos>457852.21 5439083 112.8</gml:pos>
                                                    <gml:pos>457852.21 5439083.12 112.8</gml:pos>
                                                    <gml:pos>457850.21 5439083.12 112.8</gml:pos>
                                                    <gml:pos>457850.21 5439083 112.8</gml:pos>
                                                    <gml:pos>457852.21 5439083 112.8</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58818_1640_464682_59215">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58818_1640_464682_59215_0">
                                                    <gml:pos>457843.79 5439083 114</gml:pos>
                                                    <gml:pos>457843.79 5439083.12 114</gml:pos>
                                                    <gml:pos>457845.79 5439083.12 114</gml:pos>
                                                    <gml:pos>457845.79 5439083 114</gml:pos>
                                                    <gml:pos>457843.79 5439083 114</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58819_65_364244_211813">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58819_65_364244_211813_0">
                                                    <gml:pos>457854 5439083 115.430940107676</gml:pos>
                                                    <gml:pos>457842 5439083 115.430940107676</gml:pos>
                                                    <gml:pos>457842 5439083 111.8</gml:pos>
                                                    <gml:pos>457854 5439083 111.8</gml:pos>
                                                    <gml:pos>457854 5439083 115.430940107676</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                            <gml:interior>
                                                <gml:LinearRing gml:id="PolyID58819_65_364244_211813_1">
                                                    <gml:pos>457849.005 5439083 114.375</gml:pos>
                                                    <gml:pos>457849.005 5439083 112</gml:pos>
                                                    <gml:pos>457846.995 5439083 112</gml:pos>
                                                    <gml:pos>457846.995 5439083 114.375</gml:pos>
                                                    <gml:pos>457849.005 5439083 114.375</gml:pos>
                                                </gml:LinearRing>
                                            </gml:interior>
                                            <gml:interior>
                                                <gml:LinearRing gml:id="PolyID58819_65_364244_211813_2">
                                                    <gml:pos>457850.21 5439083 114</gml:pos>
                                                    <gml:pos>457852.21 5439083 114</gml:pos>
                                                    <gml:pos>457852.21 5439083 112.8</gml:pos>
                                                    <gml:pos>457850.21 5439083 112.8</gml:pos>
                                                    <gml:pos>457850.21 5439083 114</gml:pos>
                                                </gml:LinearRing>
                                            </gml:interior>
                                            <gml:interior>
                                                <gml:LinearRing gml:id="PolyID58819_65_364244_211813_3">
                                                    <gml:pos>457845.79 5439083 112.8</gml:pos>
                                                    <gml:pos>457843.79 5439083 112.8</gml:pos>
                                                    <gml:pos>457843.79 5439083 114</gml:pos>
                                                    <gml:pos>457845.79 5439083 114</gml:pos>
                                                    <gml:pos>457845.79 5439083 112.8</gml:pos>
                                                </gml:LinearRing>
                                            </gml:interior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                </gml:CompositeSurface>
                            </gml:surfaceMember>
                        </gml:MultiSurface>
                    </bldg:lod3MultiSurface>
                    <bldg:opening>
                        <bldg:Window gml:id="GML_98d9c4f5-9e47-4f0b-95f3-cf31e7520142">
                            <gml:name>Window East</gml:name>
                            <bldg:lod3MultiSurface>
                                <gml:MultiSurface>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58820_1568_227087_210505">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58820_1568_227087_210505_0">
                                                    <gml:pos>457852.21 5439083.12 112.8</gml:pos>
                                                    <gml:pos>457852.21 5439083.12 114</gml:pos>
                                                    <gml:pos>457850.21 5439083.12 114</gml:pos>
                                                    <gml:pos>457850.21 5439083.12 112.8</gml:pos>
                                                    <gml:pos>457852.21 5439083.12 112.8</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                </gml:MultiSurface>
                            </bldg:lod3MultiSurface>
                        </bldg:Window>
                    </bldg:opening>
                    <bldg:opening>
                        <bldg:Window gml:id="GML_d0f329f3-5b05-428d-87c3-945b3868337f">
                            <gml:name>Window West</gml:name>
                            <bldg:lod3MultiSurface>
                                <gml:MultiSurface>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58821_1939_612838_272028">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58821_1939_612838_272028_0">
                                                    <gml:pos>457843.79 5439083.12 112.8</gml:pos>
                                                    <gml:pos>457845.79 5439083.12 112.8</gml:pos>
                                                    <gml:pos>457845.79 5439083.12 114</gml:pos>
                                                    <gml:pos>457843.79 5439083.12 114</gml:pos>
                                                    <gml:pos>457843.79 5439083.12 112.8</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                </gml:MultiSurface>
                            </bldg:lod3MultiSurface>
                        </bldg:Window>
                    </bldg:opening>
                    <bldg:opening>
                        <bldg:Door gml:id="GML_2d6ddf04-ee56-42a1-a9b1-b47e4181a629">
                            <gml:name>Door South</gml:name>
                            <bldg:lod3MultiSurface>
                                <gml:MultiSurface>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58822_551_84845_215911">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58822_551_84845_215911_0">
                                                    <gml:pos>457849.005 5439083.2 112</gml:pos>
                                                    <gml:pos>457849.005 5439083.2 114.375</gml:pos>
                                                    <gml:pos>457846.995 5439083.2 114.375</gml:pos>
                                                    <gml:pos>457846.995 5439083.2 112</gml:pos>
                                                    <gml:pos>457849.005 5439083.2 112</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                </gml:MultiSurface>
                            </bldg:lod3MultiSurface>
                        </bldg:Door>
                    </bldg:opening>
                </bldg:WallSurface>
            </bldg:boundedBy>
            <bldg:boundedBy>
                <bldg:WallSurface gml:id="GML_8e5db638-e46a-4739-a98a-2fc2d39c9069">
                    <gml:name>Outer Wall 3 (East)</gml:name>
                    <bldg:lod3MultiSurface>
                        <gml:MultiSurface>
                            <gml:surfaceMember>
                                <gml:CompositeSurface gml:id="GML_d5729847-0aaf-4fec-8ed4-84a5300e510f">
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58823_570_138008_107322">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58823_570_138008_107322_0">
                                                    <gml:pos>457854 5439087.78869087 115.546846106482</gml:pos>
                                                    <gml:pos>457853.87 5439087.78869087 115.546846106482</gml:pos>
                                                    <gml:pos>457853.87 5439087.75 115.566987298108</gml:pos>
                                                    <gml:pos>457854 5439087.75 115.566987298108</gml:pos>
                                                    <gml:pos>457854 5439087.78869087 115.546846106482</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58824_439_290766_393776">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58824_439_290766_393776_0">
                                                    <gml:pos>457854 5439087.87059048 116.482962913145</gml:pos>
                                                    <gml:pos>457853.87 5439087.87059048 116.482962913145</gml:pos>
                                                    <gml:pos>457853.87 5439087.91317591 116.492403876506</gml:pos>
                                                    <gml:pos>457854 5439087.91317591 116.492403876506</gml:pos>
                                                    <gml:pos>457854 5439087.87059048 116.482962913145</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58825_1589_638409_186400">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58825_1589_638409_186400_0">
                                                    <gml:pos>457854 5439088.04357787 116.498097349046</gml:pos>
                                                    <gml:pos>457853.87 5439088.04357787 116.498097349046</gml:pos>
                                                    <gml:pos>457853.87 5439088.08682409 116.492403876506</gml:pos>
                                                    <gml:pos>457854 5439088.08682409 116.492403876506</gml:pos>
                                                    <gml:pos>457854 5439088.04357787 116.498097349046</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58826_766_578922_7381">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58826_766_578922_7381_0">
                                                    <gml:pos>457854 5439088.45315389 116.21130913087</gml:pos>
                                                    <gml:pos>457853.87 5439088.45315389 116.21130913087</gml:pos>
                                                    <gml:pos>457853.87 5439088.46984631 116.171010071663</gml:pos>
                                                    <gml:pos>457854 5439088.46984631 116.171010071663</gml:pos>
                                                    <gml:pos>457854 5439088.45315389 116.21130913087</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58827_1782_330817_188911">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58827_1782_330817_188911_0">
                                                    <gml:pos>457854 5439088.49240388 115.913175911167</gml:pos>
                                                    <gml:pos>457853.87 5439088.49240388 115.913175911167</gml:pos>
                                                    <gml:pos>457853.87 5439088.48296291 115.870590477449</gml:pos>
                                                    <gml:pos>457854 5439088.48296291 115.870590477449</gml:pos>
                                                    <gml:pos>457854 5439088.49240388 115.913175911167</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58828_1522_308899_25855">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58828_1522_308899_25855_0">
                                                    <gml:pos>457854 5439088.49809735 115.956422128626</gml:pos>
                                                    <gml:pos>457853.87 5439088.49809735 115.956422128626</gml:pos>
                                                    <gml:pos>457853.87 5439088.49240388 115.913175911167</gml:pos>
                                                    <gml:pos>457854 5439088.49240388 115.913175911167</gml:pos>
                                                    <gml:pos>457854 5439088.49809735 115.956422128626</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58829_953_361344_310520">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58829_953_361344_310520_0">
                                                    <gml:pos>457854 5439088.40957602 115.713211781824</gml:pos>
                                                    <gml:pos>457853.87 5439088.40957602 115.713211781824</gml:pos>
                                                    <gml:pos>457853.87 5439088.38302222 115.678606195157</gml:pos>
                                                    <gml:pos>457854 5439088.38302222 115.678606195157</gml:pos>
                                                    <gml:pos>457854 5439088.40957602 115.713211781824</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58830_12_172372_362873">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58830_12_172372_362873_0">
                                                    <gml:pos>457854 5439087.50759612 115.913175911167</gml:pos>
                                                    <gml:pos>457853.87 5439087.50759612 115.913175911167</gml:pos>
                                                    <gml:pos>457853.87 5439087.50190265 115.956422128626</gml:pos>
                                                    <gml:pos>457854 5439087.50190265 115.956422128626</gml:pos>
                                                    <gml:pos>457854 5439087.50759612 115.913175911167</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58831_1162_492249_346547">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58831_1162_492249_346547_0">
                                                    <gml:pos>457854 5439087.59042398 116.286788218176</gml:pos>
                                                    <gml:pos>457853.87 5439087.59042398 116.286788218176</gml:pos>
                                                    <gml:pos>457853.87 5439087.61697778 116.321393804843</gml:pos>
                                                    <gml:pos>457854 5439087.61697778 116.321393804843</gml:pos>
                                                    <gml:pos>457854 5439087.59042398 116.286788218176</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58832_649_212520_225345">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58832_649_212520_225345_0">
                                                    <gml:pos>457854 5439087.91317591 115.507596123494</gml:pos>
                                                    <gml:pos>457853.87 5439087.91317591 115.507596123494</gml:pos>
                                                    <gml:pos>457853.87 5439087.87059048 115.517037086855</gml:pos>
                                                    <gml:pos>457854 5439087.87059048 115.517037086855</gml:pos>
                                                    <gml:pos>457854 5439087.91317591 115.507596123494</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58833_1455_377730_279429">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58833_1455_377730_279429_0">
                                                    <gml:pos>457854 5439087.54684611 116.21130913087</gml:pos>
                                                    <gml:pos>457853.87 5439087.54684611 116.21130913087</gml:pos>
                                                    <gml:pos>457853.87 5439087.5669873 116.25</gml:pos>
                                                    <gml:pos>457854 5439087.5669873 116.25</gml:pos>
                                                    <gml:pos>457854 5439087.54684611 116.21130913087</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58834_336_613769_96500">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58834_336_613769_96500_0">
                                                    <gml:pos>457854 5439087.59042398 115.713211781824</gml:pos>
                                                    <gml:pos>457853.87 5439087.59042398 115.713211781824</gml:pos>
                                                    <gml:pos>457853.87 5439087.5669873 115.75</gml:pos>
                                                    <gml:pos>457854 5439087.5669873 115.75</gml:pos>
                                                    <gml:pos>457854 5439087.59042398 115.713211781824</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58835_1650_623307_361645">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58835_1650_623307_361645_0">
                                                    <gml:pos>457854 5439088.21130913 115.546846106482</gml:pos>
                                                    <gml:pos>457853.87 5439088.21130913 115.546846106482</gml:pos>
                                                    <gml:pos>457853.87 5439088.17101007 115.530153689607</gml:pos>
                                                    <gml:pos>457854 5439088.17101007 115.530153689607</gml:pos>
                                                    <gml:pos>457854 5439088.21130913 115.546846106482</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58836_1456_865943_237319">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58836_1456_865943_237319_0">
                                                    <gml:pos>457854 5439087.78869087 116.453153893518</gml:pos>
                                                    <gml:pos>457853.87 5439087.78869087 116.453153893518</gml:pos>
                                                    <gml:pos>457853.87 5439087.82898993 116.469846310393</gml:pos>
                                                    <gml:pos>457854 5439087.82898993 116.469846310393</gml:pos>
                                                    <gml:pos>457854 5439087.78869087 116.453153893518</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58837_1583_77116_269612">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58837_1583_77116_269612_0">
                                                    <gml:pos>457854 5439088.21130913 116.453153893518</gml:pos>
                                                    <gml:pos>457853.87 5439088.21130913 116.453153893518</gml:pos>
                                                    <gml:pos>457853.87 5439088.25 116.433012701892</gml:pos>
                                                    <gml:pos>457854 5439088.25 116.433012701892</gml:pos>
                                                    <gml:pos>457854 5439088.21130913 116.453153893518</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58838_707_749636_174617">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58838_707_749636_174617_0">
                                                    <gml:pos>457854 5439088.28678822 115.590423977855</gml:pos>
                                                    <gml:pos>457853.87 5439088.28678822 115.590423977855</gml:pos>
                                                    <gml:pos>457853.87 5439088.25 115.566987298108</gml:pos>
                                                    <gml:pos>457854 5439088.25 115.566987298108</gml:pos>
                                                    <gml:pos>457854 5439088.28678822 115.590423977855</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58839_1996_96543_56894">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58839_1996_96543_56894_0">
                                                    <gml:pos>457854 5439087.51703709 115.870590477449</gml:pos>
                                                    <gml:pos>457853.87 5439087.51703709 115.870590477449</gml:pos>
                                                    <gml:pos>457853.87 5439087.50759612 115.913175911167</gml:pos>
                                                    <gml:pos>457854 5439087.50759612 115.913175911167</gml:pos>
                                                    <gml:pos>457854 5439087.51703709 115.870590477449</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58840_1096_855539_244285">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58840_1096_855539_244285_0">
                                                    <gml:pos>457854 5439088.46984631 116.171010071663</gml:pos>
                                                    <gml:pos>457853.87 5439088.46984631 116.171010071663</gml:pos>
                                                    <gml:pos>457853.87 5439088.48296291 116.129409522551</gml:pos>
                                                    <gml:pos>457854 5439088.48296291 116.129409522551</gml:pos>
                                                    <gml:pos>457854 5439088.46984631 116.171010071663</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58841_392_624392_8151">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58841_392_624392_8151_0">
                                                    <gml:pos>457854 5439087.61697778 115.678606195157</gml:pos>
                                                    <gml:pos>457853.87 5439087.61697778 115.678606195157</gml:pos>
                                                    <gml:pos>457853.87 5439087.59042398 115.713211781824</gml:pos>
                                                    <gml:pos>457854 5439087.59042398 115.713211781824</gml:pos>
                                                    <gml:pos>457854 5439087.61697778 115.678606195157</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58842_1595_491700_282570">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58842_1595_491700_282570_0">
                                                    <gml:pos>457854 5439088.48296291 116.129409522551</gml:pos>
                                                    <gml:pos>457853.87 5439088.48296291 116.129409522551</gml:pos>
                                                    <gml:pos>457853.87 5439088.49240388 116.086824088833</gml:pos>
                                                    <gml:pos>457854 5439088.49240388 116.086824088833</gml:pos>
                                                    <gml:pos>457854 5439088.48296291 116.129409522551</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58843_1862_478243_373176">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58843_1862_478243_373176_0">
                                                    <gml:pos>457854 5439088.04357787 115.501902650954</gml:pos>
                                                    <gml:pos>457853.87 5439088.04357787 115.501902650954</gml:pos>
                                                    <gml:pos>457853.87 5439088 115.5</gml:pos>
                                                    <gml:pos>457854 5439088 115.5</gml:pos>
                                                    <gml:pos>457854 5439088.04357787 115.501902650954</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58844_293_862667_200176">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58844_293_862667_200176_0">
                                                    <gml:pos>457854 5439088.3213938 115.616977778441</gml:pos>
                                                    <gml:pos>457853.87 5439088.3213938 115.616977778441</gml:pos>
                                                    <gml:pos>457853.87 5439088.28678822 115.590423977855</gml:pos>
                                                    <gml:pos>457854 5439088.28678822 115.590423977855</gml:pos>
                                                    <gml:pos>457854 5439088.3213938 115.616977778441</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58845_1505_99391_314552">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58845_1505_99391_314552_0">
                                                    <gml:pos>457854 5439088.40957602 116.286788218176</gml:pos>
                                                    <gml:pos>457853.87 5439088.40957602 116.286788218176</gml:pos>
                                                    <gml:pos>457853.87 5439088.4330127 116.25</gml:pos>
                                                    <gml:pos>457854 5439088.4330127 116.25</gml:pos>
                                                    <gml:pos>457854 5439088.40957602 116.286788218176</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58846_1607_419091_114346">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58846_1607_419091_114346_0">
                                                    <gml:pos>457854 5439087.64644661 116.353553390593</gml:pos>
                                                    <gml:pos>457853.87 5439087.64644661 116.353553390593</gml:pos>
                                                    <gml:pos>457853.87 5439087.6786062 116.383022221559</gml:pos>
                                                    <gml:pos>457854 5439087.6786062 116.383022221559</gml:pos>
                                                    <gml:pos>457854 5439087.64644661 116.353553390593</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58847_90_140038_409167">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58847_90_140038_409167_0">
                                                    <gml:pos>457854 5439088.28678822 116.409576022145</gml:pos>
                                                    <gml:pos>457853.87 5439088.28678822 116.409576022145</gml:pos>
                                                    <gml:pos>457853.87 5439088.3213938 116.383022221559</gml:pos>
                                                    <gml:pos>457854 5439088.3213938 116.383022221559</gml:pos>
                                                    <gml:pos>457854 5439088.28678822 116.409576022145</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58848_1643_368347_161032">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58848_1643_368347_161032_0">
                                                    <gml:pos>457854 5439088.49240388 116.086824088833</gml:pos>
                                                    <gml:pos>457853.87 5439088.49240388 116.086824088833</gml:pos>
                                                    <gml:pos>457853.87 5439088.49809735 116.043577871374</gml:pos>
                                                    <gml:pos>457854 5439088.49809735 116.043577871374</gml:pos>
                                                    <gml:pos>457854 5439088.49240388 116.086824088833</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58849_1037_422449_337587">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58849_1037_422449_337587_0">
                                                    <gml:pos>457854 5439087.53015369 115.828989928337</gml:pos>
                                                    <gml:pos>457853.87 5439087.53015369 115.828989928337</gml:pos>
                                                    <gml:pos>457853.87 5439087.51703709 115.870590477449</gml:pos>
                                                    <gml:pos>457854 5439087.51703709 115.870590477449</gml:pos>
                                                    <gml:pos>457854 5439087.53015369 115.828989928337</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58850_1035_615015_167623">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58850_1035_615015_167623_0">
                                                    <gml:pos>457854 5439088.35355339 116.353553390593</gml:pos>
                                                    <gml:pos>457853.87 5439088.35355339 116.353553390593</gml:pos>
                                                    <gml:pos>457853.87 5439088.38302222 116.321393804843</gml:pos>
                                                    <gml:pos>457854 5439088.38302222 116.321393804843</gml:pos>
                                                    <gml:pos>457854 5439088.35355339 116.353553390593</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58851_1589_798265_173568">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58851_1589_798265_173568_0">
                                                    <gml:pos>457854 5439088.17101007 115.530153689607</gml:pos>
                                                    <gml:pos>457853.87 5439088.17101007 115.530153689607</gml:pos>
                                                    <gml:pos>457853.87 5439088.12940952 115.517037086855</gml:pos>
                                                    <gml:pos>457854 5439088.12940952 115.517037086855</gml:pos>
                                                    <gml:pos>457854 5439088.17101007 115.530153689607</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58852_1403_247001_10369">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58852_1403_247001_10369_0">
                                                    <gml:pos>457854 5439087.87059048 115.517037086855</gml:pos>
                                                    <gml:pos>457853.87 5439087.87059048 115.517037086855</gml:pos>
                                                    <gml:pos>457853.87 5439087.82898993 115.530153689607</gml:pos>
                                                    <gml:pos>457854 5439087.82898993 115.530153689607</gml:pos>
                                                    <gml:pos>457854 5439087.87059048 115.517037086855</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58853_1615_480753_1407">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58853_1615_480753_1407_0">
                                                    <gml:pos>457854 5439088.38302222 116.321393804843</gml:pos>
                                                    <gml:pos>457853.87 5439088.38302222 116.321393804843</gml:pos>
                                                    <gml:pos>457853.87 5439088.40957602 116.286788218176</gml:pos>
                                                    <gml:pos>457854 5439088.40957602 116.286788218176</gml:pos>
                                                    <gml:pos>457854 5439088.38302222 116.321393804843</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58854_1444_67764_179010">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58854_1444_67764_179010_0">
                                                    <gml:pos>457854 5439088.08682409 115.507596123494</gml:pos>
                                                    <gml:pos>457853.87 5439088.08682409 115.507596123494</gml:pos>
                                                    <gml:pos>457853.87 5439088.04357787 115.501902650954</gml:pos>
                                                    <gml:pos>457854 5439088.04357787 115.501902650954</gml:pos>
                                                    <gml:pos>457854 5439088.08682409 115.507596123494</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58855_566_600693_272738">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58855_566_600693_272738_0">
                                                    <gml:pos>457854 5439087.5669873 115.75</gml:pos>
                                                    <gml:pos>457853.87 5439087.5669873 115.75</gml:pos>
                                                    <gml:pos>457853.87 5439087.54684611 115.78869086913</gml:pos>
                                                    <gml:pos>457854 5439087.54684611 115.78869086913</gml:pos>
                                                    <gml:pos>457854 5439087.5669873 115.75</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58856_812_63520_36025">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58856_812_63520_36025_0">
                                                    <gml:pos>457854 5439087.5 116</gml:pos>
                                                    <gml:pos>457853.87 5439087.5 116</gml:pos>
                                                    <gml:pos>457853.87 5439087.50190265 116.043577871374</gml:pos>
                                                    <gml:pos>457854 5439087.50190265 116.043577871374</gml:pos>
                                                    <gml:pos>457854 5439087.5 116</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58857_553_455273_290230">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58857_553_455273_290230_0">
                                                    <gml:pos>457854 5439088.4330127 116.25</gml:pos>
                                                    <gml:pos>457853.87 5439088.4330127 116.25</gml:pos>
                                                    <gml:pos>457853.87 5439088.45315389 116.21130913087</gml:pos>
                                                    <gml:pos>457854 5439088.45315389 116.21130913087</gml:pos>
                                                    <gml:pos>457854 5439088.4330127 116.25</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58858_311_115612_297940">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58858_311_115612_297940_0">
                                                    <gml:pos>457854 5439088.46984631 115.828989928337</gml:pos>
                                                    <gml:pos>457853.87 5439088.46984631 115.828989928337</gml:pos>
                                                    <gml:pos>457853.87 5439088.45315389 115.78869086913</gml:pos>
                                                    <gml:pos>457854 5439088.45315389 115.78869086913</gml:pos>
                                                    <gml:pos>457854 5439088.46984631 115.828989928337</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58859_1105_560490_423671">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58859_1105_560490_423671_0">
                                                    <gml:pos>457854 5439087.6786062 116.383022221559</gml:pos>
                                                    <gml:pos>457853.87 5439087.6786062 116.383022221559</gml:pos>
                                                    <gml:pos>457853.87 5439087.71321178 116.409576022145</gml:pos>
                                                    <gml:pos>457854 5439087.71321178 116.409576022145</gml:pos>
                                                    <gml:pos>457854 5439087.6786062 116.383022221559</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58860_1517_658785_414270">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58860_1517_658785_414270_0">
                                                    <gml:pos>457854 5439088 116.5</gml:pos>
                                                    <gml:pos>457853.87 5439088 116.5</gml:pos>
                                                    <gml:pos>457853.87 5439088.04357787 116.498097349046</gml:pos>
                                                    <gml:pos>457854 5439088.04357787 116.498097349046</gml:pos>
                                                    <gml:pos>457854 5439088 116.5</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58861_396_754114_102807">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58861_396_754114_102807_0">
                                                    <gml:pos>457854 5439088.12940952 116.482962913145</gml:pos>
                                                    <gml:pos>457853.87 5439088.12940952 116.482962913145</gml:pos>
                                                    <gml:pos>457853.87 5439088.17101007 116.469846310393</gml:pos>
                                                    <gml:pos>457854 5439088.17101007 116.469846310393</gml:pos>
                                                    <gml:pos>457854 5439088.12940952 116.482962913145</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58862_1508_661324_353344">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58862_1508_661324_353344_0">
                                                    <gml:pos>457854 5439087.82898993 115.530153689607</gml:pos>
                                                    <gml:pos>457853.87 5439087.82898993 115.530153689607</gml:pos>
                                                    <gml:pos>457853.87 5439087.78869087 115.546846106482</gml:pos>
                                                    <gml:pos>457854 5439087.78869087 115.546846106482</gml:pos>
                                                    <gml:pos>457854 5439087.82898993 115.530153689607</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58863_1761_209205_363750">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58863_1761_209205_363750_0">
                                                    <gml:pos>457854 5439087.95642213 115.501902650954</gml:pos>
                                                    <gml:pos>457853.87 5439087.95642213 115.501902650954</gml:pos>
                                                    <gml:pos>457853.87 5439087.91317591 115.507596123494</gml:pos>
                                                    <gml:pos>457854 5439087.91317591 115.507596123494</gml:pos>
                                                    <gml:pos>457854 5439087.95642213 115.501902650954</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58864_266_525477_126998">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58864_266_525477_126998_0">
                                                    <gml:pos>457854 5439087.50190265 115.956422128626</gml:pos>
                                                    <gml:pos>457853.87 5439087.50190265 115.956422128626</gml:pos>
                                                    <gml:pos>457853.87 5439087.5 116</gml:pos>
                                                    <gml:pos>457854 5439087.5 116</gml:pos>
                                                    <gml:pos>457854 5439087.50190265 115.956422128626</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58865_526_15168_228546">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58865_526_15168_228546_0">
                                                    <gml:pos>457854 5439088.45315389 115.78869086913</gml:pos>
                                                    <gml:pos>457853.87 5439088.45315389 115.78869086913</gml:pos>
                                                    <gml:pos>457853.87 5439088.4330127 115.75</gml:pos>
                                                    <gml:pos>457854 5439088.4330127 115.75</gml:pos>
                                                    <gml:pos>457854 5439088.45315389 115.78869086913</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58866_156_725100_178245">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58866_156_725100_178245_0">
                                                    <gml:pos>457854 5439088.25 116.433012701892</gml:pos>
                                                    <gml:pos>457853.87 5439088.25 116.433012701892</gml:pos>
                                                    <gml:pos>457853.87 5439088.28678822 116.409576022145</gml:pos>
                                                    <gml:pos>457854 5439088.28678822 116.409576022145</gml:pos>
                                                    <gml:pos>457854 5439088.25 116.433012701892</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58867_1367_497472_296633">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58867_1367_497472_296633_0">
                                                    <gml:pos>457854 5439088.25 115.566987298108</gml:pos>
                                                    <gml:pos>457853.87 5439088.25 115.566987298108</gml:pos>
                                                    <gml:pos>457853.87 5439088.21130913 115.546846106482</gml:pos>
                                                    <gml:pos>457854 5439088.21130913 115.546846106482</gml:pos>
                                                    <gml:pos>457854 5439088.25 115.566987298108</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58868_724_854947_375866">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58868_724_854947_375866_0">
                                                    <gml:pos>457854 5439087.95642213 116.498097349046</gml:pos>
                                                    <gml:pos>457853.87 5439087.95642213 116.498097349046</gml:pos>
                                                    <gml:pos>457853.87 5439088 116.5</gml:pos>
                                                    <gml:pos>457854 5439088 116.5</gml:pos>
                                                    <gml:pos>457854 5439087.95642213 116.498097349046</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58869_119_43728_205515">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58869_119_43728_205515_0">
                                                    <gml:pos>457854 5439088 115.5</gml:pos>
                                                    <gml:pos>457853.87 5439088 115.5</gml:pos>
                                                    <gml:pos>457853.87 5439087.95642213 115.501902650954</gml:pos>
                                                    <gml:pos>457854 5439087.95642213 115.501902650954</gml:pos>
                                                    <gml:pos>457854 5439088 115.5</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58870_1860_438101_33103">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58870_1860_438101_33103_0">
                                                    <gml:pos>457854 5439088.3213938 116.383022221559</gml:pos>
                                                    <gml:pos>457853.87 5439088.3213938 116.383022221559</gml:pos>
                                                    <gml:pos>457853.87 5439088.35355339 116.353553390593</gml:pos>
                                                    <gml:pos>457854 5439088.35355339 116.353553390593</gml:pos>
                                                    <gml:pos>457854 5439088.3213938 116.383022221559</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58871_329_330460_187159">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58871_329_330460_187159_0">
                                                    <gml:pos>457854 5439088.5 116</gml:pos>
                                                    <gml:pos>457853.87 5439088.5 116</gml:pos>
                                                    <gml:pos>457853.87 5439088.49809735 115.956422128626</gml:pos>
                                                    <gml:pos>457854 5439088.49809735 115.956422128626</gml:pos>
                                                    <gml:pos>457854 5439088.5 116</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58872_1035_165516_336492">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58872_1035_165516_336492_0">
                                                    <gml:pos>457854 5439087.50759612 116.086824088833</gml:pos>
                                                    <gml:pos>457853.87 5439087.50759612 116.086824088833</gml:pos>
                                                    <gml:pos>457853.87 5439087.51703709 116.129409522551</gml:pos>
                                                    <gml:pos>457854 5439087.51703709 116.129409522551</gml:pos>
                                                    <gml:pos>457854 5439087.50759612 116.086824088833</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58873_1558_279514_43072">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58873_1558_279514_43072_0">
                                                    <gml:pos>457854 5439088.17101007 116.469846310393</gml:pos>
                                                    <gml:pos>457853.87 5439088.17101007 116.469846310393</gml:pos>
                                                    <gml:pos>457853.87 5439088.21130913 116.453153893518</gml:pos>
                                                    <gml:pos>457854 5439088.21130913 116.453153893518</gml:pos>
                                                    <gml:pos>457854 5439088.17101007 116.469846310393</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58874_707_866165_220822">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58874_707_866165_220822_0">
                                                    <gml:pos>457854 5439087.50190265 116.043577871374</gml:pos>
                                                    <gml:pos>457853.87 5439087.50190265 116.043577871374</gml:pos>
                                                    <gml:pos>457853.87 5439087.50759612 116.086824088833</gml:pos>
                                                    <gml:pos>457854 5439087.50759612 116.086824088833</gml:pos>
                                                    <gml:pos>457854 5439087.50190265 116.043577871374</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58875_248_685565_291683">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58875_248_685565_291683_0">
                                                    <gml:pos>457854 5439088.08682409 116.492403876506</gml:pos>
                                                    <gml:pos>457853.87 5439088.08682409 116.492403876506</gml:pos>
                                                    <gml:pos>457853.87 5439088.12940952 116.482962913145</gml:pos>
                                                    <gml:pos>457854 5439088.12940952 116.482962913145</gml:pos>
                                                    <gml:pos>457854 5439088.08682409 116.492403876506</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58876_1646_212881_351851">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58876_1646_212881_351851_0">
                                                    <gml:pos>457854 5439088.35355339 115.646446609407</gml:pos>
                                                    <gml:pos>457853.87 5439088.35355339 115.646446609407</gml:pos>
                                                    <gml:pos>457853.87 5439088.3213938 115.616977778441</gml:pos>
                                                    <gml:pos>457854 5439088.3213938 115.616977778441</gml:pos>
                                                    <gml:pos>457854 5439088.35355339 115.646446609407</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58877_462_742322_212929">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58877_462_742322_212929_0">
                                                    <gml:pos>457854 5439088.4330127 115.75</gml:pos>
                                                    <gml:pos>457853.87 5439088.4330127 115.75</gml:pos>
                                                    <gml:pos>457853.87 5439088.40957602 115.713211781824</gml:pos>
                                                    <gml:pos>457854 5439088.40957602 115.713211781824</gml:pos>
                                                    <gml:pos>457854 5439088.4330127 115.75</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58878_1780_17352_191996">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58878_1780_17352_191996_0">
                                                    <gml:pos>457854 5439087.82898993 116.469846310393</gml:pos>
                                                    <gml:pos>457853.87 5439087.82898993 116.469846310393</gml:pos>
                                                    <gml:pos>457853.87 5439087.87059048 116.482962913145</gml:pos>
                                                    <gml:pos>457854 5439087.87059048 116.482962913145</gml:pos>
                                                    <gml:pos>457854 5439087.82898993 116.469846310393</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58879_886_751312_428816">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58879_886_751312_428816_0">
                                                    <gml:pos>457854 5439087.71321178 116.409576022145</gml:pos>
                                                    <gml:pos>457853.87 5439087.71321178 116.409576022145</gml:pos>
                                                    <gml:pos>457853.87 5439087.75 116.433012701892</gml:pos>
                                                    <gml:pos>457854 5439087.75 116.433012701892</gml:pos>
                                                    <gml:pos>457854 5439087.71321178 116.409576022145</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58880_589_43766_423768">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58880_589_43766_423768_0">
                                                    <gml:pos>457854 5439087.54684611 115.78869086913</gml:pos>
                                                    <gml:pos>457853.87 5439087.54684611 115.78869086913</gml:pos>
                                                    <gml:pos>457853.87 5439087.53015369 115.828989928337</gml:pos>
                                                    <gml:pos>457854 5439087.53015369 115.828989928337</gml:pos>
                                                    <gml:pos>457854 5439087.54684611 115.78869086913</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58881_71_863606_40602">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58881_71_863606_40602_0">
                                                    <gml:pos>457854 5439087.51703709 116.129409522551</gml:pos>
                                                    <gml:pos>457853.87 5439087.51703709 116.129409522551</gml:pos>
                                                    <gml:pos>457853.87 5439087.53015369 116.171010071663</gml:pos>
                                                    <gml:pos>457854 5439087.53015369 116.171010071663</gml:pos>
                                                    <gml:pos>457854 5439087.51703709 116.129409522551</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58882_1691_230441_40603">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58882_1691_230441_40603_0">
                                                    <gml:pos>457854 5439087.6786062 115.616977778441</gml:pos>
                                                    <gml:pos>457853.87 5439087.6786062 115.616977778441</gml:pos>
                                                    <gml:pos>457853.87 5439087.64644661 115.646446609407</gml:pos>
                                                    <gml:pos>457854 5439087.64644661 115.646446609407</gml:pos>
                                                    <gml:pos>457854 5439087.6786062 115.616977778441</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58883_1037_687764_121563">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58883_1037_687764_121563_0">
                                                    <gml:pos>457854 5439087.75 116.433012701892</gml:pos>
                                                    <gml:pos>457853.87 5439087.75 116.433012701892</gml:pos>
                                                    <gml:pos>457853.87 5439087.78869087 116.453153893518</gml:pos>
                                                    <gml:pos>457854 5439087.78869087 116.453153893518</gml:pos>
                                                    <gml:pos>457854 5439087.75 116.433012701892</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58884_1627_899224_389116">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58884_1627_899224_389116_0">
                                                    <gml:pos>457854 5439088.12940952 115.517037086855</gml:pos>
                                                    <gml:pos>457853.87 5439088.12940952 115.517037086855</gml:pos>
                                                    <gml:pos>457853.87 5439088.08682409 115.507596123494</gml:pos>
                                                    <gml:pos>457854 5439088.08682409 115.507596123494</gml:pos>
                                                    <gml:pos>457854 5439088.12940952 115.517037086855</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58885_876_846116_316992">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58885_876_846116_316992_0">
                                                    <gml:pos>457854 5439088.49809735 116.043577871374</gml:pos>
                                                    <gml:pos>457853.87 5439088.49809735 116.043577871374</gml:pos>
                                                    <gml:pos>457853.87 5439088.5 116</gml:pos>
                                                    <gml:pos>457854 5439088.5 116</gml:pos>
                                                    <gml:pos>457854 5439088.49809735 116.043577871374</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58886_933_860878_127263">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58886_933_860878_127263_0">
                                                    <gml:pos>457854 5439087.64644661 115.646446609407</gml:pos>
                                                    <gml:pos>457853.87 5439087.64644661 115.646446609407</gml:pos>
                                                    <gml:pos>457853.87 5439087.61697778 115.678606195157</gml:pos>
                                                    <gml:pos>457854 5439087.61697778 115.678606195157</gml:pos>
                                                    <gml:pos>457854 5439087.64644661 115.646446609407</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58887_1993_113857_311904">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58887_1993_113857_311904_0">
                                                    <gml:pos>457854 5439088.38302222 115.678606195157</gml:pos>
                                                    <gml:pos>457853.87 5439088.38302222 115.678606195157</gml:pos>
                                                    <gml:pos>457853.87 5439088.35355339 115.646446609407</gml:pos>
                                                    <gml:pos>457854 5439088.35355339 115.646446609407</gml:pos>
                                                    <gml:pos>457854 5439088.38302222 115.678606195157</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58888_351_262296_414185">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58888_351_262296_414185_0">
                                                    <gml:pos>457854 5439087.61697778 116.321393804843</gml:pos>
                                                    <gml:pos>457853.87 5439087.61697778 116.321393804843</gml:pos>
                                                    <gml:pos>457853.87 5439087.64644661 116.353553390593</gml:pos>
                                                    <gml:pos>457854 5439087.64644661 116.353553390593</gml:pos>
                                                    <gml:pos>457854 5439087.61697778 116.321393804843</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58889_1267_437476_45575">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58889_1267_437476_45575_0">
                                                    <gml:pos>457854 5439087.71321178 115.590423977855</gml:pos>
                                                    <gml:pos>457853.87 5439087.71321178 115.590423977855</gml:pos>
                                                    <gml:pos>457853.87 5439087.6786062 115.616977778441</gml:pos>
                                                    <gml:pos>457854 5439087.6786062 115.616977778441</gml:pos>
                                                    <gml:pos>457854 5439087.71321178 115.590423977855</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58890_1586_699919_109999">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58890_1586_699919_109999_0">
                                                    <gml:pos>457854 5439088.48296291 115.870590477449</gml:pos>
                                                    <gml:pos>457853.87 5439088.48296291 115.870590477449</gml:pos>
                                                    <gml:pos>457853.87 5439088.46984631 115.828989928337</gml:pos>
                                                    <gml:pos>457854 5439088.46984631 115.828989928337</gml:pos>
                                                    <gml:pos>457854 5439088.48296291 115.870590477449</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58891_1570_83267_355718">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58891_1570_83267_355718_0">
                                                    <gml:pos>457854 5439087.75 115.566987298108</gml:pos>
                                                    <gml:pos>457853.87 5439087.75 115.566987298108</gml:pos>
                                                    <gml:pos>457853.87 5439087.71321178 115.590423977855</gml:pos>
                                                    <gml:pos>457854 5439087.71321178 115.590423977855</gml:pos>
                                                    <gml:pos>457854 5439087.75 115.566987298108</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58892_1716_836988_157786">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58892_1716_836988_157786_0">
                                                    <gml:pos>457854 5439087.5669873 116.25</gml:pos>
                                                    <gml:pos>457853.87 5439087.5669873 116.25</gml:pos>
                                                    <gml:pos>457853.87 5439087.59042398 116.286788218176</gml:pos>
                                                    <gml:pos>457854 5439087.59042398 116.286788218176</gml:pos>
                                                    <gml:pos>457854 5439087.5669873 116.25</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58893_523_674478_365336">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58893_523_674478_365336_0">
                                                    <gml:pos>457854 5439087.91317591 116.492403876506</gml:pos>
                                                    <gml:pos>457853.87 5439087.91317591 116.492403876506</gml:pos>
                                                    <gml:pos>457853.87 5439087.95642213 116.498097349046</gml:pos>
                                                    <gml:pos>457854 5439087.95642213 116.498097349046</gml:pos>
                                                    <gml:pos>457854 5439087.91317591 116.492403876506</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58894_1798_71247_374813">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58894_1798_71247_374813_0">
                                                    <gml:pos>457854 5439087.53015369 116.171010071663</gml:pos>
                                                    <gml:pos>457853.87 5439087.53015369 116.171010071663</gml:pos>
                                                    <gml:pos>457853.87 5439087.54684611 116.21130913087</gml:pos>
                                                    <gml:pos>457854 5439087.54684611 116.21130913087</gml:pos>
                                                    <gml:pos>457854 5439087.53015369 116.171010071663</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58895_177_729072_1359">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58895_177_729072_1359_0">
                                                    <gml:pos>457854 5439091.50239372 112.8</gml:pos>
                                                    <gml:pos>457853.88 5439091.50239372 112.8</gml:pos>
                                                    <gml:pos>457853.88 5439089.50239372 112.8</gml:pos>
                                                    <gml:pos>457854 5439089.50239372 112.8</gml:pos>
                                                    <gml:pos>457854 5439091.50239372 112.8</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58896_1460_485806_164686">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58896_1460_485806_164686_0">
                                                    <gml:pos>457854 5439091.50239372 114</gml:pos>
                                                    <gml:pos>457853.88 5439091.50239372 114</gml:pos>
                                                    <gml:pos>457853.88 5439091.50239372 112.8</gml:pos>
                                                    <gml:pos>457854 5439091.50239372 112.8</gml:pos>
                                                    <gml:pos>457854 5439091.50239372 114</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58897_1185_441649_380580">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58897_1185_441649_380580_0">
                                                    <gml:pos>457854 5439086.205 112.8</gml:pos>
                                                    <gml:pos>457853.88 5439086.205 112.8</gml:pos>
                                                    <gml:pos>457853.88 5439084.205 112.8</gml:pos>
                                                    <gml:pos>457854 5439084.205 112.8</gml:pos>
                                                    <gml:pos>457854 5439086.205 112.8</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58898_158_629934_409515">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58898_158_629934_409515_0">
                                                    <gml:pos>457854 5439089.50239372 114</gml:pos>
                                                    <gml:pos>457853.88 5439089.50239372 114</gml:pos>
                                                    <gml:pos>457853.88 5439091.50239372 114</gml:pos>
                                                    <gml:pos>457854 5439091.50239372 114</gml:pos>
                                                    <gml:pos>457854 5439089.50239372 114</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58899_1460_365559_91271">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58899_1460_365559_91271_0">
                                                    <gml:pos>457854 5439089.50239372 112.8</gml:pos>
                                                    <gml:pos>457853.88 5439089.50239372 112.8</gml:pos>
                                                    <gml:pos>457853.88 5439089.50239372 114</gml:pos>
                                                    <gml:pos>457854 5439089.50239372 114</gml:pos>
                                                    <gml:pos>457854 5439089.50239372 112.8</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58900_306_139976_395379">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58900_306_139976_395379_0">
                                                    <gml:pos>457854 5439084.205 112.8</gml:pos>
                                                    <gml:pos>457853.88 5439084.205 112.8</gml:pos>
                                                    <gml:pos>457853.88 5439084.205 114</gml:pos>
                                                    <gml:pos>457854 5439084.205 114</gml:pos>
                                                    <gml:pos>457854 5439084.205 112.8</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58901_953_311220_138631">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58901_953_311220_138631_0">
                                                    <gml:pos>457854 5439084.205 114</gml:pos>
                                                    <gml:pos>457853.88 5439084.205 114</gml:pos>
                                                    <gml:pos>457853.88 5439086.205 114</gml:pos>
                                                    <gml:pos>457854 5439086.205 114</gml:pos>
                                                    <gml:pos>457854 5439084.205 114</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58902_1537_85954_306182">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58902_1537_85954_306182_0">
                                                    <gml:pos>457854 5439086.205 114</gml:pos>
                                                    <gml:pos>457853.88 5439086.205 114</gml:pos>
                                                    <gml:pos>457853.88 5439086.205 112.8</gml:pos>
                                                    <gml:pos>457854 5439086.205 112.8</gml:pos>
                                                    <gml:pos>457854 5439086.205 114</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58903_1839_642244_370862">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58903_1839_642244_370862_0">
                                                    <gml:pos>457854 5439088 118.317691453624</gml:pos>
                                                    <gml:pos>457854 5439083 115.430940107676</gml:pos>
                                                    <gml:pos>457854 5439083 111.8</gml:pos>
                                                    <gml:pos>457854 5439093 111.8</gml:pos>
                                                    <gml:pos>457854 5439093 115.430940107676</gml:pos>
                                                    <gml:pos>457854 5439088 118.317691453624</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                            <gml:interior>
                                                <gml:LinearRing gml:id="PolyID58903_1839_642244_370862_1">
                                                    <gml:pos>457854 5439087.51703709 116.129409522551</gml:pos>
                                                    <gml:pos>457854 5439087.53015369 116.171010071663</gml:pos>
                                                    <gml:pos>457854 5439087.54684611 116.21130913087</gml:pos>
                                                    <gml:pos>457854 5439087.5669873 116.25</gml:pos>
                                                    <gml:pos>457854 5439087.59042398 116.286788218176</gml:pos>
                                                    <gml:pos>457854 5439087.61697778 116.321393804843</gml:pos>
                                                    <gml:pos>457854 5439087.64644661 116.353553390593</gml:pos>
                                                    <gml:pos>457854 5439087.6786062 116.383022221559</gml:pos>
                                                    <gml:pos>457854 5439087.71321178 116.409576022145</gml:pos>
                                                    <gml:pos>457854 5439087.75 116.433012701892</gml:pos>
                                                    <gml:pos>457854 5439087.78869087 116.453153893518</gml:pos>
                                                    <gml:pos>457854 5439087.82898993 116.469846310393</gml:pos>
                                                    <gml:pos>457854 5439087.87059048 116.482962913145</gml:pos>
                                                    <gml:pos>457854 5439087.91317591 116.492403876506</gml:pos>
                                                    <gml:pos>457854 5439087.95642213 116.498097349046</gml:pos>
                                                    <gml:pos>457854 5439088 116.5</gml:pos>
                                                    <gml:pos>457854 5439088.04357787 116.498097349046</gml:pos>
                                                    <gml:pos>457854 5439088.08682409 116.492403876506</gml:pos>
                                                    <gml:pos>457854 5439088.12940952 116.482962913145</gml:pos>
                                                    <gml:pos>457854 5439088.17101007 116.469846310393</gml:pos>
                                                    <gml:pos>457854 5439088.21130913 116.453153893518</gml:pos>
                                                    <gml:pos>457854 5439088.25 116.433012701892</gml:pos>
                                                    <gml:pos>457854 5439088.28678822 116.409576022145</gml:pos>
                                                    <gml:pos>457854 5439088.3213938 116.383022221559</gml:pos>
                                                    <gml:pos>457854 5439088.35355339 116.353553390593</gml:pos>
                                                    <gml:pos>457854 5439088.38302222 116.321393804843</gml:pos>
                                                    <gml:pos>457854 5439088.40957602 116.286788218176</gml:pos>
                                                    <gml:pos>457854 5439088.4330127 116.25</gml:pos>
                                                    <gml:pos>457854 5439088.45315389 116.21130913087</gml:pos>
                                                    <gml:pos>457854 5439088.46984631 116.171010071663</gml:pos>
                                                    <gml:pos>457854 5439088.48296291 116.129409522551</gml:pos>
                                                    <gml:pos>457854 5439088.49240388 116.086824088833</gml:pos>
                                                    <gml:pos>457854 5439088.49809735 116.043577871374</gml:pos>
                                                    <gml:pos>457854 5439088.5 116</gml:pos>
                                                    <gml:pos>457854 5439088.49809735 115.956422128626</gml:pos>
                                                    <gml:pos>457854 5439088.49240388 115.913175911167</gml:pos>
                                                    <gml:pos>457854 5439088.48296291 115.870590477449</gml:pos>
                                                    <gml:pos>457854 5439088.46984631 115.828989928337</gml:pos>
                                                    <gml:pos>457854 5439088.45315389 115.78869086913</gml:pos>
                                                    <gml:pos>457854 5439088.4330127 115.75</gml:pos>
                                                    <gml:pos>457854 5439088.40957602 115.713211781824</gml:pos>
                                                    <gml:pos>457854 5439088.38302222 115.678606195157</gml:pos>
                                                    <gml:pos>457854 5439088.35355339 115.646446609407</gml:pos>
                                                    <gml:pos>457854 5439088.3213938 115.616977778441</gml:pos>
                                                    <gml:pos>457854 5439088.28678822 115.590423977855</gml:pos>
                                                    <gml:pos>457854 5439088.25 115.566987298108</gml:pos>
                                                    <gml:pos>457854 5439088.21130913 115.546846106482</gml:pos>
                                                    <gml:pos>457854 5439088.17101007 115.530153689607</gml:pos>
                                                    <gml:pos>457854 5439088.12940952 115.517037086855</gml:pos>
                                                    <gml:pos>457854 5439088.08682409 115.507596123494</gml:pos>
                                                    <gml:pos>457854 5439088.04357787 115.501902650954</gml:pos>
                                                    <gml:pos>457854 5439088 115.5</gml:pos>
                                                    <gml:pos>457854 5439087.95642213 115.501902650954</gml:pos>
                                                    <gml:pos>457854 5439087.91317591 115.507596123494</gml:pos>
                                                    <gml:pos>457854 5439087.87059048 115.517037086855</gml:pos>
                                                    <gml:pos>457854 5439087.82898993 115.530153689607</gml:pos>
                                                    <gml:pos>457854 5439087.78869087 115.546846106482</gml:pos>
                                                    <gml:pos>457854 5439087.75 115.566987298108</gml:pos>
                                                    <gml:pos>457854 5439087.71321178 115.590423977855</gml:pos>
                                                    <gml:pos>457854 5439087.6786062 115.616977778441</gml:pos>
                                                    <gml:pos>457854 5439087.64644661 115.646446609407</gml:pos>
                                                    <gml:pos>457854 5439087.61697778 115.678606195157</gml:pos>
                                                    <gml:pos>457854 5439087.59042398 115.713211781824</gml:pos>
                                                    <gml:pos>457854 5439087.5669873 115.75</gml:pos>
                                                    <gml:pos>457854 5439087.54684611 115.78869086913</gml:pos>
                                                    <gml:pos>457854 5439087.53015369 115.828989928337</gml:pos>
                                                    <gml:pos>457854 5439087.51703709 115.870590477449</gml:pos>
                                                    <gml:pos>457854 5439087.50759612 115.913175911167</gml:pos>
                                                    <gml:pos>457854 5439087.50190265 115.956422128626</gml:pos>
                                                    <gml:pos>457854 5439087.5 116</gml:pos>
                                                    <gml:pos>457854 5439087.50190265 116.043577871374</gml:pos>
                                                    <gml:pos>457854 5439087.50759612 116.086824088833</gml:pos>
                                                    <gml:pos>457854 5439087.51703709 116.129409522551</gml:pos>
                                                </gml:LinearRing>
                                            </gml:interior>
                                            <gml:interior>
                                                <gml:LinearRing gml:id="PolyID58903_1839_642244_370862_2">
                                                    <gml:pos>457854 5439091.50239372 114</gml:pos>
                                                    <gml:pos>457854 5439091.50239372 112.8</gml:pos>
                                                    <gml:pos>457854 5439089.50239372 112.8</gml:pos>
                                                    <gml:pos>457854 5439089.50239372 114</gml:pos>
                                                    <gml:pos>457854 5439091.50239372 114</gml:pos>
                                                </gml:LinearRing>
                                            </gml:interior>
                                            <gml:interior>
                                                <gml:LinearRing gml:id="PolyID58903_1839_642244_370862_3">
                                                    <gml:pos>457854 5439084.205 112.8</gml:pos>
                                                    <gml:pos>457854 5439084.205 114</gml:pos>
                                                    <gml:pos>457854 5439086.205 114</gml:pos>
                                                    <gml:pos>457854 5439086.205 112.8</gml:pos>
                                                    <gml:pos>457854 5439084.205 112.8</gml:pos>
                                                </gml:LinearRing>
                                            </gml:interior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                </gml:CompositeSurface>
                            </gml:surfaceMember>
                        </gml:MultiSurface>
                    </bldg:lod3MultiSurface>
                    <bldg:opening>
                        <bldg:Window gml:id="GML_ef2a1635-4f3c-48b5-afda-53c920f3132b">
                            <gml:name>Window South</gml:name>
                            <bldg:lod3MultiSurface>
                                <gml:MultiSurface>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58904_926_485070_129763">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58904_926_485070_129763_0">
                                                    <gml:pos>457853.88 5439086.205 112.8</gml:pos>
                                                    <gml:pos>457853.88 5439086.205 114</gml:pos>
                                                    <gml:pos>457853.88 5439084.205 114</gml:pos>
                                                    <gml:pos>457853.88 5439084.205 112.8</gml:pos>
                                                    <gml:pos>457853.88 5439086.205 112.8</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                </gml:MultiSurface>
                            </bldg:lod3MultiSurface>
                        </bldg:Window>
                    </bldg:opening>
                    <bldg:opening>
                        <bldg:Window gml:id="GML_a216460a-3326-41f2-b867-6846d81724a4">
                            <gml:name>Window North</gml:name>
                            <bldg:lod3MultiSurface>
                                <gml:MultiSurface>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58905_883_830507_79018">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58905_883_830507_79018_0">
                                                    <gml:pos>457853.88 5439091.50239372 114</gml:pos>
                                                    <gml:pos>457853.88 5439089.50239372 114</gml:pos>
                                                    <gml:pos>457853.88 5439089.50239372 112.8</gml:pos>
                                                    <gml:pos>457853.88 5439091.50239372 112.8</gml:pos>
                                                    <gml:pos>457853.88 5439091.50239372 114</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                </gml:MultiSurface>
                            </bldg:lod3MultiSurface>
                        </bldg:Window>
                    </bldg:opening>
                    <bldg:opening>
                        <bldg:Window gml:id="GML_23030a94-ccbc-4ce5-a0a4-9280c5b3f287">
                            <gml:name>Window Round</gml:name>
                            <bldg:lod3MultiSurface>
                                <gml:MultiSurface>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58906_886_364949_26381">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58906_886_364949_26381_0">
                                                    <gml:pos>457853.87 5439087.61697778 116.321393804843</gml:pos>
                                                    <gml:pos>457853.87 5439087.59042398 116.286788218176</gml:pos>
                                                    <gml:pos>457853.87 5439087.5669873 116.25</gml:pos>
                                                    <gml:pos>457853.87 5439087.54684611 116.21130913087</gml:pos>
                                                    <gml:pos>457853.87 5439087.53015369 116.171010071663</gml:pos>
                                                    <gml:pos>457853.87 5439087.51703709 116.129409522551</gml:pos>
                                                    <gml:pos>457853.87 5439087.50759612 116.086824088833</gml:pos>
                                                    <gml:pos>457853.87 5439087.50190265 116.043577871374</gml:pos>
                                                    <gml:pos>457853.87 5439087.5 116</gml:pos>
                                                    <gml:pos>457853.87 5439087.50190265 115.956422128626</gml:pos>
                                                    <gml:pos>457853.87 5439087.50759612 115.913175911167</gml:pos>
                                                    <gml:pos>457853.87 5439087.51703709 115.870590477449</gml:pos>
                                                    <gml:pos>457853.87 5439087.53015369 115.828989928337</gml:pos>
                                                    <gml:pos>457853.87 5439087.54684611 115.78869086913</gml:pos>
                                                    <gml:pos>457853.87 5439087.5669873 115.75</gml:pos>
                                                    <gml:pos>457853.87 5439087.59042398 115.713211781824</gml:pos>
                                                    <gml:pos>457853.87 5439087.61697778 115.678606195157</gml:pos>
                                                    <gml:pos>457853.87 5439087.64644661 115.646446609407</gml:pos>
                                                    <gml:pos>457853.87 5439087.6786062 115.616977778441</gml:pos>
                                                    <gml:pos>457853.87 5439087.71321178 115.590423977855</gml:pos>
                                                    <gml:pos>457853.87 5439087.75 115.566987298108</gml:pos>
                                                    <gml:pos>457853.87 5439087.78869087 115.546846106482</gml:pos>
                                                    <gml:pos>457853.87 5439087.82898993 115.530153689607</gml:pos>
                                                    <gml:pos>457853.87 5439087.87059048 115.517037086855</gml:pos>
                                                    <gml:pos>457853.87 5439087.91317591 115.507596123494</gml:pos>
                                                    <gml:pos>457853.87 5439087.95642213 115.501902650954</gml:pos>
                                                    <gml:pos>457853.87 5439088 115.5</gml:pos>
                                                    <gml:pos>457853.87 5439088.04357787 115.501902650954</gml:pos>
                                                    <gml:pos>457853.87 5439088.08682409 115.507596123494</gml:pos>
                                                    <gml:pos>457853.87 5439088.12940952 115.517037086855</gml:pos>
                                                    <gml:pos>457853.87 5439088.17101007 115.530153689607</gml:pos>
                                                    <gml:pos>457853.87 5439088.21130913 115.546846106482</gml:pos>
                                                    <gml:pos>457853.87 5439088.25 115.566987298108</gml:pos>
                                                    <gml:pos>457853.87 5439088.28678822 115.590423977855</gml:pos>
                                                    <gml:pos>457853.87 5439088.3213938 115.616977778441</gml:pos>
                                                    <gml:pos>457853.87 5439088.35355339 115.646446609407</gml:pos>
                                                    <gml:pos>457853.87 5439088.38302222 115.678606195157</gml:pos>
                                                    <gml:pos>457853.87 5439088.40957602 115.713211781824</gml:pos>
                                                    <gml:pos>457853.87 5439088.4330127 115.75</gml:pos>
                                                    <gml:pos>457853.87 5439088.45315389 115.78869086913</gml:pos>
                                                    <gml:pos>457853.87 5439088.46984631 115.828989928337</gml:pos>
                                                    <gml:pos>457853.87 5439088.48296291 115.870590477449</gml:pos>
                                                    <gml:pos>457853.87 5439088.49240388 115.913175911167</gml:pos>
                                                    <gml:pos>457853.87 5439088.49809735 115.956422128626</gml:pos>
                                                    <gml:pos>457853.87 5439088.5 116</gml:pos>
                                                    <gml:pos>457853.87 5439088.49809735 116.043577871374</gml:pos>
                                                    <gml:pos>457853.87 5439088.49240388 116.086824088833</gml:pos>
                                                    <gml:pos>457853.87 5439088.48296291 116.129409522551</gml:pos>
                                                    <gml:pos>457853.87 5439088.46984631 116.171010071663</gml:pos>
                                                    <gml:pos>457853.87 5439088.45315389 116.21130913087</gml:pos>
                                                    <gml:pos>457853.87 5439088.4330127 116.25</gml:pos>
                                                    <gml:pos>457853.87 5439088.40957602 116.286788218176</gml:pos>
                                                    <gml:pos>457853.87 5439088.38302222 116.321393804843</gml:pos>
                                                    <gml:pos>457853.87 5439088.35355339 116.353553390593</gml:pos>
                                                    <gml:pos>457853.87 5439088.3213938 116.383022221559</gml:pos>
                                                    <gml:pos>457853.87 5439088.28678822 116.409576022145</gml:pos>
                                                    <gml:pos>457853.87 5439088.25 116.433012701892</gml:pos>
                                                    <gml:pos>457853.87 5439088.21130913 116.453153893518</gml:pos>
                                                    <gml:pos>457853.87 5439088.17101007 116.469846310393</gml:pos>
                                                    <gml:pos>457853.87 5439088.12940952 116.482962913145</gml:pos>
                                                    <gml:pos>457853.87 5439088.08682409 116.492403876506</gml:pos>
                                                    <gml:pos>457853.87 5439088.04357787 116.498097349046</gml:pos>
                                                    <gml:pos>457853.87 5439088 116.5</gml:pos>
                                                    <gml:pos>457853.87 5439087.95642213 116.498097349046</gml:pos>
                                                    <gml:pos>457853.87 5439087.91317591 116.492403876506</gml:pos>
                                                    <gml:pos>457853.87 5439087.87059048 116.482962913145</gml:pos>
                                                    <gml:pos>457853.87 5439087.82898993 116.469846310393</gml:pos>
                                                    <gml:pos>457853.87 5439087.78869087 116.453153893518</gml:pos>
                                                    <gml:pos>457853.87 5439087.75 116.433012701892</gml:pos>
                                                    <gml:pos>457853.87 5439087.71321178 116.409576022145</gml:pos>
                                                    <gml:pos>457853.87 5439087.6786062 116.383022221559</gml:pos>
                                                    <gml:pos>457853.87 5439087.64644661 116.353553390593</gml:pos>
                                                    <gml:pos>457853.87 5439087.61697778 116.321393804843</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>

                                    </gml:surfaceMember>
                                </gml:MultiSurface>
                            </bldg:lod3MultiSurface>
                        </bldg:Window>
                    </bldg:opening>
                </bldg:WallSurface>
            </bldg:boundedBy>
            <bldg:boundedBy>
                <bldg:RoofSurface gml:id="GML_875d470b-32b4-4985-a4c8-0f02caa342a2">
                    <gml:name>Roof 1 (North)</gml:name>
                    <bldg:lod3MultiSurface>
                        <gml:MultiSurface>
                            <gml:surfaceMember>
                                <gml:Polygon gml:id="PolyID58907_1126_884498_121000">
                                    <gml:exterior>
                                        <gml:LinearRing gml:id="PolyID58907_1126_884498_121000_0">
                                            <gml:pos>457842 5439088 118.317691453624</gml:pos>
                                            <gml:pos>457854 5439088 118.317691453624</gml:pos>
                                            <gml:pos>457854 5439093 115.430940107676</gml:pos>
                                            <gml:pos>457842 5439093 115.430940107676</gml:pos>
                                            <gml:pos>457842 5439088 118.317691453624</gml:pos>
                                        </gml:LinearRing>
                                    </gml:exterior>
                                </gml:Polygon>
                            </gml:surfaceMember>
                            <gml:surfaceMember>
                                <gml:Polygon gml:id="PolyID58908_1911_59781_62378">
                                    <gml:exterior>
                                        <gml:LinearRing gml:id="PolyID58908_1911_59781_62378_0">
                                            <gml:pos>457841.5 5439088 118.317691453624</gml:pos>
                                            <gml:pos>457842 5439088 118.317691453624</gml:pos>
                                            <gml:pos>457842 5439093 115.430940107676</gml:pos>
                                            <gml:pos>457854 5439093 115.430940107676</gml:pos>
                                            <gml:pos>457854 5439088 118.317691453624</gml:pos>
                                            <gml:pos>457854.5 5439088 118.317691453624</gml:pos>
                                            <gml:pos>457854.5 5439093.5 115.142264973081</gml:pos>
                                            <gml:pos>457841.5 5439093.5 115.142264973081</gml:pos>
                                            <gml:pos>457841.5 5439088 118.317691453624</gml:pos>
                                        </gml:LinearRing>
                                    </gml:exterior>
                                </gml:Polygon>
                            </gml:surfaceMember>
                        </gml:MultiSurface>
                    </bldg:lod3MultiSurface>
                </bldg:RoofSurface>
            </bldg:boundedBy>
            <bldg:boundedBy>
                <bldg:WallSurface gml:id="GML_0f30f604-e70d-4dfe-ba35-853bc69609cc">
                    <gml:name>Outer Wall 4 (North)</gml:name>
                    <bldg:lod3MultiSurface>
                        <gml:MultiSurface>
                            <gml:surfaceMember>
                                <gml:CompositeSurface gml:id="GML_b01f8d66-c797-49d3-b089-31349e167e4d">
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58909_352_689036_60980">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58909_352_689036_60980_0">
                                                    <gml:pos>457852.795 5439093 112.8</gml:pos>
                                                    <gml:pos>457852.795 5439092.88 112.8</gml:pos>
                                                    <gml:pos>457852.795 5439092.88 114</gml:pos>
                                                    <gml:pos>457852.795 5439093 114</gml:pos>
                                                    <gml:pos>457852.795 5439093 112.8</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58910_338_408556_67913">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58910_338_408556_67913_0">
                                                    <gml:pos>457842 5439093 115.430940107676</gml:pos>
                                                    <gml:pos>457854 5439093 115.430940107676</gml:pos>
                                                    <gml:pos>457854 5439093 111.8</gml:pos>
                                                    <gml:pos>457842 5439093 111.8</gml:pos>
                                                    <gml:pos>457842 5439093 115.430940107676</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                            <gml:interior>
                                                <gml:LinearRing gml:id="PolyID58910_338_408556_67913_1">
                                                    <gml:pos>457843.05 5439093 114</gml:pos>
                                                    <gml:pos>457843.05 5439093 112.8</gml:pos>
                                                    <gml:pos>457845.05 5439093 112.8</gml:pos>
                                                    <gml:pos>457845.05 5439093 114</gml:pos>
                                                    <gml:pos>457843.05 5439093 114</gml:pos>
                                                </gml:LinearRing>
                                            </gml:interior>
                                            <gml:interior>
                                                <gml:LinearRing gml:id="PolyID58910_338_408556_67913_2">
                                                    <gml:pos>457852.795 5439093 114</gml:pos>
                                                    <gml:pos>457850.795 5439093 114</gml:pos>
                                                    <gml:pos>457850.795 5439093 112.8</gml:pos>
                                                    <gml:pos>457852.795 5439093 112.8</gml:pos>
                                                    <gml:pos>457852.795 5439093 114</gml:pos>
                                                </gml:LinearRing>
                                            </gml:interior>
                                            <gml:interior>
                                                <gml:LinearRing gml:id="PolyID58910_338_408556_67913_3">
                                                    <gml:pos>457846.66 5439093 114</gml:pos>
                                                    <gml:pos>457846.66 5439093 112.8</gml:pos>
                                                    <gml:pos>457848.66 5439093 112.8</gml:pos>
                                                    <gml:pos>457848.66 5439093 114</gml:pos>
                                                    <gml:pos>457846.66 5439093 114</gml:pos>
                                                </gml:LinearRing>
                                            </gml:interior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58911_17_268945_50084">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58911_17_268945_50084_0">
                                                    <gml:pos>457848.66 5439093 112.8</gml:pos>
                                                    <gml:pos>457848.66 5439092.88 112.8</gml:pos>
                                                    <gml:pos>457848.66 5439092.88 114</gml:pos>
                                                    <gml:pos>457848.66 5439093 114</gml:pos>
                                                    <gml:pos>457848.66 5439093 112.8</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58912_1940_702815_377183">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58912_1940_702815_377183_0">
                                                    <gml:pos>457850.795 5439093 112.8</gml:pos>
                                                    <gml:pos>457850.795 5439092.88 112.8</gml:pos>
                                                    <gml:pos>457852.795 5439092.88 112.8</gml:pos>
                                                    <gml:pos>457852.795 5439093 112.8</gml:pos>
                                                    <gml:pos>457850.795 5439093 112.8</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58913_657_341355_423224">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58913_657_341355_423224_0">
                                                    <gml:pos>457850.795 5439093 114</gml:pos>
                                                    <gml:pos>457850.795 5439092.88 114</gml:pos>
                                                    <gml:pos>457850.795 5439092.88 112.8</gml:pos>
                                                    <gml:pos>457850.795 5439093 112.8</gml:pos>
                                                    <gml:pos>457850.795 5439093 114</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58914_1307_607941_114014">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58914_1307_607941_114014_0">
                                                    <gml:pos>457848.66 5439093 114</gml:pos>
                                                    <gml:pos>457848.66 5439092.88 114</gml:pos>
                                                    <gml:pos>457846.66 5439092.88 114</gml:pos>
                                                    <gml:pos>457846.66 5439093 114</gml:pos>
                                                    <gml:pos>457848.66 5439093 114</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58915_693_321944_318712">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58915_693_321944_318712_0">
                                                    <gml:pos>457846.66 5439093 112.8</gml:pos>
                                                    <gml:pos>457846.66 5439092.88 112.8</gml:pos>
                                                    <gml:pos>457848.66 5439092.88 112.8</gml:pos>
                                                    <gml:pos>457848.66 5439093 112.8</gml:pos>
                                                    <gml:pos>457846.66 5439093 112.8</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58916_1618_190498_353340">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58916_1618_190498_353340_0">
                                                    <gml:pos>457846.66 5439093 114</gml:pos>
                                                    <gml:pos>457846.66 5439092.88 114</gml:pos>
                                                    <gml:pos>457846.66 5439092.88 112.8</gml:pos>
                                                    <gml:pos>457846.66 5439093 112.8</gml:pos>
                                                    <gml:pos>457846.66 5439093 114</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58917_257_573785_278141">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58917_257_573785_278141_0">
                                                    <gml:pos>457852.795 5439093 114</gml:pos>
                                                    <gml:pos>457852.795 5439092.88 114</gml:pos>
                                                    <gml:pos>457850.795 5439092.88 114</gml:pos>
                                                    <gml:pos>457850.795 5439093 114</gml:pos>
                                                    <gml:pos>457852.795 5439093 114</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID45488_1878_623744_29993">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID45488_1878_623744_29993_0">
                                                    <gml:pos>457845.05 5439092.88 114</gml:pos>
                                                    <gml:pos>457843.05 5439092.88 114</gml:pos>
                                                    <gml:pos>457843.05 5439093 114</gml:pos>
                                                    <gml:pos>457845.05 5439093 114</gml:pos>
                                                    <gml:pos>457845.05 5439092.88 114</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID45489_1241_160210_79540">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID45489_1241_160210_79540_0">
                                                    <gml:pos>457845.05 5439092.88 112.8</gml:pos>
                                                    <gml:pos>457845.05 5439092.88 114</gml:pos>
                                                    <gml:pos>457845.05 5439093 114</gml:pos>
                                                    <gml:pos>457845.05 5439093 112.8</gml:pos>
                                                    <gml:pos>457845.05 5439092.88 112.8</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID45490_980_523938_205257">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID45490_980_523938_205257_0">
                                                    <gml:pos>457843.05 5439092.88 112.8</gml:pos>
                                                    <gml:pos>457845.05 5439092.88 112.8</gml:pos>
                                                    <gml:pos>457845.05 5439093 112.8</gml:pos>
                                                    <gml:pos>457843.05 5439093 112.8</gml:pos>
                                                    <gml:pos>457843.05 5439092.88 112.8</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID45491_552_178616_406087">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID45491_552_178616_406087_0">
                                                    <gml:pos>457843.05 5439092.88 114</gml:pos>
                                                    <gml:pos>457843.05 5439092.88 112.8</gml:pos>
                                                    <gml:pos>457843.05 5439093 112.8</gml:pos>
                                                    <gml:pos>457843.05 5439093 114</gml:pos>
                                                    <gml:pos>457843.05 5439092.88 114</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                </gml:CompositeSurface>

                            </gml:surfaceMember>
                        </gml:MultiSurface>
                    </bldg:lod3MultiSurface>
                    <bldg:opening>
                        <bldg:Window gml:id="GML_2297f8d4-f302-464c-8e7d-a26fd5dbd755">
                            <gml:name>Window East</gml:name>
                            <bldg:lod3MultiSurface>
                                <gml:MultiSurface>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58918_1666_508104_106792">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58918_1666_508104_106792_0">
                                                    <gml:pos>457852.795 5439092.88 112.8</gml:pos>
                                                    <gml:pos>457850.795 5439092.88 112.8</gml:pos>
                                                    <gml:pos>457850.795 5439092.88 114</gml:pos>
                                                    <gml:pos>457852.795 5439092.88 114</gml:pos>
                                                    <gml:pos>457852.795 5439092.88 112.8</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                </gml:MultiSurface>
                            </bldg:lod3MultiSurface>
                        </bldg:Window>
                    </bldg:opening>
                    <bldg:opening>
                        <bldg:Window gml:id="GML_6087187b-284d-4064-9abf-85f9ba9b2c89">
                            <gml:name>Window Middle</gml:name>
                            <bldg:lod3MultiSurface>
                                <gml:MultiSurface>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID58919_293_365452_56524">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID58919_293_365452_56524_0">
                                                    <gml:pos>457848.66 5439092.88 114</gml:pos>
                                                    <gml:pos>457848.66 5439092.88 112.8</gml:pos>
                                                    <gml:pos>457846.66 5439092.88 112.8</gml:pos>
                                                    <gml:pos>457846.66 5439092.88 114</gml:pos>
                                                    <gml:pos>457848.66 5439092.88 114</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                </gml:MultiSurface>
                            </bldg:lod3MultiSurface>
                        </bldg:Window>
                    </bldg:opening>
                    <bldg:opening>
                        <bldg:Window gml:id="GML_5397681c-8367-4e9b-a989-60caec316f86">
                            <gml:name>Window West</gml:name>
                            <bldg:lod3MultiSurface>
                                <gml:MultiSurface>
                                    <gml:surfaceMember>
                                        <gml:Polygon gml:id="PolyID45494_1549_894355_77993">
                                            <gml:exterior>
                                                <gml:LinearRing gml:id="PolyID45494_1549_894355_77993_0">
                                                    <gml:pos>457843.05 5439092.88 112.8</gml:pos>
                                                    <gml:pos>457843.05 5439092.88 114</gml:pos>
                                                    <gml:pos>457845.05 5439092.88 114</gml:pos>
                                                    <gml:pos>457845.05 5439092.88 112.8</gml:pos>
                                                    <gml:pos>457843.05 5439092.88 112.8</gml:pos>
                                                </gml:LinearRing>
                                            </gml:exterior>
                                        </gml:Polygon>
                                    </gml:surfaceMember>
                                </gml:MultiSurface>
                            </bldg:lod3MultiSurface>
                        </bldg:Window>
                    </bldg:opening>
                </bldg:WallSurface>
            </bldg:boundedBy>
            <bldg:boundedBy>
                <bldg:RoofSurface gml:id="GML_eeb6796a-e261-4d3b-a6f2-475940cca80a">
                    <gml:name>Roof 2 (South)</gml:name>
                    <bldg:lod3MultiSurface>
                        <gml:MultiSurface>
                            <gml:surfaceMember>
                                <gml:Polygon gml:id="PolyID58920_1537_643295_290950">
                                    <gml:exterior>
                                        <gml:LinearRing gml:id="PolyID58920_1537_643295_290950_0">
                                            <gml:pos>457854 5439083 115.430940107676</gml:pos>
                                            <gml:pos>457842 5439083 115.430940107676</gml:pos>
                                            <gml:pos>457842 5439088 118.317691453624</gml:pos>
                                            <gml:pos>457841.5 5439088 118.317691453624</gml:pos>
                                            <gml:pos>457841.5 5439082.5 115.142264973081</gml:pos>
                                            <gml:pos>457854.5 5439082.5 115.142264973081</gml:pos>
                                            <gml:pos>457854.5 5439088 118.317691453624</gml:pos>
                                            <gml:pos>457854 5439088 118.317691453624</gml:pos>
                                            <gml:pos>457854 5439083 115.430940107676</gml:pos>
                                        </gml:LinearRing>
                                    </gml:exterior>
                                </gml:Polygon>
                            </gml:surfaceMember>
                            <gml:surfaceMember>
                                <gml:Polygon gml:id="PolyID58921_472_579834_340993">
                                    <gml:exterior>
                                        <gml:LinearRing gml:id="PolyID58921_472_579834_340993_0">
                                            <gml:pos>457854 5439083 115.430940107676</gml:pos>
                                            <gml:pos>457854 5439088 118.317691453624</gml:pos>
                                            <gml:pos>457842 5439088 118.317691453624</gml:pos>
                                            <gml:pos>457842 5439083 115.430940107676</gml:pos>
                                            <gml:pos>457854 5439083 115.430940107676</gml:pos>
                                        </gml:LinearRing>
                                    </gml:exterior>
                                </gml:Polygon>
                            </gml:surfaceMember>
                        </gml:MultiSurface>
                    </bldg:lod3MultiSurface>
                </bldg:RoofSurface>
            </bldg:boundedBy>
            <bldg:boundedBy>
                <bldg:GroundSurface gml:id="GML_257a8dde-8194-4ca3-b581-abd591dcd6a3">
                    <gml:description>Bodenplatte</gml:description>
                    <gml:name>Base Surface</gml:name>
                    <bldg:lod3MultiSurface>
                        <gml:MultiSurface>
                            <gml:surfaceMember>
                                <gml:Polygon gml:id="PolyID58922_1541_340473_350668">
                                    <gml:exterior>
                                        <gml:LinearRing gml:id="PolyID58922_1541_340473_350668_0">
                                            <gml:pos>457854 5439083 111.8</gml:pos>
                                            <gml:pos>457842 5439083 111.8</gml:pos>
                                            <gml:pos>457842 5439093 111.8</gml:pos>
                                            <gml:pos>457854 5439093 111.8</gml:pos>
                                            <gml:pos>457854 5439083 111.8</gml:pos>
                                        </gml:LinearRing>
                                    </gml:exterior>
                                </gml:Polygon>
                            </gml:surfaceMember>
                        </gml:MultiSurface>
                    </bldg:lod3MultiSurface>
                </bldg:GroundSurface>
            </bldg:boundedBy>
            <bldg:lod3Solid>
                <gml:Solid>
                    <gml:exterior>
                        <gml:CompositeSurface>
                            <!-- Roof 1 (North) -->
                            <gml:surfaceMember xlink:href="#PolyID58907_1126_884498_121000"/>
                            <!-- Roof 1 (North) -->
                            <!-- Roof 2 (South) -->
                            <gml:surfaceMember xlink:href="#PolyID58921_472_579834_340993"/>
                            <!-- Roof 2 (South) -->
                            <!-- Base Surface -->
                            <gml:surfaceMember xlink:href="#PolyID58922_1541_340473_350668"/>
                            <!-- Base Surface -->
                            <!-- Outer Wall 4 (North) -->
                            <gml:surfaceMember xlink:href="#GML_b01f8d66-c797-49d3-b089-31349e167e4d"/>
                            <!-- Window East -->
                            <gml:surfaceMember xlink:href="#PolyID58918_1666_508104_106792"/>
                            <!-- Window East -->
                            <!-- Window Middle -->
                            <gml:surfaceMember xlink:href="#PolyID58919_293_365452_56524"/>
                            <!-- Window Middle -->
                            <!-- Window West -->
                            <gml:surfaceMember xlink:href="#PolyID45494_1549_894355_77993"/>
                            <!-- Window West -->
                            <!-- Outer Wall 4 (North) -->
                            <!-- Outer Wall 3 (East) -->
                            <gml:surfaceMember xlink:href="#GML_d5729847-0aaf-4fec-8ed4-84a5300e510f"/>
                            <!-- Window Round -->
                            <gml:surfaceMember xlink:href="#PolyID58906_886_364949_26381"/>
                            <!-- Window Round -->
                            <!-- Window North -->
                            <gml:surfaceMember xlink:href="#PolyID58905_883_830507_79018"/>
                            <!-- Window North -->
                            <!-- Window South -->
                            <gml:surfaceMember xlink:href="#PolyID58904_926_485070_129763"/>
                            <!-- Window South -->
                            <!-- Outer Wall 3 (East) -->
                            <!-- Outer Wall 1 (West) -->
                            <gml:surfaceMember xlink:href="#GML_e9240361-e956-421c-bff5-1f1f6d9b59aa"/>
                            <!-- Window Round -->
                            <gml:surfaceMember xlink:href="#PolyID58803_371_698036_77126"/>
                            <!-- Window Round -->
                            <!-- Window North -->
                            <gml:surfaceMember xlink:href="#PolyID58804_647_880710_163324"/>
                            <!-- Window North -->
                            <!-- Door West -->
                            <gml:surfaceMember xlink:href="#PolyID58805_1881_773628_351228"/>
                            <!-- Door West -->
                            <!-- Window  South -->
                            <gml:surfaceMember xlink:href="#PolyID58806_328_642559_374120"/>
                            <!--Window  South -->
                            <!-- Outer Wall 2 (South) -->
                            <gml:surfaceMember xlink:href="#GML_4726d5c0-dfa2-4777-b1da-24798d72c27a"/>
                            <!-- Window East -->
                            <gml:surfaceMember xlink:href="#PolyID58820_1568_227087_210505"/>
                            <!-- Window East -->
                            <!-- Window West -->
                            <gml:surfaceMember xlink:href="#PolyID58821_1939_612838_272028"/>
                            <!-- Window West -->
                            <!-- Door South -->
                            <gml:surfaceMember xlink:href="#PolyID58822_551_84845_215911"/>
                            <!-- Door South -->
                            <!-- Outer Wall 2 (South) -->
                        </gml:CompositeSurface>
                    </gml:exterior>
                </gml:Solid>
            </bldg:lod3Solid>
            <bldg:address>
                <core:Address>
                    <core:xalAddress>
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
                    </core:xalAddress>
                </core:Address>
            </bldg:address>
        </bldg:Building>
    </core:cityObjectMember>
</core:CityModel>