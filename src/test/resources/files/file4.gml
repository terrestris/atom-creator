<?xml version="1.0" encoding="utf-8" ?>
<xs:schema 
    targetNamespace="http://ogr.maptools.org/"
    xmlns:ogr="http://ogr.maptools.org/"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:gml="http://www.opengis.net/gml/3.2"
    xmlns:gmlsf="http://www.opengis.net/gmlsf/2.0"
    elementFormDefault="qualified"
    version="1.0">
<xs:annotation>
  <xs:appinfo source="http://schemas.opengis.net/gmlsfProfile/2.0/gmlsfLevels.xsd">
    <gmlsf:ComplianceLevel>0</gmlsf:ComplianceLevel>
  </xs:appinfo>
</xs:annotation>
<xs:import namespace="http://www.opengis.net/gml/3.2" schemaLocation="http://schemas.opengis.net/gml/3.2.1/gml.xsd"/>
<xs:import namespace="http://www.opengis.net/gmlsf/2.0" schemaLocation="http://schemas.opengis.net/gmlsfProfile/2.0/gmlsfLevels.xsd"/>
<xs:element name="FeatureCollection" type="ogr:FeatureCollectionType" substitutionGroup="gml:AbstractFeature"/>
<xs:complexType name="FeatureCollectionType">
  <xs:complexContent>
    <xs:extension base="gml:AbstractFeatureType">
      <xs:sequence minOccurs="0" maxOccurs="unbounded">
        <xs:element name="featureMember">
          <xs:complexType>
            <xs:complexContent>
              <xs:extension base="gml:AbstractFeatureMemberType">
                <xs:sequence>
                  <xs:element ref="gml:AbstractFeature"/>
                </xs:sequence>
              </xs:extension>
            </xs:complexContent>
          </xs:complexType>
        </xs:element>
      </xs:sequence>
    </xs:extension>
  </xs:complexContent>
</xs:complexType>
<xs:element name="file4" type="ogr:file4_Type" substitutionGroup="gml:AbstractFeature"/>
<xs:complexType name="file4_Type">
  <xs:complexContent>
    <xs:extension base="gml:AbstractFeatureType">
      <xs:sequence>
        <xs:element name="geometryProperty" type="gml:SurfacePropertyType" nillable="true" minOccurs="0" maxOccurs="1"/> <!-- restricted to Polygon --><!-- srsName="urn:ogc:def:crs:EPSG::25833" -->
      </xs:sequence>
    </xs:extension>
  </xs:complexContent>
</xs:complexType>
</xs:schema>
<ogr:FeatureCollection
     gml:id="aFeatureCollection"
     xmlns:ogr="http://ogr.maptools.org/"
     xmlns:gml="http://www.opengis.net/gml/3.2">
  <gml:boundedBy><gml:Envelope srsName="urn:ogc:def:crs:EPSG::25833"><gml:lowerCorner>51398.3433975542 5452288.28457076</gml:lowerCorner><gml:upperCorner>279097.720532104 5813104.22064551</gml:upperCorner></gml:Envelope></gml:boundedBy>
                                                                                                                 
  <ogr:featureMember>
    <ogr:file4 gml:id="file4.0">
      <gml:boundedBy><gml:Envelope srsName="urn:ogc:def:crs:EPSG::25833"><gml:lowerCorner>126714.291218982 5636199.31994867</gml:lowerCorner><gml:upperCorner>279097.720532104 5813104.22064551</gml:upperCorner></gml:Envelope></gml:boundedBy>
      <ogr:geometryProperty><gml:Polygon srsName="urn:ogc:def:crs:EPSG::25833" gml:id="file4.geom.0"><gml:exterior><gml:LinearRing><gml:posList>126714.291218982 5690496.86372691 205533.306380942 5636199.31994867 279097.720532104 5778073.54724019 279097.720532104 5778073.54724019 149484.228932437 5813104.22064551 126714.291218982 5690496.86372691</gml:posList></gml:LinearRing></gml:exterior></gml:Polygon></ogr:geometryProperty>
    </ogr:file4>
  </ogr:featureMember>
  <ogr:featureMember>
    <ogr:file4 gml:id="file4.1">
      <gml:boundedBy><gml:Envelope srsName="urn:ogc:def:crs:EPSG::25833"><gml:lowerCorner>51398.3433975542 5452288.28457076</gml:lowerCorner><gml:upperCorner>172254.166645892 5597665.57920282</gml:upperCorner></gml:Envelope></gml:boundedBy>
      <ogr:geometryProperty><gml:Polygon srsName="urn:ogc:def:crs:EPSG::25833" gml:id="file4.geom.1"><gml:exterior><gml:LinearRing><gml:posList>51398.3433975542 5539864.96808405 135471.959570311 5452288.28457076 172254.166645892 5597665.57920282 172254.166645892 5597665.57920282 51398.3433975542 5539864.96808405</gml:posList></gml:LinearRing></gml:exterior></gml:Polygon></ogr:geometryProperty>
    </ogr:file4>
  </ogr:featureMember>
</ogr:FeatureCollection>
