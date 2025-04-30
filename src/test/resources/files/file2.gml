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
<xs:element name="file2" type="ogr:file2_Type" substitutionGroup="gml:AbstractFeature"/>
<xs:complexType name="file2_Type">
  <xs:complexContent>
    <xs:extension base="gml:AbstractFeatureType">
      <xs:sequence>
        <xs:element name="geometryProperty" type="gml:PointPropertyType" nillable="true" minOccurs="0" maxOccurs="1"/><!-- srsName="urn:ogc:def:crs:EPSG::25833" -->
      </xs:sequence>
    </xs:extension>
  </xs:complexContent>
</xs:complexType>
</xs:schema>
<ogr:FeatureCollection
     gml:id="aFeatureCollection"
     xmlns:ogr="http://ogr.maptools.org/"
     xmlns:gml="http://www.opengis.net/gml/3.2">
  <gml:boundedBy><gml:Envelope srsName="urn:ogc:def:crs:EPSG::25833"><gml:lowerCorner>62516.7223678502 5611809.23729608</gml:lowerCorner><gml:upperCorner>273788.5148911 5842972.58836487</gml:upperCorner></gml:Envelope></gml:boundedBy>
                                                                                                                   
  <ogr:featureMember>
    <ogr:file2 gml:id="file2.0">
      <gml:boundedBy><gml:Envelope srsName="urn:ogc:def:crs:EPSG::25833"><gml:lowerCorner>62516.7223678502 5834928.6157901</gml:lowerCorner><gml:upperCorner>62516.7223678502 5834928.6157901</gml:upperCorner></gml:Envelope></gml:boundedBy>
      <ogr:geometryProperty><gml:Point srsName="urn:ogc:def:crs:EPSG::25833" gml:id="file2.geom.0"><gml:pos>62516.7223678502 5834928.6157901</gml:pos></gml:Point></ogr:geometryProperty>
    </ogr:file2>
  </ogr:featureMember>
  <ogr:featureMember>
    <ogr:file2 gml:id="file2.1">
      <gml:boundedBy><gml:Envelope srsName="urn:ogc:def:crs:EPSG::25833"><gml:lowerCorner>273788.5148911 5842972.58836487</gml:lowerCorner><gml:upperCorner>273788.5148911 5842972.58836487</gml:upperCorner></gml:Envelope></gml:boundedBy>
      <ogr:geometryProperty><gml:Point srsName="urn:ogc:def:crs:EPSG::25833" gml:id="file2.geom.1"><gml:pos>273788.5148911 5842972.58836487</gml:pos></gml:Point></ogr:geometryProperty>
    </ogr:file2>
  </ogr:featureMember>
  <ogr:featureMember>
    <ogr:file2 gml:id="file2.2">
      <gml:boundedBy><gml:Envelope srsName="urn:ogc:def:crs:EPSG::25833"><gml:lowerCorner>227576.927661156 5611809.23729608</gml:lowerCorner><gml:upperCorner>227576.927661156 5611809.23729608</gml:upperCorner></gml:Envelope></gml:boundedBy>
      <ogr:geometryProperty><gml:Point srsName="urn:ogc:def:crs:EPSG::25833" gml:id="file2.geom.2"><gml:pos>227576.927661156 5611809.23729608</gml:pos></gml:Point></ogr:geometryProperty>
    </ogr:file2>
  </ogr:featureMember>
</ogr:FeatureCollection>
