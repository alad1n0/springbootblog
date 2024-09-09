package edu.itstep.final_project_v1.web.controllers;

import edu.itstep.final_project_v1.domain.models.Account;
import edu.itstep.final_project_v1.domain.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public String listUsers(Model model) {
        List<Account> accounts = accountService.findAllAccounts();
        model.addAttribute("accounts", accounts);
        return "admin/user_list";
    }

    @GetMapping("/{id}/edit")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Optional<Account> account = Optional.ofNullable(accountService.findAccountById(id));
        if (account.isPresent()) {
            model.addAttribute("account", account.get());
            return "admin/user_edit";
        } else {
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/{id}/edit")
    public String updateUser(@PathVariable Long id, @ModelAttribute Account accountForm) {
        Account existingAccount = accountService.findAccountById(id);
        if (existingAccount != null) {
            existingAccount.setEmail(accountForm.getEmail());
            existingAccount.setFirstName(accountForm.getFirstName());
            existingAccount.setLastName(accountForm.getLastName());
            accountService.updateAccount(existingAccount);
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        accountService.deleteAccountById(id);
        return "redirect:/admin/users";
    }
}
