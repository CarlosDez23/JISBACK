package com.jis.training.infrastructure.adapter.in.rest.dto;

public record SubmitTestResponse(
        String status,
        String message,
        int totalRespuestas
) {
    public static SubmitTestResponse accepted(int total) {
        return new SubmitTestResponse(
                "ACCEPTED",
                "Se están procesando " + total + " respuestas en segundo plano",
                total
        );
    }
}
