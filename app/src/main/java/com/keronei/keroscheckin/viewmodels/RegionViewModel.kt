package com.keronei.keroscheckin.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keronei.domain.entities.RegionEntity
import com.keronei.domain.usecases.RegionsUseCases
import com.keronei.domain.usecases.base.UseCaseParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RegionViewModel @Inject constructor(
    private val regionsUseCases: RegionsUseCases
) : ViewModel() {

    val regionsInformation = MutableStateFlow<List<RegionEntity>>(emptyList())

    init {

    }

    suspend fun createRegion(newRegion: RegionEntity) : Long {
       return withContext(viewModelScope.coroutineContext) {
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

    suspend fun deleteRegion(defectedRegion: RegionEntity): Int {
        return withContext(viewModelScope.coroutineContext) {
            regionsUseCases.deleteRegionUseCase(defectedRegion)
        }
    }

    suspend fun deleteAllRegions(deletableRegions : List<RegionEntity>): Int {
        return withContext(viewModelScope.coroutineContext) {
            regionsUseCases.deleteAllRegionsUseCase(deletableRegions)
        }
    }
}