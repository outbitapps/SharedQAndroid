// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import kotlin.reflect.KClass
import skip.lib.*
import skip.lib.Array
import skip.lib.Sequence
import skip.lib.Set

import skip.foundation.*
import androidx.activity.compose.BackHandler
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

class NavigationStack<Root>: View where Root: View {
    internal val root: Root
    internal val path: Binding<Array<Any>>?
    internal val navigationPath: Binding<NavigationPath>?

    constructor(root: () -> Root) {
        this.root = root()
        this.path = null
        this.navigationPath = null
    }

    constructor(path: Binding<NavigationPath>, root: () -> Root) {
        this.root = root()
        this.path = null
        this.navigationPath = path.sref()
    }

    constructor(path: Any, root: () -> Root) {
        this.root = root()
        this.path = (path as Binding<Array<Any>>?).sref()
        this.navigationPath = null
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun ComposeContent(context: ComposeContext) {
        // Have to use rememberSaveable for e.g. a nav stack in each tab
        val destinations = rememberSaveable(stateSaver = context.stateSaver as Saver<Preference<Dictionary<KClass<*>, NavigationDestination>>, Any>) { -> mutableStateOf(Preference<Dictionary<KClass<*>, NavigationDestination>>(key = NavigationDestinationsPreferenceKey::class)) }
        // Make this collector non-erasable so that destinations defined at e.g. the root nav stack layer don't disappear when you push
        val destinationsCollector = PreferenceCollector<Dictionary<KClass<*>, NavigationDestination>>(key = NavigationDestinationsPreferenceKey::class, state = destinations, isErasable = false)
        val reducedDestinations = destinations.value.reduced.sref()
        val navController = rememberNavController()
        val navigator = rememberSaveable(stateSaver = context.stateSaver as Saver<Navigator, Any>) { -> mutableStateOf(Navigator(navController = navController, destinations = reducedDestinations)) }
        navigator.value.didCompose(navController = navController, destinations = reducedDestinations, path = path, navigationPath = navigationPath, keyboardController = LocalSoftwareKeyboardController.current)

        val providedNavigator = LocalNavigator provides navigator.value
        CompositionLocalProvider(providedNavigator) { ->
            val safeArea = EnvironmentValues.shared._safeArea
            // We have to ignore the safe area around the entire NavHost to prevent push/pop animation issues with the system bars.
            // When we layout, only extend into safe areas that are due to system bars, not into any app chrome
            var ignoresSafeAreaEdges: Edge.Set = Edge.Set.of(Edge.Set.top, Edge.Set.bottom)
            ignoresSafeAreaEdges.formIntersection(safeArea?.absoluteSystemBarEdges ?: Edge.Set.of())
            IgnoresSafeAreaLayout(edges = ignoresSafeAreaEdges, context = context) { context ->
                ComposeContainer(modifier = context.modifier, fillWidth = true, fillHeight = true) { modifier ->
                    val isRTL = EnvironmentValues.shared.layoutDirection == LayoutDirection.rightToLeft
                    NavHost(navController = navController, startDestination = Navigator.rootRoute, modifier = modifier) { ->
                        composable(route = Navigator.rootRoute, exitTransition = { ->
                            slideOutHorizontally(targetOffsetX = { it -> it * (if (isRTL) 1 else -1) / 3 })
                        }, popEnterTransition = { ->
                            slideInHorizontally(initialOffsetX = { it -> it * (if (isRTL) 1 else -1) / 3 })
                        }) l@{ entry ->
                            val state_0 = navigator.value.state(for_ = entry)
                            if (state_0 == null) {
                                return@l
                            }
                            // These preferences are per-entry, but if we put them in ComposeEntry then their initial values don't show
                            // during the navigation animation. We have to collect them here
                            val title = rememberSaveable(stateSaver = state_0.stateSaver as Saver<Preference<Text>, Any>) { -> mutableStateOf(Preference<Text>(key = NavigationTitlePreferenceKey::class)) }
                            val titleCollector = PreferenceCollector<Text>(key = NavigationTitlePreferenceKey::class, state = title)
                            val toolbarPreferences = rememberSaveable(stateSaver = state_0.stateSaver as Saver<Preference<ToolbarPreferences>, Any>) { -> mutableStateOf(Preference<ToolbarPreferences>(key = ToolbarPreferenceKey::class)) }
                            val toolbarPreferencesCollector = PreferenceCollector<ToolbarPreferences>(key = ToolbarPreferenceKey::class, state = toolbarPreferences)

                            val arguments = NavigationEntryArguments(isRoot = true, state = state_0, safeArea = safeArea, ignoresSafeAreaEdges = ignoresSafeAreaEdges, title = title.value.reduced, toolbarPreferences = toolbarPreferences.value.reduced)
                            PreferenceValues.shared.collectPreferences(arrayOf(titleCollector, toolbarPreferencesCollector, destinationsCollector)) { ->
                                ComposeEntry(navigator = navigator, arguments = arguments, context = context) { context -> root.Compose(context = context) }
                            }
                        }
                        for (destinationIndex in 0 until Navigator.destinationCount) {
                            composable(route = Navigator.route(for_ = destinationIndex, valueString = "{identifier}"), arguments = listOf(navArgument("identifier") { -> type = NavType.StringType }), enterTransition = { ->
                                slideInHorizontally(initialOffsetX = { it -> it * (if (isRTL) -1 else 1) })
                            }, exitTransition = { ->
                                slideOutHorizontally(targetOffsetX = { it -> it * (if (isRTL) 1 else -1) / 3 })
                            }, popEnterTransition = { ->
                                slideInHorizontally(initialOffsetX = { it -> it * (if (isRTL) 1 else -1) / 3 })
                            }, popExitTransition = { ->
                                slideOutHorizontally(targetOffsetX = { it -> it * (if (isRTL) -1 else 1) })
                            }) l@{ entry ->
                                val state_1 = navigator.value.state(for_ = entry)
                                if (state_1 == null) {
                                    return@l
                                }
                                val targetValue_0 = state_1.targetValue.sref()
                                if (targetValue_0 == null) {
                                    return@l
                                }
                                // These preferences are per-entry, but if we put them in ComposeEntry then their initial values don't show
                                // during the navigation animation. We have to collect them here
                                val title = rememberSaveable(stateSaver = state_1.stateSaver as Saver<Preference<Text>, Any>) { -> mutableStateOf(Preference<Text>(key = NavigationTitlePreferenceKey::class)) }
                                val titleCollector = PreferenceCollector<Text>(key = NavigationTitlePreferenceKey::class, state = title)
                                val toolbarPreferences = rememberSaveable(stateSaver = state_1.stateSaver as Saver<Preference<ToolbarPreferences>, Any>) { -> mutableStateOf(Preference<ToolbarPreferences>(key = ToolbarPreferenceKey::class)) }
                                val toolbarPreferencesCollector = PreferenceCollector<ToolbarPreferences>(key = ToolbarPreferenceKey::class, state = toolbarPreferences)

                                EnvironmentValues.shared.setValues({ it ->
                                    it.setdismiss(DismissAction(action = { -> navigator.value.navigateBack() }))
                                }, in_ = { ->
                                    val arguments = NavigationEntryArguments(isRoot = false, state = state_1, safeArea = safeArea, ignoresSafeAreaEdges = ignoresSafeAreaEdges, title = title.value.reduced, toolbarPreferences = toolbarPreferences.value.reduced)
                                    PreferenceValues.shared.collectPreferences(arrayOf(titleCollector, toolbarPreferencesCollector, destinationsCollector)) { ->
                                        ComposeEntry(navigator = navigator, arguments = arguments, context = context) { context ->
                                            val destinationArguments = NavigationDestinationArguments(targetValue = targetValue_0)
                                            ComposeDestination(state_1.destination, arguments = destinationArguments, context = context)
                                        }
                                    }
                                })
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ComposeEntry(navigator: MutableState<Navigator>, arguments: NavigationEntryArguments, context: ComposeContext, content: @Composable (ComposeContext) -> Unit) {
        val context = context.content(stateSaver = arguments.state.stateSaver)

        val topBarPreferences = arguments.toolbarPreferences.navigationBar
        val bottomBarPreferences = arguments.toolbarPreferences.bottomBar
        val effectiveTitleDisplayMode = navigator.value.titleDisplayMode(for_ = arguments.state, preference = arguments.toolbarPreferences.titleDisplayMode)
        val isInlineTitleDisplayMode = useInlineTitleDisplayMode(for_ = effectiveTitleDisplayMode, safeArea = arguments.safeArea)
        val toolbarItems = ToolbarItems(content = arguments.toolbarPreferences.content ?: arrayOf())

        val searchFieldPadding = 16.dp.sref()
        val density = LocalDensity.current.sref()
        val searchFieldHeightPx = with(density) { -> searchFieldHeight.dp.toPx() + searchFieldPadding.toPx() }
        val searchFieldOffsetPx = rememberSaveable(stateSaver = context.stateSaver as Saver<Float, Any>) { -> mutableStateOf(0.0f) }
        val searchFieldScrollConnection = remember { -> SearchFieldScrollConnection(heightPx = searchFieldHeightPx, offsetPx = searchFieldOffsetPx) }

        val scrollToTop = rememberSaveable(stateSaver = context.stateSaver as Saver<Preference<() -> Unit>, Any>) { -> mutableStateOf(Preference<() -> Unit>(key = ScrollToTopPreferenceKey::class)) }
        val scrollToTopCollector = PreferenceCollector<() -> Unit>(key = ScrollToTopPreferenceKey::class, state = scrollToTop)

        val scrollBehavior = if (isInlineTitleDisplayMode) TopAppBarDefaults.pinnedScrollBehavior() else TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
        var modifier = Modifier.nestedScroll(searchFieldScrollConnection)
        if (topBarPreferences?.visibility != Visibility.hidden) {
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        }
        modifier = modifier.then(context.modifier)

        // Intercept system back button to keep our state in sync
        BackHandler(enabled = !navigator.value.isRoot) { ->
            if (arguments.toolbarPreferences.backButtonHidden != true) {
                navigator.value.navigateBack()
            }
        }

        val topBarBottomPx = remember { ->
            // Default our initial value to the expected value, which helps avoid visual artifacts as we measure actual values and
            // recompose with adjusted layouts
            val safeAreaTopPx = arguments.safeArea?.safeBoundsPx?.top ?: 0.0f
            mutableStateOf(with(density) { -> safeAreaTopPx + 112.dp.toPx() })
        }
        val topBar: @Composable () -> Unit = l@{ ->
            if (topBarPreferences?.visibility == Visibility.hidden) {
                SideEffect { -> topBarBottomPx.value = 0.0f }
                return@l
            }
            val topLeadingItems = toolbarItems.filterTopBarLeading()
            val topTrailingItems = toolbarItems.filterTopBarTrailing()
            if (arguments.isRoot && arguments.title == NavigationTitlePreferenceKey.defaultValue && topLeadingItems.isEmpty && topTrailingItems.isEmpty && topBarPreferences?.visibility != Visibility.visible) {
                SideEffect { -> topBarBottomPx.value = 0.0f }
                return@l
            }
            val materialColorScheme = (topBarPreferences?.colorScheme?.asMaterialTheme() ?: MaterialTheme.colorScheme).sref()
            MaterialTheme(colorScheme = materialColorScheme) { ->
                val tint = EnvironmentValues.shared._tint ?: Color(colorImpl = { -> MaterialTheme.colorScheme.onSurface })
                val placement = EnvironmentValues.shared._placement.sref()
                EnvironmentValues.shared.setValues({ it ->
                    it.set_placement(placement.union(ViewPlacement.toolbar))
                    it.set_tint(tint)
                }, in_ = { ->
                    val interactionSource = remember { -> MutableInteractionSource() }
                    var topBarModifier = Modifier.zIndex(1.1f)
                        .clickable(interactionSource = interactionSource, indication = null, onClick = { -> scrollToTop.value.reduced() })
                        .onGloballyPositioned { it ->
                            val bottomPx = it.boundsInWindow().bottom.sref()
                            if (bottomPx > 0.0f) {
                                topBarBottomPx.value = bottomPx
                            }
                        }
                    val topBarBackgroundColor: androidx.compose.ui.graphics.Color
                    if (topBarPreferences?.backgroundVisibility == Visibility.hidden) {
                        topBarBackgroundColor = Color.clear.colorImpl()
                    } else {
                        val matchtarget_0 = topBarPreferences?.background
                        if (matchtarget_0 != null) {
                            val background = matchtarget_0
                            val matchtarget_1 = background.asColor(opacity = 1.0, animationContext = null)
                            if (matchtarget_1 != null) {
                                val color = matchtarget_1
                                topBarBackgroundColor = color
                            } else {
                                topBarBackgroundColor = Color.clear.colorImpl()
                                background.asBrush(opacity = 1.0, animationContext = null)?.let { brush ->
                                    topBarModifier = topBarModifier.background(brush)
                                }
                            }
                        } else {
                            topBarBackgroundColor = Color.systemBarBackground.colorImpl()
                        }
                    }
                    val topBarColors = TopAppBarDefaults.topAppBarColors(containerColor = topBarBackgroundColor, scrolledContainerColor = topBarBackgroundColor, titleContentColor = MaterialTheme.colorScheme.onSurface)
                    val topBarTitle: @Composable () -> Unit = { -> androidx.compose.material3.Text(arguments.title.localizedTextString(), maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    val topBarNavigationIcon: @Composable () -> Unit = { ->
                        val hasBackButton = !arguments.isRoot && arguments.toolbarPreferences.backButtonHidden != true
                        if (hasBackButton || !topLeadingItems.isEmpty) {
                            val toolbarItemContext = context.content(modifier = Modifier.padding(start = 12.dp, end = 12.dp))
                            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) { ->
                                if (hasBackButton) {
                                    IconButton(onClick = { -> navigator.value.navigateBack() }) { ->
                                        val isRTL = EnvironmentValues.shared.layoutDirection == LayoutDirection.rightToLeft
                                        Icon(imageVector = (if (isRTL) Icons.Filled.ArrowForward else Icons.Filled.ArrowBack), contentDescription = "Back", tint = tint.colorImpl())
                                    }
                                }
                                topLeadingItems.forEach { it -> it.Compose(context = toolbarItemContext) }
                            }
                        }
                    }
                    val topBarActions: @Composable () -> Unit = { ->
                        val toolbarItemContext = context.content(modifier = Modifier.padding(start = 12.dp, end = 12.dp))
                        topTrailingItems.forEach { it -> it.Compose(context = toolbarItemContext) }
                    }
                    if (isInlineTitleDisplayMode) {
                        TopAppBar(modifier = topBarModifier, colors = topBarColors, title = topBarTitle, navigationIcon = topBarNavigationIcon, actions = { -> topBarActions() }, scrollBehavior = scrollBehavior)
                    } else {
                        // Force a larger, bold title style in the uncollapsed state by replacing the headlineSmall style the bar uses
                        val typography = MaterialTheme.typography.sref()
                        val appBarTitleStyle = typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
                        val appBarTypography = typography.copy(headlineSmall = appBarTitleStyle)
                        MaterialTheme(colorScheme = MaterialTheme.colorScheme, typography = appBarTypography, shapes = MaterialTheme.shapes) { ->
                            MediumTopAppBar(modifier = topBarModifier, colors = topBarColors, title = topBarTitle, navigationIcon = topBarNavigationIcon, actions = { -> topBarActions() }, scrollBehavior = scrollBehavior)
                        }
                    }
                })
            }
        }

        val bottomBarTopPx = remember { -> mutableStateOf(0.0f) }
        val bottomBar: @Composable () -> Unit = l@{ ->
            if (bottomBarPreferences?.visibility == Visibility.hidden) {
                SideEffect { -> bottomBarTopPx.value = 0.0f }
                return@l
            }
            val bottomItems = toolbarItems.filterBottomBar()
            if (bottomItems.isEmpty && bottomBarPreferences?.visibility != Visibility.visible) {
                SideEffect { -> bottomBarTopPx.value = 0.0f }
                return@l
            }
            val materialColorScheme = (bottomBarPreferences?.colorScheme?.asMaterialTheme() ?: MaterialTheme.colorScheme).sref()
            MaterialTheme(colorScheme = materialColorScheme) { ->
                val tint = EnvironmentValues.shared._tint ?: Color(colorImpl = { -> MaterialTheme.colorScheme.onSurface })
                val placement = EnvironmentValues.shared._placement.sref()
                EnvironmentValues.shared.setValues({ it ->
                    it.set_tint(tint)
                    it.set_placement(placement.union(ViewPlacement.toolbar))
                }, in_ = { ->
                    var bottomBarModifier = Modifier.zIndex(1.1f)
                        .onGloballyPositioned { it -> bottomBarTopPx.value = it.boundsInWindow().top }
                    val bottomBarBackgroundColor: androidx.compose.ui.graphics.Color
                    if (bottomBarPreferences?.backgroundVisibility == Visibility.hidden) {
                        bottomBarBackgroundColor = Color.clear.colorImpl()
                    } else {
                        val matchtarget_2 = bottomBarPreferences?.background
                        if (matchtarget_2 != null) {
                            val background = matchtarget_2
                            val matchtarget_3 = background.asColor(opacity = 1.0, animationContext = null)
                            if (matchtarget_3 != null) {
                                val color = matchtarget_3
                                bottomBarBackgroundColor = color
                            } else {
                                bottomBarBackgroundColor = Color.clear.colorImpl()
                                background.asBrush(opacity = 1.0, animationContext = null)?.let { brush ->
                                    bottomBarModifier = bottomBarModifier.background(brush)
                                }
                            }
                        } else {
                            bottomBarBackgroundColor = Color.systemBarBackground.colorImpl()
                        }
                    }
                    BottomAppBar(modifier = bottomBarModifier, containerColor = bottomBarBackgroundColor, contentPadding = PaddingValues.Absolute(left = 16.dp, right = 16.dp), windowInsets = WindowInsets(bottom = 0.dp)) { ->
                        // Use an HStack so that it sets up the environment for bottom toolbar Spacers
                        HStack(spacing = 24.0) { ->
                            ComposeBuilder { composectx: ComposeContext ->
                                ComposeBuilder l@{ itemContext ->
                                    bottomItems.forEach { it -> it.Compose(context = itemContext) }
                                    return@l ComposeResult.ok
                                }.Compose(composectx)
                                ComposeResult.ok
                            }
                        }.Compose(context.content())
                    }
                })
            }
        }

        // We place nav bars within each entry rather than at the navigation controller level. There isn't a fluid animation
        // between navigation bar states on Android, and it is simpler to only hoist navigation bar preferences to this level
        Column(modifier = modifier.background(Color.background.colorImpl())) { ->
            // Calculate safe area for content
            val contentSafeArea = arguments.safeArea?.insetting(Edge.top, to = topBarBottomPx.value)?.insetting(Edge.bottom, to = bottomBarTopPx.value)
            // Inset manually for any edge where our container ignored the safe area, but we aren't showing a bar
            val topPadding = (if (topBarBottomPx.value <= 0.0f && arguments.ignoresSafeAreaEdges.contains(Edge.Set.top)) WindowInsets.systemBars.asPaddingValues().calculateTopPadding() else 0.dp).sref()
            val bottomPadding = (if (bottomBarTopPx.value <= 0.0f && arguments.ignoresSafeAreaEdges.contains(Edge.Set.bottom)) WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() else 0.dp).sref()
            val contentModifier = Modifier.fillMaxWidth().weight(1.0f).padding(top = topPadding, bottom = bottomPadding)

            topBar()
            Box(modifier = contentModifier, contentAlignment = androidx.compose.ui.Alignment.Center) { ->
                var topPadding = 0.dp.sref()
                if (arguments.isRoot) {
                    EnvironmentValues.shared._searchableState?.let { searchableState ->
                        val searchFieldModifier = Modifier.background(Color.systemBarBackground.colorImpl()).height(searchFieldHeight.dp + searchFieldPadding).align(androidx.compose.ui.Alignment.TopCenter).offset({ -> IntOffset(0, Int(searchFieldOffsetPx.value)) }).padding(start = searchFieldPadding, bottom = searchFieldPadding, end = searchFieldPadding).fillMaxWidth()
                        SearchField(state = searchableState, context = context.content(modifier = searchFieldModifier))
                        val searchFieldPlaceholderPadding = (searchFieldHeight.dp + searchFieldPadding + (with(LocalDensity.current) { -> searchFieldOffsetPx.value.toDp() })).sref()
                        topPadding = searchFieldPlaceholderPadding.sref()
                    }
                }
                EnvironmentValues.shared.setValues({ it ->
                    if (contentSafeArea != null) {
                        it.set_safeArea(contentSafeArea)
                    }
                }, in_ = { ->
                    // Elevate the top padding modifier so that content always has the same context, allowing it to avoid recomposition
                    Box(modifier = Modifier.padding(top = topPadding)) { ->
                        PreferenceValues.shared.collectPreferences(arrayOf(scrollToTopCollector)) { -> content(context.content()) }
                    }
                })
            }
            bottomBar()
        }
    }

    @Composable
    private fun ComposeDestination(destination: ((Any) -> View)?, arguments: NavigationDestinationArguments, context: ComposeContext) {
        // Break out this function to give it stable arguments and avoid recomosition on push/pop
        destination?.invoke(arguments.targetValue)?.Compose(context = context)
    }

    @Composable
    private fun useInlineTitleDisplayMode(for_: ToolbarTitleDisplayMode, safeArea: SafeArea?): Boolean {
        val titleDisplayMode = for_
        if (titleDisplayMode != ToolbarTitleDisplayMode.automatic) {
            return titleDisplayMode == ToolbarTitleDisplayMode.inline
        }
        // Default to inline if in landscape or a sheet
        if ((safeArea != null) && (safeArea.presentationBoundsPx.width > safeArea.presentationBoundsPx.height)) {
            return true
        }
        return EnvironmentValues.shared._sheetDepth > 0
    }

    companion object {
    }
}

@Stable
internal class NavigationEntryArguments {
    internal val isRoot: Boolean
    internal val state: Navigator.BackStackState
    internal val safeArea: SafeArea?
    internal val ignoresSafeAreaEdges: Edge.Set
    internal val title: Text
    internal val toolbarPreferences: ToolbarPreferences

    constructor(isRoot: Boolean, state: Navigator.BackStackState, safeArea: SafeArea? = null, ignoresSafeAreaEdges: Edge.Set, title: Text, toolbarPreferences: ToolbarPreferences) {
        this.isRoot = isRoot
        this.state = state
        this.safeArea = safeArea
        this.ignoresSafeAreaEdges = ignoresSafeAreaEdges.sref()
        this.title = title
        this.toolbarPreferences = toolbarPreferences
    }

    override fun equals(other: Any?): Boolean {
        if (other !is NavigationEntryArguments) return false
        return isRoot == other.isRoot && state == other.state && safeArea == other.safeArea && ignoresSafeAreaEdges == other.ignoresSafeAreaEdges && title == other.title && toolbarPreferences == other.toolbarPreferences
    }
}

@Stable
internal class NavigationDestinationArguments {
    internal val targetValue: Any

    constructor(targetValue: Any) {
        this.targetValue = targetValue.sref()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is NavigationDestinationArguments) return false
        return targetValue == other.targetValue
    }
}

internal typealias NavigationDestinations = Dictionary<KClass<*>, NavigationDestination>
internal class NavigationDestination {
    internal val destination: (Any) -> View
    // No way to compare closures. Assume equal so we don't think our destinations are constantly updating
    override fun equals(other: Any?): Boolean = true

    constructor(destination: (Any) -> View) {
        this.destination = destination
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Stable
internal class Navigator {

    private var navController: NavHostController
        get() = field.sref({ this.navController = it })
        set(newValue) {
            field = newValue.sref()
        }
    private var keyboardController: SoftwareKeyboardController? = null
        get() = field.sref({ this.keyboardController = it })
        set(newValue) {
            field = newValue.sref()
        }
    private var destinations: Dictionary<KClass<*>, NavigationDestination>
        get() = field.sref({ this.destinations = it })
        set(newValue) {
            field = newValue.sref()
        }
    private var destinationIndexes: Dictionary<KClass<*>, Int> = dictionaryOf()
        get() = field.sref({ this.destinationIndexes = it })
        set(newValue) {
            field = newValue.sref()
        }

    // We reserve the last destination index for static destinations. Every time we navigate to a static destination view, we increment the
    // destination value to give it a unique navigation path of e.g. 99/0, 99/1, 99/2, etc
    private val viewDestinationIndex = Companion.destinationCount - 1
    private var viewDestinationValue = 0

    private var path: Binding<Array<Any>>? = null
        get() = field.sref({ this.path = it })
        set(newValue) {
            field = newValue.sref()
        }
    private var navigationPath: Binding<NavigationPath>? = null
        get() = field.sref({ this.navigationPath = it })
        set(newValue) {
            field = newValue.sref()
        }

    private var backStackState: Dictionary<String, Navigator.BackStackState> = dictionaryOf()
        get() = field.sref({ this.backStackState = it })
        set(newValue) {
            field = newValue.sref()
        }
    internal class BackStackState {
        internal val id: String
        internal val route: String
        internal val destination: ((Any) -> View)?
        internal val targetValue: Any?
        internal val stateSaver: ComposeStateSaver
        internal var titleDisplayMode: ToolbarTitleDisplayMode? = null

        internal constructor(id: String, route: String, destination: ((Any) -> View)? = null, targetValue: Any? = null, stateSaver: ComposeStateSaver = ComposeStateSaver()) {
            this.id = id
            this.route = route
            this.destination = destination
            this.targetValue = targetValue.sref()
            this.stateSaver = stateSaver
        }
    }

    internal constructor(navController: NavHostController, destinations: Dictionary<KClass<*>, NavigationDestination>) {
        this.navController = navController
        this.destinations = destinations
        updateDestinationIndexes()
    }

    /// Call with updated state on recompose.
    @Composable
    internal fun didCompose(navController: NavHostController, destinations: Dictionary<KClass<*>, NavigationDestination>, path: Binding<Array<Any>>?, navigationPath: Binding<NavigationPath>?, keyboardController: SoftwareKeyboardController?) {
        this.navController = navController
        this.destinations = destinations
        this.path = path
        this.navigationPath = navigationPath
        this.keyboardController = keyboardController
        updateDestinationIndexes()
        syncState()
        navigateToPath()
    }

    /// Whether we're at the root of the navigation stack.
    internal val isRoot: Boolean
        get() = navController.currentBackStack.value.size <= 2 // graph entry, root entry

    /// Navigate to a target value specified in a `NavigationLink`.
    internal fun navigate(to: Any) {
        val targetValue = to
        val matchtarget_4 = path
        if (matchtarget_4 != null) {
            val path = matchtarget_4
            path.wrappedValue.append(targetValue)
        } else {
            val matchtarget_5 = navigationPath
            if (matchtarget_5 != null) {
                val navigationPath = matchtarget_5
                navigationPath.wrappedValue.append(targetValue)
            } else {
                navigate(to = targetValue, type = type(of = targetValue))
            }
        }
    }

    /// Navigate to a destination view.
    internal fun navigateToView(view: View) {
        val targetValue = viewDestinationValue
        viewDestinationValue += 1

        val route = Companion.route(for_ = viewDestinationIndex, valueString = String(describing = targetValue))
        navigate(route = route, destination = { _ -> view }, targetValue = targetValue)
    }

    /// Pop the back stack.
    internal fun navigateBack() {
        val matchtarget_6 = path
        if (matchtarget_6 != null) {
            val path = matchtarget_6
            path.wrappedValue.popLast()
        } else {
            val matchtarget_7 = navigationPath
            if (matchtarget_7 != null) {
                val navigationPath = matchtarget_7
                navigationPath.wrappedValue.removeLast()
            } else if (!isRoot) {
                navController.popBackStack()
            }
        }
    }

    /// The entry being navigated to.
    internal fun state(for_: NavBackStackEntry): Navigator.BackStackState? {
        val entry = for_
        backStackState[entry.id]?.let { state ->
            return state
        }
        if (navController.currentBackStack.value.count() <= 1 || entry.id != navController.currentBackStack.value[1].id) {
            return null
        }
        val rootState = BackStackState(id = entry.id, route = Companion.rootRoute)
        backStackState[entry.id] = rootState
        return rootState
    }

    /// The effective title display mode for the given preference value.
    internal fun titleDisplayMode(for_: Navigator.BackStackState, preference: ToolbarTitleDisplayMode?): ToolbarTitleDisplayMode {
        val state = for_
        if (preference != null) {
            state.titleDisplayMode = preference
            return preference
        }

        // Base the display mode on the back stack
        var titleDisplayMode: ToolbarTitleDisplayMode? = null
        for (entry in navController.currentBackStack.value.sref()) {
            if (entry.id == state.id) {
                break
            } else {
                backStackState[entry.id]?.titleDisplayMode?.let { entryTitleDisplayMode ->
                    titleDisplayMode = entryTitleDisplayMode
                }
            }
        }
        return titleDisplayMode ?: ToolbarTitleDisplayMode.automatic
    }

    /// Sync our back stack state with the nav controller.
    @Composable
    private fun syncState() {
        // Collect as state to ensure we get re-called on change
        val entryList = navController.currentBackStack.collectAsState()
        // Sync the back stack with remaining states. We delay this to allow views that receive compose calls while animating away to find their state
        LaunchedEffect(entryList.value) { ->
            delay(1000) // 1 second
            var syncedBackStackState: Dictionary<String, Navigator.BackStackState> = dictionaryOf()
            for (entry in entryList.value.sref()) {
                backStackState[entry.id]?.let { state ->
                    syncedBackStackState[entry.id] = state
                }
            }
            backStackState = syncedBackStackState
        }
    }

    private fun navigateToPath() {
        val path_0 = (this.path?.wrappedValue ?: navigationPath?.wrappedValue?.path).sref()
        if (path_0 == null) {
            return
        }
        val backStack = navController.currentBackStack.value.sref()
        if (backStack.isEmpty()) {
            return
        }

        // Figure out where the path and back stack first differ
        var pathIndex = 0
        var backStackIndex = 2 // graph, root
        while (pathIndex < path_0.count) {
            if (backStackIndex >= backStack.count()) {
                break
            }
            val state = backStackState[backStack[backStackIndex].id]
            if (state?.targetValue != path_0[pathIndex]) {
                break
            }
            pathIndex += 1
            backStackIndex += 1
        }
        // Pop back to last common value
        for (unusedbinding in 0 until (backStack.count() - backStackIndex)) {
            navController.popBackStack()
        }
        // Navigate to any new path values
        for (i in pathIndex until path_0.count) {
            navigate(to = path_0[i], type = type(of = path_0[i]))
        }
    }

    private fun navigate(to: Any, type: KClass<*>?): Boolean {
        val targetValue = to
        if (type == null) {
            return false
        }
        val destination_0 = destinations[type]
        if (destination_0 == null) {
            for (supertype in type.supertypes.sref()) {
                if (navigate(to = targetValue, type = supertype as? KClass<*>)) {
                    return true
                }
            }
            return false
        }

        val route = route(for_ = type, value = targetValue)
        navigate(route = route, destination = destination_0.destination, targetValue = targetValue)
        return true
    }

    private fun navigate(route: String, destination: ((Any) -> View)?, targetValue: Any) {
        // We see a top app bar glitch when the keyboard animates away after push, so manually dismiss it first
        keyboardController?.hide()
        navController.navigate(route)
        navController.currentBackStackEntry.sref()?.let { entry ->
            if (backStackState[entry.id] == null) {
                backStackState[entry.id] = BackStackState(id = entry.id, route = route, destination = destination, targetValue = targetValue)
            }
        }
    }

    private fun route(for_: KClass<*>, value: Any): String {
        val targetType = for_
        val index_0 = destinationIndexes[targetType]
        if (index_0 == null) {
            return String(describing = targetType) + "?"
        }
        // Escape '/' because it is meaningful in navigation routes
        var valueString = composeBundleString(for_ = value)
        valueString = valueString.replacingOccurrences(of = "/", with = "%2F")
        return route(for_ = index_0, valueString = valueString)
    }

    private fun updateDestinationIndexes() {
        for (type in destinations.keys.sref()) {
            if (destinationIndexes[type] == null) {
                destinationIndexes[type] = destinationIndexes.count
            }
        }
    }

    companion object {
        /// Route for the root of the navigation stack.
        internal val rootRoute = "navigationroot"

        /// Number of possible destiation routes.
        ///
        /// We route to destinations by static index rather than a dynamic system based on the provided destination
        /// keys because changing the destinations of a `NavHost` wipes out its back stack. By using a fixed set of
        /// indexes, we can maintain the back stack even as we add destination mappings.
        internal val destinationCount = 100

        /// Route for the given destination index and value string.
        internal fun route(for_: Int, valueString: String): String {
            val destinationIndex = for_
            return String(describing = destinationIndex) + "/" + valueString
        }
    }
}

internal val LocalNavigator: ProvidableCompositionLocal<Navigator?> = compositionLocalOf { -> null as Navigator? }

class NavigationSplitViewStyle: RawRepresentable<Int> {
    override val rawValue: Int

    constructor(rawValue: Int) {
        this.rawValue = rawValue
    }

    override fun equals(other: Any?): Boolean {
        if (other !is NavigationSplitViewStyle) return false
        return rawValue == other.rawValue
    }

    companion object {

        var automatic = NavigationSplitViewStyle(rawValue = 0)
            get() = field.sref({ this.automatic = it })
            set(newValue) {
                field = newValue.sref()
            }
        var balanced = NavigationSplitViewStyle(rawValue = 1)
            get() = field.sref({ this.balanced = it })
            set(newValue) {
                field = newValue.sref()
            }
        var prominentDetail = NavigationSplitViewStyle(rawValue = 2)
            get() = field.sref({ this.prominentDetail = it })
            set(newValue) {
                field = newValue.sref()
            }
    }
}

class NavigationBarItem: Sendable {
    enum class TitleDisplayMode: Sendable {
        automatic,
        inline,
        large;

        companion object {
        }
    }

    override fun equals(other: Any?): Boolean = other is NavigationBarItem

    override fun hashCode(): Int = "NavigationBarItem".hashCode()

    companion object {
    }
}

internal class NavigationDestinationsPreferenceKey: PreferenceKey<Dictionary<KClass<*>, NavigationDestination>> {

    companion object: PreferenceKeyCompanion<NavigationDestinations> {
        override val defaultValue: Dictionary<KClass<*>, NavigationDestination> = dictionaryOf()
        override fun reduce(value: InOut<Dictionary<KClass<*>, NavigationDestination>>, nextValue: () -> Dictionary<KClass<*>, NavigationDestination>) {
            for ((type, destination) in nextValue()) {
                value.value[type] = destination
            }
        }
    }
}

internal class NavigationTitlePreferenceKey: PreferenceKey<Text> {

    companion object: PreferenceKeyCompanion<Text> {
        override val defaultValue = Text(LocalizedStringKey(stringLiteral = ""))
        override fun reduce(value: InOut<Text>, nextValue: () -> Text) {
            value.value = nextValue()
        }
    }
}

class NavigationLink: View, ListItemAdapting {
    internal val value: Any?
    internal val destination: ComposeBuilder?
    internal val label: ComposeBuilder

    constructor(value: Any?, label: () -> View) {
        this.value = value.sref()
        this.destination = null
        this.label = ComposeBuilder.from(label)
    }

    constructor(title: String, value: Any?): this(value = value, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(verbatim = title).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    constructor(titleKey: LocalizedStringKey, value: Any?): this(value = value, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(titleKey).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    constructor(destination: () -> View, label: () -> View) {
        this.value = null
        this.destination = ComposeBuilder.from(destination)
        this.label = ComposeBuilder.from(label)
    }

    constructor(titleKey: LocalizedStringKey, destination: () -> View): this(destination = destination, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(titleKey).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    constructor(title: String, destination: () -> View): this(destination = destination, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(verbatim = title).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        val navigationContext = context.content(modifier = NavigationModifier(context.modifier))
        ComposeTextButton(label = label, context = navigationContext)
    }

    @Composable
    override fun shouldComposeListItem(): Boolean = true

    @Composable
    override fun ComposeListItem(context: ComposeContext, contentModifier: Modifier) {
        Row(modifier = NavigationModifier(modifier = Modifier).then(contentModifier), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) { ->
            Box(modifier = Modifier.weight(1.0f)) { ->
                // Continue to specialize for list rendering within the NavigationLink (e.g. Label)
                label.Compose(context = context.content(composer = ListItemComposer(contentModifier = Modifier)))
            }
            Companion.ComposeChevron()
        }
    }

    @Composable
    private fun NavigationModifier(modifier: Modifier): Modifier {
        val navigator = LocalNavigator.current.sref()
        return modifier.clickable(enabled = (value != null || destination != null) && EnvironmentValues.shared.isEnabled) l@{ ->
            // Hack to prevent multiple quick taps from pushing duplicate entries
            val now = CFAbsoluteTimeGetCurrent()
            if (NavigationLink.lastNavigationTime + NavigationLink.minimumNavigationInterval > now) {
                return@l
            }
            NavigationLink.lastNavigationTime = now

            if (value != null) {
                navigator?.navigate(to = value)
            } else if (destination != null) {
                navigator?.navigateToView(destination)
            }
        }
    }

    companion object {

        private val minimumNavigationInterval = 0.35
        private var lastNavigationTime = 0.0

        @Composable
        internal fun ComposeChevron() {
            val isRTL = EnvironmentValues.shared.layoutDirection == LayoutDirection.rightToLeft
            Icon(imageVector = if (isRTL) Icons.Outlined.KeyboardArrowLeft else Icons.Outlined.KeyboardArrowRight, contentDescription = null, tint = androidx.compose.ui.graphics.Color.Gray)
        }
    }
}

class NavigationPath: MutableStruct {
    internal var path: Array<Any> = arrayOf()
        get() = field.sref({ this.path = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }

    constructor() {
    }

    constructor(elements: Sequence<*>) {
        path.append(contentsOf = elements as Sequence<Any>)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(codable: NavigationPath.CodableRepresentation) {
    }

    val count: Int
        get() = path.count

    val isEmpty: Boolean
        get() = path.isEmpty

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val codable: NavigationPath.CodableRepresentation?
        get() {
            fatalError()
        }

    fun append(value: Any) {
        willmutate()
        try {
            path.append(value)
        } finally {
            didmutate()
        }
    }

    fun removeLast(k: Int = 1) {
        willmutate()
        try {
            path.removeLast(k)
        } finally {
            didmutate()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is NavigationPath) {
            return false
        }
        val lhs = this
        val rhs = other
        return lhs.path == rhs.path
    }

    class CodableRepresentation: Codable {
        constructor(from: Decoder) {
        }

        override fun encode(to: Encoder) = Unit

        companion object: DecodableCompanion<NavigationPath.CodableRepresentation> {
            override fun init(from: Decoder): NavigationPath.CodableRepresentation = NavigationPath.CodableRepresentation(from = from)
        }
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as NavigationPath
        this.path = copy.path
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = NavigationPath(this as MutableStruct)

    companion object {
    }
}

