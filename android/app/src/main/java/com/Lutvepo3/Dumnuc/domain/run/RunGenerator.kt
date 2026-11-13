package com.Lutvepo3.Dumnuc.domain.run

import com.Lutvepo3.Dumnuc.core.config.GameConfig
import com.Lutvepo3.Dumnuc.domain.model.Spawn
import com.Lutvepo3.Dumnuc.domain.model.SpawnKind
import com.Lutvepo3.Dumnuc.domain.model.SpawnPlan
import kotlin.random.Random

object RunGenerator {

    private const val MAX_ATTEMPTS = 20
    private const val WAVE_EPSILON = 0.05f

    fun seedFor(stageIndex: Int, runIndex: Int): Int =
        (stageIndex * 7919) xor (runIndex * 104729)

    fun generate(stageIndex: Int, runIndex: Int): SpawnPlan {
        val seed = seedFor(stageIndex, runIndex)
        val interval = GameConfig.hazardIntervalSec(stageIndex)
        for (attempt in 0 until MAX_ATTEMPTS) {
            val random = Random(seed + attempt * 31)
            val hazards = buildHazards(random, interval, stageIndex)
            if (isSolvable(hazards)) {
                val rings = buildRings(random, hazards)
                return SpawnPlan(seed, (hazards + rings).sortedBy { it.arrivalSec }, false)
            }
        }
        val hazards = fallbackHazards(interval)
        val rings = buildRings(Random(seed), hazards)
        return SpawnPlan(seed, (hazards + rings).sortedBy { it.arrivalSec }, true)
    }

    private fun buildHazards(random: Random, interval: Float, stageIndex: Int): List<Spawn> {
        val out = ArrayList<Spawn>()
        out.add(Spawn(GameConfig.FIRST_HAZARD_SEC, 1, SpawnKind.HAZARD))
        var t = GameConfig.FIRST_HAZARD_SEC + interval
        val doubleChance = 0.18f + 0.02f * (stageIndex - 1)
        var previous = intArrayOf(1)
        while (t < GameConfig.SPAWN_HORIZON_SEC) {
            val wide = t > 8f && random.nextFloat() < doubleChance
            val lanes = pickWave(random, wide, previous)
            lanes.forEach { out.add(Spawn(t, it, SpawnKind.HAZARD)) }
            previous = lanes
            t += interval
        }
        return out
    }

    private fun pickWave(random: Random, wide: Boolean, previous: IntArray): IntArray {
        val all = intArrayOf(0, 1, 2)
        if (!wide) {
            val candidates = all.filter { previous.size > 1 || it != previous.firstOrNull() }
            val pool = if (candidates.isEmpty()) all.toList() else candidates
            return intArrayOf(pool[random.nextInt(pool.size)])
        }
        val first = random.nextInt(3)
        var second = random.nextInt(3)
        while (second == first) {
            second = random.nextInt(3)
        }
        return intArrayOf(first, second)
    }

    private fun fallbackHazards(interval: Float): List<Spawn> {
        val order = intArrayOf(1, 0, 2, 1, 2, 0)
        val out = ArrayList<Spawn>()
        var t = GameConfig.FIRST_HAZARD_SEC
        var i = 0
        out.add(Spawn(t, 1, SpawnKind.HAZARD))
        t += interval
        while (t < GameConfig.SPAWN_HORIZON_SEC) {
            out.add(Spawn(t, order[i % order.size], SpawnKind.HAZARD))
            i++
            t += interval
        }
        return out
    }

    private fun buildRings(random: Random, hazards: List<Spawn>): List<Spawn> {
        val out = ArrayList<Spawn>()
        var t = GameConfig.FIRST_RING_SEC
        while (t < GameConfig.SPAWN_HORIZON_SEC) {
            val blocked = hazards.filter { kotlin.math.abs(it.arrivalSec - t) < 0.45f }.map { it.lane }
            val free = (0 until GameConfig.LANE_COUNT).filter { it !in blocked }
            if (free.isNotEmpty()) {
                out.add(Spawn(t, free[random.nextInt(free.size)], SpawnKind.RING))
            }
            t += GameConfig.RING_INTERVAL_SEC
        }
        return out
    }

    fun isSolvable(hazards: List<Spawn>): Boolean {
        val waves = groupWaves(hazards)
        var reachable = setOf(1)
        for (wave in waves) {
            if (wave.blocked.size >= GameConfig.LANE_COUNT) return false
            val next = HashSet<Int>()
            for (lane in 0 until GameConfig.LANE_COUNT) {
                if (lane in wave.blocked) continue
                if (reachable.any { kotlin.math.abs(it - lane) <= 1 }) next.add(lane)
            }
            if (next.isEmpty()) return false
            reachable = next
        }
        return true
    }

    private data class Wave(val time: Float, val blocked: Set<Int>)

    private fun groupWaves(hazards: List<Spawn>): List<Wave> {
        val sorted = hazards.sortedBy { it.arrivalSec }
        val out = ArrayList<Wave>()
        var currentTime = Float.NEGATIVE_INFINITY
        var currentLanes = HashSet<Int>()
        for (spawn in sorted) {
            if (spawn.arrivalSec - currentTime > WAVE_EPSILON) {
                if (currentLanes.isNotEmpty()) out.add(Wave(currentTime, currentLanes))
                currentTime = spawn.arrivalSec
                currentLanes = HashSet()
            }
            currentLanes.add(spawn.lane)
        }
        if (currentLanes.isNotEmpty()) out.add(Wave(currentTime, currentLanes))
        return out
    }
}
