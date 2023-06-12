package com.gfa.urlaliaser.controllers;

import com.gfa.urlaliaser.models.LinkEntry;
import com.gfa.urlaliaser.repositories.LinkEntryRepository;
import com.gfa.urlaliaser.services.LinkEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;

@Controller
public class MainController {

    private final LinkEntryService linkEntryService;
    private final LinkEntryRepository linkEntryRepository;

    @Autowired
    public MainController(LinkEntryService linkEntryService, LinkEntryRepository linkEntryRepository) {
        this.linkEntryService = linkEntryService;
        this.linkEntryRepository = linkEntryRepository;
    }

    @GetMapping("/")
    public String mainPage(Model model) {
        model.addAttribute("linkEntry", new LinkEntry());
        return "main";
    }

    @PostMapping("/save-link")
    public String saveTheLink(@ModelAttribute LinkEntry linkEntry, Model model) {
        LinkEntry entryIHave = linkEntryRepository.findByAlias(linkEntry.getAlias());

        //condition for the error scenario
        if (entryIHave != null) {
            model.addAttribute("error", "Your alias is already in use!");
            return "main";
        }

        //generating of the secretcode
        String secretCode = linkEntryService.generateSecretCode();

        //setting and saving the secretcode
        linkEntry.setSecretCode(secretCode);
        linkEntryRepository.save(linkEntry);

        //condition for the successful scenario
        model.addAttribute("success", true);

        //passing variable linkEntry to display the alias in the main.html
        model.addAttribute("alias", linkEntry.getAlias());
        model.addAttribute("secretcode", linkEntry.getSecretCode());

        //clearing the input fields
        linkEntry.setUrl("");
        linkEntry.setAlias("");

        return "main";
    }

    @GetMapping("/a/{alias}")
    public ResponseEntity<?> getPageByAlias(@PathVariable String alias) {
        LinkEntry linkEntry = linkEntryRepository.findByAlias(alias);

        //increasing hitcount if the alias exists and set location for its url
        if (linkEntry != null) {
            linkEntry.incrementHitCount();
            linkEntryRepository.save(linkEntry);
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(linkEntry.getUrl()));
            return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
        } else {
            return ResponseEntity.status(404).build();
        }
    }


}
