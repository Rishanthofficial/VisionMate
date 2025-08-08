package com.example.langchain4j;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatGUI().createAndShowGUI());
    }
}
