package com.mps.document;
import com.mps.document.model.DocumentAudienceType; import org.junit.jupiter.api.Test; import static org.assertj.core.api.Assertions.assertThat;
class DocumentAudienceTypeTests { @Test void supportsApprovedScopes(){assertThat(DocumentAudienceType.values()).extracting(Enum::name).containsExactly("ORGANIZATION","DEPARTMENT","GROUP","ROLE","USER");} }
