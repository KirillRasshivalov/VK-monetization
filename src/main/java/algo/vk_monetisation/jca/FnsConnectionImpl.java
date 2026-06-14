package algo.vk_monetisation.jca;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class FnsConnectionImpl implements FnsConnection {

    private final FnsManagedConnection managedConnection;
    private boolean closed = false;

    @Override
    public String verifyInn(String inn) {
        if (closed) {
            throw new RuntimeException("Соединение закрыто");
        }
        log.info("FnsConnectionImpl: проверка ИНН {}", inn);
        return managedConnection.verifyInn(inn);
    }

    @Override
    public void close() {
        log.debug("Закрытие FnsConnectionImpl");
        closed = true;
    }
}