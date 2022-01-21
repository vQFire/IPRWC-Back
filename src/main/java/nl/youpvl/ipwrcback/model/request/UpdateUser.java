package nl.youpvl.ipwrcback.model.request;

import lombok.Data;

@Data
public class UpdateUser {
    private String email;
    private String name;
}
