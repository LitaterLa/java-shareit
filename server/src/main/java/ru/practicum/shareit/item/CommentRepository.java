package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Query("select c from Comment c where c.item.id = ?1")
    List<Comment> findAllByItemId(Integer itemId);

    @Query("select c from Comment c where c.item.id in ?1")
    List<Comment> findAllByItemIds(List<Integer> itemIds);

}
