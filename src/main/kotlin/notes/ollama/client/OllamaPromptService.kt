package notes.ollama.client

import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class OllamaPromptService {

    fun getMetricPrompt(transcribedText:String): String {
        val time=LocalDateTime.now().toString()
        val entityList = getEntityList()
        return """
            You are a personal assistant that helps identify specific entities like $entityList in the following text, and return each identified class as separate JSON objects with the following keys: "type" and "summary", where type is the type of entity being referred to: {$entityList}, and summary is the summary of that entity: a brief summary of the entity identified in the text. Your response should be a list of JSON objects as described above only and no other text.
            
            Text to parse:$transcribedText
        """.trimIndent()
    }

    private fun getEntityList(): String {
        return "medication, symptom, medical procedure, therapeutic intervention, activity, diet, dietary supplement, appointment"
    }

    fun getRewritePrompt(textToRewrite: String) =
        "Rewrite the following text for clarity and readability and return the rewritten text : $textToRewrite"

    fun getModelForRewrite() =
        "llama3"//TODO: make the model configurable

    fun getModelForMetricExtraction() =
        "llama3"//TODO: make the model configurable
}
//TODO give examples of the expected JSON response, take a few examples from Claude and show how the JSON should look. so that hte examples don't become too complex, maybe identify different metrics in different prompts
//Prompts
//v1 : You are a personal assistant to a person suffering from a chronic illness. To keep track of their health and the effects of their diet,activity-level, medication and supplementation they are tracking various metrics like physical or neurological symptoms like memory issues, concentration probelms, tinnitus, chest pain, headache, cough, sore throat, fatigure PEM, etc . They are also tracking their dietary, medication and supplement intake. They also would like to keep track of other therapeutic interventions like Red light therapy, fasting, breathing exercises, cold water therapy. They would like to keep track of each of these metrics as separate entities in tables for future trend analysis to see if their diet/activities/therapeutic interventions affect their symptoms. You are a helpful attentive assistant that helps identify specific entities like medications, symptoms, or medical procedures in the following text input from the person, and return each identified class as separate JSON objects with the attributes: type of entity being referred to:diet/medications/symptoms/medical procedures/therapeutic intervention/activity (maybe provide a tool to detect if something is an entity worth consideration?),summary of entity: a brief summary of the entity identified in the text, timestamp provided to you(provide a timestamp from the voice note)Text to parse:{I have a headache and have been nauseous all day. Also have some chest pain. i have a doctors appointment this saturday}