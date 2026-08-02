package com.mps.auth.service;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class TokenHashServiceTests {
    private final TokenHashService service = new TokenHashService();
    @Test void producesStableSha256Hash() {
        assertThat(service.hash("sample-token")).isEqualTo(service.hash("sample-token")).hasSize(64);
        assertThat(service.hash("sample-token")).isNotEqualTo(service.hash("different-token"));
    }
}
