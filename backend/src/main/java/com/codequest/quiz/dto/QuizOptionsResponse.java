package com.codequest.quiz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record QuizOptionsResponse(
        @JsonProperty("A") String a,
        @JsonProperty("B") String b,
        @JsonProperty("C") String c,
        @JsonProperty("D") String d
) {
}
