package com.womakerscode.microservicemeetup.repository;

import com.womakerscode.microservicemeetup.model.entity.Registration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class RegistrationRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    RegistrationRepository repository;

    @Test
    @DisplayName("Should return true when exists a registration already created")
    public void returnTrueWhenRegistrationExists(){
        String registration = "123";
        Registration registration_attribute = createNewRegistration(registration);
        entityManager.persist(registration_attribute);

        boolean exists = repository.existsByRegistration(registration);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when doesn't exist a registration_attribute with a registration already created")
    public void returnFalseWhenRegistrationAttributeDoesntExist(){
        String registration = "123";

        boolean exists = repository.existsByRegistration(registration);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should get a registration by id")
    public void findByIdTest(){
        Registration registration_attribute = createNewRegistration("323");
        entityManager.persist(registration_attribute);
        Optional<Registration> foundRegistration = repository.
                findById(registration_attribute.getId());

        assertThat(foundRegistration.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Should save a registration")
    public void saveRegistrationTest(){
        Registration registration_attribute = createNewRegistration("323");
        Registration savedRegistration = repository.save(registration_attribute);

        assertThat(savedRegistration.getId()).isNotNull();
    }

    @Test
    @DisplayName("Should delete a registration from the base")
    public void deleteRegistration(){
        Registration registration_attribute = createNewRegistration("323");
        entityManager.persist(registration_attribute);

        Registration foundRegistration = entityManager
                .find(Registration.class, registration_attribute.getId());

        repository.delete(foundRegistration);

        Registration deleteRegistration = entityManager
                .find(Registration.class, registration_attribute.getId());
        assertThat(deleteRegistration).isNull();
    }

    private Registration createNewRegistration(String registration) {
        return Registration.builder()
                .name("Jasmini Santos")
                .dateOfRegistration(LocalDate.now().toString())
                .registration(registration)
                .build();
    }
}
