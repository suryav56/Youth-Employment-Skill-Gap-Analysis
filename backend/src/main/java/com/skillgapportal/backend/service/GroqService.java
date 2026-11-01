package com.skillgapportal.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GroqService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateRecommendation(String prompt) {
        if (apiKey == null || apiKey.trim().isEmpty() || apiKey.contains("placeholder_api_key")) {
            return generateLocalFallback(prompt);
        }

        try {
            RestClient restClient = RestClient.builder()
                    .baseUrl(apiUrl)
                    .defaultHeader("Authorization", "Bearer " + apiKey)
                    .defaultHeader("Content-Type", "application/json")
                    .build();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "llama3-8b-8192");
            requestBody.put("temperature", 0.3);

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);

            requestBody.put("messages", messages);

            String response = restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            // Parse response JSON to extract text
            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> choice = choices.get(0);
                Map<String, Object> message = (Map<String, Object>) choice.get("message");
                if (message != null) {
                    return (String) message.get("content");
                }
            }
            return response;
        } catch (Exception e) {
            // Log the error and use local fallback
            System.err.println("Groq API Call Failed: " + e.getMessage());
            return generateLocalFallback(prompt);
        }
    }

    private String generateLocalFallback(String prompt) {
        // Parse the prompt slightly to understand if it is a career roadmap request
        String lowerPrompt = prompt.toLowerCase();
        
        if (lowerPrompt.contains("frontend")) {
            return """
            ### 🚀 Personalized Frontend Developer Learning Roadmap
            
            Based on your current skill gaps, here is your customized learning path:
            
            #### 1. Core Language Mastery (Weeks 1-2)
            * **Focus**: HTML5, Semantic Markup, CSS3 Grid/Flexbox, and Modern ES6+ JavaScript.
            * **Action Item**: Complete a personal portfolio site using raw HTML/CSS.
            * **Resource**: *Modern JavaScript & CSS Foundations* (YouTube/freeCodeCamp).
            
            #### 2. Component-Based Architecture (Weeks 3-5)
            * **Focus**: React.js - hooks, state management (Context API), and routing.
            * **Action Item**: Recreate a dashboard interface with dynamic sorting.
            * **Resource**: *React Complete Guide* (Udemy/Coursera).
            
            #### 3. Styling & Tooling (Weeks 6-8)
            * **Focus**: Tailwind CSS and TypeScript integration.
            * **Action Item**: Convert your portfolio to TypeScript and utilize Tailwind utility classes.
            * **Resource**: *Tailwind CSS & TS Mastery* (Coursera).
            
            ---
            **Target Careers**: Frontend Engineer, UI Developer, React Consultant
            **Missing Skills to Acquire**: React, TypeScript, Responsive CSS
            """;
        } else if (lowerPrompt.contains("backend")) {
            return """
            ### ⚙️ Personalized Backend Developer Learning Roadmap
            
            Based on your current skill gaps, here is your customized learning path:
            
            #### 1. Language Fundamentals (Weeks 1-3)
            * **Focus**: Java 21 features (pattern matching, records, virtual threads) and Object-Oriented principles.
            * **Action Item**: Build a CLI task manager using native Java collections.
            * **Resource**: *Java Programming Masterclass* (Udemy).
            
            #### 2. Web Development with Spring (Weeks 4-6)
            * **Focus**: Spring Boot starters, REST Controller architecture, and dependency injection.
            * **Action Item**: Implement a CRUD API for a bookstore backend.
            * **Resource**: *Spring Boot Fundamentals* (Udemy/Spring.io).
            
            #### 3. Data Access & Security (Weeks 7-9)
            * **Focus**: Spring Data JPA, Hibernate, MySQL optimization, and Spring Security with JWT.
            * **Action Item**: Secure your bookstore API with custom roles and JWT filters.
            * **Resource**: *Spring Security in Action* (YouTube/Coursera).
            
            ---
            **Target Careers**: Java Backend Engineer, API Specialist, Systems Engineer
            **Missing Skills to Acquire**: Spring Boot, JPA/Hibernate, Spring Security (JWT)
            """;
        } else {
            return """
            ### 📈 Personalized Technology Learning Roadmap
            
            Based on your profile, here is your customized development path:
            
            #### 1. Core Technical Foundations (Weeks 1-2)
            * **Focus**: Version Control (Git/GitHub), Terminal Operations, and Markdown documentation.
            * **Action Item**: Create a GitHub account, set up a repository, and push commits.
            
            #### 2. Scripting & Database Foundations (Weeks 3-5)
            * **Focus**: Python fundamentals and relational databases (SQL).
            * **Action Item**: Write a Python script to query a database and extract records.
            
            #### 3. Professional Methodologies (Weeks 6-8)
            * **Focus**: Agile software lifecycle, Scrum, and Jira tracking.
            * **Action Item**: Configure a Kanban board to manage your learning tasks.
            
            ---
            **Target Careers**: Junior Full Stack Developer, Systems Associate
            **Missing Skills to Acquire**: Python, Git, SQL Database Essentials
            """;
        }
    }
}
