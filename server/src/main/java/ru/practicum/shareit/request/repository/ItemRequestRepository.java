package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("SELECT i FROM ItemRequest i WHERE i.requester.id = :id")
    List<ItemRequest> findByRequesterId(Long id);

    Page<ItemRequest> findAllByOrderByCreatedDesc(Pageable pageable);
}
