package ru.kortunov.wordstress.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Service
public class OrfoDictionary implements Dictionary {

    private String parsedText;
    @Value("${orfo.dictionary.path}")
    private String orfoPdfPath;

    @Override
    public String read() {
        File file = new File(orfoPdfPath);
        try {
            var parser = new PDFParser(new RandomAccessFile(file, "r"));
            parser.parse();
            try (COSDocument cosDoc = parser.getDocument()) {
                PDFTextStripper pdfStripper = new PDFTextStripper();
                PDDocument pdDoc = new PDDocument(cosDoc);
                return pdfStripper.getText(pdDoc);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public Optional<String> search(String searchWord) {
        if (parsedText == null) {
            this.parsedText = read();
        }
        return Arrays.stream(parsedText.split("\n"))
                .map(String::trim)
                .filter(s -> s.toLowerCase().contains(searchWord))
                .findFirst();
    }

    @Override
    public String prepareMessage(String message) {
        return message.trim().toLowerCase();
    }
}
