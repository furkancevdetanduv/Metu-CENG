package com.pisti.server.services;

import com.pisti.server.model.GameScore;
import com.pisti.server.model.User;
import com.pisti.server.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Finds user with given user id if user exists
     *
     * @param id id of the user
     * @return User
     */
    public User getUserByID(long id){
        if(userRepository.findById(id).isPresent()){
            return userRepository.findById(id).get();
        }
        return null;
    }

    /**
     * Returns all users
     *
     * @return List of users.
     */
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    /**
     * Adds new user to the system
     *
     * @param newUser User object to be add
     * @return true if operation successful, false otherwise
     */
    public boolean addUser(User newUser){
        if (newUser == null) return false;
        else if(userRepository.findByName(newUser.getName()) != null){
            return false;
        }

        newUser.setPassword(Integer.toString(newUser.getPassword().hashCode()));
        userRepository.save(newUser);
        return true;
    }

    /**
     * Changes the given users password
     *
     * @param user the user whose password is to be changed
     * @param newPassword new password of the user
     * @return true if operation successful, false otherwise
     */
    public boolean changePassword(User user, String newPassword){
        if(user == null) return false;
        else if (userRepository.findById(user.getUserId()).isEmpty()) return false;

        user.setPassword(Integer.toString(newPassword.hashCode()));
        return true;
    }

    /**
     * Changes the given users password
     *
     * @param user the user whose email is to be changed
     * @param newEmail new email of user
     * @return true if operation successful, false otherwise
     */
    public boolean changeEmail(User user, String newEmail){
        if (user==null) return false;
        else if(userRepository.findById(user.getUserId()).isEmpty()) return false;

        user.setEmail(newEmail);
        return true;
    }

    /**
     * user log in
     *
     * @param name username of the user
     * @param password password of the user
     * @return true if login successful, false otherwise
     */
    public boolean login(String name, String password){
        User loginUser = userRepository.findByName(name);
        if(loginUser == null) return false;
        return loginUser.getPassword().equals(Integer.toString(password.hashCode()));
    }

    public boolean findUser(JSONObject jsonUser) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> userRoot = criteriaQuery.from(User.class);
        List<Predicate> predicates = new ArrayList<>();

        if (jsonUser.has("name")) {
            predicates.add(criteriaBuilder.equal(userRoot.get("name"), jsonUser.get("name")));
        }
        if (jsonUser.has("email")) {
            predicates.add(criteriaBuilder.equal(userRoot.get("email"), jsonUser.get("email")));
        }
        if (jsonUser.has("password")) {
            predicates.add(criteriaBuilder.equal(userRoot.get("password"), jsonUser.get("password").hashCode()));
        }

        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(criteriaQuery).getResultList().size() > 0;
    }
}
