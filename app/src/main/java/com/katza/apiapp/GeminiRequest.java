package com.katza.apiapp;

import java.util.Collections;
import java.util.List;

public class GeminiRequest {
    private List<Content> contents;

    public GeminiRequest(String question) {
        contents = Collections.singletonList(
                new Content(question)
        );
    }

    private static class Content {

        private List<Part> parts;

        public Content(String question) {
            parts = Collections.singletonList(
                    new Part(question)
            );
        }
    }

}
