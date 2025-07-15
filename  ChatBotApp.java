import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

 class ChatBotApp {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JPanel mainChatPanel;

    public ChatBotApp() {
        buildChatPanel();
    }

    private void buildChatPanel() {
        mainChatPanel = new JPanel(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        inputField = new JTextField();
        sendButton = new JButton("Send");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        mainChatPanel.add(scrollPane, BorderLayout.CENTER);
        mainChatPanel.add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        greetUser();
    }

    public JPanel getChatPanel() {
        return mainChatPanel;
    }

    private void greetUser() {
        chatArea.append("👩‍⚕️ HealthBot: Hello! I'm your AI assistant. Ask me anything about health or appointments.\n\n");
    }

    private void sendMessage() {
        String userText = inputField.getText().trim();
        if (userText.isEmpty()) return;

        chatArea.append("🧑 You: " + userText + "\n");
        String botReply = getBotResponse(userText);
        chatArea.append("👩‍⚕️ HealthBot: " + botReply + "\n\n");
        inputField.setText("");
    }

   private String getBotResponse(String input) {
    input = input.toLowerCase();

    if (input.contains("appointment") || input.contains("book doctor"))
        return "You can book or view appointments using the 'Appointment' option on the dashboard.";
    if (input.contains("prescription") || input.contains("my medicines"))
        return "You can view your prescriptions in the 'Prescription' section of the dashboard.";

    // Common symptoms
    if (input.contains("fever")) return "Stay hydrated, take rest, and use paracetamol if needed.";
    if (input.contains("cold")) return "Use steam inhalation and drink warm fluids like tulsi tea.";
    if (input.contains("cough")) return "Try honey with warm water or turmeric milk.";
    if (input.contains("headache")) return "Take rest, drink water, and avoid screen time.";
    if (input.contains("sore throat")) return "Gargle with warm salt water. Herbal tea may help.";
    if (input.contains("body pain")) return "Rest and stay hydrated. A warm compress might help.";
    if (input.contains("vomiting")) return "Drink ORS or lime water. Avoid solid food temporarily.";
    if (input.contains("diarrhea")) return "Stay hydrated, eat bananas and boiled rice.";
    if (input.contains("constipation")) return "Eat fiber-rich foods and drink plenty of water.";
    if (input.contains("stomach ache") || input.contains("abdominal pain")) return "Avoid spicy food. Try light meals like khichdi.";
    if (input.contains("bloating") || input.contains("gas")) return "Jeera water or ajwain helps. Eat slowly.";
    if (input.contains("nausea")) return "Lemon water or ginger tea may relieve nausea.";
    if (input.contains("back pain")) return "Try hot/cold compress, avoid long sitting hours.";
    if (input.contains("knee pain")) return "Apply ice, avoid strain, and consult if persistent.";
    if (input.contains("eye pain")) return "Reduce screen time, apply cold compress, or use eye drops.";
    if (input.contains("toothache")) return "Rinse with warm salt water and avoid sweet food.";
    if (input.contains("burn")) return "Cool the burn under running water. Avoid applying toothpaste.";
    if (input.contains("cut") || input.contains("wound")) return "Clean with water, apply antiseptic, and cover it.";
    if (input.contains("rash")) return "Apply calamine or aloe vera. Avoid scratching.";
    if (input.contains("itching")) return "Apply coconut oil or antihistamine cream.";
    if (input.contains("allergy")) return "Avoid known allergens. Use antihistamines if required.";
    if (input.contains("sneezing")) return "Use a mask and avoid dust/pollution.";
    if (input.contains("asthma")) return "Use prescribed inhaler and avoid cold triggers.";
    if (input.contains("acne")) return "Clean your face gently and avoid touching pimples.";
    if (input.contains("dandruff")) return "Use anti-dandruff shampoo and keep scalp clean.";
    if (input.contains("hair fall")) return "Eat protein-rich food, and consider using coconut oil.";
    if (input.contains("weight loss")) return "Try portion control, walk daily, and stay hydrated.";
    if (input.contains("weight gain")) return "Eat more frequent meals with protein and nuts.";
    if (input.contains("diabetes")) return "Monitor sugar, eat fiber, and walk after meals.";
    if (input.contains("bp") || input.contains("blood pressure")) return "Limit salt, stress, and get regular checkups.";
    if (input.contains("cholesterol")) return "Avoid fried foods. Eat oats, almonds, and vegetables.";
    if (input.contains("pcos")) return "Regular exercise, healthy food, and stress management help.";
    if (input.contains("thyroid")) return "Take medication regularly and get blood tests done.";
    if (input.contains("period pain") || input.contains("menstrual pain")) return "Use a hot pack, rest, or sip ginger tea.";
    if (input.contains("pregnancy")) return "Eat well, get regular checkups, and avoid stress.";
    if (input.contains("vomit")) return "ORS and rest help. Visit doctor if continuous.";
    if (input.contains("depression") || input.contains("sad")) return "You're not alone. Try talking to someone you trust.";
    if (input.contains("anxiety")) return "Breathe deeply, meditate, and talk to someone.";
    if (input.contains("stress")) return "Take breaks, meditate, or walk outdoors.";
    if (input.contains("sleep") || input.contains("insomnia")) return "Avoid caffeine late evening. Try a sleep routine.";
    if (input.contains("fatigue") || input.contains("tired")) return "Check your iron levels. Eat fruits and rest well.";
    if (input.contains("immunity")) return "Eat fruits, get sunlight, and sleep properly.";
    if (input.contains("corona") || input.contains("covid")) return "Wear a mask, isolate if sick, and get tested.";
    if (input.contains("vaccine")) return "Vaccination helps prevent diseases. Book it online.";
    if (input.contains("healthy diet")) return "Eat balanced meals with vegetables, fruits, and proteins.";
    if (input.contains("hydration")) return "Drink 8–10 glasses of water daily.";
    if (input.contains("exercise")) return "Walk, jog, or do yoga 30 minutes a day.";
    if (input.contains("hello") || input.contains("hi") || input.contains("hey")) return "Hello! 😊 How can I help you today?";
    if (input.contains("thank")) return "You're welcome! Stay healthy.";
    if (input.contains("bye")) return "Take care! I'm here if you need anything.";

    return "Hmm... I didn’t quite get that. 🤔 Try asking about a symptom, appointment, or prescription.";
}

}
