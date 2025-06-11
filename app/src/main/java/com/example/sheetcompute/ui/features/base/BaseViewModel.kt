package com.example.sheetcompute.ui.features.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged

open class BaseViewModel(
    protected val _loading: MutableLiveData<Boolean> = MutableLiveData(false),
    protected val _onError: MutableLiveData<String> = MutableLiveData(),
    open val loading: LiveData<Boolean> = _loading.distinctUntilChanged(),
    val onError: LiveData<String> = _onError,
) : ViewModel() {





    fun reset() {
        _onError.postValue("")
    }
}