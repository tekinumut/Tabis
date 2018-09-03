package com.umonsoft.tabis.model;

public class RecordsModel {

    private final int id;
    private final String department;
    private final String description;
    private final String address;
    private final String addressdesc;
    private final String lattitude;
    private final String longitude;
    private final String state;
    private final String statedesc;
    private final String addingdate;



    public RecordsModel(int id, String department, String description, String address, String addressdesc,
                        String lattitude, String longitude, String state, String statedesc, String addingdate) {

        this.id = id;
        this.department = department;
        this.description = description;
        this.address = address;
        this.addressdesc = addressdesc;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.state = state;
        this.statedesc=statedesc;
        this.addingdate=addingdate;
    }


    public int getId() {
        return id;
    }

    public String getAddingdate() {return addingdate;}

    public String getDepartment() {
        return department;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public String getAddressdesc() {
        return addressdesc;
    }

    public String getLattitude() {
        return lattitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getState() {
        return state;
    }

    public String getStatedesc() {return statedesc;}




}
