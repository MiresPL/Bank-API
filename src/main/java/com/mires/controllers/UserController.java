package com.mires.controllers;

import com.mires.DemoApplication;
import com.mires.objects.User;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "/api", method = RequestMethod.POST)
public class UserController {

    @CrossOrigin
    @RequestMapping(path = "users/checkUsername", method = RequestMethod.POST, produces = "application/json")
    public String checkUserName(@RequestBody Map<String, Object> postData) {
        final String usernameToCheck = String.valueOf(postData.get("username"));
        return String.valueOf(DemoApplication.getMysqlManager().checkUsername(usernameToCheck));
    }

    @CrossOrigin
    @RequestMapping(path = "users/createNewUser", method = RequestMethod.POST, produces = "application/json")
    public boolean createUser(@RequestBody Map<String, Object> postData) {
        final User user = new User(
                UUID.randomUUID(),
                String.valueOf(postData.get("name")),
                String.valueOf(postData.get("surname")),
                String.valueOf(postData.get("email")),
                String.valueOf(postData.get("username")),
                String.valueOf(postData.get("password")),
                0
        );
        return DemoApplication.getMysqlManager().createNewUser(user);
    }


    @CrossOrigin
    @RequestMapping(path = "users/login", method = RequestMethod.POST, produces = "application/json")
    public String login(@RequestBody Map<String, Object> postData) {
        final String login = String.valueOf(postData.get("login"));
        final String password = String.valueOf(postData.get("password"));
        return String.valueOf(DemoApplication.getMysqlManager().login(login, password));
    }

    @CrossOrigin
    @RequestMapping(path = "users/getUser", method = RequestMethod.POST, produces = "application/json")
    public String getUser(@RequestBody Map<String, Object> postData) {
        final String login = String.valueOf(postData.get("login"));
        return DemoApplication.getMysqlManager().getUser(login);
    }

    @CrossOrigin
    @RequestMapping(path = "users/getBlikCode", method = RequestMethod.POST, produces = "application/json")
    public String getBlikCode(@RequestBody Map<String, Object> postData) {
        final String uuid = String.valueOf(postData.get("uuid"));
        final String code = this.generateBlikCode();
        if (DemoApplication.getMysqlManager().insertBlikCode(uuid, code.replace(" ", ""))) return code;
        else return "error";
    }


    @CrossOrigin
    @RequestMapping(path = "users/deposit", method = RequestMethod.POST, produces = "application/json")
    public String deposit(@RequestBody Map<String, Object> postData) {
        final String uuid = String.valueOf(postData.get("uuid"));
        final double amount = Double.parseDouble(String.valueOf(postData.get("amount")));
        return String.valueOf(DemoApplication.getMysqlManager().deposit(uuid, amount));
    }

    @CrossOrigin
    @RequestMapping(path = "users/payByBLIK", method = RequestMethod.POST, produces = "application/json")
    public String payByBLIK(@RequestBody Map<String, Object> postData) {
        final String code = String.valueOf(postData.get("code"));
        JSONObject userObject = DemoApplication.getMysqlManager().getUserByBLIKCode(code);
        if (userObject.isEmpty()) return "no-user-found-error";
        final double userBalance = Double.parseDouble(userObject.getString("balance").replace(",", "."));
        final double amount = Double.parseDouble(String.valueOf(postData.get("amount")));

        if (userBalance < amount) return "no-cash-error";
        else {
            if (DemoApplication.getMysqlManager().payByBLIK(userObject.getString("uuid"), amount)) return "success";
            else return "transaction-error";
        }
    }

    private String generateBlikCode() {
        final Random random = new Random();
        int digit1 = random.nextInt(10);
        int digit2 = random.nextInt(10);
        int digit3 = random.nextInt(10);
        int digit4 = random.nextInt(10);
        int digit5 = random.nextInt(10);
        int digit6 = random.nextInt(10);
        return String.format("%d%d%d %d%d%d", digit1, digit2, digit3, digit4, digit5, digit6);
    }
}
