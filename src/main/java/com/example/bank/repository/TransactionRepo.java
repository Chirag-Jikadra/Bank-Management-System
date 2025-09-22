package com.example.bank.repository;

import com.example.bank.model.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepo extends JpaRepository<Transactional,Long> {
    List<Transactional> findByAccount(long account);
}
