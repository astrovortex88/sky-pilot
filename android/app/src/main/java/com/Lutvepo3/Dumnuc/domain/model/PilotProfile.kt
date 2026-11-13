package com.Lutvepo3.Dumnuc.domain.model

enum class Rank(val threshold: Int) {
    CADET(0),
    PILOT(2500),
    ACE(7500),
    CAPTAIN(15000),
    SKY_MARSHAL(30000)
}

data class Callsign(
    val id: String,
    val labelRes: Int
)

data class PilotProfile(
    val callsignId: String,
    val totalPoints: Int,
    val lifetimePoints: Int,
    val runsFlown: Int,
    val bestMultiplier: Float,
    val ringsCollected: Int,
    val stagesCleared: Int,
    val bestStreak: Int,
    val boostsFired: Int,
    val bankedRuns: Int,
    val cratesOpened: Int,
    val secondsFlown: Int
)
