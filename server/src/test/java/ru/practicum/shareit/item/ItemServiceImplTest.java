package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithDateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRequestService itemRequestService;

    private User owner;
    private ItemDto itemDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(new User(null, "Test Owner", "owner@example.com"));
        itemDto = new ItemDto(null, "Test Item", "Test Description", true,
                null, new ArrayList<>());
        commentDto = new CommentDto(null, "Test Comment", null, "Test Author",
                LocalDateTime.now());
    }


    @Test
    void createItemTest() {
        ItemDto createdItem = itemService.createItem(owner.getId(), itemDto);

        assertNotNull(createdItem);
        assertNotNull(createdItem.getId());
        assertEquals(itemDto.getName(), createdItem.getName());
        assertEquals(itemDto.getDescription(), createdItem.getDescription());
        assertTrue(createdItem.getAvailable());

        Item itemFromDb = itemRepository.findById(createdItem.getId()).orElseThrow();
        assertEquals(itemDto.getName(), itemFromDb.getName());
        assertEquals(itemDto.getDescription(), itemFromDb.getDescription());
        assertTrue(itemFromDb.getAvailable());

        ItemDto invalidItemDto = new ItemDto();
        invalidItemDto.setName("");
        assertThrows(IllegalArgumentException.class, () -> {
            itemService.createItem(owner.getId(), invalidItemDto);
        });

        invalidItemDto.setName(null);
        assertThrows(IllegalArgumentException.class, () -> {
            itemService.createItem(owner.getId(), invalidItemDto);
        });

        ItemDto itemWithRequestId = new ItemDto();
        itemWithRequestId.setName("Item with Request");
        itemWithRequestId.setRequestId(999L);
        assertThrows(IllegalArgumentException.class, () -> {
            itemService.createItem(owner.getId(), itemWithRequestId);
        });

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Test Request Description");
        ItemRequestDto createdRequest = itemRequestService.createItemRequest(owner.getId(), itemRequestDto);

        assertNotNull(createdRequest.getItems());
        assertTrue(createdRequest.getItems().isEmpty());
    }

//    @Test
//    void createItemRequestWithNullItemsShouldInitializeEmptyList() {
//        ItemRequestDto itemRequestDto = new ItemRequestDto();
//        itemRequestDto.setDescription("Test Request Description");
//
//        itemRequestDto.setItems(null);
//
//        ItemRequestDto createdRequest = itemRequestService.createItemRequest(owner.getId(), itemRequestDto);
//
//        assertNotNull(createdRequest.getItems(), "Items should not be null");
//        assertTrue(createdRequest.getItems().isEmpty(), "Items should be an empty list");
//    }
//
//
//    @Test
//    void createItemWithInvalidRequestIdShouldThrowException() {
//        itemDto.setRequestId(999L);
//        assertThrows(IllegalArgumentException.class, () -> itemService.createItem(owner.getId(), itemDto));
//    }
//
//
//    @Test
//    void createItemWithEmptyNameShouldThrowException() {
//        itemDto.setName("");
//        assertThrows(IllegalArgumentException.class, () -> itemService.createItem(owner.getId(), itemDto));
//    }



    @Test
    void updateItemTest() {
        ItemDto createdItem = itemService.createItem(owner.getId(), itemDto);

        createdItem.setName("Updated Item");
        createdItem.setDescription("Updated Description");
        ItemDto updatedItem = itemService.updateItem(owner.getId(), createdItem.getId(), createdItem);

        assertEquals("Updated Item", updatedItem.getName());
        assertEquals("Updated Description", updatedItem.getDescription());

        Item itemFromDb = itemRepository.findById(updatedItem.getId()).orElseThrow();
        assertEquals("Updated Item", itemFromDb.getName());
        assertEquals("Updated Description", itemFromDb.getDescription());
    }

    @Test
    void updateItemTestOwnerMismatch() {
        ItemDto createdItem = itemService.createItem(owner.getId(), itemDto);

        createdItem.setName("Updated Item");
        createdItem.setDescription("Updated Description");

        assertThrows(NoSuchElementException.class, () ->
                        itemService.updateItem(2L, createdItem.getId(), createdItem),
                "Вещь не найдена");
    }

    @Test
    void updateItemItemNotFoundThrowsNoSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> {
            itemService.updateItem(owner.getId(), 999L, itemDto);
        });
    }


    @Test
    void updateItemValidOwnerUpdatesItem() {
        Item item = new Item(null, "Old Item", "Old Description", true, owner, null, Collections.emptyList());
        item = itemRepository.save(item);

        itemDto.setName("Updated Item");
        itemDto.setDescription("Updated Description");

        ItemDto updatedItemDto = itemService.updateItem(owner.getId(), item.getId(), itemDto);

        assertEquals("Updated Item", updatedItemDto.getName());
        assertEquals("Updated Description", updatedItemDto.getDescription());

        Item updatedItem = itemRepository.findById(updatedItemDto.getId()).orElseThrow();
        assertEquals("Updated Item", updatedItem.getName());
        assertEquals("Updated Description", updatedItem.getDescription());
    }


    @Test
    void getItemByIdTest() {
        ItemDto createdItem = itemService.createItem(owner.getId(), itemDto);
        ItemWithDateDto itemWithDate = itemService.getItemById(owner.getId(), createdItem.getId());

        assertNotNull(itemWithDate);
        assertEquals(createdItem.getId(), itemWithDate.getId());
        assertEquals(createdItem.getName(), itemWithDate.getName());
        assertEquals(createdItem.getDescription(), itemWithDate.getDescription());
    }

    @Test
    void testItemNotFoundThrowsException() {
        Long nonExistentItemId = 999L;
        assertThrows(NoSuchElementException.class, () -> {
            itemService.getItemById(owner.getId(), nonExistentItemId);
        }, "Вещь не найдена");
    }


    @Test
    void getItemsByOwnerTest() {
        itemService.createItem(owner.getId(), itemDto);
        itemService.createItem(owner.getId(), new ItemDto(null, "Another Item", "Description",
                true, null, new ArrayList<>()));

        List<ItemWithDateDto> itemsByOwner = itemService.getItemsByOwner(owner.getId());

        assertNotNull(itemsByOwner);
        assertEquals(2, itemsByOwner.size());
    }

    @Test
    void testBookingMaps() {
        List<Object[]> lastBookings = List.of(
                new Object[]{1L, LocalDateTime.of(2025, 3, 5, 10, 0)},
                new Object[]{2L, LocalDateTime.of(2025, 3, 6, 12, 0)}
        );
        List<Object[]> nextBookings = List.of(
                new Object[]{1L, LocalDateTime.of(2025, 3, 7, 14, 0)},
                new Object[]{2L, LocalDateTime.of(2025, 3, 8, 16, 0)}
        );

        Map<Long, LocalDateTime> lastBookingMap = lastBookings.stream()
                .collect(Collectors.toMap(result -> (Long) result[0], result -> (LocalDateTime) result[1]));
        Map<Long, LocalDateTime> nextBookingMap = nextBookings.stream()
                .collect(Collectors.toMap(result -> (Long) result[0], result -> (LocalDateTime) result[1]));

        assertNotNull(lastBookingMap);
        assertEquals(2, lastBookingMap.size());
        assertEquals(LocalDateTime.of(2025, 3, 5, 10, 0), lastBookingMap.get(1L));
        assertEquals(LocalDateTime.of(2025, 3, 6, 12, 0), lastBookingMap.get(2L));

        assertNotNull(nextBookingMap);
        assertEquals(2, nextBookingMap.size());
        assertEquals(LocalDateTime.of(2025, 3, 7, 14, 0), nextBookingMap.get(1L));
        assertEquals(LocalDateTime.of(2025, 3, 8, 16, 0), nextBookingMap.get(2L));
    }


    @Test
    void searchItemsTest() {
        itemService.createItem(owner.getId(), itemDto);
        itemService.createItem(owner.getId(), new ItemDto(null, "Another Item", "Description",
                true, null, new ArrayList<>()));

        List<ItemDto> foundItems = itemService.searchItems("Test");

        assertNotNull(foundItems);
        assertEquals(1, foundItems.size());
    }

    @Test
    void searchItemsEmptyTextReturnsEmptyList() {
        List<ItemDto> result = itemService.searchItems(null);
        assertTrue(result.isEmpty(), "Expected empty list when text is null");

        result = itemService.searchItems("");
        assertTrue(result.isEmpty(), "Expected empty list when text is empty");

        result = itemService.searchItems("    ");
        assertTrue(result.isEmpty(), "Expected empty list when text contains only spaces");
    }


    @Test
    void addCommentTest() throws ItemNotAvailableException {
        User user = userRepository.save(new User(null, "Test User", "testuser@example.com"));
        ItemDto createdItem = itemService.createItem(owner.getId(), itemDto);

        Booking booking = new Booking();
        booking.setItem(itemRepository.findById(createdItem.getId()).orElseThrow());
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().minusHours(1));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        CommentDto createdComment = itemService.addComment(user.getId(), createdItem.getId(), commentDto);

        assertNotNull(createdComment);
        assertNotNull(createdComment.getId());
        assertEquals(commentDto.getText(), createdComment.getText());

        Comment commentFromDb = commentRepository.findById(createdComment.getId()).orElseThrow();
        assertEquals(commentDto.getText(), commentFromDb.getText());
        assertEquals(createdItem.getId(), commentFromDb.getItem().getId());
        assertEquals(user.getId(), commentFromDb.getAuthor().getId());

        User userWithoutBooking = userRepository.save(new User(null, "Another User", "anotheruser@example.com"));

        assertThrows(ItemNotAvailableException.class, () -> {
            itemService.addComment(userWithoutBooking.getId(), createdItem.getId(), commentDto);
        });
    }


    @Test
    void testAddCommentThrowsExceptionWhenUserOrItemNotFound() {
        Long nonExistentUserId = 999L;
        Long nonExistentItemId = 999L;

        CommentDto validCommentDto = new CommentDto(null, "Test comment", nonExistentItemId, "Author", LocalDateTime.now());

        assertThrows(NoSuchElementException.class, () -> {
            itemService.addComment(nonExistentUserId, 1L, validCommentDto);
        }, "Пользователь не найден");

        assertThrows(NoSuchElementException.class, () -> {
            itemService.addComment(1L, nonExistentItemId, validCommentDto);
        }, "Вещь не найдена");
    }

    @Test
    void getItemRequestByIdShouldThrowNoSuchElementExceptionWhenNotFound() {
        Long invalidRequestId = 999L;

        assertThrows(NoSuchElementException.class, () -> {
            itemRequestService.getItemRequestById(owner.getId(), invalidRequestId);
        });
    }

}