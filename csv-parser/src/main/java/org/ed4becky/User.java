package org.ed4becky;

import com.opencsv.bean.CsvBindByName;

public class User {
    @CsvBindByName(column = "User Id", required = true)
    public String id;
    @CsvBindByName(column = "First Name", required = true)
    public String firstName;
    @CsvBindByName(column = "Last Name", required = true)
    public String lastName;
    @CsvBindByName(column = "Version", required = true)
    public int version;
    @CsvBindByName(column = "Insurance Company", required = true)
    public String insuranceCompany;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }
    public String getLine() {
        StringBuilder builder = new StringBuilder();
         builder.append("\"")
                .append(id).append("\",\"") 
                .append(firstName).append("\",\"") 
                .append(lastName).append("\",") 
                .append(version).append(",\"")
                .append(insuranceCompany).append("\"\n")
                ;
        return builder.toString();
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", version=" + version
                + ", insuranceCompany=" + insuranceCompany + "]";
    }


}
