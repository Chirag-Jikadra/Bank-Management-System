package com.example.bank.repository;

import com.example.bank.model.Acc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepo extends JpaRepository<Acc,Long> {
}
