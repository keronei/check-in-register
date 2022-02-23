package com.keronei.keroscheckin.viewmodels

import androidx.lifecycle.ViewModel
import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.entities.RegionEntity
import kotlinx.coroutines.flow.MutableStateFlow

class ImportExportViewModel : ViewModel() {
    val parsedRegionsToImport = MutableStateFlow(value = mutableListOf<RegionEntity>())

    val parsedMembersToImport = MutableStateFlow(value = mutableListOf<MemberEntity>())
}