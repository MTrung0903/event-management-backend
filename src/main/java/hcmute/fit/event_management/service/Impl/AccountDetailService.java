package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.dto.AccountDetail;
import hcmute.fit.event_management.entity.Account;


import hcmute.fit.event_management.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountDetailService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Kiểm tra xem listAccountRoles có dữ liệu không
        if (account.getListAccountRoles() == null || account.getListAccountRoles().isEmpty()) {

            System.out.println("User has no roles");
        }

        List<GrantedAuthority> authorities = account.getListAccountRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole().getName()))
                .collect(Collectors.toList());

        System.out.println("Authorities: " + authorities);
        return new AccountDetail(account.getEmail(), account.getPassword(), authorities);
    }
}
