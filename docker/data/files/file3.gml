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
<xs:element name="file3" type="ogr:file3_Type" substitutionGroup="gml:AbstractFeature"/>
<xs:complexType name="file3_Type">
  <xs:complexContent>
    <xs:extension base="gml:AbstractFeatureType">
      <xs:sequence>
        <xs:element name="geometryProperty" type="gml:SurfacePropertyType" nillable="true" minOccurs="0" maxOccurs="1"/> <!-- restricted to Polygon --><!-- srsName="urn:ogc:def:crs:EPSG::4326" -->
      </xs:sequence>
    </xs:extension>
  </xs:complexContent>
</xs:complexType>
</xs:schema>
<ogr:FeatureCollection
     gml:id="aFeatureCollection"
     xmlns:ogr="http://ogr.maptools.org/"
     xmlns:gml="http://www.opengis.net/gml/3.2">
  <gml:boundedBy><gml:Envelope srsName="urn:ogc:def:crs:EPSG::4326"><gml:lowerCorner>49.115163615945 8.75870728325424</gml:lowerCorner><gml:upperCorner>52.3558647682801 11.7739618310585</gml:upperCorner></gml:Envelope></gml:boundedBy>
                                                                                                                   
  <ogr:featureMember>
    <ogr:file3 gml:id="file3.0">
      <gml:boundedBy><gml:Envelope srsName="urn:ogc:def:crs:EPSG::4326"><gml:lowerCorner>50.8025610407229 9.65052672916049</gml:lowerCorner><gml:upperCorner>52.3558647682801 11.7739618310585</gml:upperCorner></gml:Envelope></gml:boundedBy>
      <ogr:geometryProperty><gml:Polygon srsName="urn:ogc:def:crs:EPSG::4326" gml:id="file3.geom.0"><gml:exterior><gml:LinearRing><gml:posList>51.2433787577427 9.65052672916049 50.8025610407229 10.820465588395 52.1090159290672 11.7739618310585 52.1090159290672 11.7739618310585 52.3558647682801 9.85153008589272 51.2433787577427 9.65052672916049</gml:posList></gml:LinearRing></gml:exterior></gml:Polygon></ogr:geometryProperty>
    </ogr:file3>
  </ogr:featureMember>
  <ogr:featureMember>
    <ogr:file3 gml:id="file3.1">
      <gml:boundedBy><gml:Envelope srsName="urn:ogc:def:crs:EPSG::4326"><gml:lowerCorner>49.115163615945 8.75870728325424</gml:lowerCorner><gml:upperCorner>50.4393053964866 10.3836936081818</gml:upperCorner></gml:Envelope></gml:boundedBy>
      <ogr:geometryProperty><gml:Polygon srsName="urn:ogc:def:crs:EPSG::4326" gml:id="file3.geom.1"><gml:exterior><gml:LinearRing><gml:posList>49.8428390108124 8.75870728325424 49.115163615945 10.0037621622573 50.4393053964866 10.3836936081818 50.4393053964866 10.3836936081818 49.8428390108124 8.75870728325424</gml:posList></gml:LinearRing></gml:exterior></gml:Polygon></ogr:geometryProperty>
    </ogr:file3>
  </ogr:featureMember>
</ogr:FeatureCollection>
