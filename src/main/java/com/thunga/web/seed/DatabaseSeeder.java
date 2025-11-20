package com.thunga.web.seed;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.thunga.web.entity.Author;
import com.thunga.web.entity.Book;
import com.thunga.web.entity.Category;
import com.thunga.web.repository.AuthorRepository;
import com.thunga.web.repository.BookRepository;
import com.thunga.web.repository.CategoryRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public void seed() throws Exception {
        seedBooksFromImages();
    }

    private void seedBooksFromImages() throws IOException {

        ClassPathResource resource = new ClassPathResource("static/images/books/");
        File folder = resource.getFile();
        File[] files = folder.listFiles();

        if (files == null || files.length == 0) {
            System.out.println("⚠ Không tìm thấy file ảnh nào trong static/images/books/");
            return;
        }

        for (File file : files) {
            if (!file.isFile()) continue;

            String fileName = file.getName();
            String title = fileName.substring(0, fileName.lastIndexOf("."))
                    .replace("-", " ")
                    .replace("_", " ");

            if (!bookRepository.findByTitle(title).isEmpty()) {
                continue;
            }

            byte[] image = Files.readAllBytes(file.toPath());

            Author author = authorRepository.findFirstByOrderByIdAsc()
                    .orElseGet(() -> authorRepository.save(new Author(null, "Chưa rõ")));

            Category category = categoryRepository.findFirstByOrderByIdAsc()
                    .orElseGet(() -> categoryRepository.save(new Category(null, "Khác")));

            Book book = new Book();
            book.setTitle(title);
            book.setAuthor(author);
            book.setCategory(category);
            book.setImage(image);
            book.setPrice(100000);
            book.setNumber_in_stock(10);

            bookRepository.save(book);

            System.out.println("✔ Seeded: " + title);
        }
    }
}

