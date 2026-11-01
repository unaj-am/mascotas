package com.app.core.data.repository

import com.app.core.data.datasource.OsrmMapMatchingRemoteDataSource
import com.app.core.domain.model.MatchedRoute
import com.app.core.domain.model.WalkLocation
import com.app.core.domain.repository.MapMatchingRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException

@Singleton
class MapMatchingRepositoryImpl @Inject constructor(
    private val osrmMapMatchingRemoteDataSource: OsrmMapMatchingRemoteDataSource
) : MapMatchingRepository {

    override suspend fun matchRoute(
        locations: List<WalkLocation>
    ): MatchedRoute? {
        if (locations.size < 2) {
            return null
        }

        return try {
            osrmMapMatchingRemoteDataSource.matchRoute(locations)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            null
        }
    }
}