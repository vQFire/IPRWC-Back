package nl.youpvl.ipwrcback.model.request;

import lombok.Data;

@Data
public class GrantRoleToUser {
    private String username;
    private String roleName;
}
