package com.example.IntegrationWithHubStaff.service.ServiceImplementation;

import com.example.IntegrationWithHubStaff.dtos.AppDto;
import com.example.IntegrationWithHubStaff.model.UserApp;
import com.example.IntegrationWithHubStaff.repository.UserAppRepository;
import com.example.IntegrationWithHubStaff.service.AppService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AppServiceImpl implements AppService {
        @Autowired
        private UserAppRepository userAppRepository;
        @Autowired
        private ModelMapper modelMapper;

        @Override
        public List<AppDto> getUniqueAppDtosByOrganization(String organizationId) {
        List<UserApp> allApps = userAppRepository.findByOrganizationId(organizationId);

        return allApps.stream()
                .map(app -> modelMapper.map(app, AppDto.class))
                .collect(Collectors.toList());
        }

        public List<AppDto> getAppsByUserId(String userId) {
            List<UserApp> apps = userAppRepository.findByUserId(userId);
            return apps.stream()
                    .map(app -> modelMapper.map(app, AppDto.class))
                    .collect(Collectors.toList());
        }
}

