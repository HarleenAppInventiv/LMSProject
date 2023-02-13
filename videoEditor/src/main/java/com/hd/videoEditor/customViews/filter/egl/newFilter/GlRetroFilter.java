package com.hd.videoEditor.customViews.filter.egl.newFilter;


import com.hd.videoEditor.customViews.filter.egl.filter.GlFilter;

public class GlRetroFilter extends GlFilter {

    private static final String FRAGMENT_SHADER =
            "precision mediump float;" +
                    "varying vec2 vTextureCoord;" +
                    "uniform lowp sampler2D sTexture;" +
                    "const highp vec3 weight = vec3(0.2125, 0.7154, 0.0721);" +
                    "void main() {" +
                    "   vec4 FragColor = texture2D(sTexture, vTextureCoord);\n" +
                    "   gl_FragColor.r = dot(FragColor.rgb, vec3(0.300, 0.200, 0.500));\n" +
                    "   gl_FragColor.g = dot(FragColor.rgb, vec3(0.300, 0.400, 0.300));\n" +
                    "   gl_FragColor.b = dot(FragColor.rgb, vec3(0.300, 0.100, 0.200));\n" +
                    "}";


    public GlRetroFilter() {
        super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
    }

}
