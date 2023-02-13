package com.hd.videoEditor.customViews.filter.egl.newFilter

import android.content.Context
import android.util.Log
import com.hd.videoEditor.R
import com.hd.videoEditor.customViews.filter.FilterAdjuster
import com.hd.videoEditor.customViews.filter.MixAdjuster
import com.hd.videoEditor.customViews.filter.egl.filter.*
import com.hd.videoEditor.model.FilterData

enum class NewFilterType {
    DEFAULT, BLACKWHITE, PLUM, BRIGHTNESS, CONTRAST, RETRO, LAVENDER, GRAYSCALE, INVERT, BUDAPEST, SATURATION, SEPIA,/* SHARP,*/ DESERT;

    companion object {
        fun createFilterList(): ArrayList<FilterData> {
            val list = ArrayList<FilterData>()
            val drawableList =
                arrayListOf<Int>(
                    R.drawable.ic_original,
                    R.drawable.ic_filter_blackwhite,
                    R.drawable.ic_filter_plum,
                    R.drawable.ic_filter_brightness,
                    R.drawable.ic_filter_contrast,
                    R.drawable.ic_filter_retro,
                    R.drawable.ic_filter_lavender,
                    R.drawable.ic_filter_grayscale,
                    R.drawable.ic_filter_invert,
                    R.drawable.ic_filter_budapest,
                    R.drawable.ic_filter_saturation,
                    R.drawable.ic_filter_sepia
//                ,R.drawable.ic_filter_sharpness
                    ,
                    R.drawable.ic_filter_desert
                )

            val nameList =
                arrayListOf<String>(
                    "Original",
                    "B&W",
                    "Plum",
                    "Brightness", "Contrast", "Retro",
                    "Lavender",
                    "Grayscale",
                    "Negative",
                    "Budapest",
                    "Saturation",
                    "Sepia"
//                ,R.drawable.ic_filter_sharpness
                    ,
                    "Desert"
                )
            values().forEachIndexed { index, newFilterType ->

                list.add(FilterData(nameList[index], drawableList[index], newFilterType))
            }
            return list
        }

        fun createBasicFiltrList(): ArrayList<NewFilterType> {
            val list = ArrayList<NewFilterType>()
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

        fun createGlFilter(filterType: NewFilterType?, context: Context): GlFilter {
            return when (filterType) {
                DEFAULT -> GlFilter()
                BLACKWHITE -> GlBlackWhiteFilter()
                PLUM -> GLPlumFilter()
                BRIGHTNESS -> {
                    val glBrightnessFilter = GlBrightnessFilter()
                    glBrightnessFilter.brightness = 0.2f
                    glBrightnessFilter
                }
                RETRO -> GlRetroFilter()
                LAVENDER -> GlLavenderFilter()
                CONTRAST -> {
                    val glContrastFilter = GlContrastFilter()
                    glContrastFilter.contrast = 1.5f
                    glContrastFilter
                }
//                MIX -> {
//                    val glMixFilter = GlMixFilter()
//                    glMixFilter.setContrast(2.5f)
//                    glMixFilter.brightness = (0.2f)
//                    glMixFilter
//                }
                GRAYSCALE -> GlGrayScaleFilter()
                BUDAPEST -> GlBudapestFilter()
//                FILTER_GROUP_SAMPLE -> GlFilterGroup(GlSepiaFilter(), GlVignetteFilter())
//                GAMMA -> {
//                    val glGammaFilter = GlGammaFilter()
//                    glGammaFilter.setGamma(2f)
//                    glGammaFilter
//                }
                DESERT -> GlDesertFilter()
//                GRAY_SCALE -> GlGrayScaleFilter()
//                HALFTONE -> GlHalftoneFilter()
//                HAZE -> {
//                    val glHazeFilter = GlHazeFilter()
//                    glHazeFilter.slope = -0.5f
//                    glHazeFilter
//                }
//                HIGHLIGHT_SHADOW -> GlHighlightShadowFilter()
//                HUE -> GlHueFilter()
                INVERT -> GlInvertFilter()
//                LOOK_UP_TABLE_SAMPLE -> {
//                    val bitmap =
//                        BitmapFactory.decodeResource(context.resources, R.drawable.lookup_sample)
//                    GlLookUpTableFilter(bitmap)
//                }
//                LUMINANCE -> GlLuminanceFilter()
//                LUMINANCE_THRESHOLD -> GlLuminanceThresholdFilter()
//                MONOCHROME -> GlMonochromeFilter()
//                OPACITY -> GlOpacityFilter()
//                PIXELATION -> GlPixelationFilter()
//                POSTERIZE -> GlPosterizeFilter()
//                RGB -> {
//                    val glRGBFilter = GlRGBFilter()
//                    glRGBFilter.setRed(0f)
//                    glRGBFilter
//                }
                SATURATION -> GlSaturationFilter()
                SEPIA -> GlSepiaFilter()
                /*  SHARP -> {
                      val glSharpenFilter = GlSharpenFilter()
                      glSharpenFilter.sharpness = 4f
                      glSharpenFilter
                  }*/
//                SOLARIZE -> GlSolarizeFilter()
//                SPHERE_REFRACTION -> GlSphereRefractionFilter()
//                SWIRL -> GlSwirlFilter()
//                TONE_CURVE_SAMPLE -> {
//                    try {
//                        val `is` = context.assets.open("acv/tone_cuver_sample.acv")
//                        return GlToneCurveFilter(`is`)
//                    } catch (e: IOException) {
//                        Log.e("FilterType", "Error")
//                    }
//                    GlFilter()
//                }
//                TONE -> GlToneFilter()
//                VIBRANCE -> {
//                    val glVibranceFilter = GlVibranceFilter()
//                    glVibranceFilter.setVibrance(3f)
//                    glVibranceFilter
//                }
//                VIGNETTE -> GlVignetteFilter()
//                WATERMARK -> GlWatermarkFilter(
//                    BitmapFactory.decodeResource(
//                        context.resources,
//                        R.drawable.ic_launcher_foreground
//                    ), GlWatermarkFilter.Position.RIGHT_BOTTOM
//                )
//                WEAK_PIXEL -> GlWeakPixelInclusionFilter()
//                WHITE_BALANCE -> {
//                    val glWhiteBalanceFilter = GlWhiteBalanceFilter()
//                    glWhiteBalanceFilter.setTemperature(2400f)
//                    glWhiteBalanceFilter.setTint(2f)
//                    glWhiteBalanceFilter
//                }
//                ZOOM_BLUR -> GlZoomBlurFilter()
//                BITMAP_OVERLAY_SAMPLE -> GlBitmapOverlaySample(
//                    BitmapFactory.decodeResource(
//                        context.resources,
//                        R.drawable.ic_launcher_foreground
//                    )
//                )
                else -> GlFilter()
            }
        }

        fun createMixAdjuster(): MixAdjuster? {
            return object : MixAdjuster {
                override fun adjustBrightness(filter: GlFilter?, percentage: Int) {
                    (filter as GlMixFilter).brightness = range(percentage, -1.0f, 1.0f)
                }

                override fun adjustContrast(filter: GlFilter?, percentage: Int) {
                    (filter as GlMixFilter).contrast = range(percentage, 0.0f, 2.0f)
                }

            }
        }

        fun createFilterAdjuster(filterType: NewFilterType?): FilterAdjuster? {
            return when (filterType) {
//                BILATERAL_BLUR -> object : FilterAdjuster {
//                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
//                        (filter as GlBilateralFilter).blurSize = range(percentage, 0.0f, 1.0f)
//                    }
//                }
//                BOX_BLUR -> object : FilterAdjuster {
//                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
//                        (filter as GlBoxBlurFilter).blurSize = range(percentage, 0.0f, 1.0f)
//                    }
//                }
                BRIGHTNESS -> object : FilterAdjuster {
                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
                        (filter as GlBrightnessFilter).brightness = range(percentage, -1.0f, 1.0f)
                    }
                }
//                MIX -> object : FilterAdjuster {
//                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
//                        (filter as GlMixFilter).setBrightness(
//                            range(percentage, -1.0f, 1.0f)
//                        )
//                        (filter as GlMixFilter).setContrast(
//                            range(percentage2, 0.0f, 2.0f)
//                        )
//
//                        (filter as GlMixFilter).setSaturation(
//                            range(percentage, 0.0f, 2.0f)
//                        )
//                    }
//                }
//                CONTRAST -> object : FilterAdjuster {
//                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
//                        (filter as GlContrastFilter).contrast = range(percentage, 0.0f, 2.0f)
//                    }
//                }
//                CROSSHATCH -> object : FilterAdjuster {
//                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
//                        (filter as GlCrosshatchFilter).setCrossHatchSpacing(
//                            range(
//                                percentage,
//                                0.0f,
//                                0.06f
//                            )
//                        )
//                        filter.setLineWidth(range(percentage, 0.0f, 0.006f))
//                    }
//                }
//                EXPOSURE -> object : FilterAdjuster {
//                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
//                        (filter as GlExposureFilter).setExposure(
//                            range(percentage, -10.0f, 10.0f)
//                        )
//                    }
//                }
//                GAMMA -> object : FilterAdjuster {
//                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
//                        (filter as GlGammaFilter).setGamma(
//                            range(percentage, 0.0f, 3.0f)
//                        )
//                    }
//                }
//                HAZE -> object : FilterAdjuster {
//                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
//                        (filter as GlHazeFilter).distance = range(percentage, -0.3f, 0.3f)
//                        filter.slope = range(percentage, -0.3f, 0.3f)
//                    }
//                }
//                HIGHLIGHT_SHADOW -> object : FilterAdjuster {
//                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
//                        (filter as GlHighlightShadowFilter).setShadows(
//                            range(
//                                percentage,
//                                0.0f,
//                                1.0f
//                            )
//                        )
//                        filter.setHighlights(range(percentage, 0.0f, 1.0f))
//                    }
//                }
//                HUE -> object : FilterAdjuster {
//                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
//                        (filter as GlHueFilter).setHue(
//                            range(
//                                percentage,
//                                0.0f,
//                                360.0f
//                            )
//                        )
//                    }
//                }
//                LUMINANCE_THRESHOLD -> object : FilterAdjuster {
//                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
//                        (filter as GlLuminanceThresholdFilter).setThreshold(
//                            range(percentage, 0.0f, 1.0f)
//                        )
//                    }
//                }
//                MONOCHROME -> object : FilterAdjuster {
//                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
//                        (filter as GlMonochromeFilter).intensity = range(percentage, 0.0f, 1.0f)
//                    }
//                }
//                OPACITY -> object : FilterAdjuster {
//                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
//                        (filter as GlOpacityFilter).setOpacity(
//                            range(percentage, 0.0f, 1.0f)
//                        )
//                    }
//                }
//                PIXELATION -> object : FilterAdjuster {
//                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
//                        (filter as GlPixelationFilter).setPixel(
//                            range(percentage, 1.0f, 100.0f)
//                        )
//                    }
//                }
//                POSTERIZE -> object : FilterAdjuster {
//                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
//                        // In theorie to 256, but only first 50 are interesting
//                        (filter as GlPosterizeFilter).setColorLevels(
//                            range(percentage, 1f, 50f).toInt()
//                        )
//                    }
//                }
//                RGB -> object : FilterAdjuster {
//                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
//                        (filter as GlRGBFilter).setRed(
//                            range(
//                                percentage,
//                                0.0f,
//                                1.0f
//                            )
//                        )
//                    }
//                }
                SATURATION -> object : FilterAdjuster {
                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
                        (filter as GlSaturationFilter).saturation = range(percentage, 0.0f, 2.0f)
                    }
                }
//                SHARP -> object : FilterAdjuster {
//                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
//                        (filter as GlSharpenFilter).sharpness = range(percentage, -4.0f, 4.0f)
//                    }
//                }
//                SOLARIZE -> object : FilterAdjuster {
//                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
//                        (filter as GlSolarizeFilter).setThreshold(
//                            range(percentage, 0.0f, 1.0f)
//                        )
//                    }
//                }
//                SWIRL -> object : FilterAdjuster {
//                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
//                        (filter as GlSwirlFilter).setAngle(
//                            range(percentage, 0.0f, 2.0f)
//                        )
//                    }
//                }
//                VIBRANCE -> object : FilterAdjuster {
//                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
//                        (filter as GlVibranceFilter).setVibrance(
//                            range(percentage, -1.2f, 1.2f)
//                        )
//                    }
//                }
//                VIGNETTE -> object : FilterAdjuster {
//                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
//                        (filter as GlVignetteFilter).vignetteStart = range(percentage, 0.0f, 1.0f)
//                    }
//                }
//                WHITE_BALANCE -> object : FilterAdjuster {
//                    override fun adjust(filter: GlFilter?, percentage: Int, percentage2: Int) {
//                        (filter as GlWhiteBalanceFilter).setTemperature(
//                            range(percentage, 2000.0f, 8000.0f)
//                        )
//                    }
//                }
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