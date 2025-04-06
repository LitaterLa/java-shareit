package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findAllByOwnerId(Integer userId);

    List<Item> findAllByRequestId(Integer requestId);

    @Query("select i from Item i where i.requestId in :requestIds")
    List<Item> findAllByRequestIds(@Param("requestIds") List<Integer> requestIds);

    @Query("SELECT it from Item it WHERE LOWER(it.name) like LOWER(CONCAT('%', :text, '%')) or LOWER(it.description) like LOWER(CONCAT('%', :text, '%'))")
    List<Item> findAllByNameOrDescriptionContaining(@Param("text") String text, PageRequest pageRequest);

}
