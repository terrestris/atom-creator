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
<xs:element name="file1" type="ogr:file1_Type" substitutionGroup="gml:AbstractFeature"/>
<xs:complexType name="file1_Type">
  <xs:complexContent>
    <xs:extension base="gml:AbstractFeatureType">
      <xs:sequence>
        <xs:element name="geometryProperty" type="gml:PointPropertyType" nillable="true" minOccurs="0" maxOccurs="1"/><!-- srsName="urn:ogc:def:crs:EPSG::4326" -->
      </xs:sequence>
    </xs:extension>
  </xs:complexContent>
</xs:complexType>
</xs:schema>
<ogr:FeatureCollection
     gml:id="aFeatureCollection"
     xmlns:ogr="http://ogr.maptools.org/"
     xmlns:gml="http://www.opengis.net/gml/3.2">
  <gml:boundedBy><gml:Envelope srsName="urn:ogc:def:crs:EPSG::4326"><gml:lowerCorner>50.5944998405747 8.55349536869086</gml:lowerCorner><gml:upperCorner>52.6893523594109 11.6527292321747</gml:upperCorner></gml:Envelope></gml:boundedBy>
                                                                                                                  
  <ogr:featureMember>
    <ogr:file1 gml:id="file1.0">
      <gml:boundedBy><gml:Envelope srsName="urn:ogc:def:crs:EPSG::4326"><gml:lowerCorner>52.4884760904814 8.55349536869086</gml:lowerCorner><gml:upperCorner>52.4884760904814 8.55349536869086</gml:upperCorner></gml:Envelope></gml:boundedBy>
      <ogr:geometryProperty><gml:Point srsName="urn:ogc:def:crs:EPSG::4326" gml:id="file1.geom.0"><gml:pos>52.4884760904814 8.55349536869086</gml:pos></gml:Point></ogr:geometryProperty>
    </ogr:file1>
  </ogr:featureMember>
  <ogr:featureMember>
    <ogr:file1 gml:id="file1.1">
      <gml:boundedBy><gml:Envelope srsName="urn:ogc:def:crs:EPSG::4326"><gml:lowerCorner>52.6893523594109 11.6527292321747</gml:lowerCorner><gml:upperCorner>52.6893523594109 11.6527292321747</gml:upperCorner></gml:Envelope></gml:boundedBy>
      <ogr:geometryProperty><gml:Point srsName="urn:ogc:def:crs:EPSG::4326" gml:id="file1.geom.1"><gml:pos>52.6893523594109 11.6527292321747</gml:pos></gml:Point></ogr:geometryProperty>
    </ogr:file1>
  </ogr:featureMember>
  <ogr:featureMember>
    <ogr:file1 gml:id="file1.2">
      <gml:boundedBy><gml:Envelope srsName="urn:ogc:def:crs:EPSG::4326"><gml:lowerCorner>50.5944998405747 11.1505385598509</gml:lowerCorner><gml:upperCorner>50.5944998405747 11.1505385598509</gml:upperCorner></gml:Envelope></gml:boundedBy>
      <ogr:geometryProperty><gml:Point srsName="urn:ogc:def:crs:EPSG::4326" gml:id="file1.geom.2"><gml:pos>50.5944998405747 11.1505385598509</gml:pos></gml:Point></ogr:geometryProperty>
    </ogr:file1>
  </ogr:featureMember>
</ogr:FeatureCollection>
