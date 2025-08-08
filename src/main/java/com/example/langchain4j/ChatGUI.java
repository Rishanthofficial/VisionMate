package com.example.langchain4j;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.response.ChatResponse; // Still needed for the full response object

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.Executors; // For running blocking tasks off EDT
import java.util.concurrent.ExecutorService;

public class ChatGUI implements MychatAssistant {
    private final LangChainBackend backend;
    private byte[] selectedImageData = null;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor(); // For off-EDT tasks

    public ChatGUI() {
        backend = new LangChainBackend();
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("LangChain4j Chat with Image Input");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        // Set a modern look and feel
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        chatArea.setBackground(new Color(245, 245, 245));
        chatArea.setForeground(new Color(40, 40, 40));
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        JButton sendButton = new JButton("Send");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sendButton.setUI(new javax.swing.plaf.basic.BasicButtonUI()); // Force basic UI for custom colors
        sendButton.setBackground(new Color(0, 120, 215));
        sendButton.setForeground(Color.WHITE);
        sendButton.setOpaque(true);
        sendButton.setContentAreaFilled(true);
        sendButton.setBorderPainted(false);
        sendButton.setFocusPainted(false);
        sendButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        sendButton.setEnabled(true);

        JButton attachImageButton = new JButton("Attach Image");
        attachImageButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        attachImageButton.setBackground(new Color(230, 230, 230));
        attachImageButton.setForeground(new Color(60, 60, 60));
        attachImageButton.setFocusPainted(false);
        attachImageButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        JPanel inputPanel = new JPanel(new BorderLayout(8, 0));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.add(inputField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(attachImageButton);
        buttonPanel.add(sendButton);
        inputPanel.add(buttonPanel, BorderLayout.EAST);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(inputPanel, BorderLayout.SOUTH);

        // Add Enter key support for sending messages
        inputField.addActionListener(e -> sendButton.doClick());

        attachImageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif");
            fileChooser.setFileFilter(filter);

            int returnValue = fileChooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    selectedImageData = Files.readAllBytes(selectedFile.toPath());
                    attachImageButton.setText("Image Attached (" + selectedFile.getName() + ")");
                    chatArea.append("Image attached: " + selectedFile.getName() + "\n");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error loading image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    selectedImageData = null;
                    attachImageButton.setText("Attach Image");
                }
            }
        });

        sendButton.addActionListener(e -> {
            String userInput = inputField.getText().trim();
            if (userInput.isEmpty() && selectedImageData == null) return;

            chatArea.append("User: " + userInput);
            if (selectedImageData != null) {
                chatArea.append(" [Image Attached]");
            }
            chatArea.append("\n");
            inputField.setText("");

            // Disable input and send button while waiting for response
            inputField.setEnabled(false);
            sendButton.setEnabled(false);
            attachImageButton.setEnabled(false);

            final byte[] currentImageData = selectedImageData; // Capture for the thread
            selectedImageData = null; // Clear for next turn

            // Execute blocking network call in a separate thread to keep GUI responsive
            executorService.submit(() -> {
                try {
                    // Blocking call to backend
                    if (currentImageData != null && currentImageData.length > 0) {
                        // Image input: use the image overload
                        ChatResponse aiResponse = backend.sendMessage(userInput, currentImageData);
                        SwingUtilities.invokeLater(() -> {
                            chatArea.append("AI: " + (aiResponse.aiMessage() != null ? aiResponse.aiMessage().text() : "[No response]") + "\n");
                            chatArea.append("----------------------------\n");
                            // if (backend.getChatMemory() instanceof MessageWindowChatMemory) {
                            //     chatArea.append("Current Memory: " + ((MessageWindowChatMemory) backend.getChatMemory()).messages() + "\n");
                            // }
                            // chatArea.append("----------------------------\n");
                        });
                    } else {
                        // Text-only input: use the text-only overload
                        String aiResponse = backend.sendMessage(userInput);
                        SwingUtilities.invokeLater(() -> {
                            chatArea.append("AI: " + aiResponse + "\n");
                            chatArea.append("----------------------------\n");
                            // if (backend.getChatMemory() instanceof MessageWindowChatMemory) {
                            //     chatArea.append("Current Memory: " + ((MessageWindowChatMemory) backend.getChatMemory()).messages() + "\n");
                            // }
                            // chatArea.append("----------------------------\n");
                        });
                    }
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> chatArea.append("Error: " + ex.getMessage() + "\n"));
                } finally {
                    // Re-enable input and send button on EDT
                    SwingUtilities.invokeLater(() -> {
                        inputField.setEnabled(true);
                        sendButton.setEnabled(true); // Ensure send button is re-enabled
                        attachImageButton.setEnabled(true);
                        attachImageButton.setText("Attach Image"); // Reset button text
                    });
                }
            });
        });

        frame.setVisible(true);
    }

    @Override
    public String chat(String userInput) {
        // Delegate to backend for text-only input
        return backend.sendMessage(userInput);
    }
}