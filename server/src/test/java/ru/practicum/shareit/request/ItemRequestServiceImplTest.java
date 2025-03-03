package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserService userService;

    private UserDto userDto;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(null, "Test User", "testuser@example.com");
        itemRequestDto = new ItemRequestDto(null, "Test Request", null,
                null, new ArrayList<>());
    }

    @Test
    void createItemRequestTest() {
        UserDto createdUser = userService.createUser(userDto);
        itemRequestDto.setRequestor(createdUser.getId());

        ItemRequestDto createdItemRequest = itemRequestService.createItemRequest(createdUser.getId(), itemRequestDto);

        assertNotNull(createdItemRequest);
        assertNotNull(createdItemRequest.getId());
        assertEquals(itemRequestDto.getDescription(), createdItemRequest.getDescription());
        assertEquals(createdUser.getId(), createdItemRequest.getRequestor());

        ItemRequest itemRequestFromDb = itemRequestRepository.findById(createdItemRequest.getId()).orElseThrow();
        assertEquals(itemRequestDto.getDescription(), itemRequestFromDb.getDescription());
        assertEquals(createdUser.getId(), itemRequestFromDb.getRequestor().getId());
    }

    @Test
    void getItemRequestsByUserTest() {
        UserDto createdUser = userService.createUser(userDto);
        itemRequestDto.setRequestor(createdUser.getId());

        ItemRequestDto createdItemRequest = itemRequestService.createItemRequest(createdUser.getId(), itemRequestDto);

        List<ItemRequestDto> itemRequests = itemRequestService.getItemRequestsByUser(createdUser.getId());

        assertNotNull(itemRequests);
        assertFalse(itemRequests.isEmpty());
        assertEquals(1, itemRequests.size());
        assertEquals(createdItemRequest.getId(), itemRequests.get(0).getId());
    }

    @Test
    void getAllItemRequestsTest() {
        UserDto createdUser = userService.createUser(userDto);
        itemRequestDto.setRequestor(createdUser.getId());

        ItemRequestDto createdItemRequest = itemRequestService.createItemRequest(createdUser.getId(), itemRequestDto);

        UserDto anotherUser = new UserDto(null, "Another User", "anotheruser@example.com");
        UserDto createdAnotherUser = userService.createUser(anotherUser);
        itemRequestDto.setRequestor(createdAnotherUser.getId());
        itemRequestService.createItemRequest(createdAnotherUser.getId(), itemRequestDto);

        List<ItemRequestDto> allItemRequests = itemRequestService.getAllItemRequests(createdUser.getId());

        assertNotNull(allItemRequests);
        assertFalse(allItemRequests.isEmpty());
        assertEquals(1, allItemRequests.size());
        assertNotEquals(createdItemRequest.getRequestor(), allItemRequests.get(0).getRequestor());
    }

    @Test
    void getItemRequestByIdTest() {
        UserDto createdUser = userService.createUser(userDto);
        itemRequestDto.setRequestor(createdUser.getId());

        ItemRequestDto createdItemRequest = itemRequestService.createItemRequest(createdUser.getId(), itemRequestDto);

        ItemRequestDto foundItemRequest = itemRequestService.getItemRequestById(createdUser.getId(), createdItemRequest.getId());

        assertNotNull(foundItemRequest);
        assertEquals(createdItemRequest.getId(), foundItemRequest.getId());
    }
}