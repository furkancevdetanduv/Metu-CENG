package com.pisti.server.controller;

import com.pisti.server.model.GameScore;
import com.pisti.server.model.User;
import com.pisti.server.services.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequestMapping("api/users")
@RestController
public class UserController {

    /**
     * User service object that is autowired to use in controller.
     */
    private final UserService userService;

    /**
     * User score controller constructor from user service.
     * <p>
     * @param userService User service to be used by the constructor.
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Finds user with given user id if user exists
     *
     * @param id id of the user
     * @return User
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserByID(@RequestBody long id){
        return ResponseEntity.ok().body(this.userService.getUserByID(id));
    }

    /**
     * Returns all users
     *
     * @return List of users.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<User>>  getAllUsers(){
        return ResponseEntity.ok().body(this.userService.getAllUsers());
    }

    /**
     * Adds new user to the system
     *
     * @param newUser User object to be add
     * @return true if operation successful, false otherwise
     */
    @PostMapping("/add")
    public ResponseEntity<Boolean> addUser(@RequestBody User newUser){
        return ResponseEntity.ok().body(this.userService.addUser(newUser));
    }

    /**
     * Changes the given users password
     *
     * @param user the user whose password is to be changed
     * @param newPassword new password of the user
     * @return true if operation successful, false otherwise
     */
    @PostMapping("/changePassword")
    public ResponseEntity<Boolean> changePassword(User user, String newPassword){
        return ResponseEntity.ok().body(this.userService.changePassword(user,newPassword));
    }

    /**
     * Changes the given users E-mail
     *
     * @param user the user whose email is to be changed
     * @param newEmail new email of user
     * @return true if operation successful, false otherwise
     */
    @PostMapping("/changeEmail")
    public ResponseEntity<Boolean> changeEmail(User user, String newEmail){
        return ResponseEntity.ok().body(this.userService.changeEmail(user,newEmail));
    }

    /**
     * user log in
     *
     * @param name username of the user
     * @param password password of the user
     * @return true if login successful, false otherwise
     */
    @GetMapping("/login")
    public ResponseEntity<Boolean> login(@RequestParam String name, @RequestParam String password){
        return ResponseEntity.ok().body(this.userService.login(name, password));
    }

    @PostMapping("/find")
    public ResponseEntity<Boolean> findUser(@RequestBody String jsonUser) {
        return ResponseEntity.ok().body(this.userService.findUser(new JSONObject(jsonUser)));
    }

}
