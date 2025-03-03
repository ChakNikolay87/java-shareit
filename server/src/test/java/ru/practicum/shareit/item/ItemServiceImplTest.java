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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    }

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
    void getItemByIdTest() {
        ItemDto createdItem = itemService.createItem(owner.getId(), itemDto);
        ItemWithDateDto itemWithDate = itemService.getItemById(owner.getId(), createdItem.getId());

        assertNotNull(itemWithDate);
        assertEquals(createdItem.getId(), itemWithDate.getId());
        assertEquals(createdItem.getName(), itemWithDate.getName());
        assertEquals(createdItem.getDescription(), itemWithDate.getDescription());
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
    void searchItemsTest() {
        itemService.createItem(owner.getId(), itemDto);
        itemService.createItem(owner.getId(), new ItemDto(null, "Another Item", "Description",
                true, null, new ArrayList<>()));

        List<ItemDto> foundItems = itemService.searchItems("Test");

        assertNotNull(foundItems);
        assertEquals(1, foundItems.size());
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
    }
}