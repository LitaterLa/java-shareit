package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findByOwnerId(Integer userId);

    @Query("SELECT it from Item it WHERE LOWER(it.name) like LOWER(CONCAT('%', :text, '%')) or LOWER(it.description) like LOWER(CONCAT('%', :text, '%'))")
    List<Item> findAllByNameOrDescriptionContaining(@Param("text") String text);

}
