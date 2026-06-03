package algo.vk_monetisation.jca.connection;

import jakarta.resource.ResourceException;

public interface FnsConnection {

    String verifyInn(String inn) throws ResourceException;

    void close();
}

