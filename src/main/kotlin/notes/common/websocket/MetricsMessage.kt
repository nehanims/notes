package notes.common.websocket

import notes.common.kafka.model.MetricDTO

data class MetricsMessage(val type: String = "metrics", val transcriptionFilename: String, val audioFilename:String, val metrics: List<MetricDTO>)