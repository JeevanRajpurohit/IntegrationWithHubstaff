package com.example.IntegrationWithHubStaff.service.ServiceImplementation;

import com.example.IntegrationWithHubStaff.dtos.userResponseDto.UserResponse;
import com.example.IntegrationWithHubStaff.model.User;
import com.example.IntegrationWithHubStaff.repository.UserRepository;
import com.example.IntegrationWithHubStaff.service.UserService;
import com.example.IntegrationWithHubStaff.util.RequestValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<UserResponse> getUsersByOrganizationId(String organizationId) {
        RequestValidationUtil.checkNotNullOrBlank(organizationId, "organizationId");

        List<User> users = userRepository.findByOrganizationId(organizationId);

        return users.stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .collect(Collectors.toList());
    }
}
