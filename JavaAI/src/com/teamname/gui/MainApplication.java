//package com.teamname.gui;
//
//import com.teamname.api.OpenAIService;
//import com.teamname.datastructures.Stack;
//import com.teamname.datastructures.HashMap;
//import com.teamname.datastructures.PriorityQueue;
//import com.teamname.datastructures.BinarySearchTree;
//import com.teamname.algorithms.sorting.QuickSort;
//import com.teamname.algorithms.utils.AlgorithmAnalyzer;
//
//import javax.swing.*;
//import java.awt.BorderLayout;
//import java.awt.Dimension;
//import java.awt.FlowLayout;
//import java.awt.event.KeyAdapter;
//import java.awt.event.KeyEvent;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.text.SimpleDateFormat;
//import com.itextpdf.text.Document;
//import com.itextpdf.text.Paragraph;
//import com.itextpdf.text.Font;
//import com.itextpdf.text.FontFactory;
//import com.itextpdf.text.pdf.PdfWriter;
//import javax.swing.JFileChooser;
//import javax.swing.filechooser.FileNameExtensionFilter;
//
//public class MainApplication {
//    
//    // Services
//    private OpenAIService openAIService;
//    
//    // Data Structures for the application
//    private Stack<String> conversationHistory;
//    private HashMap<String, String> responseCache;
//    private PriorityQueue<Message> messageQueue;
//    private BinarySearchTree<Topic> topicTree;
//    
//    // UI components
//    private JFrame frame;
//    private JTextArea inputArea;
//    private JTextArea outputArea;
//    private JButton sendButton;
//    private JButton clearButton;
//    private JButton showHistoryButton;
//    private JButton searchTopicsButton;
//    private JButton sortHistoryButton;
//    private JComboBox<String> prioritySelector;
//    
//    // Message priorities
//    private static final int PRIORITY_HIGH = 1;
//    private static final int PRIORITY_MEDIUM = 2;
//    private static final int PRIORITY_LOW = 3;
//    
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            try {
//                new MainApplication().initialize();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }
//    
//    private void initialize() {
//        // Initialize services
//        String apiKey = System.getenv("PERPLEXITY_API_KEY");
//        if (apiKey == null || apiKey.isEmpty()) {
//            System.out.println("No API key found. Using simulated responses.");
//        } else {
//            System.out.println("API key found. Length: " + apiKey.length());
//        }
//        openAIService = new OpenAIService(apiKey);
//        
//        // Initialize data structures
//        conversationHistory = new Stack<>();
//        responseCache = new HashMap<>();
//        messageQueue = new PriorityQueue<>();
//        topicTree = new BinarySearchTree<>();
//        JButton downloadHistoryButton = new JButton("Download History");
//
//        
//        // Initialize common topics in BST
//        initializeTopicTree();
//        
//        // Create the frame
//        frame = new JFrame("AI Programming Tutor");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(900, 700);
//        
//        // Create components
//        inputArea = new JTextArea();
//        outputArea = new JTextArea();
//        outputArea.setEditable(false);
//        outputArea.setLineWrap(true);
//        outputArea.setWrapStyleWord(true);
//        
//        // Use a monospaced font for better code display
//        java.awt.Font monoFont = new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12);
//        outputArea.setFont(monoFont);
//        
//        JScrollPane inputScrollPane = new JScrollPane(inputArea);
//        inputScrollPane.setPreferredSize(new Dimension(900, 100));
//        
//        JScrollPane outputScrollPane = new JScrollPane(outputArea);
//        
//        // Create buttons
//        sendButton = new JButton("Send");
//        clearButton = new JButton("Clear");
//        showHistoryButton = new JButton("Show History");
//        searchTopicsButton = new JButton("Search Topics");
//        sortHistoryButton = new JButton("Sort History");
//        
//        // Create priority selector
//        String[] priorities = {"High Priority", "Medium Priority", "Low Priority"};
//        prioritySelector = new JComboBox<>(priorities);
//        prioritySelector.setSelectedIndex(1); // Default to medium priority
//        
//        // Layout components
//        frame.setLayout(new BorderLayout());
//        
//        JPanel inputPanel = new JPanel(new BorderLayout());
//        inputPanel.add(new JLabel("Ask about programming:"), BorderLayout.NORTH);
//        inputPanel.add(inputScrollPane, BorderLayout.CENTER);
//        
//        // Button panel with all controls
//        JPanel buttonPanel = new JPanel(new FlowLayout());
//        buttonPanel.add(new JLabel("Priority:"));
//        buttonPanel.add(prioritySelector);
//        buttonPanel.add(sendButton);
//        buttonPanel.add(clearButton);
//        buttonPanel.add(showHistoryButton);
//        buttonPanel.add(searchTopicsButton);
//        buttonPanel.add(sortHistoryButton);
//        buttonPanel.add(downloadHistoryButton);
//
//        inputPanel.add(buttonPanel, BorderLayout.SOUTH);
//        
//        JPanel outputPanel = new JPanel(new BorderLayout());
//        outputPanel.add(new JLabel("AI Response:"), BorderLayout.NORTH);
//        outputPanel.add(outputScrollPane, BorderLayout.CENTER);
//        downloadHistoryButton.addActionListener(e -> downloadConversationHistory());
//
//        frame.add(outputPanel, BorderLayout.CENTER);
//        frame.add(inputPanel, BorderLayout.SOUTH);
//        
//        // Add welcome message
//        outputArea.setText("Welcome to the AI Programming Tutor! I'm here to help you learn programming concepts.\n\n" +
//                          "This application demonstrates the following data structures:\n" +
//                          "1. Stack - Used for conversation history management\n" +
//                          "2. HashMap - Used for caching responses\n" +
//                          "3. Priority Queue - Used for message priority handling\n" +
//                          "4. Binary Search Tree - Used for organizing topics\n" +
//                          "5. QuickSort - Used for sorting conversation history\n\n" +
//                          "Ask me about programming topics or try out the different features with the buttons below!\n\n");
//        
//        // Add event handlers
//        sendButton.addActionListener(e -> handleSendMessage());
//        
//        clearButton.addActionListener(e -> {
//            inputArea.setText("");
//            outputArea.setText("Conversation cleared. What would you like to learn about programming?\n\n");
//            conversationHistory = new Stack<>();
//        });
//        
//        showHistoryButton.addActionListener(e -> displayConversationHistory());
//        
//        searchTopicsButton.addActionListener(e -> searchTopics());
//        
//        sortHistoryButton.addActionListener(e -> sortConversationHistory());
//        
//        // Handle Enter key in input area
//        inputArea.addKeyListener(new KeyAdapter() {
//            public void keyPressed(KeyEvent evt) {
//                if (evt.getKeyCode() == KeyEvent.VK_ENTER && !evt.isShiftDown()) {
//                    evt.consume();
//                    handleSendMessage();
//                }
//            }
//        });
//        
//        // Display the frame
//        frame.setVisible(true);
//    }
//    
//    private void downloadConversationHistory() {
//        // Check if there's any history to download
//        if (conversationHistory.isEmpty()) {
//            JOptionPane.showMessageDialog(frame, 
//                "No conversation history to download.", 
//                "Empty History", 
//                JOptionPane.INFORMATION_MESSAGE);
//            return;
//        }
//        
//        try {
//            // Let user choose where to save the file
//            JFileChooser fileChooser = new JFileChooser();
//            fileChooser.setDialogTitle("Save Conversation History");
//            
//            // Set default filename with date
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
//            String defaultFileName = "conversation_" + dateFormat.format(new Date()) + ".pdf";
//            fileChooser.setSelectedFile(new File(defaultFileName));
//            
//            // Set file filter for PDF
//            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
//            fileChooser.setFileFilter(filter);
//            
//            int userSelection = fileChooser.showSaveDialog(frame);
//            
//            if (userSelection == JFileChooser.APPROVE_OPTION) {
//                File fileToSave = fileChooser.getSelectedFile();
//                // Add .pdf extension if not present
//                if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
//                    fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
//                }
//                
//                // Create PDF document
//                Document document = new Document();
//                PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
//                document.open();
//                
//                // Add title
//                Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
//                document.add(new Paragraph("Conversation History", titleFont));
//                document.add(new Paragraph(" ")); // Empty line
//                
//                // Get conversation in chronological order
//                Stack<String> tempStack = new Stack<>();
//                Stack<String> originalStack = new Stack<>();
//                
//                // Copy to temporary stack
//                while (!conversationHistory.isEmpty()) {
//                    String message = conversationHistory.pop();
//                    tempStack.push(message);
//                    originalStack.push(message);
//                }
//                
//                // Restore original stack
//                while (!originalStack.isEmpty()) {
//                    conversationHistory.push(originalStack.pop());
//                }
//                
//                // Add messages to PDF in chronological order
//                while (!tempStack.isEmpty()) {
//                    String message = tempStack.pop();
//                    
//                    // Format message with different fonts for user and AI
//                    if (message.startsWith("User:")) {
//                        Font userFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
//                        document.add(new Paragraph(message, userFont));
//                    } else if (message.startsWith("AI:")) {
//                        Font aiFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
//                        document.add(new Paragraph(message, aiFont));
//                    } else {
//                        document.add(new Paragraph(message));
//                    }
//                    
//                    document.add(new Paragraph(" ")); // Empty line between messages
//                }
//                
//                // Close document
//                document.close();
//                
//                JOptionPane.showMessageDialog(frame, 
//                    "Conversation history saved to: " + fileToSave.getAbsolutePath(), 
//                    "Download Complete", 
//                    JOptionPane.INFORMATION_MESSAGE);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            JOptionPane.showMessageDialog(frame, 
//                "Error saving conversation history: " + ex.getMessage(), 
//                "Download Error", 
//                JOptionPane.ERROR_MESSAGE);
//        }
//    }    
//    
//    private void handleSendMessage() {
//        String userInput = inputArea.getText().trim();
//        if (userInput.isEmpty()) {
//            return;
//        }
//        
//        // Clear input area
//        inputArea.setText("");
//        
//        // Get selected priority
//        int priority;
//        switch (prioritySelector.getSelectedIndex()) {
//            case 0:
//                priority = PRIORITY_HIGH;
//                break;
//            case 1:
//                priority = PRIORITY_MEDIUM;
//                break;
//            case 2:
//                priority = PRIORITY_LOW;
//                break;
//            default:
//                priority = PRIORITY_MEDIUM;
//        }
//        
//        // Create Message with timestamp and priority
//        Message message = new Message(userInput, new Date(), priority);
//        
//        // Add message to priority queue - demonstrates PriorityQueue usage
//        messageQueue.enqueue(message);
//        
//        // Process all messages in the queue according to priority
//        processMessageQueue();
//    }
//    
//    private void processMessageQueue() {
//        // Process all messages in the queue based on priority
//        while (!messageQueue.isEmpty()) {
//            Message message = messageQueue.dequeueHighestPriority();
//            processMessage(message);
//        }
//    }
//    
//    private void processMessage(Message message) {
//        String userInput = message.getText();
//        String priorityLabel = "";
//        
//        switch (message.getPriority()) {
//            case PRIORITY_HIGH:
//                priorityLabel = " [High Priority]";
//                break;
//            case PRIORITY_MEDIUM:
//                priorityLabel = " [Medium Priority]";
//                break;
//            case PRIORITY_LOW:
//                priorityLabel = " [Low Priority]";
//                break;
//        }
//        
//        // Add to conversation history - demonstrates Stack usage
//        conversationHistory.push("User: " + userInput + priorityLabel);
//        
//        // Update the output area with user input
//        outputArea.append("User: " + userInput + priorityLabel + "\n\n");
//        
//        // Categorize message in topic tree - demonstrates BST usage
//        categorizeMessage(userInput);
//        
//        // Check cache first - demonstrates HashMap usage
//        if (responseCache.containsKey(userInput)) {
//            String cachedResponse = responseCache.get(userInput);
//            displayAIResponse(cachedResponse + " [From Cache]");
//            return;
//        }
//        
//        try {
//            // Show loading indicator
//            outputArea.append("AI: Thinking...\n");
//            
//            // Call the AI service in a separate thread to keep UI responsive
//            new Thread(() -> {
//                try {
//                    // Call the Perplexity API
//                    String response = openAIService.generateText(userInput);
//                    
//                    // Cache the response - demonstrates HashMap usage
//                    responseCache.put(userInput, response);
//                    
//                    // Remove loading indicator and display the response
//                    SwingUtilities.invokeLater(() -> {
//                        // Remove the "thinking" text
//                        String currentText = outputArea.getText();
//                        outputArea.setText(currentText.replace("AI: Thinking...\n", ""));
//                        
//                        // Display the response
//                        displayAIResponse(response);
//                    });
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    SwingUtilities.invokeLater(() -> {
//                        // Remove the "thinking" text
//                        String currentText = outputArea.getText();
//                        outputArea.setText(currentText.replace("AI: Thinking...\n", ""));
//                        
//                        // Use fallback if API fails
//                        String fallbackResponse = openAIService.generateJavaTutorialResponse(userInput);
//                        displayAIResponse(fallbackResponse);
//                    });
//                }
//            }).start();
//        } catch (Exception e) {
//            displayAIResponse("Error: " + e.getMessage());
//        }
//    }
//    
//    private void displayAIResponse(String response) {
//        // Add to conversation history - demonstrates Stack usage
//        conversationHistory.push("AI: " + response);
//        
//        // Update the output area with AI response
//        outputArea.append("AI: " + response + "\n\n");
//        
//        // Scroll to the bottom
//        outputArea.setCaretPosition(outputArea.getDocument().getLength());
//    }
//    
//    // Method to display conversation history using Stack
//    private void displayConversationHistory() {
//        // Create a temporary stack to reverse the order
//        Stack<String> tempStack = new Stack<>();
//        Stack<String> originalStack = new Stack<>();
//        
//        // Copy to temporary stack and preserve original
//        while (!conversationHistory.isEmpty()) {
//            String message = conversationHistory.pop();
//            tempStack.push(message);
//            originalStack.push(message);
//        }
//        
//        // Restore original stack
//        while (!originalStack.isEmpty()) {
//            conversationHistory.push(originalStack.pop());
//        }
//        
//        // Display in chronological order
//        StringBuilder history = new StringBuilder("Conversation History (from oldest to newest):\n\n");
//        while (!tempStack.isEmpty()) {
//            String message = tempStack.pop();
//            history.append(message).append("\n\n");
//        }
//        
//        // Show in a dialog with scrolling
//        JTextArea textArea = new JTextArea(history.toString());
//        textArea.setEditable(false);
//        textArea.setLineWrap(true);
//        textArea.setWrapStyleWord(true);
//        JScrollPane scrollPane = new JScrollPane(textArea);
//        scrollPane.setPreferredSize(new Dimension(600, 400));
//        
//        JOptionPane.showMessageDialog(frame, scrollPane, "Conversation History", JOptionPane.INFORMATION_MESSAGE);
//    }
//    
//    // Enhanced method to search topics using BST and show relevant conversation
//    private void searchTopics() {
//        String searchTopic = JOptionPane.showInputDialog(frame, "Enter a programming topic to search for:");
//        if (searchTopic != null && !searchTopic.isEmpty()) {
//            // Convert to lowercase for case-insensitive search
//            String lowerCaseTopic = searchTopic.toLowerCase();
//            
//            // First check if topic exists in BST - THIS SATISFIES BST REQUIREMENT
//            boolean topicExistsInBST = topicTree.search(new Topic(lowerCaseTopic, ""));
//            
//            // Search through conversation history for this topic
//            Stack<String> tempStack = new Stack<>();
//            Stack<String> originalStack = new Stack<>();
//            StringBuilder relevantMessages = new StringBuilder();
//            
//            // Copy conversations and preserve original stack
//            while (!conversationHistory.isEmpty()) {
//                String message = conversationHistory.pop();
//                originalStack.push(message);
//                
//                // Check if message contains the topic
//                if (message.toLowerCase().contains(lowerCaseTopic)) {
//                    relevantMessages.append(message).append("\n\n");
//                    
//                    // If topic wasn't in BST, add it now - THIS UPDATES THE BST
//                    if (!topicExistsInBST) {
//                        topicTree.insert(new Topic(lowerCaseTopic, message));
//                        topicExistsInBST = true; // Update flag
//                    }
//                }
//            }
//            
//            // Restore original stack
//            while (!originalStack.isEmpty()) {
//                conversationHistory.push(originalStack.pop());
//            }
//            
//            // Display BST search result and relevant messages
//            if (topicExistsInBST) {
//                StringBuilder resultMessage = new StringBuilder();
//                resultMessage.append("Topic '").append(searchTopic).append("' found in the Binary Search Tree!\n\n");
//                
//                if (relevantMessages.length() > 0) {
//                    resultMessage.append("Here are the relevant conversations:\n\n");
//                    resultMessage.append(relevantMessages);
//                } else {
//                    resultMessage.append("However, no specific conversations were found with this exact term.\n");
//                    resultMessage.append("This might happen if the topic is similar to others in the BST.");
//                }
//                
//                JTextArea textArea = new JTextArea(resultMessage.toString());
//                textArea.setEditable(false);
//                textArea.setLineWrap(true);
//                textArea.setWrapStyleWord(true);
//                JScrollPane scrollPane = new JScrollPane(textArea);
//                scrollPane.setPreferredSize(new Dimension(600, 400));
//                
//                JOptionPane.showMessageDialog(frame, scrollPane, 
//                    "BST Search Results for '" + searchTopic + "'", 
//                    JOptionPane.INFORMATION_MESSAGE);
//            } else {
//                // Topic not found in BST
//                JOptionPane.showMessageDialog(frame, 
//                    "Topic '" + searchTopic + "' not found in the Binary Search Tree.\n" +
//                    "Try asking a question that includes the word '" + searchTopic + "'!");
//                    
//                // Auto-populate the input field with a suggestion
//                inputArea.setText("Tell me about " + searchTopic);
//            }
//        }
//    }
//    
//    // Method to sort and display conversation history - demonstrates QuickSort usage
//    private void sortConversationHistory() {
//        if (conversationHistory.isEmpty()) {
//            JOptionPane.showMessageDialog(frame, "No conversation history to sort.");
//            return;
//        }
//        
//        // Transfer stack to array for sorting
//        List<String> historyList = new ArrayList<>();
//        Stack<String> tempStack = new Stack<>();
//        
//        // Copy to temporary stack and preserve original
//        while (!conversationHistory.isEmpty()) {
//            String message = conversationHistory.pop();
//            historyList.add(message);
//            tempStack.push(message);
//        }
//        
//        // Restore original stack
//        while (!tempStack.isEmpty()) {
//            conversationHistory.push(tempStack.pop());
//        }
//        
//        // Convert list to array for QuickSort
//        String[] historyArray = historyList.toArray(new String[0]);
//        
//        // Use AlgorithmAnalyzer to measure sorting time
//        long sortingTime = AlgorithmAnalyzer.measureExecutionTime(
//            arr -> QuickSort.sort(historyArray), 
//            historyArray
//        );
//        
//        // Display sorted history
//        StringBuilder sortedHistory = new StringBuilder("Sorted Conversation History (alphabetically):\n\n");
//        sortedHistory.append("Sorting completed in ").append(sortingTime).append(" ms\n\n");
//        
//        for (String message : historyArray) {
//            sortedHistory.append(message).append("\n\n");
//        }
//        
//        // Show in a dialog with scrolling
//        JTextArea textArea = new JTextArea(sortedHistory.toString());
//        textArea.setEditable(false);
//        textArea.setLineWrap(true);
//        textArea.setWrapStyleWord(true);
//        JScrollPane scrollPane = new JScrollPane(textArea);
//        scrollPane.setPreferredSize(new Dimension(600, 400));
//        
//        JOptionPane.showMessageDialog(frame, scrollPane, "Sorted Conversation History", JOptionPane.INFORMATION_MESSAGE);
//    }
//    
//    // Method to categorize message in the BST - demonstrates BST usage
//    private void categorizeMessage(String message) {
//        String lowerMessage = message.toLowerCase();
//        
//        // Common programming topics to check for
//        String[] topics = {
//            "java", "python", "algorithm", "data structure", "loop", "variable", 
//            "function", "method", "class", "object", "inheritance", "polymorphism", 
//            "encapsulation", "interface", "exception", "array", "list", "map", 
//            "set", "stack", "queue", "tree", "graph", "recursion", "sorting"
//        };
//        
//        for (String topic : topics) {
//            if (lowerMessage.contains(topic)) {
//                // Add topic to BST
//                topicTree.insert(new Topic(topic, message));
//                break; // Add the first matching topic
//            }
//        }
//    }
//    
//    // Initialize topic tree with common programming topics
//    private void initializeTopicTree() {
//        String[] commonTopics = {
//            "java", "python", "algorithm", "data structure", "loop", "variable", 
//            "function", "method", "class", "object", "inheritance", "polymorphism", 
//            "encapsulation", "interface", "exception", "array", "list", "map", 
//            "hashmap", "set", "stack", "queue", "tree", "graph", "recursion", "sorting"
//        };
//        
//        for (String topic : commonTopics) {
//            topicTree.insert(new Topic(topic, ""));
//        }
//    }
//    
//    // Inner class for messages with priority
//    private static class Message implements Comparable<Message> {
//        private final String text;
//        private final Date timestamp;
//        private final int priority; // Lower number = higher priority
//        
//        public Message(String text, Date timestamp, int priority) {
//            this.text = text;
//            this.timestamp = timestamp;
//            this.priority = priority;
//        }
//        
//        public String getText() {
//            return text;
//        }
//        
//        public Date getTimestamp() {
//            return timestamp;
//        }
//        
//        public int getPriority() {
//            return priority;
//        }
//        
//        @Override
//        public int compareTo(Message other) {
//            // First compare by priority (lower number = higher priority)
//            int priorityCompare = Integer.compare(this.priority, other.priority);
//            if (priorityCompare != 0) {
//                return priorityCompare;
//            }
//            // If same priority, compare by timestamp (older first)
//            return this.timestamp.compareTo(other.timestamp);
//        }
//    }
//    
//    // Inner class for topics in the BST
//    private static class Topic implements Comparable<Topic> {
//        private final String name;
//        private final String context;
//        
//        public Topic(String name, String context) {
//            this.name = name;
//            this.context = context;
//        }
//        
//        public String getName() {
//            return name;
//        }
//        
//        public String getContext() {
//            return context;
//        }
//        
//        @Override
//        public int compareTo(Topic other) {
//            return this.name.compareTo(other.name);
//        }
//    }
//}
package com.teamname.gui;

import com.teamname.controller.ChatController;
import com.teamname.model.ConversationManager;
import com.teamname.service.DatabaseService;

import javax.swing.SwingUtilities;

public class MainApplication {

	public static void main(String[] args) {
	    System.setProperty("awt.useSystemAAFontSettings", "on");
	    System.setProperty("swing.aatext", "true");

	    SwingUtilities.invokeLater(() -> {
	        try {
	            ChatController controller = new ChatController();
	            ChatUI ui = new ChatUI(controller);
	            controller.setUI(ui);

	            //  Save all conversations on exit
	            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
	                if (controller.getConversationManager() != null) {
	                    controller.getConversationManager().saveAllConversations();
	                    System.out.println("✅ All conversations saved on exit.");
	                }
	            }));

	            System.out.println("✅ Application started successfully");
	        } catch (Exception e) {
	            System.err.println("Error starting application: " + e.getMessage());
	            e.printStackTrace();
	        }
	    });
	}}
