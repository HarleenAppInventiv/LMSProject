package com.hd.videoEditor.customViews.filter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import com.hd.videoEditor.R
import com.hd.videoEditor.customViews.filter.egl.filter.*
import com.hd.videoEditor.customViews.filter.egl.newFilter.GlGrayScaleFilter
import java.io.IOException

enum class FilterType {
    DEFAULT, MIX, BILATERAL_BLUR, BOX_BLUR, BRIGHTNESS, BULGE_DISTORTION, CGA_COLORSPACE, CONTRAST, CROSSHATCH, EXPOSURE, FILTER_GROUP_SAMPLE, GAMMA, GAUSSIAN_FILTER, GRAY_SCALE, HALFTONE, HAZE, HIGHLIGHT_SHADOW, HUE, INVERT, LOOK_UP_TABLE_SAMPLE, LUMINANCE, LUMINANCE_THRESHOLD, MONOCHROME, OPACITY, OVERLAY, PIXELATION, POSTERIZE, RGB, SATURATION, SEPIA, SHARP, SOLARIZE, SPHERE_REFRACTION, SWIRL, TONE_CURVE_SAMPLE, TONE, VIBRANCE, VIGNETTE, WATERMARK, WEAK_PIXEL, WHITE_BALANCE, ZOOM_BLUR, BITMAP_OVERLAY_SAMPLE;

    companion object {
        fun createFilterList(): ArrayList<FilterType> {
            val list = ArrayList<FilterType>()
            values().forEach {
                Log.d("DATA_FILTER", "${it.name}")
                Log.d("DATA_FILTER", "${it.ordinal}")
                list.add(it)
            }
            return list
        }

        fun createBasicFiltrList(): ArrayList<FilterType> {
            val list = ArrayList<FilterType>()
            values().forEach {
                if (it.name.equals("DEFAULT") || it.name.equals("MIX") || it.name.equals("BRIGHTNESS") || it.name.equals(
                        "CONTRAST"
                    ) || it.name.equals(
                        "SATURATION"
                    )
                ) {
                    list.add(it)
                }
            }
            return list
        }

        fun createGlFilter(filterType: FilterType?, context: Context): GlFilter {
            return when (filterType) {
                DEFAULT -> GlFilter()
                BILATERAL_BLUR -> GlBilateralFilter()
                BOX_BLUR -> GlBoxBlurFilter()
                BRIGHTNESS -> {
                    val glBrightnessFilter = GlBrightnessFilter()
                    glBrightnessFilter.setBrightness(0.2f)
                    glBrightnessFilter
                }
                BULGE_DISTORTION -> GlBulgeDistortionFilter()
                CGA_COLORSPACE -> GlCGAColorspaceFilter()
                CONTRAST -> {
                    val glContrastFilter = GlContrastFilter()
                    glContrastFilter.setContrast(2.5f)
                    glContrastFilter
                }
                MIX -> {
                    val glMixFilter = GlMixFilter()
                    glMixFilter.setContrast(2.5f)
                    glMixFilter.brightness = (0.2f)
                    glMixFilter
                }
                CROSSHATCH -> GlCrosshatchFilter()
                EXPOSURE -> GlExposureFilter()
                FILTER_GROUP_SAMPLE -> GlFilterGroup(GlSepiaFilter(), GlVignetteFilter())
                GAMMA -> {
                    val glGammaFilter = GlGammaFilter()
                    glGammaFilter.setGamma(2f)
                    glGammaFilter
                }
                GAUSSIAN_FILTER -> GlGaussianBlurFilter()
                GRAY_SCALE -> GlGrayScaleFilter()
                HALFTONE -> GlHalftoneFilter()
                HAZE -> {
                    val glHazeFilter = GlHazeFilter()
                    glHazeFilter.slope = -0.5f
                    glHazeFilter
                }
                HIGHLIGHT_SHADOW -> GlHighlightShadowFilter()
                HUE -> GlHueFilter()
                INVERT -> GlInvertFilter()
                LOOK_UP_TABLE_SAMPLE -> {
                    val bitmap =
                        BitmapFactory.decodeResource(context.resources, R.drawable.lookup_sample)
                    GlLookUpTableFilter(bitmap)
                }
                LUMINANCE -> GlLuminanceFilter()
                LUMINANCE_THRESHOLD -> GlLuminanceThresholdFilter()
                MONOCHROME -> GlMonochromeFilter()
                OPACITY -> GlOpacityFilter()
                PIXELATION -> GlPixelationFilter()
                POSTERIZE -> GlPosterizeFilter()
                RGB -> {
                    val glRGBFilter = GlRGBFilter()
                    glRGBFilter.setRed(0f)
                    glRGBFilter
                }
                SATURATION -> GlSaturationFilter()
                SEPIA -> GlSepiaFilter()
                SHARP -> {
                    val glSharpenFilter = GlSharpenFilter()
                    glSharpenFilter.sharpness = 4f
                    glSharpenFilter
                }
                SOLARIZE -> GlSolarizeFilter()
                SPHERE_REFRACTION -> GlSphereRefractionFilter()
                SWIRL -> GlSwirlFilter()
                TONE_CURVE_SAMPLE -> {
                    try {
                        val `is` = context.assets.open("acv/tone_cuver_sample.acv")
                        return GlToneCurveFilter(`is`)
                    } catch (e: IOException) {
                        Log.e("FilterType", "Error")
                    }
                    GlFilter()
                }
                TONE -> GlToneFilter()
                VIBRANCE -> {
                    val glVibranceFilter = GlVibranceFilter()
                    glVibranceFilter.setVibrance(3f)
                    glVibranceFilter
                }
                VIGNETTE -> GlVignetteFilter()
                WATERMARK -> GlWatermarkFilter(
                    BitmapFactory.decodeResource(
                        context.resources,
                        R.drawable.ic_launcher_foreground
                    ), GlWatermarkFilter.Position.RIGHT_BOTTOM
                )
                WEAK_PIXEL -> GlWeakPixelInclusionFilter()
                WHITE_BALANCE -> {
                    val glWhiteBalanceFilter = GlWhiteBalanceFilter()
                    glWhiteBalanceFilter.setTemperature(2400f)
                    glWhiteBalanceFilter.setTint(2f)
                    glWhiteBalanceFilter
                }
                ZOOM_BLUR -> GlZoomBlurFilter()
                BITMAP_OVERLAY_SAMPLE -> GlBitmapOverlaySample(
                    BitmapFactory.decodeResource(
                        context.resources,
                        R.drawable.ic_launcher_foreground
                    )
                )
                else -> GlFilter()
            }
        }

        fun createMixAdjuster(): MixAdjuster? {
            return object : MixAdjuster {
                override fun adjustBrightness(filter: GlFilter?, percentage: Int) {
                    (filter as GlMixFilter).setBrightness(
                        range(percentage, -1.0f, 1.0f)
                    )
                }

                override fun adjustContrast(filter: GlFilter?, percentage: Int) {
                    (filter as GlMixFilter).setContrast(
                        range(percentage, 0.0f, 2.0f)
                    )
                }

            }
        }

        fun createFilterAdjuster(filterType: FilterType?): FilterAdjuster? {
            return when (filterType) {
                BILATERAL_BLUR -> object : FilterAdjuster {
                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
                        (filter as GlBilateralFilter).blurSize = range(percentage, 0.0f, 1.0f)
                    }
                }
                BOX_BLUR -> object : FilterAdjuster {
                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
                        (filter as GlBoxBlurFilter).blurSize = range(percentage, 0.0f, 1.0f)
                    }
                }
                BRIGHTNESS -> object : FilterAdjuster {
                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
                        (filter as GlBrightnessFilter).setBrightness(
                            range(percentage, -1.0f, 1.0f)
                        )
                    }
                }
                MIX -> object : FilterAdjuster {
                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
                        (filter as GlMixFilter).setBrightness(
                            range(percentage, -1.0f, 1.0f)
                        )
                        (filter as GlMixFilter).setContrast(
                            range(percentage2, 0.0f, 2.0f)
                        )

                        (filter as GlMixFilter).setSaturation(
                            range(percentage, 0.0f, 2.0f)
                        )
                    }
                }
                CONTRAST -> object : FilterAdjuster {
                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
                        (filter as GlContrastFilter).setContrast(
                            range(percentage, 0.0f, 2.0f)
                        )
                    }
                }
                CROSSHATCH -> object : FilterAdjuster {
                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
                        (filter as GlCrosshatchFilter).setCrossHatchSpacing(
                            range(
                                percentage,
                                0.0f,
                                0.06f
                            )
                        )
                        filter.setLineWidth(range(percentage, 0.0f, 0.006f))
                    }
                }
                EXPOSURE -> object : FilterAdjuster {
                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
                        (filter as GlExposureFilter).setExposure(
                            range(percentage, -10.0f, 10.0f)
                        )
                    }
                }
                GAMMA -> object : FilterAdjuster {
                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
                        (filter as GlGammaFilter).setGamma(
                            range(percentage, 0.0f, 3.0f)
                        )
                    }
                }
                HAZE -> object : FilterAdjuster {
                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
                        (filter as GlHazeFilter).distance = range(percentage, -0.3f, 0.3f)
                        filter.slope = range(percentage, -0.3f, 0.3f)
                    }
                }
                HIGHLIGHT_SHADOW -> object : FilterAdjuster {
                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
                        (filter as GlHighlightShadowFilter).setShadows(
                            range(
                                percentage,
                                0.0f,
                                1.0f
                            )
                        )
                        filter.setHighlights(range(percentage, 0.0f, 1.0f))
                    }
                }
                HUE -> object : FilterAdjuster {
                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
                        (filter as GlHueFilter).setHue(
                            range(
                                percentage,
                                0.0f,
                                360.0f
                            )
                        )
                    }
                }
                LUMINANCE_THRESHOLD -> object : FilterAdjuster {
                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
                        (filter as GlLuminanceThresholdFilter).setThreshold(
                            range(percentage, 0.0f, 1.0f)
                        )
                    }
                }
                MONOCHROME -> object : FilterAdjuster {
                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
                        (filter as GlMonochromeFilter).intensity = range(percentage, 0.0f, 1.0f)
                    }
                }
                OPACITY -> object : FilterAdjuster {
                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
                        (filter as GlOpacityFilter).setOpacity(
                            range(percentage, 0.0f, 1.0f)
                        )
                    }
                }
                PIXELATION -> object : FilterAdjuster {
                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
                        (filter as GlPixelationFilter).setPixel(
                            range(percentage, 1.0f, 100.0f)
                        )
                    }
                }
                POSTERIZE -> object : FilterAdjuster {
                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
                        // In theorie to 256, but only first 50 are interesting
                        (filter as GlPosterizeFilter).setColorLevels(
                            range(percentage, 1f, 50f).toInt()
                        )
                    }
                }
                RGB -> object : FilterAdjuster {
                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
                        (filter as GlRGBFilter).setRed(
                            range(
                                percentage,
                                0.0f,
                                1.0f
                            )
                        )
                    }
                }
                SATURATION -> object : FilterAdjuster {
                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
                        (filter as GlSaturationFilter).setSaturation(
                            range(percentage, 0.0f, 2.0f)
                        )
                    }
                }
                SHARP -> object : FilterAdjuster {
                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
                        (filter as GlSharpenFilter).sharpness = range(percentage, -4.0f, 4.0f)
                    }
                }
                SOLARIZE -> object : FilterAdjuster {
                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
                        (filter as GlSolarizeFilter).setThreshold(
                            range(percentage, 0.0f, 1.0f)
                        )
                    }
                }
                SWIRL -> object : FilterAdjuster {
                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
                        (filter as GlSwirlFilter).setAngle(
                            range(percentage, 0.0f, 2.0f)
                        )
                    }
                }
                VIBRANCE -> object : FilterAdjuster {
                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
                        (filter as GlVibranceFilter).setVibrance(
                            range(percentage, -1.2f, 1.2f)
                        )
                    }
                }
                VIGNETTE -> object : FilterAdjuster {
                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
                        (filter as GlVignetteFilter).vignetteStart = range(percentage, 0.0f, 1.0f)
                    }
                }
                WHITE_BALANCE -> object : FilterAdjuster {
                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
                        (filter as GlWhiteBalanceFilter).setTemperature(
                            range(percentage, 2000.0f, 8000.0f)
                        )
                    }
                }
                else -> null
            }
        }

        private fun range(percentage: Int, start: Float, end: Float): Float {
            val value = (end - start) * percentage / 100.0f + start
            Log.d("RANGEEE", " value $value")

            return value
        }


    }
}