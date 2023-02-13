package com.hd.videoEditor.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.SessionState
import com.hd.videoEditor.ffmpeg.FFmpegStateConst
import com.hd.videoEditor.ffmpeg.FFmpegStateData
import com.hd.videoEditor.ffmpeg.FFmpegUtils
import com.hd.videoEditor.model.EditedVideoData
import com.hd.videoEditor.utils.Logd

class HdEditorVM : ViewModel() {

    var inputPath: String? = null
    var originalPath: String? = null
    var outputPath: String? = null
    val TAG = "HD_EDITOR_VM"
    var editType: Int = 0
    var sessionId: Long = 0
    private var _resultLiveData: MutableLiveData<FFmpegStateData> =
        MutableLiveData<FFmpegStateData>().apply {
            value = FFmpegStateData()
        }
    var resultLiveData: LiveData<FFmpegStateData> = _resultLiveData


    fun saveVideo(resultedVideo: EditedVideoData) {

        val cmd = FFmpegUtils.apiCode("save_video").inputPath(inputPath).outputPath(outputPath)
            .resultedInfo(resultedVideo).getCommand()
        executeCommand(cmd, "save_video")
    }

    private fun executeCommand(command: String, apiCode: String) {
        _resultLiveData.postValue(
            FFmpegStateData(
                state = FFmpegStateConst.STATE_LOADING,
                operationCode = apiCode
            )
        )
        Logd(TAG, " command >>> ${command}")

        FFmpegKit.executeAsync(command,
            { session ->
                when (session?.state) {
                    SessionState.COMPLETED -> {
                        if (session.returnCode.isValueSuccess) {
                            _resultLiveData.postValue(
                                FFmpegStateData(
                                    state = FFmpegStateConst.STATE_COMPLETED,
                                    operationCode = apiCode
                                )
                            )
                        } else {
                            _resultLiveData.postValue(
                                FFmpegStateData(
                                    state = FFmpegStateConst.STATE_FAILED,
                                    operationCode = apiCode
                                )
                            )

                        }
                    }
                    SessionState.FAILED -> {
                        _resultLiveData.postValue(
                            FFmpegStateData(
                                state = FFmpegStateConst.STATE_FAILED,
                                operationCode = apiCode,
                                error = "Unable to complete"
                            )
                        )

                    }
                    SessionState.CREATED, SessionState.RUNNING -> {

                        _resultLiveData.postValue(
                            FFmpegStateData(
                                state = FFmpegStateConst.STATE_LOADING,
                                operationCode = apiCode
                            )
                        )

                    }
                }
            }, { log ->
                Logd("FFMPEG_CMD", "log message >> ${log?.message}")
            }) { statistics ->
            sessionId = statistics?.sessionId ?: 0
            _resultLiveData.postValue(
                FFmpegStateData(
                    state = FFmpegStateConst.STATE_RUNNING,
                    operationCode = apiCode,
                    time = statistics?.time
                )
            )
            Logd("FFMPEG_CMD", "statistics time >> ${statistics?.time}")
        }

/*
        FFmpegKit.executeAsync(command) { session ->
            Logd("FFMPEG_CMD","session duration >> ${session.duration}")
            Logd("FFMPEG_CMD","session allStatistics >> ${session.allStatistics}")
            Logd("FFMPEG_CMD","session lastReceivedStatistics >> ${session.lastReceivedStatistics}")
            Logd("FFMPEG_CMD","session allLogs >> ${session.allLogs}")
            Logd("FFMPEG_CMD","session endTime >> ${session.endTime}")
            Logd("FFMPEG_CMD","session statistics >> ${session.statistics}")
            Logd("FFMPEG_CMD","session command >> ${session.command}")
            Logd(TAG, "$session")
            when (session.state) {
                SessionState.COMPLETED -> {
                    if (session.returnCode.isValueSuccess) {
                        _resultLiveData.postValue(
                            FFmpegStateData(
                                state = FFmpegStateConst.STATE_COMPLETED,
                                operationCode = apiCode
                            )
                        )
                    } else {
                        _resultLiveData.postValue(
                            FFmpegStateData(
                                state = FFmpegStateConst.STATE_FAILED,
                                operationCode = apiCode
                            )
                        )

                    }
                }
                SessionState.FAILED -> {
                    _resultLiveData.postValue(
                        FFmpegStateData(
                            state = FFmpegStateConst.STATE_FAILED,
                            operationCode = apiCode,
                            error = "Unable to complete"
                        )
                    )

                }
                SessionState.CREATED, SessionState.RUNNING -> {

                    _resultLiveData.postValue(
                        FFmpegStateData(
                            state = FFmpegStateConst.STATE_LOADING,
                            operationCode = apiCode
                        )
                    )

                }
            }
        }
*/
    }

    override fun onCleared() {
        super.onCleared()
        if (sessionId != 0L) {
            FFmpegKit.cancel(sessionId)
        } else
            FFmpegKit.cancel()
    }

}