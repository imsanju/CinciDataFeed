package com.example.chilipepper.finalgooglemapsproject;

/**
 * Created by delshawnkirksey on 12/9/15.
 */
public class Blip {

    public String _inspectionType, _violationDescr, _businessName, _address, _city, _state, _recordNum, _zip, _violationComments;
    public double _longitude, _latitude;

    public Blip (String inspectionType, String violationDescr, String businessName, String address, String city, String state, String recordNum, String zip, String violationComments, double longitude, double latitude )
    {
        _inspectionType = inspectionType;
        _violationDescr = violationDescr;
        _businessName = businessName;
        _longitude = longitude;
        _latitude = latitude;
        _address = address;
        _city = city;
        _state = state;
        _recordNum = recordNum;
        _zip = zip;
        _violationComments = violationComments;
    }

    public String get_inspectionType() { return _inspectionType; }
    public String get_violationDescr() { return _violationDescr; }
    public String get_businessName() { return _businessName; }
    public String get_address() { return _address; }
    public String get_city() { return _city; }
    public String get_state() { return _state; }
    public String get_recordNum() { return _recordNum; }
    public String get_zip() { return _zip; }
    public String get_violationComments() { return _violationComments; }
    public double get_longitude() { return _longitude; }
    public double get_latitude() { return _latitude; }


}
