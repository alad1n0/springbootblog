package edu.itstep.final_project_v1.domain.services;

import edu.itstep.final_project_v1.domain.models.Account;
import edu.itstep.final_project_v1.domain.models.Authority;
import edu.itstep.final_project_v1.domain.repositories.AccountRepository;
import edu.itstep.final_project_v1.domain.repositories.AuthorityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final AuthorityRepository authorityRepository;

    @Transactional
    public Account save(Account account) {
        if (account.getId() == null) {
            if (account.getAuthorities().isEmpty()) {
                Set<Authority> authorities = new HashSet<>();
                authorityRepository.findById("ROLE_USER").ifPresent(authorities::add);
                account.setAuthorities(authorities);
            }
            account.setCreatedAt(LocalDateTime.now());
        }
        account.setUpdatedAt(LocalDateTime.now());
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        return accountRepository.save(account);
    }

    public List<Account> findAllAccounts() {
        return accountRepository.findAll();
    }

    public Account findAccountById(Long id) {
        return accountRepository.findById(id).orElse(null);
    }

    public Optional<Account> findOneByEmail(String email) {
        return accountRepository.findOneByEmailIgnoreCase(email);
    }

    public void updateAccount(Account account) {
        if (account == null || account.getId() == null || !accountRepository.existsById(account.getId())) {
            throw new IllegalArgumentException("Account must exist to update.");
        }
        accountRepository.save(account);
    }

    public void deleteAccountById(Long id) {
        accountRepository.deleteById(id);
    }

    public Account getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
            return accountRepository.findOneByEmailIgnoreCase(username).orElse(null);
        }
        return null;
    }
}
