package com.Lutvepo3.Dumnuc.domain.usecase

import com.Lutvepo3.Dumnuc.core.config.GameConfig
import com.Lutvepo3.Dumnuc.domain.model.ActiveSpawn
import com.Lutvepo3.Dumnuc.domain.model.RunState
import com.Lutvepo3.Dumnuc.domain.run.RunGenerator

class StartRunUseCase {

    operator fun invoke(
        stageIndex: Int,
        runIndex: Int,
        assistMode: Boolean,
        autoCashOut: Float
    ): RunState {
        val plan = RunGenerator.generate(stageIndex, runIndex)
        val spawns = plan.spawns.mapIndexed { index, spawn ->
            ActiveSpawn(
                id = index,
                arrivalSec = spawn.arrivalSec,
                lane = spawn.lane,
                kind = spawn.kind
            )
        }
        return RunState(
            stageIndex = stageIndex,
            seed = plan.seed,
            assistMode = assistMode,
            autoCashOut = autoCashOut,
            fuelSec = GameConfig.RUN_FUEL_SECONDS,
            multiplier = GameConfig.START_MULTIPLIER,
            spawns = spawns
        )
    }
}
