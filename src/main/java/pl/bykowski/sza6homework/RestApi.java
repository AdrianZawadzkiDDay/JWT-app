package pl.bykowski.sza6homework;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestApi {

    @GetMapping("/users")
    public String getUsers()
    {
        return "available for users and admin";
    }
}
