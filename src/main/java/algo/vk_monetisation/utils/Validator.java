package algo.vk_monetisation.utils;

import algo.vk_monetisation.dto.CompanyInfoDTO;
import algo.vk_monetisation.dto.ContactsDTO;
import algo.vk_monetisation.dto.LegalEntityDTO;
import algo.vk_monetisation.dto.RequisitesDTO;
import algo.vk_monetisation.exceptions.ValidationException;
import org.springframework.stereotype.Service;

@Service
public class Validator {

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

}
