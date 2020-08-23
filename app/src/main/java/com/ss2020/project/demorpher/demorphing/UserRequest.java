
package com.ss2020.project.demorpher.demorphing;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserRequest {



    @SerializedName("clientQueryId")
    @Expose
    private String clientQueryId;
    @SerializedName("imageData")
    @Expose
    private ImageData imageData;

    public String getClientQueryId() {
        return clientQueryId;
    }

    public void setClientQueryId(String clientQueryId) {
        this.clientQueryId = clientQueryId;
    }

    public ImageData getImageData() {
        return imageData;
    }

    public void setImageData(ImageData imageData) {
        this.imageData = imageData;
    }

}
