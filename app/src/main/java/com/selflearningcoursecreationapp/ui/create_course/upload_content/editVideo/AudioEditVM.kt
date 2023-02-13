package com.selflearningcoursecreationapp.ui.create_course.upload_content.editVideo

import android.graphics.Rect
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.SessionState
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.extensions.showLog

class AudioEditVM : BaseViewModel() {
    var cropRect: Rect? = null
//    private var _liveData = MutableLiveData<OperationData>().apply {
//        value = OperationData()
//    }

    //    fun observeData(): LiveData<OperationData> = _liveData
    var count = MutableLiveData<Int>().apply {
        value = 0
    }
    var mMarkerLeftInset = 0
    var mStartVisible = false
    var mLoadingLastUpdateTime: Long = 0
    var mLoadingKeepGoing = false
    var sessionId: Long = 0

    var mKeyDown = false

    var mStartPos = 0

    var mIsPlaying = false

    var duration: Long = 0L
    var outputPath: String? = null
    var croppedPath: String? = null
    var mDensity = 0f


    //    var videoData: VideoData? = null
    var selectedUri: Uri? = null
    var selectedPath: String? = null
    var mimeType: String? = null
    var selectedSaf: String? = null
    var durationList = MutableLiveData<ArrayList<String>>().apply {
        value = ArrayList()
    }
    var snapshotList: ArrayList<String> = ArrayList()
    var minDuration: Long = 0L
    var maxDuration: Long = 0
//    var newVideoData: VideoData? = null
//    var filter: GlFilter? = null
//    var adjuster: FilterAdjuster? = null

    var brightness = 50
    var contrast = 50
    private fun executeCommand(command: String, apiCode: String) {
//        _liveData.postValue(OperationData(state = "loading"))
        showLog("VIDEO_COMMAND", " command >>> $command")
        updateResponseObserver(Resource.Loading())
        FFmpegKit.executeAsync(command, { session ->
            Log.d("VIDEO_COMMAND", "$session")
            when (session.state) {
                SessionState.COMPLETED -> {
                    if (session.returnCode.isValueSuccess) {
                        updateResponseObserver(Resource.Success("completed", apiCode))

                    }
//                        _liveData.postValue(
//                            OperationData(
//                                state = "completed",
//                                operationCode = apiCode
//                            )
//                        )
                    else {
                        updateResponseObserver(
                            Resource.Error(
                                ToastData(errorString = "error"),
                                apiCode
                            )
                        )

//                        _liveData.postValue(
//                            OperationData(
//                                state = "error",
//                                operationCode = apiCode,
//                                error = "Unable to complete"
//                            )
//                        )
                    }
                }
                SessionState.FAILED -> {
                    updateResponseObserver(
                        Resource.Error(
                            ToastData(errorString = "error"),
                            apiCode
                        )
                    )

//                    _liveData.postValue(
//                        OperationData(
//                            state = "error",
//                            operationCode = apiCode,
//                            error = "Unable to complete"
//                        )
//                    )

                }
                SessionState.CREATED, SessionState.RUNNING -> {
                    updateResponseObserver(Resource.Loading())

//                    _liveData.postValue(OperationData(state = "loading", operationCode = apiCode))

                }
            }
        }, { log ->

        }, { stats ->
            sessionId = stats.sessionId
        })
    }

    fun enableGPL() {
//        executeCommand("./configure --enable-gpl", "enable")
    }

    fun executeTrimVideo(outputPath: String?) {
        this.outputPath = outputPath
        val complexCommand =
            "-ss ${minDuration / 1000} -y -i $selectedSaf -t ${(maxDuration - minDuration) / 1000} -vcodec mpeg4 -b:v 2097152 -b:a 48000 -ac 2 -ar 22050 $outputPath"
        executeCommand(complexCommand, "trim")
    }

    fun executeTrimAudio() {
        var path = selectedPath?.replace(" ", "%20")

        path = if (selectedPath?.contains(" ") == true) {
            selectedSaf

        } else {
            selectedPath
        }
        val complexCommand =
            if (mimeType?.contains("flac") == true) "-ss ${minDuration / 1000} -y -i $selectedSaf -t ${(maxDuration - minDuration) / 1000} $outputPath"
            else
                "-ss ${minDuration / 1000} -y -i $selectedSaf -t ${(maxDuration - minDuration) / 1000} -acodec copy $outputPath"


        executeCommand(complexCommand, "trimAudio")
    }

    fun executeFilterCommand(filterCmd: String, outputPath: String?) {
        this.outputPath = outputPath
        val path = selectedPath?.replace(" ", "%20")

        val complexCommand =
            "-y -ss ${minDuration / 1000} -i $path -vf '$filterCmd' -t ${(maxDuration - minDuration) / 1000} $outputPath"
        executeCommand(complexCommand, "filter")
    }

    fun executeTakeSnapShotCommand(
        start: Int,
        end: Int,
        outputPath: String?
    ) {
        if (!outputPath.isNullOrEmpty()) {
            val path = if (!croppedPath.isNullOrEmpty()) croppedPath?.replace(
                " ",
                "%20"
            ) else selectedPath?.replace(" ", "%20")

            val complexCommand =
                "-y -i $path -an -r 1/2 -ss ${start / 1000} -t ${(end - start) / 1000} -movflags faststart -strict experimental $outputPath"

            executeCommand(complexCommand, "snapshot")
        }
    }

    override fun onApiRetry(apiCode: String) {

    }


//    fun executeCropVideoCommand(
//        outputPath: String?
//    ) {
//        if (cropRect != null) {
//            this.croppedPath = outputPath
//            val crop = String.format(
//                "crop=%d:%d:%d:%d:exact=0",
//                cropRect!!.right,
//                cropRect!!.bottom,
//                cropRect!!.left,
//                cropRect!!.top
//            )
//            val path = selectedPath?.replace(" ", "%20")
//
//            val complexCommand =
//                "-y -i $path -vf $crop $outputPath"
//            executeCommand(complexCommand, "cropVideo")
//        }
//    }

//    fun executeVideoEditCommand(inputPath: String = "", outputPath: String? = "") {
//
//        val path = if (inputPath.isNotEmpty()) inputPath?.replace(
//            " ",
//            "%20"
//        ) else selectedPath?.replace(" ", "%20")
//        val outPath = if (outputPath.isNullOrEmpty()) this.outputPath else outputPath
//        val crop = if (cropRect != null) {
//            String.format(
//                "crop=%d:%d:%d:%d:exact=0",
//                cropRect!!.right,
//                cropRect!!.bottom,
//                cropRect!!.left,
//                cropRect!!.top
//            )
//        } else {
//            ""
//        }
//        val filterCmd = if (filter != null) {
//
//            "eq=brightness=${(filter as GlMixFilter).brightness}:contrast=${(filter as GlMixFilter).contrast}"
//        } else {
//            ""
//        }
//
//        var filterComplex = ""
//        if (!filterCmd.isNullOrEmpty()) {
//            filterComplex = filterComplex + filterCmd
//        }
//        if (!crop.isNullOrEmpty()) {
//            if (filterComplex.isNotEmpty()) {
//                filterComplex = filterComplex + ", " + crop
//            } else {
//                filterComplex + crop
//            }
//        }
//
//        if (filterComplex.isNotEmpty()) {
//            filterComplex = "-filter_complex '$filterComplex' "
//        }
//
//        val complexCommand =
//            "-y -ss $minDuration -i $path $filterComplex-t ${(maxDuration - minDuration) / 1000} -movflags faststart -strict experimental -vcodec mpeg4 -b:v 2097152 -b:a 48000 -ac 2 -ar 22050 $outPath"
//        executeCommand(complexCommand, "save")
//    }


    override fun onCleared() {
        super.onCleared()
        if (sessionId != 0L) {
            FFmpegKit.cancel(sessionId)
        } else
            FFmpegKit.cancel()
    }
}