package net.b0n541.combatsimulator.ui

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import net.b0n541.combatsimulator.generated.resources.Res
import net.b0n541.combatsimulator.generated.resources.immortal
import org.jetbrains.compose.resources.Font

@Composable
fun immortalFontFamily() = FontFamily(
    Font(Res.font.immortal, FontWeight.Normal, FontStyle.Normal),
)

@Composable
fun getTypography(): Typography {
    val immortal = immortalFontFamily()
    val scaleFactor = 1.5f
    val defaultTypography = Typography()
    return Typography(
        displayLarge = defaultTypography.displayLarge.copy(
            fontFamily = immortal,
            fontSize = defaultTypography.displayLarge.fontSize * scaleFactor
        ),
        displayMedium = defaultTypography.displayMedium.copy(
            fontFamily = immortal,
            fontSize = defaultTypography.displayMedium.fontSize * scaleFactor
        ),
        displaySmall = defaultTypography.displaySmall.copy(
            fontFamily = immortal,
            fontSize = defaultTypography.displaySmall.fontSize * scaleFactor
        ),
        headlineLarge = defaultTypography.headlineLarge.copy(
            fontFamily = immortal,
            fontSize = defaultTypography.headlineLarge.fontSize * scaleFactor
        ),
        headlineMedium = defaultTypography.headlineMedium.copy(
            fontFamily = immortal,
            fontSize = defaultTypography.headlineMedium.fontSize * scaleFactor
        ),
        headlineSmall = defaultTypography.headlineSmall.copy(
            fontFamily = immortal,
            fontSize = defaultTypography.headlineSmall.fontSize * scaleFactor
        ),
        titleLarge = defaultTypography.titleLarge.copy(
            fontFamily = immortal,
            fontSize = defaultTypography.titleLarge.fontSize * scaleFactor
        ),
        titleMedium = defaultTypography.titleMedium.copy(
            fontFamily = immortal,
            fontSize = defaultTypography.titleMedium.fontSize * scaleFactor
        ),
        titleSmall = defaultTypography.titleSmall.copy(
            fontFamily = immortal,
            fontSize = defaultTypography.titleSmall.fontSize * scaleFactor
        ),
        bodyLarge = defaultTypography.bodyLarge.copy(
            fontFamily = immortal,
            fontSize = defaultTypography.bodyLarge.fontSize * scaleFactor
        ),
        bodyMedium = defaultTypography.bodyMedium.copy(
            fontFamily = immortal,
            fontSize = defaultTypography.bodyMedium.fontSize * scaleFactor
        ),
        bodySmall = defaultTypography.bodySmall.copy(
            fontFamily = immortal,
            fontSize = defaultTypography.bodySmall.fontSize * scaleFactor
        ),
        labelLarge = defaultTypography.labelLarge.copy(
            fontFamily = immortal,
            fontSize = defaultTypography.labelLarge.fontSize * scaleFactor
        ),
        labelMedium = defaultTypography.labelMedium.copy(
            fontFamily = immortal,
            fontSize = defaultTypography.labelMedium.fontSize * scaleFactor
        ),
        labelSmall = defaultTypography.labelSmall.copy(
            fontFamily = immortal,
            fontSize = defaultTypography.labelSmall.fontSize * scaleFactor
        )
    )
}