package ru.kortunov.wordstress.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
public class OrfoDictionary implements Dictionary {

    @Value("${orfo.dictionary.path}")
    private String orfoPdfPath;

    @Override
    public String read() {
        File f = new File(orfoPdfPath);
        try {
            var parser = new PDFParser(new RandomAccessFile(f, "r"));
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
        var parsedText = read();
        if (parsedText == null) {
            return Optional.empty();
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
