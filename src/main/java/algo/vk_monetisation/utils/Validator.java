package algo.vk_monetisation.utils;

import algo.vk_monetisation.dto.*;
import algo.vk_monetisation.entities.*;
import algo.vk_monetisation.exceptions.ValidationException;
import algo.vk_monetisation.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class Validator {

    private final PersonRepository personRepository;

    private final AdvertisingCampaignRepository advertisingCampaignRepository;

    private final ContentRepository contentRepository;

    private final ContactsRepository contactsRepository;

    private final LegalEntityRepository legalEntityRepository;

    public void validatePerson(Long id, PersonDTO personDTO) {
        if (!personRepository.findById(id).isPresent()) {
            throw new ValidationException("Ответственное лицо не найдено.");
        }
        if (personDTO.surname() == null || personDTO.name() == null) {
            throw new ValidationException("Имя и фамилия должны быть указаны.");
        }
    }

    public void validatePerson(Long id) {
        if (!personRepository.findById(id).isPresent()) {
            throw new ValidationException("Ответственное лицо не найдено.");
        }
    }

    public void validatePerson(PersonDTO personDTO)  {
        if (personDTO.surname() == null || personDTO.name() == null) {
            throw new ValidationException("Имя и фамилия должны быть указаны.");
        }
    }

    public void validateLegalEntity(Long id, LegalEntityDTO legalEntity) {
        if (!legalEntityRepository.existsById(id)) {
            throw new ValidationException("Legal entity не найден.");
        }
        if (legalEntity == null) {
            throw new ValidationException("Legal entity пустой.");
        }
    }

    public void validateLegalEntity(Long id) {
        if (!legalEntityRepository.existsById(id)) {
            throw new ValidationException("Legal entity не найден.");
        }
    }

    public void validateLegalEntity(LegalEntityDTO legalEntityDTO) {
        if (legalEntityDTO == null) {
            throw new ValidationException("Legal entity пустой.");
        }
    }

    public void validateContacts(Long contactsId, ContactsDTO contacts) {
        if (contacts == null) {
            throw new ValidationException("Контакты пусты.");
        }
        if (contactsRepository.findById(contactsId) == null) {
            throw new ValidationException("Такого контакта нет.");
        }
    }

    public void validateContacts(ContactsDTO contacts) {
        if (contacts == null) {
            throw new ValidationException("Контакт пусты.");
        }
    }

    public void validateContacts(Long contactId) {
        if (contactsRepository.findById(contactId) == null) {
            throw new ValidationException("Нету такого контакта.");
        }
    }

    public void validateCampaign(Long personId, int pageNum) {
        if (personRepository.findById(personId).isEmpty()) {
            throw new ValidationException("Персоны с id =  " + personId + " не существует.");
        }
        if (pageNum < 0) {
            throw new ValidationException("Страница должна быть >= 0.");
        }
    }

    public void validateContent(Long contentId) {
        if (contentRepository.findById(contentId).isEmpty()) {
            throw new ValidationException("Нету такого контента.");
        }
    }

    public void validateContent(Long contentId, ContentDTO content) {
        if (contentRepository.findById(contentId).isEmpty()) {
            throw new ValidationException("Данному айди ничего не принадлежит.");
        }
        if (content == null) {
            throw new ValidationException("Контент пуст.");
        }
    }

    public void validateContent(Long campaignId, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, 10);
        if (advertisingCampaignRepository.findContentsByCampaignId(campaignId, pageable).isEmpty()) {
            throw new ValidationException("Данному айдишнига ничего не принадлежит.");
        }
        if (pageNum < 0) {
            throw new ValidationException("Страница должна быть >= 0.");
        }
    }

    public void validateWalletUpper(WalletTopUpDTO dto) {
        if (dto == null || dto.personId() == null) {
            throw new ValidationException("personId не должен быть пустым");
        }
        if (dto.amount() == null || dto.amount() <= 0) {
            throw new ValidationException("amount должен быть > 0");
        }
        personRepository.findById(dto.personId())
                .orElseThrow(() -> new ValidationException("Person не найден: " + dto.personId()));
    }

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

    public void validateCampaign(Long id, PosevDTO posevDTO) {
        if (advertisingCampaignRepository.findById(id).isEmpty() ||
                advertisingCampaignRepository.findById(id).get().getContent() == null) {
            throw new ValidationException("Не существует такой кампании.");
        }
        if (posevDTO == null) {
            throw new ValidationException("Пустая кампания.");
        }
    }

    public void validateCampaign(Long id) {
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
        validateINN(companyInfoDTO.inn());
        validateOGRNIP(companyInfoDTO.ogrnip());
        validateNumber(contactsDTO.contactNumber());
        validatePosIndex(legalEntityDTO.postalIndex());
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
