package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.dto.AccountDTO;

import hcmute.fit.event_management.entity.Account;
import hcmute.fit.event_management.entity.AccountRole;
import hcmute.fit.event_management.entity.Role;
import hcmute.fit.event_management.entity.keys.AccountRoleId;
import hcmute.fit.event_management.repository.AccountRepository;
import hcmute.fit.event_management.repository.AccountRoleRepository;
import hcmute.fit.event_management.repository.RoleRepository;
import hcmute.fit.event_management.service.IAccountService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class AccountServiceImpl implements IAccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AccountRoleRepository accountRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    Logger logger = LoggerFactory.getLogger(this.getClass());
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public List<Account> findAllById(Iterable<Integer> integers) {
        return accountRepository.findAllById(integers);
    }

    @Override
    public long count() {
        return accountRepository.count();
    }

    @Override
    public void delete(Account entity) {
        accountRepository.delete(entity);
    }

    @Override
    public void deleteAll() {
        accountRepository.deleteAll();
    }

    @Override
    public void deleteAllById(Iterable<? extends Integer> integers) {
        accountRepository.deleteAllById(integers);
    }

    @Override
    public <S extends Account> S save(S entity) {
        return accountRepository.save(entity);
    }

    @Override
    public List<Account> findAll(Sort sort) {
        return accountRepository.findAll(sort);
    }

    @Override
    public <S extends Account> Optional<S> findOne(Example<S> example) {
        return accountRepository.findOne(example);
    }

    @Override
    public AccountDTO DTO(Account account) {
        List<String> roles = new ArrayList<>();
        AccountDTO accountDTO = new AccountDTO();
        BeanUtils.copyProperties(account, accountDTO);
        for (AccountRole accountRole : account.getListAccountRoles()) {
            roles.add(accountRole.getRole().getName());
        }
        accountDTO.setRoles(roles);
        return accountDTO;
    }
    @Override
    public List<AccountDTO> getAllAccountDTOs() {
        List<Account> listAccounts = findAll();
        return listAccounts.stream()
                .map(this::DTO)
                .toList();
    }
    @Transactional
    public int addOrUpdateAccount(boolean isUpdate, AccountDTO accountDTO) {
        Optional<Account> existingAccount = findbyEmail(accountDTO.getEmail());

        if (!isUpdate) {
            if (existingAccount.isPresent()) {
                logger.warn("Account creation failed: Account already exists");
                return 409;
            }
            return createAccount(accountDTO);
        }

        if (existingAccount.isEmpty()) {
            logger.warn("Account update failed: Account not found");
            return 404;
        }
        return updateAccount(existingAccount.get(), accountDTO);
    }

    private int createAccount(AccountDTO accountDTO) {
        Account account = new Account();
        accountDTO.setPassword(passwordEncoder.encode(accountDTO.getPassword()));
        BeanUtils.copyProperties(accountDTO, account);
        account.setActive(accountDTO.isActive());
        save(account);

        createAccountRoles(accountDTO, account);

        logger.info("Account created successfully");
        return 201;
    }

    private int updateAccount(Account existingAccount, AccountDTO accountDTO) {
        accountDTO.setAccountID(existingAccount.getAccountID());
        accountRoleRepository.deleteAllByAccount(existingAccount);

        Account updatedAccount = new Account();
        BeanUtils.copyProperties(accountDTO, updatedAccount);
        updatedAccount.setPassword(passwordEncoder.encode(accountDTO.getPassword()));
        updatedAccount.setActive(accountDTO.isActive());
        save(updatedAccount);

        createAccountRoles(accountDTO, updatedAccount);

        logger.info("Account updated successfully");
        return 200;
    }

    private void createAccountRoles(AccountDTO accountDTO, Account account) {
        List<AccountRole> accountRoles = new ArrayList<>();
        for (String role : accountDTO.getRoles()) {
            Optional<Role> roleOptional = roleRepository.findByName(role);
            roleOptional.ifPresent(roleEntity -> {
                AccountRoleId accountRoleId = new AccountRoleId(account.getAccountID(), roleEntity.getRoleID());
                AccountRole accountRole = new AccountRole(accountRoleId, account, new Role(roleEntity.getRoleID(), roleEntity.getName()));
                accountRoles.add(accountRole);
            });
        }
        accountRoleRepository.saveAll(accountRoles);
    }

    @Override
    public Optional<Account> findbyEmail(String email) {
        return accountRepository.findByEmail(email);
    }

}
