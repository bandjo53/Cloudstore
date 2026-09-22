package com.site.Cloudstore;

import java.time.LocalDateTime;
import java.util.Date;

public record ErrorResponseDto(
        String ErrorForUser,
        String ErrorForDev,
        LocalDateTime errorTime
) {
}
