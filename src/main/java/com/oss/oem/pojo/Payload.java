package com.oss.oem.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Payload {

    @SerializedName("TARGET_NAME")
    @Expose
    private String tARGETNAME;
    @SerializedName("TARGET_TYPE")
    @Expose
    private String tARGETTYPE;
    @SerializedName("METRIC_NAME")
    @Expose
    private String mETRICNAME;
    @SerializedName("METRIC_COLUMN")
    @Expose
    private String mETRICCOLUMN;
    @SerializedName("METRIC_LABEL")
    @Expose
    private String mETRICLABEL;
    @SerializedName("COLUMN_LABEL")
    @Expose
    private String cOLUMNLABEL;
    @SerializedName("COLLECTION_TIMESTAMP")
    @Expose
    private Long cOLLECTIONTIMESTAMP;
    @SerializedName("VALUE")
    @Expose
    private String vALUE;
    @SerializedName("KEY_VALUE")
    @Expose
    private String kEYVALUE;
    @SerializedName("TIMEZONE_REGION")
    @Expose
    private String tIMEZONEREGION;

    public String getTARGETNAME() {
        return tARGETNAME;
    }

    public void setTARGETNAME(String tARGETNAME) {
        this.tARGETNAME = tARGETNAME;
    }

    public String getTARGETTYPE() {
        return tARGETTYPE;
    }

    public void setTARGETTYPE(String tARGETTYPE) {
        this.tARGETTYPE = tARGETTYPE;
    }

    public String getMETRICNAME() {
        return mETRICNAME;
    }

    public void setMETRICNAME(String mETRICNAME) {
        this.mETRICNAME = mETRICNAME;
    }

    public String getMETRICCOLUMN() {
        return mETRICCOLUMN;
    }

    public void setMETRICCOLUMN(String mETRICCOLUMN) {
        this.mETRICCOLUMN = mETRICCOLUMN;
    }

    public String getMETRICLABEL() {
        return mETRICLABEL;
    }

    public void setMETRICLABEL(String mETRICLABEL) {
        this.mETRICLABEL = mETRICLABEL;
    }

    public String getCOLUMNLABEL() {
        return cOLUMNLABEL;
    }

    public void setCOLUMNLABEL(String cOLUMNLABEL) {
        this.cOLUMNLABEL = cOLUMNLABEL;
    }

    public Long getCOLLECTIONTIMESTAMP() {
        return cOLLECTIONTIMESTAMP;
    }

    public void setCOLLECTIONTIMESTAMP(Long cOLLECTIONTIMESTAMP) {
        this.cOLLECTIONTIMESTAMP = cOLLECTIONTIMESTAMP;
    }

    public String getVALUE() {
        return vALUE;
    }

    public void setVALUE(String vALUE) {
        this.vALUE = vALUE;
    }

    public String getKEYVALUE() {
        return kEYVALUE;
    }

    public void setKEYVALUE(String kEYVALUE) {
        this.kEYVALUE = kEYVALUE;
    }

    public String getTIMEZONEREGION() {
        return tIMEZONEREGION;
    }

    public void setTIMEZONEREGION(String tIMEZONEREGION) {
        this.tIMEZONEREGION = tIMEZONEREGION;
    }

}
