package com.example.bank.service;

import com.example.bank.model.Acc;
import com.example.bank.model.TransactionType;
import com.example.bank.model.Transactional;
import com.example.bank.repository.AccountRepo;
import com.example.bank.repository.TransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AccountService {

    @Autowired
    AccountRepo accountRepo;

    @Autowired
    TransactionRepo transactionRepo;

    public static long autoGenerate(){
        return ThreadLocalRandom.current().nextLong(1_000_000_000L,10_000_000_000L);
    }


    public void createAccount(String name,double balance){
        Acc a=new Acc();
        long ano=autoGenerate();
        a.setAccountNumber(ano);
        a.setName(name);
        a.setBalance(balance);
        accountRepo.save(a);
        createAccStatement(a.getAccountNumber(),a.getBalance(),LocalDate.now(),TransactionType.deposit);
    }

    public void createAccStatement(long account_number, double balance, LocalDate date, TransactionType transactionType){
        Transactional transactional=new Transactional();
        transactional.setAccount(account_number);
        transactional.setAmount(balance);
        transactional.setDate(date);
        transactional.setTransactionType(transactionType);
        transactionRepo.save(transactional);
    }

    public void withdraw(long account_number,double amount){
        if(amount<0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Withdraw amount must be positive");
        }

        Acc account=accountRepo.findById(account_number)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        if (account.getBalance() < amount) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds");
        }

        account.setBalance(account.getBalance() - amount);
        accountRepo.save(account);

        createAccStatement(account_number, amount, LocalDate.now(),TransactionType.withdraw);

    }

    public void deposit(long account_number , double amount){
        if (amount <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deposit amount must be positive");
        }

        Acc account = accountRepo.findById(account_number)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        account.setBalance(account.getBalance() + amount);
        accountRepo.save(account);

        createAccStatement(account_number,amount,LocalDate.now(),TransactionType.deposit);
    }

    public void transfer(long fromAccount,long toAccount,double amount){
        if (amount <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transfer amount must be positive");
        }
        if (fromAccount == toAccount) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot transfer to the same account");
        }

        Acc sender=accountRepo.findById(fromAccount)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sender account not found"));

        Acc receiver=accountRepo.findById(toAccount)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Receiver account not found"));

        if(sender.getBalance() < amount){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds in sender account");
        }

        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        accountRepo.save(sender);
        accountRepo.save(receiver);

        createAccStatement(fromAccount,amount,LocalDate.now(),TransactionType.transferOut);
        createAccStatement(toAccount,amount,LocalDate.now(),TransactionType.transferIn);
    }

    public Acc showBalance(long account_number){
        return accountRepo.findById(account_number)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    }

    public List<Transactional> findByAccountNo(long account){
         accountRepo.findById(account);
         return transactionRepo.findByAccount(account);
    }

}
