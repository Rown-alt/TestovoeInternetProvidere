package com.example.testovoeinternetprovidere.di

import com.example.testovoeinternetprovidere.viewModels.RequestViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel {
        RequestViewModel(get())
    }
}