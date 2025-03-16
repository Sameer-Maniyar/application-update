package com.sam.updater;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "UpdateMetaData")
public class UpdateMetaData {

    private String fileName;
    private String checkSum;

    private String versionNumber;

    public UpdateMetaData() {}


    public UpdateMetaData(String fileName, String checkSum, String versionNumber) {
        this.fileName = fileName;
        this.checkSum = checkSum;
        this.versionNumber = versionNumber;
    }

    @XmlElement
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @XmlElement
    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    @XmlElement
    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    @Override
    public String toString() {
        return "UpdateMetaData{" +
                "fileName='" + fileName + '\'' +
                ", checkSum='" + checkSum + '\'' +
                ", versionNumber='" + versionNumber + '\'' +
                '}';
    }

    public boolean compare(Object obj) {

        if(obj==null) return false;
        UpdateMetaData updateMetaData= (UpdateMetaData) obj;
        if(this.checkSum.equals(updateMetaData.getCheckSum())) return true;
        return false;
    }
}
