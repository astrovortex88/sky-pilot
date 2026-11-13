package com.Lutvepo3.Dumnuc.data.local

import android.content.Context
import android.content.SharedPreferences

class SkyPilotPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun int(key: String, fallback: Int): Int = prefs.getInt(key, fallback)

    fun putInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun float(key: String, fallback: Float): Float = prefs.getFloat(key, fallback)

    fun putFloat(key: String, value: Float) {
        prefs.edit().putFloat(key, value).apply()
    }

    fun long(key: String, fallback: Long): Long = prefs.getLong(key, fallback)

    fun putLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    fun bool(key: String, fallback: Boolean): Boolean = prefs.getBoolean(key, fallback)

    fun putBool(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun text(key: String, fallback: String): String = prefs.getString(key, fallback) ?: fallback

    fun putText(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun ids(key: String): Set<String> {
        val raw = text(key, "")
        if (raw.isEmpty()) return emptySet()
        return raw.split(SEPARATOR).filter { it.isNotBlank() }.toSet()
    }

    fun putIds(key: String, value: Set<String>) {
        putText(key, value.joinToString(SEPARATOR))
    }

    fun wipe() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val FILE_NAME = "skypilot_store"
        private const val SEPARATOR = ","

        const val KEY_POINTS = "points"
        const val KEY_LIFETIME_POINTS = "lifetime_points"
        const val KEY_CURRENT_STAGE = "current_stage"
        const val KEY_CLEARED_MASK = "cleared_mask"
        const val KEY_STAGE_BEST = "stage_best_"
        const val KEY_BEST_MULTIPLIER = "best_multiplier"
        const val KEY_BANKED_IN_A_ROW = "banked_in_a_row"
        const val KEY_BEST_STREAK = "best_streak"
        const val KEY_RUN_INDEX = "run_index"
        const val KEY_RUNS_FLOWN = "runs_flown"
        const val KEY_BANKED_RUNS = "banked_runs"
        const val KEY_RINGS = "rings_collected"
        const val KEY_BOOSTS = "boosts_fired"
        const val KEY_SECONDS = "seconds_flown"
        const val KEY_CRATES_OPENED = "crates_opened"
        const val KEY_CALLSIGN = "callsign"
        const val KEY_UNLOCKED = "unlocked_items"
        const val KEY_EQUIPPED_LIVERY = "equipped_livery"
        const val KEY_EQUIPPED_BADGE = "equipped_badge"
        const val KEY_CRATES_STANDARD = "crates_standard"
        const val KEY_CRATES_GOLD = "crates_gold"
        const val KEY_CRATE_DAY = "crate_day"
        const val KEY_CRATES_TODAY = "crates_today"
        const val KEY_CRATE_POINTS_TODAY = "crate_points_today"
        const val KEY_GOAL_DAY = "goal_day"
        const val KEY_GOAL_PROGRESS = "goal_progress_"
        const val KEY_GOAL_CLAIMED = "goal_claimed_"
        const val KEY_CONTROL_SIDE_LEFT = "control_side_left"
        const val KEY_ASSIST_MODE = "assist_mode"
        const val KEY_AUTO_CASH_OUT = "auto_cash_out"
        const val KEY_REDUCED_MOTION = "reduced_motion"
        const val KEY_ONBOARDING_SEEN = "onboarding_seen"
        const val KEY_STORE_READY = "store_ready"
    }
}
