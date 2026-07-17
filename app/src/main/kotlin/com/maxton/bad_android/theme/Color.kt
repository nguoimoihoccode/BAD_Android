package com.maxton.bad_android.theme

import androidx.compose.ui.graphics.Color

// Flutter AppColors parity
val Primary = Color(0xFF006C49)
val PrimaryContainer = Color(0xFF10B981) // was KineticGreen
val Accent = Color(0xFFFF7E2D)
val Tertiary = Color(0xFF9D4300)
val Background = Color(0xFFF8F9FF)
val Surface = Color(0xFFF8F9FF)
val SurfaceContainer = Color(0xFFE5EEFF)
val OnBackground = Color(0xFF0B1C30)
val OnSurface = Color(0xFF0B1C30)
val OnSurfaceVariant = Color(0xFF3C4A42)
val Outline = Color(0xFF6C7A71)
val OutlineVariant = Color(0xFFBBCABF)
val Error = Color(0xFFBA1A1A)
val ErrorContainer = Color(0xFFFFDAD6)
val OnPrimary = Color(0xFFFFFFFF)

// Back-compat aliases (update call sites gradually)
val KineticGreen = PrimaryContainer
val PremiumDark = OnBackground
val SoftGray = Background
val OutlineGray = OutlineVariant
val ErrorRed = Error
val SecondaryGreen = Color(0xFF059669)
