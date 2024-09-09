package edu.itstep.final_project_v1.web.config;

import edu.itstep.final_project_v1.domain.models.Account;
import edu.itstep.final_project_v1.domain.models.Authority;
import edu.itstep.final_project_v1.domain.repositories.AccountRepository;
import edu.itstep.final_project_v1.domain.repositories.AuthorityRepository;
import edu.itstep.final_project_v1.domain.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class SeedData implements CommandLineRunner {
    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public void run(String... args) throws Exception {

        Optional<Account> existingAdmin = accountRepository.findOneByEmailIgnoreCase("admin.admin@domain.com");
        if (existingAdmin.isEmpty()) {
            Authority admin = new Authority();
            admin.setName("ROLE_ADMIN");
            authorityRepository.save(admin);

            Account account = Account
                    .builder()
                    .firstName("admin")
                    .lastName("admin")
                    .email("admin.admin@domain.com")
                    .password("password")
                    .build();

            Set<Authority> authorities = new HashSet<>();
            authorityRepository.findById("ROLE_ADMIN").ifPresent(authorities::add);
            account.setAuthorities(authorities);

            accountService.save(account);
        }
    }
}