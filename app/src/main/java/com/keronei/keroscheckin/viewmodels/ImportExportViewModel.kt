package com.keronei.keroscheckin.viewmodels

import androidx.lifecycle.ViewModel
import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.entities.RegionEntity
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.InputStream

class ImportExportViewModel : ViewModel() {
    val parsedRegionsToImport = MutableStateFlow(value = mutableListOf<RegionEntity>())

    val parsedMembersToImport = MutableStateFlow(value = mutableListOf<MemberEntity>())

    val launchedIntentInputStream = MutableStateFlow<InputStream?>(value = null)
}