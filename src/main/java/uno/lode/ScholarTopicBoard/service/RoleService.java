package uno.lode.ScholarTopicBoard.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uno.lode.ScholarTopicBoard.domain.role.RoleRepository;
import uno.lode.ScholarTopicBoard.domain.role.dto.RoleAdminResponseDTO;

@Service
public class RoleService {
	@Autowired
	private RoleRepository roleRepository;

	public List<RoleAdminResponseDTO> getAllRoles() {
		return roleRepository.findAll().stream()
				.map(RoleAdminResponseDTO::new)
				.toList();
	}
}
