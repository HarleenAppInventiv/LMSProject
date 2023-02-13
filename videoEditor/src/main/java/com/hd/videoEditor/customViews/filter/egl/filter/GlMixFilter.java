package com.hd.videoEditor.customViews.filter.egl.filter;


import android.opengl.GLES20;
import android.util.Log;

/**
 * brightness value ranges from -1.0 to 1.0, with 0.0 as the normal level
 */
public class GlMixFilter extends GlFilter {
    private static final String BRIGHTNESS_FRAGMENT_SHADER = "" +
            "precision mediump float;" +
            " varying vec2 vTextureCoord;\n" +
            " \n" +
            " uniform lowp sampler2D sTexture;\n" +
            " uniform lowp float brightness;\n" +
            " uniform lowp float contrast;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     lowp vec4 textureColor = texture2D(sTexture, vTextureCoord);\n" +
            "     \n" +
            "     gl_FragColor = vec4(((textureColor.rgb - vec3(0.5)) * contrast + vec3(0.5)) + vec3(brightness), textureColor.w);\n" +
            " }";
    private float brightness = 0f;
    private float contrast = 1.2f;
    private float saturation = 1f;

    public GlMixFilter() {
        super(DEFAULT_VERTEX_SHADER, BRIGHTNESS_FRAGMENT_SHADER);
    }

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public float getContrast() {
        return contrast;
    }

    public void setContrast(float contrast) {
        this.contrast = contrast;
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    @Override
    public void onDraw() {
        GLES20.glUniform1f(getHandle("contrast"), contrast);
//        GLES20.glUniform1f(getHandle("saturation"), saturation);
        GLES20.glUniform1f(getHandle("brightness"), brightness);
    }

    public void adjustBrightness(int percentage) {
        this.brightness = range(percentage, -1.0f, 1.0f);
    }

    public void adjustSaturation(int percentage) {
        this.saturation = range(percentage, 0.0f, 2.0f);
    }

    public void adjustContrast(int percentage) {
        this.contrast = range(percentage, 0.0f, 2.0f);

    }

    private float range(int percentage, float start, float end) {
        float value = (end - start) * percentage / 100.0f + start;
        Log.d("RANGEEE", " value >>" + value);

        return value;
    }

}
