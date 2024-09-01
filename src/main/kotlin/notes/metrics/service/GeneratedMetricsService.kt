package notes.metrics.service

import notes.metrics.domain.Metric
import notes.metrics.domain.MetricApprovalStatus
import notes.metrics.repository.MetricRepository
import org.springframework.stereotype.Service

@Service
class GeneratedMetricsService(val repository: MetricRepository) {

    fun getAllMetrics(): List<Metric> {
        return repository.findAll()
    }

    fun getMetrics(transcriptionId: Long): List<Metric> {
        // Get the metrics from the repository
        return repository.findByTranscribedFileId(transcriptionId)
    }

    fun saveMetric(metric: Metric): Metric {
        return repository.save(metric)
    }

    fun approveMetric(metric: Metric): Metric {
        val approvedMetric = metric.withApprovalStatus(MetricApprovalStatus.APPROVED)
        return repository.save(approvedMetric)
    }

    fun rejectMetric(metric: Metric): Metric {
        val rejectedMetric = metric.withApprovalStatus(MetricApprovalStatus.REJECTED)
        return repository.save(rejectedMetric)
    }

    fun getMetricsByTranscribedFilename(transcriptionFilename: String): List<Metric> {
        return repository.findByTranscribedFilename(transcriptionFilename)

    }


}
