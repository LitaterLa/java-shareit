package ru.practicum.shareit.booking.dto;

import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public interface BookingMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "WAITING")
    @Mapping(target = "item", source = "item")
    @Mapping(target = "start", source = "bookingRequest.start", qualifiedByName = "mapStringToInstant")
    @Mapping(target = "end", source = "bookingRequest.end", qualifiedByName = "mapStringToInstant")
    @Mapping(target = "booker", source = "booker")
    Booking toBooking(User booker, Item item, NewBookingRequest bookingRequest);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "start", source = "start", qualifiedByName = "instantToString")
    @Mapping(target = "end", source = "end", qualifiedByName = "instantToString")
    @Mapping(target = "booker", source = "booker", qualifiedByName = "customMapUser")
    @Mapping(target = "item", source = "item", qualifiedByName = "customMapItem")
    @Mapping(target = "status", source = "status")
    BookingDto toBookingDto(Booking booking);

    @Named("customMapItem")
    default ItemDto customMapItem(Item item) {
        if (item == null) {
            return null;
        }
        Hibernate.initialize(item);
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), item.getOwner().getId());
    }


    @Named("customMapUser")
    default User customMapUser(User user) {
        if (user == null) {
            return null;
        }
        Hibernate.initialize(user);
        return new User(user.getId(), user.getName(), user.getEmail());
    }


    @Named("mapStringToInstant")
    default Instant mapStringToInstant(String instant) {

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime localDateTime = LocalDateTime.parse(instant, formatter);
        return localDateTime.atZone(ZoneId.of("UTC")).toInstant();
    }

    @Named("instantToString")
    default String instantToString(Instant instant) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.of("UTC"));
        return formatter.format(instant);
    }

}




