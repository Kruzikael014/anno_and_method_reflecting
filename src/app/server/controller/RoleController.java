package app.server.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import app.model.Role;
import app.others.annotation.Body;
import app.others.annotation.Controller;
import app.others.annotation.PathParam;
import app.others.annotation.Route;
import app.others.enumeration.HttpMethod;
import app.others.util.JsonUtil;

@Controller(path = "/role")
public class RoleController {

	private List<Role> validRole = new ArrayList<Role>() {
		{
			add(new Role(UUID.randomUUID().toString(), "Werewolf", "Wake up and kill someone at night", 1));
			add(new Role(UUID.randomUUID().toString(), "Seer", "Can inspect one player’s role at night", 1));
			add(new Role(UUID.randomUUID().toString(), "Guardian", "Protect one player from being killed each night",
					1));
			add(new Role(UUID.randomUUID().toString(), "Villager", "No special ability, just votes during the day", 1));
			add(new Role(UUID.randomUUID().toString(), "Major", "Able to vote with 2x vote weight during the day", 1));
			add(new Role(UUID.randomUUID().toString(), "Werewolf", "Wake up and kill someone at night", 1));
		}
	};
	private Random random = new Random();

	@Route()
	public String getAllRole() {
		return JsonUtil.toJson(validRole);
	}

	@Route(path = "/{id}")
	public String getRole(@PathParam("id") String id) {
		Role role = validRole.stream().filter(r -> r.getRoleId().equals(id)).findFirst().orElse(null);
		return JsonUtil.toJson(role);
	}

	@Route(path = "/randomize")
	public String randomlyGetAvailableRole() {
		System.out.println("aaaa");
		
		List<Role> availableRoles = validRole.stream().filter(r -> r.getAvailable() == 1).collect(Collectors.toList());

		if (availableRoles.isEmpty()) {
			return "{\"error\":\"No roles available\"}";
		}

		int idx = random.nextInt(availableRoles.size());

		Role chosen = availableRoles.get(idx);

		chosen.setAvailable(0);

		System.out.println(chosen);
		return JsonUtil.toJson(chosen);
	}

	@Route(path = "/reset")
	public String resetAvailableRole() {
		validRole.forEach(r -> r.setAvailable(1));
		return "{\"message\":\"All roles reset!\"}";
	}

}
