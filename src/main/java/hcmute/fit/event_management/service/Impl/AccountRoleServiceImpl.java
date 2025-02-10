package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.entity.AccountRole;
import hcmute.fit.event_management.entity.keys.AccountRoleId;
import hcmute.fit.event_management.repository.AccountRoleRepository;
import hcmute.fit.event_management.service.IAccountRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class AccountRoleServiceImpl implements IAccountRoleService {
    @Autowired
    AccountRoleRepository accountRoleRepository;

    @Override
    public <S extends AccountRole> List<S> findAll(Example<S> example) {
        return accountRoleRepository.findAll(example);
    }

    @Override
    public <S extends AccountRole> List<S> findAll(Example<S> example, Sort sort) {
        return accountRoleRepository.findAll(example, sort);
    }

    @Override
    public List<AccountRole> findAll() {
        return accountRoleRepository.findAll();
    }

    @Override
    public List<AccountRole> findAllById(Iterable<AccountRoleId> accountRoleIds) {
        return accountRoleRepository.findAllById(accountRoleIds);
    }

    @Override
    public <S extends AccountRole> S save(S entity) {
        return accountRoleRepository.save(entity);
    }

    @Override
    public Optional<AccountRole> findById(AccountRoleId accountRoleId) {
        return accountRoleRepository.findById(accountRoleId);
    }

    @Override
    public boolean existsById(AccountRoleId accountRoleId) {
        return accountRoleRepository.existsById(accountRoleId);
    }

    @Override
    public long count() {
        return accountRoleRepository.count();
    }

    @Override
    public void deleteById(AccountRoleId accountRoleId) {
        accountRoleRepository.deleteById(accountRoleId);
    }

    @Override
    public List<AccountRole> findAll(Sort sort) {
        return accountRoleRepository.findAll(sort);
    }

    @Override
    public Page<AccountRole> findAll(Pageable pageable) {
        return accountRoleRepository.findAll(pageable);
    }

    @Override
    public <S extends AccountRole> Optional<S> findOne(Example<S> example) {
        return accountRoleRepository.findOne(example);
    }

    @Override
    public <S extends AccountRole> Page<S> findAll(Example<S> example, Pageable pageable) {
        return accountRoleRepository.findAll(example, pageable);
    }

    @Override
    public <S extends AccountRole> long count(Example<S> example) {
        return accountRoleRepository.count(example);
    }

    @Override
    public <S extends AccountRole> boolean exists(Example<S> example) {
        return accountRoleRepository.exists(example);
    }

    @Override
    public <S extends AccountRole, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return accountRoleRepository.findBy(example, queryFunction);
    }

    public <S extends AccountRole> List<S> saveAll(Iterable<S> entities) {
        return accountRoleRepository.saveAll(entities);
    }
}
