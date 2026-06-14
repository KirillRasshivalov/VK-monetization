package algo.vk_monetisation.jca;

public interface FnsConnection extends AutoCloseable {

    String verifyInn(String inn);

    @Override
    void close();
}
