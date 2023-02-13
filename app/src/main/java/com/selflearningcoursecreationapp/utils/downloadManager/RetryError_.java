package com.selflearningcoursecreationapp.utils.downloadManager;

/**
 * Created by maniselvaraj on 15/4/15.
 */
public class RetryError_ extends Exception {

    public RetryError_() {
        super("Maximum retry exceeded");
    }

    public RetryError_(Throwable cause) {
        super(cause);
    }
}
