package algo.vk_monetisation.eis.jca.smsru;

import java.io.Serializable;

public record SmsRuSendResponse(boolean success, String rawBody) implements Serializable {
}
