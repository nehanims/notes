package notes.config.websocket

import notes.kafka.model.Metric

data class MetricsMessage(val type: String = "metrics", val audioFilename: String, val metrics: List<Metric>)