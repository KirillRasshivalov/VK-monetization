package algo.vk_monetisation.eis.jca.dadata;

import java.io.Serializable;

public record DadataInnResponse(boolean valid, String companyName, String rawBody) implements Serializable {
}
