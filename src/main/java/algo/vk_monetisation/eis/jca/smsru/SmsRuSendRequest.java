package algo.vk_monetisation.eis.jca.smsru;

import java.io.Serializable;

public record SmsRuSendRequest(String phone, String message) implements Serializable {
}
