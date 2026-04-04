package com.cognizant.paymentService.service.impl;

import com.cognizant.taxease.dto.requestdto.TaxpayerRegistrationRequestDto;
import com.cognizant.taxease.dto.responsedto.TaxpayerRegistrationResponseDto;
import com.cognizant.taxease.entity.Taxpayer;
import com.cognizant.taxease.entity.User;
import com.cognizant.taxease.entity.entityEnum.StatusBasic;
import com.cognizant.taxease.entity.entityEnum.UserRole;
import com.cognizant.taxease.dao.TaxpayerRepository;
import com.cognizant.taxease.dao.UserRepository;
import com.cognizant.taxease.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class TaxpayerRegistrationService {

    private final UserRepository userRepository;
    private final TaxpayerRepository taxpayerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    @Transactional
    public TaxpayerRegistrationResponseDto registerTaxpayer(TaxpayerRegistrationRequestDto request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Account already exists for this email");
        }

        // Generate unique 11-digit ID
        String taxpayerIdNumber = generateUniqueTaxpayerId();

        // Create User
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.TAXPAYER)
                .status(StatusBasic.Active)
                .build();

        user = userRepository.save(user);

        // Create Taxpayer
        Taxpayer taxpayer = Taxpayer.builder()
                .user(user)
                .name(request.getName())
                .type(request.getTaxpayerType())
                .taxpayerIdNumber(taxpayerIdNumber)
                .address(request.getAddress())
                .contactInfo(request.getContactInfo())
                .build();

        taxpayerRepository.save(taxpayer);
        auditLogService.recordRegistration(user,"TAXPAYER_REGISTER", "taxpayers/" + taxpayerIdNumber);

        return TaxpayerRegistrationResponseDto.builder()
                .taxpayerIdNumber(taxpayerIdNumber)
                .message("Taxpayer registered successfully")
                .build();
    }

    private String generateUniqueTaxpayerId() {
        SecureRandom random = new SecureRandom();
        String id;
        int attempts = 0;
        do {
            if (attempts++ > 100) {
                throw new RuntimeException("Unable to generate unique taxpayer ID after multiple attempts");
            }
            id = String.format("%011d", random.nextInt(1000000000) + 1000000000L); // 11-digit number
        } while (taxpayerRepository.existsByTaxpayerIdNumber(id));
        return id;
    }
}