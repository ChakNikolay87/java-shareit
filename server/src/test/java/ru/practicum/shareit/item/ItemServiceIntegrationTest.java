package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemServiceIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("John Doe");
        owner.setEmail("john@example.com");
        owner = userRepository.save(owner);
    }

    @Test
    void createItemShouldSaveAndReturnItem() {
        Item item = new Item();
        item.setName("Laptop");
        item.setDescription("A powerful laptop");
        item.setAvailable(true);
        item.setOwner(owner);

        Item savedItem = itemRepository.save(item);
        Optional<Item> foundItem = itemRepository.findById(savedItem.getId());

        assertThat(foundItem).isPresent();
        assertThat(foundItem.get().getName()).isEqualTo("Laptop");
        assertThat(foundItem.get().getDescription()).isEqualTo("A powerful laptop");
        assertThat(foundItem.get().getOwner()).isEqualTo(owner);
    }

    @Test
    void createItemShouldThrowExceptionWhenNoOwner() {
        Item item = new Item();
        item.setName("Phone");
        item.setDescription("Smartphone with good camera");
        item.setAvailable(true);

        assertThrows(Exception.class, () -> itemRepository.save(item));
    }

    @Test
    void createItemShouldSaveItemSuccessfully() {
        Item item = new Item();
        item.setName("Laptop");
        item.setDescription("A powerful gaming laptop");
        item.setAvailable(true);
        item.setOwner(owner);

        Item savedItem = itemRepository.save(item);

        Optional<Item> foundItem = itemRepository.findById(savedItem.getId());
        assertThat(foundItem).isPresent();
        assertThat(foundItem.get().getName()).isEqualTo("Laptop");
        assertThat(foundItem.get().getDescription()).isEqualTo("A powerful gaming laptop");
        assertThat(foundItem.get().getOwner()).isEqualTo(owner);
    }

    @Test
    void createItemWithNullOwnerShouldThrowException() {
        Item item = new Item();
        item.setName("Tablet");
        item.setDescription("An Android tablet");
        item.setAvailable(true);
        item.setOwner(null);

        assertThrows(Exception.class, () -> itemRepository.save(item));
    }
}
