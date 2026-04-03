package edu.nd.pmcburne.hello

class PlacemarkRepository(private val dao: PlacemarkDao) {

    suspend fun syncAndGetAll(): List<PlacemarkEntity> {
        val remote = RetrofitInstance.api.getPlacemarks()
        val entities = remote.map {
            PlacemarkEntity(
                id = it.id,
                name = it.name,
                tags = it.tag_list.joinToString(","),
                description = it.description,
                latitude = it.visual_center.latitude,
                longitude = it.visual_center.longitude
            )
        }
        dao.insertAll(entities)
        return dao.getAll()
    }
}