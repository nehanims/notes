package notes.metrics.repository;

import notes.metrics.domain.Metric
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface MetricRepository : JpaRepository<Metric, Long> {

    @Query("select g from Metric g where g.transcribedFileId = ?1")
    fun findByTranscribedFileId(transcribedFileId: Long): List<Metric>


    @Query("select m from Metric m where m.transcribedFilename = ?1")
    fun findByTranscribedFilename(transcribedFilename: String): List<Metric>

}