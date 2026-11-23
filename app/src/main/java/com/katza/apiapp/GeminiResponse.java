package com.katza.apiapp;

import java.util.List;

public class GeminiResponse {
    private List<Candidate> candidates;

    public String getAnswer() {

        if (candidates == null || candidates.isEmpty()) {
            return "לא התקבלה תשובה";
        }

        Candidate candidate = candidates.get(0);

        if (candidate.content == null ||
                candidate.content.parts == null ||
                candidate.content.parts.isEmpty()) {

            return "לא התקבלה תשובה";
        }

        String text = candidate.content.parts.get(0).text;

        return text != null
                ? text
                : "לא התקבלה תשובה";
    }

    private static class Candidate {
        private ResponseContent content;
    }

    private static class ResponseContent {
        private List<ResponsePart> parts;
    }

    private static class ResponsePart {
        private String text;
    }

}
