@file:OptIn(ExperimentalMaterial3Api::class)

package com.moonglow.candles.core.navigation

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.moonglow.candles.R
import com.moonglow.candles.core.theme.*
import com.moonglow.candles.data.remote.AuthenticatedSupabaseHttp
import com.moonglow.candles.data.local.SessionManager
import com.moonglow.candles.data.repository.SupabaseRestAuthRepository
import com.moonglow.candles.data.model.UserProfile
import com.moonglow.candles.data.repository.MoonGlowRepository
import com.moonglow.candles.presentation.screens.admin.AdminScreen
import com.moonglow.candles.presentation.screens.auth.*
import com.moonglow.candles.presentation.screens.cart.*
import com.moonglow.candles.presentation.screens.chat.ChatSupportScreen
import com.moonglow.candles.presentation.screens.checkout.*
import com.moonglow.candles.presentation.screens.home.*
import com.moonglow.candles.presentation.screens.kits.*
import com.moonglow.candles.presentation.screens.notifications.*
import com.moonglow.candles.presentation.screens.onboarding.*
import com.moonglow.candles.presentation.screens.orders.*
import com.moonglow.candles.presentation.screens.profile.*
import com.moonglow.candles.presentation.screens.search.*
import com.moonglow.candles.presentation.screens.settings.*
import com.moonglow.candles.presentation.screens.shop.*
import com.moonglow.candles.presentation.screens.wishlist.*
import com.moonglow.candles.presentation.screens.workshops.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class Route(
    val path: String,
    val label: String,
    val icon: ImageVector
) {
    object Onboarding : Route("onboarding", "Start", Icons.Outlined.Spa)
    object Login : Route("login", "Login", Icons.Outlined.Person)
    object Signup : Route("signup", "Signup", Icons.Outlined.PersonAdd)

    object Home : Route("home", "Home", Icons.Outlined.Home)
    object Shop : Route("shop", "Candles", Icons.Outlined.LocalMall)
    object Kits : Route("kits", "Kits", Icons.Outlined.Inventory2)
    object Workshops : Route("workshops", "Classes", Icons.Outlined.Event)
    object Cart : Route("cart", "Cart", Icons.Outlined.ShoppingCart)
    object Profile : Route("profile", "Profile", Icons.Outlined.Person)

    object Search : Route("search", "Search", Icons.Outlined.Search)
    object Wishlist : Route("wishlist", "Wishlist", Icons.Outlined.FavoriteBorder)
    object Checkout : Route("checkout", "Checkout", Icons.Outlined.Payment)
    object Notifications : Route("notifications", "Notifications", Icons.Outlined.Notifications)
    object Settings : Route("settings", "Settings", Icons.Outlined.Settings)
    object Orders : Route("orders", "Orders", Icons.Outlined.ReceiptLong)

    object ChatSupport : Route("chat_support", "Support", Icons.Outlined.SupportAgent)
    object Admin : Route("admin", "Admin", Icons.Outlined.AdminPanelSettings)
}

/** Startup navigation must wait for Supabase Auth validation rather than local email storage. */
enum class StartupAuthState { Checking, SignedIn, SignedOut }

class AppViewModel(
    application: Application
) : AndroidViewModel(application) {

    val repo = MoonGlowRepository()
    private val sessionManager = SessionManager(application)
    private val authRepository = SupabaseRestAuthRepository(application)
    private val _startupAuthState = MutableStateFlow(StartupAuthState.Checking)
    val startupAuthState: StateFlow<StartupAuthState> = _startupAuthState.asStateFlow()

    init {
        AuthenticatedSupabaseHttp.initialize(application)
        viewModelScope.launch {
            try {
                val savedUser: UserProfile? = sessionManager.userFlow.first()
                val authenticated = authRepository.restoreSession()
                if (authenticated != null) {
                    val name = savedUser?.takeIf {
                        it.email.equals(authenticated.email, ignoreCase = true)
                    }?.name ?: authenticated.email.substringBefore('@')
                    repo.setLoggedInUser(name, authenticated.email)
                    sessionManager.saveUser(name, authenticated.email)
                    _startupAuthState.value = StartupAuthState.SignedIn
                } else {
                    // An email in DataStore alone is not proof of authentication.
                    repo.logout()
                    _startupAuthState.value = StartupAuthState.SignedOut
                }
            } catch (_: Exception) {
                repo.logout()
                _startupAuthState.value = StartupAuthState.SignedOut
            }
        }
    }

    fun saveSession(
        name: String,
        email: String
    ) {
        viewModelScope.launch {
            sessionManager.saveUser(
                name = name,
                email = email
            )
        }
    }

    fun clearSession() {
        authRepository.clearSession()
        repo.logout()
        viewModelScope.launch {
            sessionManager.clearUser()
        }
    }
}

@Composable
fun MoonGlowApp(
    vm: AppViewModel = viewModel()
) {
    val startupState by vm.startupAuthState.collectAsState()
    if (startupState == StartupAuthState.Checking) {
        // Avoid flashing onboarding/login while a saved session is being verified.
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = GoldDark)
        }
        return
    }

    val nav = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val notifications by vm.repo.notifications.collectAsState()
    val latestNotification: String? = notifications.firstOrNull()

    // Existing notifications belong in the inbox, not in a snackbar on every login.
    // This prevents old welcome messages obscuring the storefront on launch.

    val bottomRoutes = listOf(
        Route.Home,
        Route.Shop,
        Route.Kits,
        Route.Workshops,
        Route.Cart,
        Route.Profile
    )

    val current by nav.currentBackStackEntryAsState()
    val currentPath = current?.destination?.route ?: Route.Onboarding.path

    val showBottomBar = currentPath in bottomRoutes.map { it.path }
    val showTopBar = currentPath !in listOf(Route.Onboarding.path, Route.Home.path, Route.Admin.path)

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Charcoal,
                    contentColor = CardWhite,
                    actionColor = GoldLight,
                    dismissActionContentColor = GoldLight,
                    shape = RoundedCornerShape(18.dp)
                )
            }
        },
        topBar = {
            if (showTopBar) {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.brand_mark),
                                contentDescription = "AMBRYN Logo",
                                modifier = Modifier.height(32.dp).width(32.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (currentPath) {
                                    Route.Login.path -> "Login"
                                    Route.Signup.path -> "Sign Up"
                                    Route.Shop.path -> "Candles"
                                    Route.Kits.path -> "Kits"
                                    Route.Workshops.path -> "Classes"
                                    Route.Cart.path -> "Cart"
                                    Route.Profile.path -> "Profile"
                                    Route.Search.path -> "Search"
                                    Route.Wishlist.path -> "Wishlist"
                                    Route.Checkout.path -> "Checkout"
                                    Route.Notifications.path -> "Notifications"
                                    Route.Settings.path -> "Settings"
                                    Route.Orders.path -> "Orders"
                                    Route.ChatSupport.path -> "Support"
                                    else -> "AMBRYN"
                                },
                                color = Charcoal,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { if (!nav.popBackStack()) nav.navigate(Route.Home.path) }) {
                            Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Cream,
                        titleContentColor = Charcoal,
                        navigationIconContentColor = GoldDark
                    )
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                MoonGlowBottomBar(
                    bottomRoutes = bottomRoutes,
                    currentPath = currentPath,
                    onNavigate = { route ->
                        nav.navigate(route.path) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(Route.Home.path) { saveState = true }
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = if (startupState == StartupAuthState.SignedIn) {
                Route.Home.path
            } else {
                Route.Onboarding.path
            },
            modifier = Modifier.padding(padding)
        ) {
            composable(Route.Onboarding.path) {
                OnboardingScreen { nav.navigate(Route.Login.path) { popUpTo(Route.Onboarding.path) { inclusive = true } } }
            }
            composable(Route.Login.path) {
                LoginScreen(
                    repo = vm.repo,
                    onSessionSave = { name, email -> vm.saveSession(name, email) },
                    onSignup = { nav.navigate(Route.Signup.path) },
                    onDone = { role ->
                        val destination = when (role.lowercase()) {
                            "admin" -> Route.Admin.path
                            "support" -> Route.ChatSupport.path
                            else -> Route.Home.path
                        }
                        nav.navigate(destination) { popUpTo(Route.Login.path) { inclusive = true } }
                    }
                )
            }
            composable(Route.Signup.path) {
                SignupScreen(
                    repo = vm.repo,
                    onSessionSave = { name, email -> vm.saveSession(name, email) },
                    onLogin = { nav.navigate(Route.Login.path) { popUpTo(Route.Signup.path) { inclusive = true } } },
                    onDone = { nav.navigate(Route.Login.path) { popUpTo(Route.Signup.path) { inclusive = true } } }
                )
            }
            composable(Route.Home.path) { HomeScreen(repo = vm.repo, nav = nav) }
            composable(Route.Shop.path) { ShopScreen(repo = vm.repo, nav = nav) }
            composable(Route.Kits.path) { KitsScreen(repo = vm.repo) }
            composable(Route.Workshops.path) { WorkshopsScreen(repo = vm.repo) }
            composable(Route.Cart.path) { CartScreen(repo = vm.repo, onCheckout = { nav.navigate(Route.Checkout.path) }) }
            composable(Route.Checkout.path) { CheckoutScreen(repo = vm.repo) { nav.navigate(Route.Orders.path) { popUpTo(Route.Cart.path) { inclusive = true } } } }
            composable(Route.Profile.path) {
                ProfileScreen(repo = vm.repo, nav = nav, onLogout = { vm.clearSession() })
            }
            composable(Route.Search.path) { SearchScreen(repo = vm.repo) }
            composable(Route.Wishlist.path) { WishlistScreen(repo = vm.repo) }
            composable(Route.Notifications.path) { NotificationsScreen() }
            composable(Route.Settings.path) { SettingsScreen() }
            composable(Route.Orders.path) { OrdersScreen(repo = vm.repo) }
            composable(Route.ChatSupport.path) { ChatSupportScreen(repo = vm.repo) }
            composable(Route.Admin.path) { AdminScreen() }
        }
    }
}

@Composable
private fun MoonGlowBottomBar(
    bottomRoutes: List<Route>,
    currentPath: String,
    onNavigate: (Route) -> Unit
) {
    Surface(
        tonalElevation = 12.dp,
        shadowElevation = 18.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = CardWhite
    ) {
        NavigationBar(containerColor = Color.Transparent, tonalElevation = 0.dp) {
            bottomRoutes.forEach { item ->
                NavigationBarItem(
                    selected = currentPath == item.path,
                    onClick = { onNavigate(item) },
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Gold,
                        selectedTextColor = Gold,
                        indicatorColor = CreamDark,
                        unselectedIconColor = SoftBrown,
                        unselectedTextColor = SoftBrown
                    )
                )
            }
        }
    }
}