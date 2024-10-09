package com.zen.accounts.presentation.utility

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

fun main(
    content : suspend () -> Unit
) {
    CoroutineScope(Dispatchers.Main).launch {
        content.invoke()
    }
}

fun launch(
    content : suspend () -> Unit
) {
    CoroutineScope(Dispatchers.Default).launch {
        content.invoke()
    }
}

fun io(
    content : suspend () -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        content.invoke()
    }
}

fun<T> async(
    content : suspend () -> T
) : Deferred<T> {
    return CoroutineScope(Dispatchers.IO).async {
        content.invoke()
    }
}