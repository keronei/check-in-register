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
    private val regionsUseCases: RegionsUseCases
) : ViewModel() {

    val regionsInformation = MutableStateFlow<List<RegionEntity>>(emptyList())

    init {

    }

    fun createRegion(newRegion: List<RegionEntity>) {
        viewModelScope.launch {
            regionsUseCases.createRegionUseCase(newRegion)
        }
    }

    fun updateRegion(updatedRegion: RegionEntity) {
        viewModelScope.launch {
            regionsUseCases.updateRegionUseCase(updatedRegion)
        }
    }

    suspend fun queryAllRegions() = regionsUseCases.queryAllRegionsUseCase(UseCaseParams.Empty)

    suspend fun queryAllRegionsWithMembersData() =
        regionsUseCases.queryAllRegionsWithMembersUseCase(UseCaseParams.Empty)

    fun deleteRegion(defectedRegion: RegionEntity) {
        viewModelScope.launch {
            regionsUseCases.deleteRegionUseCase(defectedRegion)
        }
    }

    fun deleteAllRegions(){
        viewModelScope.launch {
            regionsUseCases.deleteAllRegionsUseCase(Unit)
        }
    }
}