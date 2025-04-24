package com.example.IntegrationWithHubStaff.service.ServiceImplementation;

import com.example.IntegrationWithHubStaff.dtos.organizationResponseDto.OrganizationResponse;
import com.example.IntegrationWithHubStaff.model.Organization;
import com.example.IntegrationWithHubStaff.repository.OrganizationRepository;
import com.example.IntegrationWithHubStaff.service.OrganizationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganizationServiceImpl implements OrganizationService {

        @Autowired
        private OrganizationRepository organizationRepository;

        @Autowired
        private ModelMapper modelMapper;

        @Override
        public List<OrganizationResponse> getAllOrganizations() {
            List<Organization> organizations = organizationRepository.getAllOrganizations();
            return organizations.stream()
                    .map(this::convertToOrganizationResponse)
                    .collect(Collectors.toList());
        }

        private OrganizationResponse convertToOrganizationResponse(Organization organization) {
            return modelMapper.map(organization, OrganizationResponse.class);
        }
}

