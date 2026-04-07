package algo.vk_monetisation.services;

import algo.vk_monetisation.dto.PersonDTO;
import algo.vk_monetisation.dto.PersonResponseDTO;
import algo.vk_monetisation.entities.Person;
import algo.vk_monetisation.repositories.PersonRepository;
import algo.vk_monetisation.utils.PersonMapper;
import algo.vk_monetisation.utils.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PersonSevice {

    private final Validator validator;

    private final PersonRepository personRepository;

    private final PersonMapper personMapper;

    @Transactional
    public void createPerson(PersonDTO personDTO) {
        log.info("Пришел запрос на сервис для добавления нового ответственного.");
        validator.validatePerson(personDTO);
        Person person = personMapper.toEntity(personDTO);
        personRepository.save(person);
    }

    public PersonResponseDTO getPerson(Long id) {
        log.info("Пришел запрос на сервис для получения ответственного лица компании.");
        validator.validatePerson(id);
        Person person = personRepository.findById(id).get();
        return personMapper.toDTO(person);
    }

    @Transactional
    public void updatePerson(Long id, PersonDTO personDTO) {
        log.info("Пришел запрос на серси на обновление ответсвтенного лица.");
        validator.validatePerson(id, personDTO);
        Person person = personRepository.findById(id).get();
        personMapper.updateEntity(person, personDTO);
        personRepository.save(person);
    }

    @Transactional
    public void deletePerson(Long id) {
        log.info("Пришел запрос на сервис для удаления ответсвтенного лица.");
        validator.validatePerson(id);
        personRepository.deleteById(id);
    }

}
