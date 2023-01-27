package com.totoinfotech.hapidtest.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoUploadApiResponse {
    @SerializedName("data")
    @Expose
    PojoProfileData data;
}
