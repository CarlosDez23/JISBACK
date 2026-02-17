package com.jis.training.infrastructure.adapter.in.rest.dto;

import java.util.List;

public record BulkLoadResult(
        int totalProcesados,
        int exitosos,
        int fallidos,
        List<String> errores
) {}
