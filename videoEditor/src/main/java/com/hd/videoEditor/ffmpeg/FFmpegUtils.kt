package com.hd.videoEditor.ffmpeg

import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.SessionState
import com.hd.videoEditor.customViews.filter.egl.filter.*
import com.hd.videoEditor.customViews.filter.egl.newFilter.*
import com.hd.videoEditor.model.EditedVideoData
import com.hd.videoEditor.utils.Logd

object FFmpegUtils {

    private var command: String = ""
    private var apiCode: String = ""
    private var inputPath: String? = ""
    private var outputPath: String? = ""
    private var resultInfo: EditedVideoData? = null
    val TAG = "FFmpeg_utils"
    fun executeCommand(onCallback: (FFmpegStateData) -> Unit) {
        onCallback(FFmpegStateData(state = FFmpegStateConst.STATE_LOADING, operationCode = apiCode))
        Logd(TAG, " command >>> $command")

        FFmpegKit.executeAsync(command) { session ->
            Logd(TAG, "$session")
            when (session.state) {
                SessionState.COMPLETED -> {
                    if (session.returnCode.isValueSuccess) {
                        onCallback(
                            FFmpegStateData(
                                state = FFmpegStateConst.STATE_COMPLETED,
                                operationCode = apiCode
                            )
                        )
                    } else {
                        onCallback(
                            FFmpegStateData(
                                state = FFmpegStateConst.STATE_FAILED,
                                operationCode = apiCode
                            )
                        )

                    }
                }
                SessionState.FAILED -> {
                    onCallback(
                        FFmpegStateData(
                            state = FFmpegStateConst.STATE_FAILED,
                            operationCode = apiCode,
                            error = "Unable to complete"
                        )
                    )

                }
                SessionState.CREATED, SessionState.RUNNING -> {
                    onCallback(
                        FFmpegStateData(
                            state = FFmpegStateConst.STATE_LOADING,
                            operationCode = apiCode
                        )
                    )

                }
            }
        }
    }

    fun apiCode(apiCode: String): FFmpegUtils {
        this.apiCode = apiCode
        return this
    }

    fun inputPath(path: String?): FFmpegUtils {
        this.inputPath = path
        return this
    }

    fun outputPath(path: String?): FFmpegUtils {
        this.outputPath = path
        return this
    }

    fun resultedInfo(info: EditedVideoData): FFmpegUtils {
        this.resultInfo = info
        makeCommand()
        return this
    }

    private fun makeCommand() {

        var filterCommand = ""
        var vidOut = ""
        if (getFilterCommand(vidOut).isNotEmpty()) {
            vidOut = "[video]"
            filterCommand += "[0:v]${getFilterCommand(vidOut)}${vidOut}"
        }


        var watermarkTripleMap = getWatermarkCommand(vidOut)
        if (!watermarkTripleMap.second.isNullOrEmpty()) {
            if (filterCommand.isNullOrEmpty()) {
                filterCommand = watermarkTripleMap.second
            } else {
                filterCommand = filterCommand + ", " + watermarkTripleMap.second
            }
            vidOut = watermarkTripleMap.first
        }

//        if (vidOut.isEmpty())
//        {
//            vidOut="[0:v]"
//
//        }
//        var scaleCmd="${vidOut}scale=iw/2:-2[finalVideo]"
//        vidOut="[finalVideo]"

        if (inputPath?.endsWith("webm") == false) {
            if (vidOut.isEmpty()) {
                vidOut = "[0:v]"

            }
            val formatCmd = "${vidOut}format=yuv420p[formatVideo]"
            if (filterCommand.isNotEmpty()) {
                filterCommand = filterCommand + ", ${formatCmd}"

            } else {
                filterCommand = formatCmd
            }
            vidOut = "[formatVideo]"

        }

        if (filterCommand.isNotEmpty()) {

            filterCommand = "-filter_complex '${filterCommand}' "
        }/*else{
            filterCommand = "-filter_complex '${scaleCmd}' "

        }*/


        var mapCmd = ""
        if (vidOut.isNotEmpty()) {
            mapCmd = "-map '$vidOut' -map 0:a:0? "
        }


        command =
            if (inputPath?.endsWith(".webm") == true) {
                "-ss ${(resultInfo?.startPosition ?: 0) / 1000} -y -i $inputPath ${watermarkTripleMap.third}$filterCommand-t ${((resultInfo?.endPosition ?: 0) - (resultInfo?.startPosition ?: 0)) / 1000} ${mapCmd}-movflags faststart -strict experimental -crf 1 -c:v libx264 -b:v 2097152 -b:a 48000 -ac 2 -preset ultrafast -max_muxing_queue_size 1024 $outputPath"

            } else {
                "-ss ${(resultInfo?.startPosition ?: 0) / 1000} -y -i $inputPath ${watermarkTripleMap.third}$filterCommand-t ${((resultInfo?.endPosition ?: 0) - (resultInfo?.startPosition ?: 0)) / 1000} ${mapCmd}-movflags faststart -strict experimental -vcodec libx264 -b:v 2097152 -b:a 48000 -ac 2 -ar 22050 -preset ultrafast -crf 28 $outputPath"

            }

//        "-y -ss ${resultInfo?.startPosition} -i $inputPath -strict experimental $filterCommand-t ${((resultInfo?.endPosition ?: 0) - (resultInfo?.startPosition ?: 0)) / 1000} $mapCmd-c:v libx264 -c:a copy -movflags +faststart -b:a 48000 -r 25 -profile:v baseline -level 3.0 -pix_fmt yuv420p -minrate 1024k -maxrate 1024k -bufsize 1024k -ac 2 -ar 22050 $outputPath"
//            "-y -ss ${resultInfo?.startPosition} -i $inputPath $filterCommand-t ${((resultInfo?.endPosition ?: 0) - (resultInfo?.startPosition ?: 0)) / 1000} -c:v libx264 -c:a aac -r 25 -profile:v baseline -level 3.0 -pix_fmt yuv420p -minrate 1024k -maxrate 1024k -bufsize 1024k -movflags +faststart -strict experimental -vcodec mpeg4 -b:v 150k -b:a 48000 -ac 2 -ar 22050 $outputPath"
//            "-y -ss ${resultInfo?.startPosition} -i $inputPath -c:v copy -c:a aac -movflags +faststart -strict experimental -b:v 2097152 -b:a 48000 -ac 2 -ar 22050 $filterCommand-t ${((resultInfo?.endPosition ?: 0) - (resultInfo?.startPosition ?: 0)) / 1000} $outputPath"
//            "-y -ss ${resultInfo?.startPosition} -i $inputPath $filterCommand-t ${((resultInfo?.endPosition ?: 0) - (resultInfo?.startPosition ?: 0)) / 1000} -c:v libx264 -movflags +faststart -strict experimental -vcodec mpeg4 -b:v 2097152 -c:a aac -b:a 48000 -ac 2 -ar 22050 $outputPath"
    }

    private fun getFilterCommand(vidOut: String): String {
        var cmd = ""
        cmd = when (resultInfo?.filter) {
            is GlBlackWhiteFilter -> {
                getBlackWhiteFilterCommand()
            }
            is GLPlumFilter -> {
                getPlumFilterCommand()
            }
            is GlBrightnessFilter -> {
                getBrightnessFilterCommand()
            }
            is GlContrastFilter -> {
                getContrastFilterCommand()
            }
            is GlRetroFilter -> {
                getRetroFilterCommand()
            }
            is GlLavenderFilter -> {
                getLavenderFilterCommand()
            }
            is GlGrayScaleFilter -> {
                getGrayScaleFilterCommand()
            }
            is GlInvertFilter -> {
                getInvertFilterCommand()
            }
            is GlBudapestFilter -> {
                getBudapestFilterCommand()
            }
            is GlSaturationFilter -> {
                getSaturationFilterCommand()
            }
            is GlSepiaFilter -> {
                getSepiaFilterCommand()
            }
            is GlDesertFilter -> {
                getDesertFilterCommand()
            }
            else -> ""
        }
        return cmd
    }


    private fun getSepiaFilterCommand(): String {
        return "colorchannelmixer=.393:.769:.189:0:.349:.686:.168:0:.272:.534:.131"/*[colorchannelmixed],[colorchannelmixed]eq=1.0:0:1.3:2.4:1.0:1.0:1.0:1.0*/
    }

    private fun getDesertFilterCommand(): String {
        return "colorchannelmixer=.300:.800:.500:0:.300:.400:.300:0:.300:.100:.200"/*[colorchannelmixed],[colorchannelmixed]eq=1.0:0:1.3:2.4:1.0:1.0:1.0:1.0*/
    }

    private fun getGrayScaleFilterCommand(): String {
        return "colorchannelmixer=.300:.400:.300:0:.300:.400:.300:0:.300:.400:.300"/*[colorchannelmixed],[colorchannelmixed]eq=1.0:0:1.3:2.4:1.0:1.0:1.0:1.0*/
    }

    private fun getBlackWhiteFilterCommand(): String {
        return "colorchannelmixer=.700:.400:.300:0:.700:.400:.300:0:.700:.400:.300"/*[colorchannelmixed],[colorchannelmixed]eq=1.0:0:1.3:2.4:1.0:1.0:1.0:1.0*/
    }

    private fun getPlumFilterCommand(): String {
        return "colorchannelmixer=.300:.400:.300:0:.600:.300:.500:0:.300:1.000:.400"/*[colorchannelmixed],[colorchannelmixed]eq=1.0:0:1.3:2.4:1.0:1.0:1.0:1.0*/
    }

    private fun getBrightnessFilterCommand(): String {
        return "eq=brightness=0.2"
    }

    private fun getContrastFilterCommand(): String {
        return "eq=contrast=1.5"
    }

    private fun getSaturationFilterCommand(): String {
        return "eq=saturation=1"
    }

    private fun getRetroFilterCommand(): String {
        return "colorchannelmixer=.300:.200:.500:0:.300:.400:.300:0:.300:.100:.200"/*[colorchannelmixed],[colorchannelmixed]eq=1.0:0:1.3:2.4:1.0:1.0:1.0:1.0*/
    }

    private fun getLavenderFilterCommand(): String {
        return "colorchannelmixer=.000:.300:0.800:0:0.500:.100:.500:0:.500:.500:1.800"/*[colorchannelmixed],[colorchannelmixed]eq=1.0:0:1.3:2.4:1.0:1.0:1.0:1.0*/
    }

    private fun getBudapestFilterCommand(): String {
        return "colorchannelmixer=.700:.800:.600:0:.200:.300:.700:0:0.500:0.500:.200"/*[colorchannelmixed],[colorchannelmixed]eq=1.0:0:1.3:2.4:1.0:1.0:1.0:1.0*/
    }

    private fun getOrange1FilterCommand(): String {
        return "colorchannelmixer=.300:.800:.500:0:.300:.400:.300:0:.300:.100:.200"/*[colorchannelmixed],[colorchannelmixed]eq=1.0:0:1.3:2.4:1.0:1.0:1.0:1.0*/
    }

    private fun getVintageFilterCommand(): String {
        return "curves=vintage"/*[colorchannelmixed],[colorchannelmixed]eq=1.0:0:1.3:2.4:1.0:1.0:1.0:1.0*/
    }

    private fun getInvertFilterCommand(): String {
        return "curves=negative"/*[colorchannelmixed],[colorchannelmixed]eq=1.0:0:1.3:2.4:1.0:1.0:1.0:1.0*/
    }

    private fun getColorNegativeFilterCommand(): String {
        return "curves=color_negative"/*[colorchannelmixed],[colorchannelmixed]eq=1.0:0:1.3:2.4:1.0:1.0:1.0:1.0*/
    }

    private fun getBoxBlurFilterCommand(): String {
        return "boxblur=5"/*[colorchannelmixed],[colorchannelmixed]eq=1.0:0:1.3:2.4:1.0:1.0:1.0:1.0*/
    }

    private fun getGBlurFilterCommand(): String {
        return "gblur=sigma=10"/*[colorchannelmixed],[colorchannelmixed]eq=1.0:0:1.3:2.4:1.0:1.0:1.0:1.0*/
    }

    private fun getLuminanceFilterCommand(): String {
        return "geq=lum=\'max(lum(X,Y) - ((X-W/2)^2+(Y-H/2)^2)/5000., 0)\':cr=\'cr(X,Y)\':cb=\'cb(X,Y)\'"/*[colorchannelmixed],[colorchannelmixed]eq=1.0:0:1.3:2.4:1.0:1.0:1.0:1.0*/
    }


    fun getCommand(): String {
        return command
    }

    private fun getWatermarkCommand(vidOut: String): Triple<String, String, String> {
        var start = if (vidOut.isEmpty()) "[0:v]" else vidOut
        var watermark = ""
        var watermarkPath = ""
        if (!resultInfo?.watermarkList.isNullOrEmpty()) {
            val watermarkData = resultInfo?.watermarkList?.get(0)
            watermarkPath = "-i ${watermarkData?.filePath} "
            watermark =
                "[1]${start}scale2ref=w=iw*${watermarkData?.watermarkSize?.first}:h=ih*${watermarkData?.watermarkSize?.second}[logo1][video1];[logo1]format=rgba,colorchannelmixer=aa=${watermarkData?.opacity}[logo];[video1][logo]overlay=${(watermarkData?.watermarkRect?.left ?: 0)}:${(watermarkData?.watermarkRect?.top ?: 0)}[video2]"
            start = "[video2]"
        }
        val result = Triple(start, watermark, watermarkPath)

        return result
    }

}