package com.example.langchain4j;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;

public class GoogleCalendarService {
    private static final String APPLICATION_NAME = "LangChain4j Assistant";
    private static final java.io.File CREDENTIALS_FILE = new java.io.File("S:/Java/langchain4j-demo/src/main/java/com/example/langchain4j/credentials.json");
    private static final java.io.File TOKENS_DIR = new java.io.File("tokens");

    private static final List<String> SCOPES = List.of(CalendarScopes.CALENDAR);
    private final Calendar calendarService;

    public GoogleCalendarService() throws Exception {
        calendarService = new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                authorize()
        ).setApplicationName(APPLICATION_NAME).build();
    }

    private Credential authorize() throws IOException, GeneralSecurityException {
        try (InputStream in = new FileInputStream(CREDENTIALS_FILE)) {
            GoogleClientSecrets secrets = GoogleClientSecrets.load(GsonFactory.getDefaultInstance(), new InputStreamReader(in));
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    secrets,
                    SCOPES
            ).setDataStoreFactory(new FileDataStoreFactory(TOKENS_DIR)).setAccessType("offline").build();

            return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        }
    }

    public String createEvent(String title, String description, String startTime, String endTime) throws IOException {
        Event event = new Event()
                .setSummary(title)
                .setDescription(description);

        EventDateTime start = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(startTime))
                .setTimeZone("Asia/Kolkata");

        EventDateTime end = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(endTime))
                .setTimeZone("Asia/Kolkata");

        event.setStart(start).setEnd(end);

        event = calendarService.events().insert("primary", event).execute();
        return "Event created: " + event.getHtmlLink();
    }

    public List<String> listUpcomingEvents() throws IOException {
        List<String> eventsInfo = new ArrayList<>();
        Events events = calendarService.events().list("primary")
                .setMaxResults(5)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        for (Event event : events.getItems()) {
            String when = event.getStart().getDateTime() != null
                    ? event.getStart().getDateTime().toString()
                    : event.getStart().getDate().toString();
            eventsInfo.add(event.getSummary() + " at " + when);
        }
        return eventsInfo;
    }

    public List<String> listUpcomingEventsWithIds() throws IOException {
        List<String> eventsInfo = new ArrayList<>();
        Events events = calendarService.events().list("primary")
                .setMaxResults(5)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        for (Event event : events.getItems()) {
            String when = event.getStart().getDateTime() != null
                    ? event.getStart().getDateTime().toString()
                    : event.getStart().getDate().toString();
            eventsInfo.add("ID: " + event.getId() + " | " + event.getSummary() + " at " + when);
        }
        if (eventsInfo.isEmpty()) {
            eventsInfo.add("No upcoming events found.");
        }
        return eventsInfo;
    }

    public String deleteEvent(String eventId) throws IOException {
        calendarService.events().delete("primary", eventId).execute();
        return "Event deleted: " + eventId;
    }
}
