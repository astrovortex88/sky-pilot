package com.Lutvepo3.Dumnuc.domain.usecase

import com.Lutvepo3.Dumnuc.domain.model.Rank

class ResolveRankUseCase {

    operator fun invoke(lifetimePoints: Int): Rank =
        Rank.entries.last { lifetimePoints >= it.threshold }

    fun next(rank: Rank): Rank? {
        val index = Rank.entries.indexOf(rank)
        return if (index < Rank.entries.size - 1) Rank.entries[index + 1] else null
    }

    fun pointsToNext(lifetimePoints: Int): Int {
        val target = next(invoke(lifetimePoints)) ?: return 0
        return (target.threshold - lifetimePoints).coerceAtLeast(0)
    }
}
