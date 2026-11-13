package com.Lutvepo3.Dumnuc.presentation.collection

import androidx.lifecycle.ViewModel
import com.Lutvepo3.Dumnuc.domain.model.CollectibleKind
import com.Lutvepo3.Dumnuc.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CollectionViewModel(
    private val collection: CollectionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CollectionUiState())
    val state: StateFlow<CollectionUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val entries = collection.entries()
        val cells = entries.map { entry ->
            CollectionCell(
                id = entry.item.id,
                nameRes = entry.item.nameRes,
                colorArgb = entry.item.colorArgb,
                livery = entry.item.kind == CollectibleKind.LIVERY,
                unlocked = entry.unlocked,
                equipped = entry.equipped,
                unlockTextRes = entry.item.unlockTextRes,
                unlockValue = entry.item.unlockValue
            )
        }
        _state.value = CollectionUiState(
            liveries = cells.filter { it.livery },
            badges = cells.filter { !it.livery },
            unlocked = entries.count { it.unlocked },
            total = entries.size
        )
    }

    fun equip(itemId: String) {
        collection.equip(itemId)
        refresh()
    }
}
