// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import kotlin.reflect.KClass
import skip.lib.*
import skip.lib.Array

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import kotlin.reflect.full.companionObjectInstance

interface PreferenceKey<Value> {
}

/// Added to `PreferenceKey` companion objects.
interface PreferenceKeyCompanion<Value> {
    val defaultValue: Value
    fun reduce(value: InOut<Value>, nextValue: () -> Value)
}

/// Internal analog to `EnvironmentValues` for preferences.
///
/// Uses environment `CompositionLocals` internally.
///
/// - Seealso: `EnvironmentValues`
internal class PreferenceValues {

    /// Return a preference collector for the given `PreferenceKey` type.
    @Composable internal fun collector(key: KClass<*>): PreferenceCollector<Any>? {
        return EnvironmentValues.shared.compositionLocals[key]?.current as? PreferenceCollector<Any>
    }

    /// Collect the values of the given preferences while composing the given content.
    @Composable internal fun collectPreferences(collectors: Array<PreferenceCollector<*>>, in_: @Composable () -> Unit) {
        val content = in_
        val provided = collectors.map { collector ->
            var compositionLocal = EnvironmentValues.shared.compositionLocals[collector.key].sref()
            if (compositionLocal == null) {
                compositionLocal = compositionLocalOf { -> Unit }
                EnvironmentValues.shared.compositionLocals[collector.key] = compositionLocal.sref()
            }
            val element = compositionLocal!! provides collector
            element
        }
        val kprovided = (provided.kotlin(nocopy = true) as MutableList<ProvidedValue<*>>).toTypedArray()
        CompositionLocalProvider(*kprovided) { -> content() }
    }

    /// Update the value of the given preference, as if by calling .preference(key:value:).
    @Composable
    internal fun contribute(context: ComposeContext, key: KClass<*>, value: Any) {
        // Use a saveable value because they preferences themselves and their node IDs are saved
        val id = rememberSaveable(stateSaver = context.stateSaver as Saver<Int?, Any>) { -> mutableStateOf<Int?>(null) }
        val collector = rememberUpdatedState(PreferenceValues.shared.collector(key = key))
        collector.value.sref()?.let { collector ->
            // A side effect is required to ensure that a state change during composition causes a recomposition
            SideEffect { -> id.value = collector.contribute(value, id = id.value) }
        }
        DisposableEffect(true) { ->
            onDispose { ->
                collector.value?.erase(id = id.value)
            }
        }
    }

    companion object {
        internal val shared = PreferenceValues()
    }
}

/// Used internally by our preferences system to collect preferences and recompose on change.
internal class PreferenceCollector<Value> {
    internal val key: KClass<*>
    internal val state: MutableState<Preference<Value>>
    internal val isErasable: Boolean

    internal constructor(key: KClass<*>, state: MutableState<Preference<Value>>, isErasable: Boolean = true) {
        this.key = key
        this.state = state.sref()
        this.isErasable = isErasable
    }

    /// Contribute a value to the collected preference.
    ///
    /// - Parameter id: The id of this value in the value chain, or nil if no id has been assigned.
    /// - Returns: The id to use for future contributions.
    internal fun contribute(value: Value, id: Int?): Int {
        var preference = state.value.sref()
        if (id == null) {
            val maxID = preference.nodes.reduce(initialResult = -1) l@{ result, node -> return@l max(result, node.id) }
            val nextID = maxID + 1
            preference.nodes.append(PreferenceNode(id = nextID, value = value))
            state.value = preference
            return nextID
        }
        val index_0 = preference.nodes.firstIndex(where = { it -> it.id == id })
        if (index_0 == null) {
            val maxID = preference.nodes.reduce(initialResult = -1) l@{ result, node -> return@l max(result, node.id) }
            val nextID = maxID + 1
            preference.nodes.append(PreferenceNode(id = nextID, value = value))
            state.value = preference
            return nextID
        }
        preference.nodes[index_0] = PreferenceNode(id = id, value = value)
        state.value = preference
        return id
    }

    /// Remove the contribution by the given id.
    internal fun erase(id: Int?) {
        if (!isErasable) {
            return
        }
        var preference = state.value.sref()
        if (id != null) {
            preference.nodes.firstIndex(where = { it -> it.id == id })?.let { index ->
                preference.nodes.remove(at = index)
                state.value = preference
            }
        }
    }
}

/// The collected preference values that are reduced to achieve the final value.
@Stable
internal class Preference<Value>: MutableStruct {
    internal var nodes: Array<PreferenceNode<Value>> = arrayOf()
        get() = field.sref({ this.nodes = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }

    internal constructor(key: KClass<*>, initialValue: Value? = null) {
        this.key = key
        this.initialValue = (initialValue ?: (key.companionObjectInstance as PreferenceKeyCompanion<Value>).defaultValue).sref()
    }

    internal val key: KClass<*>
    internal val initialValue: Value

    /// The reduced preference value.
    internal val reduced: Value
        get() {
            var value = initialValue.sref()
            for (node in nodes.sref()) {
                (key.companionObjectInstance as PreferenceKeyCompanion<Value>).reduce(value = InOut({ value }, { value = it }), nextValue = { -> node.value as Value })
            }
            return value
        }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as Preference<Value>
        this.nodes = copy.nodes
        this.key = copy.key
        this.initialValue = copy.initialValue
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = Preference<Value>(this as MutableStruct)

    override fun equals(other: Any?): Boolean {
        if (other !is Preference<*>) return false
        return nodes == other.nodes && key == other.key && initialValue == other.initialValue
    }
}

internal class PreferenceNode<Value> {
    internal val id: Int
    internal val value: Value

    constructor(id: Int, value: Value) {
        this.id = id
        this.value = value.sref()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is PreferenceNode<*>) return false
        return id == other.id && value == other.value
    }
}

