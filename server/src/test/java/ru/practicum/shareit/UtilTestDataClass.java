package ru.practicum.shareit;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPeriod;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.NewCommentRequest;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCommentBooking;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItem;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

public class UtilTestDataClass {

    public static class TestUser {
        public static UserDto paris() {
            return UserDto.builder()
                    .id(1)
                    .name("Paris")
                    .email("paris@france.fr")
                    .build();
        }

        public static NewUserRequest newParis() {
            return NewUserRequest.builder()
                    .name("Paris")
                    .email("paris@france.fr")
                    .build();
        }


        public static UpdateUserRequest updateParis() {
            return UpdateUserRequest.builder()
                    .name("Paris-is-the-capital")
                    .email("paris-capital@france.fr")
                    .build();
        }

        public static UserDto rome() {
            return UserDto.builder()
                    .id(2)
                    .name("Rome")
                    .email("rome@italy.it")
                    .build();
        }
    }

    public static class TestItemRequest {
        public static ItemRequestDto beautyRequest() {
            return ItemRequestDto.builder()
                    .id(1)
                    .description("of great beauty")
                    .userId(TestUser.paris().getId())
                    .created(LocalDateTime.now().minusDays(2))
                    .build();
        }

        public static ItemRequestDto tasteRequest() {
            return ItemRequestDto.builder()
                    .id(2)
                    .description("of great taste")
                    .userId(TestUser.rome().getId())
                    .created(LocalDateTime.now().minusDays(1))
                    .build();
        }
    }

    public static class TestItemComment {
        public static ItemCommentDto itemWithComments() {
            return ItemCommentDto.builder()
                    .id(1)
                    .name("Test Item")
                    .description("Test Description")
                    .available(true)
                    .ownerId(1)
                    .requestId(null)
                    .comments(List.of(
                            CommentDto.builder()
                                    .id(1)
                                    .text("First comment")
                                    .authorName("User1")
                                    .created("2023-01-01T10:00:00Z")
                                    .build()))
                    .build();
        }

    }


    public static class TestItemCommentBooking {
        public static ItemDtoCommentBooking createItemDtoCommentBooking() {
            return ItemDtoCommentBooking.builder()
                    .id(1)
                    .name("Дрель")
                    .description("Аккумуляторная дрель")
                    .available(true)
                    .ownerId(1)
                    .requestId(1)
                    .lastBooking(createLastBooking())
                    .nextBooking(createNextBooking())
                    .comments(createTestComments())
                    .build();
        }

        public static ItemDtoCommentBooking createAnotherItemDtoCommentBooking() {
            return ItemDtoCommentBooking.builder()
                    .id(2)
                    .name("Другая Дрель")
                    .description("Другая Аккумуляторная дрель")
                    .available(true)
                    .ownerId(1)
                    .requestId(1)
                    .lastBooking(createLastBooking())
                    .nextBooking(createNextBooking())
                    .comments(createTestComments())
                    .build();
        }

        private static BookingPeriod createLastBooking() {
            return BookingPeriod.builder()
                    .start(LocalDateTime.of(2023, 1, 1, 10, 0))
                    .end(LocalDateTime.of(2023, 1, 5, 12, 0))
                    .build();
        }

        private static BookingPeriod createNextBooking() {
            return BookingPeriod.builder()
                    .start(LocalDateTime.of(2023, 2, 1, 14, 0))
                    .end(LocalDateTime.of(2023, 2, 10, 18, 0))
                    .build();
        }

        private static List<CommentDto> createTestComments() {
            return List.of(
                    CommentDto.builder()
                            .id(1)
                            .text("Отличная дрель!")
                            .authorName("Иван")
                            .created("2023-01-06T15:30:00")
                            .build(),
                    CommentDto.builder()
                            .id(2)
                            .text("Сломалось после недели использования")
                            .authorName("Петр")
                            .created("2023-01-15T18:45:00")
                            .build()
            );
        }
    }

    public static class TestItem {
        public static ItemDto beret() {
            return ItemDto.builder()
                    .id(1)
                    .name("beret")
                    .description("a beautiful blue beret")
                    .available(true)
                    .ownerId(TestUser.paris().getId())
                    .build();
        }

        public static NewItemRequest newBeret() {
            return NewItemRequest.builder()
                    .name("beret")
                    .description("a beautiful blue beret")
                    .available(false)
                    .requestId(null)
                    .build();
        }

        public static NewItemRequest newScarf() {
            return NewItemRequest.builder()
                    .name("scarf")
                    .description("a beautiful blue scarf")
                    .available(true)
                    .requestId(null)
                    .build();
        }

        public static UpdateItem updateBeret() {
            return UpdateItem.builder()
                    .name("another beret")
                    .description("another beautiful blue beret")
                    .available(true)
                    .build();
        }

        public static ItemDto scarf() {
            return ItemDto.builder()
                    .id(2)
                    .name("scarf")
                    .description("a cashmere scarf")
                    .available(false)
                    .ownerId(TestUser.rome().getId())
                    .build();
        }
    }

    public static class TestBooking {
        public static BookingDto approvedBeretBooking() {
            return BookingDto.builder()
                    .id(1)
                    .start(LocalDateTime.of(2025, 1, 1, 10, 0))
                    .end(LocalDateTime.of(2025, 1, 5, 12, 0))
                    .item(TestItem.beret())
                    .status("APPROVED")
                    .booker(TestUser.rome())
                    .build();
        }

        public static BookingDto waitingScarfBooking() {
            return BookingDto.builder()
                    .id(2)
                    .start(LocalDateTime.of(2025, 2, 1, 14, 0))
                    .end(LocalDateTime.of(2025, 2, 10, 18, 0))
                    .item(TestItem.scarf())
                    .status("WAITING")
                    .booker(TestUser.paris())
                    .build();
        }
    }

    public static class TestComment {
        public static CommentDto beretComment() {
            return CommentDto.builder()
                    .id(1)
                    .text("Very comfortable beret!")
                    .itemId(TestItem.beret().getId())
                    .authorName(TestUser.rome().getName())
                    .created("2025-03-26T21:47:31.992350Z")
                    .build();
        }

        public static CommentDto scarfComment() {
            return CommentDto.builder()
                    .id(2)
                    .text("The scarf was too thin")
                    .itemId(TestItem.scarf().getId())
                    .authorName(TestUser.paris().getName())
                    .created("2025-03-26T21:47:31.992350Z")
                    .build();
        }

        public static NewCommentRequest newScarfComment() {
            return NewCommentRequest.builder()
                    .text("The scarf was too thin")
                    .build();
        }
    }
}

