package ru.practicum.shareit.booking;

import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@Component
@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public interface BookingMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "WAITING")
    @Mapping(target = "item", source = "item")
    @Mapping(target = "start", source = "bookingRequest.start")
    @Mapping(target = "end", source = "bookingRequest.end")
    @Mapping(target = "booker", source = "booker")
    Booking toBooking(User booker, Item item, NewBookingRequest bookingRequest);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "start", source = "start")
    @Mapping(target = "end", source = "end")
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
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                item.getOwner().getId(), item.getRequestId());
    }

    @Named("customMapUser")
    default User customMapUser(User user) {
        if (user == null) {
            return null;
        }
        Hibernate.initialize(user);
        return user;
    }

}




