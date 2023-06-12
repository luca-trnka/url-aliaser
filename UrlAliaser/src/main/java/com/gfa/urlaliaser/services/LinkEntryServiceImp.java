package com.gfa.urlaliaser.services;

import com.gfa.urlaliaser.repositories.LinkEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class LinkEntryServiceImp implements LinkEntryService {

    private final LinkEntryRepository linkEntryRepository;

    @Autowired
    public LinkEntryServiceImp(LinkEntryRepository linkEntryRepository) {
        this.linkEntryRepository = linkEntryRepository;
    }

    @Override
    public String generateSecretCode() {
        Random random = new Random();
        int secretCode = (int) (Math.random() * 10000);
        return String.format("%04d", secretCode); //the code will have 4 digits, starting with 0000
    }
}
