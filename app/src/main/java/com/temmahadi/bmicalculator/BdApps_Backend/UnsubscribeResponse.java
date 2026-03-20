package com.temmahadi.bmicalculator.BdApps_Backend;

import com.google.gson.annotations.SerializedName;

public class UnsubscribeResponse {
    @SerializedName("statusCode")
    private String statusCode;

    @SerializedName("statusDetail")
    private String statusDetail;

    @SerializedName("subscriptionStatus")
    private String subscriptionStatus;

    @SerializedName("version")
    private String version;

    @SerializedName("requestId")
    private String requestId;

    @SerializedName("rawResponse")
    private String rawResponse;

    // Getters
    public String getStatusCode() {
        return statusCode;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    public String getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public String getVersion() {
        return version;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    // Setters
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public void setStatusDetail(String statusDetail) {
        this.statusDetail = statusDetail;
    }

    public void setSubscriptionStatus(String subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setRawResponse(String rawResponse) {
        this.rawResponse = rawResponse;
    }

    // Helper method to check if unsubscribe was successful
    public boolean isSuccess() {
        return "S1000".equals(statusCode);
    }

    // Helper method to check if there was an error
    public boolean isError() {
        return subscriptionStatus != null && subscriptionStatus.equals("ERROR");
    }
}
