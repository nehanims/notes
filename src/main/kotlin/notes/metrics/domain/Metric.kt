package notes.metrics.domain

import com.fasterxml.jackson.annotation.JsonProperty

data class Metric (
    @JsonProperty("type")
    val type: String,//TODO make it an enum or from DB
    @JsonProperty("summary")
    val summary: String,
    @JsonProperty("source")
    val source: MetricSource?,
    @JsonProperty("llm")
    val llm: String?,
    @JsonProperty("transcribedText")
    val transcribedText: String?,
    @JsonProperty("transcribedTextRewrite")
    val transcribedTextRewrite: String?,
    @JsonProperty("transcribedFilename")
    val transcribedFilename: String?,
    //TODO replace this with filename instead of URLs - just a hack for now to get the frontend working for now
    @JsonProperty("voiceNoteUrl")
    val voiceNoteFilename: String?


)