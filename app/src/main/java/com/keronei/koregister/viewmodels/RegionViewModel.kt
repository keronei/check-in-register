package com.keronei.koregister.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keronei.domain.entities.RegionEntity
import com.keronei.domain.usecases.CreateRegionUseCase
import com.keronei.domain.usecases.DeleteRegionUseCase
import com.keronei.domain.usecases.QueryAllRegionsUseCase
import com.keronei.domain.usecases.UpdateRegionUseCase
import com.keronei.domain.usecases.base.UseCaseParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegionViewModel @Inject constructor (
    private val createRegionUseCase: CreateRegionUseCase,
    private val updateRegionUseCase: UpdateRegionUseCase,
    private val deleteRegionUseCase: DeleteRegionUseCase,
    private val queryAllRegionsUseCase: QueryAllRegionsUseCase
) : ViewModel() {

    init {

    }

    fun createRegion(newRegion : RegionEntity){
        viewModelScope.launch {
            createRegionUseCase(newRegion)
        }
    }

    fun updateRegion(updatedRegion : RegionEntity){
        viewModelScope.launch {
            updateRegionUseCase(updatedRegion)
        }
    }

    suspend fun queryAllRegions() = queryAllRegionsUseCase(UseCaseParams.Empty)

    fun deleteRegion(defectedRegion : RegionEntity) {
        viewModelScope.launch {
            deleteRegionUseCase(defectedRegion)
        }
    }
}