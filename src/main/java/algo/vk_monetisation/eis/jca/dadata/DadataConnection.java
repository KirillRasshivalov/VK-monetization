package algo.vk_monetisation.eis.jca.dadata;

import jakarta.resource.ResourceException;

public interface DadataConnection extends AutoCloseable {

    DadataInnResponse validateInn(DadataInnRequest request) throws ResourceException;

    @Override
    void close();
}
