package com.hd.videoEditor.customViews.filter.egl.filter;

import android.opengl.GLES20;

public class GlSaturationFilter extends GlFilter {
    private static final String SATURATION_FRAGMENT_SHADER = "" +
            "precision mediump float;" +
            " varying vec2 vTextureCoord;\n" +
            " \n" +
            " uniform lowp sampler2D sTexture;\n" +
            " uniform lowp float saturation;\n" +
            " \n" +
            " const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "    lowp vec4 textureColor = texture2D(sTexture, vTextureCoord);\n" +
            "    lowp float luminance = dot(textureColor.rgb, luminanceWeighting);\n" +
            "    lowp vec3 greyScaleColor = vec3(luminance);\n" +
            "    \n" +
            "    gl_FragColor = vec4(mix(greyScaleColor, textureColor.rgb, saturation), textureColor.w);\n" +
            "     \n" +
            " }";
    private float saturation = 1f;

    public GlSaturationFilter() {
        super(DEFAULT_VERTEX_SHADER, SATURATION_FRAGMENT_SHADER);
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    @Override
    public void onDraw() {
        GLES20.glUniform1f(getHandle("saturation"), saturation);
    }

}
