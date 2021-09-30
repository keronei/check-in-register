package com.keronei.data.remote

import com.keronei.data.remote.Constants.AUTH_ENDPOINT
import com.keronei.data.remote.Constants.LAST_SYNC_TIME
import com.keronei.data.remote.Constants.SYNC_ENDPOINT
import com.keronei.data.remote.entities.LastSyncQueryResponse
import com.keronei.data.remote.entities.SyncResponse
import com.keronei.data.remote.entities.UserAuthResponse
import retrofit2.http.GET
import retrofit2.http.POST

interface SyncService {

    @GET(LAST_SYNC_TIME)
    suspend fun determineLastSyncTime() : LastSyncQueryResponse

    @POST(SYNC_ENDPOINT)
    suspend fun syncLatestData() : SyncResponse

    @POST(AUTH_ENDPOINT)
    suspend fun authenticate() : UserAuthResponse

}