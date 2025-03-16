package com.sam.dto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "UpdateMetaData")
public class UpdateMetaData {

    private String fileName;
    private String checkSum;

    private String versionNumber;

    private Boolean isUpdatedJarDownloaded;
    private Boolean applyUpdate;
    private Boolean download;

    public UpdateMetaData() {}


    public UpdateMetaData(String fileName, String checkSum, String versionNumber, Boolean isUpdatedJarDownloaded, Boolean applyUpdate, Boolean download) {
        this.fileName = fileName;
        this.checkSum = checkSum;
        this.versionNumber = versionNumber;
        this.isUpdatedJarDownloaded = isUpdatedJarDownloaded;
        this.applyUpdate = applyUpdate;
        this.download = download;
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


    @XmlElement
    public Boolean getUpdatedJarDownloaded() {
        return isUpdatedJarDownloaded;
    }

    public void setUpdatedJarDownloaded(Boolean updatedJarDownloaded) {
        isUpdatedJarDownloaded = updatedJarDownloaded;
    }

    @XmlElement
    public Boolean getApplyUpdate() {
        return applyUpdate;
    }

    public void setApplyUpdate(Boolean applyUpdate) {
        this.applyUpdate = applyUpdate;
    }

    @XmlElement
    public Boolean getDownload() {
        return download;
    }

    public void setDownload(Boolean download) {
        this.download = download;
    }


    @Override
    public String toString() {
        return "UpdateMetaData{" +
                "fileName='" + fileName + '\'' +
                ", checkSum='" + checkSum + '\'' +
                ", versionNumber='" + versionNumber + '\'' +
                ", isUpdatedJarDownloaded=" + isUpdatedJarDownloaded +
                ", applyUpdate=" + applyUpdate +
                ", download=" + download +
                '}';
    }

    public boolean compare(Object obj) {

        if(obj==null) return false;
        UpdateMetaData updateMetaData= (UpdateMetaData) obj;
        if(this.checkSum.equals(updateMetaData.getCheckSum())) return true;
        return false;
    }
}
