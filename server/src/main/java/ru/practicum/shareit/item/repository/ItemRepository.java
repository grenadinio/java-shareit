package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i " +
            "FROM Item i " +
            "WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%'))" +
            "AND i.available = true")
    List<Item> searchAllByTextInNameOrDescription(String text);

    @Query("SELECT i FROM Item i WHERE i.owner.id = :id")
    List<Item> findAllByOwnerId(Long id);

    @Query("SELECT i FROM Item i WHERE i.request.id = :requestId")
    List<Item> findAllByRequestId(Long requestId);
}

