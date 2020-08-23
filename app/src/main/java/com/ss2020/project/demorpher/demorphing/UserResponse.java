
package com.ss2020.project.demorpher.demorphing;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserResponse {

    @SerializedName("clientQueryId")
    @Expose
    private String clientQueryId;
    @SerializedName("resultData")
    @Expose
    private ResultData resultData;

    public String getClientQueryId() {
        return clientQueryId;
    }

    public void setClientQueryId(String clientQueryId) {
        this.clientQueryId = clientQueryId;
    }

    public ResultData getResultData() {
        return resultData;
    }

    public void setResultData(ResultData resultData) {
        this.resultData = resultData;
    }

}
