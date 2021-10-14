package com.keronei.koregister.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keronei.domain.entities.RegionEntity
import com.keronei.domain.usecases.*
import com.keronei.domain.usecases.base.UseCaseParams
import com.keronei.koregister.models.RegionPresentation
import com.keronei.koregister.models.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegionViewModel @Inject constructor(
    private val createRegionUseCase: CreateRegionUseCase,
    private val updateRegionUseCase: UpdateRegionUseCase,
    private val deleteRegionUseCase: DeleteRegionUseCase,
    private val queryAllRegionsUseCase: QueryAllRegionsUseCase,
    private val queryAllRegionsWithMembersUseCase: QueryAllRegionsWithMembersUseCase
) : ViewModel() {

    val regionsInformation = MutableStateFlow<List<RegionPresentation>>(emptyList())
    init {

    }

    fun createRegion(newRegion: RegionEntity) {
        viewModelScope.launch {
            createRegionUseCase(newRegion)
        }
    }

    fun updateRegion(updatedRegion: RegionEntity) {
        viewModelScope.launch {
            updateRegionUseCase(updatedRegion)
        }
    }

    suspend fun queryAllRegions() = queryAllRegionsUseCase(UseCaseParams.Empty)

    suspend fun queryAllRegionsWithMembersData() {
        queryAllRegionsWithMembersUseCase(UseCaseParams.Empty).collect { info ->
            val asPresentations = mutableListOf<RegionPresentation>()

            info.forEach { regionEmbedEntity -> asPresentations.add(regionEmbedEntity.toPresentation()) }

            regionsInformation.emit(asPresentations)

        }
    }

    fun deleteRegion(defectedRegion: RegionEntity) {
        viewModelScope.launch {
            deleteRegionUseCase(defectedRegion)
        }
    }
}