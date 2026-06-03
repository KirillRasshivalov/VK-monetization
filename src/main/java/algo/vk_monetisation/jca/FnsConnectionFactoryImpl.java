package algo.vk_monetisation.jca;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class FnsConnectionFactoryImpl implements FnsConnectionFactory {

    private final FnsManagedConnectionFactory mcf;

    @Override
    public FnsConnection getConnection() throws Exception {
        log.debug("Получение соединения из FnsConnectionFactoryImpl");
        FnsManagedConnection managedConnection = mcf.createManagedConnection();
        return managedConnection.getConnection();
    }
}