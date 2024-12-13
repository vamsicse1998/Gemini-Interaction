package com.ortech.bookstore.catalog.service.impl;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.api.SafetySetting;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class GeminiChatService {
    private static final String PROJECT_ID = "build-blog-event";
    private static final String LOCATION = "us-central1";

    public String callGemini(String query) throws IOException {
        try (VertexAI vertexAi = new VertexAI(PROJECT_ID, LOCATION);) {
            String context=
            """
            Hi You are SQL Developer expert your role here is Write a SQL Query based on user requirement we are using Postgres SQL DB
             and Here is our table structure: create sequence product_id_seq start with 1 increment by 50;
                                                      create table products
                                                      (
                                                      id bigint default nextval('product_id_seq') not null,
                                                      code text not null,
                                                      name text not null,
                                                      description text,
                                                      image_url text,
                                                      price numeric not null,
                                                      quantity BIGINT,
                                                      primary key(id)
                                                      );
            Make sure you need to respond only SQL query in response no extra spaces nor extra characters reason I am executing
             straight away same response to DB Make sure you don't use any SQL Injection Mechanisms. 
             Same Prompt: "which is highest price item"
             Sample Response: "SELECT * FROM products ORDER BY price DESC LIMIT 1";
             prompt:        
            """;
            GenerativeModel model = new GenerativeModel("gemini-2.0-flash-exp", vertexAi);

            GenerateContentResponse response = model.generateContent(context+query);
            log.info("Gemini API call: {}",query);
            return response.getCandidates(0).getContent().getParts(0).getText();


        }
    }
    public String callModel(String query) throws IOException {
        try (VertexAI vertexAi = new VertexAI("build-blog-event", "us-central1");) {
            GenerationConfig generationConfig =
                    GenerationConfig.newBuilder()
                            .setMaxOutputTokens(8192)
                            .setTemperature(1F)
                            .setTopP(0.95F)
                            .build();
            List<SafetySetting> safetySettings = Collections.emptyList();
                /*SafetySetting.newBuilder()
                        .setCategory(HarmCategory.HARM_CATEGORY_HATE_SPEECH)
                        .setThreshold(SafetySetting.HarmBlockThreshold.OFF)
                        .build(),
                SafetySetting.newBuilder()
                        .setCategory(HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
                        .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_LOW_AND_ABOVE_VALUE)
                        .build(),
                SafetySetting.newBuilder()
                        .setCategory(HarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT)
                        .setThreshold(SafetySetting.HarmBlockThreshold.OFF)
                        .build(),
                SafetySetting.newBuilder()
                        .setCategory(HarmCategory.HARM_CATEGORY_HARASSMENT)
                        .setThreshold(SafetySetting.HarmBlockThreshold.OFF)
                        .build()*/
            GenerativeModel model =
                    new GenerativeModel.Builder()
                            .setModelName("gemini-1.5-flash-002")
                            .setVertexAi(vertexAi)
                            .setGenerationConfig(generationConfig)
                            .setSafetySettings(safetySettings)
                            .build();


            // For multi-turn responses, start a chat session.
            ChatSession chatSession = model.startChat();

            GenerateContentResponse response = model.generateContent(query);
            return response.getCandidates(0).getContent().getParts(0).getText();
        }
    }

}
