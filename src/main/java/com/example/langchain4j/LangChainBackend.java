package com.example.langchain4j;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.model.chat.ChatModel;
// No longer need to import specific request classes if not using them directly,
// but ChatResponse is still needed
import dev.langchain4j.model.chat.response.ChatResponse;
// These imports are still needed because the builder uses them
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.model.output.FinishReason;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


import java.util.ArrayList;
import java.util.List;
import java.util.Collections; // For emptyList()
import java.util.Map;
import java.util.HashMap;

// MychatAssistant is in its own file: MychatAssistant.java
// Its content should be:
/*
package com.example.langchain4j;

public interface MychatAssistant {
    String chat(String userInput);
}
*/

public class LangChainBackend {
    private final ChatModel model;
    private final ChatMemory chatMemory;
    private final MychatAssistant assistant;
    private Map<Integer, String> lastListedEventIds = new HashMap<>();
    private final GoogleCalendarService calendarService;
    private Map<String, String> lastListedEventTitlesToIds = new HashMap<>();

    public LangChainBackend() {
        try {
            model = OllamaChatModel.builder()
                    .baseUrl("http://localhost:11434")
                    .modelName("granite3.2-vision")
                    .build();

            chatMemory = MessageWindowChatMemory.withMaxMessages(50);
            calendarService = new GoogleCalendarService();
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
            String systemPrompt = "You are a helpful assistant Always treat the user with respect call him Boss Today's date is " + today + ".  " +
                    "When listing calendar events, always show the event ID and tell the user to use this ID for deletion. " +
                    "If the user wants to delete an event, ask for the event ID.";




            this.assistant = AiServices.builder(MychatAssistant.class)
                    .chatModel(model)
                    .chatMemory(chatMemory)
                    .systemMessageProvider(chatMemoryId -> systemPrompt)
                    .tools(new EmailTool())
                    .tools(new CalendarTool())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ChatResponse sendMessage(String userInput, byte[] imageData) {
        ChatResponse response;

        List<dev.langchain4j.data.message.Content> contents = new ArrayList<>();
        contents.add(TextContent.from(userInput));

        if (imageData != null && imageData.length > 0) {
            Image image = Image.builder()
                    .base64Data(java.util.Base64.getEncoder().encodeToString(imageData))
                    .mimeType("image/jpeg")
                    .build();
            contents.add(ImageContent.from(image));
        }

        UserMessage userMessageWithImage = UserMessage.from(contents);
        chatMemory.add(userMessageWithImage);

        List<ChatMessage> messages = chatMemory.messages();

        if (imageData != null && imageData.length > 0) {
            response = ((OllamaChatModel) model).chat(messages);
            chatMemory.add(response.aiMessage());
        } else {
            String aiResponseContent = assistant.chat(userInput);

            AiMessage aiMessage = AiMessage.from(aiResponseContent);

            // FIX: Use ChatResponse.builder() as per the documentation!
            response = ChatResponse.builder()
                    .aiMessage(aiMessage)
                    .tokenUsage(new TokenUsage(0, 0)) // Provide dummy token usage
                    // toolExecutionRequests is optional for the builder; it defaults to empty if not set
                    // .toolExecutionRequests(Collections.emptyList()) // Can explicitly set, or omit if builder handles default
                    .finishReason(FinishReason.OTHER) // Provide a default finish reason
                    .build();
            
            // Note: AiServices.chat(String) with chatMemory usually adds the AI message automatically.
            // If you find duplicate AI messages in your chat memory printout, remove this line:
            // chatMemory.add(aiMessage);
        }
        return response;
    }

    // For text-only input, return String
    public String sendMessage(String userInput) {
        return assistant.chat(userInput);
    }

    public ChatMemory getChatMemory() {
        return chatMemory;
    }

    public String listEventsAndStoreIds() throws Exception {
        List<String> events = calendarService.listUpcomingEventsWithIds();
        lastListedEventIds.clear();
        lastListedEventTitlesToIds.clear();
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (String event : events) {
            // Extract event ID and title from the string (assuming format "ID: <id> | <title> at <when>")
            String[] parts = event.split(" \\| ");
            String eventId = parts[0].replace("ID: ", "").trim();
            String title = parts.length > 1 ? parts[1].split(" at ")[0].trim() : "";
            lastListedEventIds.put(i, eventId);
            if (!title.isEmpty()) {
                lastListedEventTitlesToIds.put(title.toLowerCase(), eventId);
            }
            sb.append(i).append(". ").append(event).append("\n");
            i++;
        }
        return sb.toString();
    }

    // Then, for deletion:
    public String deleteEventByNumber(int number) throws Exception {
        String eventId = lastListedEventIds.get(number);
        if (eventId == null) {
            return "No such event number.";
        }
        return calendarService.deleteEvent(eventId);
    }

    public String deleteEventByTitleOrKeyword(String keyword) throws Exception {
        keyword = keyword.toLowerCase();
        // Try exact match first
        if (lastListedEventTitlesToIds.containsKey(keyword)) {
            String eventId = lastListedEventTitlesToIds.get(keyword);
            return calendarService.deleteEvent(eventId);
        }
        // Try partial match
        for (Map.Entry<String, String> entry : lastListedEventTitlesToIds.entrySet()) {
            if (entry.getKey().contains(keyword)) {
                return calendarService.deleteEvent(entry.getValue());
            }
        }
        return "No event found with title or keyword: " + keyword;
    }
}