package com.amirhosseinfsh.core.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext


val scopeMain: CoroutineScope = CoroutineScope(Dispatchers.Main)
val scopeCpu: CoroutineScope = CoroutineScope(Dispatchers.Default)
val scopeIO: CoroutineScope = CoroutineScope(Dispatchers.IO)
val dispatcherIO: CoroutineContext = Dispatchers.IO
val dispatcherMain: CoroutineContext = Dispatchers.Main

suspend fun <T> Flow<T>.onCollect(scope: CoroutineScope = scopeMain, action: suspend (T) -> Unit) {
    this.collectLatest {
        execute(scope) {
            try {
                action.invoke(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

fun execute(scope: CoroutineScope = scopeMain, block: suspend CoroutineScope.() -> Unit): Job {
    return scope.launch {
        block.invoke(this)
        this.coroutineContext.job.cancelAndJoin()
    }
}

fun LifecycleOwner.executeLife(
    life: Lifecycle.State = Lifecycle.State.CREATED,
    scope: CoroutineContext = dispatcherIO,
    block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch(scope) {
        repeatOnLifecycle(life) {
            block.invoke(this)
        }
    }
}

fun Fragment.executeLife(
    life: Lifecycle.State = Lifecycle.State.CREATED,
    scope: CoroutineContext = dispatcherIO,
    block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch(scope) {
        viewLifecycleOwner.repeatOnLifecycle(life) {
            block.invoke(this@launch)
        }
    }
}

object EventBus {
    private val _events = MutableSharedFlow<Any>()
    val events = _events.asSharedFlow()

    @Synchronized
    fun publish(event: Any) {
        execute(scopeIO) {
            _events.emit(event)
        }
    }

    @Synchronized
    fun publishByKey(key: String,data: String) {
        execute {
            _events.emit(key to data)
        }
    }


    suspend inline fun <reified T> subscribe(
        crossinline filteredResult: ((T) -> Boolean) = { true },
        crossinline onEvent: (T) -> Unit
    ) {
        try {
            events.filterIsInstance<T>()
                .filter { filteredResult.invoke(it) }
                .onCollect { event ->
                    coroutineContext.ensureActive()
                    onEvent(event)
                }
        } catch (e: Exception) {

        }

    }

    suspend inline fun <reified T> subscribeOnKey(key: String,crossinline onEvent: (T) -> Unit) {
        try {
            events.filterIsInstance<Pair<String,T>>()
                .onCollect { event ->
                    coroutineContext.ensureActive()
                    onEvent(event.second)
                }
        }catch (e:Exception){

        }

    }
}