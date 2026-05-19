package algo.vk_monetisation.messaging;

import algo.vk_monetisation.entities.AdvertisingCampaign;
import algo.vk_monetisation.entities.CompanyInfo;
import algo.vk_monetisation.entities.Contacts;
import algo.vk_monetisation.entities.Content;
import algo.vk_monetisation.entities.Person;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class CampaignLifecycleEventFactory {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static CampaignLifecycleEvent activated(AdvertisingCampaign campaign, Content content, Person person) {
        CompanyInfo company = person.getCompanyInfo();
        Contacts contacts = person.getContacts();
        return new CampaignLifecycleEvent(
                "ACTIVATED",
                campaign.getId(),
                person.getId(),
                content.getId(),
                company != null ? company.getInn() : null,
                company != null ? company.getNameOfCompany() : null,
                contacts != null ? contacts.getContactNumber() : null,
                contacts != null ? contacts.getContactPerson() : null,
                content.getViews(),
                content.getLikes(),
                LocalDateTime.now().format(ISO)
        );
    }

    public static CampaignLifecycleEvent completed(AdvertisingCampaign campaign, Content content, Person person) {
        CompanyInfo company = person.getCompanyInfo();
        Contacts contacts = person.getContacts();
        long views = content.getFinalViews() != null ? content.getFinalViews() : safe(content.getViews());
        long likes = content.getFinalLikes() != null ? content.getFinalLikes() : safe(content.getLikes());
        return new CampaignLifecycleEvent(
                "COMPLETED",
                campaign.getId(),
                person.getId(),
                content.getId(),
                company != null ? company.getInn() : null,
                company != null ? company.getNameOfCompany() : null,
                contacts != null ? contacts.getContactNumber() : null,
                contacts != null ? contacts.getContactPerson() : null,
                views,
                likes,
                LocalDateTime.now().format(ISO)
        );
    }

    private static long safe(Long value) {
        return value != null ? value : 0L;
    }

    private CampaignLifecycleEventFactory() {
    }
}
