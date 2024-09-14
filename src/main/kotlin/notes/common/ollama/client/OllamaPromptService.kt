package notes.common.ollama.client

import notes.common.auth.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class OllamaPromptService(@Autowired private val authService: AuthService) {

    fun getMetricPrompt(transcribedText:String): String {
        val time=LocalDateTime.now().toString()
        val entityList = getEntityList()
        return """
            You are a personal assistant that helps identify specific entities like $entityList in the following text, and return each identified class as separate JSON objects with the following keys: "type" and "summary", where type is the type of entity being referred to: {$entityList}, and summary is the summary of that entity: a brief summary of the entity identified in the text. Your response should be a list of JSON objects as described above only and no other text.
            
            Text to parse:$transcribedText
        """.trimIndent()
    }

    private fun getEntityList(): String {
        return "PERSON, MEDICATION, SYMPTOM, MEDICAL PROCEDURE, THERAPEUTIC INTERVENTION, ACTIVITY, DIET, INGESTED BEVERAGES, DIETARY SUPPLEMENT, APPOINTMENT"
    }

    fun getRewritePrompt(textToRewrite: String) =
        "Rewrite the following text for clarity and readability and return the rewritten text : $textToRewrite"

    fun getModelForRewrite() =
        "llama3"//TODO: make the model configurable

    fun getModelForMetricExtraction() =
        "llama3"//TODO: make the model configurable


    fun getModelForMetricExtractionUsingNER() =
        //"sciphi/triplex"
        "llama3"

    private fun getEntitiesForNER(): String {
        return "PERSON, MEDICATION, SYMPTOM, MEDICAL PROCEDURE, THERAPEUTIC INTERVENTION, ACTIVITY, DIET, INGESTED BEVERAGES, DIETARY SUPPLEMENT, APPOINTMENT"
    }
    private fun getPredicates(): List<String> {
        return listOf("has_symptom", "took_medication", "did_activity", "ate", "drank", "took_supplement", "has_appointment")
    }

    fun getMetricPromptForNER(transcribedText: String): String {
        val time=LocalDateTime.now().toString()
        val entityTypes = getEntitiesForNER()
        val predicates = getPredicates()
        val username=authService.getUsername()
        return """
        Perform Named Entity Recognition (NER) and extract knowledge graph triplets from the text in backticks. 
        NER identifies named entities of given entity types, and triple extraction identifies relationships between entities using specified predicates. 
        Replace any first person pronouns with the name of the user {$username}.
        Your response should be 2 JSON lists:
        1) a list of entities as JSON objects, where each object contains the following keys: "entity_type", "value". 
        The entity_type is the type of entity being referred to: {$entityTypes}, and the value is the specific entity identified in the text. 
        2) a list of triplets as JSON objects, where each object contains the following keys: "subject", "predicate", "object". 
        The subject is the value of the subject entity, the object is the value of the object entity and the predicate is the relationship between the subject and the object entities, one of: {$predicates}

        **Entity Types:**
        {$entityTypes}

        **Predicates:**
        {$predicates}

        **Text:**
        ```$transcribedText```
        
        Do not include any other text in your response other than the 2 JSON lists.
        
        e.g.
        
        **Text:**
        ```Yesterday I ate eggs and steak for dinner. I had green tea and spice tea today. I ran on the treadmill for 30 minutes. I had blood drawn yesterday. I vacuumed and mopped the floor in the kitchen at the dining room yesterday.```
        
        **Expected Response:**
        
        ```[
          [
             {
               "entity_type": "PERSON",
               "value": "Neha"
             },
             {
               "entity_type": "DIET",
               "value": "eggs"
             },
             {
               "entity_type": "DIET",
               "value": "steak"
             },
             {
               "entity_type": "INGESTED BEVERAGES",
               "value": "green tea"
             },
             {
               "entity_type": "INGESTED BEVERAGES",
               "value": "spice tea"
             },
             {
               "entity_type": "ACTIVITY",
               "value": "ran on the treadmill"
             },
             {
               "entity_type": "MEDICAL PROCEDURE",
               "value": "blood drawn"
             },
             {
               "entity_type": "ACTIVITY",
               "value": "vacuumed"
             },
             {
               "entity_type": "ACTIVITY",
               "value": "mopped the floor"
             }
           ], 
           [
             {
               "subject": "Neha",
               "predicate": "ate",
               "object": "eggs"
             },
             {
               "subject": "Neha",
               "predicate": "ate",
               "object": "steak"
             },
             {
               "subject": "Neha",
               "predicate": "drank",
               "object": "green tea"
             },
             {
               "subject": "Neha",
               "predicate": "drank",
               "object": "spice tea"
             },
             {
               "subject": "Neha",
               "predicate": "did_activity",
               "object": "ran on the treadmill"
             },
             {
               "subject": "Neha",
               "predicate": "did_activity",
               "object": "vacuumed"
             },
             {
               "subject": "Neha",
               "predicate": "did_activity",
               "object": "mopped the floor"
             }
           ]
        ]
        ```
        """.trimIndent()
    }

}
//TODO give examples of the expected JSON response, take a few examples from Claude and show how the JSON should look. so that hte examples don't become too complex, maybe identify different metrics in different prompts
//Prompts
//v1 : You are a personal assistant to a person suffering from a chronic illness. To keep track of their health and the effects of their diet,activity-level, medication and supplementation they are tracking various metrics like physical or neurological symptoms like memory issues, concentration probelms, tinnitus, chest pain, headache, cough, sore throat, fatigure PEM, etc . They are also tracking their dietary, medication and supplement intake. They also would like to keep track of other therapeutic interventions like Red light therapy, fasting, breathing exercises, cold water therapy. They would like to keep track of each of these metrics as separate entities in tables for future trend analysis to see if their diet/activities/therapeutic interventions affect their symptoms. You are a helpful attentive assistant that helps identify specific entities like medications, symptoms, or medical procedures in the following text input from the person, and return each identified class as separate JSON objects with the attributes: type of entity being referred to:diet/medications/symptoms/medical procedures/therapeutic intervention/activity (maybe provide a tool to detect if something is an entity worth consideration?),summary of entity: a brief summary of the entity identified in the text, timestamp provided to you(provide a timestamp from the voice note)Text to parse:{I have a headache and have been nauseous all day. Also have some chest pain. i have a doctors appointment this saturday}