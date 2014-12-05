package com.mattkula.giffer.datamodels;

import com.google.gson.JsonObject;

/**
 * Created by matt on 12/5/14.
 */
public class ImageResult {

    public String stillURL;
    public String gifURL;
    public String fullGifURL;

    public static ImageResult getFromJsonObject(JsonObject object) {
        ImageResult result = new ImageResult();
        result.gifURL = object.getAsJsonObject("images")
                .getAsJsonObject("fixed_height")
                .get("url").getAsString();
        result.stillURL = object.getAsJsonObject("images")
                .getAsJsonObject("fixed_height_still")
                .get("url").getAsString();
        result.fullGifURL = object.getAsJsonObject("images")
                .getAsJsonObject("original")
                .get("url").getAsString();
        return result;
    }
}
