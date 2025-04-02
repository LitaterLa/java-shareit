package ru.practicum.shareit.booking.dto;

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
class NewBookingRequestTest {

    private final JacksonTester<NewBookingRequest> json;

    public static NewBookingRequest newBookingRequest() {
        return NewBookingRequest.builder()
                .start(UtilTestDataClass.TestBooking.waitingScarfBooking().getStart())
                .end(UtilTestDataClass.TestBooking.waitingScarfBooking().getEnd())
                .itemId(UtilTestDataClass.TestItem.beret().getId())
                .build();
    }

    @Test
    void testNewBookingRequest() throws Exception {

        JsonContent<NewBookingRequest> result = json.write(newBookingRequest());

        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2025-02-01T14:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-02-10T18:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);

    }

}