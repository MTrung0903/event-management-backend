package hcmute.fit.event_management.service;

import hcmute.fit.event_management.entity.AccountRole;
import hcmute.fit.event_management.entity.keys.AccountRoleId;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface IAccountRoleService {
    <S extends AccountRole> List<S> findAll(Example<S> example);

    <S extends AccountRole> List<S> findAll(Example<S> example, Sort sort);

    List<AccountRole> findAll();

    List<AccountRole> findAllById(Iterable<AccountRoleId> accountRoleIds);

    <S extends AccountRole> S save(S entity);

    Optional<AccountRole> findById(AccountRoleId accountRoleId);

    boolean existsById(AccountRoleId accountRoleId);

    long count();

    void deleteById(AccountRoleId accountRoleId);

    List<AccountRole> findAll(Sort sort);

    Page<AccountRole> findAll(Pageable pageable);

    <S extends AccountRole> Optional<S> findOne(Example<S> example);

    <S extends AccountRole> Page<S> findAll(Example<S> example, Pageable pageable);

    <S extends AccountRole> long count(Example<S> example);

    <S extends AccountRole> boolean exists(Example<S> example);

    <S extends AccountRole, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction);
}
