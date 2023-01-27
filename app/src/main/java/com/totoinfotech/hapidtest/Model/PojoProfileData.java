package com.totoinfotech.hapidtest.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoProfileData {
    @SerializedName("photo")
    @Expose
    public String photo;
    @SerializedName("firstName")
    @Expose
    public String firstName;
    @SerializedName("lastName")
    @Expose
    public String lastName;
    @SerializedName("phone")
    @Expose
    public String phone;
    @SerializedName("address")
    @Expose
    public String address;
}
