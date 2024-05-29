// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array
import skip.lib.Set

import skip.foundation.*

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE

open class UIPasteboard {

    private constructor() {
        val context = ProcessInfo.processInfo.androidContext.sref()
        (context.getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager).sref()?.let { clipboardManager ->
            clipboardManager.addPrimaryClipChangedListener(Listener())
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(name: UIPasteboard.Name, create: Boolean) {
    }

    open val name: UIPasteboard.Name
        get() = UIPasteboard.Name.general

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val isPersistent: Boolean
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun setPersistent(persistent: Boolean) = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val changeCount: Int
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val itemProviders: Array<Any>
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun setItemProviders(itemProviders: Array<Any>, localOnly: Boolean, expirationDate: Date?) = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun setObjects(objects: Array<Any>) = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun setObjects(objects: Array<Any>, localOnly: Boolean, expirationDate: Date?) = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val types: Array<String>
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun contains(pasteboardTypes: Array<String>): Boolean {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun data(forPasteboardType: String): Data? {
        val pasteboardType = forPasteboardType
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun value(forPasteboardType: String): Any? {
        val pasteboardType = forPasteboardType
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun setValue(value: Any, forPasteboardType: String) = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun setData(data: Data, forPasteboardType: String) = Unit

    open val numberOfItems: Int
        get() = if (string != null) 1 else 0

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun types(forItemSet: IntSet?): Array<Array<String>> {
        val itemSet = forItemSet
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun contains(pasteboardTypes: Array<String>, inItemSet: IntSet?): Boolean {
        val itemSet = inItemSet
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun itemSet(withPasteboardTypes: Array<String>): IntSet? {
        val pasteboardTypes = withPasteboardTypes
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun values(forPasteboardType: String, inItemSet: IntSet?): Array<Any>? {
        val pasteboardType = forPasteboardType
        val itemSet = inItemSet
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun data(forPasteboardType: String, inItemSet: IntSet?): Array<Data>? {
        val pasteboardType = forPasteboardType
        val itemSet = inItemSet
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val items: Array<Dictionary<String, Any>>
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun addItems(items: Array<Dictionary<String, Any>>) = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun setItems(items: Array<Dictionary<String, Any>>, options: Dictionary<UIPasteboard.OptionsKey, Any> = dictionaryOf()) {
        fatalError()
    }

    open var string: String?
        get() {
            val context = ProcessInfo.processInfo.androidContext.sref()
            val clipboardManager_0 = (context.getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager).sref()
            if (clipboardManager_0 == null) {
                return null
            }
            if (!clipboardManager_0.hasPrimaryClip()) {
                return null
            }
            val clipData_0 = clipboardManager_0.getPrimaryClip()
            if ((clipData_0 == null) || (clipData_0.getItemCount() <= 0)) {
                return null
            }
            val string = String(clipData_0.getItemAt(0).coerceToText(context))
            return if (string.isEmpty) null else string
        }
        set(newValue) {
            if (newValue != null) {
                strings = arrayOf(newValue)
            } else {
                strings = null
            }
        }

    open var strings: Array<String>?
        get() {
            val context = ProcessInfo.processInfo.androidContext.sref()
            val clipboardManager_1 = (context.getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager).sref()
            if (clipboardManager_1 == null) {
                return null
            }
            if (!clipboardManager_1.hasPrimaryClip()) {
                return null
            }
            val clipData_1 = clipboardManager_1.getPrimaryClip()
            if (clipData_1 == null) {
                return null
            }
            val count = clipData_1.getItemCount()
            var strings: Array<String> = arrayOf()
            for (i in 0 until count) {
                val string = String(clipData_1.getItemAt(i).coerceToText(context))
                if (!string.isEmpty) {
                    strings.append(string)
                }
            }
            return (if (strings.isEmpty) null else strings).sref({ this.strings = it })
        }
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            val context = ProcessInfo.processInfo.androidContext.sref()
            val clipboardManager_2 = (context.getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager).sref()
            if (clipboardManager_2 == null) {
                return
            }
            if ((newValue == null) || newValue.isEmpty) {
                clipboardManager_2.clearPrimaryClip()
                return
            }
            val clipData = ClipData.newPlainText("", newValue[0])
            for (i in 1 until newValue.count) {
                clipData.addItem(ClipData.Item(newValue[i]))
            }
            clipboardManager_2.setPrimaryClip(clipData)
        }

    open var url: URL?
        get() {
            val context = ProcessInfo.processInfo.androidContext.sref()
            val clipboardManager_3 = (context.getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager).sref()
            if (clipboardManager_3 == null) {
                return null
            }
            if (!clipboardManager_3.hasPrimaryClip()) {
                return null
            }
            val clipData_2 = clipboardManager_3.getPrimaryClip()
            if ((clipData_2 == null) || (clipData_2.getItemCount() <= 0)) {
                return null
            }
            // We attempt to get each item as a URI first to avoid coerceToText potentially resolving the URI
            // content into a string
            clipData_2.getItemAt(0).getUri()?.let { androidURI ->
                (try { URL(string = androidURI.toString()) } catch (_: NullReturnException) { null })?.let { url ->
                    return url.sref({ this.url = it })
                }
            }
            val string = String(clipData_2.getItemAt(0).coerceToText(context))
            return (try { URL(string = string) } catch (_: NullReturnException) { null }).sref({ this.url = it })
        }
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            if (newValue != null) {
                urls = arrayOf(newValue)
            } else {
                urls = null
            }
        }

    open var urls: Array<URL>?
        get() {
            val context = ProcessInfo.processInfo.androidContext.sref()
            val clipboardManager_4 = (context.getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager).sref()
            if (clipboardManager_4 == null) {
                return null
            }
            if (!clipboardManager_4.hasPrimaryClip()) {
                return null
            }
            val clipData_3 = clipboardManager_4.getPrimaryClip()
            if (clipData_3 == null) {
                return null
            }
            val count = clipData_3.getItemCount()
            var urls: Array<URL> = arrayOf()
            for (i in 0 until count) {
                // We attempt to get each item as a URI first to avoid coerceToText potentially resolving the URI
                // content into a string
                val matchtarget_0 = clipData_3.getItemAt(i).getUri()
                if (matchtarget_0 != null) {
                    val androidURI = matchtarget_0
                    val matchtarget_1 = (try { URL(string = androidURI.toString()) } catch (_: NullReturnException) { null })
                    if (matchtarget_1 != null) {
                        val url = matchtarget_1
                        urls.append(url)
                    } else {
                        String(clipData_3.getItemAt(i).coerceToText(context))?.let { string ->
                            (try { URL(string = string) } catch (_: NullReturnException) { null })?.let { url ->
                                urls.append(url)
                            }
                        }
                    }
                } else {
                    String(clipData_3.getItemAt(i).coerceToText(context))?.let { string ->
                        (try { URL(string = string) } catch (_: NullReturnException) { null })?.let { url ->
                            urls.append(url)
                        }
                    }
                }
            }
            return (if (urls.isEmpty) null else urls).sref({ this.urls = it })
        }
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            val context = ProcessInfo.processInfo.androidContext.sref()
            val clipboardManager_5 = (context.getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager).sref()
            if (clipboardManager_5 == null) {
                return
            }
            if ((newValue == null) || newValue.isEmpty) {
                clipboardManager_5.clearPrimaryClip()
                return
            }
            val clipData = ClipData.newRawUri("", android.net.Uri.parse(newValue[0].absoluteString))
            for (i in 1 until newValue.count) {
                clipData.addItem(ClipData.Item(android.net.Uri.parse(newValue[i].absoluteString)))
            }
            clipboardManager_5.setPrimaryClip(clipData)
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var image: Any?
        get() {
            fatalError()
        }
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var images: Array<Any>?
        get() {
            fatalError()
        }
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var color: Any?
        get() {
            fatalError()
        }
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var colors: Array<Any>?
        get() {
            fatalError()
        }
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
        }

    open val hasStrings: Boolean
        get() = string != null

    open val hasURLs: Boolean
        get() = url != null

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val hasImages: Boolean
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val hasColors: Boolean
        get() {
            fatalError()
        }

    class DetectedValues {
        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val patterns: Set<AnyHashable>
            get() {
                fatalError()
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val probableWebURL: String
            get() {
                fatalError()
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val probableWebSearch: String
            get() {
                fatalError()
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val number: Double?
            get() {
                fatalError()
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val links: Array<Any>
            get() {
                fatalError()
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val phoneNumbers: Array<Any>
            get() {
                fatalError()
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val emailAddresses: Array<Any>
            get() {
                fatalError()
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val postalAddresses: Array<Any>
            get() {
                fatalError()
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val calendarEvents: Array<Any>
            get() {
                fatalError()
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val shipmentTrackingNumbers: Array<Any>
            get() {
                fatalError()
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val flightNumbers: Array<Any>
            get() {
                fatalError()
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val moneyAmounts: Array<Any>
            get() {
                fatalError()
            }

        companion object {
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun detectPatterns(for_: Set<AnyHashable>, completionHandler: (Result<Set<AnyHashable>, Error>) -> Unit) {
        val keyPaths = for_
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open suspend fun detectedPatterns(for_: Set<AnyHashable>): Set<AnyHashable> = Async.run {
        val keyPaths = for_
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun detectPatterns(for_: Set<AnyHashable>, inItemSet: IntSet?, completionHandler: (Result<Array<Set<AnyHashable>>, Error>) -> Unit) {
        val keyPaths = for_
        val itemSet = inItemSet
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open suspend fun detectedPatterns(for_: Set<AnyHashable>, inItemSet: IntSet?): Array<Set<AnyHashable>> = Async.run {
        val keyPaths = for_
        val itemSet = inItemSet
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun detectValues(for_: Set<AnyHashable>, completionHandler: (Result<UIPasteboard.DetectedValues, Error>) -> Unit) {
        val keyPaths = for_
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open suspend fun detectedValues(for_: Set<AnyHashable>): UIPasteboard.DetectedValues = Async.run {
        val keyPaths = for_
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun detectValues(for_: Set<AnyHashable>, inItemSet: IntSet?, completionHandler: (Result<Array<UIPasteboard.DetectedValues>, Error>) -> Unit) {
        val keyPaths = for_
        val itemSet = inItemSet
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open suspend fun detectedValues(for_: Set<AnyHashable>, inItemSet: IntSet?): Array<UIPasteboard.DetectedValues> = Async.run {
        val keyPaths = for_
        val itemSet = inItemSet
        fatalError()
    }

    /*
    @available(*, unavailable)
    public func setObjects<T>(_ objects: [T]) where T : _ObjectiveCBridgeable, T._ObjectiveCType : NSItemProviderWriting {
    fatalError()
    }

    @available(*, unavailable)
    public func setObjects<T>(_ objects: [T], localOnly: Bool, expirationDate: Date?) where T : _ObjectiveCBridgeable, T._ObjectiveCType : NSItemProviderWriting {
    fatalError()
    }
    */

    class OptionsKey: RawRepresentable<String>, Sendable {

        override val rawValue: String

        constructor(rawValue: String) {
            this.rawValue = rawValue
        }

        override fun equals(other: Any?): Boolean {
            if (other !is UIPasteboard.OptionsKey) return false
            return rawValue == other.rawValue
        }

        override fun hashCode(): Int {
            var result = 1
            result = Hasher.combine(result, rawValue)
            return result
        }

        companion object {
            @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
            val expirationDate: UIPasteboard.OptionsKey
                get() {
                    fatalError()
                }

            @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
            val localOnly: UIPasteboard.OptionsKey
                get() {
                    fatalError()
                }
        }
    }

    private class Listener: ClipboardManager.OnPrimaryClipChangedListener {
        override fun onPrimaryClipChanged(): Unit = NotificationCenter.default.post(name = UIPasteboard.changedNotification, object_ = UIPasteboard.general)
    }

    class Name: RawRepresentable<String>, Sendable {

        override val rawValue: String

        constructor(rawValue: String) {
            this.rawValue = rawValue
        }

        constructor(rawValue: String, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null) {
            this.rawValue = rawValue
        }

        override fun equals(other: Any?): Boolean {
            if (other !is UIPasteboard.Name) return false
            return rawValue == other.rawValue
        }

        override fun hashCode(): Int {
            var result = 1
            result = Hasher.combine(result, rawValue)
            return result
        }

        companion object {
            val general = UIPasteboard.Name(rawValue = "general")
        }
    }

    class DetectionPattern: RawRepresentable<String>, Sendable {
        override val rawValue: String

        constructor(rawValue: String) {
            this.rawValue = rawValue
        }

        override fun equals(other: Any?): Boolean {
            if (other !is UIPasteboard.DetectionPattern) return false
            return rawValue == other.rawValue
        }

        override fun hashCode(): Int {
            var result = 1
            result = Hasher.combine(result, rawValue)
            return result
        }

        companion object {
        }
    }

    companion object: CompanionClass() {
        override val general = UIPasteboard()

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        fun withUniqueName(): UIPasteboard {
            fatalError()
        }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        fun remove(withName: UIPasteboard.Name) = Unit

        override var changedNotification = Notification.Name(rawValue = "UIPasteboardChanged")

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val changedTypesAddedUserInfoKey: String
            get() {
                fatalError()
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val changedTypesRemovedUserInfoKey: String
            get() {
                fatalError()
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val removedNotification: Notification.Name
            get() {
                fatalError()
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val typeListString: Array<String>
            get() {
                fatalError()
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val typeListURL: Array<String>
            get() {
                fatalError()
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val typeListImage: Array<String>
            get() {
                fatalError()
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val typeListColor: Array<String>
            get() {
                fatalError()
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val typeAutomatic: String
            get() {
                fatalError()
            }
    }
    open class CompanionClass {
        open val general
            get() = UIPasteboard.general
        open var changedNotification
            get() = UIPasteboard.changedNotification
            set(newValue) {
                UIPasteboard.changedNotification = newValue
            }
    }
}
