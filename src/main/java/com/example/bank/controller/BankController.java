package com.example.bank.controller;

import com.example.bank.model.Acc;
import com.example.bank.model.Transactional;
import com.example.bank.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("bank")
public class BankController {

    @Autowired
    AccountService accountService;

    @PostMapping("openAccount")
    public String openAccount(@RequestBody Acc acc){
        accountService.createAccount(acc.getName(),acc.getBalance());
        return "Account Open Successfully!!!";
    }

    @GetMapping("/statement/{accountNumber}")
    public List<Transactional> statement(@PathVariable long accountNumber) {
        return accountService.findByAccountNo(accountNumber);
    }

    @PutMapping("/withdraw")
    public String withdraw(@RequestParam long account_number,@RequestParam double amount){
        accountService.withdraw(account_number,amount);
        return "Withdraw successful";
    }

    @PutMapping("/deposit/{account_number}/{amount}")
    public String deposit(@PathVariable long account_number,@PathVariable double amount){
        accountService.deposit(account_number,amount);
        return "Deposit Successful";
    }

    @PutMapping("/transfer/{fromAccount}/{toAccount}/{amount}")
    public String transfer(
            @PathVariable long fromAccount,
            @PathVariable long toAccount,
            @PathVariable double amount) {
        accountService.transfer(fromAccount, toAccount, amount);
        return "Transfer successful";
    }

    @GetMapping("/balance/{accountNumber}")
    public Acc showBalance(@PathVariable long accountNumber) {
        return accountService.showBalance(accountNumber);
    }




}
