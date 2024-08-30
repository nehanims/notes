package notes.metrics.controller

import notes.metrics.domain.Metric

import notes.metrics.service.GeneratedMetricsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/metric")
class MetricsController(private val service: GeneratedMetricsService) {

    @GetMapping("/all")
    fun getAllMetrics(transcriptionId: Long): List<Metric> {
        return service.getAllMetrics()
    }

    @GetMapping("/{transcriptionId}")
    fun getMetrics(transcriptionId: Long): List<Metric> {
        return service.getMetrics(transcriptionId)
    }

    @GetMapping("/{transcriptionFilename}")//TODO remove this - use IDs everywhere instead of filenames
    fun getMetricsByTranscriptionFilename(transcriptionFilename: String): List<Metric> {
        return service.getMetricsByTranscriptionFilename(transcriptionFilename)
    }

    @PostMapping("/approve")
    fun approveMetric(@RequestBody metric : Metric): Metric {
        return service.approveMetric(metric)
    }

    @PostMapping("/update")
    fun updatedMetric(@RequestBody metric : Metric): Metric {
        return service.saveMetric(metric)
    }

    @PostMapping("/reject")
    fun rejectMetric(@RequestBody metric : Metric): Metric {
        return service.rejectMetric(metric)
    }



}