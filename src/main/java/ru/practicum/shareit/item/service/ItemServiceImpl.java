package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.ItemValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithDateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemWithDateMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) throws ItemValidationException {
        User owner = userService.getUserEntityById(userId);
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            throw new ItemValidationException("Название вещи не может быть пустым");
        }

        Item item = ItemMapper.toItem(itemDto, owner);
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Вещь не найдена"));

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new NoSuchElementException("Вещь не найдена");
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(existingItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemWithDateDto getItemById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Вещь не найдена"));
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime lastBooking = getLastBooking(itemId, now);
        LocalDateTime nextBooking = getNextBooking(itemId, now);

        return ItemWithDateMapper.toDtoWithDate(item, lastBooking, nextBooking);
    }

    @Override
    public List<ItemWithDateDto> getItemsByOwner(Long ownerId) {
        List<Item> items = itemRepository.findByOwnerId(ownerId);
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now();

        List<Object[]> lastBookings = itemRepository.findLastBookings(itemIds, now);
        List<Object[]> nextBookings = itemRepository.findNextBookings(itemIds, now);

        Map<Long, LocalDateTime> lastBookingMap = lastBookings.stream()
                .collect(Collectors.toMap(result -> (Long) result[0], result -> (LocalDateTime) result[1]));
        Map<Long, LocalDateTime> nextBookingMap = nextBookings.stream()
                .collect(Collectors.toMap(result -> (Long) result[0], result -> (LocalDateTime) result[1]));

        return items.stream()
                .map(item -> {
                    LocalDateTime lastBooking = lastBookingMap.get(item.getId());
                    LocalDateTime nextBooking = nextBookingMap.get(item.getId());
                    return ItemWithDateMapper.toDtoWithDate(item, lastBooking, nextBooking);
                })
                .collect(Collectors.toList());
    }


    @Override
    public List<ItemDto> searchItems(String text) {
        if (!StringUtils.hasText(text)) {
            return Collections.emptyList();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) throws ItemNotAvailableException {
        User author = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NoSuchElementException("Вещь не найдена"));

        boolean hasBooked = bookingRepository.existsByBooker_IdAndItem_IdAndEndIsBefore(userId, itemId,
                LocalDateTime.now());

        if (!hasBooked) {
            throw new ItemNotAvailableException("Пользователь не арендовал вещь или срок аренды не закончился");
        }

        Comment comment = CommentMapper.toComment(commentDto, item, author);
        comment.setCreated(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.toCommentDto(savedComment);
    }

    private LocalDateTime getLastBooking(Long itemId, LocalDateTime now) {
        return bookingRepository.findLastBooking(itemId, now).stream()
                .findFirst()
                .map(Booking::getStart)
                .orElse(null);
    }

    private LocalDateTime getNextBooking(Long itemId, LocalDateTime now) {
        return bookingRepository.findNextBooking(itemId, now).stream()
                .findFirst()
                .map(Booking::getStart)
                .orElse(null);
    }
}