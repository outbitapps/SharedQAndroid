// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array

import skip.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.sharp.*
import androidx.compose.material.icons.twotone.*
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.vector.VectorPath
import androidx.compose.ui.graphics.vector.toPath
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest


private val logger: SkipLogger = SkipLogger(subsystem = "SkipUI", category = "Image") // adb logcat '*:S' 'SkipUI.Image:V'

class Image: View, MutableStruct {
    internal val image: Image.ImageType
    internal var capInsets = EdgeInsets()
        get() = field.sref({ this.capInsets = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }
    internal var resizingMode: Image.ResizingMode? = null
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    internal val scale: Double

    internal sealed class ImageType {
        class NamedCase(val associated0: String, val associated1: Bundle?, val associated2: Text?): ImageType() {
            val name = associated0
            val bundle = associated1
            val label = associated2

            override fun equals(other: Any?): Boolean {
                if (other !is NamedCase) return false
                return associated0 == other.associated0 && associated1 == other.associated1 && associated2 == other.associated2
            }
        }
        class DecorativeCase(val associated0: String, val associated1: Bundle?): ImageType() {
            val name = associated0
            val bundle = associated1

            override fun equals(other: Any?): Boolean {
                if (other !is DecorativeCase) return false
                return associated0 == other.associated0 && associated1 == other.associated1
            }
        }
        class SystemCase(val associated0: String): ImageType() {
            val systemName = associated0

            override fun equals(other: Any?): Boolean {
                if (other !is SystemCase) return false
                return associated0 == other.associated0
            }
        }
        class PainterCase(val associated0: Painter, val associated1: Double): ImageType() {
            val painter = associated0
            val scale = associated1

            override fun equals(other: Any?): Boolean {
                if (other !is PainterCase) return false
                return associated0 == other.associated0 && associated1 == other.associated1
            }
        }

        companion object {
            fun named(name: String, bundle: Bundle?, label: Text?): ImageType = NamedCase(name, bundle, label)
            fun decorative(name: String, bundle: Bundle?): ImageType = DecorativeCase(name, bundle)
            fun system(systemName: String): ImageType = SystemCase(systemName)
            fun painter(painter: Painter, scale: Double): ImageType = PainterCase(painter, scale)
        }
    }

    constructor(name: String, bundle: Bundle? = Bundle.main, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null) {
        this.scale = 1.0
        this.image = Image.ImageType.named(name = name, bundle = bundle, label = null)
    }

    constructor(name: String, bundle: Bundle? = Bundle.main, label: Text) {
        this.scale = 1.0
        this.image = Image.ImageType.named(name = name, bundle = bundle, label = label)
    }

    constructor(decorative: String, bundle: Bundle? = Bundle.main) {
        val name = decorative
        this.scale = 1.0
        this.image = Image.ImageType.decorative(name = name, bundle = bundle)
    }

    constructor(systemName: String, unusedp0: Nothing? = null, unusedp1: Nothing? = null) {
        this.scale = 1.0
        this.image = Image.ImageType.system(systemName = systemName)
    }

    constructor(painter: Painter, scale: Double) {
        this.scale = 1.0
        this.image = Image.ImageType.painter(painter = painter, scale = scale)
    }

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        val aspect = EnvironmentValues.shared._aspectRatio
        val colorScheme = EnvironmentValues.shared.colorScheme

        // Put given modifiers on the containing Box so that the image can scale itself without affecting them
        Box(modifier = context.modifier, contentAlignment = androidx.compose.ui.Alignment.Center) { ->
            val matchtarget_0 = image
            when (matchtarget_0) {
                is Image.ImageType.PainterCase -> {
                    val painter = matchtarget_0.associated0
                    val scale = matchtarget_0.associated1
                    ComposePainter(painter = painter, scale = scale, aspectRatio = aspect?.element0, contentMode = aspect?.element1)
                }
                is Image.ImageType.SystemCase -> {
                    val systemName = matchtarget_0.associated0
                    ComposeSystem(systemName = systemName, aspectRatio = aspect?.element0, contentMode = aspect?.element1, context = context)
                }
                is Image.ImageType.NamedCase -> {
                    val name = matchtarget_0.associated0
                    val bundle = matchtarget_0.associated1
                    val label = matchtarget_0.associated2
                    ComposeNamedImage(name = name, bundle = bundle, label = label, aspectRatio = aspect?.element0, contentMode = aspect?.element1, colorScheme = colorScheme, context = context)
                }
                is Image.ImageType.DecorativeCase -> {
                    val name = matchtarget_0.associated0
                    val bundle = matchtarget_0.associated1
                    ComposeNamedImage(name = name, bundle = bundle, label = null, aspectRatio = aspect?.element0, contentMode = aspect?.element1, colorScheme = colorScheme, context = context)
                }
            }
        }
    }

    @Composable
    private fun ComposeNamedImage(name: String, bundle: Bundle?, label: Text?, aspectRatio: Double?, contentMode: ContentMode?, colorScheme: ColorScheme, context: ComposeContext) {
        val matchtarget_1 = rememberCached(contentsCache, NameBundleColorScheme(name, bundle, colorScheme)) { _ -> imageResourceURL(name = name, colorScheme = colorScheme, bundle = bundle ?: Bundle.main) }
        if (matchtarget_1 != null) {
            val imageResourceURL = matchtarget_1
            ComposeAssetImage(url = imageResourceURL, label = label, context = context)
        } else {
            rememberCached(contentsCache, NameBundleColorScheme(name, bundle, null)) { _ -> symbolResourceURL(name = name, bundle = bundle ?: Bundle.main) }?.let { symbolResourceURL ->
                ComposeSymbolImage(name = name, url = symbolResourceURL, label = label, aspectRatio = aspectRatio, contentMode = contentMode, context = context)
            }
        }
    }

    @Composable
    private fun ComposeAssetImage(url: URL, label: Text?, context: ComposeContext) {
        val model = ImageRequest.Builder(LocalContext.current)
            .fetcherFactory(JarURLFetcher.Factory())
            .data(url)
            .size(coil.size.Size.ORIGINAL)
            .memoryCacheKey(url.description)
            .diskCacheKey(url.description)
            .build()
        SubcomposeAsyncImage(model = model, contentDescription = null, loading = { _ ->  }, success = { state ->
            val aspect = EnvironmentValues.shared._aspectRatio
            ComposePainter(painter = this.painter, scale = scale, aspectRatio = aspect?.element0, contentMode = aspect?.element1)
        }, error = { state ->  })
    }

    @Composable
    private fun ComposeSymbolImage(name: String, url: URL, label: Text?, aspectRatio: Double?, contentMode: ContentMode?, context: ComposeContext) {

        fun symbolToImageVector(symbol: SymbolInfo, tintColor: androidx.compose.ui.graphics.Color): ImageVector {
            // this is the default size for material icons (24f), defined in the internal MaterialIconDimension variable with the comment "All Material icons (currently) are 24dp by 24dp, with a viewport size of 24 by 24" at:
            // https://github.com/androidx/androidx/blob/androidx-main/compose/material/material-icons-core/src/commonMain/kotlin/androidx/compose/material/icons/Icons.kt#L257
            //let size = androidx.compose.ui.geometry.Size(Float(24), Float(24))

            // manually create the bounding rect for all the symbols so we know how to size the viewport and offset the group
            // note that this does not take into account symbols that are designed to be smaller than their bounds, and ignores any baseline accommodation
            var symbolBounds = (symbol.paths.first?.pathParser?.toPath()?.getBounds() ?: Rect.Zero).sref()
            for (symbolPath in symbol.paths.dropFirst()) {
                val bounds = symbolPath.pathParser.toPath().getBounds()
                symbolBounds = Rect(minOf(symbolBounds.left, bounds.left), minOf(symbolBounds.top, bounds.top), maxOf(symbolBounds.right, bounds.right), maxOf(symbolBounds.bottom, bounds.bottom))
            }

            val symbolWidth = (symbolBounds.right - symbolBounds.left).sref()
            val symbolHeight = (symbolBounds.bottom - symbolBounds.top).sref()
            val symbolSpan = maxOf(symbolWidth, symbolHeight)

            // the offsets are adjusted to center the symbol in the viewport
            val symbolOffsetX = -symbolBounds.left + (if (symbolHeight > symbolWidth) ((symbolHeight - symbolWidth) / 2.0f) else 0.0f)
            val symbolOffsetY = -symbolBounds.top + (if (symbolWidth > symbolHeight) ((symbolWidth - symbolHeight) / 2.0f) else 0.0f)

            //logger.debug("created union path symbolSpan=\(symbolSpan) bounds=\(symbolBounds)")

            val imageVector = ImageVector.Builder(name = name, defaultWidth = symbolSpan.dp, defaultHeight = symbolSpan.dp, viewportWidth = symbolSpan, viewportHeight = symbolSpan, autoMirror = true).apply { ->
                group(translationX = symbolOffsetX, translationY = symbolOffsetY) { ->
                    path(fill = SolidColor(tintColor), fillAlpha = 1.0f, stroke = SolidColor(tintColor), strokeAlpha = 1.0f, strokeLineWidth = 1.0f, strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Bevel, strokeLineMiter = 1.0f, pathFillType = PathFillType.NonZero, pathBuilder = { ->
                        for (symbolPath in symbol.paths.sref()) {
                            val pathParser = symbolPath.pathParser.sref()
                            val bounds = pathParser.toPath().getBounds()
                            val pathData = pathParser.toNodes()
                            //logger.debug("parsed path bounds=\(bounds) nodes=\(pathData)")
                            addPath(pathData, fill = SolidColor(tintColor), stroke = SolidColor(tintColor))
                        }
                    })
                }
            }.build()

            return imageVector.sref()
        }

        // parse the Symbol Export XML and extract the SVG path representation that most closely matches the current font weight (e.g., "Black-S", "Regular-S", "Ultralight-S")
        fun parseSymbolXML(url: URL): Dictionary<SymbolSize, SymbolInfo> {
            logger.debug("parsing symbol SVG at: ${url}")
            val factory = javax.xml.parsers.DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val document = builder.parse(url.kotlin().toURL().openStream())

            // filter a NodeList into an array of Elements
            fun elements(list: org.w3c.dom.NodeList): Array<org.w3c.dom.Element> {
                return Array(0 until list.length).compactMap({ i -> list.item(i) as? org.w3c.dom.Element })
            }

            var symbolInfos: Dictionary<SymbolSize, SymbolInfo> = dictionaryOf()

            val gnodes = document.getElementsByTagName("g")
            for (symbolG in elements(gnodes)) {
                if (symbolG.getAttribute("id") != "Symbols") {
                    continue // there are also "Notes" and "Guides"
                }

                for (subG in elements(symbolG.childNodes)) {
                    val subGID = subG.getAttribute("id") // e.g., "Black-S", "Regular-S", "Ultralight-S"
                    val symbolSize_0 = SymbolSize(rawValue = subGID)
                    if (symbolSize_0 == null) {
                        logger.warning("could not parse symbol size: ${subGID}")
                        continue
                    }
                    var paths: Array<SymbolPath> = arrayOf()

                    for (pathNode in elements(subG.childNodes).filter { it -> it.nodeName == "path" }) {
                        // TODO: use the layers line multicolor-0:tintColor hierarchical-0:secondary to translate into compose equivalents
                        val pathClass = pathNode.getAttribute("class") ?: "" // e.g., monochrome-0 multicolor-0:tintColor hierarchical-0:secondary SFSymbolsPreviewWireframe
                        val pathD = pathNode.getAttribute("d")
                        if (!pathD.isEmpty) {
                            val pathParser = PathParser().parsePathString(pathD)
                            paths.append(SymbolPath(pathParser = pathParser, attrs = Array(pathClass.split(" "))))
                        }
                    }

                    symbolInfos[symbolSize_0] = SymbolInfo(size = symbolSize_0, paths = paths)
                }
            }

            return symbolInfos.sref()
        }

        val symbolInfos = rememberCached(symbolXMLCache, url) { url -> parseSymbolXML(url) }

        // match the best symbol for the current font weight
        val fontWeight = EnvironmentValues.shared._fontWeight ?: Font.Weight.regular

        // Exporting as "Static" will contain all 27 variants (9 weights * 3 sizes),
        // but "Variable" will only have 3: Ultralight-S, Regular-S, and Black-S
        // in theory, we should interpolate the paths for in-between weights (like "light"),
        // but in absence of that logic, we just try to pick the closest variant for the current font weight

        val ultraLight: Array<SymbolSize> = arrayOf(SymbolSize.UltralightM, SymbolSize.UltralightS, SymbolSize.UltralightL)
        val thin: Array<SymbolSize> = arrayOf(SymbolSize.ThinM, SymbolSize.ThinS, SymbolSize.ThinL)
        val light: Array<SymbolSize> = arrayOf(SymbolSize.LightM, SymbolSize.LightS, SymbolSize.LightL)
        val regular: Array<SymbolSize> = arrayOf(SymbolSize.RegularM, SymbolSize.RegularS, SymbolSize.RegularL)
        val medium: Array<SymbolSize> = arrayOf(SymbolSize.MediumM, SymbolSize.MediumS, SymbolSize.MediumL)
        val semibold: Array<SymbolSize> = arrayOf(SymbolSize.SemiboldM, SymbolSize.SemiboldS, SymbolSize.SemiboldL)
        val bold: Array<SymbolSize> = arrayOf(SymbolSize.BoldM, SymbolSize.BoldS, SymbolSize.BoldL)
        val heavy: Array<SymbolSize> = arrayOf(SymbolSize.HeavyM, SymbolSize.HeavyS, SymbolSize.HeavyL)
        val black: Array<SymbolSize> = arrayOf(SymbolSize.BlackM, SymbolSize.BlackS, SymbolSize.BlackL)

        var weightPriority: Array<SymbolSize> = arrayOf()

        when (fontWeight) {
            Font.Weight.ultraLight -> weightPriority = (ultraLight + thin + light + regular + medium + semibold + bold + heavy + black).sref()
            Font.Weight.thin -> weightPriority = (thin + ultraLight + light + regular + medium + semibold + bold + heavy + black).sref()
            Font.Weight.light -> weightPriority = (light + thin + ultraLight + regular + medium + semibold + bold + heavy + black).sref()
            Font.Weight.regular -> weightPriority = (regular + medium + light + thin + semibold + bold + ultraLight + heavy + black).sref()
            Font.Weight.medium -> weightPriority = (medium + regular + semibold + light + bold + thin + heavy + black + ultraLight).sref()
            Font.Weight.semibold -> weightPriority = (semibold + medium + regular + bold + light + thin + heavy + ultraLight + black).sref()
            Font.Weight.bold -> weightPriority = (bold + heavy + black + semibold + medium + regular + light + thin + ultraLight).sref()
            Font.Weight.heavy -> weightPriority = (heavy + black + bold + semibold + medium + regular + light + thin + ultraLight).sref()
            Font.Weight.black -> weightPriority = (black + heavy + bold + semibold + medium + regular + light + thin + ultraLight).sref()
        }

        val tintColor = EnvironmentValues.shared._foregroundStyle?.asColor(opacity = 1.0, animationContext = context) ?: Color.primary.colorImpl()

        weightPriority.compactMap({ it -> symbolInfos[it] }).first?.let { symbolInfo ->
            val imageVector = symbolToImageVector(symbolInfo, tintColor = tintColor)
            ComposeScaledImageVector(image = imageVector, name = name, aspectRatio = aspectRatio, contentMode = contentMode, context = context)
        }
    }

    @Composable
    private fun ComposePainter(painter: Painter, scale: Double = 1.0, colorFilter: ColorFilter? = null, aspectRatio: Double?, contentMode: ContentMode?) {
        when (resizingMode) {
            Image.ResizingMode.stretch -> {
                val scale = contentScale(aspectRatio = aspectRatio, contentMode = contentMode)
                val modifier = Modifier.fillSize(expandContainer = false)
                androidx.compose.foundation.Image(painter = painter, contentDescription = null, modifier = modifier, contentScale = scale, colorFilter = colorFilter)
            }
            else -> {
                val modifier = Modifier.wrapContentSize(unbounded = true).size((painter.intrinsicSize.width / scale).dp, (painter.intrinsicSize.height / scale).dp)
                androidx.compose.foundation.Image(painter = painter, contentDescription = null, modifier = modifier, colorFilter = colorFilter)
            }
        }
    }

    @Composable
    private fun ComposeSystem(systemName: String, aspectRatio: Double?, contentMode: ContentMode?, context: ComposeContext) {
        // we first check to see if there is a bundled symbol with the name in any of the asset catalogs, in which case we will use that symbol
        // note that we can only use the `main` (i.e., top-level) bundle to look up image resources, since Image(systemName:) does not accept a bundle
        rememberCached(contentsCache, NameBundleColorScheme(systemName, null, null)) { _ -> symbolResourceURL(name = systemName, bundle = Bundle.main) }?.let { symbolResourceURL ->
            ComposeSymbolImage(name = systemName, url = symbolResourceURL, label = null, aspectRatio = aspectRatio, contentMode = contentMode, context = context)
            return
        }
        val image_0 = Companion.composeImageVector(named = systemName)
        if (image_0 == null) {
            logger.warning("Unable to find system image named: ${systemName}")
            Icon(imageVector = Icons.Default.Warning, contentDescription = "missing icon")
            return
        }

        ComposeScaledImageVector(image = image_0, name = systemName, aspectRatio = aspectRatio, contentMode = contentMode, context = context)
    }

    @Composable
    private fun ComposeScaledImageVector(image: ImageVector, name: String, aspectRatio: Double?, contentMode: ContentMode?, context: ComposeContext) {

        val tintColor = EnvironmentValues.shared._foregroundStyle?.asColor(opacity = 1.0, animationContext = context) ?: Color.primary.colorImpl()
        when (resizingMode) {
            Image.ResizingMode.stretch -> {
                val painter = rememberVectorPainter(image)
                val colorFilter: ColorFilter?
                if (tintColor != null) {
                    colorFilter = ColorFilter.tint(tintColor)
                } else {
                    colorFilter = null
                }
                ComposePainter(painter = painter, colorFilter = colorFilter, aspectRatio = aspectRatio, contentMode = contentMode)
            }
            else -> {
                val textStyle = (EnvironmentValues.shared.font?.fontImpl?.invoke() ?: LocalTextStyle.current).sref()
                val modifier: Modifier
                if (textStyle.fontSize.isSp) {
                    val textSizeDp = with(LocalDensity.current) { -> textStyle.fontSize.toDp() }
                    // Apply a multiplier to more closely match SwiftUI's relative text and system image sizes
                    modifier = Modifier.size(textSizeDp * 1.5f)
                } else {
                    modifier = Modifier
                }
                Icon(imageVector = image, contentDescription = name, modifier = modifier, tint = tintColor ?: androidx.compose.ui.graphics.Color.Unspecified)
            }
        }
    }

    private fun contentScale(aspectRatio: Double?, contentMode: ContentMode?): ContentScale {
        if (contentMode == null) {
            return ContentScale.FillBounds.sref()
        }
        if (aspectRatio == null) {
            when (contentMode) {
                ContentMode.fit -> return ContentScale.Fit.sref()
                ContentMode.fill -> return ContentScale.Crop.sref()
            }
        }
        return AspectRatioContentScale(aspectRatio = aspectRatio, contentMode = contentMode)
    }

    /// Custom scale to handle fitting or filling a user-specified aspect ratio.
    private class AspectRatioContentScale: ContentScale {
        internal val aspectRatio: Double
        internal val contentMode: ContentMode

        override fun computeScaleFactor(srcSize: Size, dstSize: Size): ScaleFactor {
            val dstAspectRatio = (dstSize.width / dstSize.height).sref()
            when (contentMode) {
                ContentMode.fit -> return if (aspectRatio > dstAspectRatio) fitToWidth(srcSize, dstSize) else fitToHeight(srcSize, dstSize)
                ContentMode.fill -> return if (aspectRatio < dstAspectRatio) fitToWidth(srcSize, dstSize) else fitToHeight(srcSize, dstSize)
            }
        }

        private fun fitToWidth(srcSize: Size, dstSize: Size): ScaleFactor = ScaleFactor(scaleX = dstSize.width / srcSize.width, scaleY = dstSize.width / Float(aspectRatio) / srcSize.height)

        private fun fitToHeight(srcSize: Size, dstSize: Size): ScaleFactor = ScaleFactor(scaleX = dstSize.height * Float(aspectRatio) / srcSize.width, scaleY = dstSize.height / srcSize.height)

        constructor(aspectRatio: Double, contentMode: ContentMode) {
            this.aspectRatio = aspectRatio
            this.contentMode = contentMode
        }
    }

    enum class ResizingMode: Sendable {
        tile,
        stretch;

        companion object {
        }
    }

    fun resizable(): Image {
        var image = this.sref()
        image.resizingMode = Image.ResizingMode.stretch
        return image.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun resizable(capInsets: EdgeInsets): Image {
        var image = this.sref()
        image.capInsets = capInsets
        return image.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun resizable(capInsets: EdgeInsets = EdgeInsets(), resizingMode: Image.ResizingMode): Image {
        var image = this.sref()
        image.capInsets = capInsets
        image.resizingMode = resizingMode
        return image.sref()
    }

    enum class Interpolation: Sendable {
        none,
        low,
        medium,
        high;

        companion object {
        }
    }

    fun interpolation(interpolation: Image.Interpolation): Image = this.sref()

    fun antialiased(isAntialiased: Boolean): Image = this.sref()

    enum class DynamicRange: Sendable {
        standard,
        constrainedHigh,
        high;

        companion object {
        }
    }

    enum class TemplateRenderingMode: Sendable {
        template,
        original;

        companion object {
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun renderingMode(renderingMode: Image.TemplateRenderingMode?): Image = this.sref()

    enum class Orientation(override val rawValue: UByte, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): CaseIterable, Sendable, RawRepresentable<UByte> {
        up(UByte(0)),
        upMirrored(UByte(1)),
        down(UByte(2)),
        downMirrored(UByte(3)),
        left(UByte(4)),
        leftMirrored(UByte(5)),
        right(UByte(6)),
        rightMirrored(UByte(7));

        companion object: CaseIterableCompanion<Image.Orientation> {
            override val allCases: Array<Image.Orientation>
                get() = arrayOf(up, upMirrored, down, downMirrored, left, leftMirrored, right, rightMirrored)
        }
    }

    enum class Scale: Sendable {
        small,
        medium,
        large;

        companion object {
        }
    }

    /// Find all the `.xcassets` resource for the given bundle
    private fun assetContentsURLs(name: String, bundle: Bundle): Array<URL> {
        val resourceNames = bundle.resourcesIndex.sref()

        var resourceURLs: Array<URL> = arrayOf()
        for (resourceName in resourceNames.sref()) {
            val components = resourceName.split(separator = '/').map({ it -> String(it) })
            // return every *.xcassets/NAME/Contents.json
            if (components.first?.hasSuffix(".xcassets") == true && components.dropFirst().first == name && components.last == "Contents.json") {
                bundle.url(forResource = resourceName, withExtension = null)?.let { contentsURL ->
                    resourceURLs.append(contentsURL)
                }
            }
        }
        return resourceURLs.sref()
    }

    private fun imageResourceURL(name: String, colorScheme: ColorScheme, bundle: Bundle): URL? {
        for (dataURL in assetContentsURLs(name = "${name}.imageset", bundle = bundle)) {
            try {
                val data = Data(contentsOf = dataURL)
                logger.debug("loading imageset asset contents from: ${dataURL}")
                val imageSet = JSONDecoder().decode(ImageSet::class, from = data)
                var images = imageSet.images.sref()
                // check for any images that map to the given color scheme and append them as higher-priority candidates for the image to render
                images += images.filter { it ->
                    // e.g.: { "appearances" : [ { "appearance" : "luminosity", "value" : "dark" } ], "filename" : "Cat_BW.jpg", "idiom" : "universal" }
                    it.appearances?.filter({ it -> it.appearance == "luminosity" })?.compactMap({ it.value })?.contains(if (colorScheme == ColorScheme.dark) "dark" else "light") == true
                }
                // fall-back to load the highest-resolution image that is set (e.g., 3x before 2x before 1x)
                images.compactMap({ it.filename }).last.sref()?.let { fileName ->
                    // get the image filename and append it to the end
                    val resURL = dataURL.deletingLastPathComponent().appendingPathComponent(fileName)
                    logger.debug("loading imageset data from: ${resURL}")
                    return resURL.sref()
                }
            } catch (error: Throwable) {
                @Suppress("NAME_SHADOWING") val error = error.aserror()
                logger.warning("error loading image data from ${name}: ${error}")
            }
        }

        return null
    }

    private fun symbolResourceURL(name: String, bundle: Bundle): URL? {
        for (dataURL in assetContentsURLs(name = "${name}.symbolset", bundle = bundle)) {
            try {
                val data = Data(contentsOf = dataURL)
                logger.debug("loading symbolset asset contents from ${dataURL}")
                val symbolSet = JSONDecoder().decode(SymbolSet::class, from = data)
                symbolSet.symbols.compactMap({ it.filename }).last.sref()?.let { fileName ->
                    // get the symbol filename and append it to the end
                    val resURL = dataURL.deletingLastPathComponent().appendingPathComponent(fileName)
                    return resURL.sref()
                }
            } catch (error: Throwable) {
                @Suppress("NAME_SHADOWING") val error = error.aserror()
                logger.warning("error loading symbol data from ${name}: ${error}")
            }
        }

        return null
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    override fun symbolRenderingMode(mode: SymbolRenderingMode?): Image = this.sref()

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as Image
        this.image = copy.image
        this.capInsets = copy.capInsets
        this.resizingMode = copy.resizingMode
        this.scale = copy.scale
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = Image(this as MutableStruct)

    override fun equals(other: Any?): Boolean {
        if (other !is Image) return false
        return image == other.image && capInsets == other.capInsets && resizingMode == other.resizingMode && scale == other.scale
    }

    companion object {

        private fun composeSymbolName(for_: String): String? {
            val symbolName = for_
            when (symbolName) {
                "person.crop.square" -> return "Icons.Outlined.AccountBox" //􀉹
                "person.crop.circle" -> return "Icons.Outlined.AccountCircle" //􀉭
                "plus.circle.fill" -> return "Icons.Outlined.AddCircle" //􀁍
                "plus" -> return "Icons.Outlined.Add" //􀅼
                "arrow.left" -> return "Icons.Outlined.ArrowBack" //􀄪
                "arrowtriangle.down.fill" -> return "Icons.Outlined.ArrowDropDown" //􀄥
                "arrow.forward" -> return "Icons.Outlined.ArrowForward" //􀰑
                "wrench" -> return "Icons.Outlined.Build" //􀎕
                "phone" -> return "Icons.Outlined.Call" //􀌾
                "checkmark.circle" -> return "Icons.Outlined.CheckCircle" //􀁢
                "checkmark" -> return "Icons.Outlined.Check" //􀆅
                "xmark" -> return "Icons.Outlined.Clear" //􀆄
                "pencil" -> return "Icons.Outlined.Create" //􀈊
                "calendar" -> return "Icons.Outlined.DateRange" //􀉉
                "trash" -> return "Icons.Outlined.Delete" //􀈑
                "envelope" -> return "Icons.Outlined.Email" //􀍕
                "arrow.forward.square" -> return "Icons.Outlined.ExitToApp" //􀰔
                "face.smiling" -> return "Icons.Outlined.Face" //􀎸
                "heart" -> return "Icons.Outlined.FavoriteBorder" //􀊴
                "heart.fill" -> return "Icons.Outlined.Favorite" //􀊵
                "house" -> return "Icons.Outlined.Home" //􀎞
                "info.circle" -> return "Icons.Outlined.Info" //􀅴
                "chevron.down" -> return "Icons.Outlined.KeyboardArrowDown" //􀆈
                "chevron.left" -> return "Icons.Outlined.KeyboardArrowLeft" //􀆉
                "chevron.right" -> return "Icons.Outlined.KeyboardArrowRight" //􀆊
                "chevron.up" -> return "Icons.Outlined.KeyboardArrowUp" //􀆇
                "list.bullet" -> return "Icons.Outlined.List" //􀋲
                "location" -> return "Icons.Outlined.LocationOn" //􀋑
                "lock" -> return "Icons.Outlined.Lock" //􀎠
                "line.3.horizontal" -> return "Icons.Outlined.Menu" //􀌇
                "ellipsis" -> return "Icons.Outlined.MoreVert" //􀍠
                "bell" -> return "Icons.Outlined.Notifications" //􀋙
                "person" -> return "Icons.Outlined.Person" //􀉩
                "mappin.circle" -> return "Icons.Outlined.Place" //􀎪
                "play" -> return "Icons.Outlined.PlayArrow" //􀊃
                "arrow.clockwise.circle" -> return "Icons.Outlined.Refresh" //􀚁
                "magnifyingglass" -> return "Icons.Outlined.Search" //􀊫
                "paperplane" -> return "Icons.Outlined.Send" //􀈟
                "gearshape" -> return "Icons.Outlined.Settings" //􀣋
                "square.and.arrow.up" -> return "Icons.Outlined.Share" //􀈂
                "cart" -> return "Icons.Outlined.ShoppingCart" //􀍩
                "star" -> return "Icons.Outlined.Star" //􀋃
                "hand.thumbsup" -> return "Icons.Outlined.ThumbUp" //􀉿
                "exclamationmark.triangle" -> return "Icons.Outlined.Warning" //􀇿
                "person.crop.square.fill" -> return "Icons.Filled.AccountBox" //􀉺
                "person.crop.circle.fill" -> return "Icons.Filled.AccountCircle" //􀉮
                "wrench.fill" -> return "Icons.Filled.Build" //􀎖
                "phone.fill" -> return "Icons.Filled.Call" //􀌿
                "checkmark.circle.fill" -> return "Icons.Filled.CheckCircle" //􀁣
                "trash.fill" -> return "Icons.Filled.Delete" //􀈒
                "envelope.fill" -> return "Icons.Filled.Email" //􀍖
                "house.fill" -> return "Icons.Filled.Home" //􀎟
                "info.circle.fill" -> return "Icons.Filled.Info" //􀅵
                "location.fill" -> return "Icons.Filled.LocationOn" //􀋒
                "lock.fill" -> return "Icons.Filled.Lock" //􀎡
                "bell.fill" -> return "Icons.Filled.Notifications" //􀋚
                "person.fill" -> return "Icons.Filled.Person" //􀉪
                "mappin.circle.fill" -> return "Icons.Filled.Place" //􀜈
                "play.fill" -> return "Icons.Filled.PlayArrow" //􀊄
                "paperplane.fill" -> return "Icons.Filled.Send" //􀈠
                "gearshape.fill" -> return "Icons.Filled.Settings" //􀣌
                "square.and.arrow.up.fill" -> return "Icons.Filled.Share" //􀈃
                "cart.fill" -> return "Icons.Filled.ShoppingCart" //􀍪
                "star.fill" -> return "Icons.Filled.Star" //􀋃
                "hand.thumbsup.fill" -> return "Icons.Filled.ThumbUp" //􀊀
                "exclamationmark.triangle.fill" -> return "Icons.Filled.Warning" //􀇿
                else -> return null
            }
        }

        /// Returns the `androidx.compose.ui.graphics.vector.ImageVector` for the given constant name.
        ///
        /// See: https://developer.android.com/reference/kotlin/androidx/compose/material/icons/Icons.Outlined
        internal fun composeImageVector(named: String): ImageVector? {
            val name = named
            when (composeSymbolName(for_ = name) ?: name) {
                "Icons.Outlined.AccountBox" -> return Icons.Outlined.AccountBox.sref()
                "Icons.Outlined.AccountCircle" -> return Icons.Outlined.AccountCircle.sref()
                "Icons.Outlined.AddCircle" -> return Icons.Outlined.AddCircle.sref()
                "Icons.Outlined.Add" -> return Icons.Outlined.Add.sref()
                "Icons.Outlined.ArrowBack" -> return Icons.Outlined.ArrowBack.sref() // Compose 1.6 TODO: Icons.AutoMirrored.Outlined.ArrowBack
                "Icons.Outlined.ArrowDropDown" -> return Icons.Outlined.ArrowDropDown.sref()
                "Icons.Outlined.ArrowForward" -> return Icons.Outlined.ArrowForward.sref() // Compose 1.6 TODO: Icons.AutoMirrored.Outlined.ArrowForward
                "Icons.Outlined.Build" -> return Icons.Outlined.Build.sref()
                "Icons.Outlined.Call" -> return Icons.Outlined.Call.sref()
                "Icons.Outlined.CheckCircle" -> return Icons.Outlined.CheckCircle.sref()
                "Icons.Outlined.Check" -> return Icons.Outlined.Check.sref()
                "Icons.Outlined.Clear" -> return Icons.Outlined.Clear.sref()
                "Icons.Outlined.Close" -> return Icons.Outlined.Close.sref()
                "Icons.Outlined.Create" -> return Icons.Outlined.Create.sref()
                "Icons.Outlined.DateRange" -> return Icons.Outlined.DateRange.sref()
                "Icons.Outlined.Delete" -> return Icons.Outlined.Delete.sref()
                "Icons.Outlined.Done" -> return Icons.Outlined.Done.sref()
                "Icons.Outlined.Edit" -> return Icons.Outlined.Edit.sref()
                "Icons.Outlined.Email" -> return Icons.Outlined.Email.sref()
                "Icons.Outlined.ExitToApp" -> return Icons.Outlined.ExitToApp.sref() // Compose 1.6 TODO: Icons.AutoMirrored.Outlined.ExitToApp
                "Icons.Outlined.Face" -> return Icons.Outlined.Face.sref()
                "Icons.Outlined.FavoriteBorder" -> return Icons.Outlined.FavoriteBorder.sref()
                "Icons.Outlined.Favorite" -> return Icons.Outlined.Favorite.sref()
                "Icons.Outlined.Home" -> return Icons.Outlined.Home.sref()
                "Icons.Outlined.Info" -> return Icons.Outlined.Info.sref()
                "Icons.Outlined.KeyboardArrowDown" -> return Icons.Outlined.KeyboardArrowDown.sref()
                "Icons.Outlined.KeyboardArrowLeft" -> return Icons.Outlined.KeyboardArrowLeft.sref() // Compose 1.6 TODO: Icons.AutoMirrored.Outlined.KeyboardArrowLeft
                "Icons.Outlined.KeyboardArrowRight" -> return Icons.Outlined.KeyboardArrowRight.sref() // Compose 1.6 TODO: Icons.AutoMirrored.Outlined.KeyboardArrowRight
                "Icons.Outlined.KeyboardArrowUp" -> return Icons.Outlined.KeyboardArrowUp.sref()
                "Icons.Outlined.List" -> return Icons.Outlined.List.sref() // Compose 1.6 TODO: Icons.AutoMirrored.Outlined.List
                "Icons.Outlined.LocationOn" -> return Icons.Outlined.LocationOn.sref()
                "Icons.Outlined.Lock" -> return Icons.Outlined.Lock.sref()
                "Icons.Outlined.MailOutline" -> return Icons.Outlined.MailOutline.sref()
                "Icons.Outlined.Menu" -> return Icons.Outlined.Menu.sref()
                "Icons.Outlined.MoreVert" -> return Icons.Outlined.MoreVert.sref()
                "Icons.Outlined.Notifications" -> return Icons.Outlined.Notifications.sref()
                "Icons.Outlined.Person" -> return Icons.Outlined.Person.sref()
                "Icons.Outlined.Phone" -> return Icons.Outlined.Phone.sref()
                "Icons.Outlined.Place" -> return Icons.Outlined.Place.sref()
                "Icons.Outlined.PlayArrow" -> return Icons.Outlined.PlayArrow.sref()
                "Icons.Outlined.Refresh" -> return Icons.Outlined.Refresh.sref()
                "Icons.Outlined.Search" -> return Icons.Outlined.Search.sref()
                "Icons.Outlined.Send" -> return Icons.Outlined.Send.sref() // Compose 1.6 TODO: Icons.AutoMirrored.Outlined.Send
                "Icons.Outlined.Settings" -> return Icons.Outlined.Settings.sref()
                "Icons.Outlined.Share" -> return Icons.Outlined.Share.sref()
                "Icons.Outlined.ShoppingCart" -> return Icons.Outlined.ShoppingCart.sref()
                "Icons.Outlined.Star" -> return Icons.Outlined.Star.sref()
                "Icons.Outlined.ThumbUp" -> return Icons.Outlined.ThumbUp.sref()
                "Icons.Outlined.Warning" -> return Icons.Outlined.Warning.sref()
                "Icons.Filled.AccountBox" -> return Icons.Filled.AccountBox.sref()
                "Icons.Filled.AccountCircle" -> return Icons.Filled.AccountCircle.sref()
                "Icons.Filled.AddCircle" -> return Icons.Filled.AddCircle.sref()
                "Icons.Filled.Add" -> return Icons.Filled.Add.sref()
                "Icons.Filled.ArrowBack" -> return Icons.Filled.ArrowBack.sref() // Compose 1.6 TODO: Icons.AutoMirrored.Filled.ArrowBack
                "Icons.Filled.ArrowDropDown" -> return Icons.Filled.ArrowDropDown.sref()
                "Icons.Filled.ArrowForward" -> return Icons.Filled.ArrowForward.sref() // Compose 1.6 TODO: Icons.AutoMirrored.Filled.ArrowForward
                "Icons.Filled.Build" -> return Icons.Filled.Build.sref()
                "Icons.Filled.Call" -> return Icons.Filled.Call.sref()
                "Icons.Filled.CheckCircle" -> return Icons.Filled.CheckCircle.sref()
                "Icons.Filled.Check" -> return Icons.Filled.Check.sref()
                "Icons.Filled.Clear" -> return Icons.Filled.Clear.sref()
                "Icons.Filled.Close" -> return Icons.Filled.Close.sref()
                "Icons.Filled.Create" -> return Icons.Filled.Create.sref()
                "Icons.Filled.DateRange" -> return Icons.Filled.DateRange.sref()
                "Icons.Filled.Delete" -> return Icons.Filled.Delete.sref()
                "Icons.Filled.Done" -> return Icons.Filled.Done.sref()
                "Icons.Filled.Edit" -> return Icons.Filled.Edit.sref()
                "Icons.Filled.Email" -> return Icons.Filled.Email.sref()
                "Icons.Filled.ExitToApp" -> return Icons.Filled.ExitToApp.sref() // Compose 1.6 TODO: Icons.AutoMirrored.Filled.ExitToApp
                "Icons.Filled.Face" -> return Icons.Filled.Face.sref()
                "Icons.Filled.FavoriteBorder" -> return Icons.Filled.FavoriteBorder.sref()
                "Icons.Filled.Favorite" -> return Icons.Filled.Favorite.sref()
                "Icons.Filled.Home" -> return Icons.Filled.Home.sref()
                "Icons.Filled.Info" -> return Icons.Filled.Info.sref()
                "Icons.Filled.KeyboardArrowDown" -> return Icons.Filled.KeyboardArrowDown.sref()
                "Icons.Filled.KeyboardArrowLeft" -> return Icons.Filled.KeyboardArrowLeft.sref() // Compose 1.6 TODO: Icons.AutoMirrored.Filled.KeyboardArrowLeft
                "Icons.Filled.KeyboardArrowRight" -> return Icons.Filled.KeyboardArrowRight.sref() // Compose 1.6 TODO: Icons.AutoMirrored.Filled.KeyboardArrowRight
                "Icons.Filled.KeyboardArrowUp" -> return Icons.Filled.KeyboardArrowUp.sref()
                "Icons.Filled.List" -> return Icons.Filled.List.sref() // Compose 1.6 TODO: Icons.AutoMirrored.Filled.List
                "Icons.Filled.LocationOn" -> return Icons.Filled.LocationOn.sref()
                "Icons.Filled.Lock" -> return Icons.Filled.Lock.sref()
                "Icons.Filled.MailOutline" -> return Icons.Filled.MailOutline.sref()
                "Icons.Filled.Menu" -> return Icons.Filled.Menu.sref()
                "Icons.Filled.MoreVert" -> return Icons.Filled.MoreVert.sref()
                "Icons.Filled.Notifications" -> return Icons.Filled.Notifications.sref()
                "Icons.Filled.Person" -> return Icons.Filled.Person.sref()
                "Icons.Filled.Phone" -> return Icons.Filled.Phone.sref()
                "Icons.Filled.Place" -> return Icons.Filled.Place.sref()
                "Icons.Filled.PlayArrow" -> return Icons.Filled.PlayArrow.sref()
                "Icons.Filled.Refresh" -> return Icons.Filled.Refresh.sref()
                "Icons.Filled.Search" -> return Icons.Filled.Search.sref()
                "Icons.Filled.Send" -> return Icons.Filled.Send.sref() // Compose 1.6 TODO: Icons.AutoMirrored.Filled.Send
                "Icons.Filled.Settings" -> return Icons.Filled.Settings.sref()
                "Icons.Filled.Share" -> return Icons.Filled.Share.sref()
                "Icons.Filled.ShoppingCart" -> return Icons.Filled.ShoppingCart.sref()
                "Icons.Filled.Star" -> return Icons.Filled.Star.sref()
                "Icons.Filled.ThumbUp" -> return Icons.Filled.ThumbUp.sref()
                "Icons.Filled.Warning" -> return Icons.Filled.Warning.sref()
                "Icons.Rounded.AccountBox" -> return Icons.Rounded.AccountBox.sref()
                "Icons.Rounded.AccountCircle" -> return Icons.Rounded.AccountCircle.sref()
                "Icons.Rounded.AddCircle" -> return Icons.Rounded.AddCircle.sref()
                "Icons.Rounded.Add" -> return Icons.Rounded.Add.sref()
                "Icons.Rounded.ArrowBack" -> return Icons.Rounded.ArrowBack.sref() // Compose 1.6 TODO: Icons.AutoMirrored.Rounded.ArrowBack
                "Icons.Rounded.ArrowDropDown" -> return Icons.Rounded.ArrowDropDown.sref()
                "Icons.Rounded.ArrowForward" -> return Icons.Rounded.ArrowForward.sref() // Compose 1.6 TODO: Icons.AutoMirrored.Rounded.ArrowForward
                "Icons.Rounded.Build" -> return Icons.Rounded.Build.sref()
                "Icons.Rounded.Call" -> return Icons.Rounded.Call.sref()
                "Icons.Rounded.CheckCircle" -> return Icons.Rounded.CheckCircle.sref()
                "Icons.Rounded.Check" -> return Icons.Rounded.Check.sref()
                "Icons.Rounded.Clear" -> return Icons.Rounded.Clear.sref()
                "Icons.Rounded.Close" -> return Icons.Rounded.Close.sref()
                "Icons.Rounded.Create" -> return Icons.Rounded.Create.sref()
                "Icons.Rounded.DateRange" -> return Icons.Rounded.DateRange.sref()
                "Icons.Rounded.Delete" -> return Icons.Rounded.Delete.sref()
                "Icons.Rounded.Done" -> return Icons.Rounded.Done.sref()
                "Icons.Rounded.Edit" -> return Icons.Rounded.Edit.sref()
                "Icons.Rounded.Email" -> return Icons.Rounded.Email.sref()
                "Icons.Rounded.ExitToApp" -> return Icons.Rounded.ExitToApp.sref() // Compose 1.6 TODO: Icons.AutoMirrored.Rounded.ExitToApp
                "Icons.Rounded.Face" -> return Icons.Rounded.Face.sref()
                "Icons.Rounded.FavoriteBorder" -> return Icons.Rounded.FavoriteBorder.sref()
                "Icons.Rounded.Favorite" -> return Icons.Rounded.Favorite.sref()
                "Icons.Rounded.Home" -> return Icons.Rounded.Home.sref()
                "Icons.Rounded.Info" -> return Icons.Rounded.Info.sref()
                "Icons.Rounded.KeyboardArrowDown" -> return Icons.Rounded.KeyboardArrowDown.sref()
                "Icons.Rounded.KeyboardArrowLeft" -> return Icons.Rounded.KeyboardArrowLeft.sref() // Compose 1.6 TODO: Icons.AutoMirrored.Rounded.KeyboardArrowLeft
                "Icons.Rounded.KeyboardArrowRight" -> return Icons.Rounded.KeyboardArrowRight.sref() // Compose 1.6 TODO: Icons.AutoMirrored.Rounded.KeyboardArrowRight
                "Icons.Rounded.KeyboardArrowUp" -> return Icons.Rounded.KeyboardArrowUp.sref()
                "Icons.Rounded.List" -> return Icons.Rounded.List.sref() // Compose 1.6 TODO: Icons.AutoMirrored.Rounded.List
                "Icons.Rounded.LocationOn" -> return Icons.Rounded.LocationOn.sref()
                "Icons.Rounded.Lock" -> return Icons.Rounded.Lock.sref()
                "Icons.Rounded.MailOutline" -> return Icons.Rounded.MailOutline.sref()
                "Icons.Rounded.Menu" -> return Icons.Rounded.Menu.sref()
                "Icons.Rounded.MoreVert" -> return Icons.Rounded.MoreVert.sref()
                "Icons.Rounded.Notifications" -> return Icons.Rounded.Notifications.sref()
                "Icons.Rounded.Person" -> return Icons.Rounded.Person.sref()
                "Icons.Rounded.Phone" -> return Icons.Rounded.Phone.sref()
                "Icons.Rounded.Place" -> return Icons.Rounded.Place.sref()
                "Icons.Rounded.PlayArrow" -> return Icons.Rounded.PlayArrow.sref()
                "Icons.Rounded.Refresh" -> return Icons.Rounded.Refresh.sref()
                "Icons.Rounded.Search" -> return Icons.Rounded.Search.sref()
                "Icons.Rounded.Send" -> return Icons.Rounded.Send.sref() // Compose 1.6 TODO: Icons.AutoMirrored.Rounded.Send
                "Icons.Rounded.Settings" -> return Icons.Rounded.Settings.sref()
                "Icons.Rounded.Share" -> return Icons.Rounded.Share.sref()
                "Icons.Rounded.ShoppingCart" -> return Icons.Rounded.ShoppingCart.sref()
                "Icons.Rounded.Star" -> return Icons.Rounded.Star.sref()
                "Icons.Rounded.ThumbUp" -> return Icons.Rounded.ThumbUp.sref()
                "Icons.Rounded.Warning" -> return Icons.Rounded.Warning.sref()
                "Icons.Sharp.AccountBox" -> return Icons.Sharp.AccountBox.sref()
                "Icons.Sharp.AccountCircle" -> return Icons.Sharp.AccountCircle.sref()
                "Icons.Sharp.AddCircle" -> return Icons.Sharp.AddCircle.sref()
                "Icons.Sharp.Add" -> return Icons.Sharp.Add.sref()
                "Icons.Sharp.ArrowBack" -> return Icons.Sharp.ArrowBack.sref() // Compose 1.6 TODO: Icons.AutoMirrored.Sharp.ArrowBack
                "Icons.Sharp.ArrowDropDown" -> return Icons.Sharp.ArrowDropDown.sref()
                "Icons.Sharp.ArrowForward" -> return Icons.Sharp.ArrowForward.sref() // Compose 1.6 TODO: Icons.AutoMirrored.Sharp.ArrowForward
                "Icons.Sharp.Build" -> return Icons.Sharp.Build.sref()
                "Icons.Sharp.Call" -> return Icons.Sharp.Call.sref()
                "Icons.Sharp.CheckCircle" -> return Icons.Sharp.CheckCircle.sref()
                "Icons.Sharp.Check" -> return Icons.Sharp.Check.sref()
                "Icons.Sharp.Clear" -> return Icons.Sharp.Clear.sref()
                "Icons.Sharp.Close" -> return Icons.Sharp.Close.sref()
                "Icons.Sharp.Create" -> return Icons.Sharp.Create.sref()
                "Icons.Sharp.DateRange" -> return Icons.Sharp.DateRange.sref()
                "Icons.Sharp.Delete" -> return Icons.Sharp.Delete.sref()
                "Icons.Sharp.Done" -> return Icons.Sharp.Done.sref()
                "Icons.Sharp.Edit" -> return Icons.Sharp.Edit.sref()
                "Icons.Sharp.Email" -> return Icons.Sharp.Email.sref()
                "Icons.Sharp.ExitToApp" -> return Icons.Sharp.ExitToApp.sref() // Compose 1.6 TODO: Icons.AutoMirrored.Sharp.ExitToApp
                "Icons.Sharp.Face" -> return Icons.Sharp.Face.sref()
                "Icons.Sharp.FavoriteBorder" -> return Icons.Sharp.FavoriteBorder.sref()
                "Icons.Sharp.Favorite" -> return Icons.Sharp.Favorite.sref()
                "Icons.Sharp.Home" -> return Icons.Sharp.Home.sref()
                "Icons.Sharp.Info" -> return Icons.Sharp.Info.sref()
                "Icons.Sharp.KeyboardArrowDown" -> return Icons.Sharp.KeyboardArrowDown.sref()
                "Icons.Sharp.KeyboardArrowLeft" -> return Icons.Sharp.KeyboardArrowLeft.sref() // Compose 1.6 TODO: Icons.AutoMirrored.Sharp.KeyboardArrowLeft
                "Icons.Sharp.KeyboardArrowRight" -> return Icons.Sharp.KeyboardArrowRight.sref() // Compose 1.6 TODO: Icons.AutoMirrored.Sharp.KeyboardArrowRight
                "Icons.Sharp.KeyboardArrowUp" -> return Icons.Sharp.KeyboardArrowUp.sref()
                "Icons.Sharp.List" -> return Icons.Sharp.List.sref() // Compose 1.6 TODO: Icons.AutoMirrored.Sharp.List
                "Icons.Sharp.LocationOn" -> return Icons.Sharp.LocationOn.sref()
                "Icons.Sharp.Lock" -> return Icons.Sharp.Lock.sref()
                "Icons.Sharp.MailOutline" -> return Icons.Sharp.MailOutline.sref()
                "Icons.Sharp.Menu" -> return Icons.Sharp.Menu.sref()
                "Icons.Sharp.MoreVert" -> return Icons.Sharp.MoreVert.sref()
                "Icons.Sharp.Notifications" -> return Icons.Sharp.Notifications.sref()
                "Icons.Sharp.Person" -> return Icons.Sharp.Person.sref()
                "Icons.Sharp.Phone" -> return Icons.Sharp.Phone.sref()
                "Icons.Sharp.Place" -> return Icons.Sharp.Place.sref()
                "Icons.Sharp.PlayArrow" -> return Icons.Sharp.PlayArrow.sref()
                "Icons.Sharp.Refresh" -> return Icons.Sharp.Refresh.sref()
                "Icons.Sharp.Search" -> return Icons.Sharp.Search.sref()
                "Icons.Sharp.Send" -> return Icons.Sharp.Send.sref() // Compose 1.6 TODO: Icons.AutoMirrored.Sharp.Send
                "Icons.Sharp.Settings" -> return Icons.Sharp.Settings.sref()
                "Icons.Sharp.Share" -> return Icons.Sharp.Share.sref()
                "Icons.Sharp.ShoppingCart" -> return Icons.Sharp.ShoppingCart.sref()
                "Icons.Sharp.Star" -> return Icons.Sharp.Star.sref()
                "Icons.Sharp.ThumbUp" -> return Icons.Sharp.ThumbUp.sref()
                "Icons.Sharp.Warning" -> return Icons.Sharp.Warning.sref()
                "Icons.TwoTone.AccountBox" -> return Icons.TwoTone.AccountBox.sref()
                "Icons.TwoTone.AccountCircle" -> return Icons.TwoTone.AccountCircle.sref()
                "Icons.TwoTone.AddCircle" -> return Icons.TwoTone.AddCircle.sref()
                "Icons.TwoTone.Add" -> return Icons.TwoTone.Add.sref()
                "Icons.TwoTone.ArrowBack" -> return Icons.TwoTone.ArrowBack.sref() // Compose 1.6 TODO: Icons.AutoMirrored.TwoTone.ArrowBack
                "Icons.TwoTone.ArrowDropDown" -> return Icons.TwoTone.ArrowDropDown.sref()
                "Icons.TwoTone.ArrowForward" -> return Icons.TwoTone.ArrowForward.sref() // Compose 1.6 TODO: Icons.AutoMirrored.TwoTone.ArrowForward
                "Icons.TwoTone.Build" -> return Icons.TwoTone.Build.sref()
                "Icons.TwoTone.Call" -> return Icons.TwoTone.Call.sref()
                "Icons.TwoTone.CheckCircle" -> return Icons.TwoTone.CheckCircle.sref()
                "Icons.TwoTone.Check" -> return Icons.TwoTone.Check.sref()
                "Icons.TwoTone.Clear" -> return Icons.TwoTone.Clear.sref()
                "Icons.TwoTone.Close" -> return Icons.TwoTone.Close.sref()
                "Icons.TwoTone.Create" -> return Icons.TwoTone.Create.sref()
                "Icons.TwoTone.DateRange" -> return Icons.TwoTone.DateRange.sref()
                "Icons.TwoTone.Delete" -> return Icons.TwoTone.Delete.sref()
                "Icons.TwoTone.Done" -> return Icons.TwoTone.Done.sref()
                "Icons.TwoTone.Edit" -> return Icons.TwoTone.Edit.sref()
                "Icons.TwoTone.Email" -> return Icons.TwoTone.Email.sref()
                "Icons.TwoTone.ExitToApp" -> return Icons.TwoTone.ExitToApp.sref() // Compose 1.6 TODO: Icons.AutoMirrored.TwoTone.ExitToApp
                "Icons.TwoTone.Face" -> return Icons.TwoTone.Face.sref()
                "Icons.TwoTone.FavoriteBorder" -> return Icons.TwoTone.FavoriteBorder.sref()
                "Icons.TwoTone.Favorite" -> return Icons.TwoTone.Favorite.sref()
                "Icons.TwoTone.Home" -> return Icons.TwoTone.Home.sref()
                "Icons.TwoTone.Info" -> return Icons.TwoTone.Info.sref()
                "Icons.TwoTone.KeyboardArrowDown" -> return Icons.TwoTone.KeyboardArrowDown.sref()
                "Icons.TwoTone.KeyboardArrowLeft" -> return Icons.TwoTone.KeyboardArrowLeft.sref() // Compose 1.6 TODO: Icons.AutoMirrored.TwoTone.KeyboardArrowLeft
                "Icons.TwoTone.KeyboardArrowRight" -> return Icons.TwoTone.KeyboardArrowRight.sref() // Compose 1.6 TODO: Icons.AutoMirrored.TwoTone.KeyboardArrowRight
                "Icons.TwoTone.KeyboardArrowUp" -> return Icons.TwoTone.KeyboardArrowUp.sref()
                "Icons.TwoTone.List" -> return Icons.TwoTone.List.sref() // Compose 1.6 TODO: Icons.AutoMirrored.TwoTone.List
                "Icons.TwoTone.LocationOn" -> return Icons.TwoTone.LocationOn.sref()
                "Icons.TwoTone.Lock" -> return Icons.TwoTone.Lock.sref()
                "Icons.TwoTone.MailOutline" -> return Icons.TwoTone.MailOutline.sref()
                "Icons.TwoTone.Menu" -> return Icons.TwoTone.Menu.sref()
                "Icons.TwoTone.MoreVert" -> return Icons.TwoTone.MoreVert.sref()
                "Icons.TwoTone.Notifications" -> return Icons.TwoTone.Notifications.sref()
                "Icons.TwoTone.Person" -> return Icons.TwoTone.Person.sref()
                "Icons.TwoTone.Phone" -> return Icons.TwoTone.Phone.sref()
                "Icons.TwoTone.Place" -> return Icons.TwoTone.Place.sref()
                "Icons.TwoTone.PlayArrow" -> return Icons.TwoTone.PlayArrow.sref()
                "Icons.TwoTone.Refresh" -> return Icons.TwoTone.Refresh.sref()
                "Icons.TwoTone.Search" -> return Icons.TwoTone.Search.sref()
                "Icons.TwoTone.Send" -> return Icons.TwoTone.Send.sref() // Compose 1.6 TODO: Icons.AutoMirrored.TwoTone.Send
                "Icons.TwoTone.Settings" -> return Icons.TwoTone.Settings.sref()
                "Icons.TwoTone.Share" -> return Icons.TwoTone.Share.sref()
                "Icons.TwoTone.ShoppingCart" -> return Icons.TwoTone.ShoppingCart.sref()
                "Icons.TwoTone.Star" -> return Icons.TwoTone.Star.sref()
                "Icons.TwoTone.ThumbUp" -> return Icons.TwoTone.ThumbUp.sref()
                "Icons.TwoTone.Warning" -> return Icons.TwoTone.Warning.sref()
                else -> return null
            }
        }

        fun Orientation(rawValue: UByte): Image.Orientation? {
            return when (rawValue) {
                UByte(0) -> Orientation.up
                UByte(1) -> Orientation.upMirrored
                UByte(2) -> Orientation.down
                UByte(3) -> Orientation.downMirrored
                UByte(4) -> Orientation.left
                UByte(5) -> Orientation.leftMirrored
                UByte(6) -> Orientation.right
                UByte(7) -> Orientation.rightMirrored
                else -> null
            }
        }
    }
}


private class SymbolInfo {
    internal val size: SymbolSize
    internal val paths: Array<SymbolPath>

    constructor(size: SymbolSize, paths: Array<SymbolPath>) {
        this.size = size
        this.paths = paths.sref()
    }
}

private class SymbolPath {
    internal val pathParser: PathParser
    internal val attrs: Array<String>

    constructor(pathParser: PathParser, attrs: Array<String>) {
        this.pathParser = pathParser.sref()
        this.attrs = attrs.sref()
    }
}

/// A cache key for remembering the Content.json URL location in the bundled assets for the given name, bundle, and ColorScheme combination
private class NameBundleColorScheme {
    internal val name: String
    internal val bundle: Bundle?
    internal val colorScheme: ColorScheme?

    constructor(name: String, bundle: Bundle? = null, colorScheme: ColorScheme? = null) {
        this.name = name
        this.bundle = bundle
        this.colorScheme = colorScheme
    }

    override fun equals(other: Any?): Boolean {
        if (other !is NameBundleColorScheme) return false
        return name == other.name && bundle == other.bundle && colorScheme == other.colorScheme
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, name)
        result = Hasher.combine(result, bundle)
        result = Hasher.combine(result, colorScheme)
        return result
    }
}

private val symbolXMLCache: Dictionary<URL, Dictionary<SymbolSize, SymbolInfo>> = dictionaryOf()
private val contentsCache: Dictionary<NameBundleColorScheme, URL?> = dictionaryOf()

/// A simple local cache that returns the cached value if it exists, or else instantiates it using the block and stores the result in the cache
internal fun <T, U> rememberCached(cache: Dictionary<T, U>, key: T, block: (T) -> U): U {
    return synchronized(cache) l@{ ->
        cache[key].sref()?.let { value ->
            return@l value
        }
        val value = block(key)
        cache[key] = value.sref()
        return@l value
    }
}


/// The Symbols layer contains up to 27 sublayers, each representing a symbol image variant. Identifiers of symbol variants have the form <weight>-<{S, M, L}>, where weight corresponds to a weight of the system font and S, M, or L matches the small, medium, or large symbol scale.
private enum class SymbolSize(override val rawValue: String, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): RawRepresentable<String> {
    UltralightS("Ultralight-S"),
    ThinS("Thin-S"),
    LightS("Light-S"),
    RegularS("Regular-S"),
    MediumS("Medium-S"),
    SemiboldS("Semibold-S"),
    BoldS("Bold-S"),
    HeavyS("Heavy-S"),
    BlackS("Black-S"),

    UltralightM("Ultralight-M"),
    ThinM("Thin-M"),
    LightM("Light-M"),
    RegularM("Regular-M"),
    MediumM("Medium-M"),
    SemiboldM("Semibold-M"),
    BoldM("Bold-M"),
    HeavyM("Heavy-M"),
    BlackM("Black-M"),

    UltralightL("Ultralight-L"),
    ThinL("Thin-L"),
    LightL("Light-L"),
    RegularL("Regular-L"),
    MediumL("Medium-L"),
    SemiboldL("Semibold-L"),
    BoldL("Bold-L"),
    HeavyL("Heavy-L"),
    BlackL("Black-L");

    internal val fontWeight: Font.Weight
        get() {
            when (this) {
                SymbolSize.UltralightS, SymbolSize.UltralightM, SymbolSize.UltralightL -> return Font.Weight.ultraLight
                SymbolSize.ThinS, SymbolSize.ThinM, SymbolSize.ThinL -> return Font.Weight.thin
                SymbolSize.LightS, SymbolSize.LightM, SymbolSize.LightL -> return Font.Weight.light
                SymbolSize.RegularS, SymbolSize.RegularM, SymbolSize.RegularL -> return Font.Weight.regular
                SymbolSize.MediumS, SymbolSize.MediumM, SymbolSize.MediumL -> return Font.Weight.medium
                SymbolSize.SemiboldS, SymbolSize.SemiboldM, SymbolSize.SemiboldL -> return Font.Weight.semibold
                SymbolSize.BoldS, SymbolSize.BoldM, SymbolSize.BoldL -> return Font.Weight.bold
                SymbolSize.HeavyS, SymbolSize.HeavyM, SymbolSize.HeavyL -> return Font.Weight.heavy
                SymbolSize.BlackS, SymbolSize.BlackM, SymbolSize.BlackL -> return Font.Weight.black
            }
        }
}

private fun SymbolSize(rawValue: String): SymbolSize? {
    return when (rawValue) {
        "Ultralight-S" -> SymbolSize.UltralightS
        "Thin-S" -> SymbolSize.ThinS
        "Light-S" -> SymbolSize.LightS
        "Regular-S" -> SymbolSize.RegularS
        "Medium-S" -> SymbolSize.MediumS
        "Semibold-S" -> SymbolSize.SemiboldS
        "Bold-S" -> SymbolSize.BoldS
        "Heavy-S" -> SymbolSize.HeavyS
        "Black-S" -> SymbolSize.BlackS
        "Ultralight-M" -> SymbolSize.UltralightM
        "Thin-M" -> SymbolSize.ThinM
        "Light-M" -> SymbolSize.LightM
        "Regular-M" -> SymbolSize.RegularM
        "Medium-M" -> SymbolSize.MediumM
        "Semibold-M" -> SymbolSize.SemiboldM
        "Bold-M" -> SymbolSize.BoldM
        "Heavy-M" -> SymbolSize.HeavyM
        "Black-M" -> SymbolSize.BlackM
        "Ultralight-L" -> SymbolSize.UltralightL
        "Thin-L" -> SymbolSize.ThinL
        "Light-L" -> SymbolSize.LightL
        "Regular-L" -> SymbolSize.RegularL
        "Medium-L" -> SymbolSize.MediumL
        "Semibold-L" -> SymbolSize.SemiboldL
        "Bold-L" -> SymbolSize.BoldL
        "Heavy-L" -> SymbolSize.HeavyL
        "Black-L" -> SymbolSize.BlackL
        else -> null
    }
}


/* The `Contents.json` in a `*.imageset` folder for an image
https://developer.apple.com/library/archive/documentation/Xcode/Reference/xcode_ref-Asset_Catalog_Format/ImageSetType.html
{
"images" : [
{
"filename" : "Cat.jpg",
"idiom" : "universal",
"scale" : "1x"
},
{
"idiom" : "universal",
"scale" : "2x"
},
{
"idiom" : "universal",
"scale" : "3x"
}
],
"info" : {
"author" : "xcode",
"version" : 1
}
}
*/
private class ImageSet: Decodable {
    internal val images: Array<ImageSet.ImageInfo>
    internal val info: AssetConentsInfo

    internal class ImageInfo: Decodable {
        internal val filename: String?
        internal val idiom: String? // e.g. "universal"
        internal val scale: String? // e.g. "3x"
        internal val appearances: Array<ImageSet.ImageAppearance>?

        constructor(filename: String? = null, idiom: String? = null, scale: String? = null, appearances: Array<ImageSet.ImageAppearance>? = null) {
            this.filename = filename
            this.idiom = idiom
            this.scale = scale
            this.appearances = appearances.sref()
        }

        private enum class CodingKeys(override val rawValue: String, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): CodingKey, RawRepresentable<String> {
            filename("filename"),
            idiom("idiom"),
            scale("scale"),
            appearances("appearances");
        }

        constructor(from: Decoder) {
            val container = from.container(keyedBy = CodingKeys::class)
            this.filename = container.decodeIfPresent(String::class, forKey = CodingKeys.filename)
            this.idiom = container.decodeIfPresent(String::class, forKey = CodingKeys.idiom)
            this.scale = container.decodeIfPresent(String::class, forKey = CodingKeys.scale)
            this.appearances = container.decodeIfPresent(Array::class, elementType = ImageSet.ImageAppearance::class, forKey = CodingKeys.appearances)
        }

        companion object: DecodableCompanion<ImageSet.ImageInfo> {
            override fun init(from: Decoder): ImageSet.ImageInfo = ImageSet.ImageInfo(from = from)

            private fun CodingKeys(rawValue: String): CodingKeys? {
                return when (rawValue) {
                    "filename" -> CodingKeys.filename
                    "idiom" -> CodingKeys.idiom
                    "scale" -> CodingKeys.scale
                    "appearances" -> CodingKeys.appearances
                    else -> null
                }
            }
        }
    }

    internal class ImageAppearance: Decodable {
        internal val appearance: String? // e.g., "luminosity"
        internal val value: String? // e.g., "light", "dark"

        constructor(appearance: String? = null, value: String? = null) {
            this.appearance = appearance
            this.value = value
        }

        private enum class CodingKeys(override val rawValue: String, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): CodingKey, RawRepresentable<String> {
            appearance("appearance"),
            value_("value");
        }

        constructor(from: Decoder) {
            val container = from.container(keyedBy = CodingKeys::class)
            this.appearance = container.decodeIfPresent(String::class, forKey = CodingKeys.appearance)
            this.value = container.decodeIfPresent(String::class, forKey = CodingKeys.value_)
        }

        companion object: DecodableCompanion<ImageSet.ImageAppearance> {
            override fun init(from: Decoder): ImageSet.ImageAppearance = ImageSet.ImageAppearance(from = from)

            private fun CodingKeys(rawValue: String): CodingKeys? {
                return when (rawValue) {
                    "appearance" -> CodingKeys.appearance
                    "value" -> CodingKeys.value_
                    else -> null
                }
            }
        }
    }

    constructor(images: Array<ImageSet.ImageInfo>, info: AssetConentsInfo) {
        this.images = images.sref()
        this.info = info
    }

    private enum class CodingKeys(override val rawValue: String, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): CodingKey, RawRepresentable<String> {
        images("images"),
        info("info");
    }

    constructor(from: Decoder) {
        val container = from.container(keyedBy = CodingKeys::class)
        this.images = container.decode(Array::class, elementType = ImageSet.ImageInfo::class, forKey = CodingKeys.images)
        this.info = container.decode(AssetConentsInfo::class, forKey = CodingKeys.info)
    }

    companion object: DecodableCompanion<ImageSet> {
        override fun init(from: Decoder): ImageSet = ImageSet(from = from)

        private fun CodingKeys(rawValue: String): CodingKeys? {
            return when (rawValue) {
                "images" -> CodingKeys.images
                "info" -> CodingKeys.info
                else -> null
            }
        }
    }
}

/* The `Contents.json` in a `*.symbolset` folder for a symbol, which looks like:
{
"info" : {
"author" : "xcode",
"version" : 1
},
"symbols" : [
{
"filename" : "face.dashed.fill.svg",
"idiom" : "universal"
}
]
}
*/
private class SymbolSet: Decodable {
    internal val symbols: Array<SymbolSet.Symbol>
    internal val info: AssetConentsInfo

    internal class Symbol: Decodable {
        internal val filename: String?
        internal val idiom: String? // e.g. "universal"

        constructor(filename: String? = null, idiom: String? = null) {
            this.filename = filename
            this.idiom = idiom
        }

        private enum class CodingKeys(override val rawValue: String, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): CodingKey, RawRepresentable<String> {
            filename("filename"),
            idiom("idiom");
        }

        constructor(from: Decoder) {
            val container = from.container(keyedBy = CodingKeys::class)
            this.filename = container.decodeIfPresent(String::class, forKey = CodingKeys.filename)
            this.idiom = container.decodeIfPresent(String::class, forKey = CodingKeys.idiom)
        }

        companion object: DecodableCompanion<SymbolSet.Symbol> {
            override fun init(from: Decoder): SymbolSet.Symbol = SymbolSet.Symbol(from = from)

            private fun CodingKeys(rawValue: String): CodingKeys? {
                return when (rawValue) {
                    "filename" -> CodingKeys.filename
                    "idiom" -> CodingKeys.idiom
                    else -> null
                }
            }
        }
    }

    constructor(symbols: Array<SymbolSet.Symbol>, info: AssetConentsInfo) {
        this.symbols = symbols.sref()
        this.info = info
    }

    private enum class CodingKeys(override val rawValue: String, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): CodingKey, RawRepresentable<String> {
        symbols("symbols"),
        info("info");
    }

    constructor(from: Decoder) {
        val container = from.container(keyedBy = CodingKeys::class)
        this.symbols = container.decode(Array::class, elementType = SymbolSet.Symbol::class, forKey = CodingKeys.symbols)
        this.info = container.decode(AssetConentsInfo::class, forKey = CodingKeys.info)
    }

    companion object: DecodableCompanion<SymbolSet> {
        override fun init(from: Decoder): SymbolSet = SymbolSet(from = from)

        private fun CodingKeys(rawValue: String): CodingKeys? {
            return when (rawValue) {
                "symbols" -> CodingKeys.symbols
                "info" -> CodingKeys.info
                else -> null
            }
        }
    }
}

/* The `Contents.json` in a `*.colorset` folder for a symbol
https://developer.apple.com/library/archive/documentation/Xcode/Reference/xcode_ref-Asset_Catalog_Format/Named_Color.html
{
"colors" : [
{
"color" : {
"platform" : "universal",
"reference" : "systemBlueColor"
},
"idiom" : "universal"
}
],
"info" : {
"author" : "xcode",
"version" : 1
}
}
*/
private class ColorSet: Decodable {
    internal val colors: Array<ColorSet.ColorSetColor>
    internal val info: AssetConentsInfo

    internal class ColorSetColor: Decodable {
        internal val color: ColorSet.ColorInfo?
        internal val idiom: String? // e.g. "universal"

        constructor(color: ColorSet.ColorInfo? = null, idiom: String? = null) {
            this.color = color
            this.idiom = idiom
        }

        private enum class CodingKeys(override val rawValue: String, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): CodingKey, RawRepresentable<String> {
            color("color"),
            idiom("idiom");
        }

        constructor(from: Decoder) {
            val container = from.container(keyedBy = CodingKeys::class)
            this.color = container.decodeIfPresent(ColorSet.ColorInfo::class, forKey = CodingKeys.color)
            this.idiom = container.decodeIfPresent(String::class, forKey = CodingKeys.idiom)
        }

        companion object: DecodableCompanion<ColorSet.ColorSetColor> {
            override fun init(from: Decoder): ColorSet.ColorSetColor = ColorSet.ColorSetColor(from = from)

            private fun CodingKeys(rawValue: String): CodingKeys? {
                return when (rawValue) {
                    "color" -> CodingKeys.color
                    "idiom" -> CodingKeys.idiom
                    else -> null
                }
            }
        }
    }

    internal class ColorInfo: Decodable {
        internal val platform: String? // e.g. "universal"
        internal val reference: String? // e.g. "systemBlueColor"

        constructor(platform: String? = null, reference: String? = null) {
            this.platform = platform
            this.reference = reference
        }

        private enum class CodingKeys(override val rawValue: String, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): CodingKey, RawRepresentable<String> {
            platform("platform"),
            reference("reference");
        }

        constructor(from: Decoder) {
            val container = from.container(keyedBy = CodingKeys::class)
            this.platform = container.decodeIfPresent(String::class, forKey = CodingKeys.platform)
            this.reference = container.decodeIfPresent(String::class, forKey = CodingKeys.reference)
        }

        companion object: DecodableCompanion<ColorSet.ColorInfo> {
            override fun init(from: Decoder): ColorSet.ColorInfo = ColorSet.ColorInfo(from = from)

            private fun CodingKeys(rawValue: String): CodingKeys? {
                return when (rawValue) {
                    "platform" -> CodingKeys.platform
                    "reference" -> CodingKeys.reference
                    else -> null
                }
            }
        }
    }

    constructor(colors: Array<ColorSet.ColorSetColor>, info: AssetConentsInfo) {
        this.colors = colors.sref()
        this.info = info
    }

    private enum class CodingKeys(override val rawValue: String, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): CodingKey, RawRepresentable<String> {
        colors("colors"),
        info("info");
    }

    constructor(from: Decoder) {
        val container = from.container(keyedBy = CodingKeys::class)
        this.colors = container.decode(Array::class, elementType = ColorSet.ColorSetColor::class, forKey = CodingKeys.colors)
        this.info = container.decode(AssetConentsInfo::class, forKey = CodingKeys.info)
    }

    companion object: DecodableCompanion<ColorSet> {
        override fun init(from: Decoder): ColorSet = ColorSet(from = from)

        private fun CodingKeys(rawValue: String): CodingKeys? {
            return when (rawValue) {
                "colors" -> CodingKeys.colors
                "info" -> CodingKeys.info
                else -> null
            }
        }
    }
}


private class AssetConentsInfo: Decodable {
    internal val author: String? // e.g. "xcode"
    internal val version: Int? // e.g. 1

    constructor(author: String? = null, version: Int? = null) {
        this.author = author
        this.version = version
    }

    private enum class CodingKeys(override val rawValue: String, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): CodingKey, RawRepresentable<String> {
        author("author"),
        version("version");
    }

    constructor(from: Decoder) {
        val container = from.container(keyedBy = CodingKeys::class)
        this.author = container.decodeIfPresent(String::class, forKey = CodingKeys.author)
        this.version = container.decodeIfPresent(Int::class, forKey = CodingKeys.version)
    }

    companion object: DecodableCompanion<AssetConentsInfo> {
        override fun init(from: Decoder): AssetConentsInfo = AssetConentsInfo(from = from)

        private fun CodingKeys(rawValue: String): CodingKeys? {
            return when (rawValue) {
                "author" -> CodingKeys.author
                "version" -> CodingKeys.version
                else -> null
            }
        }
    }
}

