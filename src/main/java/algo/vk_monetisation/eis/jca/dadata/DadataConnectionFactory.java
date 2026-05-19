package algo.vk_monetisation.eis.jca.dadata;

import jakarta.resource.ResourceException;

public interface DadataConnectionFactory {

    DadataConnection getConnection() throws ResourceException;
}
