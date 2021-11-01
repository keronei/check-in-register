package com.keronei.keroscheckin.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keronei.domain.entities.RegionEntity
import com.keronei.domain.usecases.*
import com.keronei.domain.usecases.base.UseCaseParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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

    val regionsInformation = MutableStateFlow<List<RegionEntity>>(emptyList())

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

    suspend fun queryAllRegionsWithMembersData() =
        queryAllRegionsWithMembersUseCase(UseCaseParams.Empty)

    fun deleteRegion(defectedRegion: RegionEntity) {
        viewModelScope.launch {
            deleteRegionUseCase(defectedRegion)
        }
    }
}