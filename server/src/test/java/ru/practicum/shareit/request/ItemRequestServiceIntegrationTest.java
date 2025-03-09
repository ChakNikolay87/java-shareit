package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user = userRepository.save(user);
    }

    @Test
    void createItemRequestShouldSaveRequestToDatabase() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Need a laptop");
        requestDto.setCreated(LocalDateTime.now());

        ItemRequest itemRequest = new ItemRequest(null, requestDto.getDescription(), user, requestDto.getCreated(), null);

        itemRequest = itemRequestRepository.save(itemRequest);

        Optional<ItemRequest> savedRequest = itemRequestRepository.findById(itemRequest.getId());
        assertThat(savedRequest).isPresent();
        assertThat(savedRequest.get().getDescription()).isEqualTo(requestDto.getDescription());
        assertThat(savedRequest.get().getRequestor().getId()).isEqualTo(user.getId());
    }

    @Test
    void findAllRequestsByUserShouldReturnRequests() {
        ItemRequest itemRequest1 = new ItemRequest(null, "Request 1", user, LocalDateTime.now(), null);
        ItemRequest itemRequest2 = new ItemRequest(null, "Request 2", user, LocalDateTime.now().plusDays(1), null);

        itemRequestRepository.save(itemRequest1);
        itemRequestRepository.save(itemRequest2);

        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(user.getId());
        assertThat(requests).hasSize(2);
        assertThat(requests).extracting(ItemRequest::getDescription).containsExactlyInAnyOrder("Request 1", "Request 2");
    }

    @Test
    void findByIdShouldReturnCorrectRequest() {
        // Создаем запрос
        ItemRequest itemRequest = new ItemRequest(null, "Request for a book", user, LocalDateTime.now(), null);
        itemRequest = itemRequestRepository.save(itemRequest);

        Optional<ItemRequest> foundRequest = itemRequestRepository.findById(itemRequest.getId());
        assertThat(foundRequest).isPresent();
        assertThat(foundRequest.get().getDescription()).isEqualTo("Request for a book");
        assertThat(foundRequest.get().getRequestor().getId()).isEqualTo(user.getId());
    }

    @Test
    void findAllRequestsByUserShouldReturnEmptyListWhenNoRequests() {
        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(user.getId());
        assertThat(requests).isEmpty();
    }

}
