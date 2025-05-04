package com.example.sheetcompute.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged

open class BaseViewModel(
    protected val _loading: MutableLiveData<Boolean> = MutableLiveData(false),
    protected val _onError: MutableLiveData<String> = MutableLiveData(),
    open val loading: LiveData<Boolean> = _loading.distinctUntilChanged(),
    val onError: LiveData<String> = _onError,
    protected val savedStateHandle: SavedStateHandle? = null
) : ViewModel() {

    protected fun <T> getSavedStateData(key: String, defaultValue: T): T {
        return savedStateHandle?.get(key) ?: defaultValue
    }

    protected fun <T> setSavedStateData(key: String, value: T) {
        savedStateHandle?.set(key, value)
    }

    fun reset() {
        _onError.postValue("")
    }
}