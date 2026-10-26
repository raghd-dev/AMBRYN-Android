package com.moonglow.candles.presentation.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moonglow.candles.R
import com.moonglow.candles.core.theme.*
import com.moonglow.candles.presentation.components.GlowButton

@Composable
fun OnboardingScreen(
    onStart: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Cream,
                        Background,
                        CreamDark
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = CardWhite
            ),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Your Logo Image
                Image(
                    painter = painterResource(id = R.drawable.brand_lockup),
                    contentDescription = "AMBRYN Logo",
                    modifier = Modifier
                        .width(255.dp).height(170.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "CANDLES & RITUALS",
                    style = MaterialTheme.typography.titleMedium,
                    color = SoftBrown
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Shop premium candles, book workshops, and create beautiful moments.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = SoftBrown,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(34.dp))

                GlowButton(
                    text = "Start Shopping",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    onStart()
                }
            }
        }
    }
}