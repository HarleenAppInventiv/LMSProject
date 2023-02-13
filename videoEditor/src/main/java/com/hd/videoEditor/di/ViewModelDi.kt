package com.hd.videoEditor.di

import com.hd.videoEditor.ui.HdEditorVM
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        HdEditorVM()
    }
}