package hcmute.fit.event_management.controller.admin;

import hcmute.fit.event_management.dto.UserDTO;

import hcmute.fit.event_management.service.Impl.UserRoleServiceImpl;
import hcmute.fit.event_management.service.Impl.UserServiceImpl;

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
    UserServiceImpl userServiceImpl;

    @Autowired
    UserRoleServiceImpl userRoleServiceImpl;

    @Autowired
    PasswordEncoder passwordEncoder;


    @GetMapping()
    public ResponseEntity<?> getAccount() {
        List<UserDTO> listUserDTO = userServiceImpl.getAllAccountDTOs();
        Response response = new Response(200, "Success", listUserDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<?> addAccount(@RequestBody UserDTO userDTO) {
        int statusCode = userServiceImpl.addOrUpdateAccount(false, userDTO);
        Response response;
        return switch (statusCode) {
            case 201 -> new ResponseEntity<>(new Response(201, "Account created successfully", userDTO), HttpStatus.CREATED);
            case 409 -> new ResponseEntity<>(new Response(409, "Account creation failed: Account already exists", "False"), HttpStatus.CONFLICT);
            default -> new ResponseEntity<>(new Response(500, "Account creation failed due to an unknown error", "False"), HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    @PutMapping()
    public ResponseEntity<?> updateAccount(@RequestBody UserDTO userDTO) {
        int statusCode = userServiceImpl.addOrUpdateAccount(true, userDTO);
        Response response;
        return switch (statusCode) {
            case 200 -> new ResponseEntity<>(new Response(200, "Account updated successfully", userDTO), HttpStatus.OK);
            case 404 -> new ResponseEntity<>(new Response(404, "Account update failed: Account not found", "False"), HttpStatus.NOT_FOUND);
            default ->  new ResponseEntity<>(new Response(500, "Account update failed due to an unknown error", "False"), HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }
}
