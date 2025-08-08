package com.example.langchain4j;

import dev.langchain4j.agent.tool.Tool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CalendarTool {

    private final GoogleCalendarService calendarService;

    public CalendarTool() throws Exception {
        this.calendarService = new GoogleCalendarService();
    }

    @Tool("Create a calendar event with title, description, start time and end time")
    public String createEvent(String title, String description, String startTime, String endTime) throws Exception {
        return calendarService.createEvent(title, description, startTime, endTime);
    }

    @Tool("List upcoming calendar events with their IDs")
    public String listEvents() throws Exception {
        return String.join("\n", calendarService.listUpcomingEventsWithIds());
    }

    @Tool("Delete a calendar event with eventId")
    public String deleteEvent(String eventId) throws Exception {
        return calendarService.deleteEvent(eventId);
    }
}
