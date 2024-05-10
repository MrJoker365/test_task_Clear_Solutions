package com.example.test_task_users.controller;

import com.example.test_task_users.dto.DeleteDto;
import com.example.test_task_users.dto.UsersDto;
import com.example.test_task_users.model.Users;
import com.example.test_task_users.repo.UsersRepo;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
@Data
public class UsersController {

    @Value("${filter.age}")
    private int ageLimit;

    private final UsersRepo usersRepo;



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        StringBuilder errorMessage = new StringBuilder();
        result.getFieldErrors().forEach(fieldError -> {
            errorMessage.append(fieldError.getField())
                    .append(" ")
                    .append(fieldError.getDefaultMessage())
                    .append(". ");
        });
        return ResponseEntity.badRequest().body(errorMessage.toString());
    }







    @PostMapping("/create_user")
    public ResponseEntity<?> create (@RequestBody @Valid Users user) {

        Date birthDate = user.getBirthDate();
        LocalDate localBirthDate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate now = LocalDate.now();
        Period period = Period.between(localBirthDate, now);

        if (period.getYears() <= ageLimit) {
            return ResponseEntity.badRequest().body("You must be over 18 years old");
        }

        return new ResponseEntity<>(usersRepo.save(user), HttpStatus.OK);
    }




    @GetMapping("/search_by_birth_date")
    public ResponseEntity<?> searchByBirthDay(@RequestParam(name = "fromDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate,
                                              @RequestParam(name = "toDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date toDate)  {

        if (fromDate.after(toDate)) {
            return ResponseEntity.badRequest().body("From date must be less than to date.");
        }

        return new ResponseEntity<>(usersRepo.findAllByBirthDateBetween(fromDate, toDate), HttpStatus.OK);
    }






    @PutMapping("/update_some")
    public ResponseEntity<?> updateSomeUserField(@RequestBody @Valid UsersDto usersDto) {
        Users users;
        try {
            users = usersRepo.findAllByEmail(usersDto.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Such email does not exist: " + usersDto.getEmail()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

//        if (usersDto.getEmail() != null) {
//            users.setEmail(usersDto.getEmail());
//        }
//        if (usersDto.getFirstName() != null) {
//            users.setFirstName(usersDto.getFirstName());
//        }
//        if (usersDto.getLastName() != null) {
//            users.setLastName(usersDto.getLastName());
//        }
//        if (usersDto.getBirthDate() != null) {
//            users.setBirthDate(usersDto.getBirthDate());
//        }
//        if (usersDto.getAddress() != null) {
//            users.setAddress(usersDto.getAddress());
//        }
//        if (usersDto.getPhoneNumber() != null) {
//            users.setPhoneNumber(usersDto.getPhoneNumber());
//        }



//        це те ж саме що в закоментованому полі
        Field[] fields = usersDto.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(usersDto);
                if (value != null) {
                    Field userField = Users.class.getDeclaredField(field.getName());
                    userField.setAccessible(true);
                    userField.set(users, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        Users updatedUser;

        try {
            updatedUser = usersRepo.save(users);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }catch (TransactionSystemException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }



    @PutMapping("/update_all")
    public ResponseEntity<?> updateAllUserField(@RequestBody @Valid Users updateUser) {
        Users users;
        try {
            users = usersRepo.findAllByEmail(updateUser.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Such email does not exist: " + updateUser.getEmail()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }


        updateUser.setId(users.getId());


        return new ResponseEntity<>(updateUser, HttpStatus.OK);
    }


    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestBody @Valid DeleteDto deleteDto) {
        Users users;
        try {
            users = usersRepo.findAllByEmail(deleteDto.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Such email does not exist: " + deleteDto.getEmail()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }


        try {
            usersRepo.delete(users);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting user: " + e.getMessage());
        }



        return ResponseEntity.ok().body("Successfully deleted");
    }





}
