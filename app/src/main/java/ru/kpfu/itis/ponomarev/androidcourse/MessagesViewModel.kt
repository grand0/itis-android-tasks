package ru.kpfu.itis.ponomarev.androidcourse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MessagesViewModel : ViewModel() {

    private val _messages: MutableList<String?> = MutableList(LIST_SIZE) {_ -> null}
    private val _messagesLiveData = MutableLiveData<List<String?>>(_messages)
    val messages: LiveData<List<String?>> get() = _messagesLiveData

    fun addMessage(message: String) {
        _messages.removeAt(0)
        _messages.add(message)
        _messagesLiveData.value = _messages
    }

    companion object {
        const val LIST_SIZE = 3
    }
}
