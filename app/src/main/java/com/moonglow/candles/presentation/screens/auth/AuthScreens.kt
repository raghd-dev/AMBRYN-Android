package com.moonglow.candles.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import com.moonglow.candles.R
import com.moonglow.candles.core.theme.*
import com.moonglow.candles.data.remote.SupabaseRoleService
import com.moonglow.candles.data.repository.MoonGlowRepository
import com.moonglow.candles.data.repository.SupabaseRestAuthRepository
import com.moonglow.candles.presentation.components.GlowButton
import kotlinx.coroutines.launch

private val emailRegex =
    Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

@Composable
fun LoginScreen(
    repo: MoonGlowRepository,
    onSessionSave: (String, String) -> Unit,
    onSignup: () -> Unit,
    onDone: (String) -> Unit
) {
    val context = LocalContext.current
    val authRepo = remember(context) { SupabaseRestAuthRepository(context.applicationContext) }
    val roleRepo = remember { SupabaseRoleService() }
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("customer") }

    var showPassword by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    AuthShell(
        title = "Welcome Back",
        subtitle = "Choose your role and login"
    ) {
        RoleSelector(
            selectedRole = selectedRole,
            onRoleSelected = { selectedRole = it }
        )

        Spacer(Modifier.height(16.dp))

        MoonGlowTextField(
            value = email,
            onValueChange = {
                email = it.trim()
                error = null
            },
            label = "Email",
            icon = Icons.Outlined.Email
        )

        Spacer(Modifier.height(14.dp))

        MoonGlowTextField(
            value = password,
            onValueChange = {
                password = it
                error = null
            },
            label = "Password",
            icon = Icons.Outlined.Lock,
            isPassword = true,
            showPassword = showPassword,
            onTogglePassword = { showPassword = !showPassword }
        )

        AuthError(error)

        Spacer(Modifier.height(24.dp))

        GlowButton(
            text = if (loading) "Logging in..." else "Login as ${selectedRole.uppercase()}",
            modifier = Modifier.fillMaxWidth()
        ) {
            val cleanEmail = email.trim().lowercase()
            val cleanPassword = password

            when {
                cleanEmail.isBlank() || cleanPassword.isBlank() -> {
                    error = "Please fill all fields."
                }

                !cleanEmail.matches(emailRegex) -> {
                    error = "Please enter a valid email address."
                }

                cleanPassword.length < 6 -> {
                    error = "Password must be at least 6 characters."
                }

                else -> {
                    loading = true
                    error = null

                    scope.launch {
                        authRepo.login(cleanEmail, cleanPassword)
                            .onSuccess {
                                roleRepo.getRole(cleanEmail)
                                    .onSuccess { realRole ->
                                        if (realRole != selectedRole) {
                                            authRepo.clearSession()
                                            loading = false
                                            error = "This account is not registered as $selectedRole."
                                            return@onSuccess
                                        }

                                        val name =
                                            cleanEmail.substringBefore("@")
                                                .replaceFirstChar { it.uppercase() }

                                        repo.setLoggedInUser(name, cleanEmail)
                                        onSessionSave(name, cleanEmail)

                                        loading = false
                                        onDone(realRole)
                                    }
                                    .onFailure {
                                        loading = false
                                        error = it.message
                                    }
                            }
                            .onFailure {
                                loading = false
                                error = cleanError(it.message)
                            }
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        TextButton(
            enabled = !loading,
            onClick = onSignup
        ) {
            Text("Don’t have an account? Sign up", color = SoftBrown)
        }
    }
}

@Composable
fun SignupScreen(
    repo: MoonGlowRepository,
    onSessionSave: (String, String) -> Unit,
    onLogin: () -> Unit,
    onDone: (String) -> Unit
) {
    val context = LocalContext.current
    val authRepo = remember(context) { SupabaseRestAuthRepository(context.applicationContext) }
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var showPassword by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    AuthShell(
        title = "Join AMBRYN",
        subtitle = "Create a customer account"
    ) {
        MoonGlowTextField(
            value = name,
            onValueChange = {
                name = it
                error = null
            },
            label = "Full name",
            icon = Icons.Outlined.Person
        )

        Spacer(Modifier.height(14.dp))

        MoonGlowTextField(
            value = email,
            onValueChange = {
                email = it.trim()
                error = null
            },
            label = "Email",
            icon = Icons.Outlined.Email
        )

        Spacer(Modifier.height(14.dp))

        MoonGlowTextField(
            value = password,
            onValueChange = {
                password = it
                error = null
            },
            label = "Password",
            icon = Icons.Outlined.Lock,
            isPassword = true,
            showPassword = showPassword,
            onTogglePassword = { showPassword = !showPassword }
        )

        AuthError(error)

        Spacer(Modifier.height(24.dp))

        GlowButton(
            text = if (loading) "Creating Account..." else "Create Customer Account",
            modifier = Modifier.fillMaxWidth()
        ) {
            val cleanName = name.trim()
            val cleanEmail = email.trim().lowercase()
            val cleanPassword = password

            when {
                cleanName.isBlank() -> error = "Please enter your name."
                cleanEmail.isBlank() || cleanPassword.isBlank() -> error = "Please fill all fields."
                !cleanEmail.matches(emailRegex) -> error = "Please enter a valid email address."
                cleanPassword.length < 6 -> error = "Password must be at least 6 characters."

                else -> {
                    loading = true
                    error = null

                    scope.launch {
                        authRepo.signup(cleanName, cleanEmail, cleanPassword)
                            .onSuccess {
                                // Do not trust signup as a logged-in session. Supabase may
                                // require email confirmation before login is allowed.
                                loading = false
                                Toast.makeText(
                                    context,
                                    "Account created. Verify your email if requested, then log in.",
                                    Toast.LENGTH_LONG
                                ).show()
                                onDone("customer")
                            }
                            .onFailure {
                                loading = false
                                error = cleanError(it.message)
                            }
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        TextButton(
            enabled = !loading,
            onClick = onLogin
        ) {
            Text("Already have an account? Login", color = SoftBrown)
        }
    }
}

@Composable
private fun RoleSelector(
    selectedRole: String,
    onRoleSelected: (String) -> Unit
) {
    val roles = listOf(
        "customer" to Icons.Outlined.Person,
        "admin" to Icons.Outlined.AdminPanelSettings,
        "support" to Icons.Outlined.SupportAgent
    )

    Column {
        Text(
            text = "Login Role",
            color = Charcoal,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            roles.forEach { role ->
                FilterChip(
                    modifier = Modifier.weight(1f),
                    selected = selectedRole == role.first,
                    onClick = { onRoleSelected(role.first) },
                    label = {
                        Text(
                            text = role.first.replaceFirstChar { it.uppercase() },
                            maxLines = 1,
                            softWrap = false,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun AuthError(error: String?) {
    error?.let {
        Spacer(Modifier.height(12.dp))
        Text(
            text = it,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun AuthShell(
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Cream, Background, CreamDark)
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(34.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(74.dp)
                        .background(
                            Brush.verticalGradient(listOf(GoldLight, GoldDark)),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.brand_mark),
                        contentDescription = "AMBRYN logo",
                        modifier = Modifier.size(50.dp)
                    )
                }

                Spacer(Modifier.height(18.dp))

                Text(
                    "AMBRYN",
                    style = MaterialTheme.typography.displayLarge,
                    color = Charcoal,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Charcoal,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    subtitle,
                    color = SoftBrown,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(26.dp))

                content()
            }
        }
    }
}

@Composable
private fun MoonGlowTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onTogglePassword: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        leadingIcon = {
            Icon(icon, null, tint = GoldDark)
        },
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = onTogglePassword) {
                    Icon(
                        if (showPassword) Icons.Outlined.VisibilityOff
                        else Icons.Outlined.Visibility,
                        null,
                        tint = SoftBrown
                    )
                }
            }
        },
        visualTransformation =
            if (isPassword && !showPassword) PasswordVisualTransformation()
            else VisualTransformation.None,
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Gold,
            unfocusedBorderColor = CreamDark,
            focusedContainerColor = WarmWhite,
            unfocusedContainerColor = WarmWhite
        )
    )
}

private fun cleanError(message: String?): String {
    if (message.isNullOrBlank()) return "Something went wrong. Please try again."

    return try {
        val key = "\"msg\":\""
        val start = message.indexOf(key)

        if (start != -1) {
            val from = start + key.length
            val to = message.indexOf("\"", from)
            if (to != -1) message.substring(from, to) else message
        } else {
            message
        }
    } catch (_: Exception) {
        message
    }
}