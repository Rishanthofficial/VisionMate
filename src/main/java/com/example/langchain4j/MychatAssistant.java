package com.example.langchain4j;

// No StreamingResponseHandler import needed anymore for blocking
// import dev.langchain4j.model.StreamingResponseHandler;

public interface MychatAssistant {
    String chat(String userInput);
}