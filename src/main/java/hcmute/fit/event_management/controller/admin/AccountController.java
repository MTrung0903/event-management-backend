package hcmute.fit.event_management.controller.admin;

import hcmute.fit.event_management.dto.AccountDTO;

import hcmute.fit.event_management.service.Impl.AccountRoleServiceImpl;
import hcmute.fit.event_management.service.Impl.AccountServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import payload.Response;


import java.util.List;


@RestController
@RequestMapping("/admin/account")
public class AccountController {
    @Autowired
    AccountServiceImpl accountServiceImpl;

    @Autowired
    AccountRoleServiceImpl accountRoleServiceImpl;

    @Autowired
    PasswordEncoder passwordEncoder;


    @GetMapping()
    public ResponseEntity<?> getAccount() {
        List<AccountDTO> listAccountDTO = accountServiceImpl.getAllAccountDTOs();
        Response response = new Response(200, "Success", listAccountDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<?> addAccount(@RequestBody AccountDTO accountDTO) {
        int statusCode = accountServiceImpl.addOrUpdateAccount(false, accountDTO);
        Response response;
        return switch (statusCode) {
            case 201 -> new ResponseEntity<>(new Response(201, "Account created successfully", accountDTO), HttpStatus.CREATED);
            case 409 -> new ResponseEntity<>(new Response(409, "Account creation failed: Account already exists", "False"), HttpStatus.CONFLICT);
            default -> new ResponseEntity<>(new Response(500, "Account creation failed due to an unknown error", "False"), HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    @PutMapping()
    public ResponseEntity<?> updateAccount(@RequestBody AccountDTO accountDTO) {
        int statusCode = accountServiceImpl.addOrUpdateAccount(true, accountDTO);
        Response response;
        return switch (statusCode) {
            case 200 -> new ResponseEntity<>(new Response(200, "Account updated successfully", accountDTO), HttpStatus.OK);
            case 404 -> new ResponseEntity<>(new Response(404, "Account update failed: Account not found", "False"), HttpStatus.NOT_FOUND);
            default ->  new ResponseEntity<>(new Response(500, "Account update failed due to an unknown error", "False"), HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }
}
