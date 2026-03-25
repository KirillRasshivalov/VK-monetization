package algo.vk_monetisation.utils;

import algo.vk_monetisation.dto.*;
import algo.vk_monetisation.entities.AdvertisingCampaign;
import algo.vk_monetisation.exceptions.ValidationException;
import algo.vk_monetisation.repositories.AdvertisingCampaignRepository;
import algo.vk_monetisation.repositories.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class Validator {

    private final PersonRepository personRepository;

    private final AdvertisingCampaignRepository advertisingCampaignRepository;

    public void validateAuthorContent(Long campaignId, MultipartFile image, MultipartFile video) {
        if (campaignId == null) {
            throw new ValidationException("campaignId не может быть null");
        }
        boolean hasImage = image != null && !image.isEmpty();
        boolean hasVideo = video != null && !video.isEmpty();
        if (!hasImage && !hasVideo) {
            throw new ValidationException("Нужно передать хотя бы один файл контента (image или video).");
        }
        AdvertisingCampaign campaign = advertisingCampaignRepository.findById(campaignId)
                .orElseThrow(() -> new ValidationException("Кампания не найдена: " + campaignId));
        var person = campaign.getPerson();
        if (person == null) {
            throw new ValidationException("У кампании не задан заказчик (person).");
        }
    }

    public void validateCampaignStatus(Long id) {
        if (advertisingCampaignRepository.findById(id).isEmpty()) {
            throw new ValidationException("Нету такой кампании.");
        }
    }

    public void validateCompaign(Long id) {
        if (advertisingCampaignRepository.findById(id).isEmpty() ||
                advertisingCampaignRepository.findById(id).get().getContent() == null) {
            throw new ValidationException("Не существует такой кампании.");
        }
    }

    public void validatePosev(PosevDTO posevDTO) throws ValidationException {
        validatePersonId(posevDTO.personId());
    }

    public void validateRequisites(RequisitesDTO requisitesDTO) throws ValidationException {
        CompanyInfoDTO companyInfoDTO = requisitesDTO.companyInfoDTO();
        ContactsDTO contactsDTO = requisitesDTO.contactsDTO();
        LegalEntityDTO legalEntityDTO = requisitesDTO.legalEntityDTO();
        validateINN(companyInfoDTO.INN());
        validateOGRNIP(companyInfoDTO.ogrnip());
        validateNumber(contactsDTO.contactNumber());
        validateNumber(contactsDTO.contactPearson());
        validatePosIndex(legalEntityDTO.index());
    }

    private void validateINN(String INN) {
        if (INN.toString().length() != 12) {
            throw new ValidationException("INN должен быть 12 цифр.");
        }
    }

    private void validateOGRNIP(String OGRNIP) {
        if (OGRNIP.length() != 15) {
            throw new ValidationException("ОГРНИП должен быть 15 цифр.");
        }
        if (OGRNIP.toString().charAt(0) != '3') {
            throw new ValidationException("Первая цифра ОГРНИП должна быть равна 3.");
        }
    }

    private void validateNumber(String number) {
        String newNum = number.replaceFirst("^\\+7", "8");
        if (newNum.length() != 11) {
            throw new ValidationException("Номер телефона должен состоять из 11 цифр.");
        }
    }

    private void validatePosIndex(int posIndex) {
        if (((Integer) posIndex).toString().length() != 6) {
            throw new ValidationException("Почтовый индекс должен состоять из 6 цифр.");
        }
    }

    private void validateMediaFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return;
        }
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            if (file == null || file.isEmpty()) {
                throw new ValidationException("Медиафайл не должен быть пустым.");
            }
            if (file.getSize() == 0) {
                throw new ValidationException("Размер файла не должен быть 0 байт.");
            }
        }
    }

    private void validatePersonId(Long personId) {
        if (personId == null) {
            throw new ValidationException("Значение ответственного за рекламную кампанию не должно быть пусто.");
        }
        if (!personRepository.existsById(personId)) {
            throw new ValidationException("Данный пользователь не может создать рекламу.");
        }
    }

}
