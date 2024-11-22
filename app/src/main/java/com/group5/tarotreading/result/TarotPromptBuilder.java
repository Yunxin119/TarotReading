package com.group5.tarotreading.result;

import java.util.ArrayList;

public class TarotPromptBuilder {
    private String questionContent;
    private String spreadType;
    private ArrayList<String> cardPicked;

    public TarotPromptBuilder(String questionContent, String spreadType, ArrayList<String> cardPicked) {
        this.questionContent = questionContent;
        this.spreadType = spreadType;
        this.cardPicked = cardPicked;
    }

    private String getSpreadTypeMeaning() {
        switch (spreadType) {
            case "TimeFlow":
                return "when read this spread, first look at how the cut card can influence the whole spread, and then read by past, present and future's order. 1st card means past situation, 2nd card means current situation, 3rd card means future situation.";
            case "LoverBack":
                return "This spread is to analyze the ex's thoughts, predict the possibility and development of getting back together. 1st card, ex's thought about me, 2nd card means ex's opinion about getting back together, 3rd card means the obstacle of getting back together, and 4th card means the future of the relationship.";
            case "LoverCross":
                return "This spread is suitable for most of the romantic relationship situations. 1st card means your attitude and opinion about the relationship, 2nd card means the other person's attitude, 3rd card means current situation or dynamic of the relationship, 4th card means the future development, indicates potential future outcomes or progress. 5th card means the outcome, suggest the overall result of the situation.";
            case "Marlboro":
                return "This spread is to understand one's current situation and determing the best course of action. When reading it, first look at how the cut card can influence the whole spread, and then read by order of the card. 1st card means current situation, 2nd card means the predictable situation, aspects or events that are foreseeable and within control, 3rd card means the unpredictable situation, represent elements or influences that are uncertain or beyond control, 4th card means what will happen based on the current dynamics, 5th card means outcome, indicates the overall result of the situation and way to solve it.";
            case "TwoSelection":
                return "This spread can be used for situations involving two distinct choices. 1st card: your current state, represents your overall situation or mindset regarding the decision. 2nd card: State of Choice A: Explores the circumstances and details if you choose option A. 3rd card: State of Choice B: Explores the circumstances and details if you choose option B. 4th card: Impact of Choice A: Reveals the outcomes or effects of selecting option A. 5th card: Impact of Choice B: Reveals the outcomes or effects of selecting option B.";
            case "Hexgram":
                return "This spread helps clarify the progression of the situation and provides actionable insights for resolution.It consists of six cards: Past, reflecting past events or experiences; Present, representing the current situation; Future, indicating potential developments; Cause, revealing the root reason behind the issue; Obstacle, highlighting challenges that may arise; and Advice, offering guidance or strategies for resolution. To interpret, observe the overall impact of the cutcard to other cards and read them in order: Past, Present, Future, Cause, Obstacle, and Advice.";
            default:
                return "";
        }
    }

    public String buildPrompt() {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("You are a wise and mystical tarot master. Based on the following information, provide an insightful reading and interpretation.\n\n")
                .append("Question: ").append(questionContent).append("\n")
                .append("Spread Type: ").append(spreadType).append("\n")
                .append("Reading Instructions: ").append(getSpreadTypeMeaning()).append("\n")
                .append("Cards Picked:\n");

        for (String card : cardPicked) {
            promptBuilder.append(card).append("\n");
        }

        promptBuilder.append("\nInstructions for the reading:")
                .append("\n1. Adopt the persona of a wise and compassionate tarot master.")
                .append("\n2. Structure your response following these steps:")
                .append("\n   a. Begin with a brief introduction connecting to the seeker's question")
                .append("\n   b. Interpret each card individually, separated by '##'")
                .append("\n   c. Conclude with a synthesis of all cards' meanings and specific guidance, separated by '##'")
                .append("\n3. For each card interpretation, include:")
                .append("\n   - The card's name and position in the spread")
                .append("\n   - Its specific meaning in the context of the question")
                .append("\n   - How it relates to other cards in the spread")
                .append("\n4. Keep the language:")
                .append("\n   - Clear and gental sounds like tarot master")
                .append("\n   - Empowering and constructive")
                .append("\n   - Professional yet mystical");

        return promptBuilder.toString();
    }
}
