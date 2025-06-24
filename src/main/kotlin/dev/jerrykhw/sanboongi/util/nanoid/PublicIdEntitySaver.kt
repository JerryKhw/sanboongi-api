package dev.jerrykhw.sanboongi.util.nanoid
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component

@Component
class PublicIdEntitySaver {

    companion object {
        private const val MAX_RETRY = 5
    }

    fun <T> saveWithNanoIdRetry(
        repo: JpaRepository<T, *>,
        entity: T,
    ): T where T : PublicIdEntity {
        var lastException: Exception? = null

        repeat(MAX_RETRY) { attempt ->
            if (attempt > 0) {
                entity.publicId = NanoId.generate()
            }

            try {
                return repo.save(entity)
            } catch (ex: DataIntegrityViolationException) {
                val rootMessage = ex.rootCause?.message ?: ""

                if (!rootMessage.contains("public_id", ignoreCase = true)) {
                    throw ex
                }

                lastException = ex
            }
        }

        throw IllegalStateException("duplicated_public_id", lastException)
    }
}
