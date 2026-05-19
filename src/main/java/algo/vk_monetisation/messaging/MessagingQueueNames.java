package algo.vk_monetisation.messaging;

public final class MessagingQueueNames {

    public static final String COMPLIANCE = "vk.campaign.compliance";
    public static final String NOTIFICATIONS = "vk.campaign.notifications";

    /** STOMP destination для очереди RabbitMQ */
    public static String stompQueueDestination(String queueName) {
        return "/queue/" + queueName;
    }

    private MessagingQueueNames() {
    }
}
