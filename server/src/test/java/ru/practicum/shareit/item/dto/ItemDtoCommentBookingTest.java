package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.UtilTestDataClass;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemDtoCommentBookingTest {
    private final JacksonTester<ItemDtoCommentBooking> json;

    @Test
    void itemDtoCommentBookingTest() throws Exception {
        JsonContent<ItemDtoCommentBooking> result = json.write(UtilTestDataClass.TestItemCommentBooking.createItemDtoCommentBooking());

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Аккумуляторная дрель");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);

        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start").isEqualTo("2023-01-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.end").isEqualTo("2023-01-05T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.start").isEqualTo("2023-02-01T14:00:00");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.end").isEqualTo("2023-02-10T18:00:00");

        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("Отличная дрель!");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("Иван");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created").isEqualTo("2023-01-06T15:30:00");

        assertThat(result).extractingJsonPathNumberValue("$.comments[1].id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.comments[1].text").isEqualTo("Сломалось после недели использования");
        assertThat(result).extractingJsonPathStringValue("$.comments[1].authorName").isEqualTo("Петр");
        assertThat(result).extractingJsonPathStringValue("$.comments[1].created").isEqualTo("2023-01-15T18:45:00");
    }

}